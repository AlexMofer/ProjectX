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

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * 任务
 * Created by Alex on 2017/9/11.
 */
@SuppressWarnings("all")
public abstract class Job<T> {

    public static final int LEVEL_LOW = -1;
    public static final int LEVEL_DEFAULT = 0;
    public static final int LEVEL_MIDDLE = 1;
    public static final int LEVEL_HIGH = 2;
    private final WeakReference<T> mWeakReference;
    protected int mAction;
    protected Object[] mParams;
    private JobHolder mHolder;
    private T mCallback;

    protected Job(T callback) {
        this(callback, 0);
    }

    protected Job(T callback, boolean weakReference) {
        this(callback, weakReference, 0);
    }

    protected Job(T callback, int action, Object... params) {
        this(callback, true, action, params);
    }

    protected Job(T callback, boolean weakReference, int action, Object... params) {
        if (weakReference) {
            mWeakReference = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakReference = new WeakReference<>(callback);
        }
        mAction = action;
        mParams = params;
    }

    public static Executor getDefaultExecutor() {
        return JobExecutor.getDefault();
    }

    public static Executor getSingleExecutor() {
        return JobExecutor.getSingle();
    }

    void setHolder(JobHolder holder) {
        mHolder = holder;
    }

    protected abstract void doInBackground();

    protected final void publishProgress(Object... values) {
        if (mHolder != null)
            mHolder.publishProgress(values);
    }

    protected void onProgressUpdate(Object... values) {
        final T callback = mCallback == null ? mWeakReference.get() : mCallback;
        dispatchProgress(callback, values);
    }

    protected void dispatchProgress(T callback, Object... values) {
    }

    protected void onPostExecute() {
        final T callback = mCallback == null ? mWeakReference.get() : mCallback;
        dispatchResult(callback);
        mParams = null;
    }

    protected void dispatchResult(T callback) {
        mCallback = null;
    }

    public int getLevel() {
        return LEVEL_DEFAULT;
    }

    public void execute() {
        JobExecutor.getDefault().execute(JobHolder.get(this));
    }

    public void executeInSingle() {
        JobExecutor.getSingle().execute(JobHolder.get(this));
    }
}