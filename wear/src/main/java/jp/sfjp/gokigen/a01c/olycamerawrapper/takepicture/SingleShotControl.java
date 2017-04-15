package jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture;

import android.graphics.RectF;
import android.util.Log;

import java.util.HashMap;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.liveview.IAutoFocusFrameDisplay;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IIndicatorControl;

/**
 *   一枚撮影用のクラス
 *
 * Created by MRSa on 2016/06/18.
 */
public class SingleShotControl implements OLYCamera.TakePictureCallback
{
    private final String TAG = toString();
    //private final Context context;
    private final OLYCamera camera;
    private final IIndicatorControl indicator;
    private final IAutoFocusFrameDisplay frameDisplayer;
    private final IShowInformation statusDrawer;

    /**
     *  コンストラクタ
     *
     */
    public SingleShotControl(OLYCamera camera, IAutoFocusFrameDisplay frameInfo, IIndicatorControl indicator, IShowInformation statusDrawer)
    {
        //this.context = context;
        this.camera = camera;
        this.indicator = indicator;
        this.frameDisplayer = frameInfo;
        this.statusDrawer = statusDrawer;
    }

    /**
     *   1枚撮影する
     *
     */
    public void singleShot()
    {
        try
        {
            camera.takePicture(new HashMap<String, Object>(), this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgress(OLYCamera olyCamera, OLYCamera.TakingProgress takingProgress, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
    {
        if (takingProgress != OLYCamera.TakingProgress.EndFocusing)
        {
            return;
        }

        String result = olyCameraAutoFocusResult.getResult();
        if (result == null)
        {
            Log.v(TAG, "FocusResult is null.");
        }
        else switch (result)
        {
            case "ok":
                RectF postFocusFrameRect = olyCameraAutoFocusResult.getRect();
                if (postFocusFrameRect != null)
                {
                    showFocusFrame(postFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Focused, 0.0);
                }
                break;

            case "none":
            default:
                hideFocusFrame();
                break;
        }
    }

    @Override
    public void onCompleted()
    {
        try
        {
            camera.clearAutoFocusPoint();
            hideFocusFrame();
            // 撮影成功をバイブレータで知らせる
            statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE);
            {
                // 撮影成功の表示をToastで行う
                //Toast.makeText(context, R.string.shoot_camera, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorOccurred(Exception e)
    {
        try
        {
            camera.clearAutoFocusPoint();
            hideFocusFrame();
            // 撮影失敗をバイブレータで知らせる
            statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT);
            {
                // 撮影失敗の表示をToastで行う
                //Toast.makeText(context, R.string.shoot_camera_failure, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        e.printStackTrace();
    }

    private void showFocusFrame(RectF rect, IAutoFocusFrameDisplay.FocusFrameStatus status, double duration)
    {
        if (frameDisplayer != null)
        {
            frameDisplayer.showFocusFrame(rect, status, duration);
        }
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused == status);
    }

    private void hideFocusFrame()
    {
        if (frameDisplayer != null)
        {
            frameDisplayer.hideFocusFrame();
        }
        indicator.onAfLockUpdate(false);
    }
}
