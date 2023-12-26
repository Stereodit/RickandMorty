package com.example.rickandmorty.ui.core.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingSource
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.models.Character
import com.example.rickandmorty.data.models.Filters
import com.example.rickandmorty.ui.core.ErrorLazyGrid
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingLazyGrid
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.NotFoundScreen
import com.example.rickandmorty.ui.core.viewmodels.CharactersViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme
import retrofit2.HttpException

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    charactersViewModel: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
) {
    val characters = charactersViewModel.charactersFlow.collectAsLazyPagingItems()
    var searchText by remember {
        mutableStateOf(charactersViewModel.searchByName.value)
    }
    var isActive by remember {
        mutableStateOf(false)
    }
    var checked by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(Filters.SelectedCharacterFilters("", "", "")) }
    val focusManager = LocalFocusManager.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = characters.loadState.refresh is LoadState.Loading,
        onRefresh = {
            charactersViewModel.setSearchByName("")
            searchText = ""
            characters.refresh()
        }
    )

    Scaffold(
        modifier = Modifier,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    SearchBar(
                        query = searchText,
                        onQueryChange = { text ->
                            searchText = text
                            charactersViewModel.setSearchByName(searchText)
                        },
                        onSearch = {
                            isActive = false
                            focusManager.clearFocus()
                        },
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text(text = "Search...") },
                        shape = SearchBarDefaults.inputFieldShape,
                        modifier = modifier
                            .weight(1.0F)
                    ) {}
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            modifier = modifier
                                .size(30.dp)
                                .clickable { isActive = !isActive },
                            painter = painterResource(R.drawable.baseline_filter_alt_24),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if(isActive) {
                            Image(
                                modifier = modifier
                                    .size(15.dp),
                                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = null,
                            )
                        } else {
                            Image(
                                modifier = modifier
                                    .size(15.dp),
                                painter = painterResource(R.drawable.baseline_arrow_drop_up_24),
                                contentDescription = null,
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                if (it) {
                                    charactersViewModel.setFilterStatus(selectedFilters.status)
                                    charactersViewModel.setFilterSpecies(selectedFilters.species)
                                    charactersViewModel.setFilterGender(selectedFilters.gender)
                                    charactersViewModel.setIsActiveFilters(true)
                                } else {
                                    selectedFilters.status = ""
                                    selectedFilters.species = ""
                                    selectedFilters.gender = ""
                                    charactersViewModel.setFilterStatus("")
                                    charactersViewModel.setFilterSpecies("")
                                    charactersViewModel.setFilterGender("")
                                }
                            },
                            modifier = Modifier
                        )
                    }
                }
                if (isActive) {
                    FiltersCard(
                        selectedFilters = selectedFilters,
                        filters = Filters.CharacterFilters
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (characters.loadState.refresh is LoadState.Error) {
                ErrorScreen( retryAction = { characters.refresh() } )
            } else {
                when (characters.loadState.prepend) {
                    is LoadState.Loading -> {
                        LoadingScreen()
                    }
                    is LoadState.Error -> {
                        ErrorScreen(retryAction = { characters.retry() })
                    }
                    is LoadState.NotLoading -> {
                        Box {
                            if (characters.itemCount == 0 && characters.loadState.refresh !is LoadState.Loading) {
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
                                    items(characters.itemCount) {
                                        if (characters[it] != null)
                                            CharacterCard(character = characters[it]!!)
                                    }
                                    item(span = { GridItemSpan(maxLineSpan) } ) {
                                        if (characters.loadState.append is LoadState.Loading) LoadingLazyGrid()
                                        if (characters.loadState.append is LoadState.Error) ErrorLazyGrid(retryAction = { characters.retry() })
                                    }
                                }
                            }
                            PullRefreshIndicator(
                                refreshing = characters.loadState.refresh is LoadState.Loading,
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

@Composable
fun FiltersCard(
    modifier: Modifier = Modifier,
    selectedFilters: Filters.SelectedCharacterFilters,
    filters: Filters.CharacterFilters
) {
    Card(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = "Status:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.status.size) {
                    OutlinedButton(
                        onClick = { selectedFilters.status = filters.status[it] },
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    ) {
                        Text(text = filters.status[it])
                    }
                }
            }
            Text(text = "Species:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.species.size) {
                    OutlinedButton(
                        onClick = { selectedFilters.species = filters.species[it] },
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    ) {
                        Text(text = filters.species[it])
                    }
                }
            }
            Text(text = "Gender:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.gender.size) {
                    OutlinedButton(
                        onClick = { selectedFilters.gender = filters.gender[it] },
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    ) {
                        Text(text = filters.gender[it])
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
            )
            Text(
                text = buildAnnotatedString {
                        append("Name: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(character.name)
                        }
                    },
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp, start = 8.dp, end = 8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Species: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(character.species)
                    }
                },
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 8.dp, end = 8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Status: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(character.status)
                    }
                },
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp, start = 8.dp, end = 8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Gender: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(character.gender)
                    }
                },
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

//@Preview(showBackground = false)
//@Composable
//fun PreviewCharacterCard() {
//    RickAndMortyTheme {
//        CharacterCard(
//            character = MockData.mockCharacter,
//        )
//    }
//}

@Preview(showBackground = false)
@Composable
fun PreviewFilterCard() {
    RickAndMortyTheme {
        FiltersCard(selectedFilters = Filters.SelectedCharacterFilters("", "", ""), filters = Filters.CharacterFilters)
    }
}