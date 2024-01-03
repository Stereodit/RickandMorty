package com.example.rickandmorty.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rickandmorty.data.local.models.CharacterEntity
import com.example.rickandmorty.data.local.models.CharacterEpisodeEntity
import com.example.rickandmorty.data.local.models.CharacterLocationEntity
import com.example.rickandmorty.data.local.models.CharacterOriginEntity
import com.example.rickandmorty.data.local.models.CharacterRemoteKeys
import com.example.rickandmorty.data.local.models.EpisodeCharacterEntity
import com.example.rickandmorty.data.local.models.EpisodeEntity
import com.example.rickandmorty.data.local.models.EpisodeRemoteKeys
import com.example.rickandmorty.data.local.models.LocationCharacterEntity
import com.example.rickandmorty.data.local.models.LocationEntity
import com.example.rickandmorty.data.local.models.LocationRemoteKeys

@Database(
    entities = [
        CharacterEntity::class,
        CharacterLocationEntity::class,
        CharacterOriginEntity::class,
        CharacterEpisodeEntity::class,
        CharacterRemoteKeys::class,
        EpisodeEntity::class,
        EpisodeCharacterEntity::class,
        EpisodeRemoteKeys::class,
        LocationEntity::class,
        LocationCharacterEntity::class,
        LocationRemoteKeys::class
    ],
    version = 7
)
abstract class RickAndMortyDatabase: RoomDatabase() {
    abstract val getCharactersDao: CharactersDao
    abstract val getCharactersRemoteKeysDao: CharactersRemoteKeysDao

    abstract val getEpisodesDao: EpisodesDao
    abstract val getEpisodesRemoteKeysDao: EpisodesRemoteKeysDao

    abstract val getLocationsDao: LocationsDao
    abstract val getLocationsRemoteKeysDao: LocationsRemoteKeysDao

    companion object {

        private var INSTANCE: RickAndMortyDatabase? = null

        fun getInstance(context: Context): RickAndMortyDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RickAndMortyDatabase::class.java,
                        "rickandmorty_db"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}