package io.github.alexmofer.android.support.concurrent;

/**
 * 请求
 * Created by Alex on 2024/3/2.
 */
public interface Callable<Param, Result> {

    /**
     * 执行
     *
     * @param param 参数
     * @return 结果
     */
    Result call(Param param);
}
