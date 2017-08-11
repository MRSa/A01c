package jp.sfjp.gokigen.a01c.olycamerawrapper.indicator;

import android.graphics.Color;
import android.util.Log;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider;

/**
 *
 *
 */
public class CameraStatusDisplay implements  ICameraStatusDisplay
{
    private final String TAG = toString();
    private final IOlyCameraPropertyProvider propertyProxy;
    private final IShowInformation informationObject;

    // 表示エリア定義用...  : 将来的には preferenceにおいてカスタマイズ可能にするつもり
    private int shutterButtonId = R.id.btn_6;                          // シャッターボタンのボタンID
    private int takeModeArea = IShowInformation.AREA_1;                // 撮影モードの表示エリア指定
    private int shutterSpeedArea = IShowInformation.AREA_2;           // シャッタースピードの表示エリア指定
    private int apertureArea = IShowInformation.AREA_3;                // 絞り値の表示エリア指定
    private int isoSensitivityArea = IShowInformation.AREA_4;         // ISO感度の表示エリア指定
    private int focalLengthArea = IShowInformation.AREA_NONE;         // 焦点距離の表示エリア指定
    private int exposureCompensationArea = IShowInformation.AREA_6;  // 露出補正値の表示エリア指定
    private int warningArea = IShowInformation.AREA_7;                 // 警告の表示エリア指定
    private int batteryLevelArea = IShowInformation.AREA_NONE;       // バッテリの残量表示エリア指定
    private int whiteBalanceArea = IShowInformation.AREA_5;           // ホワイトバランスの表示エリア指定
    private int driveModeArea = IShowInformation.AREA_NONE;           // ドライブモードの表示エリア指定
    private int aeModeArea = IShowInformation.AREA_9;                  // 測光モードの表示エリア指定
    private int aeLockStateArea = IShowInformation.AREA_A;            // AEロック状態の表示エリア指定
    private int colorToneArea = IShowInformation.AREA_8;              // 仕上がり・ピクチャーモードの表示エリア指定
    private int artFilterArea = IShowInformation.AREA_NONE;           // アートフィルターの表示エリア指定
    private int movieQualityArea = IShowInformation.AREA_NONE;       // 動画撮影時のサイズ表示エリア指定
    private int shortMovieLengthArea = IShowInformation.AREA_NONE;       // 動画撮影時のショートムービー長さ表示エリア指定
    private int movieTakeModeArea = IShowInformation.AREA_NONE;      // 動画撮影時の撮影モード表示エリア指定

    private int stillFocusArea =  IShowInformation.AREA_NONE;           // スチル用フォーカスモード表示エリア指定 (AF/MF)
    private int movieFocusArea = IShowInformation.AREA_NONE;         // ムービー用フォーカスモード表示エリア指定
    private int fullTimeAfArea = IShowInformation.AREA_NONE;         // フルタイムAF表示エリア指定


