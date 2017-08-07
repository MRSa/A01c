package jp.sfjp.gokigen.a01c.liveview.dialog;

public interface IDialogController
{
    void showDialog(IDialogDrawer dialog);
    void hideDialog();
    boolean touchedPosition(float posX, float posY);
}
