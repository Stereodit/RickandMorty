package com.example.rickandmorty.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Characters: BottomBarItem(
        route = "characters",
        title = "Characters",
        icon = Icons.Default.Face
    )

    object Locations: BottomBarItem(
        route = "locations",
        title = "Locations",
        icon = Icons.Default.Place
    )

    object Episodes: BottomBarItem(
        route = "episodes",
        title = "Episodes",
        icon = Icons.Default.Menu
    )
}