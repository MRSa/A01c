package jp.sfjp.gokigen.a01c.olycamerawrapper;


public interface IIndicatorControl
{
    // 撮影状態の記録
    enum shootingStatus
    {
        Unknown,
        Starting,
        Stopping,
    }

    void onAfLockUpdate(boolean isAfLocked);
    void onShootingStatusUpdate(shootingStatus status);

}
