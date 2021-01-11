package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation

import android.content.Context
import android.graphics.Color
import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.olycamerawrapper.IIndicatorControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject

class ThetaMovieRecordingControl(val context: Context, private val sessionIdProvider: IThetaSessionIdProvider, private val indicator: IIndicatorControl, private val statusDrawer: IShowInformation, private val liveViewControl: ICameraController)
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
                        indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Starting)

                        // 画像処理が終わるまで待つ
                        //waitChangeStatus()

                        // ライブビューのの再実行を指示する
                        //indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Stopping)
                        //liveViewControl.stopLiveView()
                        //waitMs(300) // ちょっと待つ...
                        //liveViewControl.startLiveView()
                    }
                    else
                    {
                        Log.v(TAG, "startCapture() reply is null.")
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
                        indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Starting)

                        //// 画像処理が終わるまで待つ
                        //waitChangeStatus()

                        // ライブビューのの再実行を指示する
                        indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Stopping)
                        liveViewControl.stopLiveView()
                        waitMs(300) // ちょっと待つ...
                        liveViewControl.startLiveView()
                    }
                    else
                    {
                        Log.v(TAG, "stopCapture() reply is null.")
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
     * 撮影状態が変わるまで待つ。
     * (ただし、タイムアウト時間を超えたらライブビューを再開させる)
     */
    private fun waitChangeStatus()
    {
        val getStateUrl = "http://192.168.1.1/osc/state"
        val maxWaitTimeoutMs = 9900 // 最大待ち時間 (単位: ms)
        var fingerprint = ""
        try
        {
            val result: String? = httpClient.httpPost(getStateUrl, "", timeoutMs)
            if ((result != null)&&(result.isNotEmpty()))
            {
                val jsonObject = JSONObject(result)
                fingerprint = jsonObject.getString("fingerprint")

                //  現在の状態(ログを出す)
                Log.v(TAG, " $getStateUrl $result ($fingerprint)")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        try
        {
            val firstTime = System.currentTimeMillis()
            var currentTime = firstTime
            while (currentTime - firstTime < maxWaitTimeoutMs)
            {
                //  ... 状態を見て次に進める
                val result: String? = httpClient.httpPost(getStateUrl, "", timeoutMs)
                if ((result != null)&&(result.isNotEmpty()))
                {
                    val jsonObject = JSONObject(result)
                    val currentFingerprint = jsonObject.getString("fingerprint")

                    //  ログを出してみる
                    // Log.v(TAG, " " + getStateUrl + " ( " + result + " ) " + "(" + fingerprint + " " + current_fingerprint + ")");
                    if (fingerprint != currentFingerprint)
                    {
                        // fingerprintが更新された！
                        break
                    }
                    Log.v(TAG, "  -----  NOW PROCESSING  ----- : $fingerprint")
                }
                waitMs(1000)
                currentTime = System.currentTimeMillis()
            }
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
    private fun waitMs(waitMs: Int)
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