package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.example.rickandmorty.data.remote.mediators.LocationsRemoteMediator
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocationRepository {
    fun getPagedLocations(searchString: String): Flow<PagingData<Location>>
    suspend fun getSelectedLocation(id: Int): Location
    suspend fun getCharactersOfSelectedLocation(query: String): List<Character>
    suspend fun getCharacterOfSelectedLocation(query: String): Character
}

class LocationRepositoryImpl (
    private val rickAndMortyLocalDataSource: RickAndMortyDatabase,
    private val rickAndMortyRemoteDataSource: RickAndMortyApiService
) : LocationRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedLocations(searchString: String): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyLocalDataSource.getLocationsDao.filteredLocationPagingSource(
                    searchString = if(searchString == "") null else searchString
                )
            },
            remoteMediator = LocationsRemoteMediator(
                rickAndMortyLocalDataSource,
                rickAndMortyRemoteDataSource
            )
        ).flow.map { value ->
            value.map { entity ->
                Location(
                    created = entity.created,
                    dimension = entity.dimension,
                    id = entity.id,
                    name = entity.name,
                    residents = listOf("", ""),
                    type = entity.type,
                    url = entity.url
                )
            }
        }
    }

    override suspend fun getSelectedLocation(id: Int): Location = rickAndMortyRemoteDataSource.getSelectedLocation(id)
    override suspend fun getCharactersOfSelectedLocation(query: String): List<Character> = rickAndMortyRemoteDataSource.getCharactersOfSelectedLocation(query)
    override suspend fun getCharacterOfSelectedLocation(query: String): Character = rickAndMortyRemoteDataSource.getCharacterOfSelectedLocation(query)

}