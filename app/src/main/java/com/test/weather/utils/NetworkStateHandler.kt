package com.test.weather.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log


/**
 *
 * Class to handle network state
 */
class NetworkStateHandler {

    /**
     * TAG : class name
     */
    private var TAG = this.javaClass.name

    /**
     * Network state Listener
     */
    private var networkStateListener: NetworkStateListener? = null

    /**
     * Network state boolean value
     */
    private var state: Boolean = false

    /**
     * Broadcast receiver for network state listener
     */
    private val networkStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceiveHandler(intent)
        }
    }

    /**
     * To handle upon network change received
     */
    private fun onReceiveHandler(intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.action!!, ignoreCase = true)) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (networkStateListener != null) {
                    state = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                        java.lang.Boolean.FALSE)
                    networkStateListener!!.onNetworkStateReceived(state)
                }
            }, 3000)
        }
    }


    /**
     * To register the network state listener
     */
    fun registerNetWorkStateBroadCast(context: Context) {
        Log.d(TAG, "registerNetWorkStateBroadCast")
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkStateReceiver, intentFilter)
    }

    /**
     * To un register network state listener
     */
    fun unRegisterNetWorkStateBroadCast(context: Context) {
        Log.d(TAG, "unRegisterNetWorkStateBroadCast")
        try {
            context.unregisterReceiver(networkStateReceiver)
        } catch (ex: Exception) {
            Log.d(TAG, "unRegisterNetWorkStateBroadCast" + ex.localizedMessage)
        }
    }

    /**
     * To  set network listener
     */
    fun setNetworkStateListener(networkStateListener: NetworkStateListener) {
        this.networkStateListener = networkStateListener
    }

    /**
     * network state listener call back
     */
    interface NetworkStateListener {
        fun onNetworkStateReceived(online: Boolean)
    }

}