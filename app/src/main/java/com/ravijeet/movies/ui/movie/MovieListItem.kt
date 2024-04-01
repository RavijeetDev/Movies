package com.ravijeet.movies.ui.movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ravijeet.movies.R
import com.ravijeet.movies.core.search.domain.model.Movie
import com.ravijeet.movies.ui.imagePlaceholder.debugPlaceholder
import com.ravijeet.movies.ui.theme.MoviesTheme

@Composable
fun MovieListItem(
    modifier: Modifier = Modifier,
    movie: Movie
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(110.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = movie.posterUrl,
                error = painterResource(id = R.drawable.no_poster_placeholder),
                placeholder = debugPlaceholder(debugPreview = R.drawable.no_poster_placeholder),
                contentDescription = null,
                modifier = Modifier.width(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Text(
                    text = "Year: ${movie.year}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Tap")
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun MovieListItemPreview() {
    val movie = Movie(
        imdbId = "01",
        title = "Movie Name Movie Name Movie Name Movie Name Movie Name Movie Name Movie Name Movie Name Movie Name Movie Name ",
        year = "2024",
        type = "movie",
        posterUrl = "https://picsum.photos/300/200"
    )
    MoviesTheme {
        MovieListItem(modifier = Modifier.padding(8.dp), movie = movie)
    }
}