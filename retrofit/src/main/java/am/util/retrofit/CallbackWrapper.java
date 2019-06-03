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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 回调包装器
 * Created by Alex on 2018/3/14.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CallbackWrapper<T> implements Callback<T> {

    public static final int ERROR_CODE_THROWABLE = 0xff000001;
    public static final int ERROR_CODE_NULL = 0xff000002;
    private final SimplifiedCallback<T> mSimplified;
    private final TinyCallback<T> mTiny;

    public CallbackWrapper(SimplifiedCallback<T> callback) {
        mSimplified = callback;
        mTiny = null;
    }

    public CallbackWrapper(TinyCallback<T> callback) {
        mSimplified = null;
        mTiny = callback;
    }

    /**
     * 是否检查空返回
     *
     * @return 默认不检查
     */
    public boolean checkNullResponse() {
        return false;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            final T result = response.body();
            if (checkNullResponse() && result == null)
                onFailure(call, ERROR_CODE_NULL, null, null);
            else
                onResponse(call, result);
        } else {
            onFailure(call, response.code(), response.message(), response.body());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(call, ERROR_CODE_THROWABLE, t.getMessage(), null);
    }

    /**
     * 响应
     *
     * @param call   请求
     * @param result 结果（在回调包装器允许空结果的情况下，可能为空）
     */
    protected void onResponse(Call<T> call, T result) {
        if (mSimplified != null)
            mSimplified.onResponse(call, result);
        if (mTiny != null)
            mTiny.onResponse(result);
    }

    /**
     * 错误
     *
     * @param call    请求
     * @param code    错误码
     * @param message 错误信息（可能为空）
     * @param result  结果（可能为空）
     */
    protected void onFailure(Call<T> call, int code, String message, T result) {
        if (mSimplified != null)
            mSimplified.onFailure(call, code, message, result);
        if (mTiny != null)
            mTiny.onFailure(code, message, result);
    }
}
