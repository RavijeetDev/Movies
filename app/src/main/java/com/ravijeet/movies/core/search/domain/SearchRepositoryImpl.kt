package com.ravijeet.movies.core.search.domain

import com.ravijeet.movies.core.network.ApiError
import com.ravijeet.movies.core.network.HTTPError
import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.search.asDomainModel
import com.ravijeet.movies.core.search.domain.model.SearchData
import com.ravijeet.movies.core.search.remote.SearchRemoteDataSource
import java.lang.Exception

/**
 * Class to map remote result into domain model and sending it to view model
 */
class SearchRepositoryImpl(private val dataSource: SearchRemoteDataSource) : SearchRepository {

    /**
     * Fetch movies list data from SearchApi and returns RemoteResult
     *
     * If response is success with empty list we wrap it into a remote error
     * of type ApiError with custom error message for api
     */
    override suspend fun getSearchMovie(text: String, page: Int): RemoteResult<SearchData> {

        return when (val result = dataSource.getSearchMovie(text, page)) {
            is RemoteResult.Success -> {
                val searchDataInDomain = result.value.asDomainModel()
                if (searchDataInDomain.response.not()) {
                    RemoteResult.Error(
                        ApiError(Throwable(""), apiErrorMessage = result.value.error ?: "")
                    )
                } else {
                    RemoteResult.Success(searchDataInDomain)
                }
            }

            is RemoteResult.Error -> result
        }
    }
}