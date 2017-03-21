package jp.sfjp.gokigen.a01c.olycamerawrapper;

import java.util.Map;

/**
 *
 *
 */
public interface ICameraHardwareStatus
{
    String getLensMountStatus();
    String getMediaMountStatus();

    float getMinimumFocalLength();
    float getMaximumFocalLength();
    float getActualFocalLength();

    Map<String, Object> inquireHardwareInformation();
}
