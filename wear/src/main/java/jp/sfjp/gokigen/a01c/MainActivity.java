package jp.sfjp.gokigen.a01c;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import jp.sfjp.gokigen.a01c.liveview.CameraLiveImageView;
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.FeatureDispatcher;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;
import jp.sfjp.gokigen.a01c.liveview.IMessageDrawer;
import jp.sfjp.gokigen.a01c.liveview.OlyCameraLiveViewOnTouchListener;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.olycamerawrapper.OlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.preference.IPreferenceCameraPropertyAccessor;

/**
 *   メインのActivity
 *
 */
public class MainActivity extends WearableActivity implements  IChangeScene, IShowInformation, ICameraStatusReceiver
{
    private final String TAG = toString();
    static final int REQUEST_NEED_PERMISSIONS = 1010;
    static final int COMMAND_MY_PROPERTY = 0x00000100;

    private CameraLiveImageView liveView = null;
    private IOlyCameraCoordinator coordinator = null;
    private IMessageDrawer messageDrawer = null;
    private OlyCameraLiveViewOnTouchListener listener = null;
    private Vibrator vibrator = null;
    private boolean cameraDisconnectedHappened = false;
    private boolean ambientMode = false;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        // Ambientモードを許してみる...
        setAmbientEnabled();

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

        if (!hasGps())
        {
            // GPS機能が搭載されていない場合...
            Log.d(TAG, "This hardware doesn't have GPS.");
            // Fall back to functionality that does not use location or
            // warn the user that location function is not available.
        }

