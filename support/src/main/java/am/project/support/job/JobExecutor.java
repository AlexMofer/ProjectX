/*
 * Copyright (C) 2015 AlexMofer
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

package am.project.support.job;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Executor
 * Created by Alex on 2017/9/11.
 */

class JobExecutor extends ThreadPoolExecutor
        implements Handler.Callback {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        @SuppressWarnings("all")
        public Thread newThread(Runnable r) {
            return new Thread(r, "Job #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new PriorityBlockingQueue<>(128);

    private static final BlockingQueue<Runnable> sSinglePoolWorkQueue =
            new PriorityBlockingQueue<>(128);
    private static final ArrayList<MessageTag> MESSAGE_TAGS = new ArrayList<>();
    private static final int MSG_RESULT = 100;
    private static final int MSG_PROGRESS = 101;
    /**
     * An {@link Executor} that can be used to execute jobs in parallel.
     */
    private static Executor JOB_EXECUTOR;
    /**
     * An {@link Executor} that can be used to execute jobs in parallel.
     */
    private static Executor SINGLE_EXECUTOR;
    private final Handler mHandler = new Handler(Looper.getMainLooper(), this);

    private JobExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                        BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    private static MessageTag getMessageTag() {
        MessageTag tag;
        synchronized (MESSAGE_TAGS) {
            if (MESSAGE_TAGS.isEmpty()) {
                tag = new MessageTag();
            } else {
                tag = MESSAGE_TAGS.remove(MESSAGE_TAGS.size() - 1);
            }
        }
        return tag;
    }

    static Executor getDefault() {
        if (JOB_EXECUTOR == null) {
            JobExecutor jobExecutor = new JobExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            if (Build.VERSION.SDK_INT >= 9)
                jobExecutor.allowCoreThreadTimeOut(true);
            JOB_EXECUTOR = jobExecutor;
        }
        return JOB_EXECUTOR;
    }

    static Executor getSingle() {
        if (SINGLE_EXECUTOR == null) {
            JobExecutor singleExecutor = new JobExecutor(1, 1,
                    KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, sSinglePoolWorkQueue, sThreadFactory);
            if (Build.VERSION.SDK_INT >= 9)
                singleExecutor.allowCoreThreadTimeOut(true);
            SINGLE_EXECUTOR = singleExecutor;
        }
        return SINGLE_EXECUTOR;
    }

    static Executor getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                ThreadFactory threadFactory, boolean allowCoreThreadTimeOut) {
        JobExecutor singleExecutor = new JobExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue, threadFactory);
        if (Build.VERSION.SDK_INT >= 9)
            singleExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return singleExecutor;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof JobHolder) {
            ((JobHolder) r).attachJobExecutor(this);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof JobHolder) {
            ((JobHolder) r).detachJobExecutor();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RESULT:
                postExecute(msg);
                break;
            case MSG_PROGRESS:
                progressUpdate(msg);
                break;
        }
        return true;
    }

    private void postExecute(Message msg) {
        if (msg.obj instanceof MessageTag) {
            MessageTag tag = (MessageTag) msg.obj;
            tag.getJob().onPostExecute();
            tag.clear();
            synchronized (MESSAGE_TAGS) {
                MESSAGE_TAGS.add(tag);
            }
        }
    }

    private void progressUpdate(Message msg) {
        if (msg.obj instanceof MessageTag) {
            MessageTag tag = (MessageTag) msg.obj;
            tag.getJob().onProgressUpdate(tag.getValues());
            tag.clear();
            synchronized (MESSAGE_TAGS) {
                MESSAGE_TAGS.add(tag);
            }
        }
    }

    void publishResult(Job job) {
        MessageTag tag = getMessageTag();
        tag.setJob(job);
        mHandler.obtainMessage(MSG_RESULT, tag).sendToTarget();
    }

    void publishProgress(Job job, Object... values) {
        MessageTag tag = getMessageTag();
        tag.setJob(job);
        tag.setValues(values);
        mHandler.obtainMessage(MSG_PROGRESS, tag).sendToTarget();
    }

    private static class MessageTag {
        private Job mJob;
        private Object[] mValues;

        private Job getJob() {
            return mJob;
        }

        private void setJob(Job job) {
            mJob = job;
        }

        private Object[] getValues() {
            return mValues;
        }

        private void setValues(Object[] values) {
            mValues = values;
        }

        private void clear() {
            mJob = null;
            mValues = null;
        }
    }
}
