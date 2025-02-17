package io.github.alexmofer.android.support.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * UI线程执行者
 * Created by Alex on 2025/2/17.
 */
public class UIThreadExecutor extends Handler implements Executor {

    private static UIThreadExecutor sExecutor;

    private UIThreadExecutor() {
        super(Looper.getMainLooper());
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static UIThreadExecutor get() {
        synchronized (UIThreadExecutor.class) {
            if (sExecutor == null) {
                sExecutor = new UIThreadExecutor();
            }
            return sExecutor;
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        final Object obj = msg.obj;
        if (obj instanceof Runnable) {
            ((Runnable) obj).run();
        }
    }

    @Override
    public void execute(Runnable command) {
        obtainMessage(0, command).sendToTarget();
    }
}
