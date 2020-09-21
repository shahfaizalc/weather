package com.test.weather.repository

import com.test.weather.service.ServiceApi

/**
 * Repository interface for retrofit service api
 */
interface MainRepository {
    fun getServiceApi(): ServiceApi
}