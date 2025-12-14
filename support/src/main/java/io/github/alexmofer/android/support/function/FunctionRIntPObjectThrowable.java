package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionRIntPObjectThrowable<P> {
    /**
     * 执行
     */
    int execute(P param) throws Exception;
}
