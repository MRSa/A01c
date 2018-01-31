package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.util.SparseArray;

import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.ICameraFeatureDispatcher;

public class PushedButtonFactory
{
    //private final String TAG = toString();
    private SparseArray<IPushedButton> buttonMap;

    public PushedButtonFactory(Context context, ICameraFeatureDispatcher dispatcher)
    {
        buttonMap = new SparseArray<>();
        buttonMap.put(R.id.btn_1, new PushedButton1(context, dispatcher));
        buttonMap.put(R.id.btn_2, new PushedButton2(context, dispatcher));
        buttonMap.put(R.id.btn_3, new PushedButton3(context, dispatcher));
        buttonMap.put(R.id.btn_4, new PushedButton4(context, dispatcher));
        buttonMap.put(R.id.btn_5, new PushedButton5(context, dispatcher));
        buttonMap.put(R.id.btn_6, new PushedButton6(context, dispatcher));
        buttonMap.put(R.id.text_1, new PushedArea1(context, dispatcher));
        buttonMap.put(R.id.text_2, new PushedArea2(context, dispatcher));
        buttonMap.put(R.id.text_3, new PushedArea3(context, dispatcher));
        buttonMap.put(R.id.text_4, new PushedArea4(context, dispatcher));
        buttonMap.put(R.id.liveview, new PushedLowerArea(context, dispatcher));
    }

    public SparseArray<IPushedButton> getButtonMap()
    {
        return (buttonMap);
    }
}
