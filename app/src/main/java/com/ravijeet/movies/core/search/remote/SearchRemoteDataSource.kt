package com.ravijeet.movies.core.search.remote

import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.search.remote.model.SearchResponse

/**
 * Interface for remote data sources used in repositories
 */
interface SearchRemoteDataSource {

    suspend fun getSearchMovie(searchText: String, page: Int): RemoteResult<SearchResponse>
}