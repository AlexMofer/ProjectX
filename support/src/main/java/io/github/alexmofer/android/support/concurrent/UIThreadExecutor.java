package io.github.alexmofer.android.support.concurrent;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.Executor;

/**
 * UI线程执行者
 * Created by Alex on 2025/2/17.
 */
public class UIThreadExecutor implements Executor {
    private static UIThreadExecutor sDefault;
    private final Handler mHandler;

    public UIThreadExecutor() {
        mHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    public static UIThreadExecutor getDefault() {
        if (sDefault == null) {
            sDefault = new UIThreadExecutor();
        }
        return sDefault;
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }

    /**
     * 获取 Handler
     *
     * @return Handler
     */
    public Handler getHandler() {
        return mHandler;
    }
}