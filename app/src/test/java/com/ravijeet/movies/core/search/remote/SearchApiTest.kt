package com.ravijeet.movies.core.search.remote

import com.ravijeet.movies.core.network.NetworkConstants
import com.ravijeet.movies.util.FileReaderUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

/**
 * Test for SearchApi with MockWebServer
 */
class SearchApiTest {

    private val mockWebServer = MockWebServer()
    private lateinit var searchApi: SearchApi

    @Before
    fun setup() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(NetworkConstants.RETROFIT_SLOW_TIMEOUT, TimeUnit.SECONDS)
            .build()
        searchApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SearchApi::class.java)

    }

    @After
    fun stop() {
        mockWebServer.shutdown()
    }

    /**
     * Test a successful response for movies list data
     */
    @Test
    fun `fetch movie list`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReaderUtils.readTestResourceFile("search.json"))
        mockWebServer.enqueue(mockResponse)

        val response = searchApi.getSearchMovie("jaws", 1, "")
        Assert.assertTrue(response.searchResults?.isNotEmpty() == true)

        val searchResult = response.searchResults!!

        assertEquals(4, searchResult.size)
        assertEquals("The Making of 'Jaws 2'", searchResult[0].title)
        assertEquals("2001", searchResult[0].year)

    }

    /**
     * Test a successful response for empty movies list data
     */
    @Test
    fun `fetch empty movie list`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReaderUtils.readTestResourceFile("search_empty.json"))
        mockWebServer.enqueue(mockResponse)

        val response = searchApi.getSearchMovie("adkshaksj", 1, "")
        assertTrue(response.searchResults.isNullOrEmpty())
        assertTrue(response.response.lowercase() == "false")
        assertTrue(response.error.isNullOrEmpty().not())
    }

}