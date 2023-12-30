package com.example.rickandmorty.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodesResponse(
    val info: EpisodesPageInfo,
    val results: List<Episode>
)

@Serializable
data class EpisodesPageInfo(
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