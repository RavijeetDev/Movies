package com.ravijeet.movies.core.network

import com.ravijeet.movies.core.search.remote.SearchApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Class to provide instance of Retrofit and API interfaces
 */
object NetworkService {

    private lateinit var retrofit: Retrofit

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun getNetworkService(): Retrofit {
        synchronized(this) {
            if (!this::retrofit.isInitialized) {
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(NetworkConstants.RETROFIT_SLOW_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(NetworkConstants.RETROFIT_SLOW_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(NetworkConstants.RETROFIT_SLOW_TIMEOUT, TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConstants.BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .client(okHttpClient)
                    .build()
            }
        }
        return retrofit
    }


    /**
     * creating instance of [SearchApi]
     */
    fun getSearchApiService(): SearchApi {
        return getNetworkService().create(SearchApi::class.java)
    }
}