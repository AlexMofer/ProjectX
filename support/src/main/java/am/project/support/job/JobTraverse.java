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
package am.project.support.job;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * 任务线程通信
 * Created by Xiang Zhicheng on 2021/3/1.
 */
public class JobTraverse {

    private static final int MSG_PROGRESS = 1001;
    private static final int MSG_RESULT = 1002;
    private static final ArrayList<Tag> TAGS = new ArrayList<>();
    private static JobTraverse MAIN_TRAVERSE = null;
    private final Handler mHandler;

    public JobTraverse(Looper looper) {
        mHandler = new Handler(looper, this::handleMessage);
    }

    static JobTraverse getMainTraverse() {
        if (MAIN_TRAVERSE == null) {
            MAIN_TRAVERSE = new JobTraverse(Looper.getMainLooper());
        }
        return MAIN_TRAVERSE;
    }

    private static Tag getTag(Job<?> job, Object value) {
        final Tag progress;
        synchronized (TAGS) {
            if (TAGS.isEmpty()) {
                progress = new Tag();
            } else {
                progress = TAGS.remove(0);
            }
        }
        progress.put(job, value);
        return progress;
    }

    private static void putTag(Tag tag) {
        tag.clear();
        synchronized (TAGS) {
            TAGS.add(tag);
        }
    }

    void publishProgress(Job<?> job, JobProgress progress) {
        mHandler.obtainMessage(MSG_PROGRESS, getTag(job, progress)).sendToTarget();
    }

    void publishResult(Job<?> job, JobResult result) {
        mHandler.obtainMessage(MSG_RESULT, getTag(job, result)).sendToTarget();
    }

    private boolean handleMessage(@NonNull Message msg) {
        if (msg.obj instanceof Tag) {
            final Tag tag = (Tag) msg.obj;
            switch (msg.what) {
                case MSG_PROGRESS:
                    handleProgress(tag.getJob(), tag.getValue());
                    break;
                case MSG_RESULT:
                    handleResult(tag.getJob(), tag.getValue());
                    break;
            }
            putTag(tag);
            return true;
        }
        return false;
    }

    private void handleProgress(Job<?> job, JobProgress progress) {
        job.dispatchProgress(progress);
    }

    private void handleResult(Job<?> job, JobResult result) {
        job.dispatchResult(result);
    }

    private static class Tag {
        private Job<?> mJob;
        private Object mValue;

        void put(Job<?> job, Object value) {
            mJob = job;
            mValue = value;
        }

        Job<?> getJob() {
            return mJob;
        }

        <V> V getValue() {
            //noinspection unchecked
            return (V) mValue;
        }

        private void clear() {
            mJob = null;
            mValue = null;
        }
    }
}
