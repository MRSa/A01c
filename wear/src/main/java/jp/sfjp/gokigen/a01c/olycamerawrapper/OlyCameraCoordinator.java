package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceManager;

import jp.co.olympus.camerakit.OLYCamera;

import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.sfjp.gokigen.a01c.ICameraConnection;
import jp.sfjp.gokigen.a01c.ICameraController;
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher;
import jp.sfjp.gokigen.a01c.ICameraStatusWatcher;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.liveview.CameraLiveViewListenerImpl;
import jp.sfjp.gokigen.a01c.liveview.IAutoFocusFrameDisplay;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;
import jp.sfjp.gokigen.a01c.liveview.ILiveImageStatusNotify;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.FeatureDispatcher;
import jp.sfjp.gokigen.a01c.olycamerawrapper.indicator.CameraStatusDisplay;
import jp.sfjp.gokigen.a01c.olycamerawrapper.indicator.ICameraStatusDisplay;
import jp.sfjp.gokigen.a01c.olycamerawrapper.listeners.CameraPropertyListenerImpl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.listeners.CameraRecordingListenerImpl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.listeners.CameraStatusListenerImpl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ILoadSaveCameraProperties;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraProperty;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.IOlyCameraPropertyProvider;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.LoadSaveCameraProperties;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.OlyCameraPropertyProxy;
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.AutoFocusControl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.BracketingShotControl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.MovieRecordingControl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture.SingleShotControl;
import jp.sfjp.gokigen.a01c.olycamerawrapper.property.CameraPropertyLoadSaveOperations;
import jp.sfjp.gokigen.a01c.preference.IPreferenceCameraPropertyAccessor;

/**
 *   OlyCameraCoordinator : Olympus Air との接続、切断の間をとりもつクラス。
 *                         (OLYCameraクラスの実体を保持する)
 *
 *    1. クラスを作成する
 *    2. connectWifi() でカメラと接続する
 *    3. disconnect() でカメラと切断する
 *
 *    X. onDisconnectedByError() でカメラの通信状態が変更されたことを受信する
 *    o. CameraInteractionCoordinator.ICameraCallback でカメラとの接続状態を通知する
 *
 */
public class OlyCameraCoordinator implements ICameraController, IIndicatorControl, ICameraRunMode, IOLYCameraObjectProvider
{
    private final String TAG = toString();
    private final IAutoFocusFrameDisplay focusFrameDisplay;
    private final OLYCamera camera;
    private final Activity context;

    // 本クラスの配下のカメラ制御クラス群
    private final AutoFocusControl autoFocus;
    private final SingleShotControl singleShot;
    private final MovieRecordingControl movieControl;
    private final BracketingShotControl bracketingShot;
    private final OlyCameraPropertyProxy propertyProxy;
    private final CameraPropertyLoadSaveOperations loadSaveOperations;
    private final LoadSaveCameraProperties loadSaveCameraProperties;
    private final OlyCameraConnection cameraConnection;
    private final ICameraStatusDisplay cameraStatusDisplay;
    private final LevelMeterHolder levelMeter;
    private final ZoomLensHolder zoomLensHolder;

    private boolean isManualFocus = false;
    private boolean isAutoFocusLocked = false;
    //private boolean isExposureLocked = false;

    private FeatureDispatcher featureDispatcher = null;


