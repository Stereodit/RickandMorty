package com.example.rickandmorty.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationsResponse(
    val info: LocationsPageInfo,
    val results: List<Location>
)

@Serializable
data class LocationsPageInfo(
    val count: Int,
    val next: String?,
    val pages: Int,
    val prev: String?
)

@Serializable
data class Location(
    val created: String,
    val dimension: String,
    val id: Int,
    val name: String,
    val residents: List<String>,
    val type: String,
    val url: String
)