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

    // 表示エリア定義用...  : 将来的には preferenceにおいてカスタマイズ可能にする
    private int takeModeArea = IShowInformation.AREA_1;
    private int shutterSpeedArea = IShowInformation.AREA_2;
    private int apertureArea = IShowInformation.AREA_3;
    private int isoSensitivityArea = 0;
    private int focalLengthArea = 0;
    private int exposureCompensationArea = 0;
    private int warningArea = IShowInformation.AREA_4;


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
        if ((propetyValue != null)&&(takeModeArea != 0))
        {
            informationObject.setMessage(takeModeArea, Color.WHITE, propetyValue);
        }
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
    public void updateWarning(String value)
    {
        if((value != null)&&(warningArea != 0))
        {
            {
                informationObject.setMessage(warningArea, Color.argb(0, 255,204,0), value);
            }
        }
    }

    @Override
    public void updateExposureCompensation(String value)
    {
        if((value != null)&&(exposureCompensationArea != 0))
        {
            //if (!"0".equals(value))
            {
                informationObject.setMessage(exposureCompensationArea, Color.WHITE, value);
            }
        }
    }

    @Override
    public void updateFocalLength(String value)
    {
        if((value != null)&&(focalLengthArea != 0))
        {
            informationObject.setMessage(focalLengthArea, Color.WHITE, value);
        }
    }

    @Override
    public void updateIsoSensitivity(String value)
    {
        String prefix = "ISO";
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ISO_SENSITIVITY));
        if ("Auto".equals(propetyValue))
        {
            prefix = prefix + "-A ";
        }
        if((value != null)&&(isoSensitivityArea != 0))
        {
            informationObject.setMessage(isoSensitivityArea, Color.WHITE, prefix + value);
        }
    }

    @Override
    public void updateShutterSpeed(String value)
    {
        if((value != null)&&(shutterSpeedArea != 0))
        {
            informationObject.setMessage(shutterSpeedArea, Color.WHITE, value);
        }
    }

    @Override
    public void updateAperture(String value)
    {
        if((value != null)&&(apertureArea != 0))
        {
            informationObject.setMessage(apertureArea, Color.WHITE, "F" + value);
        }
    }

    @Override
    public void updateCameraStatus(String message)
    {
        informationObject.setMessage(IShowInformation.AREA_4, Color.argb(0, 255,204,0), message);
    }
}
