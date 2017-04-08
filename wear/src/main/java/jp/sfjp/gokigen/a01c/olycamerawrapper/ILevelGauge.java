package jp.sfjp.gokigen.a01c.olycamerawrapper;


import jp.co.olympus.camerakit.OLYCamera;

public interface ILevelGauge
{
    float LEVELGAUGE_THRESHOLD_MIDDLE = 2.0f;
    float LEVELGAUGE_THRESHOLD_OVER = 15.0f;

    enum LevelArea
    {
        LEVEL_HORIZONTAL,
        LEVEL_VERTICAL,
    };

    float getLevel(LevelArea area);
    int getLevelColor(float value);

    void checkLevelGauge(OLYCamera camera);
    void updateLevelGaugeChecking(boolean isWatch);
}
