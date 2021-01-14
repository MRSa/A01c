package jp.sfjp.gokigen.a01c.thetacamerawrapper.operation;

import androidx.annotation.Nullable;

public interface IOperationCallback
{
    void operationExecuted(int result, @Nullable String resultStr);
}
