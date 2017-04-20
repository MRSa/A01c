package jp.sfjp.gokigen.a01c.liveview.button;

public interface IPushedButton
{
    // ボタンが押されたとき（長押しかどうか）
    boolean pushedButton(boolean isLongClick);
}
