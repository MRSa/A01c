package jp.sfjp.gokigen.a01c;

/**
 *
 */
public interface ICameraStatusUpdateNotify
{
    void updateCameraStatus(final String message);
    void updatedExposureCompensation(String xv);
    void updateRemainBattery(final double percentage);
    void updateCaptureMode(final String message);

/*
    void updateDriveMode(String driveMode);
    void updateAeLockState(boolean isAeLocked);
    //void updateCameraStatus(String message);
    void updateLevelGauge(String orientation, float roll, float pitch);

    void updatedTakeMode(String mode);
    void updatedShutterSpeed(String tv);
    void updatedAperture(String av);
    void updatedMeteringMode(String meteringMode);
    void updatedWBMode(String wbMode);
    void updateFocusedStatus(boolean focused, boolean focusLocked);
    void updateIsoSensitivity(String sv);
    void updateWarning(String warning);
    void updateStorageStatus(String status);
*/
}
