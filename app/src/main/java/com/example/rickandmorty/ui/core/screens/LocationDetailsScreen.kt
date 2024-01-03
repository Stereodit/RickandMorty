package com.example.rickandmorty.ui.core.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.mock.MockData
import com.example.rickandmorty.data.remote.models.Character
import com.example.rickandmorty.data.remote.models.Location
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.viewmodels.CharactersOfSelectedLocationUiState
import com.example.rickandmorty.ui.core.viewmodels.LocationDetailsUiState
import com.example.rickandmorty.ui.core.viewmodels.LocationDetailsViewModel
import com.example.rickandmorty.ui.theme.RickAndMortyTheme
import kotlinx.coroutines.launch

@Composable
fun LocationDetailsScreen(
    modifier: Modifier = Modifier,
    locationDetailsViewModel: LocationDetailsViewModel = viewModel(factory = LocationDetailsViewModel.Factory),
    selectedLocationId: Int,
    onCharacterClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit
) {
    BackHandler { onCancelButtonClick() }
    val composableScope = rememberCoroutineScope()

    LaunchedEffect(
        key1 = locationDetailsViewModel,
        block = {
            locationDetailsViewModel.getLocation(selectedLocationId)
            locationDetailsViewModel.getCharactersOfSelectedLocation()
        }
    )

    when(locationDetailsViewModel.locationDetailsUiState) {
        is LocationDetailsUiState.Loading -> LoadingScreen()
        is LocationDetailsUiState.SuccessLoadLocation ->
            LocationDetailsScreenLayout(
                location = (locationDetailsViewModel.locationDetailsUiState
                        as LocationDetailsUiState.SuccessLoadLocation).location,
                charactersUiState = locationDetailsViewModel.charactersUiState,
                retryAction = { locationDetailsViewModel.getCharactersOfSelectedLocation() },
                onCharacterClick = onCharacterClick,
                onBackButtonClick = onBackButtonClick
            )
        else -> ErrorScreen(retryAction = {
            composableScope.launch {
                locationDetailsViewModel.getLocation(selectedLocationId)
            }
        })
    }
}

@Composable
fun LocationDetailsScreenLayout(
    modifier: Modifier = Modifier,
    charactersUiState: CharactersOfSelectedLocationUiState,
    retryAction: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    location: Location
) {
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { onBackButtonClick() }
                )
                Text(text = location.name)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Identity document: " + location.id)
                Text(text = "Name: " + location.name)
                Text(text = "Type: " + location.type.ifEmpty { "*none*" })
                Text(text = "Dimension: " + location.dimension.ifEmpty { "*none*" })

                when(charactersUiState) {
                    is CharactersOfSelectedLocationUiState.Loading -> LoadingScreen()
                    is CharactersOfSelectedLocationUiState.SuccessLoadCharacters -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(charactersUiState.charactersList) {
                                LocationDetailsCharacterCard(
                                    character = it,
                                    onCharacterClick = onCharacterClick
                                )
                            }
                        }
                    }
                    else -> ErrorScreen(retryAction = retryAction)
                }
            }
        }
    }
}

@Composable
fun LocationDetailsCharacterCard(
    character: Character,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = Modifier
            .padding(4.dp)
            .clickable { onCharacterClick(character.id) }
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

@Preview(showBackground = true)
@Composable
fun PreviewLocationDetailsScreenLayout() {
    RickAndMortyTheme {
        LocationDetailsScreenLayout(
            charactersUiState = CharactersOfSelectedLocationUiState.Loading,
            retryAction = {},
            onCharacterClick = {},
            onBackButtonClick = {},
            location = MockData.mockLocation
        )
    }
}