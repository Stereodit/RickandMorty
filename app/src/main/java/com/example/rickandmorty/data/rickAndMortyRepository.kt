package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.models.Episode
import com.example.rickandmorty.data.models.Location
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.remote.CharacterLocation
import com.example.rickandmorty.data.remote.CharacterOrigin
import com.example.rickandmorty.data.remote.CharactersRemoteMediator
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PAGE_SIZE = 20

interface RickAndMortyRepository {
    fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>>
    suspend fun refreshCharacters()

//    fun getFilteredPagedCharacters(searchBy: String, status: String, species: String, gender: String): Flow<PagingData<Character>>
    fun getPagedEpisodes(): Flow<PagingData<Episode>>
    fun getPagedLocations(): Flow<PagingData<Location>>
}

class NetworkRickAndMortyRepository(
    private val rickAndMortyDatabase: RickAndMortyDatabase,
    private val rickAndMortyApiService: RickAndMortyApiService
) : RickAndMortyRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyDatabase.getCharactersDao.filteredPagingSource(
                    searchString = searchString,
                    filterString = filterDesigner(status, species, gender)
                )
            },
            remoteMediator = CharactersRemoteMediator(
                rickAndMortyDatabase,
                rickAndMortyApiService
            )
        ).flow.map { value ->
            value.map {entity ->
                Character(
                    created = entity.created,
                    episode = listOf("", ""),
                    gender = entity.gender,
                    id = entity.id,
                    image = entity.image,
                    location = CharacterLocation(
                        name = "",
                        url = ""
                    ),
                    name = entity.name,
                    origin = CharacterOrigin(
                        name = "",
                        url = ""
                    ),
                    species = entity.species,
                    status = entity.species,
                    type = entity.type,
                    url = entity.url
                )
            }
        }
    }

    override suspend fun refreshCharacters() {
        rickAndMortyDatabase.getCharactersDao.clearAll()
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

private fun filterDesigner(status: String, species: String, gender: String): String {
    var filter = ""
    if (status != "") filter += " AND status = '$status'"
    if (species != "") filter += " AND species = '$species'"
    if (gender != "") filter += " AND gender = '$gender'"
    return filter
}



