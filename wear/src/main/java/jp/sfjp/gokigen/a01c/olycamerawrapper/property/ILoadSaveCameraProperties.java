package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

public interface ILoadSaveCameraProperties
{
    int MAX_STORE_PROPERTIES = 256;   // お気に入り設定の最大記憶数...
    String TITLE_KEY = "CameraPropTitleKey";
    String DATE_KEY = "CameraPropDateTime";

    void loadCameraSettings(final String id);
    void saveCameraSettings(final String id, final String name);
}
