package jp.sfjp.gokigen.a01c

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

class WifiConnection(
    context: Context,
    private val callback: IWifiConnection
) : DefaultLifecycleObserver {

    private val applicationContext = context.applicationContext
    private val connectivityManager: ConnectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentNetwork: Network? = null
    @Volatile
    private var isCallbackRegistered = false

    private val timeoutRunnable = Runnable {
        Log.d(TAG, "Network connection timeout")
        unregisterNetworkCallback()
        callback.onNetworkConnectionTimeout()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            mainHandler.post {
                mainHandler.removeCallbacks(timeoutRunnable)
                Log.d(TAG, "Network available: $network")

                currentNetwork = network
                callback.onNetworkAvailable()

                checkWifiConnectionStatus()
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            Log.d(TAG, "Network capabilities changed: $networkCapabilities")
        }

        override fun onLost(network: Network) {
            mainHandler.post {
                Log.d(TAG, "Network lost: $network")
                if (currentNetwork == network) {
                    currentNetwork = null
                    callback.onNetworkLost()
                }
                unregisterNetworkCallback()
            }
        }

        override fun onUnavailable() {
            mainHandler.post {
                Log.d(TAG, "Network unavailable (onUnavailable)")
                mainHandler.removeCallbacks(timeoutRunnable)
                unregisterNetworkCallback()
                callback.onNetworkLost()
            }
        }
    }

    init {
        Log.v(TAG, "WifiConnection initialized")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart: Requesting network updates.")
        requestHighBandwidthNetwork()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop: Stopping network updates.")
        releaseNetwork()
    }

    fun startWatchWifiStatus() {
        Log.v(TAG, "startWatchWifiStatus()")
        requestHighBandwidthNetwork()
    }

    private fun requestHighBandwidthNetwork() {
        if (isCallbackRegistered) {
            Log.d(TAG, "Network callback is already registered.")
            return
        }

        try {
            Log.d(TAG, "Requesting high-bandwidth network...")

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            connectivityManager.requestNetwork(request, networkCallback)
            isCallbackRegistered = true

            mainHandler.postDelayed(timeoutRunnable, NETWORK_CONNECTIVITY_TIMEOUT_MS)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request network: ${e.message}", e)
            callback.onError("Failed to request network: ${e.message}")
        }
    }

    private fun unregisterNetworkCallback() {
        if (!isCallbackRegistered) return

        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d(TAG, "Network callback unregistered.")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to unregister network callback: ${e.message}")
        } finally {
            isCallbackRegistered = false
            mainHandler.removeCallbacks(timeoutRunnable)
        }
    }

    private fun releaseNetwork() {
        unregisterNetworkCallback()
        currentNetwork = null
    }

    private fun checkWifiConnectionStatus() {
        try {
            val network = currentNetwork ?: connectivityManager.activeNetwork
            if (network != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d(TAG, "Connected to a Wi-Fi network.")
                    callback.onConnectedToWifi()
                } else {
                    Log.d(TAG, "Network is available but not Wi-Fi or capabilities not available.")
                }
            } else {
                Log.d(TAG, "No active network available.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Wi-Fi status: ${e.message}", e)
        }
    }

    companion object {
        private val TAG = WifiConnection::class.java.simpleName
        private val NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(20)
    }
}
