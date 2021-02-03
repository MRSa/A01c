package jp.sfjp.gokigen.a01c;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import jp.sfjp.gokigen.a01c.liveview.CameraLiveImageView;
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl;
import jp.sfjp.gokigen.a01c.liveview.dialog.FavoriteSettingSelectionDialog;
import jp.sfjp.gokigen.a01c.liveview.dialog.IDialogDismissedNotifier;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;
import jp.sfjp.gokigen.a01c.liveview.IMessageDrawer;
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewOnTouchListener;
import jp.sfjp.gokigen.a01c.liveview.glview.GokigenGLView;
import jp.sfjp.gokigen.a01c.olycamerawrapper.OlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.preference.IPreferenceCameraPropertyAccessor;
import jp.sfjp.gokigen.a01c.preference.PreferenceAccessWrapper;
import jp.sfjp.gokigen.a01c.thetacamerawrapper.ThetaCameraController;
import jp.sfjp.gokigen.a01c.utils.GestureParser;

/**
 *   メインのActivity
 *
 */
public class MainActivity extends AppCompatActivity implements  IChangeScene, IShowInformation, ICameraStatusReceiver, IDialogDismissedNotifier, IWifiConnection
{
    private final String TAG = toString();
    static final int REQUEST_NEED_PERMISSIONS = 1010;
    //static final int COMMAND_MY_PROPERTY = 0x00000100;

    private PreferenceAccessWrapper preferences = null;
    private PowerManager powerManager = null;
    private CameraLiveImageView liveView = null;
    private GokigenGLView glView = null;
    private GestureParser gestureParser = null;
    private ICameraController currentCoordinator = null;
    private ICameraController olyAirCoordinator = null;
    private ICameraController thetaCoordinator = null;
    private IMessageDrawer messageDrawer = null;
    private CameraLiveViewOnTouchListener listener = null;
    private FavoriteSettingSelectionDialog selectionDialog = null;
    private Vibrator vibrator = null;
    private boolean cameraDisconnectedHappened = false;
    private WifiConnection wifiConnection = null;
    //private boolean ambientMode = false;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        // Ambientモードを許してみる...
        //setAmbientEnabled();

        //  画面全体の設定
        setContentView(R.layout.activity_main);

        // WiFIアクセス権のオプトイン
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.INTERNET,
                    },
                    REQUEST_NEED_PERMISSIONS);
        }

        try
        {
            if (!hasGps())
            {
                // GPS機能が搭載されていない場合...
                Log.d(TAG, "This hardware doesn't have GPS.");
                // Fall back to functionality that does not use location or
                // warn the user that location function is not available.
            }

            // バイブレータをつかまえる
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            // パワーマネージャをつかまえる
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            setupCameraCoordinator();
            setupInitialButtonIcons();
            setupActionListener();

            wifiConnection = new WifiConnection(this, this);
            wifiConnection.requestNetwork();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume()");
        if (wifiConnection != null)
        {
            // ネットワークを要求する！
            wifiConnection.requestNetwork();
            wifiConnection.startWatchWifiStatus();
        }
    }

    /**
     *
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    /**
     *
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    /**
     *
     *
     */
    @Override
    public void onStop()
    {
        super.onStop();
        Log.v(TAG, "onStop()");
        exitApplication();
    }

