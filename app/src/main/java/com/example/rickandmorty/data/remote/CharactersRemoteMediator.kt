package com.example.rickandmorty.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.rickandmorty.data.local.CharacterEntity
import com.example.rickandmorty.data.local.FullInfoCharacter
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.local.toFullInfoCharacter
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharactersRemoteMediator(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
): RemoteMediator<Int, FullInfoCharacter>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FullInfoCharacter>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.characterEntity.id / state.config.pageSize) + 1
                    }
                }
            }

            val characters = rickAndMortyApiService.getCharactersByPage(page = loadKey)

            rickAndMortyDatabase.runCatching {
                if(loadType == LoadType.REFRESH) {
                    rickAndMortyDatabase.dao.clearAll()
                }
                val fullInfoCharacters = characters.results.map { it.toFullInfoCharacter() }
                rickAndMortyDatabase.dao.upsertAll(fullInfoCharacters)
            }

            MediatorResult.Success(
                endOfPaginationReached = characters.results.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}