package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation

import android.content.Context
import android.graphics.Color
import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient

class ThetaMovieRecordingControl(val context: Context, private val sessionIdProvider: IThetaSessionIdProvider, private val statusDrawer: IShowInformation, private val liveViewControl: ICameraController)
{
    private val httpClient = SimpleHttpClient()
    private var isCapturing = false

    fun movieControl(useOSCv2 : Boolean)
    {
        try
        {
            if (!(isCapturing))
            {
                startCapture(useOSCv2)
                statusDrawer.setMessage(IShowInformation.AREA_9, Color.RED, context.getString(R.string.video_recording))
            }
            else
            {
                stopCapture(useOSCv2)
                statusDrawer.setMessage(IShowInformation.AREA_9, Color.WHITE, "")
            }
            statusDrawer.invalidate()
            isCapturing = !isCapturing
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun startCapture(useOSCv2 : Boolean)
    {
        Log.v(TAG, "startCapture() (API v2.1 : $useOSCv2)")
        try
        {
            val thread = Thread {
                try
                {
                    val shootUrl = "http://192.168.1.1/osc/commands/execute"
                    // val postData = if (useOSCv2) "{\"name\":\"camera.startCapture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera.startCapture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"
                    val postData = if (useOSCv2) "{\"name\":\"camera.startCapture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._startCapture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"

                    Log.v(TAG, " start Capture : $postData")
                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " startCapture() : $result")
                    }
                    else
                    {
                        Log.v(TAG, "startCapture() reply is null.  $postData")
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

    private fun stopCapture(useOSCv2 : Boolean)
    {
        Log.v(TAG, "stopCapture() (API v2.1 : $useOSCv2)")
        try
        {
            val thread = Thread {
                try
                {
                    val shootUrl = "http://192.168.1.1/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.stopCapture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._stopCapture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"

                    Log.v(TAG, " stop Capture : $postData")

                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " stopCapture() : $result")
                        if (!useOSCv2)
                        {
                            // THETA V / THETA Z1 は、videoモードでライブビューができるので...
                            liveViewControl.stopLiveView()
                            waitMs() // ちょっと待つ...
                            liveViewControl.startLiveView()
                        }
                    }
                    else
                    {
                        Log.v(TAG, "stopCapture() reply is null. $postData")
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

    /**
     *
     *
     */
    private fun waitMs(waitMs: Int = 300)
    {
        try
        {
            Thread.sleep(waitMs.toLong())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaMovieRecordingControl::class.java.simpleName
        private const val timeoutMs = 6000
    }
}