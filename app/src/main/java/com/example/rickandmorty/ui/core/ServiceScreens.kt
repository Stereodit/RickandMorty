package com.example.rickandmorty.ui.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rickandmorty.R

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            modifier = modifier.size(200.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error),
                contentDescription = ""
            )
            Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
            Button(onClick = retryAction) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun NotFoundScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.2F)
    ) {
        Image(
            modifier = modifier
                .size(100.dp)
                .padding(bottom = 4.dp),
            painter = painterResource(R.drawable.ic_not_found_image),
            contentDescription = stringResource(R.string.nothing_was_found),
        )
        Text(text = stringResource(R.string.nothing_was_found))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotFoundScreen(modifier: Modifier = Modifier) {
    NotFoundScreen()
}

@Composable
fun LoadingLazyGrid(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            modifier = modifier.size(100.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@Composable
fun ErrorLazyGrid(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = stringResource(R.string.error_title), modifier = Modifier.padding(bottom = 8.dp))
            Button(onClick = retryAction ) {
                Text(text = stringResource(R.string.try_again))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}