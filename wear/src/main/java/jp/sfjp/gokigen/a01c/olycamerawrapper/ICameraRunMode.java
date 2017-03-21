package jp.sfjp.gokigen.a01c.olycamerawrapper;

public interface ICameraRunMode
{
    /** カメラの動作モード変更 **/
    void changeRunMode(boolean isRecording);
    boolean isRecordingMode();
}
