package jp.sfjp.gokigen.a01c.liveview;

import androidx.annotation.NonNull;

/**
 *
 *
 */
public interface ICameraStatusReceiver
{
    void onStatusNotify(String message);
    void onCameraConnected();
    void onCameraDisconnected();
    void onCameraConnectError(@NonNull String message);
    void onCameraOccursException(@NonNull String message, Exception e);
}
