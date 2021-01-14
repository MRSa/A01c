package jp.sfjp.gokigen.a01c;

import androidx.annotation.NonNull;

public interface ICameraStatusWatcher
{
    void prepareStatusWatch();
    void startStatusWatch(@NonNull ICameraStatusUpdateNotify notifier);
    void stopStatusWatch();
}
