package com.example.rickandmorty.ui.navigation

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rickandmorty.ui.core.screens.CharacterDetailsScreen
import com.example.rickandmorty.ui.core.screens.CharactersScreen
import com.example.rickandmorty.ui.core.screens.EpisodeDetailsScreen
import com.example.rickandmorty.ui.core.screens.EpisodesScreen
import com.example.rickandmorty.ui.core.screens.LocationDetailsScreen
import com.example.rickandmorty.ui.core.screens.LocationsScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarItem.Characters.route,
        modifier = modifier
    ) {
        composable(route = BottomBarItem.Characters.route) {
            CharactersScreen(
                lazyListState = LazyGridState(),
                onCharacterClick = { characterId ->
                    navController.navigate("SelectedCharacter/$characterId")
                }
            )
        }
        composable(route = BottomBarItem.Locations.route) {
            LocationsScreen(
                lazyListState = LazyGridState(),
                onLocationClick = { locationId ->
                    navController.navigate("SelectedLocation/$locationId")
                }
            )
        }
        composable(route = BottomBarItem.Episodes.route) {
            EpisodesScreen(
                lazyListState = LazyGridState(),
                onEpisodeClick = { episodeId ->
                    navController.navigate("SelectedEpisode/$episodeId")
                }
            )
        }
        composable(
            route = "SelectedCharacter/{characterId}",
            arguments = listOf(navArgument("characterId") {
                type = NavType.IntType
            })
        ) {navBackStackEntry ->
            val characterId = navBackStackEntry.arguments?.getInt("characterId")
            characterId?.let {
                CharacterDetailsScreen(
                    selectedCharacterId = it,
                    onEpisodeClick = { episodeId ->
                        navController.navigate("SelectedEpisode/$episodeId")
                    },
                    onOriginClick = { originId ->
                        navController.navigate("SelectedLocation/$originId")
                    },
                    onLocationClick = { locationId ->
                        navController.navigate("SelectedLocation/$locationId")
                    },
                    onBackButtonClick = {
                        try {
                            navController.navigate(navController.previousBackStackEntry?.destination?.route!!)
                        } catch (e: Exception) {
                            navController.clearBackStack(BottomBarItem.Characters.route)
                            navController.navigate(BottomBarItem.Characters.route)
                        }
                    },
                    onCancelButtonClick = {
                        navController.clearBackStack(BottomBarItem.Characters.route)
                        navController.navigate(BottomBarItem.Characters.route)
                    }
                )
            }
        }
        composable(
            route = "SelectedLocation/{locationId}",
            arguments = listOf(navArgument("locationId") {
                type = NavType.IntType
            })
        ) { navBackStackEntry ->
            val locationId = navBackStackEntry.arguments?.getInt("locationId")
            locationId?.let {
                LocationDetailsScreen(
                    selectedLocationId = it,
                    onCharacterClick = { characterId ->
                        navController.navigate("SelectedCharacter/$characterId")
                    },
                    onBackButtonClick = {
                        try {
                            navController.navigate(navController.previousBackStackEntry?.destination?.route!!)
                        } catch (e: Exception) {
                            navController.clearBackStack(BottomBarItem.Locations.route)
                            navController.navigate(BottomBarItem.Locations.route)
                        }
                    },
                    onCancelButtonClick = {
                        navController.clearBackStack(BottomBarItem.Characters.route)
                        navController.navigate(BottomBarItem.Characters.route)
                    }
                )
            }
        }
        composable(
            route = "SelectedEpisode/{episodeId}",
            arguments = listOf(navArgument("episodeId") {
                type = NavType.IntType
            })
        ) { navBackStackEntry ->
            val episodeId = navBackStackEntry.arguments?.getInt("episodeId")
            episodeId?.let {
                EpisodeDetailsScreen(
                    selectedEpisodeId = it,
                    onCharacterClick = { characterId ->
                        navController.navigate("SelectedCharacter/$characterId")
                    },
                    onBackButtonClick = {
                        try {
                            navController.navigate(navController.previousBackStackEntry?.destination?.route!!)
                        } catch (e: Exception) {
                            navController.clearBackStack(BottomBarItem.Episodes.route)
                            navController.navigate(BottomBarItem.Episodes.route)
                        }
                    },
                    onCancelButtonClick = {
                        navController.clearBackStack(BottomBarItem.Characters.route)
                        navController.navigate(BottomBarItem.Characters.route)
                    }
                )
            }
        }
    }
}