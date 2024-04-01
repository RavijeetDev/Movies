package com.ravijeet.movies.core.network

import com.ravijeet.movies.R

/**
 * Sealed class to handle remote responses
 * if its a success it will return data
 * otherwise returns [RemoteError]
 */
sealed class RemoteResult<out T> {
    data class Success<out T>(val value: T): RemoteResult<T>()
    data class Error(val remoteError: RemoteError): RemoteResult<Nothing>()
}

/**
 * Class which consists of exceptions thrown on calling api with message resource id
 */
sealed class RemoteError {
    abstract val throwable: Throwable
    abstract val messageResId: Int
}

data class NoInternet(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.no_internet
): RemoteError()

data class TimeOut(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.timed_out
): RemoteError()

data class IOError(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.io_error
): RemoteError()

data class HTTPError(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.http_error,
    val code: Int,
    var apiErrorMessage: String
): RemoteError()

data class ApiError(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.unknown_error,
    var apiErrorMessage: String
): RemoteError()

data class Other(
    override val throwable: Throwable,
    override val messageResId: Int = R.string.unknown_error,
): RemoteError()
