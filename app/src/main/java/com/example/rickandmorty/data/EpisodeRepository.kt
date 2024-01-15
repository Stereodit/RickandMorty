package com.example.rickandmorty.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.example.rickandmorty.data.remote.mediators.EpisodesRemoteMediator
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface EpisodeRepository {
    fun getPagedEpisodes(searchString: String): Flow<PagingData<Episode>>
    suspend fun getSelectedEpisode(id: Int): Episode
    suspend fun getCharactersOfSelectedEpisode(query: String): List<Character>
    suspend fun getCharacterOfSelectedEpisode(query: String): Character
}

class EpisodeRepositoryImpl(
    private val rickAndMortyLocalDataSource: RickAndMortyDatabase,
    private val rickAndMortyRemoteDataSource: RickAndMortyApiService
) : EpisodeRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedEpisodes(searchString: String): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                rickAndMortyLocalDataSource.getEpisodesDao.filteredEpisodePagingSource(
                    searchString = if(searchString == "") null else searchString
                )
            },
            remoteMediator = EpisodesRemoteMediator(
                rickAndMortyLocalDataSource,
                rickAndMortyRemoteDataSource
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

    override suspend fun getSelectedEpisode(id: Int): Episode = rickAndMortyRemoteDataSource.getSelectedEpisode(id)
    override suspend fun getCharactersOfSelectedEpisode(query: String): List<Character> = rickAndMortyRemoteDataSource.getCharactersOfSelectedEpisode(query)
    override suspend fun getCharacterOfSelectedEpisode(query: String): Character = rickAndMortyRemoteDataSource.getCharacterOfSelectedEpisode(query)

}