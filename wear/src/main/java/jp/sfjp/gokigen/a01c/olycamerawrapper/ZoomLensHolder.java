package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.util.Log;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *   ズームレンズの情報を保持する
 *
 */
class ZoomLensHolder implements IZoomLensHolder
{
    private final String TAG = toString();
    private boolean canZoom = false;
    private float minimumLength = 0.0f;
    private float maximumLength = 0.0f;
    private float currentLength = 0.0f;
    private float minimumDigitalScale = 1.0f;
    private float maximumDigitalScale = 1.0f;
    private float currentDigitalScale = 1.0f;
    private final OLYCamera camera;

    ZoomLensHolder(OLYCamera camera)
    {
        this.camera = camera;
        //initialize();
    }

    private void initialize()
    {
        canZoom = false;
        try
        {
            if (camera == null)
            {
                return;
            }

            // デジタルズームの情報
            Map<String, Float> value = camera.getDigitalZoomScaleRange();
            maximumDigitalScale = value.get(OLYCamera.DIGITAL_ZOOM_SCALE_RANGE_MAXIMUM_KEY);
            minimumDigitalScale = value.get(OLYCamera.DIGITAL_ZOOM_SCALE_RANGE_MINIMUM_KEY);
            Log.v(TAG, "DIGITAL ZOOM SCALE : " + minimumDigitalScale + " - " + maximumDigitalScale);

            // 光学ズームの情報
            canZoom = (camera.getLensMountStatus()).contains("electriczoom");
            if (canZoom)
            {
                try
                {
                    minimumLength = camera.getMinimumFocalLength();
                    maximumLength = camera.getMaximumFocalLength();
                    currentLength = camera.getActualFocalLength();
                    Log.v(TAG, "OPTICAL ZOOM RANGE : " + minimumLength + " - " + maximumLength + " (" + currentLength + ")");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    @Override
    public boolean canZoom()
    {
        return (canZoom);
    }

    @Override
    public void updateStatus()
    {
        initialize();
    }

    @Override
    public float getMaximumFocalLength()
    {
        return (maximumLength);
    }

    @Override
    public float getMinimumFocalLength()
    {
        return (minimumLength);
    }

    @Override
    public float getCurrentFocalLength()
    {
        return (currentLength);
    }

    /**
     * ズームレンズを動作させる
     *
     * @param targetLength  焦点距離
     */
    @Override
    public void driveZoomLens(float targetLength)
    {
        try
        {
            // レンズがサポートする焦点距離と、現在の焦点距離を取得する
            float targetFocalLength = targetLength;

            // 焦点距離が最大値・最小値を超えないようにする
            if (targetFocalLength > maximumLength)
            {
                targetFocalLength = maximumLength;
            }
            if (targetFocalLength < minimumLength)
            {
                targetFocalLength = minimumLength;
            }

            // レンズのスーム操作
            Log.v(TAG, "ZOOM from " + currentLength + "mm to " + targetFocalLength + "mm");

            // ズーム動作中でない時には、レンズをズームさせる
            if (!camera.isDrivingZoomLens())
            {
                camera.startDrivingZoomLensToFocalLength(targetFocalLength);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ズームレンズを動作させる
     *
     * @param direction  (方向)
     */
    @Override
    public void driveZoomLens(int direction)
    {
        float mag = Math.abs(direction) * 1.5f;
        driveZoomLens((direction < 0) ? (currentLength * (0.88f / mag)) : (currentLength * 1.15f * mag));
    }

    /**
     * 現在ズーム中か確認する
     *
     * @return true : ズーム中  / false : ズーム中でない
     */
    @Override
    public boolean isDrivingZoomLens()
    {
        return  ((camera != null)&&(camera.isDrivingZoomLens()));
    }


    /**
     *   現在のデジタルズーム倍率を応答する
     *
     * @return  現在のデジタルズーム倍率
     */
    @Override
    public float getCurrentDigitalZoomScale()
    {
        return (currentDigitalScale);
    }

    /**
     *   デジタルズームを実行する
     *
     * @param scale ズーム倍率
     */
    @Override
    public void changeDigitalZoomScale(float scale, boolean isCyclic)
    {
        float zoomScale = scale;
        try
        {
            if (zoomScale <= minimumDigitalScale)
            {
                zoomScale = (isCyclic) ? maximumDigitalScale : minimumDigitalScale;
            }
            if (zoomScale >= maximumDigitalScale)
            {
                zoomScale = (isCyclic) ? minimumDigitalScale : maximumDigitalScale;
            }
            camera.changeDigitalZoomScale(zoomScale);
            currentDigitalScale = zoomScale;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
