package jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher;

import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import jp.sfjp.gokigen.a01c.ConfirmationDialog;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOlyCameraCoordinator;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IZoomLensHolder;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider;
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.IBracketingShotStyle;
import jp.sfjp.gokigen.a01c.preference.PreferenceAccessWrapper;


/**
 *   カメラ機能の実行
 *
 */
public class FeatureDispatcher implements ICameraFeatureDispatcher
{
    private final String TAG = toString();

    private final AppCompatActivity activity;
    private final IShowInformation statusDrawer;
    private final IOlyCameraCoordinator camera;
    private final ILiveImageStatusNotify liveImageView;
    private final PreferenceAccessWrapper preferences;

    public FeatureDispatcher(@NonNull AppCompatActivity context, @NonNull IShowInformation statusDrawer, @NonNull IOlyCameraCoordinator camera, ILiveImageStatusNotify liveImageView)
    {
        this.activity = context;
        this.statusDrawer = statusDrawer;
        this.camera = camera;
        this.liveImageView = liveImageView;
        this.preferences = new PreferenceAccessWrapper(context);
    }

    /**
     *   指定した機能を実行する
     *
     * @param objectId　　　　　操作したオブジェクト
     * @param key　          　操作する機能
     * @param defaultAction   標準機能
     */
    @Override
    public boolean dispatchAction(int objectId, @NonNull String key, int defaultAction)
    {
        int featureNumber = preferences.getInt(key, defaultAction);
        if (featureNumber <= ICameraFeatureDispatcher.FEATURE_ACTION_NONE)
        {
            // 何もしない
            return (false);
        }

        // 機能実行の割り当て...
        int duration = IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT;
        switch (featureNumber)
        {
            case ICameraFeatureDispatcher.FEATURE_SETTINGS:
                // 設定画面を開く
                showSettingsScreen();
                duration =IShowInformation.VIBRATE_PATTERN_NONE;
                break;
            case ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_GRID:
                // グリッド標示ON/OFF
                changeShowGrid(objectId);
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SINGLESHOT:
                // シャッター
                pushShutterButton();
                //duration =IShowInformation.VIBRATE_PATTERN_NONE;
                break;
            case ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE:
                // 撮影モードの変更
                changeTakeMode();
                break;
            case ICameraFeatureDispatcher.FEATURE_CHAGE_AE_LOCK_MODE:
                // AE LOCKのON/OFF切り替え
                changeAeLockMode();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_DOWN:
                // 露出補正を１段階下げる
                changeExposureBiasValueDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSURE_BIAS_UP:
                // 露出補正を１段階上げる
                changeExposureBiasValueUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_APERTURE_DOWN:
                // 絞り値を１段階下げる
                changeApertureValueDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_APERTURE_UP:
                // 絞り値を１段階上げる
                changeApertureValueUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_DOWN:
                // シャッター速度を１段階下げる
                changeShutterSpeedDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHUTTER_SPEED_UP:
                // シャッター速度を１段階上げる
                changeShutterSpeedUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_COLORTONE_DOWN:
                // 仕上がり・ピクチャーモードを選択
                changeColorToneDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_COLORTONE_UP:
                // 仕上がり・ピクチャーモードを選択
                changeColorToneUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_ART_FILTER_DOWN:
                // アートフィルターを選択
                changeArtFilterDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_ART_FILTER_UP:
                // アートフィルターを選択
                changeArtFilterUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_TOGGLE_SHOW_LEVEL_GAUGE:
                // デジタル水準器の表示・非表示
                changeShowLevelGauge();
                break;
            case ICameraFeatureDispatcher.FEATURE_CHANGE_TAKEMODE_REVERSE:
                // 撮影モードの変更（逆順）
                changeTakeModeReverse();
                break;
            case ICameraFeatureDispatcher.FEATURE_CONTROL_MOVIE:
                // 動画の撮影・撮影終了
                movieControl();
                break;
            case ICameraFeatureDispatcher.FEATURE_AE_DOWN:
                // AE(測光方式)を選択
                changeAEModeDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_AE_UP:
                // AE(測光方式)を選択
                changeAEModeUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_ISO_DOWN:
                // ISO感度を選択
                changeIsoSensitivityDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_ISO_UP:
                // ISO感度を選択
                changeIsoSensitivityUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_WB_DOWN:
                // ホワイトバランスを選択
                changeWhiteBalanceDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_WB_UP:
                // ホワイトバランスを選択
                changeWhiteBalanceUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_QUALITY_MOVIE_DOWN:
                // 動画撮影クオリティを選択
                changeMovieQualityModeDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_QUALITY_MOVIE_UP:
                // 動画撮影クオリティを選択
                changeMovieQualityModeUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHORT_MOVIE_RECORD_TIME_DOWN:
                // ショートムービー時の撮影時間を選択
                changeShortMovieRecordLengthDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_SHORT_MOVIE_RECORD_TIME_UP:
                // ショートムービー時の撮影時間を選択
                changeShortMovieRecordLengthUp();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSE_MOVIE_SELECT_DOWN:
                // 動画の撮影モードを選択
                changeMovieTakeModeDown();
                break;
            case ICameraFeatureDispatcher.FEATURE_EXPOSE_MOVIE_SELECT_UP:
                // 動画の撮影モードを選択
                changeMovieTakeModeUp();
                break;
            case FEATURE_CHANGE_AF_MF:
                // AF/MFの切り替えを行う
                toggleAfMf();
                break;
            case FEATURE_CHANGE_AE:
                // AE(測光方式)を選択
                changeAEMode(1);
                break;
            case FEATURE_CHANGE_AE_REVERSE:
                // AE(測光方式)を選択
                changeAEMode(-1);
                break;

            case FEATURE_SHOT_INTERVAL_3SEC:
                // ３秒待ってから１枚撮影する
                intervalOneShot(3);
                break;

            case FEATURE_SHOT_INTERVAL_5SEC:
                // 5秒待ってから１枚撮影する
                intervalOneShot(5);
                break;

            case FEATURE_SHOT_INTERVAL_10SEC:
                // 10秒待ってから１枚撮影する
                intervalOneShot(10);
                break;
            case FEATURE_SHOT_BRACKET_EXPOSURE:
                // 露出ブラケット撮影を行う (5枚)
                bracketingShot(IBracketingShotStyle.BRACKET_EXPREV, 5);
                break;

            case FEATURE_SHOT_BRACKET_APERATURE:
                // 絞りブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_APERTURE, 5);
                break;

            case FEATURE_SHOT_BRACKET_SHUTTER:
                // シャッターブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_SHUTTER, 5);
                break;

            case FEATURE_SHOT_BRACKET_COLORTONE:
                // カラートーンブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_COLOR_TONE, 11);
                break;

            case FEATURE_SHOT_BRACKET_WB:
                // ホワイトバランスブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_WB, 7);
                break;

            case FEATURE_SHOT_BRACKET_ART_FILTER:
                // アートフィルターブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_ART_FILTER, 5);
                break;

            case FEATURE_SHOT_BRACKET_ISO:
                // ブラケット撮影を行う
                bracketingShot(IBracketingShotStyle.BRACKET_ISO, 3);
                break;

            case FEATURE_SHOW_FAVORITE_DIALOG:
                // お気に入りのダイアログ表示を行う
                showFavoriteDialog();
                break;

            case FEATURE_LENS_ZOOMIN:
                // ズームイン（パワーズームレンズ装着時）
                if (!driveZoomLens(1))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_LENS_ZOOMOUT:
                // ズームアウト（パワーズームレンズ装着時）
                if (!driveZoomLens(-1))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_LENS_ZOOMIN_2X:
                // ズームイン（パワーズームレンズ装着時）
                if (!driveZoomLens(2))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_LENS_ZOOMOUT_2X:
                // ズームアウト（パワーズームレンズ装着時）
                if (!driveZoomLens(-2))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_DIGITAL_ZOOM_RESET:
                // デジタルズームのリセット
                resetDigitalZoom();
                break;

            case FEATURE_DIGITAL_ZOOM_CHANGE:
                // デジタルズームの設定 (繰り返し)
                if (!driveDigitalZoom(0))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;
            case FEATURE_DIGITAL_ZOOMIN:
                // デジタルズーム ズームイン
                if (!driveDigitalZoom(1))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_DIGITAL_ZOOMOUT:
                // デジタルズーム ズームアウト
                if (!driveDigitalZoom(-1))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X5:
                // ライブビュー拡大（5倍）
                if (!changeLiveViewMagnify(5))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7:
                // ライブビュー拡大（7倍）
                if (!changeLiveViewMagnify(7))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X10:
                // ライブビュー拡大（10倍）
                if (!changeLiveViewMagnify(10))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            case FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X14:
                // ライブビュー拡大（14倍）
                if (!changeLiveViewMagnify(14))
                {
                    duration =IShowInformation.VIBRATE_PATTERN_NONE;
                }
                break;

            default:
                // 上記以外...なにもしない
                duration =IShowInformation.VIBRATE_PATTERN_NONE;
                break;
        }

        // コマンド実行完了後、ぶるぶるさせる
        statusDrawer.vibrate(duration);
        return (true);
    }

