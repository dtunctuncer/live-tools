package com.raqun.liveconnection

import android.arch.lifecycle.LiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.support.v4.content.LocalBroadcastManager
import com.raqun.live_tools_core.LiveResult
import com.raqun.live_tools_core.PermissionUtil
import com.raqun.live_tools_core.LiveResult.LiveValue
import com.raqun.live_tools_core.LiveResult.PermissionRequired

/**
 * Created by tyln on 22.02.2018.
 */
class ConnectionLiveData(private val context: Context) : LiveData<LiveResult>() {

    private val networkChangeReceiver: NetworkChangeReceiver

    init {
        networkChangeReceiver = NetworkChangeReceiver()
    }

    override fun onActive() {
        super.onActive()
        LocalBroadcastManager.getInstance(context).registerReceiver(networkChangeReceiver,
                IntentFilter().apply {
                    addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                    addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                })
    }

    override fun onInactive() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(networkChangeReceiver)
        super.onInactive()
    }

    private fun getConnectionStatus(): LiveResult {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (!PermissionUtil.isConnectionPermissionsGranted(context)) {
            return PermissionRequired(PermissionUtil.connectionPermissions)
        }

        val networkInfo: NetworkInfo? = cm.activeNetworkInfo as NetworkInfo
        return when (networkInfo?.type) {
            ConnectivityManager.TYPE_WIFI -> LiveValue(ConnectionType.WIFI)
            ConnectivityManager.TYPE_MOBILE -> LiveValue(ConnectionType.MOBILE)
            else -> LiveValue(ConnectionType.UNAVAILABLE)
        }
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            postValue(getConnectionStatus())
        }
    }
}