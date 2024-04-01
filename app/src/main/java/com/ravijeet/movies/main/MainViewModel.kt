package com.ravijeet.movies.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ravijeet.movies.core.network.NetworkService
import com.ravijeet.movies.core.network.RemoteError
import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.search.domain.SearchRepositoryImpl
import com.ravijeet.movies.core.search.domain.model.Movie
import com.ravijeet.movies.core.search.remote.SearchRemoteDataSourceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val PAGINATION_ERROR_DELAY = 2_000L

class MainViewModel: ViewModel() {

    enum class SearchState {
        INITIAL, LOADING, SUCCESS, ERROR
    }

    private val _searchState = MutableLiveData<SearchState>(null)
    val searchState: LiveData<SearchState> = _searchState

    private val _isPaginating = MutableLiveData<Boolean>(false)
    val isPaginating: LiveData<Boolean> = _isPaginating

    private val _searchList = MutableLiveData<ArrayList<Movie>>()
    val searchList: LiveData<ArrayList<Movie>> = _searchList

    private val _error = MutableLiveData<RemoteError>()
    val error: LiveData<RemoteError> = _error

    private val searchRemoteDataSource = SearchRemoteDataSourceImpl(NetworkService.getSearchApiService())
    private val searchRepository = SearchRepositoryImpl(searchRemoteDataSource)

    private var currentPage = 1
    private var listExhausted = false
    private var searchText = ""
    private var paginationDisabled = false


    init {
        _searchState.value = SearchState.INITIAL

    }


    /**
     * This function takes in search text string and fetches the very first result for movies
     * list using the search api
     */
    fun getInitialSearch(searchText: String) {
        this.searchText = searchText
        _searchState.value = SearchState.LOADING

        viewModelScope.launch {
            when (val result = searchRepository.getSearchMovie(searchText, currentPage)) {

                is RemoteResult.Success -> {
                    val searchData = result.value
                    _searchList.value = searchData.searchResults as ArrayList<Movie>
                    _searchState.value = SearchState.SUCCESS

                    if (_searchList.value!!.size >= searchData.totalResults) {
                        listExhausted = true
                    }
                }

                is RemoteResult.Error -> {
                    _error.value = result.remoteError
                    _searchState.value = SearchState.ERROR
                }
            }
        }
    }


    /**
     * This function fetches the next pages of search results using the previous saved search
     * text string
     */
    fun loadNextPage() {
        if (!listExhausted && !paginationDisabled) {
            currentPage++
            _isPaginating.value = true

            viewModelScope.launch {
                when (val result = searchRepository.getSearchMovie(searchText, currentPage)) {

                    is RemoteResult.Success -> {
                        val searchData = result.value
                        _searchList.value?.addAll(searchData.searchResults)

                        if (_searchList.value!!.size >= searchData.totalResults) {
                            listExhausted = true
                        }

                        _isPaginating.value = false
                    }

                    is RemoteResult.Error -> {
                        currentPage--
                        paginationDisabled = true
                        _isPaginating.value = false
                        _error.value = result.remoteError

                        delay(PAGINATION_ERROR_DELAY)
                        paginationDisabled = false
                    }
                }
            }
        }
    }


    /**
     * Retry functionality in case of network error
     */
    fun retryCall() {
        if (currentPage > 1) {
            loadNextPage()
        } else {
            getInitialSearch(searchText)
        }
    }


    /**
     * Resets the variables to initial value before calling [getInitialSearch]]
     */
    fun resetPages() {
        currentPage = 1
        listExhausted = false
    }
}