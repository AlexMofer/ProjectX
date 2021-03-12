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

import java.lang.ref.WeakReference;

/**
 * 基础任务
 * Created by Alex on 2021/3/1.
 */
public abstract class BaseJob<C> {
    public static final int PRIORITY_LOW = -1;// 低优先级
    public static final int PRIORITY_MIDDLE = 0; // 中优先级
    public static final int PRIORITY_HIGH = 1;// 高优先级
    private final Params mParam;
    private Traverse mTraverse;
    private C mCallback;
    private WeakReference<C> mWeakCallback;
    private int mId;
    private Object mTag;
    private int mPriority = PRIORITY_MIDDLE;

    public BaseJob(Params params, Traverse traverse,
                   C callback, boolean weakCallback, int id, Object... values) {
        mParam = params;
        mTraverse = traverse;
        if (weakCallback) {
            mCallback = null;
            mWeakCallback = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakCallback = null;
        }
        mId = id;
        mParam.setAll(values);
    }

    public BaseJob(Traverse traverse,
                   C callback, boolean weakCallback, int id, Object... params) {
        this(new HashMapParams(), traverse, callback, weakCallback, id, params);
    }

    /**
     * 生成任务
     *
     * @return 任务
     */
    protected Task generateTask() {
        return JobTask.get();
    }

    /**
     * 回收任务
     *
     * @param task 任务
     */
    protected void recycleTask(Task task) {
        if (task instanceof JobTask) {
            JobTask.put((JobTask) task);
        }
    }

    /**
     * 生成结果
     *
     * @return 结果
     */
    protected Result generateResult() {
        return HashMapResult.get();
    }

    /**
     * 回收结果
     *
     * @param result 结果
     */
    protected void recycleResult(Result result) {
        if (result instanceof HashMapResult) {
            HashMapResult.put((HashMapResult) result);
        }
    }

    /**
     * 生成进度
     *
     * @return 进度
     */
    protected Progress generateProgress() {
        return HashMapProgress.get();
    }

    /**
     * 回收进度
     *
     * @param progress 进度
     */
    protected void recycleProgress(Progress progress) {
        if (progress instanceof HashMapProgress) {
            HashMapProgress.put((HashMapProgress) progress);
        }
    }

