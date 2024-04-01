package com.ravijeet.movies.core.search.remote

import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.network.TimeOut
import com.ravijeet.movies.core.search.remote.model.SearchResponse
import com.ravijeet.movies.util.FileReaderUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.ravijeet.movies.R
import com.ravijeet.movies.core.network.HTTPError
import com.ravijeet.movies.core.network.IOError
import com.ravijeet.movies.core.network.NoInternet
import com.ravijeet.movies.core.network.Other

/**
 * Tests for [SearchRemoteDataSource]
 */
class SearchRemoteDataSourceTest {

    private val mockSearchApi = mockk<SearchApi>(relaxed = true)
    private val remoteDataSource = SearchRemoteDataSourceImpl(mockSearchApi)
    private lateinit var moshiAdapter: JsonAdapter<SearchResponse>

    @Before
    fun setup() {
        /**
         * Setting up moshi adapter for converting string into json
         */
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        moshiAdapter =
            moshi.adapter(SearchResponse::class.java)
    }

    @After
    fun afterTests() {
        unmockkAll()
    }


    /**
     * Test a successful response for movies list data
     */
    @Test
    fun `get search list success response`() = runBlocking {
        val fileData = FileReaderUtils.readTestResourceFile("search.json")
        val searchListResponse = moshiAdapter.fromJson(fileData)!!

        coEvery { mockSearchApi.getSearchMovie(any(), any(), any()) } returns searchListResponse

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Success)
        assertEquals(searchListResponse, (result as RemoteResult.Success).value)

        val searchResult = result.value.searchResults!!

        assertEquals(4, searchResult.size)
        assertEquals("The Making of 'Jaws 2'", searchResult[0].title)
        assertEquals("2001", searchResult[0].year)
    }


    /**
     * Test a fail response for movies list with [SocketTimeoutException]
     */
    @Test
    fun `get search list response as SocketTimeOutException`() = runBlocking {

        val exception = SocketTimeoutException()
        coEvery { mockSearchApi.getSearchMovie(any(), any(), any()) } answers { throw exception }

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is SocketTimeoutException)
        assertTrue(errorResult.remoteError is TimeOut)
        assertEquals(
            R.string.timed_out,
            errorResult.remoteError.messageResId
        )
    }


    /**
     * Test a fail response for movies list with [UnknownHostException]
     */
    @Test
    fun `get search list response as UnknownHostException`() = runBlocking {

        val exception = UnknownHostException()
        coEvery { mockSearchApi.getSearchMovie(any(), any(), any()) } answers { throw exception }

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is UnknownHostException)
        assertTrue(errorResult.remoteError is NoInternet)
        assertEquals(
            R.string.no_internet,
            errorResult.remoteError.messageResId
        )
    }


    /**
     * Test a fail response for movies list with [HttpException]
     */
    @Test
    fun `get search list response as HttpException`() = runBlocking {

        val exception = HttpException(
            Response.error<Any>(
                404, "test".toResponseBody("text/plain".toMediaType())
            )
        )
        coEvery { mockSearchApi.getSearchMovie(any(), any(), any()) } answers { throw exception }

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is HttpException)
        assertTrue(errorResult.remoteError is HTTPError)
        assertEquals(404, (errorResult.remoteError as HTTPError).code)
        assertEquals(R.string.http_error, errorResult.remoteError.messageResId)
    }


    /**
     * Test a fail response for movies list with [IOException]
     */
    @Test
    fun `get search list response as IOException`() = runBlocking {

        val exception = IOException()
        coEvery { mockSearchApi.getSearchMovie(any(), any(), any())  } answers { throw exception }

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is IOException)
        assertTrue(errorResult.remoteError is IOError)
        assertEquals(R.string.io_error, errorResult.remoteError.messageResId)
    }


    /**
     * Test a fail response for movies list with unknown exception
     */
    @Test
    fun `get search list response as Unknown Exception`() = runBlocking {

        val exception = Exception()
        coEvery { mockSearchApi.getSearchMovie(any(), any(), any())  } answers { throw exception }

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is Exception)
        assertTrue(errorResult.remoteError is Other)
        assertEquals(R.string.unknown_error, errorResult.remoteError.messageResId)
    }


    /**
     * Test a successful response for empty movies list
     */
    @Test
    fun `get empty search list success response`() = runBlocking {
        val fileData = FileReaderUtils.readTestResourceFile("search_empty.json")
        val searchListResponse = moshiAdapter.fromJson(fileData)!!

        coEvery { mockSearchApi.getSearchMovie(any(), any(), any()) } returns searchListResponse

        val result = remoteDataSource.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Success)
        assertEquals(searchListResponse, (result as RemoteResult.Success).value)

        assertTrue(result.value.searchResults.isNullOrEmpty())
        assertTrue(result.value.response.lowercase() == "false")
        assertTrue(result.value.error.isNullOrEmpty().not())

    }
}