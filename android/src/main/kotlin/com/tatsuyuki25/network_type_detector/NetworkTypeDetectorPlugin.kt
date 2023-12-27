package com.tatsuyuki25.network_type_detector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** NetworkTypeDetectorPlugin */
class NetworkTypeDetectorPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {

    private lateinit var channel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var telephonyManager: TelephonyManager
    private var broadcastReceiver: NetworkBroadcastReceiver? = null
    private var networkCallback: NetworkCallback? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "network_type_detector")
        channel.setMethodCallHandler(this)

        eventChannel =
            EventChannel(flutterPluginBinding.binaryMessenger, "network_type_detector_status")
        eventChannel.setStreamHandler(this)

        context = flutterPluginBinding.applicationContext
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "networkStatus") {
            result.success(getNetworkState(connectivityManager, telephonyManager))
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            networkCallback = NetworkCallback(events, connectivityManager, telephonyManager)
            connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
        } else {
            if (broadcastReceiver == null) {
                broadcastReceiver =
                    NetworkBroadcastReceiver(events, connectivityManager, telephonyManager)
            }
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(broadcastReceiver, filter)
        }
    }

    override fun onCancel(arguments: Any?) {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver)
            broadcastReceiver = null
        } else if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
            networkCallback = null
        }
    }
}

private class NetworkBroadcastReceiver(
    val events: EventChannel.EventSink?,
    val connectivityManager: ConnectivityManager,
    val telephonyManager: TelephonyManager
) : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(p0: Context?, p1: Intent?) {
        events?.success(getNetworkState(connectivityManager, telephonyManager))
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private class NetworkCallback(
    val events: EventChannel.EventSink?,
    val connectivityManager: ConnectivityManager,
    val telephonyManager: TelephonyManager
) : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: android.net.Network) {
        super.onAvailable(network)
        CoroutineScope(Dispatchers.Main).launch {
            events?.success(getNetworkState(connectivityManager, telephonyManager))
        }
    }

    override fun onLost(network: android.net.Network) {
        super.onLost(network)
        CoroutineScope(Dispatchers.Main).launch {
            events?.success(NetworkState.UNREACHABLE.toString())
        }
    }

}

// Get network status
private fun getNetworkState(
    connectivityManager: ConnectivityManager,
    telephonyManager: TelephonyManager
): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkState.UNREACHABLE.toString()
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET
            )
        ) {
            return NetworkState.WIFI.toString()
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return getMobileNetworkType(connectivityManager, telephonyManager)
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            return NetworkState.UNREACHABLE.toString()
        }
        return when (networkInfo.type) {
            ConnectivityManager.TYPE_ETHERNET, ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX -> {
                NetworkState.WIFI.toString()
            }

            ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_DUN, ConnectivityManager.TYPE_MOBILE_HIPRI -> {
                getMobileNetworkType(connectivityManager, telephonyManager)
            }

            else -> NetworkState.UNREACHABLE.toString()
        }
    }
    return NetworkState.UNREACHABLE.toString()
}

private fun getMobileNetworkType(
    connectivityManager: ConnectivityManager,
    telephonyManager: TelephonyManager
): String {
    val subType: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        telephonyManager.dataNetworkType
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
            ?: return NetworkState.MOBILE_OTHER.toString()
        networkInfo.subtype
    }
    val mobile2GTypes = arrayOf(
        TelephonyManager.NETWORK_TYPE_1xRTT,
        TelephonyManager.NETWORK_TYPE_EDGE,
        TelephonyManager.NETWORK_TYPE_GPRS,
        TelephonyManager.NETWORK_TYPE_CDMA,
        TelephonyManager.NETWORK_TYPE_IDEN
    )
    if (subType in mobile2GTypes) {
        return NetworkState.MOBILE_2G.toString()
    }
    val mobile3GTypes = arrayOf(
        TelephonyManager.NETWORK_TYPE_UMTS,
        TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_EVDO_A,
        TelephonyManager.NETWORK_TYPE_HSDPA,
        TelephonyManager.NETWORK_TYPE_HSUPA,
        TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_EVDO_B,
        TelephonyManager.NETWORK_TYPE_EHRPD,
        TelephonyManager.NETWORK_TYPE_HSPAP
    )
    if (subType in mobile3GTypes) {
        return NetworkState.MOBILE_3G.toString()
    }
    if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
        return NetworkState.MOBILE_4G.toString()
    }
    if (subType == TelephonyManager.NETWORK_TYPE_NR) {
        return NetworkState.MOBILE_5G.toString()
    }
    return NetworkState.MOBILE_OTHER.toString()
}

private enum class NetworkState {
    UNREACHABLE,
    MOBILE_2G,
    MOBILE_3G,
    WIFI,
    MOBILE_4G,
    MOBILE_5G,
    MOBILE_OTHER
}