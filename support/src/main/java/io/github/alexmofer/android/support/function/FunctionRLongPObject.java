package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionRLongPObject<P> {
    /**
     * 执行
     */
    long execute(P param);
}
