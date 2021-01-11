package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation

import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.olycamerawrapper.IIndicatorControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient

class ThetaOptionUpdateControl(private val sessionIdProvider: IThetaSessionIdProvider, private val indicator: IIndicatorControl, private val liveViewControl: ICameraController)
{
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun setOptions(options: String, useOSCv2 : Boolean)
    {
        Log.v(TAG, "setOptions() useOSCv2 : $useOSCv2  MSG : $options")
        try
        {
            val thread = Thread {
                try
                {
                    val setOptionsUrl = "http://192.168.1.1/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.setOptions\",\"parameters\":{\"timeout\":0, \"options\": {\"$options\"}}}" else "{\"name\":\"camera.setOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"options\": { $options }}}"

                    Log.v(TAG, " OPTIONS : $postData")

                    val result: String? = httpClient.httpPostWithHeader(setOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " setOptions() : $result")
                    }
                    else
                    {
                        Log.v(TAG, "setOptions() reply is null or empty.")
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

    companion object
    {
        private val TAG = ThetaOptionUpdateControl::class.java.simpleName
        private const val timeoutMs = 3000
    }

}