package com.example.rickandmorty.ui.navigation

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rickandmorty.ui.core.screens.CharactersScreen
import com.example.rickandmorty.ui.core.screens.EpisodesScreen
import com.example.rickandmorty.ui.core.screens.LocationsScreen
import com.example.rickandmorty.ui.core.viewmodels.CharactersViewModel

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
                lazyListState = LazyGridState()
            )
        }
        composable(route = BottomBarItem.Locations.route) {
            LocationsScreen(
                lazyListState = LazyGridState()
            )
        }
        composable(route = BottomBarItem.Episodes.route) {
            EpisodesScreen(
                lazyListState = LazyGridState()
            )
        }
    }
}