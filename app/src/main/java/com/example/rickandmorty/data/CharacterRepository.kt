package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.example.rickandmorty.data.remote.mediators.CharactersRemoteMediator
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.CharacterLocation
import com.example.rickandmorty.data.remote.models.CharacterOrigin
import com.example.rickandmorty.data.remote.models.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CharacterRepository {
    fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>>
    suspend fun getSelectedCharacter(id: Int): Character
    suspend fun getEpisodesOfSelectedCharacter(query: String): List<Episode>
    suspend fun getEpisodeOfSelectedCharacter(query: String): Episode
}

class CharacterRepositoryImpl(
    private val rickAndMortyLocalDataSource: RickAndMortyDatabase,
    private val rickAndMortyRemoteDataSource: RickAndMortyApiService
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedCharacters(searchString: String, status: String, species: String, gender: String): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyLocalDataSource.getCharactersDao.filteredCharacterPagingSource(
                    searchString = if(searchString == "") null else searchString,
                    status = if(status == "") null else status,
                    species = if(species == "") null else species,
                    gender = if(gender == "") null else gender,
                )
            },
            remoteMediator = CharactersRemoteMediator(
                rickAndMortyLocalDataSource,
                rickAndMortyRemoteDataSource
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

    override suspend fun getSelectedCharacter(id: Int): Character = rickAndMortyRemoteDataSource.getSelectedCharacter(id)
    override suspend fun getEpisodesOfSelectedCharacter(query: String): List<Episode> = rickAndMortyRemoteDataSource.getEpisodesOfSelectedCharacter(query)
    override suspend fun getEpisodeOfSelectedCharacter(query: String): Episode = rickAndMortyRemoteDataSource.getEpisodeOfSelectedCharacter(query)
}