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

import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
    private static final ArrayList<Job.Progress> PROGRESSES = new ArrayList<>();
    private final SparseArray<Object> mParams = new SparseArray<>();
    private final SparseArray<WeakReference<Object>> mWeakParams = new SparseArray<>();
    private final SparseArray<Object> mResults = new SparseArray<>();
    private JobHolder mHolder;
    private T mCallback;
    private WeakReference<T> mWeakReference;
    private int mAction;
    private boolean mSuccess = false;
    private Object mTag;

    public Job(T callback) {
        this(callback, 0);
    }

    public Job(T callback, boolean weakReference) {
        this(callback, weakReference, 0);
    }

    public Job(T callback, int action, Object... params) {
        this(callback, true, action, params);
    }

    public Job(T callback, boolean weakReference, int action, Object... params) {
        setCallback(callback, weakReference);
        mAction = action;
        int key = 0;
        for (Object param : params) {
            putParam(key, param);
            key++;
        }
    }

    public static Executor getDefaultExecutor() {
        return JobExecutor.getDefault();
    }

    public static Executor getSingleExecutor() {
        return JobExecutor.getSingle();
    }

    public static Executor getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                       TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                       ThreadFactory threadFactory, boolean allowCoreThreadTimeOut) {
        return JobExecutor.getExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, allowCoreThreadTimeOut);
    }

    public static Progress getProgress() {
        final Progress progress;
        synchronized (PROGRESSES) {
            if (PROGRESSES.isEmpty()) {
                progress = new Progress();
            } else {
                progress = PROGRESSES.remove(0);
            }
        }
        return progress;
    }

    void setHolder(JobHolder holder) {
        mHolder = holder;
    }

    public void setCallback(T callback, boolean weakReference) {
        if (weakReference) {
            mWeakReference = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakReference = new WeakReference<>(callback);
        }
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public void putParam(int key, Object value) {
        if (value == null) {
            mParams.remove(key);
            return;
        }
        mParams.put(key, value);
    }

    public <Params> Params getParamOrThrow(int key) throws IllegalArgumentException {
        final Object param = mParams.get(key);
        if (param == null)
            throw new IllegalArgumentException("There is no param with key" + key + ".");
        final Params result;
        try {
            result = (Params) param;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return result;
    }

    public <Params> Params getParam(int key, Params defaultValue) {
        try {
            return getParamOrThrow(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public <Params> Params getParam(int key) {
        return getParam(key, null);
    }

    public void clearParams() {
        mParams.clear();
    }

    public boolean getBooleanParam(int key) throws IllegalArgumentException {
        final Boolean param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public boolean getBooleanParam(int key, boolean defaultValue) {
        try {
            return getBooleanParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public byte getByteParam(int key) throws IllegalArgumentException {
        final Byte param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public byte getByteParam(int key, byte defaultValue) {
        try {
            return getByteParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public short getShortParam(int key) throws IllegalArgumentException {
        final Short param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public short getShortParam(int key, short defaultValue) {
        try {
            return getShortParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public char getCharParam(int key) throws IllegalArgumentException {
        final Character param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public char getCharParam(int key, char defaultValue) {
        try {
            return getCharParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public int getIntParam(int key) throws IllegalArgumentException {
        final Integer param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public int getIntParam(int key, int defaultValue) {
        try {
            return getIntParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public long getLongParam(int key) throws IllegalArgumentException {
        final Long param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public long getLongParam(int key, long defaultValue) {
        try {
            return getLongParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public float getFloatParam(int key) throws IllegalArgumentException {
        final Float param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public float getFloatParam(int key, float defaultValue) {
        try {
            return getFloatParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public double getDoubleParam(int key) throws IllegalArgumentException {
        final Double param = getParamOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public double getDoubleParam(int key, double defaultValue) {
        try {
            return getDoubleParam(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public void putWeakParam(int key, Object value) {
        if (value == null) {
            mWeakParams.remove(key);
            return;
        }
        mWeakParams.put(key, new WeakReference<>(value));
    }

    public <Params> Params getWeakParamOrThrow(int key) throws IllegalArgumentException {
        final WeakReference<Object> weak = mWeakParams.get(key);
        if (weak == null)
            throw new IllegalArgumentException("There is no param with key" + key + ".");
        final Object param = weak.get();
        if (param == null)
            return null;
        final Params result;
        try {
            result = (Params) param;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return result;
    }

    public <Params> Params getWeakParam(int key, Params defaultValue) {
        try {
            return getWeakParamOrThrow(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public <Params> Params getWeakParam(int key) {
        return getWeakParam(key, null);
    }

    public void clearWeakParams() {
        mWeakParams.clear();
    }

    public void setResult(boolean success, Object... results) {
        clearResult();
        mSuccess = success;
        int i = 0;
        for (Object result : results) {
            putResult(i, result);
            i++;
        }
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void putResult(int key, Object value) {
        if (value == null) {
            mResults.remove(key);
            return;
        }
        mResults.put(key, value);
    }

    public <Result> Result getResultOrThrow(int key) throws IllegalArgumentException {
        final Object result = mResults.get(key);
        if (result == null)
            throw new IllegalArgumentException("There is no result with key" + key + ".");
        final Result r;
        try {
            r = (Result) result;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return r;
    }

    public <Result> Result getResult(int key, Result defaultValue) {
        try {
            return getResultOrThrow(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public <Result> Result getResult(int key) {
        return getResult(key, null);
    }

    public void clearResult() {
        mSuccess = false;
        mResults.clear();
    }

    public boolean getBooleanResult(int key) throws IllegalArgumentException {
        final Boolean param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public boolean getBooleanResult(int key, boolean defaultValue) {
        try {
            return getBooleanResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public byte getByteResult(int key) throws IllegalArgumentException {
        final Byte param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public byte getByteResult(int key, byte defaultValue) {
        try {
            return getByteResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public short getShortResult(int key) throws IllegalArgumentException {
        final Short param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public short getShortResult(int key, short defaultValue) {
        try {
            return getShortResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public char getCharResult(int key) throws IllegalArgumentException {
        final Character param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public char getCharResult(int key, char defaultValue) {
        try {
            return getCharResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public int getIntResult(int key) throws IllegalArgumentException {
        final Integer param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public int getIntResult(int key, int defaultValue) {
        try {
            return getIntResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public long getLongResult(int key) throws IllegalArgumentException {
        final Long param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public long getLongResult(int key, long defaultValue) {
        try {
            return getLongResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public float getFloatResult(int key) throws IllegalArgumentException {
        final Float param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public float getFloatResult(int key, float defaultValue) {
        try {
            return getFloatResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public double getDoubleResult(int key) throws IllegalArgumentException {
        final Double param = getResultOrThrow(key);
        if (param == null)
            throw new IllegalArgumentException("It's null object.");
        return param;
    }

    public double getDoubleResult(int key, double defaultValue) {
        try {
            return getDoubleResult(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public Object getTag() {
        return mTag;
    }

    public Job<T> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    protected abstract void doInBackground();

    protected final void publishProgress(Progress progress) {
        if (mHolder != null)
            mHolder.publishProgress(progress);
    }

    protected void onProgressUpdate(Job.Progress progress) {
        final T callback = mCallback == null ? mWeakReference.get() : mCallback;
        dispatchProgress(callback, progress);
        synchronized (PROGRESSES) {
            PROGRESSES.add(progress);
        }
    }

    protected void dispatchProgress(T callback, Job.Progress progress) {
    }

    protected void onPostExecute() {
        final T callback = mCallback == null ? mWeakReference.get() : mCallback;
        dispatchResult(callback);
        mCallback = null;
        mWeakReference = null;
        clearParams();
        clearWeakParams();
        clearResult();
    }

    protected void dispatchResult(T callback) {
    }

    protected Runnable getJobHolder(Job job) {
        return JobHolder.get(job);
    }

    public int getLevel() {
        return LEVEL_DEFAULT;
    }

    public void execute() {
        getDefaultExecutor().execute(getJobHolder(this));
    }

    public void executeInSingle() {
        getSingleExecutor().execute(getJobHolder(this));
    }

    public static class Progress {

        private final SparseArray<Object> mProgress = new SparseArray<>();

        public void putProgress(int key, Object value) {
            if (value == null) {
                mProgress.remove(key);
                return;
            }
            mProgress.put(key, value);
        }

        public <T> T getProgressOrThrow(int key) throws IllegalArgumentException {
            final Object progress = mProgress.get(key);
            if (progress == null)
                throw new IllegalArgumentException("There is no progress with key" + key + ".");
            try {
                return (T) progress;
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        public <T> T getProgress(int key, T defaultValue) {
            try {
                return getProgressOrThrow(key);
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        }

        public <T> T getProgress(int key) {
            return getProgress(key, null);
        }
    }
}