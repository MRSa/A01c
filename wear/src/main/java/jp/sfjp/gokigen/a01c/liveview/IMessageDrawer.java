package jp.sfjp.gokigen.a01c.liveview;

import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge;

public interface IMessageDrawer
{

    // メッセージを表示する位置
    enum MessageArea
    {
        UPLEFT,
        UPRIGHT,
        CENTER,
        LOWLEFT,
        LOWRIGHT
    }

    int SIZE_STD = 16;
    int SIZE_LARGE = 24;
    int SIZE_BIG = 32;

    void setMessageToShow(MessageArea area, int color, int size, String message);
    void setLevelGauge(ILevelGauge levelGauge);

    ILevelGauge getLevelGauge();
}
