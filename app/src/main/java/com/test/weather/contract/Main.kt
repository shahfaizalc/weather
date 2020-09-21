package com.test.weather.contract

import android.location.Location
import com.test.weather.model.WeatherData
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task

/**
 * Interface to list down the functions used respective sections
 */
interface Main {

    // Interface used in View class
    interface View{
        fun loadingState(state:Int)
        fun onWeatherDataFetched(weatherData: WeatherData)
        fun allObservers();
        fun registerListeners();
        fun unRegisterListeners();
        fun onRequestGranted();
    }

    // Interface used in View Model class
    interface ViewModel : ErrorMsg {
        fun init()
        fun onLocationChanged(location: Location?)
        fun onFetchLastKnownLocation(task: Task<Location>)
        fun onLocationResult(locationResult: LocationResult?)
        fun onApiSuccess(weatherData: WeatherData)
    }
}