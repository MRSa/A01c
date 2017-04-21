package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.ICameraFeatureDispatcher;

class PushedArea4 implements IPushedButton
{
    private final SharedPreferences preferences;
    private final ICameraFeatureDispatcher dispatcher;

    PushedArea4(Context context, ICameraFeatureDispatcher dispatcher)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_SETTINGS;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA4 + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        if (isLongClick)
        {
            return (dispatcher.dispatchAction(IShowInformation.AREA_4, preferences.getInt(preference_action_id, defaultAction)));
        }

        // 設定画面を開く
        return (dispatcher.dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
    }
}
