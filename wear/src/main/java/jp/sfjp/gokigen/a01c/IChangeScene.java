package jp.sfjp.gokigen.a01c;

/**
 *
 */
public interface IChangeScene
{
    void exitApplication();
    boolean showConnectionStatus();
    boolean checkConnectionFeature(int id, int btnId);
    boolean touchedPosition(float posX, float posY);
}
