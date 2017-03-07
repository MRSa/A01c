package jp.sfjp.gokigen.a01c.liveview;

public interface IStatusViewDrawer
{
    void updateStatusView(String message);
    void updateGridFrameStatus();
    void showFavoriteSettingDialog();
}
