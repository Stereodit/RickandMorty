package com.example.rickandmorty.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CharactersDao {
    @Upsert
    suspend fun upsertCharacters(characters: List<CharacterEntity>)

    @Upsert
    suspend fun upsertCharacterOrigin(characterOrigin: List<CharacterOriginEntity>)

    @Upsert
    suspend fun upsertCharacterLocation(characterLocation: List<CharacterLocationEntity>)

    @Upsert
    suspend fun upsertCharacterEpisodes(characterEpisodes: List<CharacterEpisodeEntity>)

    @Query("SELECT * FROM characters ORDER BY page")
    fun pagingSource(): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :searchString || '%'")
    fun filteredPagingSource(searchString: String, filterString: String): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("Select * From remote_keys Where character_id = :id")
    suspend fun getRemoteKeyByCharacterId(id: Int): RemoteKeys?

    @Query("Delete From remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}