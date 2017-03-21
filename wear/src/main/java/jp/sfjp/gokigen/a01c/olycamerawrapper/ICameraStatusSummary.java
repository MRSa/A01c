package jp.sfjp.gokigen.a01c.olycamerawrapper;


import jp.co.olympus.camerakit.OLYCamera;

public interface ICameraStatusSummary
{
    String getCameraStatusMessage(OLYCamera camera, String name);

}
