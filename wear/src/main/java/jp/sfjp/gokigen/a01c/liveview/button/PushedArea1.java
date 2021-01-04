package jp.sfjp.gokigen.a01c.liveview.button;

import androidx.annotation.NonNull;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;

class PushedArea1 implements IPushedButton
{
    private final ICameraFeatureDispatcher dispatcher;

    PushedArea1(@NonNull ICameraFeatureDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
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
        return (dispatcher.dispatchAction(IShowInformation.AREA_1, preference_action_id, defaultAction));
    }
}
