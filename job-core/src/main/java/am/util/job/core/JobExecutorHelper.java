/*
 * Copyright (C) 2021 AlexMofer
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
package am.util.job.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务执行者辅助
 * Created by Alex on 2021/3/1.
 */
final class JobExecutorHelper {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Job #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> QUEUE =
            new PriorityBlockingQueue<>(128);

    private static final BlockingQueue<Runnable> SINGLE_QUEUE =
            new PriorityBlockingQueue<>(128);

    /**
     * An {@link Executor} that can be used to execute jobs in parallel.
     */
    private static JobExecutor JOB_EXECUTOR;
    /**
     * An {@link Executor} that can be used to execute jobs in parallel.
     */
    private static JobExecutor SINGLE_EXECUTOR;

    private JobExecutorHelper() {
        //no instance
    }

    static JobExecutor getDefault() {
        if (JOB_EXECUTOR == null) {
            JOB_EXECUTOR = new JobExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    QUEUE, THREAD_FACTORY);
            JOB_EXECUTOR.allowCoreThreadTimeOut(true);
        }
        return JOB_EXECUTOR;
    }

    static JobExecutor getSingle() {
        if (SINGLE_EXECUTOR == null) {
            SINGLE_EXECUTOR = new JobExecutor(1, 1,
                    KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, SINGLE_QUEUE, THREAD_FACTORY);
            SINGLE_EXECUTOR.allowCoreThreadTimeOut(true);
        }
        return SINGLE_EXECUTOR;
    }
}
