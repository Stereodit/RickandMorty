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
import com.example.rickandmorty.data.remote.models.Location
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface LocationDetailsUiState {
    data class SuccessLoadLocation(val location: Location) : LocationDetailsUiState
    object Error : LocationDetailsUiState
    object Loading : LocationDetailsUiState
}

sealed interface CharactersOfSelectedLocationUiState {
    data class SuccessLoadCharacters(val charactersList: List<Character>) : CharactersOfSelectedLocationUiState
    object LocationNotLoaded: CharactersOfSelectedLocationUiState
    object Error : CharactersOfSelectedLocationUiState
    object Loading : CharactersOfSelectedLocationUiState
}

class LocationDetailsViewModel(
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {
    var locationDetailsUiState: LocationDetailsUiState by mutableStateOf(LocationDetailsUiState.Loading)
        private set

    var charactersUiState: CharactersOfSelectedLocationUiState by mutableStateOf(CharactersOfSelectedLocationUiState.Loading)

    suspend fun getLocation(locationId: Int) {
        viewModelScope.launch {
            locationDetailsUiState = LocationDetailsUiState.Loading
            locationDetailsUiState = try {
                LocationDetailsUiState.SuccessLoadLocation(rickAndMortyRepository.getSelectedLocation(locationId))
            } catch (e: IOException) {
                LocationDetailsUiState.Error
            } catch (e: HttpException) {
                LocationDetailsUiState.Error
            }
        }
    }

    fun getCharactersOfSelectedLocation() {
        if(locationDetailsUiState is LocationDetailsUiState.SuccessLoadLocation) {
            viewModelScope.launch {
                var query = ""
                (locationDetailsUiState as LocationDetailsUiState.SuccessLoadLocation).location.residents.forEach {
                    query += it.removePrefix("https://rickandmortyapi.com/api/character/")
                    if(it != (locationDetailsUiState as LocationDetailsUiState.SuccessLoadLocation).location.residents.last())
                        query += ","
                }
                charactersUiState = CharactersOfSelectedLocationUiState.Loading
                charactersUiState = try {
                    if ((locationDetailsUiState as LocationDetailsUiState.SuccessLoadLocation).location.residents.size == 1) {
                        CharactersOfSelectedLocationUiState.SuccessLoadCharacters(listOf(rickAndMortyRepository.getCharacterOfSelectedLocation(query)))
                    } else {
                        CharactersOfSelectedLocationUiState.SuccessLoadCharacters(rickAndMortyRepository.getCharactersOfSelectedLocation(query))
                    }
                } catch (e: IOException) {
                    CharactersOfSelectedLocationUiState.Error
                } catch (e: HttpException) {
                    CharactersOfSelectedLocationUiState.Error
                }
            }
        } else {
            charactersUiState = CharactersOfSelectedLocationUiState.LocationNotLoaded
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val rickAndMortyRepository = application.container.rickAndMortyRepository
                LocationDetailsViewModel(rickAndMortyRepository = rickAndMortyRepository)
            }
        }
    }
}