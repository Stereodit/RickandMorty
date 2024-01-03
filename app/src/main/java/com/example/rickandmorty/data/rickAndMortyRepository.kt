package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.example.rickandmorty.data.remote.mediators.CharactersRemoteMediator
import com.example.rickandmorty.data.remote.mediators.EpisodesRemoteMediator
import com.example.rickandmorty.data.remote.mediators.LocationsRemoteMediator
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.CharacterLocation
import com.example.rickandmorty.data.remote.models.CharacterOrigin
import com.example.rickandmorty.data.remote.models.Episode
import com.example.rickandmorty.data.remote.models.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PAGE_SIZE = 20

interface RickAndMortyRepository {
    fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>>

    suspend fun getSelectedCharacter(id: Int): Character
    suspend fun getEpisodesOfSelectedCharacter(query: String): List<Episode>
    suspend fun getEpisodeOfSelectedCharacter(query: String): Episode
    fun getPagedEpisodes(searchString: String): Flow<PagingData<Episode>>
    suspend fun getSelectedEpisode(id: Int): Episode

    suspend fun getCharactersOfSelectedEpisode(query: String): List<Character>
    suspend fun getCharacterOfSelectedEpisode(query: String): Character
    fun getPagedLocations(searchString: String): Flow<PagingData<Location>>

    suspend fun getSelectedLocation(id: Int): Location

    suspend fun getCharactersOfSelectedLocation(query: String): List<Character>
    suspend fun getCharacterOfSelectedLocation(query: String): Character
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

    override suspend fun getSelectedCharacter(id: Int): Character = rickAndMortyApiService.getSelectedCharacter(id)
    override suspend fun getEpisodesOfSelectedCharacter(query: String): List<Episode> = rickAndMortyApiService.getEpisodesOfSelectedCharacter(query)
    override suspend fun getEpisodeOfSelectedCharacter(query: String): Episode = rickAndMortyApiService.getEpisodeOfSelectedCharacter(query)

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

    override suspend fun getSelectedEpisode(id: Int): Episode = rickAndMortyApiService.getSelectedEpisode(id)
    override suspend fun getCharactersOfSelectedEpisode(query: String): List<Character> = rickAndMortyApiService.getCharactersOfSelectedEpisode(query)
    override suspend fun getCharacterOfSelectedEpisode(query: String): Character = rickAndMortyApiService.getCharacterOfSelectedEpisode(query)

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

    override suspend fun getSelectedLocation(id: Int): Location = rickAndMortyApiService.getSelectedLocation(id)
    override suspend fun getCharactersOfSelectedLocation(query: String): List<Character> = rickAndMortyApiService.getCharactersOfSelectedLocation(query)
    override suspend fun getCharacterOfSelectedLocation(query: String): Character = rickAndMortyApiService.getCharacterOfSelectedLocation(query)
}



