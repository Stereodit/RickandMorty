package com.example.rickandmorty.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationsSet(
    val info: LocationsInfo,
    val results: List<Location>
)

@Serializable
data class LocationsInfo(
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