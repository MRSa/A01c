package jp.sfjp.gokigen.a01c.thetacamerawrapper.status

import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraStatusUpdateNotify
import jp.sfjp.gokigen.a01c.ICameraStatusWatcher
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaStatusHolder
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.thetacamerawrapper.status.ICameraStatus.*
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraStatusWatcher(private val sessionIdProvider: IThetaSessionIdProvider, private val statusHolder : IThetaStatusHolder) : ICameraStatus, ICameraStatusWatcher
{
    private val httpClient = SimpleHttpClient()
    private var whileFetching = false
    private var currentBatteryLevel : Double = 0.0
    private var currentExposureCompensation : Double = 0.0
    private var currentCaptureMode : String = ""
    private var currentCaptureStatus : String = ""
    private var currentBatteryStatus : String = ""
    private var currentWhiteBalance : String = ""


    override fun getStatusList(key: String): List<String>
    {
        return (ArrayList())
    }

    override fun getStatus(key: String): String
    {
        return ("")
    }

    override fun startStatusWatch(notifier: ICameraStatusUpdateNotify)
    {
        startStatusWatch1(notifier)
    }

    private fun startStatusWatch1(notifier: ICameraStatusUpdateNotify)
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
                    val getOptionsUrl = "http://192.168.1.1/osc/commands/execute"
                    val postData = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"whiteBalance\"] }" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"whiteBalance\"] }}"

                    Log.v(TAG, " >>>>> START STATUS WATCH : $getOptionsUrl $postData")
                    while (whileFetching)
                    {
                        val response: String? = httpClient.httpPostWithHeader(getOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response.isNullOrEmpty()))
                        {
                            // ステータスデータ受信
                            checkStatus1(response, notifier)
                        }
                        try
                        {
                            Thread.sleep(loopWaitMs)
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

    private fun checkStatus1(response : String, notifier: ICameraStatusUpdateNotify)
    {
        try
        {
            //Log.v(TAG, " STATUS : $response")
            val stateObject = JSONObject(response).getJSONObject("results").getJSONObject("options")
            try
            {
                val exposureCompensation = stateObject.getDouble(THETA_EXPOSURE_COMPENSATION)
                if (exposureCompensation != currentExposureCompensation)
                {
                    Log.v(TAG, " EXPREV : $currentExposureCompensation => $exposureCompensation")
                    currentExposureCompensation = exposureCompensation
                    notifier.updatedExposureCompensation(String.format("%1.1f",currentExposureCompensation))
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val whiteBalance = stateObject.getString(THETA_WHITE_BALANCE)
                if (whiteBalance != currentWhiteBalance)
                {
                    Log.v(TAG, " WB : $currentWhiteBalance => $whiteBalance")
                    currentWhiteBalance = whiteBalance
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val captureMode = stateObject.getString(THETA_CAPTURE_MODE)
                if (captureMode != currentCaptureMode)
                {
                    Log.v(TAG, " CAPTURE MODE : $currentCaptureMode -> $captureMode")
                    notifier.updateCaptureMode(captureMode)
                    currentCaptureMode = captureMode
                    statusHolder.setCaptureMode(captureMode)
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


    private fun startStatusWatch0(notifier: ICameraStatusUpdateNotify)
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
                            checkStatus0(response, notifier)
                        }
                        try
                        {
                            Thread.sleep(loopWaitMs)
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

    private fun checkStatus0(response : String, notifier: ICameraStatusUpdateNotify)
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

    override fun prepareStatusWatch()
    {
        //
    }

    companion object
    {
        private val TAG = ThetaCameraStatusWatcher::class.java.simpleName
        private const val timeoutMs = 1500
        private const val loopWaitMs : Long = 660
    }
}
