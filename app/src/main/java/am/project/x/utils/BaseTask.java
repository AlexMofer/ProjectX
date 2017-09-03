package am.project.x.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * 基础任务
 * Created by  on 2017/9/3.
 */
@SuppressWarnings("all")
public abstract class BaseTask<T> extends AsyncTask<Object, Void, Integer> {

    private final boolean mWeak;
    private final T mCallback;
    private final WeakReference<T> mWeakReference;
    private Object mResult;
    private Object mTag;

    protected BaseTask(@Nullable T callback) {
        this(callback, true);
    }

    protected BaseTask(@Nullable T callback, boolean weakReference) {
        mWeak = weakReference;
        if (weakReference) {
            mCallback = null;
            mWeakReference = new WeakReference<>(callback);
        } else {
            mCallback = callback;
            mWeakReference = null;
        }
    }

    @Override
    protected Integer doInBackground(Object... params) {
        final int action = (Integer) params[0];
        if (params.length > 1) {
            Object[] data = new Object[params.length - 1];
            System.arraycopy(params, 1, data, 0, data.length);
            handleAction(action, data);
        } else {
            handleAction(action);
        }
        return action;
    }

    protected abstract void handleAction(int action, Object... params);

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        final T callback;
        if (mWeak) {
            callback = mWeakReference.get();
        } else {
            callback = mCallback;
        }
        if (callback == null)
            return;
        notifyResult(result, callback);
        mResult = null;
        mTag = null;
    }

    protected void notifyResult(int action, T callback) {
    }

    protected final Object getResult() {
        return mResult;
    }

    protected final void setResult(Object result) {
        mResult = result;
    }

    protected final Object getTag() {
        return mTag;
    }

    protected final void setTag(Object tag) {
        mTag = tag;
    }
}