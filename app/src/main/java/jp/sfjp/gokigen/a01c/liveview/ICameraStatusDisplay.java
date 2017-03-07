package jp.sfjp.gokigen.a01c.liveview;

/**
 *
 */
public interface ICameraStatusDisplay
{
    void updateTakeMode();
    void updateDriveMode();
    void updateWhiteBalance();
    void updateBatteryLevel();
    void updateAeMode();
    void updateAeLockState();
    void updateCameraStatus();
    void updateCameraStatus(String message);
}
