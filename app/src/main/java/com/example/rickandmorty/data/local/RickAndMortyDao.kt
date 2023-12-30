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

    @Query("""
        SELECT * FROM characters WHERE
        (:searchString IS NULL OR name LIKE '%' || :searchString || '%') AND
        (:status IS NULL OR status = :status) AND
        (:species IS NULL OR species = :species) AND
        (:gender IS NULL OR gender = :gender)
        ORDER BY page
    """)
    fun filteredCharacterPagingSource(searchString: String?, status: String?, species: String?, gender: String?): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()

    @Query("DELETE FROM character_origin")
    suspend fun clearCharacterOrigin()

    @Query("DELETE FROM character_location")
    suspend fun clearCharacterLocation()

    @Query("DELETE FROM character_episodes")
    suspend fun clearCharacterEpisodes()

    @Query("SELECT * FROM characters ORDER BY page")
    fun getCharacters(): List<CharacterEntity>
}

@Dao
interface CharactersRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CharacterRemoteKeys>)

    @Query("Select * From character_remote_keys Where character_id = :id")
    suspend fun getRemoteKeyByCharacterId(id: Int): CharacterRemoteKeys?

    @Query("Delete From character_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From character_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}

@Dao
interface EpisodesDao {
    @Upsert
    suspend fun upsertEpisodes(episodes: List<EpisodeEntity>)

    @Upsert
    suspend fun upsertEpisodeCharacters(episodeCharacters: List<EpisodeCharacterEntity>)

    @Query("""
        SELECT * FROM episodes WHERE
        (:searchString IS NULL OR name LIKE '%' || :searchString || '%')
        ORDER BY page
    """)
    fun filteredEpisodePagingSource(searchString: String?): PagingSource<Int, EpisodeEntity>

    @Query("DELETE FROM episodes")
    suspend fun clearEpisodes()

    @Query("DELETE FROM episode_characters")
    suspend fun clearEpisodeCharacters()
}

@Dao
interface EpisodesRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<EpisodeRemoteKeys>)

    @Query("Select * From episode_remote_keys Where episode_id = :id")
    suspend fun getRemoteKeyByEpisodeId(id: Int): EpisodeRemoteKeys?

    @Query("Delete From episode_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From episode_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}

@Dao
interface LocationsDao {
    @Upsert
    suspend fun upsertLocations(locations: List<LocationEntity>)

    @Upsert
    suspend fun upsertLocationCharacters(locationCharacters: List<LocationCharacterEntity>)

    @Query("""
        SELECT * FROM locations WHERE
        (:searchString IS NULL OR name LIKE '%' || :searchString || '%')
        ORDER BY page
    """)
    fun filteredLocationPagingSource(searchString: String?): PagingSource<Int, LocationEntity>

    @Query("DELETE FROM locations")
    suspend fun clearLocations()

    @Query("DELETE FROM location_characters")
    suspend fun clearLocationCharacters()
}

@Dao
interface LocationsRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<LocationRemoteKeys>)

    @Query("Select * From location_remote_keys Where location_id = :id")
    suspend fun getRemoteKeyByLocationId(id: Int): LocationRemoteKeys?

    @Query("Delete From location_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From location_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}