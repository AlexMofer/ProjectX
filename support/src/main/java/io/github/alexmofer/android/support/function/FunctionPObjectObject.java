package io.github.alexmofer.android.support.function;

/**
 * 方法
 * Created by Alex on 2025/12/11.
 */
@FunctionalInterface
public interface FunctionPObjectObject<P1, P2> {
    /**
     * 执行
     */
    void execute(P1 param1, P2 param2);
}
