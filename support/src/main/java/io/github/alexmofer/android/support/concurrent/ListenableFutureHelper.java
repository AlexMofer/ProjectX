/*
 * Copyright (C) 2026 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.alexmofer.android.support.concurrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * ListenableFuture 辅助
 * Created by Alex on 2026/6/1.
 */
public final class ListenableFutureHelper {
    public static final int PRIORITY_VERY_HIGH = 2;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_VERY_LOW = -2;
    public static final int SORT_STACKS = 0;// 先进先出
    public static final int SORT_QUEUES = 1;// 先进后出
    private static final Object sJobThreadPoolLock = new Object();
    private static volatile ExecutorService sJobThreadPool;

    private ListenableFutureHelper() {
        //no instance
    }

    /**
     * 获取任务线程池
     *
     * @return 任务线程池
     */
    public static ExecutorService getTaskThreadPool() {
        if (sJobThreadPool == null) {
            synchronized (sJobThreadPoolLock) {
                if (sJobThreadPool == null) {
                    final int CPU = Runtime.getRuntime().availableProcessors();
                    final int corePoolSize = Math.max(2, Math.min(CPU - 1, 4));
                    final int maximumPoolSize = CPU * 2 + 1;
                    sJobThreadPool = new ThreadPoolExecutor(
                            corePoolSize,
                            maximumPoolSize,
                            30, TimeUnit.SECONDS,
                            new PriorityBlockingQueue<>(11, new InnerComparator()),
                            new InnerThreadFactory(),
                            new ThreadPoolExecutor.CallerRunsPolicy());

                    ((ThreadPoolExecutor) sJobThreadPool).allowCoreThreadTimeOut(true);
                }
            }
        }
        return sJobThreadPool;
    }

