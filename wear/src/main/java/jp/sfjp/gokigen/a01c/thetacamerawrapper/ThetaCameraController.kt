package jp.sfjp.gokigen.a01c.thetacamerawrapper

import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceDataStore
import jp.sfjp.gokigen.a01c.*
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl
import jp.sfjp.gokigen.a01c.liveview.IAutoFocusFrameDisplay
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraRunMode
import jp.sfjp.gokigen.a01c.olycamerawrapper.IIndicatorControl
import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider
import jp.sfjp.gokigen.a01c.preference.PreferenceAccessWrapper
import jp.sfjp.gokigen.a01c.thetacamerawrapper.connection.ThetaCameraConnection
import jp.sfjp.gokigen.a01c.thetacamerawrapper.liveview.ThetaLiveViewControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaDummyOperation
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaMovieRecordingControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaOptionUpdateControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaSingleShotControl
import jp.sfjp.gokigen.a01c.thetacamerawrapper.status.ThetaCameraStatusWatcher

class ThetaCameraController(val context: AppCompatActivity, private val focusFrameDisplay: IAutoFocusFrameDisplay, private val showInformation: IShowInformation, private val receiver: ICameraStatusReceiver, private val preferences: PreferenceAccessWrapper) : ICameraController, IIndicatorControl
{
    private lateinit var featureDispatcher : ThetaFeatureDispatcher
    private lateinit var liveViewControl : ThetaLiveViewControl
    private val dummyOperation = ThetaDummyOperation()
    private val sessionIdHolder = ThetaSessionHolder()
    private val cameraConnection = ThetaCameraConnection(context, receiver, sessionIdHolder)
    private val singleShot = ThetaSingleShotControl(sessionIdHolder, this, this)
    private val movieShot = ThetaMovieRecordingControl(context, sessionIdHolder, this, showInformation, this)
    private val optionSet = ThetaOptionUpdateControl(sessionIdHolder, this, this)
    private val statusWatcher = ThetaCameraStatusWatcher()

    override fun connectFinished()
    {
        try
        {
            // スチルモードに切り替える
            changeCaptureImageMode(sessionIdHolder.isApiLevelV21())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun setLiveViewListener(listener: CameraLiveViewListenerImpl)
    {
        Log.v(TAG, " setLiveViewListener() : ${sessionIdHolder.isApiLevelV21()} ")
        this.liveViewControl = ThetaLiveViewControl(listener)
    }

    override fun changeLiveViewSize(size: String?)
    {
        // ログだけ残す
        Log.v(toString(), " changeLiveViewSize: $size")
    }

    override fun startLiveView()
    {
        try
        {
            // ライブビューの表示...
            if (::liveViewControl.isInitialized)
            {
                liveViewControl.startLiveView(sessionIdHolder)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopLiveView()
    {
        if (::liveViewControl.isInitialized)
        {
            liveViewControl.stopLiveView()
        }
    }

    override fun updateTakeMode()
    {
        if (::featureDispatcher.isInitialized)
        {
            when (featureDispatcher.takeMode)
            {
                "P" -> changeCaptureImageMode(sessionIdHolder.isApiLevelV21())
                "Movie" -> changeCaptureVideoMode(sessionIdHolder.isApiLevelV21())
            }
        }
    }

    private fun changeCaptureImageMode(apiV21 : Boolean)
    {
        try
        {
            optionSet.setOptions("\"captureMode\" : \"image\"", apiV21)
            waitMs(200);
            startLiveView()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun changeCaptureVideoMode(apiV21 : Boolean)
    {
        try
        {
            if (apiV21)
            {
                optionSet.setOptions("\"captureMode\" : \"video\"", apiV21)
            }
            else
            {
                optionSet.setOptions("\"captureMode\" : \"_video\"", apiV21)

                // API Level 1 の対応機種では、Videoモードでライブビューが動かないので止める
                waitMs(200);
                stopLiveView()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun driveAutoFocus(event: MotionEvent?): Boolean
    {
        return (true)
    }

    override fun unlockAutoFocus()
    {
        // なにもしない
    }

    override fun isContainsAutoFocusPoint(event: MotionEvent?): Boolean
    {
        return (false)
    }

    override fun singleShot()
    {
        singleShot.singleShot(sessionIdHolder.isApiLevelV21())
    }

    override fun movieControl()
    {
        movieShot.movieControl(sessionIdHolder.isApiLevelV21())
    }

    override fun bracketingShot(bracketingStyle: Int, bracketingCount: Int, durationSeconds: Int)
    {
        // TODO("Not yet implemented")
    }

    override fun setRecViewMode(isRecViewMode: Boolean)
    {
        // なにもしない
    }

    override fun toggleAutoExposure()
    {
        // なにもしない
    }

    override fun toggleManualFocus()
    {
        // なにもしない
    }

    override fun isManualFocus(): Boolean
    {
        return (false)
    }

    override fun isAFLock(): Boolean
    {
        return (false)
    }

    override fun isAELock(): Boolean
    {
        return (false)
    }

    override fun updateStatusAll()
    {
        // なにもしない
    }

    override fun getStatusWatcher(): ICameraStatusWatcher
    {
        return (statusWatcher)
    }

    override fun getCameraPropertyProvider(): IOlyCameraPropertyProvider
    {
        return (dummyOperation)
    }

    override fun getCameraPropertyLoadSaveOperations(): ICameraPropertyLoadSaveOperations
    {
        return (dummyOperation)
    }

    override fun getLoadSaveCameraProperties(): ILoadSaveCameraProperties
    {
        return (dummyOperation)
    }

    override fun getChangeRunModeExecutor(): ICameraRunMode
    {
        return (dummyOperation)
    }

    override fun getConnectionInterface(): ICameraConnection
    {
        return (cameraConnection)
    }

    override fun getZoomLensHolder(): IZoomLensHolder
    {
        return (dummyOperation)
    }

    override fun getLevelGauge(): ILevelGauge
    {
        return (dummyOperation)
    }

    override fun getFeatureDispatcher(context: AppCompatActivity, statusDrawer: IShowInformation, camera: ICameraController, accessWrapper: PreferenceDataStore, liveImageView: ILiveImageStatusNotify): ICameraFeatureDispatcher
    {
        if (!(::featureDispatcher.isInitialized))
        {
            featureDispatcher = ThetaFeatureDispatcher(context, statusDrawer, camera, accessWrapper, liveImageView)
        }
        return (featureDispatcher)
    }

    override fun onAfLockUpdate(isAfLocked: Boolean)
    {
        //TODO("Not yet implemented")
    }

    override fun onShootingStatusUpdate(status: IIndicatorControl.shootingStatus?)
    {
        //TODO("Not yet implemented")
    }

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
        private val TAG = ThetaCameraController::class.java.simpleName
    }
}
