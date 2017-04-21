package jp.sfjp.gokigen.a01c.olycamerawrapper.listeners;

import android.util.Log;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.indicator.ICameraStatusDisplay;

/**
 *  OLYCameraPropertyListenerの実装
 *  (LiveViewFragment用)
 *
 */
public class CameraPropertyListenerImpl implements OLYCameraPropertyListener
{
    private final String TAG = this.toString();

    private final ICameraStatusDisplay display;

    public CameraPropertyListenerImpl(ICameraStatusDisplay parent)
    {
        this.display = parent;
    }

    @Override
    public void onUpdateCameraProperty(OLYCamera camera, final String name)
    {
        switch (name)
        {
            case IOlyCameraProperty.TAKE_MODE:
                display.updateTakeMode();
                break;

            case IOlyCameraProperty.DRIVE_MODE:
                display.updateDriveMode();
                break;

            case IOlyCameraProperty.WB_MODE:
                display.updateWhiteBalance();
                break;

            case IOlyCameraProperty.BATTERY_LEVEL:
                display.updateBatteryLevel();
                break;

            case IOlyCameraProperty.AE_MODE:
                display.updateAeMode();
                break;

            case IOlyCameraProperty.AE_LOCK_STATE:
                display.updateAeLockState();
                break;

            case IOlyCameraProperty.COLOR_TONE:
                display.updateColorTone();
                break;

            case IOlyCameraProperty.ART_FILTER:
                display.updateArtFilter();
                break;
            default:
                Log.v(TAG, "onUpdateCameraProperty() : " + name);
                display.updateCameraStatus();
                break;
        }
    }
}
