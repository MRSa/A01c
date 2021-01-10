package jp.sfjp.gokigen.a01c.thetacamerawrapper.liveview

import android.util.Log
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.IThetaSessionIdProvider
import jp.sfjp.gokigen.a01c.utils.SimpleLiveviewSlicer

class ThetaLiveViewControl(private val sessionIdProvider: IThetaSessionIdProvider, private val liveViewListener: CameraLiveViewListenerImpl)
{
    private var whileFetching = false

    fun startLiveView()
    {
        Log.v(TAG, " startLiveView()")
        try
        {
            val thread = Thread {
                try
                {

                    start(!(sessionIdProvider.sessionId.isEmpty()))
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

    fun stopLiveView()
    {
        Log.v(TAG, " stopLiveView()")
        whileFetching = false
    }

    private fun start(useOscV2 : Boolean)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.")
            return
        }
        whileFetching = true

        try
        {
            val thread = Thread {
                Log.d(TAG, "Starting retrieving streaming data from server.")
                val slicer = SimpleLiveviewSlicer()
                var continuousNullDataReceived = 0
                try
                {
                    val streamUrl = "http://192.168.1.1/osc/commands/execute"
                    val paramData = if (useOscV2) "{\"name\":\"camera.getLivePreview\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._getLivePreview\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.getSessionId().toString() + "\"}}"
                    Log.v(TAG, " >>>>> START THETA PREVIEW : $streamUrl $paramData")

                    // Create Slicer to open the stream and parse it.
                    slicer.open(streamUrl, paramData, "application/json;charset=utf-8")
                    while (whileFetching) {
                        val payload: SimpleLiveviewSlicer.Payload? = slicer.nextPayloadForMotionJpeg()
                        if (payload == null)
                        {
                            //Log.v(TAG, "Liveview Payload is null.");
                            continuousNullDataReceived++
                            if (continuousNullDataReceived > FETCH_ERROR_MAX)
                            {
                                Log.d(TAG, " FETCH ERROR MAX OVER ")
                                break
                            }
                            continue
                        }
                        liveViewListener.setImageData(payload.jpegData, null)
                        continuousNullDataReceived = 0
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    slicer.close()
                    if (whileFetching && continuousNullDataReceived > FETCH_ERROR_MAX)
                    {
                        // 再度ライブビューのスタートをやってみる。
                        whileFetching = false
                        start(useOscV2)
                    }
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
        private val TAG = ThetaLiveViewControl::class.java.simpleName
        private const val FETCH_ERROR_MAX = 30
    }
}
