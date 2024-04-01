package com.ravijeet.movies.core.search.domain

import com.ravijeet.movies.R
import com.ravijeet.movies.core.network.ApiError
import com.ravijeet.movies.core.network.HTTPError
import com.ravijeet.movies.core.network.IOError
import com.ravijeet.movies.core.network.NoInternet
import com.ravijeet.movies.core.network.Other
import com.ravijeet.movies.core.network.RemoteResult
import com.ravijeet.movies.core.network.TimeOut
import com.ravijeet.movies.core.search.asDomainModel
import com.ravijeet.movies.core.search.remote.SearchRemoteDataSource
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

/**
 * Tests for [SearchRepository]
 */
class SearchRepositoryTest {

    private val mockSearchRemoteDataSource = mockk<SearchRemoteDataSource>()
    private val searchRepository = SearchRepositoryImpl(mockSearchRemoteDataSource)
    private lateinit var moshiAdapter: JsonAdapter<SearchResponse>

    @Before
    fun setup() {
        /**
         * setting up moshi adapter for converting string into json
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
     * Test a successful response for movies list with RemoteResult
     */
    @Test
    fun `get search list success response`() = runBlocking {
        val fileData = FileReaderUtils.readTestResourceFile("search.json")
        val searchResponse = moshiAdapter.fromJson(fileData)!!

        val searchDataDomain = searchResponse.asDomainModel()
        coEvery {
            mockSearchRemoteDataSource.getSearchMovie(
                any(),
                any()
            )
        } returns RemoteResult.Success(
            searchResponse
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Success)
        assertEquals(searchDataDomain, (result as RemoteResult.Success).value)

        val searchData = result.value

        assertEquals(4, searchData.searchResults.size)
        assertEquals("The Making of 'Jaws 2'", searchData.searchResults[0].title)
        assertEquals("2001", searchData.searchResults[0].year)
        
    }


    /**
     * Test a fail response for movies list with [SocketTimeoutException]
     */
    @Test
    fun `get search list response as SocketTimeOutException`() = runBlocking {

        val remoteError = TimeOut(SocketTimeoutException())
        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Error(
            remoteError
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

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

        val remoteError = NoInternet(UnknownHostException())
        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Error(
            remoteError
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

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

        val remoteError = HTTPError(
            HttpException(
                Response.error<Any>(
                    404, "test".toResponseBody("text/plain".toMediaType())
                )
            ), code = 404, apiErrorMessage = ""
        )
        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Error(
            remoteError
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is HttpException)
        assertTrue(errorResult.remoteError is HTTPError)
        assertEquals(404, (errorResult.remoteError as HTTPError).code)
        assertEquals(
            R.string.http_error,
            errorResult.remoteError.messageResId
        )
    }


    /**
     * Test a fail response for movies list with [IOException]
     */
    @Test
    fun `get search list response as IOException`() = runBlocking {

        val remoteError = IOError(IOException())
        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Error(
            remoteError
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

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

        val remoteError = Other(Exception())
        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Error(
            remoteError
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)
        val errorResult = result as RemoteResult.Error

        assertTrue(errorResult.remoteError.throwable is Exception)
        assertTrue(errorResult.remoteError is Other)
        assertEquals(
            R.string.unknown_error,
            errorResult.remoteError.messageResId
        )
    }


    /**
     * Test a successful response for empty movies list
     */
    @Test
    fun `get empty search list success response`() = runBlocking {
        val fileData = FileReaderUtils.readTestResourceFile("search_empty.json")
        val searchDataResponse = moshiAdapter.fromJson(fileData)!!
        val searchDataDomain = searchDataResponse.asDomainModel()

        coEvery { mockSearchRemoteDataSource.getSearchMovie(any(), any()) } returns RemoteResult.Success(
            searchDataResponse
        )

        val result = searchRepository.getSearchMovie("jaws", 1)

        assertTrue(result is RemoteResult.Error)

        val errorResult = result as RemoteResult.Error
        assertTrue(errorResult.remoteError is ApiError)

    }
}