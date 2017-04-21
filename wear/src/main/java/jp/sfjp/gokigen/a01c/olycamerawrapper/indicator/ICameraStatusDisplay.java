package jp.sfjp.gokigen.a01c.olycamerawrapper.indicator;

/**
 *
 */
public interface ICameraStatusDisplay
{
    void updateAperture(String value);
    void updateShutterSpeed(String value);
    void updateIsoSensitivity(String value);
    void updateFocalLength(String value);
    void updateExposureCompensation(String value);
    void updateWarning(String value);

    void updateColorTone();
    void updateArtFilter();
    void updateTakeMode();
    void updateDriveMode();
    void updateWhiteBalance();
    void updateBatteryLevel();
    void updateAeMode();
    void updateAeLockState();
    void updateCameraStatus();
    void updateCameraStatusAll();
    void updateCameraStatus(String message);

    void updateMovieQuality();
    void updateShortMovieLength();
    void updateMovieTakeMode();

    void updateStillFocusMode();
    void updateMovieFocusMode();
    void updateFullTimeAutoFocus();

}
