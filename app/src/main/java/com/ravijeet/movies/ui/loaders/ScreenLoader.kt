package com.ravijeet.movies.ui.loaders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ravijeet.movies.ui.theme.BackgroundColor
import com.ravijeet.movies.ui.theme.MoviesTheme


@Composable
fun ScreenLoader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp)
                .background(BackgroundColor),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun ScreenLoaderPreview() {
    MoviesTheme {
        ScreenLoader(
            Modifier.fillMaxSize()
        )
    }
}