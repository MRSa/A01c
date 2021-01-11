package jp.sfjp.gokigen.a01c.thetacamerawrapper

import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceDataStore
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraProperty
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.IBracketingShotStyle

class ThetaFeatureDispatcher(val context: AppCompatActivity, val statusDrawer: IShowInformation, val camera: ICameraController, private val preferences: PreferenceDataStore, val liveImageView: ILiveImageStatusNotify): ICameraFeatureDispatcher
{
    private var takeMode : String = "P"

    /**
     * 指定した機能を実行する
     *
     * @param objectId　　　　　操作したオブジェクト
     * @param key　          　操作する機能
     * @param defaultAction   標準機能
     */
    override fun dispatchAction(objectId: Int, key: String, defaultAction: Int): Boolean
    {
        val featureNumber = preferences.getInt(key, defaultAction)
        if (featureNumber <= ICameraFeatureDispatcher.FEATURE_ACTION_NONE)
        {
            // 何もしない
            return false
        }

        // 機能実行の割り当て...
        var duration = IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT
        when (featureNumber) {
            ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID ->                    // グリッド標示ON/OFF
                changeShowGrid(objectId)
            ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT ->                  // シャッター(一枚撮影)
                pushShutterButton()
            ICameraFeatureDispatcher.FEATURE_SETTINGS -> {
                showSettingsScreen()                                                // 設定画面を開く
                duration = IShowInformation.VIBRATE_PATTERN_NONE
            }
            ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE ->                       // 動画の撮影・撮影終了
                movieControl()
            ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE ->                    // 撮影モードの変更
                changeTakeMode()
            ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE_REVERSE ->            // 撮影モードの変更（逆順）
                changeTakeModeReverse()
/*
            ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE ->                 // AE LOCKのON/OFF切り替え
                changeAeLockMode()
            ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN ->                 // 露出補正を１段階下げる
                changeExposureBiasValueDown()
            ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP ->                   // 露出補正を１段階上げる
                changeExposureBiasValueUp()
            ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN ->                      // 絞り値を１段階下げる
                changeApertureValueDown()
            ICameraFeatureDispatcher.FEATURE_APERTURE_UP ->                        // 絞り値を１段階上げる
                changeApertureValueUp()
            ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN ->                 // シャッター速度を１段階下げる
                changeShutterSpeedDown()
            ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP ->                   // シャッター速度を１段階上げる
                changeShutterSpeedUp()
            ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN ->                    // 仕上がり・ピクチャーモードを選択
                changeColorToneDown()
            ICameraFeatureDispatcher.FEATURE_COLORTONE_UP ->                       // 仕上がり・ピクチャーモードを選択
                changeColorToneUp()
            ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN ->                    // アートフィルターを選択
                changeArtFilterDown()
            ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP ->                      // アートフィルターを選択
                changeArtFilterUp()
            ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_LEVEL_GAUGE ->                 // デジタル水準器の表示・非表示
                changeShowLevelGauge()
            ICameraFeatureDispatcher.FEATURE_AE_DOWN ->                 // AE(測光方式)を選択
                changeAEModeDown()
            ICameraFeatureDispatcher.FEATURE_AE_UP ->                 // AE(測光方式)を選択
                changeAEModeUp()
            ICameraFeatureDispatcher.FEATURE_ISO_DOWN ->                 // ISO感度を選択
                changeIsoSensitivityDown()
            ICameraFeatureDispatcher.FEATURE_ISO_UP ->                 // ISO感度を選択
                changeIsoSensitivityUp()
            ICameraFeatureDispatcher.FEATURE_WB_DOWN ->                 // ホワイトバランスを選択
                changeWhiteBalanceDown()
            ICameraFeatureDispatcher.FEATURE_WB_UP ->                 // ホワイトバランスを選択
                changeWhiteBalanceUp()
            ICameraFeatureDispatcher.FEATURE_QUALITY_MOVIE_DOWN ->                 // 動画撮影クオリティを選択
                changeMovieQualityModeDown()
            ICameraFeatureDispatcher.FEATURE_QUALITY_MOVIE_UP ->                 // 動画撮影クオリティを選択
                changeMovieQualityModeUp()
            ICameraFeatureDispatcher.FEATURE_SHORT_MOVIE_RECORD_TIME_DOWN ->                 // ショートムービー時の撮影時間を選択
                changeShortMovieRecordLengthDown()
            ICameraFeatureDispatcher.FEATURE_SHORT_MOVIE_RECORD_TIME_UP ->                 // ショートムービー時の撮影時間を選択
                changeShortMovieRecordLengthUp()
            ICameraFeatureDispatcher.FEATURE_EXPOSE_MOVIE_SELECT_DOWN ->                 // 動画の撮影モードを選択
                changeMovieTakeModeDown()
            ICameraFeatureDispatcher.FEATURE_EXPOSE_MOVIE_SELECT_UP ->                 // 動画の撮影モードを選択
                changeMovieTakeModeUp()
            ICameraFeatureDispatcher.FEATURE_CHANGE_AF_MF ->                 // AF/MFの切り替えを行う
                toggleAfMf()
            ICameraFeatureDispatcher.FEATURE_CHANGE_AE ->                 // AE(測光方式)を選択
                changeAEMode(1)
            ICameraFeatureDispatcher.FEATURE_CHANGE_AE_REVERSE ->                 // AE(測光方式)を選択
                changeAEMode(-1)
            ICameraFeatureDispatcher.FEATURE_SHOT_INTERVAL_3SEC ->                 // ３秒待ってから１枚撮影する
                intervalOneShot(3)
            ICameraFeatureDispatcher.FEATURE_SHOT_INTERVAL_5SEC ->                 // 5秒待ってから１枚撮影する
                intervalOneShot(5)
            ICameraFeatureDispatcher.FEATURE_SHOT_INTERVAL_10SEC ->                 // 10秒待ってから１枚撮影する
                intervalOneShot(10)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_EXPOSURE ->                 // 露出ブラケット撮影を行う (5枚)
                bracketingShot(IBracketingShotStyle.BRACKET_EXPREV, 5)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_APERATURE ->                 // 絞りブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_APERTURE, 5)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_SHUTTER ->                       // シャッターブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_SHUTTER, 5)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_COLORTONE ->                      // カラートーンブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_COLOR_TONE, 11)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_WB ->                             // ホワイトバランスブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_WB, 7)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_ART_FILTER ->                     // アートフィルターブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_ART_FILTER, 5)
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_ISO ->                            // ブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_ISO, 3)
            ICameraFeatureDispatcher.FEATURE_SHOW_FAVORITE_DIALOG ->                        // お気に入りのダイアログ表示を行う
                showFavoriteDialog()
            ICameraFeatureDispatcher.FEATURE_LENS_ZOOMIN ->                                 // ズームイン（パワーズームレンズ装着時）
                if (!driveZoomLens(1)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_LENS_ZOOMOUT ->                                // ズームアウト（パワーズームレンズ装着時）
                if (!driveZoomLens(-1)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_LENS_ZOOMIN_2X ->                              // ズームイン（パワーズームレンズ装着時）
                if (!driveZoomLens(2)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_LENS_ZOOMOUT_2X ->                             // ズームアウト（パワーズームレンズ装着時）
                if (!driveZoomLens(-2)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOM_RESET ->                         // デジタルズームのリセット
                resetDigitalZoom()
            ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOM_CHANGE ->                        // デジタルズームの設定 (繰り返し)
                if (!driveDigitalZoom(0)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOMIN ->                             // デジタルズーム ズームイン
                if (!driveDigitalZoom(1)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_DIGITAL_ZOOMOUT ->                            // デジタルズーム ズームアウト
                if (!driveDigitalZoom(-1)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X5 ->                 // ライブビュー拡大（5倍）
                if (!changeLiveViewMagnify(5)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7 ->                 // ライブビュー拡大（7倍）
                if (!changeLiveViewMagnify(7)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X10 ->                 // ライブビュー拡大（10倍）
                if (!changeLiveViewMagnify(10)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
            ICameraFeatureDispatcher.FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X14 ->                 // ライブビュー拡大（14倍）
                if (!changeLiveViewMagnify(14)) {
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
*/
            else ->                 // 上記以外...なにもしない
                duration = IShowInformation.VIBRATE_PATTERN_NONE
        }

        // コマンド実行完了後、ぶるぶるさせる
        statusDrawer.vibrate(duration)
        return (true)
    }

