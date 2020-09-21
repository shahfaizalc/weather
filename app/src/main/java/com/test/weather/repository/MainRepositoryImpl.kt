package com.test.weather.repository

import com.test.weather.service.NetworkService

/**
 * Repository implementation for retrofit service api
 */
class MainRepositoryImpl : MainRepository {
    override fun getServiceApi() = NetworkService.getClient()
}