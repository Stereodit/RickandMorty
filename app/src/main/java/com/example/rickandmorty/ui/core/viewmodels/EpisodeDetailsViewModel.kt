package com.example.rickandmorty.ui.core.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.RickAndMortyRepository
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.Episode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface EpisodeDetailsUiState {
    data class SuccessLoadEpisode(val episode: Episode) : EpisodeDetailsUiState
    object Error : EpisodeDetailsUiState
    object Loading : EpisodeDetailsUiState
}

sealed interface CharactersOfSelectedEpisodeUiState {
    data class SuccessLoadCharacters(val charactersList: List<Character>) : CharactersOfSelectedEpisodeUiState
    object EpisodeNotLoaded: CharactersOfSelectedEpisodeUiState
    object Error : CharactersOfSelectedEpisodeUiState
    object Loading : CharactersOfSelectedEpisodeUiState
}

class EpisodeDetailsViewModel(
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {
    var episodeDetailsUiState: EpisodeDetailsUiState by mutableStateOf(EpisodeDetailsUiState.Loading)
        private set

    var charactersUiState: CharactersOfSelectedEpisodeUiState by mutableStateOf(CharactersOfSelectedEpisodeUiState.Loading)

    suspend fun getEpisode(episodeId: Int) {
        viewModelScope.launch {
            episodeDetailsUiState = EpisodeDetailsUiState.Loading
            episodeDetailsUiState = try {
                EpisodeDetailsUiState.SuccessLoadEpisode(rickAndMortyRepository.getSelectedEpisode(episodeId))
            } catch (e: IOException) {
                EpisodeDetailsUiState.Error
            } catch (e: HttpException) {
                EpisodeDetailsUiState.Error
            }
        }
    }

    fun getCharactersOfSelectedEpisode() {
        if(episodeDetailsUiState is EpisodeDetailsUiState.SuccessLoadEpisode) {
            viewModelScope.launch {
                var query = ""
                (episodeDetailsUiState as EpisodeDetailsUiState.SuccessLoadEpisode).episode.characters.forEach {
                    query += it.removePrefix("https://rickandmortyapi.com/api/character/")
                    if(it != (episodeDetailsUiState as EpisodeDetailsUiState.SuccessLoadEpisode).episode.characters.last())
                        query += ","
                }
                charactersUiState = CharactersOfSelectedEpisodeUiState.Loading
                charactersUiState = try {
                    if ((episodeDetailsUiState as EpisodeDetailsUiState.SuccessLoadEpisode).episode.characters.size == 1) {
                        CharactersOfSelectedEpisodeUiState.SuccessLoadCharacters(listOf(rickAndMortyRepository.getCharacterOfSelectedEpisode(query)))
                    } else {
                        CharactersOfSelectedEpisodeUiState.SuccessLoadCharacters(rickAndMortyRepository.getCharactersOfSelectedEpisode(query))
                    }
                } catch (e: IOException) {
                    CharactersOfSelectedEpisodeUiState.Error
                } catch (e: HttpException) {
                    CharactersOfSelectedEpisodeUiState.Error
                }
            }
        } else {
            charactersUiState = CharactersOfSelectedEpisodeUiState.EpisodeNotLoaded
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val rickAndMortyRepository = application.container.rickAndMortyRepository
                EpisodeDetailsViewModel(rickAndMortyRepository = rickAndMortyRepository)
            }
        }
    }
}