        // バイブレータをつかまえる
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        setupCameraCoordinator();
        setupInitialButtonIcons();
        setupActionListener();
    }

    /**
     *
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume()");
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

    /**
     *
     *
     */
     @Override
     public void onEnterAmbient(Bundle ambientDetails)
     {
         super.onEnterAmbient(ambientDetails);
         Log.v(TAG, "onEnterAmbient()");
         ambientMode =true;
     }

    /**
     *
     *
     */
    @Override
    public void onExitAmbient()
    {
        super.onExitAmbient();
        Log.v(TAG, "onExitAmbient()");
        ambientMode = false;
    }

    /**
     *
     *
     */
    @Override
    public void onUpdateAmbient()
    {
        super.onUpdateAmbient();
        Log.v(TAG, "onUpdateAmbient()");
    }

    /**
     *   ボタンが押された、画面がタッチされた、、は、リスナクラスで処理するよう紐づける
     *
     */
    private void setupActionListener()
    {
        final ImageButton btn1 = (ImageButton) findViewById(R.id.btn_1);
        btn1.setOnClickListener(listener);
        btn1.setOnLongClickListener(listener);

        final ImageButton btn2 = (ImageButton) findViewById(R.id.btn_2);
        btn2.setOnClickListener(listener);
        btn2.setOnLongClickListener(listener);

        final ImageButton btn3 = (ImageButton) findViewById(R.id.btn_3);
        btn3.setOnClickListener(listener);
        btn3.setOnLongClickListener(listener);

        final ImageButton btn4 = (ImageButton) findViewById(R.id.btn_4);
        btn4.setOnClickListener(listener);
        btn4.setOnLongClickListener(listener);

        final ImageButton btn5 = (ImageButton) findViewById(R.id.btn_5);
        btn5.setOnClickListener(listener);
        btn5.setOnLongClickListener(listener);

        final ImageButton btn6 = (ImageButton) findViewById(R.id.btn_6);
        btn6.setOnClickListener(listener);
        btn6.setOnLongClickListener(listener);

        final TextView textArea1 = (TextView) findViewById(R.id.text_1);
        textArea1.setOnClickListener(listener);
        textArea1.setOnLongClickListener(listener);

        final TextView textArea2 = (TextView) findViewById(R.id.text_2);
        textArea2.setOnClickListener(listener);
        textArea2.setOnLongClickListener(listener);

        final TextView textArea3 = (TextView) findViewById(R.id.text_3);
        textArea3.setOnClickListener(listener);
        textArea3.setOnLongClickListener(listener);

        final TextView textArea4 = (TextView) findViewById(R.id.text_4);
        textArea4.setOnClickListener(listener);
        textArea4.setOnLongClickListener(listener);

        if (liveView == null)
        {
            liveView = (CameraLiveImageView) findViewById(R.id.liveview);
        }
        liveView.setOnTouchListener(listener);
        messageDrawer = liveView.getMessageDrawer();
        messageDrawer.setLevelGauge(coordinator.getLevelGauge());
    }

    /**
     *   ボタンアイコンの初期設定
     *
     */
    private void setupInitialButtonIcons()
    {
        if (coordinator != null)
        {
            int resId;
            SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean(IPreferenceCameraPropertyAccessor.SHOW_GRID_STATUS, true))
            {
                // ボタンをGrid OFFアイコンにする
                resId = R.drawable.btn_ic_grid_off;
            }
            else
            {
                // ボタンをGrid ONアイコンにする
                resId = R.drawable.btn_ic_grid_on;
            }
            setButtonDrawable(IShowInformation.BUTTON_1, resId);
        }
    }

    /**
     *   Intentを使ってWiFi設定画面を開く
     *
     */
    private boolean launchWifiSettingScreen()
    {
        try
        {
            // Wifi 設定画面を表示する... (SONY Smart Watch 3では開かないけど...)
            startActivity(new Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"));
            return (true);
        }
        catch (android.content.ActivityNotFoundException ex)
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
            catch (android.content.ActivityNotFoundException ex2)
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
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e2)
        {
            e2.printStackTrace();
        }
        return (false);
    }

    /**
     *   Olympus Cameraクラスとのやりとりをするクラスを準備する
     *   （カメラとの接続も、ここでスレッドを起こして開始する）
     */
    private void setupCameraCoordinator()
    {
        if (liveView == null)
        {
            liveView = (CameraLiveImageView) findViewById(R.id.liveview);
        }
        coordinator = null;
        coordinator = new OlyCameraCoordinator(this, liveView, this, this);
        coordinator.setLiveViewListener(new CameraLiveViewListenerImpl(liveView));
        listener = new OlyCameraLiveViewOnTouchListener(this, new FeatureDispatcher(this, coordinator, liveView), this);
        connectToCamera();
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
                coordinator.getConnectionInterface().connect();
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
        Log.v(TAG, "exitApplication()");
        if (ambientMode)
        {
            // アンビエントモードの時（≒自分でアプリを終了しなかったとき）は、何もしない
            // (接続したままとする)
            Log.v(TAG, "keep liveview.");
            return;
        }

        // ライブビューを停止させる
        coordinator.stopLiveView();

        //  パラメータを確認し、カメラの電源を切る
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IPreferenceCameraPropertyAccessor.EXIT_APPLICATION_WITH_DISCONNECT, true))
        {
            Log.v(TAG, "Shutdown camera...");

            // カメラの電源をOFFにする
            coordinator.getConnectionInterface().disconnect(true);
        }
        //finish();
        //finishAndRemoveTask();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     *   接続機能を確認する
     */
    @Override
    public boolean checkConnectionFeature(int id)
    {
        boolean ret = false;
        if (id == 0)
        {
            // Wifi 設定画面を開く
            ret = launchWifiSettingScreen();
        }
        return (ret);
    }

    /**
     *   接続状態を見る or 再接続する
     */
    @Override
    public boolean showConnectionStatus()
    {
        if ((!listener.isEnabledOperation())&&(cameraDisconnectedHappened))
        {
            // カメラが切断されたとき、再接続を指示する
            connectToCamera();
            cameraDisconnectedHappened = false;
            return (true);
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

        // ライブビューの開始 ＆ タッチ/ボタンの操作を可能にする
        coordinator.startLiveView();
        coordinator.setRecViewMode(false);
        listener.setEnableOperation(true, false);
        setMessage(IShowInformation.AREA_C, Color.WHITE, "");
        coordinator.updateStatusAll();
    }

    /**
     *   カメラとの接続が切れたとき...何もしない
     *
     */
    @Override
    public void onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()");
        setMessage(IShowInformation.AREA_C, Color.YELLOW, getString(R.string.camera_disconnected));
        listener.setEnableOperation(false, false);
        cameraDisconnectedHappened = true;
    }

    /**
     *  カメラに例外発生
     */
    @Override
    public void onCameraOccursException(String message, Exception e)
    {
        Log.v(TAG, "onCameraOccursException()");
        setMessage(IShowInformation.AREA_C, Color.YELLOW, message);
        listener.setEnableOperation(false, false);
        cameraDisconnectedHappened = true;
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
             public void run() {
                 final TextView textArea = (TextView) findViewById(areaId);
                 textArea.setTextColor(color);
                 textArea.setText(message);
                 textArea.invalidate();
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
            public void run() {
                final ImageButton button = (ImageButton) findViewById(areaId);
                button.setImageDrawable(getDrawable(labelId));
                button.invalidate();
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
                            vibrator.vibrate(75);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT:
                            vibrator.vibrate(20);
                            break;
                        case IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE:
                            long[] pattern = { 10, 25, 20, 25, 0 };
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
    public void setEnabledOperation(boolean operation, boolean suppress)
    {
        if (listener != null)
        {
            listener.setEnableOperation(operation, suppress);
        }
    }

    /**
     *   「お気に入り設定」表示画面を開く
     *
     */
    @Override
    public void showFavoriteSettingsDialog()
    {
/*
        // お気に入り設定画面を開く...
        LoadSaveMyCameraPropertyDialog dialog = new LoadSaveMyCameraPropertyDialog();
        dialog.setTargetFragment(this, COMMAND_MY_PROPERTY);
        dialog.setPropertyOperationsHolder(coordinator.getCameraPropertyLoadSaveOperations());
        dialog.show(this, "my_dialog");
*/
/*
        //  コマンドの実行確認ダイアログ... 動かん。。
        ConfirmationDialog dialog = new ConfirmationDialog(this);
        dialog.show(R.string.title_my_settings, R.string.message_none, new ConfirmationDialog.Callback()
        {
            @Override
            public void confirm()
            {
                vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_LONGLONG);
            }
        });
*/
    }


}