    /**
     *
     *
     */
    public CameraStatusDisplay(IOlyCameraPropertyProvider propertyProxy, IShowInformation informationObject)
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
        Log.v(TAG, "updateTakeMode()");
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));

        updateButtonIcon(propertyValue);   // ボタンアイコンの更新
        if (takeModeArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        if (propertyValue != null)
        {
            informationObject.setMessage(takeModeArea, Color.WHITE, propertyValue);
            updateDisplayArea(propertyValue);
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
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.DRIVE_MODE));
        if (propertyValue != null)
        {
            informationObject.setMessage(driveModeArea, Color.WHITE, propertyValue);
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
        String propertyValue = propertyProxy.getCameraPropertyValue(IOlyCameraProperty.WB_MODE);
        if (propertyValue != null)
        {
            String message = "";
            if (!propertyValue.equals("<WB/WB_AUTO>"))
            {
                // WB Auto以外の時には、画面に設定値を表示する
                message = propertyProxy.getCameraPropertyValueTitle(propertyValue);
            }
            informationObject.setMessage(whiteBalanceArea, Color.argb(255, 240,240,240), /*Color.WHITE,*/ message);
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
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.BATTERY_LEVEL));
        if (propertyValue != null)
        {
            informationObject.setMessage(batteryLevelArea, Color.WHITE, propertyValue);
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
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_MODE));
        if (propertyValue != null)
        {
            if ("ESP".equals(propertyValue))
            {
                informationObject.setMessage(aeModeArea, Color.WHITE, "");
            }
            else
            {
                informationObject.setMessage(aeModeArea, Color.WHITE, propertyValue);
            }
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
        String propertyValue = propertyProxy.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE);
        if (propertyValue != null)
        {
            if (propertyValue.equals("<AE_LOCK_STATE/LOCK>"))
            {
                message = "AE-L";
            }
            Log.v(TAG,"updateAeLockState() [" + message + "]" + propertyValue);
        }
        informationObject.setMessage(aeLockStateArea, Color.argb(255, 255,204,0), message);
    }

    /**
     *
     *
     */
    @Override
    public void updateMovieQuality()
    {
        if (movieQualityArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.QUALITY_MOVIE));
        if (propertyValue != null)
        {
            informationObject.setMessage(movieQualityArea, Color.WHITE, propertyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateShortMovieLength()
    {
        if (shortMovieLengthArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.SHORT_MOVIE_RECORD_TIME));
        if (propertyValue != null)
        {
            informationObject.setMessage(shortMovieLengthArea, Color.WHITE, propertyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateMovieTakeMode()
    {
        if (movieTakeModeArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE_MOVIE));
        if (propertyValue != null)
        {
            informationObject.setMessage(movieTakeModeArea, Color.WHITE, propertyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateStillFocusMode()
    {
        if (stillFocusArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.FOCUS_STILL));
        if (propertyValue != null)
        {
            if ("S-AF".equals(propertyValue))
            {
                informationObject.setMessage(stillFocusArea, Color.WHITE, "");
            }
            else
            {
                informationObject.setMessage(stillFocusArea, Color.WHITE, propertyValue);
            }
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateMovieFocusMode()
    {
        if (movieFocusArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.FOCUS_MOVIE));
        if (propertyValue != null)
        {
            informationObject.setMessage(movieFocusArea, Color.WHITE, propertyValue);
        }
    }

    /**
     *
     *
     */
    @Override
    public void updateFullTimeAutoFocus()
    {
        if (fullTimeAfArea == IShowInformation.AREA_NONE)
        {
            return;
        }
        String propertyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.FULL_TIME_AF));
        if (propertyValue != null)
        {
            informationObject.setMessage(fullTimeAfArea, Color.WHITE, propertyValue);
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
            informationObject.setMessage(colorToneArea, Color.argb(255, 245,245,245), /*Color.WHITE, */ propertyValue);
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
            informationObject.setMessage(artFilterArea, Color.argb(255, 240,240,240), /*Color.WHITE, Color.argb(255, 0x72, 0x39, 0x34),*/ propertyValue);
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
            informationObject.setMessage(isoSensitivityArea, Color.WHITE, actualValue + prefix);
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
     *   ボタンのアイコンを更新する
     *
     */
    private void updateButtonIcon(String takeMode)
    {
        int btnResId;
        if (takeMode.equals("Movie"))
        {
            btnResId = R.drawable.btn_videocam;
        }
        else
        {
            btnResId = R.drawable.btn_ic_camera_alt;
        }
        if (shutterButtonId != 0)
        {
            informationObject.setButtonDrawable(shutterButtonId, btnResId);
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
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_9;
                updateColorTone();
                updateWhiteBalance();
                break;

            case "A":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_9;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "S":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_9;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "M":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_9;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                break;

            case "ART":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_8;
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_9;
                updateArtFilter();
                break;

            case "Movie":
                colorToneArea = IShowInformation.AREA_8;
                artFilterArea = IShowInformation.AREA_NONE;
                whiteBalanceArea = IShowInformation.AREA_5;
                aeModeArea = IShowInformation.AREA_NONE;
                updateWhiteBalance();
                updateColorTone();
                informationObject.setMessage(IShowInformation.AREA_9, Color.WHITE, "");
                break;

            case "iAuto":
                colorToneArea = IShowInformation.AREA_NONE;
                artFilterArea = IShowInformation.AREA_NONE;
                whiteBalanceArea = IShowInformation.AREA_NONE;
                aeModeArea = IShowInformation.AREA_9;
                informationObject.setMessage(IShowInformation.AREA_8, Color.WHITE, "");
                informationObject.setMessage(IShowInformation.AREA_5, Color.WHITE, "");
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
                whiteBalanceArea = IShowInformation.AREA_5;            // ホワイトバランスの表示エリア指定
                driveModeArea = IShowInformation.AREA_NONE;            // ドライブモードの表示エリア指定
                aeModeArea = IShowInformation.AREA_9;                   // 測光モードの表示エリア指定
                aeLockStateArea = IShowInformation.AREA_A;             // AEロック状態の表示エリア指定
                colorToneArea = IShowInformation.AREA_8;               // 仕上がり・ピクチャーモードの表示エリア指定
                artFilterArea = IShowInformation.AREA_NONE;           // アートフィルターの表示エリア指定
                movieQualityArea = IShowInformation.AREA_NONE;        // 動画モードの品質表示エリア指定
                shortMovieLengthArea = IShowInformation.AREA_NONE;    // 動画のショーとクリップ撮影秒数設定表示エリア指定
                movieTakeModeArea = IShowInformation.AREA_NONE;       // 動画モード時の撮影モード表示エリア指定
                stillFocusArea = IShowInformation.AREA_NONE;              // フォーカスモード静止画用の表示エリア指定
                movieFocusArea = IShowInformation.AREA_NONE;          // フォーカスモードムービー用の表示エリア指定
                fullTimeAfArea = IShowInformation.AREA_NONE;          // フルタイムAFモード表示エリア指定
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
        //updateStillFocusMode();
    }
}
