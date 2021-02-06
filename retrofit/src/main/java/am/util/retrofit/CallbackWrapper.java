/*
 * Copyright (C) 2018 AlexMofer
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

package am.util.retrofit;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 回调包装器
 * Created by Alex on 2018/3/14.
 */
public class CallbackWrapper<T> implements retrofit2.Callback<T> {

    private WeakReference<Callback<T>> mWeakCallback;
    private Callback<T> mCallback;

    /**
     * 设置回调
     *
     * @param callback 回调
     * @param weak     是否为弱引用
     * @return 回调包装器
     */
    public CallbackWrapper<T> setCallback(Callback<T> callback, boolean weak) {
        if (weak) {
            mWeakCallback = new WeakReference<>(callback);
            mCallback = null;
        } else {
            mWeakCallback = null;
            mCallback = callback;
        }
        return this;
    }

    private Callback<T> getCallback() {
        if (mCallback != null)
            return mCallback;
        if (mWeakCallback != null)
            return mWeakCallback.get();
        return null;
    }

    /**
     * 是否检查空返回
     *
     * @return 默认不检查
     */
    public boolean checkEmptyResponse() {
        return false;
    }

    @Override
    public void onResponse(@NotNull Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            final T result = response.body();
            if (checkEmptyResponse() && result == null)
                onEmptyResponse(call, response);
            else
                onResponse(call, result);
        } else {
            onFailure(call, response.code(), response.message());
        }
    }

    @Override
    public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
        final Callback<T> callback = getCallback();
        if (callback == null)
            return;
        callback.onFailure(Callback.ERROR_CODE_THROWABLE, t.getMessage());
    }

    /**
     * 响应
     *
     * @param call   请求
     * @param result 结果（在回调包装器允许空结果的情况下，可能为空）
     */
    protected void onResponse(Call<T> call, T result) {
        final Callback<T> callback = getCallback();
        if (callback == null)
            return;
        callback.onResponse(result);
    }

    /**
     * 错误
     *
     * @param call    请求
     * @param code    错误码
     * @param message 错误信息（可能为空）
     */
    protected void onFailure(Call<T> call, int code, String message) {
        final Callback<T> callback = getCallback();
        if (callback == null)
            return;
        callback.onFailure(code, message);
    }

    /**
     * 出现空响应
     *
     * @param call     请求
     * @param response 响应
     */
    protected void onEmptyResponse(Call<T> call, Response<T> response) {
        final Callback<T> callback = getCallback();
        if (callback == null)
            return;
        callback.onFailure(Callback.ERROR_CODE_EMPTY, "Empty Response.");
    }
}
