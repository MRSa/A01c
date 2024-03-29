package jp.sfjp.gokigen.a01c.liveview.button;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;


/**
 *   ライブビュー画面のオートフォーカスエリア外のボタン操作
 *
 */
class PushedLowerArea implements IPushedButton
{
    private final ICameraFeatureDispatcher dispatcher;
    PushedLowerArea(ICameraFeatureDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
    {
        int defaultAction;   // = ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_BUTTONL;
        if (isLongClick)
        {
            preference_action_id = preference_action_id + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        }
        String takeMode = dispatcher.getTakeMode();
        switch (takeMode)
        {
            case "P":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_P;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "A":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_A;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "S":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_S;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "M":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_M;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X14 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "ART":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_ART;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X10 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;

            case "Movie":
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_MOVIE;
                defaultAction = ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE;
                break;

            case "iAuto":
            default:
                preference_action_id = preference_action_id + ICameraFeatureDispatcher.MODE_IAUTO;
                defaultAction =  (isLongClick) ? ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X5 : ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT;
                break;
        }
        return (dispatcher.dispatchAction(IShowInformation.LOWER_AREA, preference_action_id, defaultAction));
    }
}
