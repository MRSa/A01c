package jp.sfjp.gokigen.a01c.liveview;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;

/**
 *  OLYCameraLiveViewListener の実装
 *  （LiveViewFragment用）
 *
 */
public class CameraLiveViewListenerImpl implements OLYCameraLiveViewListener, IImageDataReceiver
{
    private final String TAG = toString();
    private final IImageDataReceiver imageView;

    /**
     * コンストラクタ
     */
    public CameraLiveViewListenerImpl(@NonNull IImageDataReceiver target)
    {
        Log.v(toString(), "CameraLiveViewListenerImpl is created. : " + target.toString());
        this.imageView = target;
    }

    /**
     * LiveViewの画像データを更新する
     *
     */
    @Override
    public void onUpdateLiveView(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        if (imageView != null)
        {
            imageView.setImageData(data, metadata);
        }
    }

    @Override
    public void setImageData(byte[] data, Map<String, Object> metadata)
    {
        // Log.v(TAG, " setImageData len : " + data.length);
        if (imageView != null)
        {
            imageView.setImageData(data, null);
        }
    }
}
