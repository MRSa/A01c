package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

public interface ILoadSaveCameraProperties
{
    void loadCameraSettings(final String id);
    void saveCameraSettings(final String id, final String name);
}
