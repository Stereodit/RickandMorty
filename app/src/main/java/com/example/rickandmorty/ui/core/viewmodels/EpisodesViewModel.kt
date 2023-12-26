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
import com.example.rickandmorty.data.models.Episode
import kotlinx.coroutines.flow.Flow

class EpisodesViewModel(
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {

    val episodesFlow: Flow<PagingData<Episode>> = rickAndMortyRepository.getPagedEpisodes().cachedIn(viewModelScope)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val rickAndMortyRepository = application.container.rickAndMortyRepository
                EpisodesViewModel(rickAndMortyRepository = rickAndMortyRepository)
            }
        }
    }
}