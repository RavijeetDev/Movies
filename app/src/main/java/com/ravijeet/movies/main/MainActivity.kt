package com.ravijeet.movies.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ravijeet.movies.R
import com.ravijeet.movies.ui.search.SearchBar
import com.ravijeet.movies.ui.theme.BackgroundColor
import com.ravijeet.movies.ui.theme.MoviesTheme
import com.ravijeet.movies.ui.topAppBar.TopBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ravijeet.movies.core.network.ApiError
import com.ravijeet.movies.core.network.HTTPError
import com.ravijeet.movies.ui.errorView.ErrorView
import com.ravijeet.movies.ui.loaders.ScreenLoader
import com.ravijeet.movies.ui.movie.MovieList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviesTheme {
                MainScreenWithTopBar()
            }
        }
    }
}


@Composable
fun MainScreenWithTopBar() {
    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopBar(title = stringResource(id = R.string.app_name))
        }
    ) { padding ->
        MainScreen(Modifier.padding(padding))
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current

    var inputText by rememberSaveable { mutableStateOf("") }
    val screenState = viewModel.searchState.observeAsState().value
    val movieList = viewModel.searchList.observeAsState(listOf()).value
    val error = viewModel.error.observeAsState().value
    val isPaginating = viewModel.isPaginating.observeAsState()

    Column {
        SearchBar(onValueChanged = {
            inputText = it
        }, modifier.padding(8.dp))

        when (screenState) {
            MainViewModel.SearchState.LOADING -> {
                ScreenLoader(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            MainViewModel.SearchState.SUCCESS -> {
                MovieList(
                    list = movieList,
                    modifier = Modifier.weight(1f),
                    loadNewPage = {
                        viewModel.loadNextPage()
                    }
                )
            }

            MainViewModel.SearchState.ERROR -> {
                val message =
                    if (error != null)
                        if (error is ApiError)
                            error.apiErrorMessage
                        else stringResource(id = error.messageResId)
                    else stringResource(id = R.string.unknown_error)
                ErrorView(
                    errorMessage = message,
                    onRetryCalled = { viewModel.retryCall() },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            else -> {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (isPaginating.value == true) {
            ScreenLoader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                if (inputText.trim().isEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter some text to search!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.resetPages()
                    viewModel.getInitialSearch(inputText)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.search_button_text),
                Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun MainScreenWithTopBarPreview() {
    MoviesTheme {
        MainScreenWithTopBar()
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun MainScreenPreview() {
    MoviesTheme {
        MainScreen()
    }
}