    override fun dispatchAreaAction(event: MotionEvent?, areaFeatureNumber: Int): Boolean
    {
        var ret = false
        when (areaFeatureNumber)
        {
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS -> ret = camera.driveAutoFocus(event)
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_NOT_CONNECTED -> {
            }
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA ->                 // AUTOFOCUS エリアに含まれているかどうかチェックする
                ret = camera.isContainsAutoFocusPoint(event)
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_NONE ->                 // 何もしない
                ret = false
            else -> ret = false
        }
        return ret
    }

    /**
     * 撮影モードの取得
     *
     */
    override fun getTakeMode(): String?
    {
        return (takeMode)   // 暫定でプログラムモード

        //val propertyProxy = camera.cameraPropertyProvider
        //return propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE))
    }

    /**
     * 撮影モードの変更指示
     * (P > A > S > M > ART > movie > iAuto > ...)
     */
    private fun changeTakeMode()
    {
        if (takeMode.contains("P"))
        {
            takeMode = "Movie"
        }
        else
        {
            takeMode = "P"
        }
        statusDrawer.setMessage(IShowInformation.AREA_1, Color.WHITE, takeMode)

        //  撮影モードの更新
        camera.updateTakeMode();

/*
        val propertyProxy = camera.cameraPropertyProvider
        val propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE))
                ?: // データ取得失敗
                return
        var targetMode = "<" + IOlyCameraProperty.TAKE_MODE // 変更先モード
        targetMode = when (propetyValue) {
            "P" -> "$targetMode/A>"
            "A" -> "$targetMode/S>"
            "S" -> "$targetMode/M>"
            "M" -> "$targetMode/ART>"
            "ART" -> "$targetMode/movie>"
            "Movie" -> "$targetMode/iAuto>"
            "iAuto" -> "$targetMode/P>"
            else -> "$targetMode/P>"
        }

        Log.v(TAG, "changeTakeMode() $targetMode")
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode)
        camera.unlockAutoFocus()
*/
    }

