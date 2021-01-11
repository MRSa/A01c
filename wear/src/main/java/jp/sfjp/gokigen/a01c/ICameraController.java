package jp.sfjp.gokigen.a01c;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceDataStore;

import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl;
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify;
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraRunMode;
import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations;
import jp.sfjp.gokigen.a01c.preference.PreferenceAccessWrapper;

/**
 *
 *
 */
public interface ICameraController
{
    /** ライブビュー関係 **/
    void setLiveViewListener(@NonNull CameraLiveViewListenerImpl listener);
    void changeLiveViewSize(String size);
    void startLiveView();
    void stopLiveView();

    /** 撮影モードの更新  **/
    void updateTakeMode();

    /** オートフォーカス機能の実行 **/
    boolean driveAutoFocus(MotionEvent event);
    void unlockAutoFocus();

    /** ポイントがオートフォーカス可能なエリアかどうかチェックする **/
    boolean isContainsAutoFocusPoint(MotionEvent event);  // trueならオートフォーカス可能

    /** シングル撮影機能の実行 **/
    void singleShot();

    /** ムービー撮影機能の実行(開始・終了) **/
    void movieControl();

    /** インターバル＆ブラケッティング撮影の実行 **/
    void bracketingShot(int bracketingStyle, int bracketingCount, int durationSeconds);

    /** 撮影確認画像の設定 **/
    void setRecViewMode(boolean isRecViewMode);


    /** AE Lockの設定・解除、 AF/MFの切替え **/
    void toggleAutoExposure();
    void toggleManualFocus();

    /** カメラの状態取得 **/
    boolean isManualFocus();
    boolean isAFLock();
    boolean isAELock();

    ///** カメラの状態変化リスナの設定 **/
    //void setCameraStatusListener(OLYCameraStatusListener listener);

    /** カメラ状態の表示をすべて更新する **/
    void updateStatusAll();

    ///** カメラの状態サマリ(のテキスト情報)を取得する **/
    //String getCameraStatusSummary(ICameraStatusSummary decoder);

    // カメラプロパティアクセスインタフェース
    IOlyCameraPropertyProvider getCameraPropertyProvider();

    // カメラプロパティのロード・セーブインタフェース（読み込み中/保存中のダイアログ表示機能付き）
    ICameraPropertyLoadSaveOperations getCameraPropertyLoadSaveOperations();

    // カメラプロパティのロード・セーブインタフェース
    ILoadSaveCameraProperties getLoadSaveCameraProperties();

    // カメラの動作モード変更インタフェース
    ICameraRunMode getChangeRunModeExecutor();

    ICameraConnection getConnectionInterface();

    /** ズームレンズの状態ホルダを応答 **/
    IZoomLensHolder getZoomLensHolder();

    // デジタル水準器のホルダーを取得する
    ILevelGauge getLevelGauge();

    // 機能の処理を行うクラスを取得する
    @NonNull ICameraFeatureDispatcher getFeatureDispatcher(@NonNull AppCompatActivity context, @NonNull IShowInformation statusDrawer, @NonNull ICameraController camera, @NonNull PreferenceDataStore preferenceAccessWrapper, @NonNull ILiveImageStatusNotify liveImageView);
}
