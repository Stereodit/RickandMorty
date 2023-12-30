package com.example.rickandmorty.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "episode_remote_keys")
data class EpisodeRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "episode_id")
    val episodeId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

data class FullInfoEpisode(
    @Embedded val episodeEntity: EpisodeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "episode_owner_id"
    )
    val episodeCharacters: List<EpisodeCharacterEntity>
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val airDate: String,
    val created: String,
    val episode: String,
    val name: String,
    val url: String,
    var page: Int
)

@Entity(tableName = "episode_characters")
data class EpisodeCharacterEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "episode_character_id")
    val episodeCharacterId: Int = 0,
    @ColumnInfo(name = "episode_owner_id")
    val episodeOwnerId: Int,
    val url: String
)