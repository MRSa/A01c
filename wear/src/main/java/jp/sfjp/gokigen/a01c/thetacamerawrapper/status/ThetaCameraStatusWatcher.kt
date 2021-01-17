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
import kotlin.math.ceil

class ThetaCameraStatusWatcher(private val sessionIdProvider: IThetaSessionIdProvider, private val statusHolder: IThetaStatusHolder, private val showInformation: IShowInformation) : ICameraStatusWatcher
{
    private val httpClient = SimpleHttpClient()
    private var whileFetching = false
    private var currentIsoSensitivity : Int = 0
    private var currentBatteryLevel : Double = 0.0
    private var currentAperture : Double = 0.0
    private var currentShutterSpeed : Double = 0.0
    private var currentExposureCompensation : Double = 0.0
    private var currentCaptureMode : String = ""
    private var currentExposureProgram : String = ""
    private var currentCaptureStatus : String = ""
    private var currentWhiteBalance : String = ""
    private var currentFilter : String = ""


    override fun startStatusWatch()
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
                    val getStateUrl = "http://192.168.1.1/osc/state"
                    val postData = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }}"
                    Log.v(TAG, " >>>>> START STATUS WATCH : $getOptionsUrl $postData")
                    while (whileFetching)
                    {
                        val response: String? = httpClient.httpPostWithHeader(getOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response.isNullOrEmpty()))
                        {
                            // 設定データ受信、解析する
                            checkStatus1(response)
                        }
                        val response2: String? = httpClient.httpPostWithHeader(getStateUrl, "", null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response2.isNullOrEmpty()))
                        {
                            // ステータスデータ受信、解析する
                            checkStatus0(response2)
                        }

                        try
                        {
                            // ちょっと休む
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
                    Log.v(TAG, " XV : $currentExposureCompensation => $exposureCompensation")
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
                    Log.v(TAG, " CapMode : $currentCaptureMode -> $captureMode")
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
                    Log.v(TAG, " ExpPrg : $currentExposureProgram -> $exposureProgram")
                    currentExposureProgram = exposureProgram

                    var mode = ""
                    when (currentExposureProgram) {
                        "1" -> mode = "Manual"
                        "2" -> mode = "Normal"
                        "3" -> mode = "Aperture"
                        "4" -> mode = "Shutter"
                        "9" -> mode = "ISO"
                    }
                    showInformation.setMessage(IShowInformation.AREA_2, Color.WHITE, mode)
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

            try
            {
                val isoSensitivity = stateObject.getInt(THETA_ISO_SENSITIVITY)
                if (isoSensitivity != currentIsoSensitivity)
                {
                    Log.v(TAG, " ISO : $currentIsoSensitivity -> $isoSensitivity")
                    currentIsoSensitivity = isoSensitivity
                    if (currentIsoSensitivity == 0)
                    {
                        showInformation.setMessage(IShowInformation.AREA_4, Color.WHITE, "AUTO:iso")
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_4, Color.WHITE, "${currentIsoSensitivity}iso")
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val aperture = stateObject.getDouble(THETA_APERTURE)
                if (aperture != currentAperture)
                {
                    Log.v(TAG, " A : $currentAperture -> $aperture")
                    currentAperture = aperture
                    if ((currentExposureProgram == "1")||(currentExposureProgram == "3"))
                    {
                        if (currentAperture == 0.0)
                        {
                            showInformation.setMessage(IShowInformation.AREA_8, Color.WHITE, "auto")
                        }
                        else
                        {
                            showInformation.setMessage(IShowInformation.AREA_8, Color.WHITE, "F$currentAperture")
                        }
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val shutterSpeed = stateObject.getDouble(THETA_SHUTTER_SPEED)
                if (shutterSpeed != currentShutterSpeed)
                {
                    Log.v(TAG, " SS : $currentShutterSpeed -> $shutterSpeed")
                    currentShutterSpeed = shutterSpeed
                    if (currentShutterSpeed == 0.0)
                    {
                        showInformation.setMessage(IShowInformation.AREA_3, Color.WHITE, "")
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_3, Color.WHITE, convertShutterSpeedString(currentShutterSpeed))
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

    private fun convertShutterSpeedString(shutterSpeed : Double) : String
    {
        var inv = 0.0
        var stringValue = ""
        try
        {
            if (shutterSpeed  < 1.0)
            {
                inv = 1.0 / shutterSpeed
            }
            if (inv < 2.0) // if (inv < 10.0)
            {
                inv = 0.0
            }
            if (inv > 0.0f)
            {
                // シャッター速度を分数で表示する
                var intValue = inv.toInt()
                val modValue = intValue % 10
                if (modValue == 9 || modValue == 4)
                {
                    // ちょっと格好が悪いけど...切り上げ
                    intValue++
                }
                stringValue = "1/$intValue"
            }
            else
            {
                // シャッター速度を数値(秒数)で表示する
                stringValue = "${shutterSpeed}s "
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (stringValue)
    }

    private fun checkStatus0(response: String)
    {
        try
        {
            //Log.v(TAG, " STATUS : $response")
            val stateObject = JSONObject(response).getJSONObject("state")
            try
            {
                val captureStatus = stateObject.getString(THETA_CAPTURE_STATUS)
                if (captureStatus != currentCaptureStatus)
                {
                    Log.v(TAG, " CapStatus : $currentCaptureStatus -> $captureStatus")
                    if (captureStatus != "idle")
                    {
                        showInformation.setMessage(IShowInformation.AREA_8, Color.WHITE, captureStatus)
                    }
                    else
                    {
                        showInformation.setMessage(IShowInformation.AREA_8, Color.WHITE, "")
                    }
                    showInformation.invalidate()
                    currentCaptureStatus = captureStatus
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
            try
            {
                val batteryLevel = stateObject.getDouble(THETA_BATTERY_LEVEL)
                if (batteryLevel != currentBatteryLevel)
                {
                    Log.v(TAG, " BATTERY : $currentBatteryLevel => $batteryLevel")
                    currentBatteryLevel = batteryLevel
                    updateRemainBattery(currentBatteryLevel)
                    showInformation.invalidate()
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
                val percentage = ceil(percentageDouble * 100.0).toInt()
                showInformation.setMessage(IShowInformation.AREA_8, color, "Batt.$percentage%")
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
        private const val timeoutMs = 3300
        private const val loopWaitMs : Long = 450
    }
}
