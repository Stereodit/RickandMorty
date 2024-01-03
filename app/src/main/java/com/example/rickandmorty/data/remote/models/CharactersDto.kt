package com.example.rickandmorty.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class CharactersResponse(
    val info: CharactersPageInfo,
    val results: List<Character>
)

@Serializable
data class CharactersPageInfo(
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
    val origin: CharacterOrigin,
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
data class CharacterOrigin(
    val name: String,
    val url: String
)