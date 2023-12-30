package com.example.rickandmorty.ui.core.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.remote.Location
import com.example.rickandmorty.ui.core.ErrorLazyGrid
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingLazyGrid
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.NotFoundScreen
import com.example.rickandmorty.ui.core.viewmodels.LocationsViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    locationsViewModel: LocationsViewModel = viewModel(factory = LocationsViewModel.Factory)
) {
    var searchText by remember { mutableStateOf(locationsViewModel.searchByName.value) }

    val locations = locationsViewModel.locationsFlow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = locations.loadState.refresh is LoadState.Loading,
        onRefresh = { locations.refresh() }
    )

    Scaffold(
        modifier = Modifier,
        topBar = {
            SearchBar(
                query = searchText,
                onQueryChange = { text ->
                    searchText = text
                    locationsViewModel.setSearchByName(searchText)
                },
                onSearch = {
                    focusManager.clearFocus()
                },
                active = false,
                onActiveChange = {},
                placeholder = { Text(text = "Search...") },
                shape = SearchBarDefaults.inputFieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {}
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (locations.loadState.refresh is LoadState.Error) {
                ErrorScreen(retryAction = { locations.refresh() })
            } else {
                when (locations.loadState.prepend) {
                    is LoadState.Loading -> {
                        LoadingScreen()
                    }

                    is LoadState.Error -> {
                        ErrorScreen(retryAction = { locations.retry() })
                    }

                    is LoadState.NotLoading -> {
                        Box {
                            if (locations.itemCount == 0 && locations.loadState.refresh !is LoadState.Loading) {
                                NotFoundScreen()
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .pullRefresh(pullRefreshState),
                                    contentPadding = PaddingValues(4.dp),
                                    state = lazyListState
                                ) {
                                    items(locations.itemCount) {
                                        if (locations[it] != null)
                                            LocationCard(location = locations[it]!!)
                                    }
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        if (locations.loadState.append is LoadState.Loading) LoadingLazyGrid()
                                        if (locations.loadState.append is LoadState.Error) ErrorLazyGrid(
                                            retryAction = { locations.retry() })
                                    }
                                }
                                PullRefreshIndicator(
                                    refreshing = locations.loadState.refresh is LoadState.Loading,
                                    state = pullRefreshState,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: Location,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = location.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Justify,
                lineHeight = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Text(
                text =
                    if (location.dimension.length <= 10) {
                        if (location.dimension != "")
                            "Dimension: " + location.dimension
                        else "Dimension: Not specified"
                    } else if (location.dimension.contains("Dimension"))
                        location.dimension
                    else "Dimension:\n" + location.dimension,
                lineHeight = 16.sp,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1.0F))
            Text(
                text = location.type,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLocationCard() {
    RickAndMortyTheme {
        LocationCard(
            location = MockData.mockLocation,
            modifier = Modifier.sizeIn(minWidth = 250.dp, maxWidth = 300.dp)
        )
    }
}