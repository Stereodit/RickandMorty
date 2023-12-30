package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.remote.CharacterLocation
import com.example.rickandmorty.data.remote.CharacterOrigin
import com.example.rickandmorty.data.remote.CharactersRemoteMediator
import com.example.rickandmorty.data.remote.Episode
import com.example.rickandmorty.data.remote.EpisodesRemoteMediator
import com.example.rickandmorty.data.remote.Location
import com.example.rickandmorty.data.remote.LocationsRemoteMediator
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PAGE_SIZE = 20

interface RickAndMortyRepository {
    fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>>
    fun getPagedEpisodes(searchString: String): Flow<PagingData<Episode>>
    fun getPagedLocations(searchString: String): Flow<PagingData<Location>>
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
                rickAndMortyDatabase.getCharactersDao.filteredCharacterPagingSource(
                    searchString = if(searchString == "") null else searchString,
                    status = if(status == "") null else status,
                    species = if(species == "") null else species,
                    gender = if(gender == "") null else gender,
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
                    status = entity.status,
                    type = entity.type,
                    url = entity.url
                )
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedEpisodes(searchString: String): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyDatabase.getEpisodesDao.filteredEpisodePagingSource(
                    searchString = if(searchString == "") null else searchString
                )
            },
            remoteMediator = EpisodesRemoteMediator(
                rickAndMortyDatabase,
                rickAndMortyApiService
            )
        ).flow.map { value ->
            value.map { entity ->
                Episode(
                    airDate = entity.airDate,
                    characters = listOf("", ""),
                    created = entity.created,
                    episode = entity.episode,
                    id = entity.id,
                    name = entity.name,
                    url = entity.url
                )
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedLocations(searchString: String): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyDatabase.getLocationsDao.filteredLocationPagingSource(
                    searchString = if(searchString == "") null else searchString
                )
            },
            remoteMediator = LocationsRemoteMediator(
                rickAndMortyDatabase,
                rickAndMortyApiService
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
}



