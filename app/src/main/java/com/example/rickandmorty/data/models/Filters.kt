package com.example.rickandmorty.data.models

sealed class Filters {
    object CharacterFilters {
        val status: List<String> = listOf("Alive", "Dead", "Unknown")
        val species: List<String> = listOf("Human", "Alien", "Humanoid", "Robot", "Disease", "Mythological Creature", "Cronenberg", "Poopybutthole", "Animal", "Unknown")
        val gender: List<String> = listOf("Female", "Male", "Genderless", "Unknown")
    }
    data class SelectedCharacterFilters(
        var status: String = "",
        var species: String = "",
        var gender: String = ""
    )
}