package com.ravijeet.movies.core.search.remote

import com.ravijeet.movies.core.network.NetworkConstants
import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.network.safeApiCall
import com.ravijeet.movies.core.search.remote.model.SearchResponse

/**
 * RemoteDataSource Implementation class for calling api safely from [SearchApi] and returning
 * data in RemoteResult
 */
class SearchRemoteDataSourceImpl(private val api: SearchApi): SearchRemoteDataSource {

    override suspend fun getSearchMovie(searchText: String, page: Int): RemoteResult<SearchResponse> {
        return safeApiCall { api.getSearchMovie(searchText, page, NetworkConstants.API_KEY) }
    }
}