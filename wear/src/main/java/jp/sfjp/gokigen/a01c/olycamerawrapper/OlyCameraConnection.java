package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.sfjp.gokigen.a01c.ICameraConnection;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;

/**
 *   カメラの接続・切断処理クラス
 *
 *     https://github.com/googlesamples/android-WearHighBandwidthNetworking/blob/master/Wearable/src/main/java/com/example/android/wearable/wear/wearhighbandwidthnetworking/MainActivity.java
 *
 * Created by MRSa on 2017/02/28.
 */
class OlyCameraConnection implements ICameraConnection, OLYCameraConnectionListener
{
    private final String TAG = toString();
    private final Activity context;
    private final OLYCamera camera;
    private final Executor cameraExecutor = Executors.newFixedThreadPool(1);
    private final BroadcastReceiver connectionReceiver;
    private final ICameraStatusReceiver statusReceiver;

    private boolean isWatchingWifiStatus = false;

    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback = null;

    // Handler for dealing with network connection timeouts.
    private final Handler networkConnectionTimeoutHandler;

    // Message to notify the network request timout handler that too much time has passed.
    private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    // These constants are used by setUiState() to determine what information to display in the UI,
    // as this app reuses UI components for the various states of the app, which is dependent on
    // the state of the network.
    static final int UI_STATE_REQUEST_NETWORK = 1;
    static final int UI_STATE_REQUESTING_NETWORK = 2;
    static final int UI_STATE_NETWORK_CONNECTED = 3;
    static final int UI_STATE_CONNECTION_TIMEOUT = 4;
    static final int MIN_BANDWIDTH_KBPS = 160;

    // How long the app should wait trying to connect to a sufficient high-bandwidth network before
    // asking the user to add a new Wi-Fi network.
    private static final long NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(2000);

    // The minimum network bandwidth required by the app for high-bandwidth operations.
    private static final int MIN_NETWORK_BANDWIDTH_KBPS = 10000;

    // Intent action for sending the user directly to the add Wi-Fi network activity.
    private static final String ACTION_ADD_NETWORK_SETTINGS = "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS";


    /**
     *    コンストラクタ
     *
     */
    OlyCameraConnection(Activity context, OLYCamera camera, ICameraStatusReceiver statusReceiver)
    {
        Log.v(TAG, "OlyCameraConnection()");
        this.context = context;
        this.camera = camera;
        this.connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkConnectionTimeoutHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Log.d(TAG, "Network connection timeout");
                        //setUiState(UI_STATE_CONNECTION_TIMEOUT);
                        unregisterNetworkCallback();
                        break;
                }
            }
        };
        requestHighBandwidthNetwork();
