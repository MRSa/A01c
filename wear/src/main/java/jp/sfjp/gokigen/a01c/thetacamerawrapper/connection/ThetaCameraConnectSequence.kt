package jp.sfjp.gokigen.a01c.thetacamerawrapper.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdNotifier
import jp.sfjp.gokigen.a01c.utils.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraConnectSequence(private val context: AppCompatActivity, private val cameraStatusReceiver: ICameraStatusReceiver, private val sessionIdNotifier: IThetaSessionIdNotifier) : Runnable
{
    private var useThetaV21 : Boolean = false
    private val httpClient = SimpleHttpClient()

    override fun run()
    {
        // 使用する API Levelを決める
        useThetaV21 = decideApiLevel()
        try
        {
            Log.v(TAG,"Theta API v2.1 : $useThetaV21")
            if (useThetaV21)
            {
                // API Level V2.1を使用して通信する
                connectApiV21()
            }
            else
            {
                // API Level V2 を使用して通信する
                connectApiV2()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *
     */
    private fun decideApiLevel() : Boolean
    {
        var apiLevelIsV21 = false
        try
        {
            val oscInfoUrl = "http://192.168.1.1/osc/info"
            val timeoutMs = 5000
            val response: String = httpClient.httpGet(oscInfoUrl, timeoutMs)
            Log.v(TAG, " $oscInfoUrl $response")
            if (response.isNotEmpty())
            {
                val apiLevelArray = JSONObject(response).getJSONArray("apiLevel")
                val size = apiLevelArray.length()
                for (index in 0 until size)
                {
                    val api = apiLevelArray.getInt(index)
                    if (api == 1)  //if (api == 1 && useThetaV21)
                    {
                        apiLevelIsV21 = false
                        break
                    }
                    if (api == 2)  //if (api == 2 && useThetaV21)
                    {
                        apiLevelIsV21 = true
                        break
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (apiLevelIsV21)
    }

    /**
     *
     */
    private fun connectApiV2()
    {
        val commandsExecuteUrl = "http://192.168.1.1/osc/commands/execute"
        val startSessionData = "{\"name\":\"camera.startSession\",\"parameters\":{\"timeout\":0}}"
        val getStateUrl = "http://192.168.1.1/osc/state"
        val timeoutMs = 5000
        try
        {
            val response: String? = httpClient.httpPostWithHeader(commandsExecuteUrl, startSessionData, null, "application/json;charset=utf-8", timeoutMs)
            Log.v(TAG, " $commandsExecuteUrl $startSessionData $response")

            val response2: String? = httpClient.httpPostWithHeader(getStateUrl, "", null, "application/json;charset=utf-8", timeoutMs)
            Log.v(TAG, " $getStateUrl $response2")
            if ((response2 != null)&&(response2.isNotEmpty()))
            {
                try
                {
                    val jsonObject = JSONObject(response2)
                    val sessionId = jsonObject.getJSONObject("state").getString("sessionId")
                    sessionIdNotifier.receivedSessionId(sessionId)
                    onConnectNotify()
                    return
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            // 応答なし、を応答する。
            onConnectError(context.getString(R.string.theta_connect_response_ng))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            onConnectError(e.localizedMessage)
        }
    }

    private fun connectApiV21()
    {
        val commandsExecuteUrl = "http://192.168.1.1/osc/commands/execute"
        val startSessionData = "{\"name\":\"camera.startSession\",\"parameters\":{\"timeout\":0}}"
        val getStateUrl = "http://192.168.1.1/osc/state"
        val timeoutMs = 5000
        try
        {
            val responseS: String? = httpClient.httpPostWithHeader(commandsExecuteUrl, startSessionData, null, "application/json;charset=utf-8", timeoutMs)
            Log.v(TAG, " [ $httpClient ] $startSessionData ::: $responseS")
            val response: String? = httpClient.httpPostWithHeader(getStateUrl, "", null, null, timeoutMs)
            Log.v(TAG, " ($getStateUrl) $response")
            if ((response != null)&&(response.isNotEmpty()))
            {
                var apiLevel = 1
                var sessionId: String? = null
                val jsonObject = JSONObject(response)
                try
                {
                    apiLevel = jsonObject.getJSONObject("state").getInt("_apiVersion")
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                try
                {
                    sessionId = jsonObject.getJSONObject("state").getString("sessionId")
                    sessionIdNotifier.receivedSessionId(sessionId)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                if (apiLevel != 2)
                {
                    val setApiLevelData = "{\"name\":\"camera.setOptions\",\"parameters\":{\"sessionId\" : \"$sessionId\", \"options\":{ \"clientVersion\":2}}}"
                    val response3: String? = httpClient.httpPostWithHeader(commandsExecuteUrl, setApiLevelData, null, "application/json;charset=utf-8", timeoutMs)
                    Log.v(TAG, " $commandsExecuteUrl $setApiLevelData $response3")
                }
                onConnectNotify()
            } else {
                onConnectError(context.getString(R.string.camera_not_found))
            }
        } catch (e: Exception)
        {
            e.printStackTrace()
            onConnectError(e.localizedMessage)
        }
    }

    private fun onConnectNotify()
    {
        try
        {
            val thread = Thread { // カメラとの接続確立を通知する
                cameraStatusReceiver.onStatusNotify(context.getString(R.string.connect_connected))
                cameraStatusReceiver.onCameraConnected()
                Log.v(TAG, "onConnectNotify()")
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun onConnectError(reason: String?)
    {
        if (reason != null)
        {
            cameraStatusReceiver.onCameraConnectError(reason)
        }
        else
        {
            cameraStatusReceiver.onCameraConnectError("")
        }
    }

    companion object
    {
        private val TAG = ThetaCameraConnectSequence::class.java.simpleName
    }
}