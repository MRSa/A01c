package jp.sfjp.gokigen.a01c.liveview;

public interface IMessageDrawer
{

    // メッセージを表示する位置
    enum MessageArea
    {
        UP,
        CENTER,
        LOW
    };

    int SIZE_STD = 16;
    int SIZE_LARGE = 24;
    int SIZE_BIG = 32;

   void setMessageToShow(MessageArea area, int color, int size, String message);

}