    /**
     * 撮影モードの変更指示
     * (iAuto < P < A < S < M < ART < movie < iAuto < ...)
     */
    private fun changeTakeModeReverse()
    {
        changeTakeMode()
/*
        val propertyProxy = camera.cameraPropertyProvider
        val propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE))
                ?: // データ取得失敗
                return
        var targetMode = "<" + IOlyCameraProperty.TAKE_MODE // 変更先モード
        targetMode = when (propetyValue) {
            "P" -> "$targetMode/iAuto>"
            "A" -> "$targetMode/P>"
            "S" -> "$targetMode/A>"
            "M" -> "$targetMode/S>"
            "ART" -> "$targetMode/M>"
            "Movie" -> "$targetMode/ART>"
            "iAuto" -> "$targetMode/movie>"
            else -> "$targetMode/movie>"
        }
        Log.v(TAG, "changeTakeMode() $targetMode")
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode)
        camera.unlockAutoFocus()

        //  撮影モードの更新
        //camera.updateTakeMode();
*/
    }

    /**
     * シャッターボタンが押された！
     * （現在は、連続撮影モードについてはまだ非対応）
     */
    private fun pushShutterButton()
    {
        // カメラ側のシャッターを押す
        camera.singleShot()
    }

    /**
     * 動画の撮影・停止を行う
     *
     */
    private fun movieControl()
    {
        camera.movieControl()
    }

