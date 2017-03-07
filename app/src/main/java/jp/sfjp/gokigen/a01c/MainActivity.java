package jp.sfjp.gokigen.a01c;

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
import jp.sfjp.gokigen.a01c.liveview.OlyCameraLiveViewOnTouchListener;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.olycamerawrapper.OlyCameraCoordinator;

public class MainActivity extends WearableActivity implements  IChangeScene, ICameraStatusReceiver
{
    private final String TAG = this.toString();
    private final int REQUEST_NEED_PERMISSIONS = 1010;

    private CameraLiveImageView liveView = null;
    private IOlyCameraCoordinator coordinator = null;
    private CameraLiveViewListenerImpl liveViewListener = null;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //  画面全体の設定
        setContentView(R.layout.activity_main);
/**/
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
/**/
        setupCameraCoodinator();
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

       final  ImageButton btn5 = (ImageButton) findViewById(R.id.btn_5);
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
        listener.prepareInterfaces(coordinator, liveView, liveView);
    }

    private void setupCameraCoodinator()
    {
        if (liveView == null)
        {
            liveView = (CameraLiveImageView) findViewById(R.id.liveview);
        }
        coordinator = null;
        coordinator = new OlyCameraCoordinator(this, liveView, liveView, this);
        liveViewListener = new CameraLiveViewListenerImpl(liveView);
        coordinator.setLiveViewListener(liveViewListener);
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

    }

    @Override
    public void onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()");
        coordinator.startLiveView();
/*
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //coordinator.stopLiveView();
                //coordinator.setLiveViewListener(new CameraLiveViewListenerImpl(liveView));
                coordinator.startLiveView();
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
  */
    }

    @Override
    public void onCameraDisconnected()
    {

    }

    @Override
    public void onCameraOccursException(String message, Exception e)
    {

    }
}
