package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraStatusListener;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.preference.ICameraPropertyAccessor;

/**
 *   OLYCameraStatusListenerの実装
 *   (LiveViewFragment用)
 */
public class CameraStatusListenerImpl implements OLYCameraStatusListener, ICameraStatusSummary
{
    private final String TAG = this.toString();

    public static final String APERTURE_VALUE = "ActualApertureValue";
    public static final String SHUTTER_SPEED = "ActualShutterSpeed";
    public static final String EXPOSURE_COMPENSATION = "ActualExposureCompensation";
    public static final String ISO_SENSITIVITY = "ActualIsoSensitivity";
    public static final String RECORDABLEIMAGES = "RemainingRecordableImages";
    public static final String MEDIA_BUSY = "MediaBusy";
    public static final String MEDIA_ERROR = "MediaError";
    public static final String DETECT_FACES = "DetectedHumanFaces";
    public static final String FOCAL_LENGTH = "ActualFocalLength";
    public static final String ACTUAL_ISO_SENSITIVITY_WARNING = "ActualIsoSensitivityWarning";
    public static final String EXPOSURE_WARNING = "ExposureWarning";
    public static final String EXPOSURE_METERING_WARNING = "ExposureMeteringWarning";
    public static final String HIGH_TEMPERATURE_WARNING = "HighTemperatureWarning";
    public static final String LEVEL_GAUGE = "LevelGauge";
    public static final String LENS_MOUNT_STATUS = "LensMountStatus";
    public static final String MEDIA_MOUNT_STATUS = "MediaMountStatus";
    public static final String REMAINING_RECORDABLE_TIME = "RemainingRecordableTime";
    public static final String MINIMUM_FOCAL_LENGTH = "MinimumFocalLength";
    public static final String MAXIMUM_FOCAL_LENGTH = "MaximumFocalLength";

    private final ILevelGauge levelGauge;
    private final ICameraStatusDisplay display;
    private final Context context;

    /**
     *   コンストラクタ
     *
     */
    CameraStatusListenerImpl(Context context, ICameraStatusDisplay display, ILevelGauge levelGauge)
    {
        this.context = context;
        this.display = display;
        this.levelGauge = levelGauge;
    }

    /**
     *
     */
    @Override
    public void onUpdateStatus(OLYCamera camera, final String name)
    {
        if (name == null)
        {
            // name がないとき、何もしない
            return;
        }
        try
        {
            switch (name)
            {
                case APERTURE_VALUE:
                    // 絞り値の更新
                    display.updateAperture(camera.getCameraPropertyValueTitle(camera.getActualApertureValue()));
                    break;

                case SHUTTER_SPEED:
                    // シャッター速度の表示更新
                    display.updateShutterSpeed(camera.getCameraPropertyValueTitle(camera.getActualShutterSpeed()));
                    break;

                case ISO_SENSITIVITY:
                    // ISO感度の表示更新
                    display.updateIsoSensitivity(camera.getActualIsoSensitivity());
                    break;

                case FOCAL_LENGTH:
                    // 焦点距離の表示更新
                    display.updateFocalLength(String.format(Locale.ENGLISH, "%3.0fmm", camera.getActualFocalLength()));
                    break;

                case EXPOSURE_COMPENSATION:
                    // 露出補正値の表示更新
                    display.updateExposureCompensation(camera.getActualExposureCompensation());
                    break;

                case EXPOSURE_WARNING:
                case EXPOSURE_METERING_WARNING:
                case HIGH_TEMPERATURE_WARNING:
                case ACTUAL_ISO_SENSITIVITY_WARNING:
                case MEDIA_MOUNT_STATUS:
                    // ワーニング系のメッセージの表示更新
                    display.updateWarning(decideWarningMessage(camera));
                    break;

                case LEVEL_GAUGE:
                    // 水準器の確認
                    levelGauge.checkLevelGauge(camera);
                    break;

                case RECORDABLEIMAGES:
                case MEDIA_BUSY:
                case MEDIA_ERROR:
                case DETECT_FACES:
                case LENS_MOUNT_STATUS:
                case REMAINING_RECORDABLE_TIME:
                case MINIMUM_FOCAL_LENGTH:
                case MAXIMUM_FOCAL_LENGTH:
                default:
                    // 他の値が変わった場合には、ログだけ残して何もしない。
                    Log.v(TAG, "onUpdateStatus() : " + name);
                    display.updateCameraStatus(getCameraStatusMessage(camera, name));
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *   警告メッセージを生成する
     *
     */
    private String decideWarningMessage(OLYCamera camera)
    {
        String message = "";
        try
        {
            // 警告メッセージを生成
            if (camera.isHighTemperatureWarning())
            {
                message = message + context.getString(R.string.high_temperature_warning);
            }
            if ((camera.isExposureMeteringWarning())||(camera.isExposureWarning())||(camera.isActualIsoSensitivityWarning()))
            {
                // 露出警告
                message = message + " " + context.getString(R.string.exposure_metering_warning);
            }
            try
            {
                boolean mediaError = false;
                String media = camera.getMediaMountStatus();
                if (media != null)
                {
                    switch (media)
                    {
                        case "normal":
                            // エラーなし
                            break;
                        case "readonly":
                        case "cardfull":
                        case "unmount":
                        case "error":
                            mediaError = true;
                            break;
                        default:
                            Log.v(TAG, "getMediaMountStatus : " + media);
                            mediaError = true;
                            break;
                    }
                }
                if (mediaError)
                {
                    message = message + " " + context.getString(R.string.media_status_warning);
                }

            }
            catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            message = "";
        }
        return (message);
    }


    /**
     *  表示用のメッセージを生成する
     *
     */
    @Override
    public String getCameraStatusMessage(OLYCamera camera, String name)
    {
        return (name);
/*
        String message = name;
        String warn = "";
        try
        {
            // 警告メッセージを生成
            if (camera.isHighTemperatureWarning())
            {
                warn = warn + " " + context.getString(R.string.high_temperature_warning);
            }
            if (camera.isExposureMeteringWarning())
            {
                warn = warn + " " + context.getString(R.string.exposure_metering_warning);
            }
            if (camera.isActualIsoSensitivityWarning())
            {
                warn = warn + " " + context.getString(R.string.iso_sensitivity_warning);
            }

            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.add(IOlyCameraProperty.TAKE_MODE);
            treeSet.add(IOlyCameraProperty.WB_MODE);
            treeSet.add(IOlyCameraProperty.AE_MODE);
            treeSet.add(IOlyCameraProperty.EXPOSURE_COMPENSATION);
            Map<String, String> values = camera.getCameraPropertyValues(treeSet);
            //for (Map.Entry<String, String> entry : values.entrySet())
            //{
            //    Log.v(TAG, "STATUS : " + entry.getKey() + " : " + entry.getValue());
            //}
            String takeMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.TAKE_MODE));
            String wbMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.WB_MODE));
            String aeMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.AE_MODE));
            String aperture = camera.getCameraPropertyValueTitle(camera.getActualApertureValue());
            String iso = camera.getCameraPropertyValueTitle(camera.getActualIsoSensitivity());
            String shutter = camera.getCameraPropertyValueTitle(camera.getActualShutterSpeed());
            message = "  " + takeMode + " " + shutter + " F" + aperture + " ISO" + iso + " " + wbMode + " [" + aeMode + "]" + warn;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (message);
*/
    }
}
