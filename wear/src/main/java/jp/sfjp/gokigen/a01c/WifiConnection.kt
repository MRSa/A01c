package jp.sfjp.gokigen.a01c

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

class WifiConnection(
    private val context: Context,
    private val callback: IWifiConnection
) : DefaultLifecycleObserver
{
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var currentNetwork: Network? = null

    private val networkConnectionTimeoutHandler = object : Handler(Looper.getMainLooper())
    {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_CONNECTIVITY_TIMEOUT -> {
                    Log.d(TAG, "Network connection timeout")
                    unregisterNetworkCallback()
                    callback.onNetworkConnectionTimeout()
                }
            }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            networkConnectionTimeoutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT)
            Log.d(TAG, "Network available: $network")

            // 特定のネットワークにプロセスをバインドする必要がある場合のみ使用
            // 通常のインターネット接続では不要な場合が多い
            // if (!connectivityManager.bindProcessToNetwork(network)) {
            //     Log.e(TAG, "ConnectivityManager.bindProcessToNetwork() failed.")
            //     callback.onError("Failed to bind process to network.")
            //     return
            // }

            currentNetwork = network
            callback.onNetworkAvailable()

            // Wi-Fi接続確認ロジック
            checkWifiConnectionStatus()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            Log.d(TAG, "Network capabilities changed: $networkCapabilities")
            // 必要に応じて、ネットワークの能力変化に対応するロジックを追加
            // 例: 帯域幅の変化を監視し、特定の条件を満たした場合に何かアクションを起こす
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Network lost: $network")
            if (currentNetwork == network) {
                currentNetwork = null
                callback.onNetworkLost()
            }
            unregisterNetworkCallback() // ネットワークが失われたらコールバックを解除
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.d(TAG, "Network unavailable (onUnavailable)")
            networkConnectionTimeoutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT)
            unregisterNetworkCallback()
            callback.onNetworkLost()
        }
    }

    init
    {
        Log.v(TAG, "WifiConnection initialized")
    }

    // LifecycleObserver のメソッドをオーバーライド
    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "onStart: Requesting network updates.")
        requestHighBandwidthNetwork()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop: Stopping network updates.")
        releaseNetwork()
    }

    fun startWatchWifiStatus()
    {
        Log.v(TAG, "startWatchWifiStatus()")
        requestHighBandwidthNetwork()
    }

    // ----- 高帯域幅ネットワークの要求
    private fun requestHighBandwidthNetwork()
    {
        try
        {
            // 既存のネットワークリクエストがあれば解除
            unregisterNetworkCallback()

            Log.d(TAG, "Requesting high-bandwidth network...")

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                // .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) // 必要に応じて追加
                .build()

            connectivityManager.requestNetwork(request, networkCallback)
            networkConnectionTimeoutHandler.sendMessageDelayed(
                networkConnectionTimeoutHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
                NETWORK_CONNECTIVITY_TIMEOUT_MS
            )
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Failed to request network: ${e.message}", e)
            callback.onError("Failed to request network: ${e.message}")
        }
    }

    //  ネットワークコールバックの登録解除とリソースの開放
    private fun unregisterNetworkCallback()
    {
        try
        {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            networkConnectionTimeoutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT)
            Log.d(TAG, "Network callback unregistered.")
        }
        catch (e: Exception)
        {
            // コールバックが登録されていない場合の例外をキャッチ
            Log.w(TAG, "Network callback was not registered or already unregistered.")
            e.printStackTrace()
        }
    }

    //  ネットワークリソースの解放
    private fun releaseNetwork()
    {
        unregisterNetworkCallback()
        currentNetwork = null
    }

    //  現在のWi-Fi接続状態を確認し、コールバックを呼び出す
    private fun checkWifiConnectionStatus()
    {
        try
        {
            val network = currentNetwork ?: connectivityManager.activeNetwork // activeNetworkもフォールバックとして確認
            if (network != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                {
                    Log.d(TAG, "Connected to a Wi-Fi network.")
                    callback.onConnectedToWifi()
                }
                else
                {
                    Log.d(TAG, "Network is available but not Wi-Fi or capabilities not available.")
                }
            }
            else
            {
                Log.d(TAG, "No active network available.")
            }
        }
        catch (e: Exception)
        {
            Log.v(TAG, "checkWifiConnectionStatus()")
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = WifiConnection::class.java.simpleName
        private const val MESSAGE_CONNECTIVITY_TIMEOUT = 1
        // private const val MIN_NETWORK_BANDWIDTH_KBPS = 10000
        private val NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(20) // 20秒に修正
    }
}
