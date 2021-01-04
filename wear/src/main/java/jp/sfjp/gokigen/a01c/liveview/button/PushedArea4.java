package jp.sfjp.gokigen.a01c.liveview.button;

import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;

class PushedArea4 implements IPushedButton
{
    private final ICameraFeatureDispatcher dispatcher;

    PushedArea4(ICameraFeatureDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean pushedButton(boolean isLongClick)
    {
        int defaultAction = ICameraFeatureDispatcher.FEATURE_SETTINGS;
        String preference_action_id = ICameraFeatureDispatcher.ACTION_AREA4 + ICameraFeatureDispatcher.ACTION_SECOND_CHOICE;
        if (isLongClick)
        {
            return (dispatcher.dispatchAction(IShowInformation.AREA_4, preference_action_id, defaultAction));
        }

        // 設定画面を開く...
        return (dispatcher.dispatchAction(IShowInformation.AREA_4, ICameraFeatureDispatcher.ACTION_AREA4, ICameraFeatureDispatcher.FEATURE_SETTINGS));
    }
}
