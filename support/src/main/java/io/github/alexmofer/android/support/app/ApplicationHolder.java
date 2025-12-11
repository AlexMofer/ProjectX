/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.app;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

/**
 * Application 持有者
 * Created by Alex on 2024/2/28.
 */
public class ApplicationHolder {

    private static ApplicationHolder mInstance;
    private final Application mApplication;
    private final ArrayMap<String, Object> mDataMap = new ArrayMap<>();

    private ApplicationHolder(Application application) {
        mApplication = application;
    }

    /**
     * 创建
     *
     * @param application Application
     */
    public static void create(Application application) {
        mInstance = new ApplicationHolder(application);
    }

    @SuppressWarnings("ALL")
    @NonNull
    private static ApplicationHolder getInstance() {
        if (mInstance == null) {
            // 尝试使用反射方法构建
            try {
                final Class<?> clazz = Class.forName("android.app.ActivityThread", false,
                        Application.class.getClassLoader());
                final Object application = clazz.getMethod("getApplication").invoke(
                        clazz.getMethod("currentActivityThread").invoke(null));
                if (application instanceof Application) {
                    create((Application) application);
                }
            } catch (Throwable t) {
                throw new RuntimeException("Call create before use ApplicationHolder.");
            }
        }
        return mInstance;
    }

    /**
     * Return the application.
     */
    @NonNull
    public static <T extends Application> T getApplication() {
        //noinspection unchecked
        return (T) getInstance().mApplication;
    }

    /**
     * 获取 Application级别 Context
     *
     * @return Context
     */
    public static Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * 存入数据
     *
     * @param id   ID
     * @param data 数据
     */
    public static void putData(@NonNull String id, Object data) {
        getInstance().mDataMap.put(id, data);
    }

    /**
     * 获取数据
     *
     * @param id ID
     * @return 数据，注意类型转换问题
     */
    @Nullable
    public static <T> T getData(String id) {
        //noinspection unchecked
        return (T) getInstance().mDataMap.get(id);
    }

    /**
     * 移除数据
     *
     * @param id ID
     */
    public static void removeData(String id) {
        getInstance().mDataMap.remove(id);
    }
}
