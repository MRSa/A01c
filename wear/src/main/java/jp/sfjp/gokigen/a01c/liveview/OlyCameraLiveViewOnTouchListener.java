package jp.sfjp.gokigen.a01c.liveview;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraPropertyProvider;

import static jp.sfjp.gokigen.a01c.liveview.ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;


/**
 *
 *
 */
public class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener
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
                pushedButton1(false);
                break;
            case R.id.btn_2:
                pushedButton2(false);
                break;
            case R.id.btn_3:
                pushedButton3(false);
                break;
            case R.id.btn_4:
                pushedButton4(false);
                break;
            case R.id.btn_5:
                pushedButton5(false);
                break;
            case R.id.btn_6:
                pushedButton6(false);
                break;
            case R.id.text_1:
                pushedArea1(false);
                break;
            case R.id.text_2:
                pushedArea2(false);
                break;
            case R.id.text_3:
                pushedArea3(false);
                break;
            case R.id.text_4:
                pushedArea4(false);
                break;
            default:
                // その他...何もしない
                break;
        }
    }

    /**
     *   長押しされたとき...
     *
     *
     */
    @Override
    public boolean onLongClick(View v)
    {
        boolean ret = false;
        int id = v.getId();
        Log.v(TAG, "onLongClick() : " + id);
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onClick() : prohibit operation");
            return (false);
        }
        switch (id)
        {
            case R.id.btn_1:
                ret = pushedButton1(true);
                break;
            case R.id.btn_2:
                ret = pushedButton2(true);
                break;
            case R.id.btn_3:
                ret = pushedButton3(true);
                break;
            case R.id.btn_4:
                ret = pushedButton4(true);
                break;
            case R.id.btn_5:
                ret = pushedButton5(true);
                break;
            case R.id.btn_6:
                ret = pushedButton6(true);
                break;
            case R.id.text_1:
                ret = pushedArea1(true);
                break;
            case R.id.text_2:
                ret = pushedArea2(true);
                break;
            case R.id.text_3:
                ret = pushedArea3(true);
                break;
            case R.id.text_4:
                ret = pushedArea4(true);
                break;
            default:
                // その他...何もしない
                break;
        }
        return (ret);
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

            // 実験... WIFIステート
            //Intent intent = new Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS");
            //context.startActivity(intent);
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

    /**
     *   ボタン１が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton1(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON1;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_1, preferences.getInt(preference_action_id, defaultAction)));
    }


    /**
     *   ボタン２が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton2(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON2;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction = ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction = ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_2, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   ボタン３が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton3(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON3;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction = ICameraFeatureDispatcher.FEATURE_COLORTONE_UP;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction = ICameraFeatureDispatcher.FEATURE_APERTURE_UP;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_3, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   ボタン４が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton4(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON4;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_4, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   ボタン５が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton5(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON5;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = ICameraFeatureDispatcher.FEATURE_APERTURE_UP;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_5, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   ボタン６が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedButton6(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON6;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.BUTTON_6, preferences.getInt(preference_action_id, defaultAction)));
    }


    /**
     *   表示エリア１が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedArea1(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA1;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.AREA_1, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   表示エリア２が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedArea2(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA2;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.AREA_2, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   表示エリア３が押された時の機能を引き当て実行する
     *
     */
    private boolean pushedArea3(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA3;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ACTION_SECOND_CHOICE;
        }
        String takeMode = getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatchAction(IShowInformation.AREA_3, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   テキスト表示エリア４（機能は「設定画面を開く」で固定）
     */
    private boolean pushedArea4(boolean isLongClick)
    {
        if (isLongClick)
        {
            // 設定画面を開く
            return (dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
        }

        // 設定画面を開く
        return (dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
    }

    /***************************************************************
     *   以下、具体的な機能の実行... ここから下は、あとで切り離す。
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
        Log.v(TAG, "changeTakeMode() " + targetMode);
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode);
        camera.unlockAutoFocus();

        //  撮影モードの更新
        //camera.updateTakeMode();
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
    private void changeShowGrid(int objectId)
    {
        liveImageView.toggleShowGridFrame();
        updateGridStatusButton(objectId);
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
     *　  露出補正を１段階下げる
     */
    private void changeExposureBiasValueDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.EXPOSURE_COMPENSATION);
    }

    /**
     *   露出補正を１段階あげる
     *
     */
    private void changeExposureBiasValueUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.EXPOSURE_COMPENSATION);
    }

    /**
     *　  絞り値を１段階下げる
     */
    private void changeApertureValueDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.APERTURE);
    }

    /**
     *   絞り値を１段階あげる
     *
     */
    private void changeApertureValueUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.APERTURE);
    }

    /**
     *　  シャッター速度を１段階下げる
     */
    private void changeShutterSpeedDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.SHUTTER_SPEED);
    }

    /**
     *   シャッター速度を１段階あげる
     *
     */
    private void changeShutterSpeedUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.SHUTTER_SPEED);
    }


    /**
     *　  仕上がり・ピクチャーモードを１段階下げる
     */
    private void changeColorToneDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.COLOR_TONE);
    }

    /**
     *   仕上がり・ピクチャーモードを１段階あげる
     *
     */
    private void changeColorToneUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.COLOR_TONE);
    }

    /**
     *   アートフィルターを１段階さげる
     *
     */
    private void changeArtFilterDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.ART_FILTER);
    }

    /**
     *   アートフィルターを１段階あげる
     *
     */
    private void changeArtFilterUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.ART_FILTER);
    }


    /**
     *   設定画面を開く
     *
     */
    private void showSettingsScreen()
    {
        // TBD...
    }

    /**
     *   指定した機能を実行する
     *
     * @param objectId　　　　　操作したオブジェクト
     * @param featureNumber　　操作する機能
     */
    private boolean dispatchAction(int objectId, int featureNumber)
    {
        if (featureNumber <= ICameraFeatureDispatcher.FEATURE_ACTION_NONE)
        {
            // 何もしない
            return (false);
        }

        // 機能実行の割り当て...
        int duration = IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT;
        switch (featureNumber)
        {
            case ICameraFeatureDispatcher.FEATURE_SETTINGS:
                // 設定画面を開く
                showSettingsScreen();
                duration =IShowInformation.VIBRATE_PATTERN_NONE;
                break;
            case ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID:
                // グリッド標示ON/OFF
                changeShowGrid(objectId);
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT:
                // シャッター
                pushShutterButton();
                duration =IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE;
                break;
            case ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE:
                // 撮影モードの変更
                changeTakeMode();
                break;
            case ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE:
                // AE LOCKのON/OFF切り替え
                changeAeLockMode();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN:
                // 露出補正を１段階下げる
                changeExposureBiasValueDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP:
                // 露出補正を１段階上げる
                changeExposureBiasValueUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN:
                // 絞り値を１段階下げる
                changeApertureValueDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_APERTURE_UP:
                // 絞り値を１段階上げる
                changeApertureValueUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN:
                // シャッター速度を１段階下げる
                changeShutterSpeedDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP:
                // シャッター速度を１段階上げる
                changeShutterSpeedUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN:
                // 仕上がり・ピクチャーモードを選択
                changeColorToneDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_COLORTONE_UP:
                // 仕上がり・ピクチャーモードを選択
                changeColorToneUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN:
                // アートフィルターを選択
                changeArtFilterDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP:
                // アートフィルターを選択
                changeArtFilterUp();
                break;
        }

        // コマンド実行完了後、ぶるぶるさせる
        statusDrawer.vibrate(duration);
        return (true);
    }
}
