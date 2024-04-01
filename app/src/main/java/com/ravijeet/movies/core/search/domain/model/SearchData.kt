package com.ravijeet.movies.core.search.domain.model

import com.squareup.moshi.Json


data class SearchData(

    val searchResults: List<Movie>,

    val totalResults: Int,

    val response: Boolean,

    val error: String
)
