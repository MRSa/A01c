package jp.sfjp.gokigen.a01c.thetacamerawrapper

import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceDataStore
import jp.sfjp.gokigen.a01c.ICameraController
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher
import jp.sfjp.gokigen.a01c.IShowInformation
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify
import jp.sfjp.gokigen.a01c.thetacamerawrapper.operation.ThetaOptionUpdateControl

class ThetaFeatureDispatcher(private val context: AppCompatActivity, private val statusDrawer: IShowInformation, private val camera: ICameraController, private val preferences: PreferenceDataStore, private val liveImageView: ILiveImageStatusNotify, private val optionSet : ThetaOptionUpdateControl, private val statusHolder : IThetaStatusHolder, private val sessionIdProvider : IThetaSessionIdProvider): ICameraFeatureDispatcher
{
    private var exposureCompensationIndex : Int = 6
    private var exposureProgramIndex : Int = 1
    private var thetaFilterModeIndex : Int = 0
    private var thetaWhiteBalanceIndex : Int = 0
    private var thetaIsoSensitivityIndex: Int = 2
    private var thetaShutterSpeedIndex : Int = 26

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
                if (statusHolder.captureMode != "Movie")
                {
                    showSettingsScreen()                                            // 設定画面を開く
                    duration = IShowInformation.VIBRATE_PATTERN_NONE
                }
                else
                {
                    changeIsoSensitivity()                                         // ISO感度を変更（Movieモード時)
                }
            }
            ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE ->                      // 動画の撮影・撮影終了
                movieControl()
            ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE ->                    // 撮影モードの変更
                changeTakeMode()
            ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE_REVERSE ->            // 撮影モードの変更（逆順）
                changeTakeModeReverse()
            ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN ->                 // 露出補正を１段階下げる
                changeExposureBiasValueDown()
            ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP ->                   // 露出補正を１段階上げる
                changeExposureBiasValueUp()
            ICameraFeatureDispatcher.FEATURE_SHOW_FAVORITE_DIALOG ->               // Exposure Program(SHOW_FAVORITE_DIALOG)
                changeExposureProgram()
            ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE ->                 // FILTER MODE (AE LOCKのON/OFF切り替え)
                changeFilterMode()
            ICameraFeatureDispatcher.FEATURE_CHANGE_AE ->                          // FILTER MODE AE(測光方式)を選択
                changeFilterMode()
            ICameraFeatureDispatcher.FEATURE_WB_DOWN ->                            // シャッタースピードDOWN (ホワイトバランスを選択)
                changeShutterSpeedDown()
            ICameraFeatureDispatcher.FEATURE_WB_UP ->                              // シャッタースピードUP (ホワイトバランスを選択)
                changeShutterSpeedUp()
            ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN ->                     // ホワイトバランス(仕上がり・ピクチャーモードを選択)
                changeWhiteBalanceDown()
            ICameraFeatureDispatcher.FEATURE_COLORTONE_UP ->                       // ホワイトバランス(仕上がり・ピクチャーモードを選択)
                changeWhiteBalanceUp()
            ICameraFeatureDispatcher.FEATURE_ISO_DOWN ->                           // ISO感度を選択
                changeIsoSensitivityDown()
            ICameraFeatureDispatcher.FEATURE_ISO_UP ->                             // ISO感度を選択
                changeIsoSensitivityUp()
            ICameraFeatureDispatcher.FEATURE_SHOT_BRACKET_EXPOSURE ->              // ブラケット撮影
                bracketingControl()
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
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA ->    // AUTOFOCUS エリアに含まれているかどうかチェックする
                ret = camera.isContainsAutoFocusPoint(event)
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_NONE ->                             // 何もしない
                ret = false
            else -> ret = false
        }
        return ret
    }

    /**
     * 撮影モードの取得
     *
     */
    override fun getTakeMode(): String
    {
        return (statusHolder.captureMode)
    }

    /**
     * 撮影モードの変更指示
     */
    private fun changeTakeMode()
    {
        //  撮影モードの更新
        camera.updateTakeMode()
    }

    /**
     * 撮影モードの変更指示
     * (iAuto < P < A < S < M < ART < movie < iAuto < ...)
     */
    private fun changeTakeModeReverse()
    {
        changeTakeMode()
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
     * ブラケット撮影・停止を行う
     *
     */
    private fun bracketingControl()
    {
        camera.bracketingControl()
    }

    /**
     *   フィルター
     *
     */
    private fun changeFilterMode()
    {
        try
        {
            thetaFilterModeIndex++
            if (thetaFilterModeIndex > MAX_FILTER_SELECTION)
            {
                thetaFilterModeIndex = 0
            }
            val optionStr = context.resources.getStringArray(R.array.theta_filter_set_value)[thetaFilterModeIndex]
            optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *   Exposure Program
     *
     */
    private fun changeExposureProgram()
    {
        try
        {
            exposureProgramIndex++
            if (exposureProgramIndex > MAX_EXPOSURE_PROGRAM)
            {
                exposureProgramIndex = 0
            }
            val optionStr = context.resources.getStringArray(R.array.exposure_program_value)[exposureProgramIndex]
            optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
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
     * グリッドフレームの表示・非表示ボタンを更新する
     *
     */
    private fun updateGridStatusButton(buttonId: Int)
    {
        val btnResId: Int = if (liveImageView.isShowGrid) {
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
        try
        {
            if (exposureCompensationIndex > 0)
            {
                exposureCompensationIndex--

                val optionStr = context.resources.getStringArray(R.array.exposure_compensation_value)[exposureCompensationIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * 露出補正を１段階あげる
     *
     */
    private fun changeExposureBiasValueUp()
    {
        try
        {
            if (exposureCompensationIndex < MAX_EXPOSURE_COMPENSATION)
            {
                exposureCompensationIndex++

                val optionStr = context.resources.getStringArray(R.array.exposure_compensation_value)[exposureCompensationIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * 　  シャッター速度を１段階下げる
     */
    private fun changeShutterSpeedDown()
    {
        try
        {
            if (thetaShutterSpeedIndex > 0)
            {
                thetaShutterSpeedIndex--

                val optionStr = context.resources.getStringArray(R.array.theta_shutter_speed_value)[thetaShutterSpeedIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * シャッター速度を１段階あげる
     *
     */
    private fun changeShutterSpeedUp()
    {
        try
        {
            if (thetaShutterSpeedIndex < MAX_SHUTTER_SPEED)
            {
                thetaShutterSpeedIndex++

                val optionStr = context.resources.getStringArray(R.array.theta_shutter_speed_value)[thetaShutterSpeedIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ISO感度
     *
     */
    private fun changeIsoSensitivity()
    {
        try
        {
            thetaIsoSensitivityIndex++
            if (thetaIsoSensitivityIndex > MAX_ISO_SENSITIVITY)
            {
                thetaIsoSensitivityIndex = 0
            }
            val optionStr = context.resources.getStringArray(R.array.theta_iso_sensitivity_value)[thetaIsoSensitivityIndex]
            optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ISO感度を１段階さげる
     *
     */
    private fun changeIsoSensitivityDown()
    {
        try
        {
            if (thetaIsoSensitivityIndex > 0)
            {
                thetaIsoSensitivityIndex--

                val optionStr = context.resources.getStringArray(R.array.theta_iso_sensitivity_value)[thetaIsoSensitivityIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ISO感度を１段階あげる
     *
     */
    private fun changeIsoSensitivityUp()
    {
        try
        {
            if (thetaIsoSensitivityIndex < MAX_ISO_SENSITIVITY)
            {
                thetaIsoSensitivityIndex++

                val optionStr = context.resources.getStringArray(R.array.theta_iso_sensitivity_value)[thetaIsoSensitivityIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ホワイトバランスを１段階さげる
     *
     */
    private fun changeWhiteBalanceDown()
    {
        try
        {
            if (thetaWhiteBalanceIndex > 0)
            {
                thetaWhiteBalanceIndex--

                val optionStr = context.resources.getStringArray(R.array.white_balance_value)[thetaWhiteBalanceIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * ホワイトバランスを１段階あげる
     *
     */
    private fun changeWhiteBalanceUp()
    {
        try
        {
            if (thetaWhiteBalanceIndex < MAX_WHITE_BALANCE)
            {
                thetaWhiteBalanceIndex++

                val optionStr = context.resources.getStringArray(R.array.white_balance_value)[thetaWhiteBalanceIndex]
                optionSet.setOptions(optionStr, sessionIdProvider.sessionId.isEmpty())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
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

        private const val MAX_EXPOSURE_COMPENSATION = 12 // exposureCompensation
        private const val MAX_EXPOSURE_PROGRAM = 4       // exposureProgram
        private const val MAX_FILTER_SELECTION = 4       // _filter
        private const val MAX_WHITE_BALANCE = 11         // whiteBalance
        private const val MAX_ISO_SENSITIVITY = 21       //  ISO Sensitivity
        private const val MAX_SHUTTER_SPEED = 60         //  Shutter Speed
    }
}
