/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.lifecycle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import java.util.UUID;

import io.github.alexmofer.android.support.app.ApplicationHolder;

/**
 * 借助 ApplicationHolder 缓存，使用 SavedStateHandle 记录缓存ID
 * Created by Alex on 2026/3/17.
 */
public interface SavedStateHandleWithApplicationCacheDataHelper<T> {

    /**
     * 获取 SavedStateProvider 的存储 Key
     *
     * @return 存储 Key
     */
    @NonNull
    default String getBundleKey() {
        return "SavedStateHandleWithApplicationCacheDataHelper:bundle";
    }

    /**
     * 获取用于在 ApplicationHolder 中读取数据的缓存 ID 的存储于 SavedStateProvider 的存储数据中的缓存 ID Key
     *
     * @return 缓存 ID Key
     */
    @NonNull
    default String getCacheIdKey() {
        return "SavedStateHandleWithApplicationCacheDataHelper:cacheId";
    }

    /**
     * 创建数据
     * 注意：ApplicationHolder 中没有对应缓存数据时，才会调用该创建方法
     *
     * @return 数据
     */
    T create();

    /**
     * 处理
     *
     * @param handle SavedStateHandle
     * @param helper 辅助
     * @return 缓存的或者刚创建的数据
     */
    static <T> T handle(@NonNull SavedStateHandle handle,
                        @NonNull SavedStateHandleWithApplicationCacheDataHelper<T> helper) {
        final String bundleKey = helper.getBundleKey();
        final String cacheIdKey = helper.getCacheIdKey();
        final Bundle savedInstanceState = handle.get(bundleKey);
        T data = null;
        String cacheId = null;
        if (savedInstanceState != null) {
            cacheId = savedInstanceState.getString(cacheIdKey);
            if (cacheId != null) {
                data = ApplicationHolder.getData(cacheId);
                ApplicationHolder.removeData(cacheId);
            }
        }
        if (data == null) {
            data = helper.create();
            cacheId = UUID.randomUUID().toString();
        }
        final Bundle savedState = new Bundle();
        savedState.putString(cacheIdKey, cacheId);
        final Object[] temp = new Object[2];
        temp[0] = cacheId;
        temp[1] = data;
        handle.setSavedStateProvider(bundleKey, () -> {
            ApplicationHolder.putData((String) temp[0], temp[1]);
            return savedState;
        });
        return data;
    }
}