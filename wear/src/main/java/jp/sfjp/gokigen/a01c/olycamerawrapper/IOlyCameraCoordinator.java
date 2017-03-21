package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.view.MotionEvent;

import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

/**
 *
 *
 */
public interface IOlyCameraCoordinator
{
    /** ライブビュー関係 **/
    void changeLiveViewSize(String size);
    void setLiveViewListener(OLYCameraLiveViewListener listener);
    void startLiveView();
    void stopLiveView();

    /** 撮影モードの更新  **/
    void updateTakeMode();

    /** オートフォーカス機能の実行 **/
    boolean driveAutoFocus(MotionEvent event);
    void unlockAutoFocus();

    /** シングル撮影機能の実行 **/
    void singleShot();

    /** 撮影確認画像の設定 **/
    void setRecViewMode(boolean isRecViewMode);


    /** AE Lockの設定・解除、 AF/MFの切替え **/
    void toggleAutoExposure();
    void toggleManualFocus();

    /** カメラの状態取得 **/
    boolean isManualFocus();
    boolean isAFLock();
    boolean isAELock();

    /** カメラの状態変化リスナの設定 **/
    void setCameraStatusListener(OLYCameraStatusListener listener);

    /** カメラ状態の表示をすべて更新する **/
    void updateStatusAll();

    /** カメラの状態サマリ(のテキスト情報)を取得する **/
    String getCameraStatusSummary(ICameraStatusSummary decoder);

    // カメラプロパティアクセスインタフェース
    IOlyCameraPropertyProvider getCameraPropertyProvider();

    // カメラプロパティのロード・セーブインタフェース
    ILoadSaveCameraProperties getLoadSaveCameraProperties();

    // カメラの動作モード変更インタフェース
    ICameraRunMode getChangeRunModeExecutor();

    IOlyCameraConnection getConnectionInterface();

}
