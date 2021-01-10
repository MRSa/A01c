package jp.sfjp.gokigen.a01c.thetacamerawrapper.connection

import android.content.*
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.sfjp.gokigen.a01c.ICameraConnection
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdNotifier
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 */
class ThetaCameraConnection(private val context: AppCompatActivity, private val statusReceiver: ICameraStatusReceiver, private val sessionIdNotifier: IThetaSessionIdNotifier) : ICameraConnection
{
    private val cameraExecutor: Executor = Executors.newFixedThreadPool(1)
    private var connectionStatus: ICameraConnection.CameraConnectionStatus = ICameraConnection.CameraConnectionStatus.UNKNOWN
    private var isNetworkStatusWatching : Boolean = false
    private var connectionReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            onReceiveBroadcastOfConnection(context, intent)
        }
    }

    /**
     *
     */
    private fun onReceiveBroadcastOfConnection(context: Context, intent: Intent)
    {
        statusReceiver.onStatusNotify(context.getString(R.string.connect_check_wifi))
        Log.v(TAG, context.getString(R.string.connect_check_wifi))
        val action = intent.action
        if (action == null)
        {
            Log.v(TAG, "intent.getAction() : null")
            return
        }
        try
        {
            if (action == ConnectivityManager.CONNECTIVITY_ACTION)
            {
                Log.v(TAG, "onReceiveBroadcastOfConnection() : CONNECTIVITY_ACTION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info = wifiManager.connectionInfo
                if ((wifiManager.isWifiEnabled)&&(info != null))
                {
                    connectToCamera()
                }
                else
                {
                    if (info == null)
                    {
                        Log.v(TAG, "NETWORK INFO IS NULL.")
                    }
                    else
                    {
                        Log.v(TAG, "isWifiEnabled : " + wifiManager.isWifiEnabled + " NetworkId : " + info.networkId)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            Log.w(TAG, "onReceiveBroadcastOfConnection() EXCEPTION" + e.message)
            e.printStackTrace()
        }
    }

    /**
     *
     */
    override fun startWatchWifiStatus(context: Context)
    {
        Log.v(TAG, "startWatchWifiStatus()")
        statusReceiver.onStatusNotify("prepare")
        isNetworkStatusWatching = true
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectionReceiver, filter)
    }

    /**
     *
     */
    override fun stopWatchWifiStatus(context: Context)
    {
        Log.v(TAG, "stopWatchWifiStatus()")
        isNetworkStatusWatching = false
        context.unregisterReceiver(connectionReceiver)
        disconnect(false)
    }

    /**
     *
     */
    override fun isWatchWifiStatus(): Boolean
    {
        return (isNetworkStatusWatching)
    }

    /**
     *
     */
    override fun disconnect(powerOff: Boolean)
    {
        Log.v(TAG, "disconnect()")
        disconnectFromCamera(powerOff)
        connectionStatus = ICameraConnection.CameraConnectionStatus.DISCONNECTED
        statusReceiver.onCameraDisconnected()
    }

    /**
     *
     */
    override fun connect()
    {
        Log.v(TAG, "connect()")
        connectToCamera()
    }

    /**
     * カメラとの切断処理
     */
    private fun disconnectFromCamera(powerOff: Boolean)
    {
        Log.v(TAG, "disconnectFromCamera() : $powerOff")
        try
        {
            cameraExecutor.execute(ThetaCameraDisconnectSequence())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラとの接続処理
     */
    private fun connectToCamera()
    {
        Log.v(TAG, "connectToCamera()")
        connectionStatus = ICameraConnection.CameraConnectionStatus.CONNECTING
        try
        {
            cameraExecutor.execute(ThetaCameraConnectSequence(context, statusReceiver, sessionIdNotifier))
        }
        catch (e: Exception)
        {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.message)
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaCameraConnection::class.java.simpleName
    }
}