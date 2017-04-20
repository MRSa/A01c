package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.liveview.ICameraFeatureDispatcher;

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
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_6, preferences.getInt(preference_action_id, defaultAction)));
    }
}
