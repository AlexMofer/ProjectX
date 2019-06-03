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

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 弱引用回调
 * Created by Alex on 2018/3/14.
 */
@SuppressWarnings("unused")
public class WeakCallback<T> implements Callback<T> {

    private final WeakReference<Callback<T>> mWeakCallback;

    public WeakCallback(Callback<T> callback) {
        if (callback == null)
            mWeakCallback = null;
        else
            mWeakCallback = new WeakReference<>(callback);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        final Callback<T> callback = mWeakCallback == null ? null : mWeakCallback.get();
        if (callback == null)
            return;
        callback.onResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        final Callback<T> callback = mWeakCallback == null ? null : mWeakCallback.get();
        if (callback == null)
            return;
        callback.onFailure(call, t);
    }
}
