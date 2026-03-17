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
package io.github.alexmofer.android.support.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import java.util.UUID;
import java.util.function.Supplier;

import io.github.alexmofer.android.support.app.ApplicationHolder;

/**
 * SavedStateHandle 工具
 * Created by Alex on 2026/3/17.
 */
public final class SavedStateHandleUtils {
    private static final String KEY_CACHE_ID = "io.github.alexmofer.android.support:CACHE_ID";
    private static final String KEY_BUNDLE = "io.github.alexmofer.android.support:bundle";

    private SavedStateHandleUtils() {
        //no instance
    }

    /**
     * 获取缓存或者创建数据
     *
     * @param handle      SavedStateHandle
     * @param bundleKey   SavedStateProvider 的存储 Key
     * @param cacheIdKey  用于在 ApplicationHolder 中读取数据的缓存 ID 的存储于 SavedStateProvider 的存储数据中的缓存 ID Key
     * @param constructor 数据构建器
     * @return 数据
     */
    public static <T> T getOrCreateByApplicationData(@NonNull SavedStateHandle handle,
                                                     @NonNull String bundleKey,
                                                     @NonNull String cacheIdKey,
                                                     @NonNull Supplier<T> constructor) {
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
            data = constructor.get();
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

    /**
     * 获取缓存或者创建数据
     *
     * @param handle      SavedStateHandle
     * @param bundleKey   SavedStateProvider 的存储 Key
     * @param constructor 数据构建器
     * @return 数据
     */
    public static <T> T getOrCreateByApplicationData(@NonNull SavedStateHandle handle,
                                                     @NonNull String bundleKey,
                                                     @NonNull Supplier<T> constructor) {
        return getOrCreateByApplicationData(handle, bundleKey, KEY_CACHE_ID, constructor);
    }

    /**
     * 获取缓存或者创建数据
     *
     * @param handle      SavedStateHandle
     * @param constructor 数据构建器
     * @return 数据
     */
    public static <T> T getOrCreateByApplicationData(@NonNull SavedStateHandle handle,
                                                     @NonNull Supplier<T> constructor) {
        return getOrCreateByApplicationData(handle, KEY_BUNDLE, constructor);
    }
}
