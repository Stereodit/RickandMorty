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
import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.Episode
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface CharacterDetailsUiState {
    data class SuccessLoadCharacter(val character: Character) : CharacterDetailsUiState
    object Error : CharacterDetailsUiState
    object Loading : CharacterDetailsUiState
}

sealed interface EpisodesOfSelectedCharacterUiState {
    data class SuccessLoadEpisodes(val episodesList: List<Episode>) : EpisodesOfSelectedCharacterUiState
    object CharacterNotLoaded: EpisodesOfSelectedCharacterUiState
    object Error : EpisodesOfSelectedCharacterUiState
    object Loading : EpisodesOfSelectedCharacterUiState
}

class CharacterDetailsViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {
    var characterDetailsUiState: CharacterDetailsUiState by mutableStateOf(CharacterDetailsUiState.Loading)
        private set

    var episodesUiState: EpisodesOfSelectedCharacterUiState by mutableStateOf(EpisodesOfSelectedCharacterUiState.Loading)

    fun getCharacter(characterId: Int) {
        viewModelScope.launch {
            characterDetailsUiState = CharacterDetailsUiState.Loading
            characterDetailsUiState = try {
                CharacterDetailsUiState.SuccessLoadCharacter(characterRepository.getSelectedCharacter(characterId))
            } catch (e: IOException) {
                CharacterDetailsUiState.Error
            } catch (e: HttpException) {
                CharacterDetailsUiState.Error
            }
        }
    }

    fun getEpisodesOfSelectedCharacter() {
        if(characterDetailsUiState is CharacterDetailsUiState.SuccessLoadCharacter) {
            viewModelScope.launch {
                var query = ""
                (characterDetailsUiState as CharacterDetailsUiState.SuccessLoadCharacter).character.episode.forEach {
                    query += it.removePrefix("https://rickandmortyapi.com/api/episode/")
                    if(it != (characterDetailsUiState as CharacterDetailsUiState.SuccessLoadCharacter).character.episode.last())
                        query += ","
                }
                episodesUiState = EpisodesOfSelectedCharacterUiState.Loading
                episodesUiState = try {
                    if ((characterDetailsUiState as CharacterDetailsUiState.SuccessLoadCharacter).character.episode.size == 1) {
                        EpisodesOfSelectedCharacterUiState.SuccessLoadEpisodes(listOf(characterRepository.getEpisodeOfSelectedCharacter(query)))
                    } else {
                        EpisodesOfSelectedCharacterUiState.SuccessLoadEpisodes(characterRepository.getEpisodesOfSelectedCharacter(query))
                    }
                } catch (e: IOException) {
                    EpisodesOfSelectedCharacterUiState.Error
                } catch (e: HttpException) {
                    EpisodesOfSelectedCharacterUiState.Error
                }
            }
        } else {
            episodesUiState = EpisodesOfSelectedCharacterUiState.CharacterNotLoaded
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val characterRepository = application.container.characterRepository
                CharacterDetailsViewModel(characterRepository = characterRepository)
            }
        }
    }
}