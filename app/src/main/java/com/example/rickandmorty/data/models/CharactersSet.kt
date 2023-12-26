package com.example.rickandmorty.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CharactersSet(
    val info: CharactersInfo,
    val results: List<Character>
)

@Serializable
data class CharactersInfo(
    val count: Int,
    val next: String?,
    val pages: Int,
    val prev: String?
)

@Serializable
data class Character(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: CharacterLocation,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)

@Serializable
data class CharacterLocation(
    val name: String,
    val url: String
)

@Serializable
data class Origin(
    val name: String,
    val url: String
)