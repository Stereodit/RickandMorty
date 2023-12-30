package com.example.rickandmorty.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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