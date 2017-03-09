package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.graphics.Color;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import jp.co.olympus.camerakit.OLYCamera;
import jp.sfjp.gokigen.a01c.IShowInformation;


class CameraStatusDisplay implements  ICameraStatusDisplay
{
    private final String TAG = toString();
    private final IOlyCameraPropertyProvider propertyProxy;
    private final IShowInformation informationObject;

    CameraStatusDisplay(IOlyCameraPropertyProvider propertyProxy, IShowInformation informationObject)
    {
        this.propertyProxy = propertyProxy;
        this.informationObject = informationObject;
    }

    /****************** ICameraStatusDisplayの 実装  *****************/

    @Override
    public void updateTakeMode()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        informationObject.setMessage(IShowInformation.AREA_1, Color.WHITE, propetyValue);

        propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.SHUTTER_SPEED));
        informationObject.setMessage(IShowInformation.AREA_2, Color.WHITE, propetyValue);

        propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.APERTURE));
        informationObject.setMessage(IShowInformation.AREA_3, Color.WHITE, "F" + propetyValue);

        informationObject.setMessage(IShowInformation.AREA_4, Color.GRAY, "");

    }

    @Override
    public void updateDriveMode()
    {
        //updateCameraPropertyStatus();
    }

    @Override
    public void updateWhiteBalance()
    {
        //updateCameraPropertyStatus();
    }

    @Override
    public void updateBatteryLevel()
    {
    }

    @Override
    public void updateAeMode()
    {
    }

    @Override
    public void updateAeLockState()
    {

    }

    @Override
    public void updateCameraStatus()
    {
        Log.v(TAG,"updateCameraStatus()");
        updateTakeMode();
    }

    @Override
    public void updateCameraStatus(String message)
    {
        informationObject.setMessage(IShowInformation.AREA_4, Color.MAGENTA, message);
    }
}
