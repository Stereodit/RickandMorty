package com.example.rickandmorty.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CharacterEntity::class, CharacterLocationEntity::class, CharacterOriginEntity::class, CharacterEpisodeEntity::class, RemoteKeys::class],
    version = 2
)
abstract class RickAndMortyDatabase: RoomDatabase() {
    abstract val getCharactersDao: CharactersDao
    abstract val getRemoteKeysDao: RemoteKeysDao

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