/*
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null)
        {
            int bandwidth = connectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            if (bandwidth < MIN_BANDWIDTH_KBPS)
            {
                // Request a high-bandwidth network
            }
        }
        else
        {
            // You already are on a high-bandwidth network, so start your network request
        }
*/

        this.statusReceiver = statusReceiver;
        connectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                onReceiveBroadcastOfConnection(context, intent);
            }
        };

    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    private boolean isNetworkHighBandwidth()
    {
        Network network = connectivityManager.getBoundNetworkForProcess();
        network = network == null ? connectivityManager.getActiveNetwork() : network;
        if (network == null)
        {
            return (false);
        }

        // requires android.permission.ACCESS_NETWORK_STATE
        int bandwidth = connectivityManager.getNetworkCapabilities(network).getLinkDownstreamBandwidthKbps();
        return (bandwidth >= MIN_NETWORK_BANDWIDTH_KBPS);
    }



    private void unregisterNetworkCallback()
    {
        if (networkCallback != null)
        {
            Log.d(TAG, "Unregistering network callback");
            connectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
    }

    private void releaseHighBandwidthNetwork()
    {
        connectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }

    private void addWifiNetwork()
    {
        // requires android.permission.CHANGE_WIFI_STATE
        context.getApplicationContext().startActivity(new Intent(ACTION_ADD_NETWORK_SETTINGS));
    }


    /**
     *   Wifiが使える状態だったら、カメラと接続して動作するよ
     *
     */
    private void onReceiveBroadcastOfConnection(Context context, Intent intent)
    {
        statusReceiver.onStatusNotify(context.getString(R.string.connect_check_wifi));
        Log.v(TAG,context.getString(R.string.connect_check_wifi));

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiManager.isWifiEnabled() && info != null && info.getNetworkId() != -1)
            {
                // カメラとの接続処理を行う
                connectToCamera();
            }
        }
    }


    /**
     * Wifi接続状態の監視
     * (接続の実処理は onReceiveBroadcastOfConnection() で実施)
     */
    @Override
    public void startWatchWifiStatus(@NonNull Context context)
    {
        Log.v(TAG, "startWatchWifiStatus()");
        statusReceiver.onStatusNotify("prepare");

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(connectionReceiver, filter);
        isWatchingWifiStatus = true;
    }

    /**
     * Wifi接続状態の監視終了
     */
    @Override
    public void stopWatchWifiStatus(@NonNull Context context)
    {
        Log.v(TAG, "stopWatchWifiStatus()");
        context.unregisterReceiver(connectionReceiver);
        isWatchingWifiStatus = false;
        disconnect(false);
    }

    /**
     * Wifi接続状態の監視処理を行っているかどうか
     *
     * @return true : 監視中 / false : 停止中
     */
    @Override
    public boolean isWatchWifiStatus() {
        return (isWatchingWifiStatus);
    }

    /**
     * 　 カメラとの接続を解除する
     *
     * @param powerOff 真ならカメラの電源オフを伴う
     */
    @Override
    public void disconnect(final boolean powerOff)
    {
        Log.v(TAG, "disconnect()");
        disconnectFromCamera(powerOff);
        statusReceiver.onCameraDisconnected();
    }

    /**
     * カメラとの再接続を指示する
     */
    @Override
    public void connect() {
        connectToCamera();
    }

    /**
     * カメラの通信状態変化を監視するためのインターフェース
     *
     * @param camera 例外が発生した OLYCamera
     * @param e      カメラクラスの例外
     */
    @Override
    public void onDisconnectedByError(OLYCamera camera, OLYCameraKitException e)
    {
        // カメラが切れた時に通知する
        statusReceiver.onCameraDisconnected();
    }

    /**
     * カメラとの切断処理
     */
    private void disconnectFromCamera(final boolean powerOff)
    {
        Log.v(TAG, "disconnectFromCamera()");
        try
        {
            cameraExecutor.execute(new CameraDisconnectSequence(camera, powerOff));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * カメラとの接続処理
     */
    private void connectToCamera()
    {
        Log.v(TAG, "connectToCamera()");
        try
        {
            cameraExecutor.execute(new CameraConnectSequence(context, camera, statusReceiver));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void requestHighBandwidthNetwork()
    {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
        unregisterNetworkCallback();

        Log.d(TAG, "requestHighBandwidthNetwork(): Requesting high-bandwidth network");

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(final Network network)
            {
                networkConnectionTimeoutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT);
                context.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        // requires android.permission.INTERNET
                        if (!connectivityManager.bindProcessToNetwork(network))
                        {
                            Log.e(TAG, "ConnectivityManager.bindProcessToNetwork()  requires android.permission.INTERNET");
                        }
                        else
                        {
                            Log.d(TAG, "Network available");
                        }
                    }
                });
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities)
            {
                context.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d(TAG, "Network capabilities changed");
                    }
                });
            }

            @Override
            public void onLost(Network network)
            {
                Log.d(TAG, "Network lost");
            }
        };

        // requires android.permission.CHANGE_NETWORK_STATE
        connectivityManager.requestNetwork(request, networkCallback);

        networkConnectionTimeoutHandler.sendMessageDelayed(networkConnectionTimeoutHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT), NETWORK_CONNECTIVITY_TIMEOUT_MS);
    }
}
