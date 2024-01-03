package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.CharactersResponse
import com.example.rickandmorty.data.remote.models.Episode
import com.example.rickandmorty.data.remote.models.EpisodesResponse
import com.example.rickandmorty.data.remote.models.Location
import com.example.rickandmorty.data.remote.models.LocationsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character/")
    suspend fun getCharactersByPage(
        @Query("page") page: Int
    ): CharactersResponse

    @GET("character/{id}")
    suspend fun getSelectedCharacter(@Path("id") id: Int): Character

    @GET("episode/{query}")
    suspend fun getEpisodesOfSelectedCharacter(@Path("query") query: String): List<Episode>

    @GET("episode/{query}")
    suspend fun getEpisodeOfSelectedCharacter(@Path("query") query: String): Episode

    @GET("episode/")
    suspend fun getEpisodesByPage(
        @Query("page") page: Int
    ): EpisodesResponse

    @GET("episode/{id}")
    suspend fun getSelectedEpisode(@Path("id") id: Int): Episode

    @GET("character/{query}")
    suspend fun getCharactersOfSelectedEpisode(@Path("query") query: String): List<Character>
    suspend fun getCharacterOfSelectedEpisode(@Path("query") query: String): Character

    @GET("location/")
    suspend fun getLocationsByPage(
        @Query("page") page: Int
    ): LocationsResponse

    @GET("location/{id}")
    suspend fun getSelectedLocation(@Path("id") id: Int): Location

    @GET("character/{query}")
    suspend fun getCharactersOfSelectedLocation(@Path("query") query: String): List<Character>
    suspend fun getCharacterOfSelectedLocation(@Path("query") query: String): Character
}