package com.example.rickandmorty.data.local.mappers

import com.example.rickandmorty.data.local.models.FullInfoLocation
import com.example.rickandmorty.data.local.models.LocationCharacterEntity
import com.example.rickandmorty.data.local.models.LocationEntity
import com.example.rickandmorty.data.remote.models.Location

fun Location.toFullInfoLocation(page: Int): FullInfoLocation {
    return FullInfoLocation(
        locationEntity = LocationEntity(
            id = id,
            created = created,
            dimension = dimension,
            name = name,
            type = type,
            url = url,
            page = page
        ),
        locationCharacters = residents.toLocationCharactersEntity(id)
    )
}

fun List<String>.toLocationCharactersEntity(locationId: Int): List<LocationCharacterEntity> {
    var list: MutableList<LocationCharacterEntity> = mutableListOf()
    this.forEach { list.add(LocationCharacterEntity(locationOwnerId = locationId, url = it)) }
    return list.toList()
}