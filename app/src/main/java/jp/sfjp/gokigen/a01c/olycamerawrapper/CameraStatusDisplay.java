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
    private int focalLengthArea = IShowInformation.AREA_NONE;         // 焦点距離の表示エリア指定
    private int exposureCompensationArea = IShowInformation.AREA_6;  // 露出補正値の表示エリア指定
    private int warningArea = IShowInformation.AREA_7;                 // 警告の表示エリア指定
    private int batteryLevelArea = IShowInformation.AREA_NONE;       // バッテリの残量表示エリア指定
    private int whiteBalanceArea = IShowInformation.AREA_NONE;       // ホワイトバランスの表示エリア指定
    private int driveModeArea = IShowInformation.AREA_NONE;           // ドライブモードの表示エリア指定
    private int aeModeArea = IShowInformation.AREA_NONE;              // 測光モードの表示エリア指定
    private int aeLockStateArea = IShowInformation.AREA_5;            // AEロック状態の表示エリア指定
    private int colorToneArea = IShowInformation.AREA_8;              // 仕上がり・ピクチャーモードの表示エリア指定
    private int artFilterArea = IShowInformation.AREA_NONE;           // アートフィルターの表示エリア指定

    /**
     *
     *
     */
    CameraStatusDisplay(IOlyCameraPropertyProvider propertyProxy, IShowInformation informationObject)
    {
        this.propertyProxy = propertyProxy;
        this.informationObject = informationObject;
    }

    ////////////////////// ICameraStatusDisplayの 実装  ////////////////////////

    /**
     *
     *
     */
    @Override
    public void updateTakeMode()
    {
        if (takeModeArea == IShowInformation.AREA_NONE)
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
        updateAeLockState();  // ちょっと暫定。。。
    }

    /**
     *
     *
     */
    @Override
    public void updateDriveMode()
    {
        if (driveModeArea == IShowInformation.AREA_NONE)
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
        if (whiteBalanceArea == IShowInformation.AREA_NONE)
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
        if (batteryLevelArea == IShowInformation.AREA_NONE)
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
        if (aeModeArea == IShowInformation.AREA_NONE)
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
        if (aeLockStateArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String message = "";
        String propetyValue = propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE);
        if (propetyValue != null)
        {
            if (propetyValue.equals("<AE_LOCK_STATE/LOCK>"))
            {
                message = "AE-L";
            }
            Log.v(TAG,"updateAeLockState() [" + message + "]" + propetyValue);
        }
        informationObject.setMessage(aeLockStateArea, Color.WHITE, message);
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
        Log.v(TAG,"updateWarning() " + value);
        if (warningArea == IShowInformation.AREA_NONE)
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
        if (colorToneArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.COLOR_TONE));
        if (propertyValue != null)
        {
            informationObject.setMessage(colorToneArea, Color.WHITE, propertyValue);
        }
    }

    @Override
    public void updateArtFilter()
    {
        if (artFilterArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ART_FILTER));
        if (propertyValue != null)
        {
            informationObject.setMessage(artFilterArea, Color.WHITE, /*Color.argb(255, 0x72, 0x39, 0x34),*/ propertyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateExposureCompensation(String value)
    {
        if (exposureCompensationArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String actualValue = value;
        if (actualValue == null)
        {
            actualValue = propertyProxy.getCameraPropertyValue(IOlyCameraProperty.EXPOSURE_COMPENSATION);
        }
        if (actualValue != null)
        {
            actualValue = propertyProxy.getCameraPropertyValueTitle(actualValue);
            if ("0.0".equals(actualValue))
            {
                informationObject.setMessage(exposureCompensationArea, Color.WHITE, "");
            }
            else
            {
                informationObject.setMessage(exposureCompensationArea, Color.WHITE, actualValue);
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
        if (focalLengthArea == IShowInformation.AREA_NONE)
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
        if (isoSensitivityArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String actualValue = propertyProxy.getCameraPropertyValueTitle(value);
        String prefix = "ISO";
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.ISO_SENSITIVITY));
        if ("Auto".equals(propertyValue))
        {
            prefix = "iso";
        }
        if (actualValue != null)
        {
            informationObject.setMessage(isoSensitivityArea, Color.WHITE, prefix + actualValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateShutterSpeed(String value)
    {
        if (shutterSpeedArea == IShowInformation.AREA_NONE)
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
        if (apertureArea == IShowInformation.AREA_NONE)
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
                artFilterArea = IShowInformation.AREA_NONE;
                updateColorTone();
                break;

            case "A":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "S":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "M":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "ART":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_8;
                updateArtFilter();
                break;

            case "iAuto":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "movie":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            default:
                takeModeArea = IShowInformation.AREA_1;                // 撮影モードの表示エリア指定
                shutterSpeedArea = IShowInformation.AREA_2;           // シャッタースピードの表示エリア指定
                apertureArea = IShowInformation.AREA_3;                // 絞り値の表示エリア指定
                isoSensitivityArea = IShowInformation.AREA_4;         // ISO感度の表示エリア指定
                focalLengthArea = IShowInformation.AREA_NONE;         // 焦点距離の表示エリア指定
                exposureCompensationArea = IShowInformation.AREA_6;  // 露出補正値の表示エリア指定
                warningArea = IShowInformation.AREA_7;                 // 警告の表示エリア指定
                batteryLevelArea = IShowInformation.AREA_NONE;        // バッテリの残量表示エリア指定
                whiteBalanceArea = IShowInformation.AREA_NONE;        // ホワイトバランスの表示エリア指定
                driveModeArea = IShowInformation.AREA_NONE;            // ドライブモードの表示エリア指定
                aeModeArea = IShowInformation.AREA_NONE;               // 測光モードの表示エリア指定
                aeLockStateArea = IShowInformation.AREA_6;             // AEロック状態の表示エリア指定
                colorToneArea = IShowInformation.AREA_8;               // 仕上がり・ピクチャーモードの表示エリア指定
                artFilterArea = IShowInformation.AREA_NONE;           // アートフィルターの表示エリア指定
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
        Log.v(TAG, "updateCameraStatusAll()");
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
