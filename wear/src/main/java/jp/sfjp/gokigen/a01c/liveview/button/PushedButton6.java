package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.ICameraFeatureDispatcher;

class PushedButton6 implements IPushedButton
{
    private final SharedPreferences preferences;
    private final ICameraFeatureDispatcher dispatcher;

    PushedButton6(Context context, ICameraFeatureDispatcher dispatcher)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
    {
        int defaultAction;// = ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
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
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_EXPOSURE : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_APERATURE : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_SHUTTER : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_WB : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_ART_FILTER : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction = ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE;
                break;

            case "iAuto":
            default:
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_SHOT_INTERVAL_5SEC : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;

                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_6, preferences.getInt(preference_action_id, defaultAction)));
    }
}
