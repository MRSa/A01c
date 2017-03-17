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
    private int exposureCompensationArea = IShowInformation.AREA_6;  // 露出補正値の表示エリア指定
    private int warningArea = IShowInformation.AREA_7;                 // 警告の表示エリア指定
    private int batteryLevelArea = 0;                                  // バッテリの残量表示エリア指定
    private int whiteBalanceArea = 0;                                  // ホワイトバランスの表示エリア指定
    private int driveModeArea = 0;                                     // ドライブモードの表示エリア指定
    private int aeModeArea = 0;                                         // 測光モードの表示エリア指定
    private int aeLockStateArea = IShowInformation.AREA_6;            // AEロック状態の表示エリア指定
    private int colorToneArea = IShowInformation.AREA_8;              // 仕上がり・ピクチャーモードの表示エリア指定
    private int artFilterArea = 0;                                     // アートフィルターの表示エリア指定

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
        if (takeModeArea == 0)
        {
            return;
        }
        Log.v(TAG, "updateTakeMode()");
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        if (propetyValue != null)
        {
            informationObject.setMessage(takeModeArea, Color.WHITE, propetyValue);
            updateDisplayArea(propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateDriveMode()
    {
        if (driveModeArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.DRIVE_MODE));
        if (propetyValue != null)
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
        if (whiteBalanceArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.WB_MODE));
        if (propetyValue != null)
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
        if (batteryLevelArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.BATTERY_LEVEL));
        if (propetyValue != null)
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
        if (aeModeArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_MODE));
        if (propetyValue != null)
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
        if (aeLockStateArea == 0)
        {
            return;
        }
        String message = "";
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE));
        if (propetyValue != null)
        {
            if (propetyValue.equals("LOCK"))
            {
                message = "AE-L";
            }
        }
        if (message.length() > 0)
        {
            informationObject.setMessage(aeLockStateArea, Color.WHITE, message);
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
        if (warningArea == 0)
        {
            return;
        }
        if(value != null)
        {
            {
                informationObject.setMessage(warningArea, Color.argb(255, 255,204,0), value);
            }
        }
    }

    @Override
    public void updateColorTone()
    {
        if (colorToneArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.COLOR_TONE));
        if (propetyValue != null)
        {
            informationObject.setMessage(colorToneArea, Color.WHITE, propetyValue);
        }
    }


    @Override
    public void updateArtFilter()
    {
        if (artFilterArea == 0)
        {
            return;
        }
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ART_FILTER));
        if (propetyValue != null)
        {
            informationObject.setMessage(artFilterArea, Color.WHITE, /*Color.argb(255, 0x72, 0x39, 0x34),*/ propetyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateExposureCompensation(String value)
    {
        if (exposureCompensationArea == 0)
        {
            return;
        }
        if (value != null)
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
        if (focalLengthArea == 0)
        {
            return;
        }
        if (value != null)
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
        if (isoSensitivityArea == 0)
        {
            return;
        }
        String prefix = "ISO";
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ISO_SENSITIVITY));
        if ("Auto".equals(propetyValue))
        {
            prefix = "iso";
        }
        if (value != null)
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
        if (shutterSpeedArea == 0)
        {
            return;
        }
        if (value != null)
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
        if (apertureArea == 0)
        {
            return;
        }
        if (value != null)
        {
            informationObject.setMessage(apertureArea, Color.WHITE, "F" + value);
        }
    }


    /**
     *   撮影モードに合わせて、表示内容を変化させる...
     *
     */
    private void updateDisplayArea(String takeMode)
    {
        switch (takeMode)
        {
            case "P":
                colorToneArea = IShowInformation.AREA_8;
                artFilterArea = 0;
                updateColorTone();
                break;

            case "A":
                colorToneArea = 0;
                artFilterArea = 0;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "S":
                colorToneArea = 0;
                artFilterArea = 0;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "M":
                colorToneArea = 0;
                artFilterArea = 0;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "ART":
                colorToneArea = 0;
                artFilterArea = IShowInformation.AREA_8;
                updateArtFilter();
                break;

            case "iAuto":
                colorToneArea = 0;
                artFilterArea = 0;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "movie":
                colorToneArea = 0;
                artFilterArea = 0;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            default:
                takeModeArea = IShowInformation.AREA_1;                // 撮影モードの表示エリア指定
                shutterSpeedArea = IShowInformation.AREA_2;           // シャッタースピードの表示エリア指定
                apertureArea = IShowInformation.AREA_3;                // 絞り値の表示エリア指定
                isoSensitivityArea = IShowInformation.AREA_4;         // ISO感度の表示エリア指定
                focalLengthArea = 0;                                   // 焦点距離の表示エリア指定
                exposureCompensationArea = IShowInformation.AREA_6;  // 露出補正値の表示エリア指定
                warningArea = IShowInformation.AREA_7;                 // 警告の表示エリア指定
                batteryLevelArea = 0;                                  // バッテリの残量表示エリア指定
                whiteBalanceArea = 0;                                  // ホワイトバランスの表示エリア指定
                driveModeArea = 0;                                     // ドライブモードの表示エリア指定
                aeModeArea = 0;                                         // 測光モードの表示エリア指定
                aeLockStateArea = IShowInformation.AREA_6;           // AEロック状態の表示エリア指定
                colorToneArea = IShowInformation.AREA_8;              // 仕上がり・ピクチャーモードの表示エリア指定
                artFilterArea = 0;                                     // アートフィルターの表示エリア指定
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;
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

    /**
     *   すべてのステータスをまとめて更新する
     *
     */
    @Override
    public void updateCameraStatusAll()
    {
        updateTakeMode();

        updateColorTone();
        updateDriveMode();
        updateWhiteBalance();
        updateBatteryLevel();
        updateAeMode();
        updateAeLockState();
        updateArtFilter();
    }
}
