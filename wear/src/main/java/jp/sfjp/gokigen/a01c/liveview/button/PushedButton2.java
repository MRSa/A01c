package jp.sfjp.gokigen.a01c.liveview.button;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;

class PushedButton2 implements IPushedButton
{
    private final ICameraFeatureDispatcher dispatcher;

    PushedButton2(ICameraFeatureDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
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
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN;
                break;

            case "iAuto":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction = (isLongClick) ?  ICameraFeatureDispatcher.FEATURE_LENS_ZOOMOUT_2X : ICameraFeatureDispatcher.FEATURE_LENS_ZOOMOUT;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction = (isLongClick) ? ICameraFeatureDispatcher.FEATURE_WB_DOWN : ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN;
                break;

            default:
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.BUTTON_2, preference_action_id, defaultAction));
    }
}
