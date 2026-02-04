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

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.collection.ArrayMap;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

/**
 * Application 持有者
 * Created by Alex on 2024/2/28.
 */
public class ApplicationHolder {

    @SuppressLint("StaticFieldLeak")
    private static ApplicationHolder mInstance;
    private final Application mApplication;
    private final ArrayMap<String, Object> mDataMap = new ArrayMap<>();
    private boolean mContextAdjusted;
    private Context mAdjustedContext;

    private ApplicationHolder(@NonNull Application application) {
        mApplication = application;
    }

    /**
     * 创建
     *
     * @param application Application
     */
    public static void create(@NonNull Application application) {
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
     * 获取应用级别上下文
     * <p> 如果应用有应用内多语言切换，请使用 {@link #getAdjustedApplicationContext()}
     *
     * @return Context
     * @see #getAdjustedApplicationContext()
     */
    @NonNull
    public static Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * 获取针对应用内多语言切换修正过的应用级别上下文
     *
     * @return Context
     */
    @NonNull
    public static Context getAdjustedApplicationContext() {
        if (Build.VERSION.SDK_INT >= 33) {
            return getApplicationContext();
        }
        final ApplicationHolder holder = getInstance();
        holder.mApplication.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                invalidateAdjustedApplicationContext();
            }

            @Override
            public void onLowMemory() {
                // do nothings
            }
        });
        if (holder.mContextAdjusted) {
            if (holder.mAdjustedContext != null) {
                return holder.mAdjustedContext;
            }
            return getApplicationContext();
        }
        final Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale == null) {
            // 跟随系统
            holder.mContextAdjusted = true;
            holder.mAdjustedContext = null;
            return getApplicationContext();
        }
        final Context context = ApplicationHolder.getApplicationContext();
        final Configuration configuration =
                new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        holder.mContextAdjusted = true;
        holder.mAdjustedContext = context.createConfigurationContext(configuration);
        return holder.mAdjustedContext;
    }

    /**
     * 重置纠正过的应用级上下文
     * <p> 在应用内设置语言后，请调用此方法，否则通过应用级上下文获取的资源可能并不对，尤其是多语言字符串
     *
     * @see AppCompatDelegate#setApplicationLocales(LocaleListCompat)
     */
    public static void invalidateAdjustedApplicationContext() {
        if (Build.VERSION.SDK_INT >= 33) {
            return;
        }
        if (mInstance != null) {
            mInstance.mContextAdjusted = false;
            mInstance.mAdjustedContext = null;
        }
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
    public static <T> T getData(@NonNull String id) {
        //noinspection unchecked
        return (T) getInstance().mDataMap.get(id);
    }

    /**
     * 移除数据
     *
     * @param id ID
     */
    public static void removeData(@NonNull String id) {
        getInstance().mDataMap.remove(id);
    }
}
