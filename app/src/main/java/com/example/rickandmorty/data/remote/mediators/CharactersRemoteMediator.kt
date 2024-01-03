package com.example.rickandmorty.data.remote.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.models.CharacterEntity
import com.example.rickandmorty.data.local.models.CharacterRemoteKeys
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.local.mappers.toEntityEpisode
import com.example.rickandmorty.data.local.mappers.toFullInfoCharacter
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CharactersRemoteMediator(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
): RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (rickAndMortyDatabase.getCharactersRemoteKeysDao.getCreationTime() ?: 0) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val page: Int = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val apiResponse = rickAndMortyApiService.getCharactersByPage(page = page)
            val characters = apiResponse.results
            val endOfPaginationReached = characters.isEmpty()

            rickAndMortyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    rickAndMortyDatabase.getCharactersRemoteKeysDao.clearRemoteKeys()
                    rickAndMortyDatabase.getCharactersDao.clearCharacters()
                    rickAndMortyDatabase.getCharactersDao.clearCharacterOrigin()
                    rickAndMortyDatabase.getCharactersDao.clearCharacterLocation()
                    rickAndMortyDatabase.getCharactersDao.clearCharacterEpisodes()
                }
                val prevKey = if (apiResponse.info.prev == null) null else page - 1
                val nextKey = if (apiResponse.info.next == null) null else page + 1
                val characterRemoteKeys = characters.map {
                    CharacterRemoteKeys(characterId = it.id, prevKey = prevKey, currentPage = page, nextKey = nextKey)
                }

                rickAndMortyDatabase.getCharactersRemoteKeysDao.insertAll(characterRemoteKeys)
                rickAndMortyDatabase.getCharactersDao.upsertCharacters(characters.map { it.toFullInfoCharacter(page).characterEntity })
                rickAndMortyDatabase.getCharactersDao.upsertCharacterOrigin(characters.map { it.toFullInfoCharacter(page).origin })
                rickAndMortyDatabase.getCharactersDao.upsertCharacterLocation(characters.map { it.toFullInfoCharacter(page).location })
                characters.forEach {
                    rickAndMortyDatabase.getCharactersDao.upsertCharacterEpisodes(it.episode.toEntityEpisode(it.id))
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                rickAndMortyDatabase.getCharactersRemoteKeysDao.getRemoteKeyByCharacterId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { character ->
            rickAndMortyDatabase.getCharactersRemoteKeysDao.getRemoteKeyByCharacterId(character.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { character ->
            rickAndMortyDatabase.getCharactersRemoteKeysDao.getRemoteKeyByCharacterId(character.id)
        }
    }
}