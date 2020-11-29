package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.ICameraFeatureDispatcher;

class PushedButton5 implements IPushedButton
{
    private final SharedPreferences preferences;
    private final ICameraFeatureDispatcher dispatcher;

    PushedButton5(Context context, ICameraFeatureDispatcher dispatcher)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
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
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ISO_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ISO_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ISO_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ISO_UP : ICameraFeatureDispatcher.FEATURE_APERTURE_UP;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_ISO_UP : ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOM_CHANGE : ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOMIN;
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
}
