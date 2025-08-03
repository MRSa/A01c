package jp.sfjp.gokigen.a01c;

public interface IWifiConnection
{
    void onConnectedToWifi();
    void onNetworkAvailable();
    void onNetworkLost();
    void onNetworkConnectionTimeout();
    void onError(String message);
}
