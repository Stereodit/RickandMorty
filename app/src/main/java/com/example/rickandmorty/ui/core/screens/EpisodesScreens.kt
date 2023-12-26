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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.models.Episode
import com.example.rickandmorty.ui.core.ErrorLazyGrid
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingLazyGrid
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.viewmodels.EpisodesViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EpisodesScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    episodesViewModel: EpisodesViewModel = viewModel(factory = EpisodesViewModel.Factory)
) {
    val episodes = episodesViewModel.episodesFlow.collectAsLazyPagingItems()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = episodes.loadState.refresh is LoadState.Loading,
        onRefresh = { episodes.refresh() }
    )

    if (episodes.loadState.refresh is LoadState.Error) {
        ErrorScreen( retryAction = { episodes.refresh() } )
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
                        item(span = { GridItemSpan(maxLineSpan) } ) {
                            if (episodes.loadState.append is LoadState.Loading) LoadingLazyGrid()
                            if (episodes.loadState.append is LoadState.Error) ErrorLazyGrid(retryAction = { episodes.retry() })
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