/*
     @Override
     public void onEnterAmbient(Bundle ambientDetails)
     {
         super.onEnterAmbient(ambientDetails);
         Log.v(TAG, "onEnterAmbient()");
         ambientMode =true;
     }

    @Override
    public void onExitAmbient()
    {
        super.onExitAmbient();
        Log.v(TAG, "onExitAmbient()");
        ambientMode = false;
    }

    @Override
    public void onUpdateAmbient()
    {
        super.onUpdateAmbient();
        Log.v(TAG, "onUpdateAmbient()");
    }
*/

    /**
     *   ボタンが押された、画面がタッチされた、、は、リスナクラスで処理するよう紐づける
     *
     */
    private void setupActionListener()
    {
        try
        {
            final ImageButton btn1 = findViewById(R.id.btn_1);
            btn1.setOnClickListener(listener);
            btn1.setOnLongClickListener(listener);

            final ImageButton btn2 = findViewById(R.id.btn_2);
            btn2.setOnClickListener(listener);
            btn2.setOnLongClickListener(listener);

            final ImageButton btn3 = findViewById(R.id.btn_3);
            btn3.setOnClickListener(listener);
            btn3.setOnLongClickListener(listener);

            final ImageButton btn4 = findViewById(R.id.btn_4);
            btn4.setOnClickListener(listener);
            btn4.setOnLongClickListener(listener);

            final ImageButton btn5 = findViewById(R.id.btn_5);
            btn5.setOnClickListener(listener);
            btn5.setOnLongClickListener(listener);

            final ImageButton btn6 = findViewById(R.id.btn_6);
            btn6.setOnClickListener(listener);
            btn6.setOnLongClickListener(listener);

            final TextView textArea1 = findViewById(R.id.text_1);
            textArea1.setOnClickListener(listener);
            textArea1.setOnLongClickListener(listener);

            final TextView textArea2 = findViewById(R.id.text_2);
            textArea2.setOnClickListener(listener);
            textArea2.setOnLongClickListener(listener);

            final TextView textArea3 = findViewById(R.id.text_3);
            textArea3.setOnClickListener(listener);
            textArea3.setOnLongClickListener(listener);

            final TextView textArea4 = findViewById(R.id.text_4);
            textArea4.setOnClickListener(listener);
            textArea4.setOnLongClickListener(listener);

            if (liveView == null)
            {
                liveView = findViewById(R.id.liveview);
            }
            liveView.setOnTouchListener(listener);
            messageDrawer = liveView.getMessageDrawer();
            messageDrawer.setLevelGauge(currentCoordinator.getLevelGauge());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ボタンアイコンの初期設定
     *
     */
    private void setupInitialButtonIcons()
    {
        try
        {
            if (currentCoordinator != null)
            {
                int resId;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (preferences.getBoolean(IPreferenceCameraPropertyAccessor.SHOW_GRID_STATUS, true)) {
                    // ボタンをGrid OFFアイコンにする
                    resId = R.drawable.btn_ic_grid_off;
                } else {
                    // ボタンをGrid ONアイコンにする
                    resId = R.drawable.btn_ic_grid_on;
                }
                setButtonDrawable(IShowInformation.BUTTON_1, resId);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   Intentを使ってWiFi設定画面を開く
     *
     */
    private boolean launchWifiSettingScreen()
    {
        Log.v(TAG, "launchWifiSettingScreen()");
        try
        {
            // Wifi 設定画面を表示する... (SONY Smart Watch 3では開かないけど...)
            startActivity(new Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"));
            return (true);
        }
        catch (Exception ex)
        {
            try
            {
                Log.v(TAG, "launchWifiSettingScreen() : ACTION_WIFI_SETTINGS");
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return (true);
            }
            catch (Exception e)
            {
                Log.v(TAG, "android.content.ActivityNotFoundException... " + "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS");
                try
                {
                    // SONY Smart Watch 3で開く場合のIntent...
                    Intent intent = new Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS");
                    intent.setClassName("com.google.android.apps.wearable.settings", "com.google.android.clockwork.settings.wifi.WifiSettingsActivity");
                    startActivity(intent);
                    return (true);
                }
                catch (Exception ex2)
                {
                    try
                    {
                        // Wifi 設定画面を表示する...普通のAndroidの場合
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        return (true);
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                        try
                        {
                            // LG G Watch Rで開く場合のIntent...
                            Intent intent = new Intent("android.intent.action.MAIN");
                            intent.setClassName("com.google.android.apps.wearable.settings", "com.google.android.clockwork.settings.MainSettingsActivity");
                            startActivity(intent);
                            return (true);
                        }
                        catch (android.content.ActivityNotFoundException ex3)
                        {
                            ex3.printStackTrace();
                        }
                    }
                }
            }
        }
        return (false);
    }

    /**
     *   Olympus Cameraクラスとのやりとりをするクラスを準備する
     *   （カメラとの接続も、ここでスレッドを起こして開始する）
     */
    private void setupCameraCoordinator()
    {
        try
        {
            preferences = new PreferenceAccessWrapper(this);
            preferences.initialize();
            String connectionMethod = preferences.getString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE);
            if (liveView == null)
            {
                liveView = findViewById(R.id.liveview);
                liveView.setVisibility(View.VISIBLE);
            }
            CameraLiveViewListenerImpl liveViewListener = new CameraLiveViewListenerImpl(liveView);
            gestureParser = null;
            glView = null;
            boolean enableGlView = preferences.getBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false);
            if ((enableGlView)&&(connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)))
            {
                if (glView == null)
                {
                    // GL VIEW に切り替える
                    glView = findViewById(R.id.glview);
                }
                if (glView != null)
                {
                    // GL VIEW に切り替える
                    gestureParser = new GestureParser(getApplicationContext(), glView);
                    glView.setImageProvider(liveViewListener);
                    glView.setVisibility(View.VISIBLE);
                    liveView.setVisibility(View.GONE);
                }
            }
            olyAirCoordinator = new OlyCameraCoordinator(this, liveView, this, this);
            thetaCoordinator = new ThetaCameraController(this, this, this);
            currentCoordinator = (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) ? thetaCoordinator : olyAirCoordinator;
            currentCoordinator.setLiveViewListener(liveViewListener);
            listener = new CameraLiveViewOnTouchListener(this, currentCoordinator.getFeatureDispatcher(this, this, currentCoordinator, preferences, liveView), this);
            selectionDialog = new FavoriteSettingSelectionDialog(this, currentCoordinator.getCameraPropertyLoadSaveOperations(), this);
            connectToCamera();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   カメラと接続する
     *
     */
    private void connectToCamera()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                currentCoordinator.getConnectionInterface().connect();
            }
        });
        try
        {
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   カメラの電源をOFFいして、アプリを抜ける処理
     *
     */
    @Override
    public void exitApplication()
    {
        try
        {
            Log.v(TAG, "exitApplication()");
/*
            if (ambientMode)
            {
                // アンビエントモードの時（≒自分でアプリを終了しなかったとき）は、何もしない
                // (接続したままとする)
                Log.v(TAG, "keep liveview.");
                return;
            }
*/

            // パワーマネージャを確認し、interactive modeではない場合は、ライブビューも止めず、カメラの電源も切らない
            if ((powerManager != null) && (!powerManager.isInteractive()))
            {
                Log.v(TAG, "not interactive, keep liveview.");
                return;
            }

            // ライブビューを停止させる
            currentCoordinator.stopLiveView();

            // ステータス監視を止める
            ICameraStatusWatcher watcher = currentCoordinator.getStatusWatcher();
            if (watcher != null)
            {
                watcher.stopStatusWatch();
            }

            //  パラメータを確認し、カメラの電源を切る
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IPreferenceCameraPropertyAccessor.EXIT_APPLICATION_WITH_DISCONNECT, true))
            {
                Log.v(TAG, "Shutdown camera...");

                // カメラの電源をOFFにする
                currentCoordinator.getConnectionInterface().disconnect(true);
            }
            //finish();
            //finishAndRemoveTask();
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   接続機能を確認する
     */
    @Override
    public boolean checkConnectionFeature(int id, int btnId)
    {
        boolean ret = false;
        if (id == 0)
        {
            // Wifi 設定画面を開く
            ret = launchWifiSettingScreen();
        }
        else if (id == 1)
        {
            // 接続の変更を確認する
            changeConnectionMethod();
        }
        return (ret);
    }

    /**
     *   画面をタッチした場所を受信する
     *
     * @param posX  X座標位置 (0.0f - 1.0f)
     * @param posY  Y座標位置 (0.0f - 1.0f)
     * @return true / false
     */
    @Override
    public boolean touchedPosition(float posX, float posY)
    {
        Log.v(TAG, "touchedPosition (" + posX + ", " + posY);
        return ((liveView != null)&&(liveView.touchedPosition(posX, posY)));
    }

    /**
     *   接続状態を見る or 再接続する
     */
    @Override
    public boolean showConnectionStatus()
    {
        try
        {
            if ((listener.isEnabledOperation() == IShowInformation.operation.ONLY_CONNECT) && (cameraDisconnectedHappened)) {
                // カメラが切断されたとき、再接続を指示する
                connectToCamera();
                cameraDisconnectedHappened = false;
                return (true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    /**
     *
     */
    @Override
    public void onStatusNotify(String message)
    {
        setMessage(IShowInformation.AREA_C, Color.WHITE, message);
    }

    /**
     *
     */
    @Override
    public void onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()");
        try
        {
            // ライブビューの開始 ＆ タッチ/ボタンの操作を可能にする
            currentCoordinator.connectFinished();
            currentCoordinator.startLiveView();
            currentCoordinator.setRecViewMode(false);
            listener.setEnableOperation(operation.ENABLE);
            setMessage(IShowInformation.AREA_C, Color.WHITE, "");
            currentCoordinator.updateStatusAll();
            ICameraStatusWatcher watcher = currentCoordinator.getStatusWatcher();
            if (watcher != null)
            {
                watcher.startStatusWatch();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   カメラとの接続が切れたとき...何もしない
     *
     */
    @Override
    public void onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()");
        try
        {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, getString(R.string.camera_disconnected));
            listener.setEnableOperation(operation.ONLY_CONNECT);
            cameraDisconnectedHappened = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  カメラと接続失敗
     */
    @Override
    public void onCameraConnectError(@NonNull String message)
    {
        Log.v(TAG, "onCameraOccursException()");
        try
        {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message);
            listener.setEnableOperation(operation.ONLY_CONNECT);
            cameraDisconnectedHappened = true;
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    /**
     *  カメラに例外発生
     */
    @Override
    public void onCameraOccursException(@NonNull String message, Exception e)
    {
        Log.v(TAG, "onCameraOccursException()");
        try
        {
            setMessage(IShowInformation.AREA_C, Color.YELLOW, message);
            listener.setEnableOperation(operation.ONLY_CONNECT);
            cameraDisconnectedHappened = true;
        }
        catch (Exception ee)
        {
            e.printStackTrace();
            ee.printStackTrace();
        }
    }

    /**s
     *   メッセージの表示
     *
     * @param area    表示エリア (AREA_1 ～ AREA_6, AREA_C)
     * @param color　 表示色
     * @param message 表示するメッセージ
     */
    @Override
    public void setMessage(final int area, final int color, final String message)
    {
        int id = 0;
        switch (area)
        {
            case IShowInformation.AREA_1:
                id = R.id.text_1;
                break;
            case IShowInformation.AREA_2:
                id = R.id.text_2;
                break;
            case IShowInformation.AREA_3:
                id = R.id.text_3;
                break;
            case IShowInformation.AREA_4:
                id = R.id.text_4;
                break;
            case IShowInformation.AREA_NONE:
            default:
                // unknown
                break;
        }
        if (messageDrawer != null)
        {
            if (area == IShowInformation.AREA_C)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.CENTER, color, IMessageDrawer.SIZE_LARGE, message);
                return;
            }
            if (area == IShowInformation.AREA_5)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.UPLEFT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_6)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.LOWLEFT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_7)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.UPRIGHT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_8)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.LOWRIGHT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_9)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.UPCENTER, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_A)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.LOWCENTER, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_B)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.CENTERLEFT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_D)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.CENTERRIGHT, color, IMessageDrawer.SIZE_STD, message);
                return;
            }

            if (id == 0)
            {
                // 描画エリアが不定の場合...
                return;
            }
        }

        final int areaId = id;
        runOnUiThread(new Runnable()
        {
             @Override
             public void run()
             {
                 final TextView textArea = findViewById(areaId);
                 if (textArea != null)
                 {
                     textArea.setTextColor(color);
                     textArea.setText(message);
                     textArea.invalidate();
                 }
             }
        });
    }

    /**
     *   ボタンの表示イメージを変更する
     *
     * @param button  ボタンの場所
     * @param labelId 変更する内容
     */
    @Override
    public void setButtonDrawable(final int button, final int labelId)
    {
        int id;
        switch (button)
        {
            case IShowInformation.BUTTON_1:
                id = R.id.btn_1;
                break;
            case IShowInformation.BUTTON_2:
                id = R.id.btn_2;
                break;
            case IShowInformation.BUTTON_3:
                id = R.id.btn_3;
                break;
            case IShowInformation.BUTTON_4:
                id = R.id.btn_4;
                break;
            case IShowInformation.BUTTON_5:
                id = R.id.btn_5;
                break;
            case IShowInformation.BUTTON_6:
            default:
                id = R.id.btn_6;
                break;
        }

        final int areaId = id;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final ImageButton button = findViewById(areaId);
                    final Drawable drawTarget = ContextCompat.getDrawable(getApplicationContext(), labelId);
                    if (button != null)
                    {
                        button.setImageDrawable(drawTarget);
                        button.invalidate();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     * @return true GPS搭載, false GPS非搭載
     */
    private boolean hasGps()
    {
        return (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
    }

    /**
     *   タッチイベントをフックする
     *
     *
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        //Log.v(TAG, " dispatchTouchEvent() ");
        if (gestureParser != null)
        {
            //Log.v(TAG, " onTouch() ");
            gestureParser.onTouch(event);
        }
        return (super.dispatchTouchEvent(event));
    }

    /**
     *
     *
     */
    @Override
    public void vibrate(final int vibratePattern)
    {
        try
        {
            if ((vibrator == null)||(!vibrator.hasVibrator()))
            {
                return;
            }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    switch (vibratePattern)
                    {
                        case IShowInformation.VIBRATE_PATTERN_SIMPLE_LONGLONG:
                            vibrator.vibrate(300);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SIMPLE_LONG:
                            vibrator.vibrate(150);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE:
                            vibrator.vibrate(80);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT:
                            vibrator.vibrate(30);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE:
                            long[] pattern = { 10, 35, 30, 35, 0 };
                            vibrator.vibrate(pattern, -1);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_NONE:
                        default:
                            // ぶるぶるしない
                            break;
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setEnabledOperation(IShowInformation.operation operation)
    {
        if (listener != null)
        {
            listener.setEnableOperation(operation);
        }
    }

    /**
     *   「お気に入り設定」表示画面を開く
     *
     */
    @Override
    public void showFavoriteSettingsDialog()
    {
        if ((liveView != null)&&(listener != null)&&(listener.isEnabledOperation() != operation.ONLY_CONNECT))
        {
            listener.setEnableOperation(operation.ENABLE_ONLY_TOUCHED_POSITION);
            liveView.showDialog(selectionDialog);
        }
    }

    @Override
    public void showToast(final int rscId, @NonNull final String appendMessage, final int duration)
    {
        try
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        String message = (rscId != 0) ? getString(rscId) + appendMessage : appendMessage;
                        Toast.makeText(getApplicationContext(), message, duration).show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void invalidate()
    {
        try
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (liveView != null)
                    {
                        liveView.invalidate();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   「お気に入り設定」表示画面を閉じる
     *
     */
    @Override
    public void dialogDismissed(boolean isExecuted)
    {
        try
        {
            if ((liveView != null) && (listener != null))
            {
                liveView.hideDialog();
                listener.setEnableOperation(operation.ENABLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateConnectionMethodMessage()
    {
        try
        {
            String connectionMethod = preferences.getString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE);
            int methodId = (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA)) ? R.string.connection_method_theta : R.string.connection_method_opc;
            setMessage(IShowInformation.AREA_7, Color.MAGENTA, getString(methodId));
            if (liveView == null)
            {
                liveView = findViewById(R.id.liveview);
            }
            liveView.setupInitialBackgroundImage(this);
            liveView.setVisibility(View.VISIBLE);
            liveView.invalidate();

            if (glView != null)
            {
                glView.setVisibility(View.GONE);
                glView = null;
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateConnectionMethod(String parameter, ICameraController method)
    {
        try
        {
            currentCoordinator = method;
            preferences.putString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, parameter);
            vibrate(IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   接続方式を変更するか確認する (OPC ⇔ THETA)
     *
     */
    private void changeConnectionMethod()
    {
        final AppCompatActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    int titleId = R.string.change_title_from_opc_to_theta;
                    int messageId = R.string.change_message_from_opc_to_theta;
                    boolean method = false;
                    String connectionMethod = preferences.getString(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD, IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE);
                    if (connectionMethod.contains(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA))
                    {
                        titleId = R.string.change_title_from_theta_to_opc;
                        messageId = R.string.change_message_from_theta_to_opc;
                        method = true;
                    }
                    final boolean isTheta = method;
                    ConfirmationDialog confirmation = new ConfirmationDialog(activity);
                    confirmation.show(titleId, messageId, new ConfirmationDialog.Callback() {
                        @Override
                        public void confirm() {
                            Log.v(TAG, " --- CONFIRMED! --- (theta:" + isTheta + ")");
                            if (isTheta)
                            {
                                // 接続方式を OPC に切り替える
                                updateConnectionMethod(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_OPC, olyAirCoordinator);
                            }
                            else
                            {
                                // 接続方式を Theta に切り替える
                                updateConnectionMethod(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_THETA, thetaCoordinator);
                            }
                            updateConnectionMethodMessage();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onConnectedToWifi()
    {
        try
        {
            Log.v(TAG, "onConnectedToWifi()");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
