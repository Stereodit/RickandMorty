package com.example.rickandmorty.ui.core.viewmodels

import android.text.BoringLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.RickAndMortyRepository
import com.example.rickandmorty.data.models.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CharactersViewModel(
    private val rickAndMortyRepository: RickAndMortyRepository
) : ViewModel() {

    var charactersFlow: Flow<PagingData<Character>>
    val isActiveFilters = MutableStateFlow(false)
    val searchByName = MutableStateFlow("")
    private var filterStatus = ""
    private var filterSpecies = ""
    private var filterGender = ""

    init {
        charactersFlow = searchByName.asStateFlow()
            .debounce(500)
            .flatMapLatest {
                if(isActiveFilters.value) rickAndMortyRepository.getFilteredPagedCharacters(it, filterStatus, filterSpecies, filterGender)
                    else rickAndMortyRepository.getFilteredPagedCharacters(it, "", "", "")
            }
            .cachedIn(viewModelScope)
    }

    fun setSearchByName(value: String) {
        if (this.searchByName.value == value) return
        this.searchByName.value = value
    }

    fun setIsActiveFilters(value: Boolean) {
        if (this.isActiveFilters.value == value) return
        this.isActiveFilters.value = value
    }

    fun setFilterStatus(value: String) {
        if (this.filterStatus == value) return
        this.filterStatus = value
    }

    fun setFilterSpecies(value: String) {
        if (this.filterSpecies == value) return
        this.filterSpecies = value
    }

    fun setFilterGender(value: String) {
        if (this.filterGender == value) return
        this.filterGender = value
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RickAndMortyApplication)
                val rickAndMortyRepository = application.container.rickAndMortyRepository
                CharactersViewModel(rickAndMortyRepository = rickAndMortyRepository)
            }
        }
    }
}