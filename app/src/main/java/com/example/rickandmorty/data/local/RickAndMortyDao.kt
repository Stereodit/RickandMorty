package com.example.rickandmorty.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface RickAndMortyDao {

    @Transaction
    @Upsert
    suspend fun upsertAll(characters: List<FullInfoCharacter>)

    @Transaction
    @Query("SELECT * FROM characters")
    fun pagingSource(): PagingSource<Int, FullInfoCharacter>

    @Transaction
    @Query("DELETE FROM characters")
    suspend fun clearAll()
}