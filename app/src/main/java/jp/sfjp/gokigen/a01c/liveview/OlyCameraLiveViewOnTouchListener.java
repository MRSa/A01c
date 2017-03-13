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
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraPropertyProvider;


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
     *   ボタン（オブジェクト）をクリックしたときの処理
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
                pushedButton1();
                break;
            case R.id.btn_2:
                pushedButton2();
                break;
            case R.id.btn_3:
                pushedButton3();
                break;
            case R.id.btn_4:
                pushedButton4();
                break;
            case R.id.btn_5:
                pushedButton5();
                break;
            case R.id.btn_6:
                pushedButton6();
                break;
            case R.id.text_1:
                pushedArea1();
                break;
            case R.id.text_2:
                pushedArea2();
                break;
            case R.id.text_3:
                pushedArea3();
                break;
            case R.id.text_4:
                pushedArea4();
                break;
            default:
                // その他...何もしない
                break;
        }
    }

    /**
     *   画面(ライブビュー部分)をタッチした時の処理
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
        return ((id == R.id.liveview)&&(touchedLiveViewArea(event)));
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
     *   以下、ボタンが押された時の処理... あとで切り離す。
     *   (撮影モードごとに処理を変えたい)
     ***************************************************************/

    private boolean touchedLiveViewArea(MotionEvent event)
    {
        return (camera.driveAutoFocus(event));
    }


    private void pushedButton1()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
        // グリッドの表示 / 非表示
        changeShowGrid();
    }


    private void pushedButton2()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
    }



    private void pushedButton3()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
    }


    private void pushedButton4()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
    }


    private void pushedButton5()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
    }


    private void pushedButton6()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
        pushShutterButton();
    }


    private void pushedArea1()
    {
        // 撮影モードの変更
        changeTakeMode();
    }

    private void pushedArea2()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
    }

    private void pushedArea3()
    {
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                //
                break;

            case "A":
                //
                break;

            case "S":
                //
                break;

            case "M":
                //
                break;

            case "ART":
                //
                break;

            case "iAuto":
            case "movie":
            default:
                //
                break;
        }
        changeAeLockMode();
    }

    /**
     *   テキスト表示エリア（設定画面を開くで固定）
     */
    private void pushedArea4()
    {
        showSettingsScreen();
    }

    /***************************************************************
     *   以下、具体的な機能の実行... あとで切り離す。
     *
     ***************************************************************/

    private String getTakeMode()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        return (propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE)));
    }


    /**
     *   撮影モードの変更指示
     *   (P > A > S > S > ART > iAuto > ...)
     */
    private void changeTakeMode()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        if (propetyValue == null)
        {
            // データ取得失敗
            return;
        }
        String targetMode = "<" + IOlyCameraProperty.TAKE_MODE;  // 変更先モード
        switch (propetyValue)
        {
            case "P":
                targetMode = targetMode + "/A>";
                break;

            case "A":
                    targetMode =  targetMode + "/S>";
                break;

            case "S":
                targetMode =  targetMode + "/M>";
                break;

            case "M":
                targetMode =  targetMode + "/ART>";
                break;

            case "ART":
                targetMode =  targetMode + "/iAuto>";
                break;

            case "iAuto":
            case "movie":
            default:
                targetMode =  targetMode + "/P>";
                break;
        }
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode);
        camera.unlockAutoFocus();

        //  撮影モードの更新
        camera.updateTakeMode();
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
     *   AE-Lock/Lock解除を行う
     *
     */
    private void changeAeLockMode()
    {
        camera.toggleAutoExposure();
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

    /**
     *   設定画面を開く
     *
     */
    private void showSettingsScreen()
    {
        // TBD...
    }
}
