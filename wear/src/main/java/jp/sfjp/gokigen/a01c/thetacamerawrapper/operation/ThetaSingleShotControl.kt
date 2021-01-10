package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation

import android.util.Log
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.olycamerawrapper.IIndicatorControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject


class ThetaSingleShotControl(private val sessionIdProvider: IThetaSessionIdProvider, private val indicator: IIndicatorControl, private val liveViewControl: ICameraController)
{
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun singleShot(useThetaV21 : Boolean)
    {
        Log.v(TAG, "singleShot()")
        try
        {
            val thread = Thread {
                try
                {
                    val shootUrl = "http://192.168.1.1/osc/commands/execute"
                    val postData = if (useThetaV21) "{\"name\":\"camera.takePicture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera.takePicture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"
                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " singleShot() : $result")
                        indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Starting)

                        // 画像処理が終わるまで待つ
                        waitChangeStatus()

                        // ライブビューのの再実行を指示する
                        indicator.onShootingStatusUpdate(IIndicatorControl.shootingStatus.Stopping)
                        liveViewControl.stopLiveView()
                        waitMs(300) // ちょっと待つ...
                        liveViewControl.startLiveView()
                    }
                    else
                    {
                        Log.v(TAG, "singleShot() reply is null.")
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
        val maxWaitTimeoutMs = 9000 // 最大待ち時間 (単位: ms)
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
        private val TAG = ThetaSingleShotControl::class.java.simpleName
        private const val timeoutMs = 6000
    }
}
