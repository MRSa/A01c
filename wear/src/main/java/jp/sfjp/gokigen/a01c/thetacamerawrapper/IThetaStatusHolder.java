package jp.sfjp.gokigen.a01c.thetacamerawrapper;

import androidx.annotation.NonNull;

public interface IThetaStatusHolder
{
    void setCaptureMode(@NonNull String captureMode);
    @NonNull String getCaptureMode();
}
