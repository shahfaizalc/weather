package com.test.weather.service

import com.test.weather.model.WeatherData
import com.test.weather.utils.Constants.URL_PATH
import com.test.weather.utils.Constants.LATLONG
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Network service api
 */

interface ServiceApi {

    @GET(URL_PATH)
    fun getWeatherData(@Path(LATLONG) location: String): Call<WeatherData>

}