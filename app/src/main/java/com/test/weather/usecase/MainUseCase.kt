package com.test.weather.usecase

import com.test.weather.viewmodel.MainViewModel


/**
 * interface Main use case
 */
interface MainUseCase {
    fun getWeatherData(
        location: String,
        mainViewModel: MainViewModel
    )
}