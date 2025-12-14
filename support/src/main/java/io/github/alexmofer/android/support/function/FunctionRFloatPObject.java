package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionRFloatPObject<P> {
    /**
     * 执行
     */
    float execute(P param);
}
