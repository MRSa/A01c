package jp.sfjp.gokigen.a01c.liveview.dialog;

import android.graphics.Canvas;

/**
 *   ダイアログの描画インタフェース
 *
 */
public interface IDialogDrawer
{
    /** ダイアログの描画 **/
    void drawDialog(Canvas canvas);

    /** 画面タッチ情報 **/
    boolean touchedPosition(float posX, float posY);
}
