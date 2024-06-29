package io.github.alexmofer.android.support.concurrent;

import androidx.annotation.MainThread;

/**
 * 回调
 * Created by Alex on 2024/3/2.
 */
public interface Callback<Param, Result> {

    /**
     * 完成
     *
     * @param param  参数
     * @param result 结果
     */
    @MainThread
    void complete(Param param, Result result);
}
