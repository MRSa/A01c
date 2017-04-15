package jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;

/**
 *   ビデオ撮影の開始・終了制御クラス。
 *
 */
public class MovieRecordingControl implements OLYCamera.CompletedCallback
{
    private final String TAG = toString();
    private final Context context;
    private final OLYCamera camera;
    private final IShowInformation statusDrawer;

    /**
     *   コンストラクタ
     *
     */
    public MovieRecordingControl(Context context, OLYCamera camera, IShowInformation statusDrawer)
    {
        this.context = context;
        this.camera = camera;
        this.statusDrawer = statusDrawer;
    }

    /**
     *   動画撮影の開始と終了
     *
     */
    public void movieControl()
    {
        try
        {
            if (camera.isTakingPicture())
            {
                // スチル撮影中の場合は、何もしない（モード異常なので）
                Log.v(TAG, "NOW TAKING PICTURE(STILL) : COMMAND IGNORED");
                return;
            }
            if (!camera.isRecordingVideo())
            {
                // ムービー撮影の開始指示
                camera.startRecordingVideo(new HashMap<String, Object>(), this);
                statusDrawer.setMessage(IShowInformation.AREA_9, Color.RED, context.getString(R.string.video_recording));
            }
            else
            {
                // ムービー撮影の終了指示
                camera.stopRecordingVideo(this);
                statusDrawer.setMessage(IShowInformation.AREA_9, Color.WHITE, "");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   処理完了
     *
     */
    @Override
    public void onCompleted()
    {
        // 撮影終了をバイブレータで知らせる
        statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_MIDDLE);
    }

    /**
     *   エラー発生
     *
     * @param e 例外情報
     */
    @Override
    public void onErrorOccurred(OLYCameraKitException e)
    {
        // 撮影失敗をバイブレータで知らせる
        statusDrawer.vibrate(IShowInformation.VIBRATE_PATTERN_SIMPLE_SHORT);
        {
            // 撮影失敗の表示をToastで行う
            Toast.makeText(context, R.string.video_failure, Toast.LENGTH_SHORT).show();
        }
        e.printStackTrace();
    }
}
