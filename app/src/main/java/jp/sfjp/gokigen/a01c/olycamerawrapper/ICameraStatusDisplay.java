package jp.sfjp.gokigen.a01c.olycamerawrapper;

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


    void updateTakeMode();
    void updateDriveMode();
    void updateWhiteBalance();
    void updateBatteryLevel();
    void updateAeMode();
    void updateAeLockState();
    void updateCameraStatus();
    void updateCameraStatus(String message);
}
