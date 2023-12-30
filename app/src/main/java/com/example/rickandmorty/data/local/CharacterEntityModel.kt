package com.example.rickandmorty.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "character_id")
    val characterId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

data class FullInfoCharacter(
    @Embedded val characterEntity: CharacterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_owner_id"
    )
    val location: CharacterLocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_owner_id"
    )
    val origin: CharacterOriginEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_owner_id"
    )
    val episode: List<CharacterEpisodeEntity>
)

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val created: String,
    val gender: String,
    val image: String,
    val name: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String,
    var page: Int
)

@Entity(tableName = "character_location")
data class CharacterLocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "character_location_id")
    val characterLocationId: Int = 0,
    @ColumnInfo(name = "character_owner_id")
    val characterOwnerId: Int,
    val name: String,
    val url: String
)

@Entity(tableName = "character_origin")
data class CharacterOriginEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "character_origin_id")
    val characterOriginId: Int = 0,
    @ColumnInfo(name = "character_owner_id")
    val characterOwnerId: Int,
    val name: String,
    val url: String
)

@Entity(tableName = "character_episodes")
data class CharacterEpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "character_episode_id")
    val characterEpisodeId: Int = 0,
    @ColumnInfo(name = "character_owner_id")
    val characterOwnerId: Int,
    val url: String
)