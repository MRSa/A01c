package jp.sfjp.gokigen.a01c.liveview;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;

/**
 *   画面がタッチ・クリックされた時の処理分岐
 *
 */
public class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener
{
    private final String TAG = toString();

    private final SharedPreferences preferences;
    private final ICameraFeatureDispatcher dispatcher;
    private boolean prohibitOperation = true;

    /**
     *   コンストラクタの整理
     *
     */
    public OlyCameraLiveViewOnTouchListener(Context context, ICameraFeatureDispatcher dispatcher)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dispatcher = dispatcher;
    }

    /**
     *   ボタン（オブジェクト）をクリックしたときの処理
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
            Log.v(TAG, "onLongClick() : prohibit operation");
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

            return (false);
        }
        // 現在のところ、タッチエリアの場合はオートフォーカス実行で固定
        return ((id == R.id.liveview)&&(dispatcher.dispatchAreaAction(event, ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS)));
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


    ///***************************************************************
    // *   以下、ボタンが押された時の処理... あとで切り離す。
    // *   (撮影モードごとに処理を変えたい)
    ///***************************************************************/

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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
            defaultAction = ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_LEVEL_GAUGE;
        }
        String takeMode = dispatcher.getTakeMode();
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

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_1, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN : ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN : ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN : ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN : ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN : ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_2, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_COLORTONE_UP : ICameraFeatureDispatcher.FEATURE_COLORTONE_UP;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_APERTURE_UP : ICameraFeatureDispatcher.FEATURE_APERTURE_UP;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP : ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP :  ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP : ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_3, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN : ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_4, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_APERTURE_UP : ICameraFeatureDispatcher.FEATURE_APERTURE_UP;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction = ICameraFeatureDispatcher.FEATURE_ACTION_NONE;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_5, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
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

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction = ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE;
                break;

            case "iAuto":
            default:
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                break;
        }
        Log.v(TAG, "SHUTTER : " + takeMode);
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_6, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
            defaultAction = ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE_REVERSE;
        }
        String takeMode = dispatcher.getTakeMode();
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

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.AREA_1, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
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

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.AREA_2, preferences.getInt(preference_action_id, defaultAction)));
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
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
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

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.AREA_3, preferences.getInt(preference_action_id, defaultAction)));
    }

    /**
     *   テキスト表示エリア４（機能は「設定画面を開く」で固定）
     */
    private boolean pushedArea4(boolean isLongClick)
    {
        if (isLongClick)
        {
            // 設定画面を開く
            return (dispatcher.dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
        }

        // 設定画面を開く
        return (dispatcher.dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
    }
}
