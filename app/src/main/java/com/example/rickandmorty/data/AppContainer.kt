package com.example.rickandmorty.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.rickandmorty.data.local.RickAndMortyDatabase
import com.example.rickandmorty.data.remote.RickAndMortyApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val rickAndMortyRepository: RickAndMortyRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://rickandmortyapi.com/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: RickAndMortyApiService by lazy {
        retrofit.create(RickAndMortyApiService::class.java)
    }

    companion object {
        @Volatile
        private var INSTANCE: RickAndMortyDatabase? = null

        fun getDatabase(context: Context): RickAndMortyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    RickAndMortyDatabase::class.java, "jetpack")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    override val rickAndMortyRepository: RickAndMortyRepository by lazy {
        NetworkRickAndMortyRepository(rickAndMortyApiService = retrofitService)
    }
}