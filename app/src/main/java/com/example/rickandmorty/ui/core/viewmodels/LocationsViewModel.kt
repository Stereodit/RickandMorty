package com.example.rickandmorty.ui.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.RickAndMortyRepository
import com.example.rickandmorty.data.remote.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class LocationsViewModel(
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {

    val locationsFlow: Flow<PagingData<Location>>
    var searchByName = MutableStateFlow("")

    init {
        locationsFlow = searchByName.asStateFlow()
            .debounce(500)
            .flatMapLatest {
                rickAndMortyRepository.getPagedLocations(it.trim())
            }
            .cachedIn(viewModelScope)
    }

    fun setSearchByName(value: String) {
        if (this.searchByName.value == value) return
        this.searchByName.value = value.trim()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val rickAndMortyRepository = application.container.rickAndMortyRepository
                LocationsViewModel(rickAndMortyRepository = rickAndMortyRepository)
            }
        }
    }
}