package com.test.weather.service

import com.test.weather.utils.Constants.BASE_URL
import com.test.weather.utils.Constants.TIME_OUT_SECONDS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network Service class
 */
object NetworkService {

    /**
     * Network request retrofit api
     */
    fun getClient(): ServiceApi {
        val retrofit = getRetrofitBuilder(BASE_URL)
        return retrofit.create(ServiceApi::class.java)
    }

    /**
     * Get Retrofit builder
     */
    private fun getRetrofitBuilder(url: String) = Retrofit.Builder()
        .baseUrl(url)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * OkHttp Client builder
     */
    private fun getOkHttpClient() = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
        .build()

}