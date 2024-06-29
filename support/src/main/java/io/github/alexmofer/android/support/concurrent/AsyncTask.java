package io.github.alexmofer.android.support.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务
 * Created by Alex on 2024/3/1.
 */
public final class AsyncTask {
    private static Executor sExecutor;
    private static InternalHandler sHandler;

    private AsyncTask() {
        //no instance
    }

    /**
     * 设置执行者
     *
     * @param executor 执行者
     */
    public static void setExecutor(Executor executor) {
        sExecutor = executor;
    }

    private static InternalHandler getHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    @AnyThread
    public static <Param, Result> Task execute(@NonNull Executor executor,
                                               @NonNull Callable<Param, Result> callable,
                                               @Nullable ExceptionHandler<Param> handler,
                                               @Nullable Callback<Param, Result> callback,
                                               @Nullable Param param, int priority) {
        return new InternalRunnable<>(executor, callable, handler, callback, param, priority);
    }

    @AnyThread
    public static <Param, Result> Task execute(@NonNull Callable<Param, Result> callable,
                                               @Nullable Callback<Param, Result> callback,
                                               @Nullable Param param, int priority) {
        if (sExecutor == null) {
            throw new RuntimeException("setExecutor before execute");
        }
        return execute(sExecutor, callable, null, callback, param, priority);
    }

    @AnyThread
    public static <Param, Result> Task execute(@NonNull Callable<Param, Result> callable,
                                               @Nullable Callback<Param, Result> callback,
                                               @Nullable Param param) {
        return execute(callable, callback, param, Task.PRIORITY_MIDDLE);
    }

    private static class InternalRunnable<Param, Result> implements Task,
            Comparable<Runnable>, Runnable {

        private final long mTime = System.currentTimeMillis();
        private final Executor mExecutor;
        private final Callable<Param, Result> mCallable;
        private final ExceptionHandler<Param> mHandler;
        private final Callback<Param, Result> mCallback;
        private final Param mParam;
        private volatile int mPriority;
        private Result mResult;
        private Throwable mThrowable;
        private boolean mDone;
        private volatile boolean mCancelled;
        private volatile Thread mThread;

        public InternalRunnable(@NonNull Executor executor,
                                @NonNull Callable<Param, Result> callable,
                                @Nullable ExceptionHandler<Param> handler,
                                @Nullable Callback<Param, Result> callback,
                                @Nullable Param param,
                                int priority) {
            mExecutor = executor;
            mCallable = callable;
            mHandler = handler;
            mCallback = callback;
            mParam = param;
            mPriority = priority;
            mExecutor.execute(this);
        }

        @Override
        public int getPriority() {
            return mPriority;
        }

        @Override
        public void setPriority(int priority) {
            mPriority = Math.max(PRIORITY_HIGHEST, Math.min(PRIORITY_LOWEST, priority));
        }

        @Override
        public boolean isDone() {
            return mDone;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (mCancelled) {
                return false;
            }
            mCancelled = true;
            if (mayInterruptIfRunning) {
                final Thread t = mThread;
                if (t != null) {
                    t.interrupt();
                    return true;
                }
            }
            if (mExecutor instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) mExecutor).remove(this);
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return mCancelled;
        }

        @Override
        public int compareTo(Runnable o) {
            if (o instanceof InternalRunnable) {
                final InternalRunnable<?, ?> other = (InternalRunnable<?, ?>) o;
                final int priority = getPriority();
                final int result = Integer.compare(priority, other.getPriority());
                if (result == 0) {
                    // 相同优先级下
                    if (priority < PRIORITY_MIDDLE) {
                        // 先进后执行
                        return Long.compare(other.mTime, mTime);
                    } else {
                        // 先进先执行
                        return Long.compare(mTime, other.mTime);
                    }
                }
                return result;
            }
            return 0;
        }

        @Override
        public void run() {
            if (mCancelled) {
                return;
            }
            mThread = Thread.currentThread();
            try {
                mResult = mCallable.call(mParam);
            } catch (Throwable throwable) {
                if (mCancelled) {
                    return;
                }
                mThrowable = throwable;
                mDone = true;
                mThread = null;
                if (mHandler != null) {
                    getHandler().postHandle(this);
                }
                return;
            }
            if (mCancelled) {
                return;
            }
            mDone = true;
            mThread = null;
            if (mCallback != null) {
                getHandler().postComplete(this);
            }
        }

        protected void handle() {
            if (mHandler != null) {
                mHandler.handle(mParam, mThrowable);
            }
        }

        protected void complete() {
            if (mCallback != null) {
                mCallback.complete(mParam, mResult);
            }
        }
    }

    private static class InternalHandler extends Handler {

        private static final int MESSAGE_POST_COMPLETE = 1;
        private static final int MESSAGE_POST_HANDLE = 2;

        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        public void postComplete(InternalRunnable<?, ?> runnable) {
            obtainMessage(MESSAGE_POST_COMPLETE, runnable).sendToTarget();
        }

        public void postHandle(InternalRunnable<?, ?> runnable) {
            obtainMessage(MESSAGE_POST_HANDLE, runnable).sendToTarget();
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final InternalRunnable<?, ?> runnable = (InternalRunnable<?, ?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_COMPLETE:
                    runnable.complete();
                    break;
                case MESSAGE_POST_HANDLE:
                    runnable.handle();
                    break;
            }
        }
    }
}