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
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.collection.ArrayMap;

/**
 * Application 持有者
 * Created by Alex on 2024/2/28.
 */
public class ApplicationHolder {

    private static ApplicationHolder mInstance;
    private final Application mApplication;
    private final ArrayMap<String, ApplicationData> mApplicationDataMap = new ArrayMap<>();

    private ApplicationHolder(Application application) {
        mApplication = application;
        application.registerComponentCallbacks(new InnerComponentCallbacks(this));
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
     * 发送Toast
     *
     * @param text     文本
     * @param duration 时长
     */
    @Deprecated
    public static void toast(CharSequence text, int duration) {
        Toast.makeText(getApplicationContext(), text, duration).show();
    }

    /**
     * 发送Toast
     *
     * @param text 文本
     */
    @Deprecated
    public static void toast(CharSequence text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    /**
     * 发送Toast
     *
     * @param resId    文本资源
     * @param duration 时长
     */
    @Deprecated
    public static void toast(@StringRes int resId, int duration) {
        Toast.makeText(getApplicationContext(), resId, duration).show();
    }

    /**
     * 发送Toast
     *
     * @param resId 文本资源
     */
    @Deprecated
    public static void toast(@StringRes int resId) {
        toast(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 获取应用级数据
     *
     * @param clazz   类
     * @param creator 构建者
     * @param <T>     类型
     * @return 应用级数据对象
     */
    @Deprecated
    @NonNull
    public static <T extends ApplicationData> T getApplicationData(Class<T> clazz,
                                                                   @NonNull ApplicationDataCreator<T> creator) {
        synchronized (ApplicationHolder.class) {
            final ApplicationHolder holder = getInstance();
            final String key = clazz.getName();
            final ApplicationData saved = holder.mApplicationDataMap.get(key);
            if (saved != null) {
                //noinspection unchecked
                return (T) saved;
            }
            final T created = creator.create();
            holder.mApplicationDataMap.put(key, created);
            return created;
        }
    }

    static void removeApplicationData(ApplicationData data) {
        synchronized (ApplicationHolder.class) {
            final ApplicationData removed =
                    getInstance().mApplicationDataMap.remove(data.getClass().getName());
            if (removed != null) {
                removed.onDestroy();
            }
        }
    }

    private static class InnerComponentCallbacks implements ComponentCallbacks2 {

        private final ApplicationHolder mHolder;

        public InnerComponentCallbacks(ApplicationHolder holder) {
            mHolder = holder;
        }

        @Override
        public void onConfigurationChanged(@NonNull Configuration newConfig) {
            synchronized (ApplicationHolder.class) {
                final int count = mHolder.mApplicationDataMap.size();
                for (int i = 0; i < count; i++) {
                    mHolder.mApplicationDataMap.valueAt(i).onConfigurationChanged(newConfig);
                }
            }
        }

        @Override
        public void onLowMemory() {
            // ignore 14 以前的主要回调，实现 onTrimMemory 即可
        }

        @Override
        public void onTrimMemory(int level) {
            synchronized (ApplicationHolder.class) {
                final int count = mHolder.mApplicationDataMap.size();
                for (int i = count - 1; i >= 0; i--) {
                    final ApplicationData saved = mHolder.mApplicationDataMap.valueAt(i);
                    if (saved.onTrimMemory(level)) {
                        mHolder.mApplicationDataMap.removeAt(i).onDestroy();
                    }
                }
            }
        }
    }
}
