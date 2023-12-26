package com.example.rickandmorty.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodesSet(
    val info: EpisodesInfo,
    val results: List<Episode>
)

@Serializable
data class EpisodesInfo(
    val count: Int,
    val next: String?,
    val pages: Int,
    val prev: String?
)

@Serializable
data class Episode(
    @SerialName(value = "air_date")
    val airDate: String,
    val characters: List<String>,
    val created: String,
    val episode: String,
    val id: Int,
    val name: String,
    val url: String
)