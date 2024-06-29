package io.github.alexmofer.projectx.business.developing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.github.alexmofer.android.support.concurrent.PriorityThreadFactory;

/**
 * 应用线程池管理器
 * Created by Alex on 2024/3/1.
 */
public class ThreadPoolExecutorManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));// 核心数至少2个至多4个
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static ExecutorService sRenderingThreadPool;
    private static ExecutorService sJobThreadPool;
    private static ExecutorService sJobSingleThreadPool;

    private ThreadPoolExecutorManager() {
        //no instance
    }

    public static ExecutorService getRenderingThreadPool() {
        if (sRenderingThreadPool == null) {
            sRenderingThreadPool = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE,
                    TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>(),
                    new PriorityThreadFactory(android.os.Process.THREAD_PRIORITY_BACKGROUND
                            + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE
                            + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE,
                            "Rendering"));
        }
        return sRenderingThreadPool;
    }

    public static ExecutorService getJobThreadPool() {
        if (sJobThreadPool == null) {
            sJobThreadPool = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE,
                    TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>(),
                    new PriorityThreadFactory(android.os.Process.THREAD_PRIORITY_BACKGROUND,
                            "Job"));
        }
        return sJobThreadPool;
    }

    public static ExecutorService getJobSingleThreadPool() {
        if (sJobSingleThreadPool == null) {
            sJobSingleThreadPool = new ThreadPoolExecutor(1, 1,
                    KEEP_ALIVE,
                    TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>(),
                    new PriorityThreadFactory(android.os.Process.THREAD_PRIORITY_BACKGROUND,
                            "Job Single"));
        }
        return sJobSingleThreadPool;
    }
}
