package com.example.rickandmorty.data

import android.content.Context
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val rickAndMortyRepository: RickAndMortyRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val baseUrl = "https://rickandmortyapi.com/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: RickAndMortyApiService by lazy {
        retrofit.create(RickAndMortyApiService::class.java)
    }

    override val rickAndMortyRepository: RickAndMortyRepository by lazy {
        NetworkRickAndMortyRepository(rickAndMortyDatabase = RickAndMortyDatabase.getInstance(context), rickAndMortyApiService = retrofitService)
    }
}