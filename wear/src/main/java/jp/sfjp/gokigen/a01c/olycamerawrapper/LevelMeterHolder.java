package jp.sfjp.gokigen.a01c.olycamerawrapper;

import android.graphics.Color;
import android.util.Log;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.sfjp.gokigen.a01c.IShowInformation;

class LevelMeterHolder implements ILevelGauge
{
    private final String TAG = toString();
    private final IShowInformation vibrationNotifier;
    private final float GAUGE_FLAT_THRESHOLD = 1.1f;
    private final float GAUGE_SENSITIVITY = 0.3f;
    private float prevRoll = 0.0f;
    private float prevPitch = 0.0f;
    private String prevOrientation = "";

    private float roll = Float.NaN;
    private float pitch = Float.NaN;
    private String orientation = "";
    private boolean isFlat = false;
    private boolean isWatchingLevelGauge = false;

    /**
     *   コンストラクタ
     *
     */
    LevelMeterHolder(IShowInformation vibrationNotifier, boolean initialValue)
    {
        // 初期値
        isWatchingLevelGauge = initialValue;
        this.vibrationNotifier = vibrationNotifier;
    }
    /**
     *   レベルゲージの情報確認
     *
     *
     */
    public void checkLevelGauge(OLYCamera camera)
    {
        if (!isWatchingLevelGauge)
        {
            // レベルゲージの監視はしない
            // Log.v(TAG, "checkLevelGauge() : not watch");  // 結構ログが出るので消す
            return;
        }
        try
        {
            Map<String, Object> levelGauge = camera.getLevelGauge();
            float roll = (float) levelGauge.get(OLYCamera.LEVEL_GAUGE_ROLLING_KEY);
            float pitch = (float) levelGauge.get(OLYCamera.LEVEL_GAUGE_PITCHING_KEY);
            float sensitivity = GAUGE_SENSITIVITY;
            String orientation = (String) levelGauge.get(OLYCamera.LEVEL_GAUGE_ORIENTATION_KEY);

            // 差動が一定以上あったら報告する
            boolean diffOrientation = prevOrientation.equals(orientation);
            float diffRoll = Math.abs(roll - prevRoll);
            float diffPitch = Math.abs(pitch - prevPitch);
            if ((!diffOrientation)||((!Float.isNaN(roll))&&(diffRoll > sensitivity))||((!Float.isNaN(pitch))&&(diffPitch > sensitivity)))
            {
                // 差動が大きいので変動があったと報告する
                this.orientation = orientation;
                this.roll = roll;
                this.pitch = pitch;
                prevOrientation = orientation;
                prevRoll = roll;
                prevPitch = pitch;
            }
            //else
            //{
            // 差動レベルが一定以下の場合は、報告しない
            //Log.v(TAG, "Level Gauge: " + orientation + "[" + roll + "(" + diffRoll + ")" +  "," + pitch + "(" + diffPitch + ")]");
            //}

            // 水平になったかどうかの処理...
            if (Math.abs(roll) < GAUGE_FLAT_THRESHOLD)
            {
                if (!isFlat)
                {
                    // rollがフラットになったよ通知 ... バイブレータでぶるぶるさせる。。。(あんまり役に立たない感じだが。。) 入れるかどうか考える。
                    vibrationNotifier.vibrate(IShowInformation.VIBRATE_PATTERN_SHORT_DOUBLE);
                    Log.v(TAG, "Level Gauge (FLAT) : " + orientation + "[" + roll + "(" + diffRoll + ")" +  "," + pitch + "(" + diffPitch + ")]");
                }
                isFlat = true;
            }
            else
            {
                // フラット状態から外れたとき...
                isFlat = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLevelGaugeChecking(boolean isWatch)
    {
        isWatchingLevelGauge = isWatch;
    }

    @Override
    public float getLevel(LevelArea area)
    {
        float value;
        if (area == LevelArea.LEVEL_HORIZONTAL)
        {
            value = roll;
        }
        else //  if (area == LevelArea.LEVEL_VERTICAL)
        {
            value = pitch;
        }
        return (value);
    }

    @Override
    public int getLevelColor(float value)
    {
        value = Math.abs(value);

        if (value < LEVELGAUGE_THRESHOLD_MIDDLE)
        {
            return (Color.GREEN);
        }
        if (value > LEVELGAUGE_THRESHOLD_OVER)
        {
            return (Color.RED);
        }
        return (Color.YELLOW);
    }

}
