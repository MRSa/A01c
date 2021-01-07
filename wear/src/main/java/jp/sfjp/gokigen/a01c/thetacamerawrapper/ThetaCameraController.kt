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
import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider

class ThetaCameraController(val context : AppCompatActivity, val focusFrameDisplay : IAutoFocusFrameDisplay, val showInformation : IShowInformation, val receiver : ICameraStatusReceiver) : ICameraController
{
    private lateinit var listener : CameraLiveViewListenerImpl

    override fun setLiveViewListener(listener: CameraLiveViewListenerImpl)
    {
        this.listener = listener
    }

    override fun changeLiveViewSize(size: String?)
    {
        // ログだけ残す
        Log.v(toString(), " changeLiveViewSize: $size")
    }

    override fun startLiveView() {
        TODO("Not yet implemented")
    }

    override fun stopLiveView() {
        TODO("Not yet implemented")
    }

    override fun updateTakeMode() {
        TODO("Not yet implemented")
    }

    override fun driveAutoFocus(event: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun unlockAutoFocus() {
        TODO("Not yet implemented")
    }

    override fun isContainsAutoFocusPoint(event: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun singleShot() {
        TODO("Not yet implemented")
    }

    override fun movieControl() {
        TODO("Not yet implemented")
    }

    override fun bracketingShot(bracketingStyle: Int, bracketingCount: Int, durationSeconds: Int) {
        TODO("Not yet implemented")
    }

    override fun setRecViewMode(isRecViewMode: Boolean) {
        TODO("Not yet implemented")
    }

    override fun toggleAutoExposure() {
        TODO("Not yet implemented")
    }

    override fun toggleManualFocus() {
        TODO("Not yet implemented")
    }

    override fun isManualFocus(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAFLock(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAELock(): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateStatusAll() {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyProvider(): IOlyCameraPropertyProvider {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyLoadSaveOperations(): ICameraPropertyLoadSaveOperations {
        TODO("Not yet implemented")
    }

    override fun getLoadSaveCameraProperties(): ILoadSaveCameraProperties {
        TODO("Not yet implemented")
    }

    override fun getChangeRunModeExecutor(): ICameraRunMode {
        TODO("Not yet implemented")
    }

    override fun getConnectionInterface(): ICameraConnection {
        TODO("Not yet implemented")
    }

    override fun getZoomLensHolder(): IZoomLensHolder {
        TODO("Not yet implemented")
    }

    override fun getLevelGauge(): ILevelGauge {
        TODO("Not yet implemented")
    }


}