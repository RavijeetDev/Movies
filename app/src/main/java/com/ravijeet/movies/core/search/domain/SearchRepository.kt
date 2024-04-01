package com.ravijeet.movies.core.search.domain

import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.search.domain.model.SearchData

/**
 * Interface of repository to interact with UI
 */
interface SearchRepository {

    suspend fun getSearchMovie(text: String, page: Int): RemoteResult<SearchData>
}