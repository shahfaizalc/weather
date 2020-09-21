package com.test.weather.viewmodel

import android.Manifest
import android.location.Location
import android.util.Log
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.weather.contract.Main
import com.test.weather.model.WeatherData
import com.test.weather.usecase.MainServiceHandler
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task

/**
 * Main view model
 */
class MainViewModel : Main.ViewModel, ViewModel() {

    /**
     * class name
     */
    private var TAG = this.javaClass.name

    /**
     * progress loading state
     */
    @VisibleForTesting
    private val loadingState = MutableLiveData<Int>()

    /**
     * Data fetch error
     */
    private val onDataFetchErrorMsg = MutableLiveData<String>()

    /**
     * Data fetch error
     */
    private val onDataFetchErrorId = MutableLiveData<Int>()

    /**
     * data fetch success
     */
    private val onDataFetchSuccess = MutableLiveData<WeatherData>()

    /**
     * request location
     */
    private val requestLocation = MutableLiveData<Boolean>()

    /**
     * array of permissions
     */
    private val permissionArray = MutableLiveData<Array<String>>()

    /**
     * Main Service Handler
     */
    private val mainUseCase = MainServiceHandler()

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * set up view model primaries
     */
    override fun init() {
        permissionArray.value = permissions
    }

    /**
     * get permissions
     */
    fun getPermissions() = permissionArray

    /**
     * get progress loading state
     */
    fun getLoadingState() = loadingState

    /**
     * get on data fetch error
     */
    fun getOnDataFetchErrorMsg() = onDataFetchErrorMsg

    /**
     * get on data fetch error id
     */
    fun getOnDataFetchErrorId() = onDataFetchErrorId

    /**
     * get on data fetch success
     */
    fun getOnDataFetchSuccess() = onDataFetchSuccess

    /**
     * get request location
     */
    fun getRequestLocation() = requestLocation

    /**
     * do on location changed
     */
    override fun onLocationChanged(location: Location?) {
        location?.let {
            val locationPath = "${it.latitude}, ${it.latitude}"
            mainUseCase.getWeatherData(locationPath,this)
        }
    }

    /***
     * get last known location information
     */
    override fun onFetchLastKnownLocation(task: Task<Location>) {
        Log.d(TAG,"task "+task.isSuccessful)
        if (task.isSuccessful) {
            val location = task.result
            when (location != null) {
                true -> onLocationChanged(location)
                else -> requestLocation.value = true
            }
        } else {
            requestLocation.value = true
        }
    }

    /**
     * on location result
     */
    override fun onLocationResult(locationResult: LocationResult?) {
        if (null != locationResult && locationResult.locations.isNotEmpty()) {
            Log.d(TAG, "onLocationResult $locationResult")
            onLocationChanged(locationResult.locations[0])
        }
    }

    override fun onApiSuccess(weatherData: WeatherData) {
        loadingState.postValue(View.GONE)
        onDataFetchSuccess.postValue(weatherData)
    }

    /**
     * return error string
     */
    override fun onError(msg: String) {
        loadingState.postValue(View.GONE)
        onDataFetchErrorMsg.postValue(msg)
    }

    /**
     * return error string id
     */
    override fun onError(id: Int) {
        loadingState.postValue( View.GONE)
        onDataFetchErrorId.postValue(id)
    }
}