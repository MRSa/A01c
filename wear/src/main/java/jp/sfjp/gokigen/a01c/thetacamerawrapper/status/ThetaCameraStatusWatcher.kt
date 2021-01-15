package jp.sfjp.gokigen.a01c.thetacamerawrapper.status

import android.graphics.Color
import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraStatusWatcher
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaStatusHolder
import jp.sfjp.gokigen.a01c.thetacamerawrapper.status.ICameraStatus.*
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraStatusWatcher(private val sessionIdProvider: IThetaSessionIdProvider, private val statusHolder: IThetaStatusHolder, private val showInformation: IShowInformation) : ICameraStatusWatcher
{
    private val httpClient = SimpleHttpClient()
    private var whileFetching = false
    private var currentBatteryLevel : Double = 0.0
    private var currentExposureCompensation : Double = 0.0
    private var currentCaptureMode : String = ""
    private var currentExposureProgram : String = ""
    private var currentCaptureStatus : String = ""
    private var currentBatteryStatus : String = ""
    private var currentWhiteBalance : String = ""
    private var currentFilter : String = ""

    override fun startStatusWatch()
    {
        startStatusWatch1()
    }

    private fun startStatusWatch1()
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
                    val postData = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }}"

                    Log.v(TAG, " >>>>> START STATUS WATCH : $getOptionsUrl $postData")
                    while (whileFetching)
                    {
                        val response: String? = httpClient.httpPostWithHeader(getOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response.isNullOrEmpty()))
                        {
                            // ステータスデータ受信
                            checkStatus1(response)
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

    private fun checkStatus1(response: String)
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
                    if (currentExposureCompensation == 0.0)
                    {
                        // 補正なしの時には数値を表示しない
                        showInformation.setMessage(IShowInformation.AREA_6, Color.WHITE, "")
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_6, Color.WHITE, String.format("%1.1f", currentExposureCompensation))
                    }
                }
            }
            catch (e: Exception)
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
                    if (currentWhiteBalance == "auto")
                    {
                        showInformation.setMessage(IShowInformation.AREA_7, Color.WHITE, "")
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_7, Color.WHITE, currentWhiteBalance)
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val captureMode = stateObject.getString(THETA_CAPTURE_MODE)
                if (captureMode != currentCaptureMode)
                {
                    Log.v(TAG, " CAPTURE MODE : $currentCaptureMode -> $captureMode")
                    currentCaptureMode = captureMode
                    statusHolder.captureMode = captureMode
                    showInformation.setMessage(IShowInformation.AREA_1, Color.WHITE, captureMode)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val exposureProgram = stateObject.getString(THETA_EXPOSURE_PROGRAM)
                if (exposureProgram != currentExposureProgram)
                {
                    Log.v(TAG, " EXPOSURE PROGRAM : $currentExposureProgram -> $exposureProgram")
                    currentExposureProgram = exposureProgram
                    //showInformation.setMessage(IShowInformation.AREA_8, Color.WHITE, currentExposureProgram)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val filterValue = stateObject.getString(THETA_FILTER)
                if (filterValue != currentFilter)
                {
                    Log.v(TAG, " FILTER : $currentFilter -> $filterValue")
                    currentFilter = filterValue
                    if (currentFilter == "off")
                    {
                        showInformation.setMessage(IShowInformation.AREA_5, Color.WHITE, "")
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_5, Color.WHITE, currentFilter)
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        catch (ee: Exception)
        {
            ee.printStackTrace()
        }
    }


    private fun startStatusWatch0()
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
                            checkStatus0(response)
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

    private fun checkStatus0(response: String)
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
                    updateRemainBattery(currentBatteryLevel)
                }
            }
            catch (e: Exception)
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
            catch (e: Exception)
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
                        showInformation.setMessage(IShowInformation.AREA_5, Color.WHITE, captureStatus)
                    }
                    currentCaptureStatus = captureStatus
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        catch (ee: Exception)
        {
            ee.printStackTrace()
        }
    }

    private fun updateRemainBattery(percentageDouble: Double)
    {
        var color = Color.YELLOW
        if (percentageDouble < 0.5)
        {
            if (percentageDouble < 0.3)
            {
                color = Color.RED
            }
            try
            {
                val percentage = Math.ceil(percentageDouble * 100.0).toInt()
                showInformation.setMessage(IShowInformation.AREA_7, color, "Bat: $percentage%")
            }
            catch (ee: java.lang.Exception)
            {
                ee.printStackTrace()
            }
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
        private const val loopWaitMs : Long = 350
    }
}
