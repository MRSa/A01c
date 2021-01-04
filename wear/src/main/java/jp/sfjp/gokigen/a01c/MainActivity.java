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
import android.widget.ImageButton;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import jp.sfjp.gokigen.a01c.liveview.CameraLiveImageView;
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl;
import jp.sfjp.gokigen.a01c.liveview.dialog.FavoriteSettingSelectionDialog;
import jp.sfjp.gokigen.a01c.liveview.dialog.IDialogDismissedNotifier;
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
public class MainActivity extends AppCompatActivity implements  IChangeScene, IShowInformation, ICameraStatusReceiver, IDialogDismissedNotifier
{
    private final String TAG = toString();
    static final int REQUEST_NEED_PERMISSIONS = 1010;
    //static final int COMMAND_MY_PROPERTY = 0x00000100;

    private PowerManager powerManager = null;
    private CameraLiveImageView liveView = null;
    private IOlyCameraCoordinator coordinator = null;
    private IMessageDrawer messageDrawer = null;
    private OlyCameraLiveViewOnTouchListener listener = null;
    private FavoriteSettingSelectionDialog selectionDialog = null;
    private Vibrator vibrator = null;
    private boolean cameraDisconnectedHappened = false;
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
            messageDrawer.setLevelGauge(coordinator.getLevelGauge());
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
            if (coordinator != null)
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
            if (liveView == null) {
                liveView = findViewById(R.id.liveview);
            }
            coordinator = null;
            coordinator = new OlyCameraCoordinator(this, liveView, this, this);
            coordinator.setLiveViewListener(new CameraLiveViewListenerImpl(liveView));
            listener = new OlyCameraLiveViewOnTouchListener(this, new FeatureDispatcher(this, this, coordinator, liveView), this);
            selectionDialog = new FavoriteSettingSelectionDialog(this, coordinator.getCameraPropertyLoadSaveOperations(), this);
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            coordinator.startLiveView();
            coordinator.setRecViewMode(false);
            listener.setEnableOperation(operation.ENABLE);
            setMessage(IShowInformation.AREA_C, Color.WHITE, "");
            coordinator.updateStatusAll();
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
     *  カメラに例外発生
     */
    @Override
    public void onCameraOccursException(String message, Exception e)
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
}
