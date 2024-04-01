package com.ravijeet.movies.core.search.remote

import com.ravijeet.movies.core.search.remote.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface used with retrofit to access the Search API
 */
interface SearchApi {

    @GET("/")
    suspend fun getSearchMovie(
        @Query("s") searchText: String,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String
    ): SearchResponse
}