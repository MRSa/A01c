package jp.sfjp.gokigen.a01c.liveview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.sfjp.gokigen.a01c.liveview.glview.IImageProvider;

/**
 *  OLYCameraLiveViewListener の実装
 *  （LiveViewFragment用）
 *
 */
public class CameraLiveViewListenerImpl implements OLYCameraLiveViewListener, IImageDataReceiver, IImageProvider
{
    private final String TAG = toString();
    private final IImageDataReceiver imageView;
    private byte[] imageData = null;

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
    public @Nullable Bitmap getImage()
    {
        try
        {
            if (imageData == null)
            {
                return (null);
            }
            return (BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public void setImageData(byte[] data, Map<String, Object> metadata)
    {
        // Log.v(TAG, " setImageData len : " + data.length);
        if (imageView != null)
        {
            imageView.setImageData(data, null);
        }
        imageData = null;
        imageData = data;
    }

}
