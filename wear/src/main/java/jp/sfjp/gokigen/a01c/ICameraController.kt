package jp.sfjp.gokigen.a01c

import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceDataStore
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraRunMode
import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider

interface ICameraController {
    /** 接続終了(ライブビュースタート前の準備)  */
    fun connectFinished()

    /** ライブビュー関係  */
    fun setLiveViewListener(listener: CameraLiveViewListenerImpl)
    fun changeLiveViewSize(size: String?)
    fun startLiveView()
    fun stopLiveView()

    /** 撮影モードの更新   */
    fun updateTakeMode()

    /** オートフォーカス機能の実行  */
    fun driveAutoFocus(event: MotionEvent?): Boolean
    fun unlockAutoFocus()

    /** ポイントがオートフォーカス可能なエリアかどうかチェックする  */
    fun isContainsAutoFocusPoint(event: MotionEvent?): Boolean // trueならオートフォーカス可能

    /** シングル撮影機能の実行  */
    fun singleShot()

    /** ムービー撮影機能の実行(開始・終了)  */
    fun movieControl()

    /** ブラケット撮影(開始・終了)  */
    fun bracketingControl()

    /** インターバル＆ブラケッティング撮影の実行  */
    fun bracketingShot(bracketingStyle: Int, bracketingCount: Int, durationSeconds: Int)

    /** 撮影確認画像の設定  */
    fun setRecViewMode(isRecViewMode: Boolean)


    /** AE Lockの設定・解除、 AF/MFの切替え  */
    fun toggleAutoExposure()
    fun toggleManualFocus()

    /** カメラの状態取得  */
    fun isManualFocus(): Boolean
    fun isAFLock(): Boolean
    fun isAELock(): Boolean

    /** カメラ状態の表示をすべて更新する  */
    fun updateStatusAll()

    // ステータス監視のタスクを取得する
    fun getStatusWatcher(): ICameraStatusWatcher?

    // カメラプロパティアクセスインタフェース
    fun getCameraPropertyProvider(): IOlyCameraPropertyProvider

    // カメラプロパティのロード・セーブインタフェース（読み込み中/保存中のダイアログ表示機能付き）
    fun getCameraPropertyLoadSaveOperations(): ICameraPropertyLoadSaveOperations?

    // カメラプロパティのロード・セーブインタフェース
    fun getLoadSaveCameraProperties(): ILoadSaveCameraProperties

    // カメラの動作モード変更インタフェース
    fun getChangeRunModeExecutor(): ICameraRunMode

    fun getConnectionInterface(): ICameraConnection?

    /** ズームレンズの状態ホルダを応答  */
    fun getZoomLensHolder(): IZoomLensHolder?

    // デジタル水準器のホルダーを取得する
    fun getLevelGauge(): ILevelGauge

    // 機能の処理を行うクラスを取得する
    fun getFeatureDispatcher(
        context: AppCompatActivity,
        statusDrawer: IShowInformation,
        camera: ICameraController,
        preferenceAccessWrapper: PreferenceDataStore,
        liveImageView: ILiveImageStatusNotify
    ): ICameraFeatureDispatcher
}
