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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * 任务
 * Created by Alex on 2021/3/1.
 */
public abstract class Job<C> {
    public static final int PRIORITY_LOW = -1;// 低优先级
    public static final int PRIORITY_MIDDLE = 0; // 中优先级
    public static final int PRIORITY_HIGH = 1;// 高优先级
    private final JobParam mParam = new JobParam();
    private final Command mCommand = new Command(this);
    private C mCallback;
    private WeakReference<C> mWeakCallback;
    private long mId;
    private Object mTag;
    private int mPriority = PRIORITY_MIDDLE;
    private JobTraverse mTraverse = JobTraverse.getMainTraverse();

    public Job(@Nullable C callback, boolean weakCallback, long id, Object... params) {
        if (weakCallback) {
            mCallback = null;
            mWeakCallback = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakCallback = null;
        }
        mId = id;
        mParam.set(params);
    }

    public Job(@Nullable C callback, long id, Object... params) {
        this(callback, true, id, params);
    }

    public Job(@Nullable C callback) {
        this(callback, 0, true);
    }

    /**
     * 获取回调
     *
     * @return 回调
     */
    @Nullable
    protected C getCallback() {
        return mWeakCallback != null ? mWeakCallback.get() : mCallback;
    }

    /**
     * 设置回调
     *
     * @param callback 回调
     * @param weak     是否为弱引用
     * @return 自身
     */
    public Job<C> setCallback(@Nullable C callback, boolean weak) {
        if (weak) {
            mCallback = null;
            mWeakCallback = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakCallback = null;
        }
        return this;
    }

    /**
     * 获取任务ID
     *
     * @return 任务ID
     */
    protected long getId() {
        return mId;
    }

    /**
     * 设置ID
     *
     * @param id ID
     * @return 自身
     */
    public Job<C> setId(long id) {
        mId = id;
        return this;
    }

    /**
     * 获取附属物
     *
     * @return 附属物
     */
    @Nullable
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置附属物
     *
     * @param tag 附属物
     * @return 自身
     */
    public Job<C> setTag(@Nullable Object tag) {
        mTag = tag;
        return this;
    }

    /**
     * 获取优先级
     *
     * @return 优先级
     */
    public int getPriority() {
        return mPriority;
    }

    /**
     * 设置优先级
     *
     * @param priority 优先级
     * @return 自身
     */
    public Job<C> setPriority(int priority) {
        mPriority = priority;
        return this;
    }

    /**
     * 设置任务线程通信（默认为子线程到UI主线程）
     *
     * @param traverse 任务线程通信
     * @return 自身
     */
    public Job<C> setTraverse(@NonNull JobTraverse traverse) {
        mTraverse = traverse;
        return this;
    }

    /**
     * 清空弱引用值
     */
    public void clearWeakParams() {
        mParam.clearWeak();
    }

    /**
     * 设置弱引用值
     *
     * @param key   键
     * @param value 值
     * @return 自身
     */
    public Job<C> putWeakParam(int key, @Nullable Object value) {
        mParam.putWeak(key, value);
        return this;
    }

    /**
     * 异步执行
     *
     * @param executor 执行者
     */
    public void execute(@NonNull JobExecutor executor) {
        executor.execute(mCommand);
    }

    /**
     * 并发异步执行
     */
    public void execute() {
        execute(JobExecutorHelper.getDefault());
    }

    /**
     * 串行异步执行
     */
    public void executeInSingle() {
        execute(JobExecutorHelper.getSingle());
    }

    /**
     * 获取参数持有者
     *
     * @return 参数持有者
     */
    @NonNull
    protected JobParam getParam() {
        return mParam;
    }

    /**
     * 后台执行任务
     *
     * @param result 结果
     */
    protected abstract void doInBackground(@NonNull JobResult result);

    /**
     * 发布进度
     *
     * @param progress 进度
     */
    protected final void publishProgress(@NonNull JobProgress progress) {
        mTraverse.publishProgress(this, progress);
    }

    /**
     * 构造进度对象
     *
     * @return 进度对象
     */
    @NonNull
    protected JobProgress createProgress() {
        return JobProgress.get();
    }

    /**
     * 分发进度
     *
     * @param progress 进度
     */
    protected void dispatchProgress(@NonNull JobProgress progress) {
        final C callback = getCallback();
        if (callback != null) {
            onProgress(callback, progress);
        }
        JobProgress.put(progress);
    }

    /**
     * 处理进度
     *
     * @param callback 回调
     * @param progress 进度
     */
    protected void onProgress(@NonNull C callback, @NonNull JobProgress progress) {
    }

    /**
     * 分发结果
     *
     * @param result 结果
     */
    protected void dispatchResult(@NonNull JobResult result) {
        final C callback = getCallback();
        if (callback != null) {
            onResult(callback, result);
        }
        JobResult.put(result);
    }

    /**
     * 处理结果
     *
     * @param callback 回调
     * @param result   结果
     */
    protected void onResult(@NonNull C callback, @NonNull JobResult result) {
    }

    private static class Command implements JobExecutor.Executable, Comparable<Command> {

        private final Job<?> mJob;
        private JobExecutor mExecutor;
        private long mTime = 0;

        public Command(Job<?> job) {
            mJob = job;
        }

        @Override
        public void run() {
            final JobResult result = JobResult.get();
            mJob.doInBackground(result);
            mJob.mTraverse.publishResult(mJob, result);
        }

        @Override
        public void beforeExecute(@NonNull JobExecutor executor) {
            mExecutor = executor;
            mTime = System.currentTimeMillis();
        }

        @Override
        public void afterExecute(@NonNull JobExecutor executor) {
            mTime = 0;
            mExecutor = null;
        }

        @Override
        public int compareTo(Command o) {
            final int priority = mJob.getPriority();
            final int priorityOther = o.mJob.getPriority();
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
    }
}
