package io.github.alexmofer.android.support.concurrent;

import androidx.annotation.MainThread;

/**
 * 异常处理器
 * Created by Alex on 2024/3/2.
 */
public interface ExceptionHandler<V> {

    /**
     * 处理异常
     *
     * @param param 参数
     * @param e     异常
     */
    @MainThread
    void handle(V param, Throwable e);
}