    /**
     * AF/MFの切り替えを行う
     */
    private fun toggleAfMf()
    {
        camera.toggleManualFocus()
    }

    /**
     * グリッド表示の ON/OFFを切り替える
     *
     */
    private fun changeShowGrid(objectId: Int)
    {
        liveImageView.toggleShowGridFrame()
        updateGridStatusButton(objectId)
    }

    /**
     * 　デジタル水準器の ON/OFFを切り替える
     *
     */
    private fun changeShowLevelGauge()
    {
        liveImageView.toggleShowLevelGauge()
    }

    /**
     * AE-Lock/Lock解除を行う
     *
     */
    private fun changeAeLockMode()
    {
        camera.toggleAutoExposure()
    }

    /**
     * グリッドフレームの表示・非表示ボタンを更新する
     *
     */
    private fun updateGridStatusButton(buttonId: Int)
    {
        val btnResId: Int
        btnResId = if (liveImageView.isShowGrid) {
            // グリッドがON状態、グリッドをOFFにするボタンを出す
            R.drawable.btn_ic_grid_off
        } else {
            //  グリッドがOFF状態、グリッドをONにするボタンを出す
            R.drawable.btn_ic_grid_on
        }
        statusDrawer.setButtonDrawable(buttonId, btnResId)
    }

