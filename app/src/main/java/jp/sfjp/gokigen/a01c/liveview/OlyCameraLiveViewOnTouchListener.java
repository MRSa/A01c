package jp.sfjp.gokigen.a01c.liveview;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.preference.ICameraPropertyAccessor;

/**
 *
 *
 */
public class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener
{
    private final String TAG = toString();
    private final Context context;
    private IOlyCameraCoordinator camera = null;
    private IStatusViewDrawer statusDrawer = null;
    private ILiveImageStatusNotify liveImageView = null;
    private final SharedPreferences preferences;

    public OlyCameraLiveViewOnTouchListener(Context context)
    {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void prepareInterfaces(IOlyCameraCoordinator cameraCoordinator, IStatusViewDrawer statusDrawer, ILiveImageStatusNotify liveImageView)
    {
        this.camera = cameraCoordinator;
        this.statusDrawer = statusDrawer;
        this.liveImageView = liveImageView;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Log.v(TAG, "onClick() : " + id);
        switch (id)
        {
            case R.id.btn_1:
                //pushShutterButton();
                break;

            case R.id.btn_2:
                //phoneShutter.onTouchedPreviewArea();
                break;

            case R.id.btn_3:
                //camera.toggleManualFocus();
                break;

            case R.id.btn_4:
                //camera.unlockAutoFocus();
                break;

            case R.id.btn_5:
                //camera.toggleAutoExposure();
                break;

            case R.id.btn_6:
                //camera.configure_expert();
                pushShutterButton();
                break;

            case R.id.text_1:
                //
                break;

            case R.id.text_2:
                //
                break;

            case R.id.text_3:
                //
                break;

            case R.id.text_4:
                //
                break;

            default:
                //
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        Log.v(TAG, "onTouch() : " + id);
        if (id == R.id.liveview)
        {
            return (camera.driveAutoFocus(event));
        }
        return (false);
    }

    /**
     *   シャッターボタンが押された！
     *   （現在は、連続撮影モードやムービー撮影についてはまだ非対応）
     */
    private void pushShutterButton()
    {
        // カメラ側のシャッターを押す
        camera.singleShot();
        {
            // パラメータが ON (ONLY CAMERA)の時は、スマートフォン側の撮影は行わない。
            // （本体カメラのシャッターを切らない時だけ、Toastで通知する。）
            Toast.makeText(context, R.string.shoot_camera, Toast.LENGTH_SHORT).show();
        }
    }
}
