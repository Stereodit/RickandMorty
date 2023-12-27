package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.local.toCharacterDto
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.models.Episode
import com.example.rickandmorty.data.models.Location
import com.example.rickandmorty.data.remote.CharactersRemoteMediator
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PAGE_SIZE = 20

interface RickAndMortyRepository {
//    fun getPagedCharacters(searchBy: String): Flow<PagingData<Character>>
    fun getFilteredPagedCharacters(searchBy: String, status: String, species: String, gender: String): Flow<PagingData<Character>>
    fun getPagedEpisodes(): Flow<PagingData<Episode>>
    fun getPagedLocations(): Flow<PagingData<Location>>
}

class NetworkRickAndMortyRepository(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
) : RickAndMortyRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getFilteredPagedCharacters(searchBy: String, status: String, species: String, gender: String): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = CharactersRemoteMediator(rickAndMortyDatabase, rickAndMortyApiService),
            pagingSourceFactory = {
                rickAndMortyDatabase.dao.pagingSource()
            }
        ).flow.map { value -> value.map { it.toCharacterDto() }  }
    }

    override fun getPagedEpisodes(): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EpisodesPagingSource(rickAndMortyApiService) }
        ).flow
    }

    override fun getPagedLocations(): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { LocationsPagingSource(rickAndMortyApiService) }
        ).flow
    }
}