    @Override
    public boolean dispatchAreaAction(MotionEvent event, int areaFeatureNumber)
    {
        boolean ret = false;
        switch (areaFeatureNumber)
        {
            case ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS:
                ret = camera.driveAutoFocus(event);
                break;

            case ICameraFeatureDispatcher.FEATURE_AREA_ACTION_NOT_CONNECTED:
                /*
                try
                {
                    // 実験... WIFIステート設定画面を開く
                    //Intent intent = new Intent(ACTION_ADD_NETWORK_SETTINGS);
                    Intent intent = new Intent(ACTION_NETWORK_SETTINGS);
                    context.startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                */
                break;

            case ICameraFeatureDispatcher.FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA:
                // AUTOFOCUS エリアに含まれているかどうかチェックする
                ret = camera.isContainsAutoFocusPoint(event);
                break;

            case FEATURE_AREA_ACTION_NONE:
            default:
                // 何もしない
                ret = false;
                break;
        }
        return (ret);
    }

    /**
     *   撮影モードの取得
     *
     */
    @Override
    public String getTakeMode()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        return (propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE)));
    }

    /**
     *   撮影モードの変更指示
     *   (P > A > S > M > ART > movie > iAuto > ...)
     */
    private void changeTakeMode()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        if (propetyValue == null)
        {
            // データ取得失敗
            return;
        }
        String targetMode = "<" + IOlyCameraProperty.TAKE_MODE;  // 変更先モード
        switch (propetyValue)
        {
            case "P":
                targetMode = targetMode + "/A>";
                break;

            case "A":
                targetMode =  targetMode + "/S>";
                break;

            case "S":
                targetMode =  targetMode + "/M>";
                break;

            case "M":
                targetMode =  targetMode + "/ART>";
                break;

            case "ART":
                targetMode =  targetMode + "/movie>";
                break;

            case "Movie":
                targetMode =  targetMode + "/iAuto>";
                break;

            case "iAuto":
            default:
                targetMode =  targetMode + "/P>";
                break;
        }
        Log.v(TAG, "changeTakeMode() " + targetMode);
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode);
        camera.unlockAutoFocus();

        //  撮影モードの更新
        //camera.updateTakeMode();
    }

    /**
     *   撮影モードの変更指示
     *   (iAuto < P < A < S < M < ART < movie < iAuto < ...)
     */
    private void changeTakeModeReverse()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        String propetyValue = propertyProxy.getCameraPropertyValueTitle(propertyProxy.getCameraPropertyValue(IOlyCameraProperty.TAKE_MODE));
        if (propetyValue == null)
        {
            // データ取得失敗
            return;
        }
        String targetMode = "<" + IOlyCameraProperty.TAKE_MODE;  // 変更先モード
        switch (propetyValue)
        {
            case "P":
                targetMode = targetMode + "/iAuto>";
                break;

            case "A":
                targetMode =  targetMode + "/P>";
                break;

            case "S":
                targetMode =  targetMode + "/A>";
                break;

            case "M":
                targetMode =  targetMode + "/S>";
                break;

            case "ART":
                targetMode =  targetMode + "/M>";
                break;
            case "Movie":
                targetMode =  targetMode + "/ART>";
                break;
            case "iAuto":
            default:
                targetMode =  targetMode + "/movie>";
                break;
        }
        Log.v(TAG, "changeTakeMode() " + targetMode);
        propertyProxy.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, targetMode);
        camera.unlockAutoFocus();

        //  撮影モードの更新
        //camera.updateTakeMode();
    }

    /**
     *   シャッターボタンが押された！
     *   （現在は、連続撮影モードについてはまだ非対応）
     */
    private void pushShutterButton()
    {
        // カメラ側のシャッターを押す
        camera.singleShot();
    }

    /**
     *   動画の撮影・停止を行う
     *
     */
    private void movieControl()
    {
        camera.movieControl();
    }

    /**
     *   AF/MFの切り替えを行う
     */
    private void toggleAfMf()
    {
        camera.toggleManualFocus();
    }

    /**
     *   グリッド表示の ON/OFFを切り替える
     *
     */
    private void changeShowGrid(int objectId)
    {
        liveImageView.toggleShowGridFrame();
        updateGridStatusButton(objectId);
    }

    /**
     * 　デジタル水準器の ON/OFFを切り替える
     *
     */
    private void changeShowLevelGauge()
    {
        liveImageView.toggleShowLevelGauge();
    }

    /**
     *   AE-Lock/Lock解除を行う
     *
     */
    private void changeAeLockMode()
    {
        camera.toggleAutoExposure();
    }

    /**
     *  グリッドフレームの表示・非表示ボタンを更新する
     *
     */
    private void updateGridStatusButton(int buttonId)
    {
        int btnResId;
        if (liveImageView.isShowGrid())
        {
            // グリッドがON状態、グリッドをOFFにするボタンを出す
            btnResId = R.drawable.btn_ic_grid_off;
        }
        else
        {
            //  グリッドがOFF状態、グリッドをONにするボタンを出す
            btnResId = R.drawable.btn_ic_grid_on;
        }
        statusDrawer.setButtonDrawable(buttonId, btnResId);
    }

    /**
     *　  露出補正を１段階下げる
     */
    private void changeExposureBiasValueDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.EXPOSURE_COMPENSATION);
    }

    /**
     *   露出補正を１段階あげる
     *
     */
    private void changeExposureBiasValueUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.EXPOSURE_COMPENSATION);
    }

    /**
     *　  絞り値を１段階下げる
     */
    private void changeApertureValueDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.APERTURE);
    }

    /**
     *   絞り値を１段階あげる
     *
     */
    private void changeApertureValueUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.APERTURE);
    }

    /**
     *　  シャッター速度を１段階下げる
     */
    private void changeShutterSpeedDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.SHUTTER_SPEED);
    }

    /**
     *   シャッター速度を１段階あげる
     *
     */
    private void changeShutterSpeedUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.SHUTTER_SPEED);
    }


    /**
     *　  仕上がり・ピクチャーモードを１段階下げる
     */
    private void changeColorToneDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.COLOR_TONE);
    }

    /**
     *   仕上がり・ピクチャーモードを１段階あげる
     *
     */
    private void changeColorToneUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.COLOR_TONE);
    }

    /**
     *   アートフィルターを１段階さげる
     *
     */
    private void changeArtFilterDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.ART_FILTER);
    }

    /**
     *   アートフィルターを１段階あげる
     *
     */
    private void changeArtFilterUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.ART_FILTER);
    }


    /**
     *   測光方式を１段階さげる
     *
     */
    private void changeAEModeDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.AE_MODE);
    }

    /**
     *   測光方式を１段階あげる
     *
     */
    private void changeAEModeUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.AE_MODE);
    }

    /**
     *   測光方式を更新する
     *
     */
    private void changeAEMode(int direction)
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.changeCameraProperty(IOlyCameraProperty.AE_MODE, direction);
    }

    /**
     *   ISO感度を１段階さげる
     *
     */
    private void changeIsoSensitivityDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.ISO_SENSITIVITY);
    }

    /**
     *   ISO感度を１段階あげる
     *
     */
    private void changeIsoSensitivityUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.ISO_SENSITIVITY);
    }


    /**
     *   ホワイトバランスを１段階さげる
     *
     */
    private void changeWhiteBalanceDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.WB_MODE);
    }

    /**
     *   ホワイトバランスを１段階あげる
     *
     */
    private void changeWhiteBalanceUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.WB_MODE);
    }

    /**
     *   動画撮影モードを１段階さげる
     *
     */
    private void changeMovieQualityModeDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.QUALITY_MOVIE);
    }

    /**
     *    動画撮影モードを１段階あげる
     *
     */
    private void changeMovieQualityModeUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.QUALITY_MOVIE);
    }


    /**
     *   動画撮影モードがショートムービーのときの撮影時間を１段階さげる
     *
     */
    private void changeShortMovieRecordLengthDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.SHORT_MOVIE_RECORD_TIME);
    }

    /**
     *    動画撮影モードがショートムービーのときの撮影時間を１段階あげる
     *
     */
    private void changeShortMovieRecordLengthUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.SHORT_MOVIE_RECORD_TIME);
    }


    /**
     *   動画撮影モードを１段階さげる
     *
     */
    private void changeMovieTakeModeDown()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyDown(IOlyCameraProperty.TAKE_MODE_MOVIE);
    }

    /**
     *    動画撮影モードを１段階あげる
     *
     */
    private void changeMovieTakeModeUp()
    {
        IOlyCameraPropertyProvider propertyProxy = camera.getCameraPropertyProvider();
        propertyProxy.updateCameraPropertyUp(IOlyCameraProperty.TAKE_MODE_MOVIE);
    }

    /**
     *   インターバル撮影（１枚）を行う
     *
     * @param waitSeconds  撮影待ち時間（単位：秒）
     */
    private void intervalOneShot(int waitSeconds)
    {
        camera.bracketingShot(IBracketingShotStyle.BRACKET_NONE, 1, waitSeconds);
    }

    /**
     *   ブラケット撮影を行う
     *
     * @param style  撮影スタイル
     * @param count  撮影枚数
     */
    private void bracketingShot(int style, int count)
    {
        camera.bracketingShot(style, count, 0);
    }


    /**
     *   「お気に入り設定」表示画面を開く
     *
     */
    private void showFavoriteDialog()
    {
        // お気に入り設定表示画面を開く
        statusDrawer.showFavoriteSettingsDialog();
    }

    /**
     *    ズームイン・ズームアウト操作を行う
     *
     * @param direction ズーム操作の方向
     *
     */
    private boolean driveZoomLens(int direction)
    {
        boolean isExecute = false;
        IZoomLensHolder zoom = camera.getZoomLensHolder();
        if (zoom != null)
        {
            zoom.updateStatus();
            if (zoom.canZoom())
            {
                // ズーム操作を行う
                try
                {
                    zoom.driveZoomLens(direction);
                    isExecute = true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return (isExecute);
    }

    private void resetDigitalZoom()
    {
        IZoomLensHolder zoom = camera.getZoomLensHolder();
        if (zoom != null)
        {
            zoom.updateStatus();
            zoom.changeDigitalZoomScale(1.0f, false);
        }
    }

    private boolean driveDigitalZoom(int zoomType)
    {
        boolean isExecute = false;
        IZoomLensHolder zoom = camera.getZoomLensHolder();
        if (zoom != null)
        {
            zoom.updateStatus();

            float magnify = zoomType;
            if (zoomType == 0)
            {
                magnify = 1.0f;
            }

            float currentScale = zoom.getCurrentDigitalZoomScale();
            float targetScale = currentScale + magnify * 0.5f;
            zoom.changeDigitalZoomScale(targetScale, (zoomType == 0));
            isExecute = true;
        }
        return (isExecute);
    }


    /**
     *   ライブビューのサイズを指定した倍率に拡大する（拡大中の場合にはもとに戻す。）
     *
     * @param scale  拡大倍率
     * @return  実行した場合true, 実行しなかった場合はfalse
     */
    private boolean changeLiveViewMagnify(int scale)
    {
        boolean isExecute = false;
        IZoomLensHolder zoom = camera.getZoomLensHolder();
        if (zoom != null)
        {
            isExecute = zoom.magnifyLiveView(scale);
        }
        return (isExecute);
    }

    /**
     *   設定画面を開く
     *
     */
    private void showSettingsScreen()
    {
        try
        {
            Log.v(TAG, " --- showSettingsScreen() ---");
            ConfirmationDialog confirmation = new ConfirmationDialog(activity);
            confirmation.show(R.string.change_title_from_opc_to_theta, R.string.change_message_from_opc_to_theta, new ConfirmationDialog.Callback() {
                @Override
                public void confirm() {
                    Log.v(TAG, " --- CONFIRMED! --- ");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