    /**
     * 获取回调
     *
     * @return 回调
     */
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
    public BaseJob<C> setCallback(C callback, boolean weak) {
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
    protected int getId() {
        return mId;
    }

    /**
     * 设置ID
     *
     * @param id ID
     * @return 自身
     */
    public BaseJob<C> setId(int id) {
        mId = id;
        return this;
    }

    /**
     * 获取附属物
     *
     * @return 附属物
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置附属物
     *
     * @param tag 附属物
     * @return 自身
     */
    public BaseJob<C> setTag(Object tag) {
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
    public BaseJob<C> setPriority(int priority) {
        mPriority = priority;
        return this;
    }

    /**
     * 获取参数持有者
     *
     * @return 参数持有者
     */
    public Params getParams() {
        return mParam;
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
    public BaseJob<C> putWeakParam(int key, Object value) {
        mParam.putWeak(key, value);
        return this;
    }

    /**
     * 设置任务线程通信
     *
     * @param traverse 任务线程通信
     * @return 自身
     */
    public BaseJob<C> setTraverse(Traverse traverse) {
        mTraverse = traverse;
        return this;
    }

    /**
     * 异步执行
     *
     * @param executor 执行者
     */
    public void execute(Executor executor) {
        final Task task = generateTask();
        task.onAttached(this);
        executor.execute(task);
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
     * 后台执行任务
     *
     * @param result 结果
     */
    protected abstract void doInBackground(Result result);

    /**
     * 发布进度
     *
     * @param progress 进度
     */
    protected final void publishProgress(Progress progress) {
        mTraverse.publishProgress(this::dispatchProgress, progress);
    }

    /**
     * 分发结果
     *
     * @param result 结果
     */
    protected void dispatchResult(Result result) {
        final C callback = getCallback();
        if (callback != null) {
            onResult(callback, result);
        }
        recycleResult(result);
    }

    /**
     * 处理结果
     *
     * @param callback 回调
     * @param result   结果
     */
    protected void onResult(C callback, Result result) {
    }


    /**
     * 分发进度
     *
     * @param progress 进度
     */
    protected void dispatchProgress(Progress progress) {
        final C callback = getCallback();
        if (callback != null) {
            onProgress(callback, progress);
        }
        recycleProgress(progress);
    }

    /**
     * 处理进度
     *
     * @param callback 回调
     * @param progress 进度
     */
    protected void onProgress(C callback, Progress progress) {
    }

    private void doInBackground() {
        final BaseJob.Result result = generateResult();
        doInBackground(result);
        mTraverse.publishResult(this::dispatchResult, result);
    }

    private void afterExecute(Task task) {
        task.onDetached(this);
        recycleTask(task);
    }

    /**
     * 任务参数
     */
    public interface Params extends DataArray {

        /**
         * 清空弱引用值
         */
        void clearWeak();

        /**
         * 设置弱引用值
         *
         * @param key   键
         * @param value 值
         */
        void putWeak(int key, Object value);

        /**
         * 获取弱引用值
         *
         * @param key          键
         * @param defaultValue 默认值
         * @param <V>          类型
         * @return 值
         */
        <V> V getWeak(int key, V defaultValue);

        /**
         * 获取弱引用值
         *
         * @param key 键
         * @param <V> 类型
         * @return 值
         */
        default <V> V getWeak(int key) {
            return getWeak(key, null);
        }
    }

    /**
     * 执行者
     */
    public interface Executor {

        /**
         * 执行任务
         *
         * @param task 任务
         */
        void execute(Task task);
    }

    /**
     * 任务结果
     */
    public interface Result extends DataArray {

        /**
         * 设置
         *
         * @param success 是否成功
         * @param results 结果数据
         */
        void set(boolean success, Object... results);

        /**
         * 判断是否成功
         *
         * @return 任务成功时返回true
         */
        boolean isSuccess();
    }

    /**
     * 任务进度
     */
    public interface Progress extends DataArray {
    }

    /**
     * 任务线程通信器
     */
    public interface Traverse {

        /**
         * 发布任务结果
         *
         * @param callback 结果回调
         * @param result   任务结果
         */
        void publishResult(ResultCallback callback, Result result);

        /**
         * 发布任务进度
         *
         * @param callback 进度回调
         * @param progress 任务进度
         */
        void publishProgress(ProgressCallback callback, Progress progress);

        /**
         * 任务结果回调
         */
        interface ResultCallback {
            /**
             * 分发任务结果
             *
             * @param result 任务结果
             */
            void dispatchResult(Result result);
        }

        /**
         * 任务进度回调
         */
        interface ProgressCallback {
            /**
             * 分发任务进度
             *
             * @param progress 任务进度
             */
            void dispatchProgress(Progress progress);
        }
    }

    /**
     * 任务
     */
    public static class Task {

        private BaseJob<?> mJob;

        /**
         * 绑定
         *
         * @param job Job
         */
        protected void onAttached(BaseJob<?> job) {
            mJob = job;
        }

        /**
         * 解绑
         *
         * @param job Job
         */
        protected void onDetached(BaseJob<?> job) {
            if (mJob == job) {
                mJob = null;
            }
        }

        /**
         * 执行之前
         *
         * @param executor 执行者
         */
        public void beforeExecute(Executor executor) {
        }

        /**
         * 执行中
         */
        public void onExecute() {
            if (mJob != null) {
                mJob.doInBackground();
            }
        }

        /**
         * 执行之后
         *
         * @param executor 执行者
         */
        public void afterExecute(Executor executor) {
            if (mJob != null) {
                mJob.afterExecute(this);
            }
        }
    }
}
