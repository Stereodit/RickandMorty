package com.example.rickandmorty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CharacterEntity::class, CharacterLocationEntity::class, CharacterOriginEntity::class, CharacterEpisodeEntity::class],
    version = 1
)
abstract class RickAndMortyDatabase: RoomDatabase() {
    abstract val dao: RickAndMortyDao
}