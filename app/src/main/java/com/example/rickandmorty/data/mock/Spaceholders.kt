package com.example.rickandmorty.data.mock

import com.example.rickandmorty.data.remote.Episode
import com.example.rickandmorty.data.remote.Location
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.data.remote.CharacterLocation
import com.example.rickandmorty.data.remote.CharacterOrigin

object MockData {
    val mockCharacter = Character(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = CharacterOrigin("Earth", ""),
        location = CharacterLocation("Earth", ""),
        image = "",
        episode = listOf("", ""),
        url = "",
        created = "2017-11-04T18:50:21.651Z"
    )
    val mockEpisode = Episode(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = "S01E01",
        characters = listOf("", ""),
        url = "",
        created = "2017-11-10T12:56:33.798Z"
    )
    val mockLocation = Location(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        residents = listOf("", ""),
        url = "",
        created = "2017-11-10T12:42:04.162Z"
    )
}
