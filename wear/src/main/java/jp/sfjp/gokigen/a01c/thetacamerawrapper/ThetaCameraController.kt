package jp.sfjp.gokigen.a01c.thetacamerawrapper

import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import jp.sfjp.gokigen.a01c.ICameraConnection
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl
import jp.sfjp.gokigen.a01c.liveview.IAutoFocusFrameDisplay
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver
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
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaSingleShotControl

class ThetaCameraController(val context: AppCompatActivity, private val focusFrameDisplay: IAutoFocusFrameDisplay, private val showInformation: IShowInformation, private val receiver: ICameraStatusReceiver, private val preferences: PreferenceAccessWrapper) : ICameraController, IIndicatorControl
{
    //private lateinit var listener : CameraLiveViewListenerImpl
    private lateinit var liveViewControl : ThetaLiveViewControl
    private val dummyOperation = ThetaDummyOperation()
    private val sessionIdHolder = ThetaSessionHolder()
    private val cameraConnection = ThetaCameraConnection(context, receiver, sessionIdHolder)
    private val singleShot = ThetaSingleShotControl(sessionIdHolder, this, this)

    override fun setLiveViewListener(listener: CameraLiveViewListenerImpl)
    {
        this.liveViewControl = ThetaLiveViewControl(sessionIdHolder, listener)
    }

    override fun changeLiveViewSize(size: String?)
    {
        // ログだけ残す
        Log.v(toString(), " changeLiveViewSize: $size")
    }

    override fun startLiveView()
    {
        if (::liveViewControl.isInitialized)
        {
            liveViewControl.startLiveView()
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
        // なにもしない
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
        // TODO("Not yet implemented")
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

    override fun onAfLockUpdate(isAfLocked: Boolean)
    {
        //TODO("Not yet implemented")
    }

    override fun onShootingStatusUpdate(status: IIndicatorControl.shootingStatus?)
    {
        //TODO("Not yet implemented")
    }
}
