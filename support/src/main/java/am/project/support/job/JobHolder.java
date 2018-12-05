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

import java.util.ArrayList;

/**
 * Holder
 * Created by Alex on 2017/9/11.
 */
class JobHolder implements Runnable, Comparable<JobHolder> {

    private static final ArrayList<JobHolder> HOLDERS = new ArrayList<>();

    private JobExecutor mExecutor;
    private long mTime = 0;
    private Job mJob;

    static JobHolder get(Job job) {
        JobHolder holder;
        synchronized (HOLDERS) {
            if (HOLDERS.isEmpty()) {
                holder = new JobHolder();
            } else {
                holder = HOLDERS.remove(0);
            }
        }
        holder.setJob(job);
        return holder;
    }

    final void attachJobExecutor(JobExecutor executor) {
        mExecutor = executor;
        mTime = System.currentTimeMillis();
        mJob.setHolder(this);
    }

    final void detachJobExecutor() {
        mTime = 0;
        mJob.setHolder(null);
        if (mExecutor != null)
            mExecutor.publishResult(mJob);
        mExecutor = null;
        mJob = null;
        synchronized (HOLDERS) {
            HOLDERS.add(this);
        }
    }

    private void setJob(Job job) {
        mJob = job;
    }

    @Override
    public final void run() {
        mJob.doInBackground();
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") JobHolder o) {
        final int level = mJob.getLevel();
        final int levelOther = o.mJob.getLevel();
        if (level == levelOther) {
            //noinspection UseCompareMethod
            if (mTime == o.mTime) {
                return 0;
            } else if (mTime > o.mTime) {
                return 1;
            } else {
                return -1;
            }
        } else if (level > levelOther) {
            return 1;
        } else {
            return -1;
        }
    }

    void publishProgress(Job.Progress progress) {
        if (mExecutor != null)
            mExecutor.publishProgress(mJob, progress);
    }
}
