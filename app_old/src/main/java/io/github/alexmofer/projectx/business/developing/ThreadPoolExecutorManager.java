package io.github.alexmofer.projectx.business.developing;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 应用线程池管理器
 * Created by Alex on 2024/3/1.
 */
public class ThreadPoolExecutorManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));// 核心数至少2个至多4个
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static ExecutorService sJobThreadPool;

    private ThreadPoolExecutorManager() {
        //no instance
    }

    public static ExecutorService getJobThreadPool() {
        if (sJobThreadPool == null) {
            sJobThreadPool = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE,
                    TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>(3, new InnerComparator()),
                    new InnerThreadFactory());
        }
        return sJobThreadPool;
    }

    private static class InnerComparator implements Comparator<Runnable> {

        @Override
        public int compare(Runnable o1, Runnable o2) {
            if (o1 instanceof Comparable && o2 instanceof Comparable) {
                //noinspection unchecked,rawtypes
                return ((Comparable) o1).compareTo(o2);
            }
            return 0;
        }
    }

    private static class InnerThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Job #" + mCount.getAndIncrement()) {

                @Override
                public void run() {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    super.run();
                }
            };
        }
    }
}
