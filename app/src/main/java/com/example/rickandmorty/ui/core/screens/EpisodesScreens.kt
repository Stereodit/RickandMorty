package com.example.rickandmorty.ui.core.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.remote.Episode
import com.example.rickandmorty.ui.core.ErrorLazyGrid
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingLazyGrid
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.NotFoundScreen
import com.example.rickandmorty.ui.core.viewmodels.EpisodesViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EpisodesScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    episodesViewModel: EpisodesViewModel = viewModel(factory = EpisodesViewModel.Factory)
) {
    var searchText by remember { mutableStateOf(episodesViewModel.searchByName.value) }

    val episodes = episodesViewModel.episodesFlow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = episodes.loadState.refresh is LoadState.Loading,
        onRefresh = {
            episodesViewModel.setSearchByName("")
            searchText = ""
            episodes.refresh()
        }
    )

    Scaffold(
        modifier = Modifier,
        topBar = {
            SearchBar(
                query = searchText,
                onQueryChange = { text ->
                    searchText = text
                    episodesViewModel.setSearchByName(searchText)
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
            if (episodes.loadState.refresh is LoadState.Error) {
                ErrorScreen(retryAction = { episodes.refresh() })
            } else {
                when (episodes.loadState.prepend) {
                    is LoadState.Loading -> {
                        LoadingScreen()
                    }

                    is LoadState.Error -> {
                        ErrorScreen(retryAction = { episodes.retry() })
                    }

                    is LoadState.NotLoading -> {
                        Box {
                            if (episodes.itemCount == 0 && episodes.loadState.refresh !is LoadState.Loading) {
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
                                    items(episodes.itemCount) {
                                        if (episodes[it] != null)
                                            EpisodeCard(episode = episodes[it]!!)
                                    }
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        if (episodes.loadState.append is LoadState.Loading) LoadingLazyGrid()
                                        if (episodes.loadState.append is LoadState.Error) ErrorLazyGrid(
                                            retryAction = { episodes.retry() })
                                    }
                                }
                                PullRefreshIndicator(
                                    refreshing = episodes.loadState.refresh is LoadState.Loading,
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
fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(104.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = episode.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = modifier.weight(1.0F))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = episode.episode,
                    fontSize = 12.sp
                )
                Text(
                    text = episode.airDate,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewEpisodeCard() {
    RickAndMortyTheme {
        EpisodeCard(
            episode = MockData.mockEpisode,
            modifier = Modifier.sizeIn(maxWidth = 250.dp)
        )
    }
}