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

import java.util.ArrayList;

/**
 * 任务可执行者
 * Created by Alex on 2021/3/12.
 */
class JobTask extends BaseJob.Task implements Comparable<JobTask>, Runnable {

    private static final ArrayList<JobTask> CACHED = new ArrayList<>();
    private BaseJob.Executor mExecutor;
    private int mPriority = BaseJob.PRIORITY_LOW;
    private long mTime = 0;

    private JobTask() {
    }

    static JobTask get() {
        final JobTask task;
        synchronized (CACHED) {
            if (CACHED.isEmpty()) {
                task = new JobTask();
            } else {
                task = CACHED.remove(0);
            }
        }
        return task;
    }

    static void put(JobTask task) {
        if (task == null) {
            return;
        }
        synchronized (CACHED) {
            CACHED.add(task);
        }
    }

    @Override
    protected void onAttached(BaseJob<?> job) {
        super.onAttached(job);
        mPriority = job.getPriority();
    }

    @Override
    public void beforeExecute(BaseJob.Executor executor) {
        super.beforeExecute(executor);
        mExecutor = executor;
        mTime = System.currentTimeMillis();
    }

    @Override
    public void afterExecute(BaseJob.Executor executor) {
        super.afterExecute(executor);
        mTime = 0;
        mExecutor = null;
    }

    @Override
    public int compareTo(JobTask o) {
        final int priority = mPriority;
        final int priorityOther = o.mPriority;
        if (priority == priorityOther) {
            if (mExecutor == o.mExecutor) {
                // 同一执行者
                return Long.compare(mTime, o.mTime);
            } else {
                return 0;
            }
        } else if (priority > priorityOther) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public void run() {
        onExecute();
    }
}
