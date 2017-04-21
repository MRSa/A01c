package jp.sfjp.gokigen.a01c.olycamerawrapper.listeners;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.co.olympus.camerakit.OLYCameraRecordingListener;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;


public class CameraRecordingListenerImpl implements OLYCameraRecordingListener
{
    private final String TAG = toString();
    private final Context context;
    private final IShowInformation statusDrawer;

    public CameraRecordingListenerImpl(Context context, IShowInformation statusDrawer)
    {
        this.context = context;
        this.statusDrawer = statusDrawer;
    }

    @Override
    public void onStartRecordingVideo(OLYCamera olyCamera)
    {
        Log.v(TAG, "onStartRecordingVideo()");
        statusDrawer.setMessage(IShowInformation.AREA_9, Color.RED, context.getString(R.string.video_recording));
    }

    @Override
    public void onStopRecordingVideo(OLYCamera olyCamera)
    {
        Log.v(TAG, "onStopRecordingVideo()");
        statusDrawer.setMessage(IShowInformation.AREA_9, Color.WHITE, "");
    }

    @Override
    public void onChangeAutoFocusResult(OLYCamera olyCamera, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
    {
        Log.v(TAG, "onStopRecordingVideo()");
    }
}
