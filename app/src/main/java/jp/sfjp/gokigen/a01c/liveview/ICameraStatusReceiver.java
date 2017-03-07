package jp.sfjp.gokigen.a01c.liveview;

/**
 *
 *
 */
public interface ICameraStatusReceiver
{
    void onStatusNotify(String message);
    void onCameraConnected();
    void onCameraDisconnected();
    void onCameraOccursException(String message, Exception e);
}
