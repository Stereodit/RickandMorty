package com.example.rickandmorty.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.EpisodeEntity
import com.example.rickandmorty.data.local.EpisodeRemoteKeys
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.local.toEpisodeCharactersEntity
import com.example.rickandmorty.data.local.toFullInfoEpisode
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
): RemoteMediator<Int, EpisodeEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (rickAndMortyDatabase.getEpisodesRemoteKeysDao.getCreationTime() ?: 0) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
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
            val apiResponse = rickAndMortyApiService.getEpisodesByPage(page = page)
            val episodes = apiResponse.results
            val endOfPaginationReached = episodes.isEmpty()

            rickAndMortyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    rickAndMortyDatabase.getEpisodesRemoteKeysDao.clearRemoteKeys()
                    rickAndMortyDatabase.getEpisodesDao.clearEpisodes()
                    rickAndMortyDatabase.getEpisodesDao.clearEpisodeCharacters()
                }
                val prevKey = if (apiResponse.info.prev == null) null else page - 1
                val nextKey = if (apiResponse.info.next == null) null else page + 1
                val episodeRemoteKeys = episodes.map {
                    EpisodeRemoteKeys(episodeId = it.id, prevKey = prevKey, currentPage = page, nextKey = nextKey)
                }

                rickAndMortyDatabase.getEpisodesRemoteKeysDao.insertAll(episodeRemoteKeys)
                rickAndMortyDatabase.getEpisodesDao.upsertEpisodes(episodes.map { it.toFullInfoEpisode(page).episodeEntity })
                episodes.forEach {
                    rickAndMortyDatabase.getEpisodesDao.upsertEpisodeCharacters(it.characters.toEpisodeCharactersEntity(it.id))
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                rickAndMortyDatabase.getEpisodesRemoteKeysDao.getRemoteKeyByEpisodeId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { episode ->
            rickAndMortyDatabase.getEpisodesRemoteKeysDao.getRemoteKeyByEpisodeId(episode.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, EpisodeEntity>): EpisodeRemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { episode ->
            rickAndMortyDatabase.getEpisodesRemoteKeysDao.getRemoteKeyByEpisodeId(episode.id)
        }
    }
}