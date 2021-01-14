package jp.sfjp.gokigen.a01c.thetacamerawrapper.status;

import androidx.annotation.NonNull;

import java.util.List;

public interface ICameraStatus
{
    @NonNull List<String> getStatusList(final @NonNull String key);
    @NonNull String getStatus(final @NonNull String key);
    //void setStatus(final @NonNull String key, final @NonNull String value);


    String THETA_BATTERY_LEVEL = "batteryLevel";
    String THETA_CAPTURE_STATUS = "_captureStatus";
    String THETA_RECORDING_SEC = "_recordedTime";
    String THETA_BATTERY_STATE = "_batteryState";
    String THETA_CURRENT_API_LEVEL = "_apiVersion";
    String THETA_SHOOTING_FUNCTION = "_function";
    String THETA_CAMERA_ERROR = "_cameraError";

    String THETA_APERTURE = "aperture";
    String THETA_CAPTURE_MODE = "captureMode";
    String THETA_EXPOSURE_COMPENSATION = "exposureCompensation";
    String THETA_EXPOSURE_PROGRAM = "exposureProgram";
    String THETA_ISO_SENSITIVITY = "iso";
    String THETA_SHUTTER_SPEED = "shutterSpeed";
    String THETA_WHITE_BALANCE = "whiteBalance";

    /*
    String BATTERY = "battery";
    String STATE = "state";
    String FOCUS_MODE = "focusMode";
    String AF_MODE = "AFMode";

    String RESOLUTION = "reso";
    String DRIVE_MODE = "shootMode";
    String WHITE_BALANCE = "WBMode";

    String AE = "meteringMode";

    String AE_STATUS_MULTI = "multi";
    String AE_STATUS_ESP = "ESP";
    String AE_STATUS_SPOT = "spot";
    String AE_STATUS_PINPOINT = "Spot";
    String AE_STATUS_CENTER = "center";
    String AE_STATUS_CENTER2 = "Ctr-Weighted";

    String EFFECT = "effect";
    String TAKE_MODE = "exposureMode";
    String IMAGESIZE = "stillSize";
    String MOVIESIZE = "movieSize";

    String APERATURE = "av";
    String SHUTTER_SPEED = "tv";
    String ISO_SENSITIVITY = "sv";
    String EXPREV = "xv";
    String FLASH_XV = "flashxv";

    String TAKE_MODE_MOVIE = "movie";
 */
}
