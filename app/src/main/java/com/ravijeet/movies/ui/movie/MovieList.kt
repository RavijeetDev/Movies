package com.ravijeet.movies.ui.movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ravijeet.movies.core.search.domain.model.Movie
import com.ravijeet.movies.ui.theme.MoviesTheme
import com.ravijeet.movies.util.rememberScrollContext

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    list: List<Movie>,
    loadNewPage: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState
    ) {
        items(list) { movie ->
            MovieListItem(movie = movie)
        }
    }

    val scrollContext = rememberScrollContext(listState)
    if (scrollContext.isBottom) {
        loadNewPage()
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun MovieListPreview() {
    val movie = Movie(
        imdbId = "01",
        title = "Movie Name",
        year = "2024",
        type = "movie",
        posterUrl = "https://picsum.photos/300/200"
    )
    val list = listOf(movie, movie, movie, movie, movie)
    MoviesTheme {
        MovieList(list = list, loadNewPage = {})
    }
}