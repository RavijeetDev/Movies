package com.ravijeet.movies.core.search

import com.ravijeet.movies.core.search.domain.model.Movie
import com.ravijeet.movies.core.search.domain.model.SearchData
import com.ravijeet.movies.core.search.remote.model.MovieRemote
import com.ravijeet.movies.core.search.remote.model.SearchResponse
import java.util.Locale

/**
 * Mapping Remote Model [MovieRemote] to Domain Model [Movie]
 */
fun MovieRemote.asDomainModel() = Movie(
    imdbId = imdbId,
    title = title,
    year = year,
    type = type,
    posterUrl = posterUrl
)


/**
 * Mapping Remote Model [SearchResponse] to Domain Model [SearchData]
 */
fun SearchResponse.asDomainModel() = SearchData(
    totalResults = totalResults?.toInt() ?: 0,
    response = response.lowercase(Locale.ROOT).toBoolean(),
    searchResults = searchResults?.map {
        it.asDomainModel()
    } as? ArrayList<Movie> ?: arrayListOf(),
    error = error ?: ""
)