package com.example.rickandmorty.data.local

import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.remote.CharacterLocation
import com.example.rickandmorty.data.remote.Origin

fun Character.toFullInfoCharacter(): FullInfoCharacter {
    return FullInfoCharacter(
        characterEntity = CharacterEntity(
            id = id,
            created = created,
            gender = gender,
            image = image,
            name = name,
            species = species,
            status = status,
            type = type,
            url = url
        ),
        location = CharacterLocationEntity(
            characterOwnerId = id,
            name = this.location.name,
            url = this.location.url
        ),
        origin = CharacterOriginEntity(
            characterOwnerId = id,
            name = this.origin.name,
            url = this.origin.url
        ),
        episodes = episodes.toEntityEpisode(id)
    )
}

fun FullInfoCharacter.toCharacterDto(): Character {
    return Character(
        created = characterEntity.created,
        episodes = episodes.toStringList(),
        gender = characterEntity.gender,
        id = characterEntity.id,
        image = characterEntity.image,
        location = CharacterLocation(
            name = location.name,
            url = location.url
        ),
        name = characterEntity.name,
        origin = Origin(
            name = origin.name,
            url = origin.url
        ),
        species = characterEntity.species,
        status = characterEntity.species,
        type = characterEntity.type,
        url = characterEntity.url
    )
}

private fun List<CharacterEpisodeEntity>.toStringList(): List<String> {
    var list: MutableList<String> = mutableListOf()
    this.forEach { list.add(it.url) }
    return list.toList()
}

private fun List<String>.toEntityEpisode(characterId: Int): List<CharacterEpisodeEntity> {
    var list: MutableList<CharacterEpisodeEntity> = mutableListOf()
    this.forEach { list.add(CharacterEpisodeEntity(characterOwnerId = characterId, url = it)) }
    return list.toList()
}