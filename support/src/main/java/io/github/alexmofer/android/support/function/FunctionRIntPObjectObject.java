package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionRIntPObjectObject<P1, P2> {
    /**
     * 执行
     */
    int execute(P1 param1, P2 param2);
}
