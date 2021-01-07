package jp.sfjp.gokigen.a01c.liveview;

import java.util.Map;

public interface IImageDataReceiver
{
    void setImageData(byte[] data, Map<String, Object> metadata);
}
