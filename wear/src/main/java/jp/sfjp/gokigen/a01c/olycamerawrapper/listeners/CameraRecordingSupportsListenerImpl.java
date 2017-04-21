package jp.sfjp.gokigen.a01c.olycamerawrapper.listeners;

import android.util.Log;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraRecordingSupportsListener;

public class CameraRecordingSupportsListenerImpl implements OLYCameraRecordingSupportsListener
{
    private final String TAG = toString();

    public CameraRecordingSupportsListenerImpl()
    {
         //
    }

    @Override
    public void onReadyToReceiveCapturedImagePreview(OLYCamera olyCamera)
    {
        Log.v(TAG, "onReadyToReceiveCapturedImagePreview()");
    }

    @Override
    public void onReceiveCapturedImagePreview(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map)
    {
        Log.v(TAG, "onReceiveCapturedImagePreview()");

    }

    @Override
    public void onFailToReceiveCapturedImagePreview(OLYCamera olyCamera, Exception e)
    {
        Log.v(TAG, "onFailToReceiveCapturedImagePreview()");

    }

    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera olyCamera)
    {
        Log.v(TAG, "onReadyToReceiveCapturedImage()");

    }

    @Override
    public void onReceiveCapturedImage(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map)
    {
        Log.v(TAG, "onReceiveCapturedImage()");

    }

    @Override
    public void onFailToReceiveCapturedImage(OLYCamera olyCamera, Exception e)
    {
        Log.v(TAG, "onFailToReceiveCapturedImage()");

    }

    @Override
    public void onStopDrivingZoomLens(OLYCamera olyCamera)
    {
        Log.v(TAG, "onStopDrivingZoomLens()");

    }
}
