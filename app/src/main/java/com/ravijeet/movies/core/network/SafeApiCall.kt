package com.ravijeet.movies.core.network

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Function to fetch api calls safely and adding their result in RemoteResult Wrapper
 */
inline fun <T> safeApiCall(apiCall: () -> T): RemoteResult<T> {
    return try {
        RemoteResult.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when(throwable) {
            is SocketTimeoutException -> {
                RemoteResult.Error(TimeOut(throwable))
            }
            is UnknownHostException -> {
                RemoteResult.Error(NoInternet(throwable))
            }
            is IOException -> {
                RemoteResult.Error(IOError(throwable))
            }
            is HttpException -> {
                val body = throwable.response()?.errorBody()
                val errorMessage = getErrorMessage(body)
                RemoteResult.Error(HTTPError(throwable = throwable, code = throwable.code(), apiErrorMessage = errorMessage))
            }
            else -> {
                RemoteResult.Error(Other(throwable))
            }
        }
    }
}


/**
 * Fetches custom error message form Api with HTTP Exception
 */
fun getErrorMessage(responseBody: ResponseBody?): String {
    return try {
        val jsonObject = JSONObject(responseBody!!.string())
        when {
            jsonObject.has("error") -> jsonObject.getString("error")
            jsonObject.has("message") -> jsonObject.getString("message")
            jsonObject.has("Message") -> jsonObject.getString("Message")
            else -> "Something wrong happened"
        }
    } catch (e: Exception) {
        "Something wrong happened"
    }
}