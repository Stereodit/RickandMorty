package com.example.rickandmorty.ui.core.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
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
import com.example.rickandmorty.data.remote.models.Episode
import com.example.rickandmorty.ui.core.ErrorScreen
import com.example.rickandmorty.ui.core.LoadingScreen
import com.example.rickandmorty.ui.core.viewmodels.CharacterDetailsUiState
import com.example.rickandmorty.ui.core.viewmodels.CharacterDetailsViewModel
import com.example.rickandmorty.ui.core.viewmodels.EpisodesOfSelectedCharacterUiState
import com.example.rickandmorty.ui.theme.RickAndMortyTheme
import kotlinx.coroutines.launch

@Composable
fun CharacterDetailsScreen(
    characterDetailsViewModel: CharacterDetailsViewModel = viewModel(factory = CharacterDetailsViewModel.Factory),
    selectedCharacterId: Int,
    onEpisodeClick: (Int) -> Unit,
    onOriginClick: (Int) -> Unit,
    onLocationClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit
) {
    BackHandler { onCancelButtonClick() }

    val composableScope = rememberCoroutineScope()

    LaunchedEffect(
        key1 = characterDetailsViewModel,
        block = {
            characterDetailsViewModel.getCharacter(selectedCharacterId)
            characterDetailsViewModel.getEpisodesOfSelectedCharacter()
        }
    )

    when(characterDetailsViewModel.characterDetailsUiState) {
        is CharacterDetailsUiState.Loading -> LoadingScreen()
        is CharacterDetailsUiState.SuccessLoadCharacter -> {
                CharacterDetailsScreenLayout(
                    character = (characterDetailsViewModel.characterDetailsUiState
                            as CharacterDetailsUiState.SuccessLoadCharacter).character,
                    episodesUiState = characterDetailsViewModel.episodesUiState,
                    retryAction = { characterDetailsViewModel.getEpisodesOfSelectedCharacter() },
                    onEpisodeClick = onEpisodeClick,
                    onOriginClick = onOriginClick,
                    onLocationClick = onLocationClick,
                    onBackButtonClick = onBackButtonClick
                )
            }
        else -> ErrorScreen(retryAction = {
            composableScope.launch {
                characterDetailsViewModel.getCharacter(selectedCharacterId)
            }
        })
    }
}

@Composable
fun CharacterDetailsScreenLayout(
    episodesUiState: EpisodesOfSelectedCharacterUiState,
    retryAction: () -> Unit,
    onEpisodeClick: (Int) -> Unit,
    onOriginClick: (Int) -> Unit,
    onLocationClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    character: Character
) {
    var isActive by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onBackButtonClick() }
                )
                Text(
                    text = character.name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ){
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Card {
                    Column {
                        if(isActive) {
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
                                    .aspectRatio(1.0F)
                            )
                        }
                        Row (
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        append("Identity document: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.id.toString())
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Name: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.name)
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Status: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.status)
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Species: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.species)
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Type: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.type.ifEmpty { "*none*" })
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Gender: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(character.gender)
                                        }
                                    },
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp
                                )
                                Row {
                                    Text(
                                        text = "Origin: ",
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp
                                    )
                                    if (character.origin.name != "unknown") {
                                        Text(
                                            text = character.origin.name,
                                            color = MaterialTheme.colorScheme.inversePrimary,
                                            modifier = Modifier
                                                .clickable {
                                                    onOriginClick(
                                                        character.origin.url.removePrefix("https://rickandmortyapi.com/api/location/")
                                                            .toInt()
                                                    )
                                                },
                                            fontSize = 14.sp,
                                            lineHeight = 16.sp
                                        )
                                    } else {
                                        Text(
                                            text = "*none*",
                                            fontSize = 14.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                                Row {
                                    Text(
                                        text = "Location: ",
                                        fontSize = 14.sp,
                                        lineHeight = 16.sp
                                    )
                                    if (character.location.name != "unknown") {
                                        Text(
                                            text = character.location.name,
                                            color = MaterialTheme.colorScheme.inversePrimary,
                                            modifier = Modifier
                                                .clickable {
                                                    onLocationClick(
                                                        character.location.url.removePrefix(
                                                            "https://rickandmortyapi.com/api/location/"
                                                        ).toInt()
                                                    )
                                                },
                                            fontSize = 14.sp,
                                            lineHeight = 16.sp
                                        )
                                    } else {
                                        Text(
                                            text = "*none*",
                                            fontSize = 14.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.weight(1.0F))
                            Icon(
                                imageVector = if(isActive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { isActive = !isActive }
                                    .align(Alignment.Bottom)
                            )
                        }
                    }
                }

                Text(
                    text = "Episode(s) with this character:",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp),
                    fontSize = 18.sp
                )

                when(episodesUiState) {
                    is EpisodesOfSelectedCharacterUiState.Loading -> LoadingScreen()
                    is EpisodesOfSelectedCharacterUiState.SuccessLoadEpisodes -> {
                        LazyColumn() {
                            items(episodesUiState.episodesList) {
                                CharacterDetailsEpisodeCard(
                                    episode = it,
                                    onEpisodeClick = onEpisodeClick
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
fun CharacterDetailsEpisodeCard(
    episode: Episode,
    onEpisodeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onEpisodeClick(episode.id) }
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

@Preview(showBackground = true)
@Composable
fun PreviewCharacterDetailsScreenLayout() {
    RickAndMortyTheme {
        CharacterDetailsScreenLayout(
            episodesUiState = EpisodesOfSelectedCharacterUiState.Loading,
            retryAction = {},
            onEpisodeClick = {},
            onOriginClick = {},
            onLocationClick = {},
            onBackButtonClick = {},
            character = MockData.mockCharacter
        )
    }
}