package jp.sfjp.gokigen.a01c.liveview;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;


/**
 *
 *
 */
public class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener
{
    private final String TAG = toString();
    private final Context context;
    private IOlyCameraCoordinator camera = null;
    private IShowInformation statusDrawer = null;
    private ILiveImageStatusNotify liveImageView = null;
    private final SharedPreferences preferences;

    private boolean prohibitOperation = true;

    /**
     *
     */
    public OlyCameraLiveViewOnTouchListener(Context context)
    {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     *
     */
    public void prepareInterfaces(IOlyCameraCoordinator cameraCoordinator, IShowInformation statusDrawer, ILiveImageStatusNotify liveImageView)
    {
        this.camera = cameraCoordinator;
        this.statusDrawer = statusDrawer;
        this.liveImageView = liveImageView;
    }

    /**
     *
     */
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Log.v(TAG, "onClick() : " + id);
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onClick() : prohibit operation");
            return;
        }

        switch (id)
        {
            case R.id.btn_1:
                changeShowGrid();
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

    /**
     *
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        Log.v(TAG, "onTouch() : " + id);
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onTouch() : prohibit operation");
            return (false);
        }

        if (id == R.id.liveview)
        {
            return (camera.driveAutoFocus(event));
        }
        return (false);
    }

    /**
     *   操作の可否を設定する。
     *
     *    @param operation  true: 操作可能, false: 操作不可
     *
     */
    public void setEnableOperation(boolean operation)
    {
        prohibitOperation = !operation;
    }


    /***************************************************************
     *   ボタンが押された時の処理... あとで切り離す。
     *
     ***************************************************************/


    private void changeTakeMode()
    {



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

    /**
     *   グリッド表示の ON/OFFを切り替える
     *
     */
    private void changeShowGrid()
    {
        liveImageView.toggleShowGridFrame();
        updateGridStatusButton(IShowInformation.BUTTON_1);
    }

    /**
     *  グリッドフレームの表示・非表示ボタンを更新する
     *
     */
    private void updateGridStatusButton(int buttonId)
    {
        int btnResId;
        if (liveImageView.isShowGrid())
        {
            // グリッドがON状態、グリッドをOFFにするボタンを出す
            btnResId = R.drawable.btn_ic_grid_off;
        }
        else
        {
            //  グリッドがOFF状態、グリッドをONにするボタンを出す
            btnResId = R.drawable.btn_ic_grid_on;
        }
        statusDrawer.setButtonDrawable(buttonId, btnResId);
    }

}