    /**
     * コンストラクタ
     */
    public OlyCameraCoordinator(Activity context, IAutoFocusFrameDisplay focusFrameDisplay, IShowInformation showInformation, ICameraStatusReceiver receiver)
    {
        this.context = context;
        this.focusFrameDisplay = focusFrameDisplay;

        // OLYMPUS CAMERA クラスの初期化、リスナの設定
        camera = new OLYCamera();
        camera.setContext(context.getApplicationContext());

        this.cameraConnection = new OlyCameraConnection(context, camera, receiver);
        camera.setConnectionListener(cameraConnection);

        // 本クラスの配下のカメラ制御クラス群の設定
        autoFocus = new AutoFocusControl(camera, focusFrameDisplay, this); // AF制御
        singleShot = new SingleShotControl(camera, focusFrameDisplay, this, showInformation);  // １枚撮影
        movieControl = new MovieRecordingControl(context, camera, showInformation); // ムービー撮影
        bracketingShot = new BracketingShotControl(camera, focusFrameDisplay, this, showInformation);  // ブラケッティング＆インターバル撮影
        propertyProxy = new OlyCameraPropertyProxy(camera); // カメラプロパティ
        cameraStatusDisplay = new CameraStatusDisplay(propertyProxy, showInformation);  // 画面表示
        this.levelMeter = new LevelMeterHolder(showInformation, PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IPreferenceCameraPropertyAccessor.SHOW_LEVEL_GAUGE_STATUS, false));  // デジタル水準器
        camera.setCameraStatusListener(new CameraStatusListenerImpl(context, cameraStatusDisplay, levelMeter));
        camera.setCameraPropertyListener(new CameraPropertyListenerImpl(cameraStatusDisplay));
        camera.setRecordingListener(new CameraRecordingListenerImpl(context, showInformation));
        loadSaveCameraProperties = new LoadSaveCameraProperties(context, propertyProxy, this);
        loadSaveOperations = new CameraPropertyLoadSaveOperations(context, loadSaveCameraProperties, cameraStatusDisplay);
        zoomLensHolder = new ZoomLensHolder(camera);
    }

    @Override
    public void connectFinished()
    {
        // 何もしない
    }

    @Override
    public void setLiveViewListener(@NonNull CameraLiveViewListenerImpl listener)
    {
        Log.v(TAG, "setLiveViewListener()");
        try
        {
            camera.setLiveViewListener(listener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの解像度を設定する
     *
     */
    @Override
    public void changeLiveViewSize(String size)
    {
        Log.v(TAG, "changeLiveViewSize() : " + size);
        try
        {
            camera.changeLiveViewSize(CameraPropertyUtilities.toLiveViewSizeType(size));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの開始
     *
     */
    @Override
    public void startLiveView()
    {
        Log.v(TAG, "startLiveView()");
        try
        {
            camera.startLiveView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの終了
     *
     */
    @Override
    public void stopLiveView()
    {
        Log.v(TAG, "stopLiveView()");
        try
        {
            camera.stopLiveView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   撮影モードの更新
     *
     */
    @Override
    public void updateTakeMode()
    {
        cameraStatusDisplay.updateTakeMode();
    }

    /**
     *   フォーカスロックの実行
     */
    public boolean driveAutoFocus(MotionEvent event)
    {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
        {
            return (false);
        }
        PointF point = focusFrameDisplay.getPointWithEvent(event);
        return ((focusFrameDisplay.isContainsPoint(point)) && autoFocus.lockAutoFocus(point));
    }

    /**
     *   オートフォーカスのポイントに含まれているか確認する (trueだったら含まれる)
     *
     */
    @Override
    public boolean isContainsAutoFocusPoint(MotionEvent event)
    {
        //// 以下の機能は、タッチイベントの受信時のみ動作させる
        //if (event.getAction() != MotionEvent.ACTION_DOWN)
        //{
        //    return (false);
        //}
        try
        {
            // タッチした位置を取得する
            PointF point = focusFrameDisplay.getPointWithEvent(event);

            // AFの有効座標を取得する
            RectF rect = camera.getAutoFocusEffectiveArea();
            //Log.v(TAG, "AF POINT (" + point.x + "," + point.y + ")");
            //Log.v(TAG, "AF EFFECTIVE AREA (" + rect.left + "," + rect.left + ") - (" + rect.right + "," + rect.bottom + ")");
            return (rect.contains(point.x, point.y));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    /**
     *   フォーカスロックの解除
     */
    public void unlockAutoFocus()
    {
        autoFocus.unlockAutoFocus();
        focusFrameDisplay.hideFocusFrame();
        isAutoFocusLocked = false;
    }

    /**
     *   画像を１枚撮影
     */
    @Override
    public void singleShot()
    {
        singleShot.singleShot();
        {
            // 撮影の表示をToastで行う (成功とか失敗とか言っていない)
            Toast.makeText(context, R.string.shoot_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *   ムービーの撮影・停止
     */
    @Override
    public void movieControl()
    {
        movieControl.movieControl();
    }

    /**
     *   インターバル＆ブラケッティング撮影の実行
     *
     *     @param bracketingStyle : ブラケッティングスタイル
     *     @param bracketingCount : 撮影枚数
     *     @param durationSeconds : 撮影間隔（単位：秒）
     *
     */
    @Override
    public void bracketingShot(int bracketingStyle, int bracketingCount, int durationSeconds)
    {
        bracketingShot.bracketingShot(bracketingStyle, bracketingCount, durationSeconds);
    }

    /**
     *   撮影確認画像を生成するか設定する
     *
     */
    @Override
    public void setRecViewMode(boolean isRecViewMode)
    {
        try
        {
            String value = "<" + IOlyCameraProperty.REC_PREVIEW + "/";
            if (isRecViewMode)
            {
                value = value + "ON>";
            }
            else
            {
                value = value + "OFF>";
            }
            propertyProxy.setCameraPropertyValue(IOlyCameraProperty.REC_PREVIEW, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void toggleAutoExposure()
    {
        try
        {
            if (isAELock())
            {
                Log.v(TAG, "toggleAutoExposure() : unlockAutoExposure()");
                camera.unlockAutoExposure();
            }
            else
            {
                Log.v(TAG, "toggleAutoExposure() : lockAutoExposure()");
                camera.lockAutoExposure();
            }
            updateIndicatorScreen(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        cameraStatusDisplay.updateAeLockState();
    }

    /**
     *   AF/MF の切り替えを行う
     *
     */
    @Override
    public void toggleManualFocus()
    {
        try
        {
            boolean isHideFocusFrame = false;
            String property_name = IOlyCameraProperty.FOCUS_STILL;
            String poverty_value = "<" + IOlyCameraProperty.FOCUS_STILL + "/";

            // マニュアルフォーカス切替え
            if (!isManualFocus)
            {
                // AF -> MF  : オートフォーカスを解除して設定する
                Log.v(TAG, "toggleManualFocus() : to " + IOlyCameraProperty.FOCUS_MF);
                poverty_value = poverty_value + IOlyCameraProperty.FOCUS_MF + ">";
                camera.unlockAutoFocus();
                camera.setCameraPropertyValue(property_name, poverty_value);
                isHideFocusFrame = true;
            }
            else
            {
                // MF -> AF
                Log.v(TAG, "toggleManualFocus() : to " + IOlyCameraProperty.FOCUS_SAF);
                poverty_value = poverty_value + IOlyCameraProperty.FOCUS_SAF + ">";
                camera.setCameraPropertyValue(property_name, poverty_value);
            }
            updateIndicatorScreen(isHideFocusFrame);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateIndicatorScreen(boolean isHideFocusFrame)
    {
        isManualFocus();
        if (isHideFocusFrame)
        {
            isAutoFocusLocked = false;
            focusFrameDisplay.hideFocusFrame();
        }
        cameraStatusDisplay.updateCameraStatus();
    }

    @Override
    public boolean isManualFocus()
    {
        isManualFocus = propertyProxy.isManualFocus();
        return (isManualFocus);
    }

    @Override
    public boolean isAFLock()
    {
        return (isAutoFocusLocked);
    }

    @Override
    public boolean isAELock()
    {
        return (propertyProxy.isExposureLocked());
    }

/*
    @Override
    public void setCameraStatusListener(OLYCameraStatusListener listener)
    {
        camera.setCameraStatusListener(listener);
    }

    @Override
    public String getCameraStatusSummary(ICameraStatusSummary decoder)
    {
        return (decoder.getCameraStatusMessage(camera, ""));
    }
*/

    /**
     *   ステータス表示をすべて更新する
     *
     */
    @Override
    public void updateStatusAll()
    {
        cameraStatusDisplay.updateCameraStatusAll();
    }

    @Nullable
    @Override
    public ICameraStatusWatcher getStatusWatcher()
    {
        return (null);
    }

    @Override
    public void changeRunMode(boolean isRecording)
    {
        OLYCamera.RunMode runMode = (isRecording) ? OLYCamera.RunMode.Recording : OLYCamera.RunMode.Playback;
        Log.v(TAG, "changeRunMode() : " + runMode);
        try
        {
            camera.changeRunMode(runMode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecordingMode()
    {
        boolean isRecordingMode = false;
        try
        {
            OLYCamera.RunMode runMode = camera.getRunMode();
            isRecordingMode =  (runMode == OLYCamera.RunMode.Recording);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return (isRecordingMode);
    }

    @Override
    public IOlyCameraPropertyProvider getCameraPropertyProvider()
    {
        return (propertyProxy);
    }

    @Override
    public ICameraPropertyLoadSaveOperations getCameraPropertyLoadSaveOperations()
    {
        return (loadSaveOperations);
    }

    @Override
    public ILoadSaveCameraProperties getLoadSaveCameraProperties()
    {
        return (loadSaveCameraProperties);
    }

    @Override
    public ICameraRunMode getChangeRunModeExecutor()
    {
        return (this);
    }

    @Override
    public ICameraConnection getConnectionInterface()
    {
        return (cameraConnection);
    }

    @Override
    public IZoomLensHolder getZoomLensHolder()
    {
        return (zoomLensHolder);
    }

    @Override
    public ILevelGauge getLevelGauge()
    {
        return (levelMeter);
    }

    @Override
    public @NonNull ICameraFeatureDispatcher getFeatureDispatcher(@NonNull AppCompatActivity context, @NonNull IShowInformation statusDrawer, @NonNull ICameraController camera, @NonNull PreferenceDataStore preferenceAccessWrapper, @NonNull ILiveImageStatusNotify liveImageView)
    {
        if (featureDispatcher == null)
        {
            featureDispatcher = new FeatureDispatcher(context, statusDrawer, camera, preferenceAccessWrapper, liveImageView);
        }
        return (featureDispatcher);
    }

    @Override
    public void onAfLockUpdate(boolean isAfLocked)
    {
        isAutoFocusLocked = isAfLocked;
        updateIndicatorScreen(false);
    }

    @Override
    public void onShootingStatusUpdate(shootingStatus status)
    {
        Log.v(TAG, " ShootingStatus : " + status);
    }

    @Override
    public OLYCamera getOLYCamera()
    {
        return (camera);
    }

}
