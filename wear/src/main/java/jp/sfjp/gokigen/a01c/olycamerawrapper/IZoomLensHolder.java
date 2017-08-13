package jp.sfjp.gokigen.a01c.olycamerawrapper;

/**
 *  ズームレンズの状態
 *
 */

public interface IZoomLensHolder
{
    boolean canZoom();
    void updateStatus();
    float getMaximumFocalLength();
    float getMinimumFocalLength();
    float getCurrentFocalLength();
    void driveZoomLens(float targetLength);
    void driveZoomLens(int direction);
    boolean isDrivingZoomLens();
    float getCurrentDigitalZoomScale();
    void changeDigitalZoomScale(float scale, boolean isCyclic);
}
