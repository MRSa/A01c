package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

import jp.sfjp.gokigen.a01c.liveview.IAutoFocusFrameDisplay;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusDisplay;
import jp.sfjp.gokigen.a01c.liveview.ICameraStatusReceiver;

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
public class OlyCameraCoordinator implements IOlyCameraCoordinator, IIndicatorControl, ICameraRunMode, IOLYCameraObjectProvider
{
    private final String TAG = toString();
    private final IAutoFocusFrameDisplay focusFrameDisplay;
    private final ICameraStatusDisplay cameraStatusDisplay;
    private final OLYCamera camera;

    // 本クラスの配下のカメラ制御クラス群
    private final AutoFocusControl autoFocus;
    private final SingleShotControl singleShot;
    private final OlyCameraPropertyProxy propertyProxy;
    private final LoadSaveCameraProperties loadSaveCameraProperties;
    private final OlyCameraConnection cameraConnection;


    private boolean isManualFocus = false;
    private boolean isAutoFocusLocked = false;
    private boolean isExposureLocked = false;

    /**
     * コンストラクタ
     */
    public OlyCameraCoordinator(Activity context, IAutoFocusFrameDisplay focusFrameDisplay, ICameraStatusDisplay cameraStatusDisplay, ICameraStatusReceiver receiver)
    {
        this.focusFrameDisplay = focusFrameDisplay;
        this.cameraStatusDisplay = cameraStatusDisplay;

        // OLYMPUS CAMERA クラスの初期化、リスナの設定
        camera = new OLYCamera();
        camera.setContext(context.getApplicationContext());

        this.cameraConnection = new OlyCameraConnection(context, camera, receiver);
        camera.setConnectionListener(cameraConnection);

        // 本クラスの配下のカメラ制御クラス群の設定
        autoFocus = new AutoFocusControl(camera, focusFrameDisplay, this); // AF制御
        singleShot = new SingleShotControl(camera, focusFrameDisplay, this);  // 撮影
        propertyProxy = new OlyCameraPropertyProxy(camera); // カメラプロパティ

        loadSaveCameraProperties = new LoadSaveCameraProperties(context, propertyProxy, this);
    }

    /**
     * ライブビューの設定
     */
    public void setLiveViewListener(OLYCameraLiveViewListener listener)
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
    public void singleShot() {
        singleShot.singleShot();
    }

    @Override
    public void toggleAutoExposure()
    {
        try
        {
            if (isExposureLocked)
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
        isExposureLocked = propertyProxy.isExposureLocked();
        return (isExposureLocked);
    }

    @Override
    public void setCameraStatusListener(OLYCameraStatusListener listener)
    {
        camera.setCameraStatusListener(listener);
    }

    @Override
    public String getCameraStatusSummary(ICameraStatusSummary decoder)
    {
        return (decoder.geCameraStatusMessage(camera, ""));
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
    public IOlyCameraConnection getConnectionInterface()
    {
        return (cameraConnection);
    }

    @Override
    public void onAfLockUpdate(boolean isAfLocked)
    {
        isAutoFocusLocked = isAfLocked;
        updateIndicatorScreen(false);
    }

    @Override
    public OLYCamera getOLYCamera()
    {
        return (camera);
    }

}
