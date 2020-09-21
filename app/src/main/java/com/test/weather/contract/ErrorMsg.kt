package com.test.weather.contract

/**
 * interface to display error responses
 */
interface ErrorMsg {
    fun onError(msg: String)
    fun onError(id: Int)
}