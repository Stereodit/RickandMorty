package com.example.rickandmorty.ui.core.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.models.Filters
import com.example.rickandmorty.data.remote.Character
import com.example.rickandmorty.ui.core.ErrorLazyGrid
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingLazyGrid
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.NotFoundScreen
import com.example.rickandmorty.ui.core.viewmodels.CharactersViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    charactersViewModel: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
) {
    var searchText by remember { mutableStateOf(charactersViewModel.searchByName.value) }
    var isActive by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    val characters = charactersViewModel.charactersFlow.collectAsLazyPagingItems()
    val selectedFilters by remember { mutableStateOf(Filters.SelectedCharacterFilters("", "", "")) }
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
                            painter = painterResource(R.drawable.ic_filter_image),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if(isActive) {
                            Image(
                                modifier = modifier
                                    .size(15.dp),
                                painter = painterResource(R.drawable.ic_arrow_drop_down),
                                contentDescription = null,
                            )
                        } else {
                            Image(
                                modifier = modifier
                                    .size(15.dp),
                                painter = painterResource(R.drawable.ic_arrow_drop_up),
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
                                    charactersViewModel.filterStatus = selectedFilters.status
                                    charactersViewModel.filterSpecies = selectedFilters.species
                                    charactersViewModel.filterGender = selectedFilters.gender
                                    charactersViewModel.searchByName.value += " "
                                } else {
                                    charactersViewModel.filterStatus = ""
                                    charactersViewModel.filterSpecies = ""
                                    charactersViewModel.filterGender = ""
                                    selectedFilters.status = ""
                                    selectedFilters.species = ""
                                    selectedFilters.gender = ""
                                    charactersViewModel.searchByName.value += " "
                                    isActive = false
                                }
                            },
                            modifier = Modifier
                        )
                    }
                }
                if (isActive) {
                    FiltersCard(
                        selectedFilters = selectedFilters,
                        filters = Filters.CharacterFilters,
                        selectStatus = {
                            selectedFilters.status = it
                            isActive = !isActive
                            isActive = !isActive
                            if (checked) {
                                charactersViewModel.filterStatus = selectedFilters.status
                                charactersViewModel.searchByName.value += " "
                            }
                        },
                        selectSpecies = {
                            selectedFilters.species = it
                            isActive = !isActive
                            isActive = !isActive
                            if (checked) {
                                charactersViewModel.filterSpecies = selectedFilters.species
                                charactersViewModel.searchByName.value += " "
                            }
                        },
                        selectGender = {
                            selectedFilters.gender = it
                            isActive = !isActive
                            isActive = !isActive
                            if (checked) {
                                charactersViewModel.filterGender = selectedFilters.gender
                                charactersViewModel.searchByName.value += " "
                            }
                        }
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
    filters: Filters.CharacterFilters,
    selectStatus: (String) -> Unit,
    selectSpecies: (String) -> Unit,
    selectGender: (String) -> Unit,
) {
    Card(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Status:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.status.size) {
                    var selected = selectedFilters.status == filters.status[it]
                    Box(
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .selectable(
                                selected = selected,
                                onClick = {
                                    if (selected) selectStatus("")
                                    else selectStatus(filters.status[it])
                                })
                            .background(color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Text(
                            text = filters.status[it],
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                    if (filters.status.lastIndex != it) {
                        Spacer(modifier = Modifier.defaultMinSize(minWidth = 8.dp))
                    }
                }
            }
            Text(text = "Species:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.species.size) {
                    var selected = selectedFilters.species == filters.species[it]
                    Box(
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .selectable(
                                selected = selected,
                                onClick = {
                                    if (selected) selectSpecies("")
                                    else selectSpecies(filters.species[it])
                                })
                            .background(color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Text(
                            text = filters.species[it],
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                    if (filters.species.lastIndex != it) {
                        Spacer(modifier = Modifier.defaultMinSize(minWidth = 8.dp))
                    }
                }
            }
            Text(text = "Gender:", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(filters.gender.size) {
                    var selected = selectedFilters.gender == filters.gender[it]
                    Box(
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .selectable(
                                selected = selected,
                                onClick = {
                                    if (selected) selectGender("")
                                    else selectGender(filters.gender[it])
                                })
                            .background(color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    ) {
                        Text(
                            text = filters.gender[it],
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                    if (filters.gender.lastIndex != it) {
                        Spacer(modifier = Modifier.defaultMinSize(minWidth = 8.dp))
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

@Preview(showBackground = false)
@Composable
fun PreviewCharacterCard() {
    RickAndMortyTheme {
        CharacterCard(
            character = MockData.mockCharacter,
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewFilterCard() {
    RickAndMortyTheme {
        FiltersCard(
            selectedFilters = Filters.SelectedCharacterFilters("", "", ""),
            filters = Filters.CharacterFilters,
            selectStatus = {},
            selectSpecies = {},
            selectGender = {}
        )
    }
}