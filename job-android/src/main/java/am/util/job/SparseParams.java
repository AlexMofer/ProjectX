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

import android.os.Build;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import am.util.job.core.BaseJob;

/**
 * 使用SparseArray实现的任务参数
 * Created by Alex on 2021/3/12.
 */
class SparseParams extends SparseDataArray implements BaseJob.Params {

    private final SparseArray<WeakReference<Object>> mWeakParams = new SparseArray<>();

    private boolean contains(int key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return mWeakParams.contains(key);
        } else {
            return mWeakParams.get(key) != null;
        }
    }

    @Override
    public void clearWeak() {
        mWeakParams.clear();
    }

    @Override
    public void putWeak(int key, Object value) {
        if (value == null) {
            mWeakParams.remove(key);
            return;
        }
        mWeakParams.put(key, new WeakReference<>(value));
    }

    @Override
    public <V> V getWeak(int key, V defaultValue) {
        if (contains(key)) {
            final WeakReference<Object> reference = mWeakParams.get(key);
            final Object value = reference == null ? null : reference.get();
            //noinspection unchecked
            return value == null ? defaultValue : (V) value;
        } else {
            return defaultValue;
        }
    }
}
