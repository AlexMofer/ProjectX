package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionRObjectPObjectThrowable<R, P> {
    /**
     * 执行
     */
    R execute(P param) throws Exception;
}
