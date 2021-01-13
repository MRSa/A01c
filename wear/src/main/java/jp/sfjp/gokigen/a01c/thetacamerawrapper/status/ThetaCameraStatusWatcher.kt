package jp.sfjp.gokigen.a01c.thetacamerawrapper.status

import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraStatusUpdateNotify
import jp.sfjp.gokigen.a01c.ICameraStatusWatcher
import jp.sfjp.gokigen.a01c.thetacamerawrapper.status.ICameraStatus.*
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraStatusWatcher : ICameraStatus, ICameraStatusWatcher
{
    private val httpClient = SimpleHttpClient()
    private var whileFetching = false
    private var currentBatteryLevel : Double = 0.0
    private var currentCaptureStatus : String = ""
    private var currentBatteryStatus : String = ""


    override fun getStatusList(key: String): List<String>
    {
        return (ArrayList<String>())
    }

    override fun getStatus(key: String): String
    {
        return ("")
    }

    override fun startStatusWatch(notifier: ICameraStatusUpdateNotify)
    {
        if (whileFetching)
        {
            Log.v(TAG, "startStatusWatch() already starting.")
            return
        }
        whileFetching = true
        try
        {
            val thread = Thread {
                try
                {
                    val getStateUrl = "http://192.168.1.1/osc/state"
                    Log.v(TAG, " >>>>> START STATUS WATCH : $getStateUrl")
                    while (whileFetching)
                    {
                        val response: String? = httpClient.httpPostWithHeader(getStateUrl, "", null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response.isNullOrEmpty()))
                        {
                            // ステータスデータ受信
                            checkStatus(response, notifier)
                        }
                        try
                        {
                            Thread.sleep(300)
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkStatus(response : String, notifier: ICameraStatusUpdateNotify)
    {
        try
        {
            //Log.v(TAG, " STATUS : $response")
            val stateObject = JSONObject(response).getJSONObject("state")
            try
            {
                val batteryLevel = stateObject.getDouble(THETA_BATTERY_LEVEL)
                if (batteryLevel != currentBatteryLevel)
                {
                    Log.v(TAG, " BATTERY : $currentBatteryLevel => $batteryLevel")
                    currentBatteryLevel = batteryLevel
                    notifier.updateRemainBattery(currentBatteryLevel)
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val batteryStatus = stateObject.getString(THETA_CAPTURE_STATUS)
                if (batteryStatus != currentBatteryStatus)
                {
                    Log.v(TAG, " BATTERY STATUS : $currentBatteryStatus => $batteryStatus")
                    currentBatteryStatus = batteryStatus
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val captureStatus = stateObject.getString(THETA_CAPTURE_STATUS)
                if (captureStatus != currentCaptureStatus)
                {
                    Log.v(TAG, " CAPTURE STATUS : $currentCaptureStatus -> $captureStatus")
                    if (captureStatus != "idle")
                    {
                        notifier.updateCameraStatus(captureStatus)
                    }
                    currentCaptureStatus = captureStatus
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
        catch (ee : Exception)
        {
            ee.printStackTrace()
        }
    }

    override fun stopStatusWatch()
    {
        whileFetching = false
    }

    companion object
    {
        private val TAG = ThetaCameraStatusWatcher::class.java.simpleName
        private const val timeoutMs = 1500
    }

}