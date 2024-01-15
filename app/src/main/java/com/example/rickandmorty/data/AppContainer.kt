package com.example.rickandmorty.data

import android.content.Context
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

const val PAGE_SIZE = 20

interface AppContainer {
    val locationRepository: LocationRepository
    val episodeRepository: EpisodeRepository
    val characterRepository: CharacterRepository
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

    override val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(rickAndMortyLocalDataSource = RickAndMortyDatabase.getInstance(context), rickAndMortyRemoteDataSource = retrofitService)
    }

    override val episodeRepository: EpisodeRepository by lazy {
        EpisodeRepositoryImpl(rickAndMortyLocalDataSource = RickAndMortyDatabase.getInstance(context), rickAndMortyRemoteDataSource = retrofitService)
    }

    override val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(rickAndMortyLocalDataSource = RickAndMortyDatabase.getInstance(context), rickAndMortyRemoteDataSource = retrofitService)
    }
}