package com.example.rickandmorty.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "location_remote_keys")
data class LocationRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "location_id")
    val locationId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
data class FullInfoLocation(
    @Embedded val locationEntity: LocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "location_owner_id"
    )
    val locationCharacters: List<LocationCharacterEntity>
)

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val created: String,
    val dimension: String,
    val name: String,
    val type: String,
    val url: String,
    val page: Int
)

@Entity(tableName = "location_characters")
data class LocationCharacterEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_character_id")
    val locationCharacterId: Int = 0,
    @ColumnInfo(name = "location_owner_id")
    val locationOwnerId: Int,
    val url: String
)