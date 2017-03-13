package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.graphics.Color;
import android.util.Log;

import jp.sfjp.gokigen.a01c.IShowInformation;

/**
 *
 *
 */
class CameraStatusDisplay implements  ICameraStatusDisplay
{
    private final String TAG = toString();
    private final IOlyCameraPropertyProvider propertyProxy;
    private final IShowInformation informationObject;

    // 表示エリア定義用...  : 将来的には preferenceにおいてカスタマイズ可能にするつもり
    private int takeModeArea = IShowInformation.AREA_1;                // 撮影モードの表示エリア指定
    private int shutterSpeedArea = IShowInformation.AREA_2;           // シャッタースピードの表示エリア指定
    private int apertureArea = IShowInformation.AREA_3;                // 絞り値の表示エリア指定
    private int isoSensitivityArea = IShowInformation.AREA_4;         // ISO感度の表示エリア指定
    private int focalLengthArea = 0;                                   // 焦点距離の表示エリア指定
    private int exposureCompensationArea = IShowInformation.AREA_6; // 露出補正値の表示エリア指定
    private int warningArea = IShowInformation.AREA_5;                // 警告の表示エリア指定
    private int batteryLevelArea = 0;                                 // バッテリの残量表示エリア指定
    private int whiteBalanceArea = 0;                                 // ホワイトバランスの表示エリア指定
    private int driveModeArea = 0;                                    // ドライブモードの表示エリア指定
    private int aeModeArea = 0;                                        // 測光モードの表示エリア指定
    private int aeLockStateArea = 0;                                  // AEロック状態の表示エリア指定

    /**
     *
     *
     */
    CameraStatusDisplay(IOlyCameraPropertyProvider propertyProxy, IShowInformation informationObject)
    {
        this.propertyProxy = propertyProxy;
        this.informationObject = informationObject;
    }

    /****************** ICameraStatusDisplayの 実装  *****************/

    /**
     *
     *
     */
    @Override
    public void updateTakeMode()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        if ((propetyValue != null)&&(takeModeArea != 0))
        {
            informationObject.setMessage(takeModeArea, Color.WHITE, propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateDriveMode()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.DRIVE_MODE));
        if ((propetyValue != null)&&(driveModeArea != 0))
        {
            informationObject.setMessage(driveModeArea, Color.WHITE, propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateWhiteBalance()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.WB_MODE));
        if ((propetyValue != null)&&(whiteBalanceArea != 0))
        {
            informationObject.setMessage(whiteBalanceArea, Color.WHITE, propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateBatteryLevel()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.BATTERY_LEVEL));
        if ((propetyValue != null)&&(batteryLevelArea != 0))
        {
            informationObject.setMessage(batteryLevelArea, Color.WHITE, propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateAeMode()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_MODE));
        if ((propetyValue != null)&&(aeModeArea != 0))
        {
            informationObject.setMessage(aeModeArea, Color.WHITE, propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateAeLockState()
    {
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE));
        if ((propetyValue != null)&&(aeLockStateArea != 0))
        {
            if (propetyValue.equals("LOCK"))
            {
                informationObject.setMessage(aeLockStateArea, Color.WHITE, "AE-L");
            }
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateCameraStatus()
    {
        Log.v(TAG,"updateCameraStatus()");
        updateTakeMode();
    }

    /**
     *
     *
     */
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

    /**
     *
     *
     */
    @Override
    public void updateExposureCompensation(String value)
    {
        if((value != null)&&(exposureCompensationArea != 0))
        {
            if ("0.0".equals(value))
            {
                informationObject.setMessage(exposureCompensationArea, Color.WHITE, "");
            }
            else
            {
                informationObject.setMessage(exposureCompensationArea, Color.WHITE, value);
            }
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateFocalLength(String value)
    {
        if((value != null)&&(focalLengthArea != 0))
        {
            informationObject.setMessage(focalLengthArea, Color.WHITE, value);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateIsoSensitivity(String value)
    {
        String prefix = "ISO";
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ISO_SENSITIVITY));
        if ("Auto".equals(propetyValue))
        {
            prefix = "iso";
        }
        if((value != null)&&(isoSensitivityArea != 0))
        {
            informationObject.setMessage(isoSensitivityArea, Color.WHITE, prefix + value);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateShutterSpeed(String value)
    {
        if((value != null)&&(shutterSpeedArea != 0))
        {
            informationObject.setMessage(shutterSpeedArea, Color.WHITE, value);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateAperture(String value)
    {
        if((value != null)&&(apertureArea != 0))
        {
            informationObject.setMessage(apertureArea, Color.WHITE, "F" + value);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateCameraStatus(String message)
    {
        //informationObject.setMessage(IShowInformation.AREA_4, Color.argb(0, 255,204,0), message);
    }
}
