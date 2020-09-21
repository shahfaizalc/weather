package com.test.weather.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.test.weather.R
import com.test.weather.contract.ErrorMsg
import com.test.weather.utils.NetworkStateHandler
import com.test.weather.contract.Main
import com.test.weather.contract.LocationApi
import com.test.weather.model.WeatherData
import com.test.weather.viewmodel.MainViewModel
import com.test.weather.utils.Constants
import com.test.weather.utils.showSnackBar
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Class for displaying Current location Weather data using darksky.net apis
 */
class MainActivity : AppCompatActivity(), Main.View, LocationApi, ErrorMsg,
    NetworkStateHandler.NetworkStateListener {

    /**
     * class name
     */
    private var TAG = this.javaClass.name

    /**
     * Main View Model
     */
    private lateinit var areaViewModel: MainViewModel

    /**
     * Fused Location Provider Client
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * Network State Handler
     */
    private lateinit var networkStateHandler: NetworkStateHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        areaViewModel = getViewModel()
        setupView()
    }

    /**
     * Setup View
     */
    private fun setupView() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        areaViewModel.init()
        networkStateHandler = NetworkStateHandler()
        allObservers()
    }

    /**
     * Get view model
     */
    private fun getViewModel() = ViewModelProviders.of(this).get(MainViewModel::class.java)

    /**
     * Get all observers
     */
    override fun allObservers() {
        Log.d(TAG, "allObservers:")
        areaViewModel.getLoadingState().observe(this, Observer { loadingState(it) })
        areaViewModel.getPermissions().observe(this, Observer { requestForPermission(it) })
        areaViewModel.getRequestLocation().observe(this, Observer { requestLocation() })
        areaViewModel.getOnDataFetchErrorMsg().observe(this, Observer { onError(it) })
        areaViewModel.getOnDataFetchErrorId().observe(this, Observer { onError(it) })
        areaViewModel.getOnDataFetchSuccess().observe(this, Observer { onWeatherDataFetched(it) })
    }

    /**
     * on request granted
     */
    override fun onRequestGranted() {
        Log.d(TAG, "onRequestGranted")
        when (getGooglePlayServiceStatus()) {
            ConnectionResult.SERVICE_MISSING,
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
            ConnectionResult.SERVICE_DISABLED -> showGooglePlayServiceError()
            else -> run {
                loadingState(View.VISIBLE)
                setLocationManager()
            }
        }
    }

    /**
     * Request for permissions
     */
    override fun requestForPermission(permissions: Array<String>) {
        when {
            hasLocationPermission() -> onRequestGranted()
            else -> requestPermissions(permissions, Constants.LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * on request permissions result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (hasLocationPermission()) {
            true -> onRequestGranted()
        }
    }

    /**
     * set location manager
     */
    @SuppressLint("MissingPermission")
    override fun setLocationManager() {
        when (hasLocationPermission()) {
            true -> {
                fusedLocationProviderClient.lastLocation?.addOnCompleteListener {
                    areaViewModel.onFetchLastKnownLocation(it)
                }
            }
        }
    }

    /**
     * to check location permission
     */
    override fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun getGooglePlayServiceStatus() =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

    override fun showGooglePlayServiceError() {
        GoogleApiAvailability.getInstance()
            .getErrorDialog(this, R.string.app_name, R.string.app_name)
    }

    /**
     * Request location information
     */
    @SuppressLint("MissingPermission")
    override fun requestLocation() {
        Log.d(TAG, "requestLocation")
        var callback: LocationCallback? = null
        callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                fusedLocationProviderClient.removeLocationUpdates(callback)
                areaViewModel.onLocationResult(locationResult)
            }
        }
        val request = LocationRequest().apply {
            interval = 500
            smallestDisplacement = 0f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            setExpirationDuration(1000 * 30)
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    /**
     * View progressbar
     */
    override fun loadingState(state: Int) {
        Log.d(TAG, "loadingState:change$state")
        loader.visibility = state
    }

    /**
     * on weather data fetched
     */
    override fun onWeatherDataFetched(weatherData: WeatherData) {
        Log.d(TAG, "onWeatherDataFetched:")
        updateWeather(weatherData)
    }

    /**
     * on Error msg
     */
    override fun onError(msg: String) {
        Log.d(TAG, "onError:$msg")
        showSnackBar(msg, findViewById(android.R.id.content))
    }

    /**
     * on Error msg
     */
    override fun onError(id: Int) {
        Log.d(TAG, "onError:$id")
        showSnackBar(getString(id), findViewById(android.R.id.content))
    }

    /**
     * update weather results
     */
    private fun updateWeather(weatherData: WeatherData) {
        Log.d(TAG, "updateWeather:")
        tv_temp.text = getString(R.string.temp, weatherData.currently.temperature)
        tv_humidity.text = getString(R.string.humidity, weatherData.currently.humidity)
        tv_wind_speed.text = getString(R.string.pressure, weatherData.currently.pressure)
        tv_visibility.text = getString(R.string.visibility, weatherData.currently.visibility)

    }

    /*
    * Register network state handler
    */
    override fun registerListeners() {
        Log.d(TAG, "registerListeners ")
        networkStateHandler.registerNetWorkStateBroadCast(this)
        networkStateHandler.setNetworkStateListener(this)
    }

    /*
     * To Unregister network state handler
     */
    override fun unRegisterListeners() {
        Log.d(TAG, "unRegisterListeners ")
        networkStateHandler.unRegisterNetWorkStateBroadCast(this)
    }

    /**
     * To handle on network state change received.
     * @param online: network state
     */
    override fun onNetworkStateReceived(online: Boolean) {
        Log.d(TAG, "onNetWorkStateReceived :$online")
        when (online) {
            false -> onError(R.string.connectionError)
        }
    }

    override fun onResume() {
        super.onResume()
        registerListeners()
    }

    override fun onStop() {
        unRegisterListeners()
        super.onStop()
    }
}