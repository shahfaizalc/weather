package com.test.weather.model

/**
 * Data model class
 */
data class WeatherData(
    val currently: Currently,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)