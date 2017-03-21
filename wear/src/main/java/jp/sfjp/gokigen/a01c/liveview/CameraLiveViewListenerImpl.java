package jp.sfjp.gokigen.a01c.liveview;

import android.util.Log;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;

/**
 *  OLYCameraLiveViewListener の実装
 *  （LiveViewFragment用）
 *
 */
public class CameraLiveViewListenerImpl implements OLYCameraLiveViewListener
{
    private final String TAG = toString();
    private final IImageDataReceiver imageView;

    /**
     * コンストラクタ
     */
    public CameraLiveViewListenerImpl(IImageDataReceiver target)
    {
        Log.v(TAG, "CameraLiveViewListenerImpl is created. ; " + target.toString());
        this.imageView = target;
        //
    }

    /**
     * LiveViewの画像データを更新する
     *
     */
    @Override
    public void onUpdateLiveView(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        //Log.v(TAG, "onUpdateLiveView()");
        if (imageView != null)
        {
            imageView.setImageData(data, metadata);
        }
    }

    /**
     * 　 CameraLiveImageView
     */
    interface IImageDataReceiver
    {
        void setImageData(byte[] data, Map<String, Object> metadata);
    }
}
