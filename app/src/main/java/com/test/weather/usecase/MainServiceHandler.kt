package com.test.weather.usecase

import android.util.Log
import com.test.weather.model.WeatherData
import com.test.weather.repository.MainRepositoryImpl
import com.test.weather.viewmodel.MainViewModel
import kotlinx.coroutines.*
import retrofit2.await

/**
 *Main Service Handler
 */
class MainServiceHandler : MainUseCase {

    /**
     * class name
     */
    private var TAG = this.javaClass.name

    /**
     * Repository implementation
     */
    private val repository = MainRepositoryImpl()

    /**
     * Function to request data from the server
     * @param locationPath location url path
     */
    override fun getWeatherData(
        locationPath: String,
        mainViewModel: MainViewModel
    ) {
        Log.d(TAG, "getWeatherData")
        runBlocking {
            // handler manages the coroutine request and response.
            val handler = coroutineExceptionHandler(mainViewModel)
            GlobalScope.launch(handler) {
                // initiate and make retrofit request call
                val service = repository.getServiceApi()
                val repositories = withContext(Dispatchers.Default) {
                    service.getWeatherData(locationPath).await()
                }
                // receive and process the success response
                withContext(Dispatchers.Default) {
                    coroutineSuccessHandler(
                        repositories,
                        mainViewModel
                    )
                }
            }
        }
    }

    /**
     * Method to handle the server response. Iterate the feed in to model class to load in to recyclerview
     * @param response: WeatherData
     */
    private fun coroutineSuccessHandler(
        response: WeatherData,
        mainViewModel: MainViewModel
    ) {
        Log.d(TAG, "coroutineHandler:success $response")
        mainViewModel.onApiSuccess(response)
    }

    /**
     * Method to handle the request exception and to notify the failure to user.
     */
    private fun coroutineExceptionHandler(
        mainViewModel: MainViewModel
    ) = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "coroutineHandler:exception " + exception.localizedMessage);
        mainViewModel.onError(exception.localizedMessage!!)
    }
}