    /**
     * 设置任务线程池
     *
     * @param builder 线程池构建器
     */
    public static void setTaskThreadPool(@NonNull ExecutorServiceBuilder builder) {
        if (sJobThreadPool == null) {
            synchronized (sJobThreadPoolLock) {
                if (sJobThreadPool == null) {
                    sJobThreadPool = builder.build(
                            new PriorityBlockingQueue<>(11, new InnerComparator()),
                            new InnerThreadFactory(),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                    ((ThreadPoolExecutor) sJobThreadPool).allowCoreThreadTimeOut(true);
                }
            }
        }
    }

    /**
     * 获取UI线程执行器
     *
     * @return UI线程执行器
     */
    @NonNull
    public static UIThreadExecutor getUIThreadExecutor() {
        return UIThreadExecutor.getDefault();
    }

    /**
     * 提交异步任务
     *
     * @param worker   任务执行者
     * @param listener 监听执行者
     * @param task     任务执行回调
     * @param priority 优先级
     * @param sort     排序方式
     * @param success  任务成功回调
     * @param failure  任务失败回调
     * @param <T>      返回类型
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull Executor worker,
                                                 @NonNull Executor listener,
                                                 @NonNull Callable<T> task,
                                                 int priority, int sort,
                                                 @Nullable Consumer<T> success,
                                                 @Nullable Consumer<Throwable> failure) {
        final ListenableFuture<T> future = CallbackToFutureAdapter.getFuture(
                completer -> {
                    worker.execute(new InnerTask<>(completer, task, priority, sort));
                    return "submit_task_" + task.getClass().getName();
                });

        future.addListener(() -> {
            try {
                final T result = future.get();
                if (success != null) {
                    success.accept(result);
                }
            } catch (ExecutionException e) {
                if (failure != null) {
                    failure.accept(e.getCause());
                }
            } catch (Throwable t) {
                if (failure != null) {
                    failure.accept(t);
                }
            }
        }, listener);

        return future;
    }

    /**
     * 提交异步任务
     *
     * @param task     任务执行回调
     * @param priority 优先级
     * @param sort     排序方式
     * @param success  任务成功回调
     * @param failure  任务失败回调
     * @param <T>      返回类型
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull Callable<T> task,
                                                 int priority, int sort,
                                                 @Nullable Consumer<T> success,
                                                 @Nullable Consumer<Throwable> failure) {
        return submit(getTaskThreadPool(), getUIThreadExecutor(),
                task, priority, sort, success, failure);
    }

    /**
     * 提交异步任务
     *
     * @param task    任务执行回调
     * @param success 任务成功回调
     * @param failure 任务失败回调
     * @param <T>     返回类型
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull Callable<T> task,
                                                 @Nullable Consumer<T> success,
                                                 @Nullable Consumer<Throwable> failure) {
        return submit(task, PRIORITY_NORMAL, SORT_STACKS, success, failure);
    }

    /**
     * 提交异步任务
     *
     * @param task     任务执行回调
     * @param priority 优先级
     * @param sort     排序方式
     * @param success  任务成功回调
     * @param failure  任务失败回调
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull ThrowableRunnable task,
                                                 int priority, int sort,
                                                 @Nullable Runnable success,
                                                 @Nullable Consumer<Throwable> failure) {
        return submit(getTaskThreadPool(), getUIThreadExecutor(),
                () -> {
                    task.run();
                    return null;
                }, priority, sort,
                success == null ? null : unused -> success.run(),
                failure);
    }

    /**
     * 提交异步任务
     *
     * @param task    任务执行回调
     * @param success 任务成功回调
     * @param failure 任务失败回调
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull ThrowableRunnable task,
                                                 @Nullable Runnable success,
                                                 @Nullable Consumer<Throwable> failure) {
        return submit(task, PRIORITY_NORMAL, SORT_STACKS, success, failure);
    }

    /**
     * 提交异步任务
     *
     * @param task     任务执行回调
     * @param callback 任务回调
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull ThrowableRunnable task,
                                                 @Nullable Consumer<Throwable> callback) {
        final Runnable success = callback == null ? null : () -> callback.accept(null);
        return submit(task, success, callback);
    }

    /**
     * 提交异步任务
     *
     * @param task 任务执行回调
     * @return 异步任务
     * @noinspection UnusedReturnValue
     */
    @NonNull
    public static <T> ListenableFuture<T> submit(@NonNull ThrowableRunnable task) {
        return submit(task, null, null);
    }

    /**
     * 提交异步任务
     *
     * @param task     任务执行回调
     * @param priority 优先级
     * @param sort     排序方式
     */
    public static void execute(@NonNull Runnable task, int priority, int sort) {
        submit(() -> {
                    task.run();
                    return null;
                },
                priority, sort, null, null);
    }

    /**
     * 提交异步任务
     *
     * @param task 任务执行回调
     */
    public static void execute(@NonNull Runnable task) {
        execute(task, PRIORITY_NORMAL, SORT_STACKS);
    }

    @FunctionalInterface
    public interface ExecutorServiceBuilder {

        @NonNull
        ExecutorService build(@NonNull BlockingQueue<Runnable> workQueue,
                              @NonNull ThreadFactory threadFactory,
                              @NonNull RejectedExecutionHandler handler);
    }

    @FunctionalInterface
    public interface Callable<T> {
        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Throwable if unable to compute a result
         */
        @WorkerThread
        T call() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        /**
         * When an object implementing interface {@code Runnable} is used
         * to create a thread, starting the thread causes the object's
         * {@code run} method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method {@code run} is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        void run() throws Throwable;
    }

    private static class InnerTask<T> implements Runnable, Comparable<InnerTask<?>> {
        private final CallbackToFutureAdapter.Completer<T> mCompleter;
        private final Callable<T> mTask;
        private final long mTimestamp;
        private final int mPriority;
        private final int mSort;

        public InnerTask(@NonNull CallbackToFutureAdapter.Completer<T> completer,
                         @NonNull Callable<T> task,
                         int priority, int sort) {
            mCompleter = completer;
            mTask = task;
            mTimestamp = System.currentTimeMillis();
            mPriority = priority;
            mSort = sort;
        }

        @Override
        public void run() {
            try {
                mCompleter.set(mTask.call());
            } catch (Throwable throwable) {
                mCompleter.setException(throwable);
            }
        }

        @Override
        public int compareTo(InnerTask<?> other) {
            if (other == null) {
                return 1;
            }

            // 1. 先按照优先级排序（高优先级排在前面）
            if (mPriority != other.mPriority) {
                return Integer.compare(other.mPriority, mPriority);
            }

            // 2. 优先级相同时，按照排序方式处理
            if (mSort == other.mSort) {
                // 相同排序方式，按时间戳排序
                if (mSort == SORT_STACKS) {
                    // 先进先出，时间戳小的排前面
                    return Long.compare(mTimestamp, other.mTimestamp);
                } else {
                    // 后进先出，时间戳大的排前面
                    return Long.compare(other.mTimestamp, mTimestamp);
                }
            } else {
                // 不同排序方式：SORT_STACKS(0) 优先于 SORT_QUEUES(1)
                return Integer.compare(mSort, other.mSort);
            }
        }
    }

    private static class InnerComparator implements Comparator<Runnable> {

        @Override
        public int compare(Runnable o1, Runnable o2) {
            if (o1 instanceof InnerTask && o2 instanceof InnerTask) {
                return ((InnerTask<?>) o1).compareTo((InnerTask<?>) o2);
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
