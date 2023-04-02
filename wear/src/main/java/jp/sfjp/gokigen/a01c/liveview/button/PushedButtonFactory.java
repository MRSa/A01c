package jp.sfjp.gokigen.a01c.liveview.button;

import android.content.Context;
import android.util.SparseArray;

import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;

public class PushedButtonFactory
{
    private final SparseArray<IPushedButton> buttonMap;

    public PushedButtonFactory(Context context, ICameraFeatureDispatcher dispatcher)
    {
        buttonMap = new SparseArray<>();
        buttonMap.put(R.id.btn_1, new PushedButton1(dispatcher));
        buttonMap.put(R.id.btn_2, new PushedButton2(dispatcher));
        buttonMap.put(R.id.btn_3, new PushedButton3(dispatcher));
        buttonMap.put(R.id.btn_4, new PushedButton4(dispatcher));
        buttonMap.put(R.id.btn_5, new PushedButton5(dispatcher));
        buttonMap.put(R.id.btn_6, new PushedButton6(dispatcher));
        buttonMap.put(R.id.btn_025, new PushedButton1(dispatcher));
        buttonMap.put(R.id.btn_026, new PushedButton6(dispatcher));
        buttonMap.put(R.id.text_1, new PushedArea1(dispatcher));
        buttonMap.put(R.id.text_2, new PushedArea2(dispatcher));
        buttonMap.put(R.id.text_3, new PushedArea3(dispatcher));
        buttonMap.put(R.id.text_4, new PushedArea4(dispatcher));
        buttonMap.put(R.id.btn_021, new PushedArea1(dispatcher));
        buttonMap.put(R.id.btn_022, new PushedArea2(dispatcher));
        buttonMap.put(R.id.btn_023, new PushedArea3(dispatcher));
        buttonMap.put(R.id.btn_024, new PushedArea4(dispatcher));
        buttonMap.put(R.id.liveview, new PushedLowerArea(dispatcher));
    }
    public SparseArray<IPushedButton> getButtonMap()
    {
        return (buttonMap);
    }
}
