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
package am.util.job;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import am.util.job.core.BaseJob;

/**
 * 用Handler实现的任务线程通信器
 * Created by Alex on 2021/3/12.
 */
class HandlerTraverse implements BaseJob.Traverse {

    private static final int MSG_RESULT = 1001;
    private static final int MSG_PROGRESS = 1002;
    private static final ArrayList<Tag> TAGS = new ArrayList<>();
    private static HandlerTraverse MAIN_TRAVERSE = null;
    private final Handler mHandler;

    public HandlerTraverse(Looper looper) {
        mHandler = new Handler(looper, this::handleMessage);
    }

    static HandlerTraverse getMainTraverse() {
        if (MAIN_TRAVERSE == null) {
            MAIN_TRAVERSE = new HandlerTraverse(Looper.getMainLooper());
        }
        return MAIN_TRAVERSE;
    }

    private static Tag getTag(Object callback, Object value) {
        final Tag progress;
        synchronized (TAGS) {
            if (TAGS.isEmpty()) {
                progress = new Tag();
            } else {
                progress = TAGS.remove(0);
            }
        }
        progress.put(callback, value);
        return progress;
    }

    private static void putTag(Tag tag) {
        tag.clear();
        synchronized (TAGS) {
            TAGS.add(tag);
        }
    }

    @Override
    public void publishResult(ResultCallback callback, BaseJob.Result result) {
        mHandler.obtainMessage(MSG_RESULT, getTag(callback, result)).sendToTarget();
    }

    @Override
    public void publishProgress(ProgressCallback callback, BaseJob.Progress progress) {
        mHandler.obtainMessage(MSG_PROGRESS, getTag(callback, progress)).sendToTarget();
    }

    private boolean handleMessage(Message msg) {
        if (msg.obj instanceof Tag) {
            final Tag tag = (Tag) msg.obj;
            switch (msg.what) {
                case MSG_RESULT:
                    tag.<ResultCallback>getCallback().dispatchResult(tag.getValue());
                    break;
                case MSG_PROGRESS:
                    tag.<ProgressCallback>getCallback().dispatchProgress(tag.getValue());
                    break;
            }
            putTag(tag);
            return true;
        }
        return false;
    }

    private static class Tag {
        private Object mCallback;
        private Object mValue;

        void put(Object callback, Object value) {
            mCallback = callback;
            mValue = value;
        }

        <V> V getCallback() {
            //noinspection unchecked
            return (V) mCallback;
        }

        <V> V getValue() {
            //noinspection unchecked
            return (V) mValue;
        }

        private void clear() {
            mCallback = null;
            mValue = null;
        }
    }
}