    /**
     * 　  露出補正を１段階下げる
     */
    private fun changeExposureBiasValueDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.EXPOSURE_COMPENSATION)
    }

    /**
     * 露出補正を１段階あげる
     *
     */
    private fun changeExposureBiasValueUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.EXPOSURE_COMPENSATION)
    }

    /**
     * 　  絞り値を１段階下げる
     */
    private fun changeApertureValueDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.APERTURE)
    }

    /**
     * 絞り値を１段階あげる
     *
     */
    private fun changeApertureValueUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.APERTURE)
    }

    /**
     * 　  シャッター速度を１段階下げる
     */
    private fun changeShutterSpeedDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.SHUTTER_SPEED)
    }

    /**
     * シャッター速度を１段階あげる
     *
     */
    private fun changeShutterSpeedUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.SHUTTER_SPEED)
    }

    /**
     * 　  仕上がり・ピクチャーモードを１段階下げる
     */
    private fun changeColorToneDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.COLOR_TONE)
    }

    /**
     * 仕上がり・ピクチャーモードを１段階あげる
     *
     */
    private fun changeColorToneUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.COLOR_TONE)
    }

    /**
     * アートフィルターを１段階さげる
     *
     */
    private fun changeArtFilterDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.ART_FILTER)
    }

    /**
     * アートフィルターを１段階あげる
     *
     */
    private fun changeArtFilterUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.ART_FILTER)
    }


    /**
     * 測光方式を１段階さげる
     *
     */
    private fun changeAEModeDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.AE_MODE)
    }

    /**
     * 測光方式を１段階あげる
     *
     */
    private fun changeAEModeUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.AE_MODE)
    }

    /**
     * 測光方式を更新する
     *
     */
    private fun changeAEMode(direction: Int)
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.changeCameraProperty(IOlyCameraProperty.AE_MODE, direction)
    }

    /**
     * ISO感度を１段階さげる
     *
     */
    private fun changeIsoSensitivityDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.ISO_SENSITIVITY)
    }

    /**
     * ISO感度を１段階あげる
     *
     */
    private fun changeIsoSensitivityUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.ISO_SENSITIVITY)
    }


    /**
     * ホワイトバランスを１段階さげる
     *
     */
    private fun changeWhiteBalanceDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.WB_MODE)
    }

    /**
     * ホワイトバランスを１段階あげる
     *
     */
    private fun changeWhiteBalanceUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.WB_MODE)
    }

    /**
     * 動画撮影モードを１段階さげる
     *
     */
    private fun changeMovieQualityModeDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.QUALITY_MOVIE)
    }

    /**
     * 動画撮影モードを１段階あげる
     *
     */
    private fun changeMovieQualityModeUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.QUALITY_MOVIE)
    }


    /**
     * 動画撮影モードがショートムービーのときの撮影時間を１段階さげる
     *
     */
    private fun changeShortMovieRecordLengthDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.SHORT_MOVIE_RECORD_TIME)
    }

    /**
     * 動画撮影モードがショートムービーのときの撮影時間を１段階あげる
     *
     */
    private fun changeShortMovieRecordLengthUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.SHORT_MOVIE_RECORD_TIME)
    }


    /**
     * 動画撮影モードを１段階さげる
     *
     */
    private fun changeMovieTakeModeDown()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.TAKE_MODE_MOVIE)
    }

    /**
     * 動画撮影モードを１段階あげる
     *
     */
    private fun changeMovieTakeModeUp()
    {
        val propertyProxy = camera.cameraPropertyProvider
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.TAKE_MODE_MOVIE)
    }

    /**
     * インターバル撮影（１枚）を行う
     *
     * @param waitSeconds  撮影待ち時間（単位：秒）
     */
    private fun intervalOneShot(waitSeconds: Int)
    {
        camera.bracketingShot(IBracketingShotStyle.BRACKET_NONE, 1, waitSeconds)
    }

    /**
     * ブラケット撮影を行う
     *
     * @param style  撮影スタイル
     * @param count  撮影枚数
     */
    private fun bracketingShot(style: Int, count: Int)
    {
        camera.bracketingShot(style, count, 0)
    }


    /**
     * 「お気に入り設定」表示画面を開く
     *
     */
    private fun showFavoriteDialog()
    {
        // お気に入り設定表示画面を開く
        statusDrawer.showFavoriteSettingsDialog()
    }

    /**
     * ズームイン・ズームアウト操作を行う
     *
     * @param direction ズーム操作の方向
     */
    private fun driveZoomLens(direction: Int): Boolean
    {
        var isExecute = false
        val zoom = camera.zoomLensHolder
        if (zoom != null)
        {
            zoom.updateStatus()
            if (zoom.canZoom())
            {
                // ズーム操作を行う
                try
                {
                    zoom.driveZoomLens(direction)
                    isExecute = true
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }
        return isExecute
    }

    private fun resetDigitalZoom()
    {
        val zoom = camera.zoomLensHolder
        if (zoom != null)
        {
            zoom.updateStatus()
            zoom.changeDigitalZoomScale(1.0f, false)
        }
    }

    private fun driveDigitalZoom(zoomType: Int): Boolean
    {
        var isExecute = false
        val zoom = camera.zoomLensHolder
        if (zoom != null)
        {
            zoom.updateStatus()
            var magnify = zoomType.toFloat()
            if (zoomType == 0)
            {
                magnify = 1.0f
            }
            val currentScale = zoom.currentDigitalZoomScale
            val targetScale = currentScale + magnify * 0.5f
            zoom.changeDigitalZoomScale(targetScale, zoomType == 0)
            isExecute = true
        }
        return isExecute
    }

    /**
     * ライブビューのサイズを指定した倍率に拡大する（拡大中の場合にはもとに戻す。）
     *
     * @param scale  拡大倍率
     * @return  実行した場合true, 実行しなかった場合はfalse
     */
    private fun changeLiveViewMagnify(scale: Int): Boolean
    {
        var isExecute = false
        val zoom = camera.zoomLensHolder
        if (zoom != null)
        {
            isExecute = zoom.magnifyLiveView(scale)
        }
        return isExecute
    }

    /**
     * 設定画面を開く
     *
     */
    private fun showSettingsScreen()
    {
        try
        {
            Log.v(TAG, " --- showSettingsScreen() ---")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaFeatureDispatcher::class.java.simpleName
    }
}
