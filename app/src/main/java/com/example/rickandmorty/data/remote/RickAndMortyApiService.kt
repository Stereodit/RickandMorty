package com.example.rickandmorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character/")
    suspend fun getCharactersByPage(
        @Query("page") page: Int
    ): CharactersResponse

    @GET("episode/")
    suspend fun getEpisodesByPage(
        @Query("page") page: Int
    ): EpisodesResponse

    @GET("location/")
    suspend fun getLocationsByPage(
        @Query("page") page: Int
    ): LocationsResponse
}