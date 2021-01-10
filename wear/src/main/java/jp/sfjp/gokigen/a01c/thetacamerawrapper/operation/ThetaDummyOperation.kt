package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation

import android.graphics.Color
import jp.co.olympus.camerakit.OLYCamera
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraHardwareStatus
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraRunMode
import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider

class ThetaDummyOperation : ILevelGauge, IZoomLensHolder, ICameraRunMode, ILoadSaveCameraProperties, ICameraPropertyLoadSaveOperations, IOlyCameraPropertyProvider, ICameraHardwareStatus
{
    override fun getLevel(area: ILevelGauge.LevelArea?): Float {
        // TODO("Not yet implemented")
        return (1.0f)
    }

    override fun getLevelColor(value: Float): Int {
        // TODO("Not yet implemented")
        return (Color.WHITE)
    }

    override fun checkLevelGauge(camera: OLYCamera?) {
        // TODO("Not yet implemented")
    }

    override fun updateLevelGaugeChecking(isWatch: Boolean) {
        //TODO("Not yet implemented")
    }

    override fun canZoom(): Boolean {
        //TODO("Not yet implemented")
        return (false)
    }

    override fun updateStatus() {
        //TODO("Not yet implemented")
    }

    override fun getMaximumFocalLength(): Float {
        // TODO("Not yet implemented")
        return (16.0f)
    }

    override fun getActualFocalLength(): Float {
        TODO("Not yet implemented")
    }

    override fun inquireHardwareInformation(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getLensMountStatus(): String {
        TODO("Not yet implemented")
    }

    override fun getMediaMountStatus(): String {
        TODO("Not yet implemented")
    }

    override fun getMinimumFocalLength(): Float {
        //TODO("Not yet implemented")
        return (16.0f)
    }

    override fun getCurrentFocalLength(): Float {
        //TODO("Not yet implemented")
        return (16.0f)
    }

    override fun driveZoomLens(targetLength: Float) {
        //TODO("Not yet implemented")
    }

    override fun driveZoomLens(direction: Int) {
        //TODO("Not yet implemented")
    }

    override fun isDrivingZoomLens(): Boolean {
        //TODO("Not yet implemented")
        return (false)
    }

    override fun getCurrentDigitalZoomScale(): Float {
        //TODO("Not yet implemented")
        return (1.0f)
    }

    override fun magnifyLiveView(scale: Int): Boolean {
        //TODO("Not yet implemented")
        return (false)
    }

    override fun changeDigitalZoomScale(scale: Float, isCyclic: Boolean) {
       // TODO("Not yet implemented")
    }

    override fun changeRunMode(isRecording: Boolean) {
      //TODO("Not yet implemented")
    }

    override fun isRecordingMode(): Boolean {
        //TODO("Not yet implemented")
        return (true)
    }

    override fun loadCameraSettings(id: String?) {
        //TODO("Not yet implemented")
    }

    override fun saveCameraSettings(id: String?, name: String?) {
        //TODO("Not yet implemented")
    }

    override fun saveProperties(idHeader: String?, dataName: String?) {
        //TODO("Not yet implemented")
    }

    override fun loadProperties(idHeader: String?, dataName: String?) {
        //TODO("Not yet implemented")
    }

    override fun getCameraPropertyNames(): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyValue(name: String?): String {
        //TODO("Not yet implemented")
        return ("")
    }

    override fun getCameraPropertyValues(names: MutableSet<String>?): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyTitle(name: String?): String {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyValueList(name: String?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun getCameraPropertyValueTitle(propertyValue: String?): String {
        TODO("Not yet implemented")
    }

    override fun setCameraPropertyValue(name: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun setCameraPropertyValues(values: MutableMap<String, String>?) {
        TODO("Not yet implemented")
    }

    override fun updateCameraPropertyUp(name: String?) {
        TODO("Not yet implemented")
    }

    override fun updateCameraPropertyDown(name: String?) {
        TODO("Not yet implemented")
    }

    override fun changeCameraProperty(name: String?, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun canSetCameraProperty(name: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isConnected(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getHardwareStatus(): ICameraHardwareStatus {
        TODO("Not yet implemented")
    }

}