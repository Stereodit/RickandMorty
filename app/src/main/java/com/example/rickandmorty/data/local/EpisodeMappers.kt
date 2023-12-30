package com.example.rickandmorty.data.local

import com.example.rickandmorty.data.remote.Episode

fun Episode.toFullInfoEpisode(page: Int): FullInfoEpisode {
    return FullInfoEpisode(
        episodeEntity = EpisodeEntity(
            id = id,
            airDate = airDate,
            created = created,
            episode = episode,
            name = name,
            url = url,
            page = page
        ),
        episodeCharacters = characters.toEpisodeCharactersEntity(id)
    )
}

fun List<String>.toEpisodeCharactersEntity(episodeId: Int): List<EpisodeCharacterEntity> {
    var list: MutableList<EpisodeCharacterEntity> = mutableListOf()
    this.forEach { list.add(EpisodeCharacterEntity(episodeOwnerId = episodeId, url = it)) }
    return list.toList()
}