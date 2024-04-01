package com.ravijeet.movies.core.search.remote.model

import com.squareup.moshi.Json

data class SearchResponse(

    @Json(name = "Search")
    val searchResults: List<MovieRemote>?,

    val totalResults: String?,

    @Json(name = "Response")
    val response: String,

    @Json(name = "Error")
    val error: String?
)
