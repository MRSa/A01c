package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.content.Context;

/**
 *   カメラの接続/切断
 *
 * Created by MRSa on 2017/02/28.
 */
public interface IOlyCameraConnection
{
    // WIFI 接続系
    void startWatchWifiStatus(Context context);
    void stopWatchWifiStatus(Context context);
    boolean isWatchWifiStatus();

    /** カメラ接続系 **/
    void disconnect(final boolean powerOff);
    void connect();
}
