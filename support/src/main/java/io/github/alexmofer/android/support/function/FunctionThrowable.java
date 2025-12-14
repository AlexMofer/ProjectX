package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionThrowable<R> {
    /**
     * 执行
     */
    void execute() throws Exception;
}
