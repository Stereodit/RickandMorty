package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.models.EpisodesSet
import com.example.rickandmorty.data.models.LocationsSet
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character/")
    suspend fun getCharactersByPage(
        @Query("page") page: Int,
//        @Query("name") name: String,
    ): CharactersSet

    @GET("character/")
    suspend fun getFilteredCharactersByPage(
        @Query("page") page: Int,
        @Query("name") name: String,
        @Query("status") status: String,
        @Query("species") species: String,
        @Query("gender") gender: String,
    ): CharactersSet

    @GET("episode/")
    suspend fun getEpisodesByPage(
        @Query("page") page: Int,
//        @Query("name") name: String,
    ): EpisodesSet

    @GET("location/")
    suspend fun getLocationsByPage(
        @Query("page") page: Int,
//        @Query("name") name: String,
    ): LocationsSet
}