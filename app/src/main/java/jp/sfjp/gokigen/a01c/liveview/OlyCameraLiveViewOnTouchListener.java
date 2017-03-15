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

    /**
     *   ボタン１が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton1()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON1;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID;
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
        dispatchAction(IShowInformation.BUTTON_1, preferences.getInt(preference_action_id, defaultAction));
    }


    /**
     *   ボタン２が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton2()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON2;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
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
        dispatchAction(IShowInformation.BUTTON_2, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   ボタン３が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton3()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON3;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
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
        dispatchAction(IShowInformation.BUTTON_3, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   ボタン４が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton4()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON4;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
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
        dispatchAction(IShowInformation.BUTTON_4, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   ボタン５が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton5()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON5;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
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
        dispatchAction(IShowInformation.BUTTON_5, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   ボタン６が押された時の機能を引き当て実行する
     *
     */
    private void pushedButton6()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTON6;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
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
        dispatchAction(IShowInformation.BUTTON_6, preferences.getInt(preference_action_id, defaultAction));
    }


    /**
     *   表示エリア１が押された時の機能を引き当て実行する
     *
     */
    private void pushedArea1()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA1;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE;
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
        dispatchAction(IShowInformation.AREA_1, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   表示エリア２が押された時の機能を引き当て実行する
     *
     */
    private void pushedArea2()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA2;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
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
        dispatchAction(IShowInformation.AREA_2, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   表示エリア３が押された時の機能を引き当て実行する
     *
     */
    private void pushedArea3()
    {
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA3;
        String takeMode = getTakeMode();
        int defaultAction = ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE;
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
        dispatchAction(IShowInformation.AREA_3, preferences.getInt(preference_action_id, defaultAction));
    }

    /**
     *   テキスト表示エリア４（機能は「設定画面を開く」で固定）
     */
    private void pushedArea4()
    {
        // 設定画面を開く
        dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS);
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
    private void dispatchAction(int objectId, int featureNumber)
    {
        if (featureNumber <= ICameraFeatureDispatcher.FEATURE_ACTION_NONE)
        {
            // 何もしない
            return;
        }

        // 機能実行の割り当て...
        switch (featureNumber)
        {
            case ICameraFeatureDispatcher.FEATURE_SETTINGS:
                // 設定画面を開く
                showSettingsScreen();
                break;
            case ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID:
                // グリッド標示ON/OFF
                changeShowGrid(objectId);
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT:
                // シャッター
                pushShutterButton();
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
        }
    }
}
