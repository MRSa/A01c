package jp.sfjp.gokigen.a01c

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class WifiConnection(private val context: AppCompatActivity, private val callback : IWifiConnection)
{
    private var isWatchingWifiStatus = false

    private val connectivityManager: ConnectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val connectionReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            onReceiveBroadcastOfConnection(context, intent)
        }
    }

    private val networkConnectionTimeoutHandler = object : Handler(Looper.getMainLooper())
    {
        override fun handleMessage(msg: Message)
        {
            when (msg.what) {
                MESSAGE_CONNECTIVITY_TIMEOUT -> {
                    Log.d(TAG, "Network connection timeout")
                    unregisterNetworkCallback()
                }
            }
        }
    }

    private val networkCallback = object : NetworkCallback()
    {
        override fun onAvailable(network: Network) {
            networkConnectionTimeoutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT)
            context.runOnUiThread {
                if (!connectivityManager.bindProcessToNetwork(network))
                {
                    Log.e(TAG, "ConnectivityManager.bindProcessToNetwork()  requires android.permission.INTERNET")
                }
                else
                {
                    Log.d(TAG, "Network available")
                }
            }
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities)
        {
            Log.d(TAG, "Network capabilities changed")
        }

        override fun onLost(network: Network)
        {
            Log.d(TAG, "Network lost")
        }
    }


    init
    {
        Log.v(TAG, "WifiConnection()")
        requestHighBandwidthNetwork()
/*
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null)
        {
            int bandwidth = connectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            if (bandwidth < MIN_BANDWIDTH_KBPS)
            {
                // Request a high-bandwidth network
            }
        }
        else
        {
            // You already are on a high-bandwidth network, so start your network request
        }
*/

/*
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null)
        {
            int bandwidth = connectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            if (bandwidth < MIN_BANDWIDTH_KBPS)
            {
                // Request a high-bandwidth network
            }
        }
        else
        {
            // You already are on a high-bandwidth network, so start your network request
        }
*/
     }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    private fun isNetworkHighBandwidth(): Boolean
    {
        var network: Network? = connectivityManager.boundNetworkForProcess
        network = network ?: connectivityManager.activeNetwork
        if (network == null)
        {
            return (false)
        }
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return (false)
        return (networkCapabilities.linkDownstreamBandwidthKbps >= MIN_NETWORK_BANDWIDTH_KBPS)
    }


    private fun unregisterNetworkCallback()
    {
        Log.d(TAG, "Unregistering network callback")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun releaseHighBandwidthNetwork()
    {
        connectivityManager.bindProcessToNetwork(null)
        unregisterNetworkCallback()
    }

    private fun addWifiNetwork()
    {
        // requires android.permission.CHANGE_WIFI_STATE
        context.applicationContext.startActivity(Intent(ACTION_ADD_NETWORK_SETTINGS))
    }

    /**
     * Wifiが使える状態だったら、カメラと接続して動作するよ
     *
     */
    @Suppress("DEPRECATION")
    private fun onReceiveBroadcastOfConnection(context: Context, intent: Intent)
    {
        Log.v(TAG, context.getString(R.string.connect_check_wifi))
        try
        {
            val action = intent.action
            if (action == ConnectivityManager.CONNECTIVITY_ACTION)
            {
                Log.v(TAG, " CONNECTIVITY_ACTION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info = wifiManager.connectionInfo
                if (wifiManager.isWifiEnabled)
                {
                    if ((info != null)&&(info.networkId != -1))
                    {
                        Log.v(TAG, " READY TO CONNECT CAMERA. " + info.networkId)
                    }
                    callback.onConnectedToWifi()
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun requestNetwork()
    {
        Log.v(TAG, " requestNetwork()")
        requestHighBandwidthNetwork()
    }

    /**
     * Wifi接続状態の監視
     * (接続の実処理は onReceiveBroadcastOfConnection() で実施)
     */
    fun startWatchWifiStatus()
    {
        Log.v(TAG, "startWatchWifiStatus()")
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectionReceiver, filter)
        isWatchingWifiStatus = true
    }

    /**
     * Wifi接続状態の監視終了
     */
    fun stopWatchWifiStatus()
    {
        Log.v(TAG, "stopWatchWifiStatus()")
        context.unregisterReceiver(connectionReceiver)
        isWatchingWifiStatus = false
    }

    /**
     * Wifi接続状態の監視処理を行っているかどうか
     *
     * @return true : 監視中 / false : 停止中
     */
    fun isWatchWifiStatus(): Boolean
    {
        return isWatchingWifiStatus
    }

    private fun requestHighBandwidthNetwork()
    {
        try
        {
            val bandwidth: Int = connectivityManager.activeNetwork?.let { activeNetwork ->
                connectivityManager.getNetworkCapabilities(activeNetwork)?.linkDownstreamBandwidthKbps
            } ?: -1
            Log.v(TAG, " requestHighBandwidthNetwork() (Bandwidth: $bandwidth)")
            when
            {
                bandwidth < 0 -> {
                    // No active network
                }
                bandwidth in (0 until MIN_NETWORK_BANDWIDTH_KBPS) -> {
                    // Request a high-bandwidth network
                }
                else -> {
                    // すでにつながっているので、何もしない。
                    Log.d(TAG, " already connected with high-bandwidth network")
                    return
                }
            }

            // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
            unregisterNetworkCallback()
            Log.d(TAG, "requestHighBandwidthNetwork(): Requesting high-bandwidth network")

            // Requesting an unmetered network may prevent you from connecting to the cellular
            // network on the user's watch or phone; however, unless you explicitly ask for permission
            // to a access the user's cellular network, you should request an unmetered network.
            val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build()

            // requires android.permission.CHANGE_NETWORK_STATE
            connectivityManager.requestNetwork(request, networkCallback)
            networkConnectionTimeoutHandler.sendMessageDelayed(networkConnectionTimeoutHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT), NETWORK_CONNECTIVITY_TIMEOUT_MS)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = WifiConnection::class.java.simpleName
        private const val MESSAGE_CONNECTIVITY_TIMEOUT = 1
        private const val MIN_NETWORK_BANDWIDTH_KBPS = 10000
        private const val ACTION_ADD_NETWORK_SETTINGS = "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"
        private val NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(2000)
    }
}
