package jp.sfjp.gokigen.a01c;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 *   カメラの接続/切断
 *
 */
public interface ICameraConnection
{
    enum CameraConnectionStatus
    {
        UNKNOWN,
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    };

    // WIFI 接続系
    void startWatchWifiStatus(@NonNull Context context);
    void stopWatchWifiStatus(@NonNull Context context);
    boolean isWatchWifiStatus();

    /** カメラ接続系 **/
    void disconnect(final boolean powerOff);
    void connect();
}
