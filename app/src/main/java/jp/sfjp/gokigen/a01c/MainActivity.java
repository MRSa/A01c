package jp.sfjp.gokigen.a01c;

import android.graphics.Color;
import android.os.Bundle;
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
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;
import jp.sfjp.gokigen.a01c.liveview.IMessageDrawer;
import jp.sfjp.gokigen.a01c.liveview.OlyCameraLiveViewOnTouchListener;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.olycamerawrapper.OlyCameraCoordinator;

/**
 *   メインのActivity
 *
 */
public class MainActivity extends WearableActivity implements  IChangeScene, IShowInformation, ICameraStatusReceiver
{
    private final String TAG = this.toString();
    static final int REQUEST_NEED_PERMISSIONS = 1010;

    private CameraLiveImageView liveView = null;
    private IOlyCameraCoordinator coordinator = null;
    private IMessageDrawer messageDrawer = null;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //  画面全体の設定
        setContentView(R.layout.activity_main);

        // WiFIアクセス権のオプトイン
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)||
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

        setupCameraCoordinator();
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

        //coordinator.stopLiveView();
        //exitApplication();
    }

    /**
     *
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
    }

    /**
     *
     *
     */
    @Override
    public void onStop()
    {
        super.onStop();
    }

    /**
     *
     *
     */
     @Override
     public void onEnterAmbient(Bundle ambientDetails)
     {
         super.onEnterAmbient(ambientDetails);
     }

    /**
     *
     *
     */
    @Override
    public void onExitAmbient()
    {
        super.onExitAmbient();
    }

    /**
     *
     *
     */
    @Override
    public void onUpdateAmbient()
    {
        super.onUpdateAmbient();
    }


    /**
     *   ボタンが押された、画面がタッチされた、、は、リスナクラスで処理するよう紐づける
     *
     */
    private void setupActionListener()
    {
        final OlyCameraLiveViewOnTouchListener listener = new OlyCameraLiveViewOnTouchListener(this);

        final ImageButton btn1 = (ImageButton) findViewById(R.id.btn_1);
        btn1.setOnClickListener(listener);

        final ImageButton btn2 = (ImageButton) findViewById(R.id.btn_2);
        btn2.setOnClickListener(listener);

        final ImageButton btn3 = (ImageButton) findViewById(R.id.btn_3);
        btn3.setOnClickListener(listener);

        final ImageButton btn4 = (ImageButton) findViewById(R.id.btn_4);
        btn4.setOnClickListener(listener);

        final ImageButton btn5 = (ImageButton) findViewById(R.id.btn_5);
        btn5.setOnClickListener(listener);

        final ImageButton btn6 = (ImageButton) findViewById(R.id.btn_6);
        btn6.setOnClickListener(listener);

        final TextView textArea1 = (TextView) findViewById(R.id.text_1);
        textArea1.setOnClickListener(listener);

        final TextView textArea2 = (TextView) findViewById(R.id.text_2);
        textArea2.setOnClickListener(listener);

        final TextView textArea3 = (TextView) findViewById(R.id.text_3);
        textArea3.setOnClickListener(listener);

        final TextView textArea4 = (TextView) findViewById(R.id.text_4);
        textArea4.setOnClickListener(listener);

        if (liveView == null)
        {
            liveView = (CameraLiveImageView) findViewById(R.id.liveview);
        }
        liveView.setOnTouchListener(listener);
        messageDrawer = liveView.getMessageDrawer();
        listener.prepareInterfaces(coordinator, liveView, liveView);
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

        // カメラの電源をOFFにしたうえで、アプリケーションを終了する。
        coordinator.getConnectionInterface().disconnect(true);
        finish();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onStatusNotify(String message)
    {
        setMessage(IShowInformation.AREA_C, Color.WHITE, message);
    }

    @Override
    public void onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()");
        coordinator.startLiveView();
        setMessage(IShowInformation.AREA_C, Color.WHITE, "");
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
    }

    /**
     *  カメラに例外発生
     */
    @Override
    public void onCameraOccursException(String message, Exception e)
    {
        setMessage(IShowInformation.AREA_C, Color.YELLOW, message);
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
        int id;
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
            default:
                id = R.id.text_4;
                break;
        }
        if (messageDrawer != null)
        {
            if (area == IShowInformation.AREA_5)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.UP, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_6)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.LOW, color, IMessageDrawer.SIZE_STD, message);
                return;
            }
            if (area == IShowInformation.AREA_C)
            {
                messageDrawer.setMessageToShow(IMessageDrawer.MessageArea.CENTER, color, IMessageDrawer.SIZE_LARGE, message);
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
}
