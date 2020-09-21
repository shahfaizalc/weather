package com.test.weather.contract

/**
 * interface for the location APIs
 */
interface LocationApi {
    fun requestForPermission(permissions: Array<String>)
    fun setLocationManager()
    fun hasLocationPermission(): Boolean
    fun getGooglePlayServiceStatus(): Int
    fun showGooglePlayServiceError()
    fun requestLocation()
}