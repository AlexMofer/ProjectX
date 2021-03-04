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

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 任务参数
 * Created by Alex on 2021/3/1.
 */
public final class JobParam extends AnyValue {

    private final HashMap<Integer, WeakReference<Object>> mWeakParams = new HashMap<>();

    private boolean contains(int key) {
        return mWeakParams.containsKey(key);
    }

    /**
     * 清空弱引用值
     */
    public void clearWeak() {
        mWeakParams.clear();
    }

    /**
     * 设置弱引用值
     *
     * @param key   键
     * @param value 值
     */
    public void putWeak(int key, @Nullable Object value) {
        if (value == null) {
            mWeakParams.remove(key);
            return;
        }
        mWeakParams.put(key, new WeakReference<>(value));
    }

    /**
     * 获取弱引用值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <V>          类型
     * @return 值
     */
    public <V> V getWeak(int key, @Nullable V defaultValue) {
        if (contains(key)) {
            final WeakReference<Object> reference = mWeakParams.get(key);
            final Object value = reference == null ? null : reference.get();
            //noinspection unchecked
            return value == null ? defaultValue : (V) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取弱引用值
     *
     * @param key 键
     * @param <V> 类型
     * @return 值
     */
    public <V> V getWeak(int key) {
        if (contains(key)) {
            final WeakReference<Object> reference = mWeakParams.get(key);
            //noinspection unchecked
            return reference == null ? null : (V) reference.get();
        } else {
            return null;
        }
    }
}
