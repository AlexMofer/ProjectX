package io.github.alexmofer.android.support.concurrent;

/**
 * 任务
 * Created by Alex on 2024/3/2.
 */
public interface Task {
    int PRIORITY_LOWEST = 19;// 最低优先级，设置比该优先级低的将会被该值替代
    int PRIORITY_MIDDLE = 0; // 中优先级，比中优先级高的先进后执行，其他先进先执行
    int PRIORITY_HIGHEST = -19;// 最高优先级，设置比该优先级高的将会被该值替代

    /**
     * 获取优先级
     *
     * @return 优先级
     */
    int getPriority();

    /**
     * 设置优先级
     *
     * @param priority 优先级
     */
    void setPriority(int priority);

    /**
     * 判断任务是否结束
     *
     * @return 任务已执行结束时返回true
     */
    boolean isDone();

    /**
     * 取消
     *
     * @param mayInterruptIfRunning 运行时是否打断
     * @return 取消成功时返回true
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 判断是否已取消
     *
     * @return 已取消时返回true
     */
    boolean isCancelled();
}
