/*
 * Copyright (C) 2022 AlexMofer
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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

/**
 * Context工具
 * Created by Alex on 2022/11/30.
 */
public class ContextUtils {

    private ContextUtils() {
        //no instance
    }

    /**
     * 判断是否为深色模式
     *
     * @param context Context
     * @return 为深色模式时返回true
     * @noinspection BooleanMethodIsAlwaysInverted
     */
    public static boolean isNight(Context context) {
        return (context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 获取应用内声明的ContentProvider的authority
     *
     * @param context Context
     * @param clazz   ContentProvider
     * @return authority
     */
    public static String getAuthority(@NonNull Context context,
                                      @NonNull Class<? extends ContentProvider> clazz) {
        final PackageInfo info;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
        } catch (Exception e) {
            return null;
        }
        if (info == null || info.providers == null) {
            return null;
        }
        final ProviderInfo[] providers = info.providers;
        final String name = clazz.getName();
        for (ProviderInfo provider : providers) {
            if (TextUtils.equals(provider.name, name)) {
                return provider.authority;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Activity} given a {@link Context} or null if there is no {@link Activity},
     * taking into account the potential hierarchy of {@link ContextWrapper ContextWrappers}.
     */
    @Nullable
    public static Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 杀死APP所有进程
     *
     * @param context Context
     */
    public static void killAppProcesses(Context context) {
        final int myPid = android.os.Process.myPid();
        final ActivityManager manager;
        if (Build.VERSION.SDK_INT >= 23) {
            manager = context.getSystemService(ActivityManager.class);
        } else {
            manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (manager != null) {
            final List<ActivityManager.RunningAppProcessInfo> list =
                    manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : list) {
                if (info.pid != myPid) {
                    android.os.Process.killProcess(info.pid);
                }
            }
        }
        android.os.Process.killProcess(myPid);
    }

    /**
     * 获取外部缓存目录
     *
     * @param context     Context
     * @param useInternal 外部存储不可用时是否使用内部存储，禁用内部存储时，外部存储不可用时返回null
     * @return 外部缓存目录
     */
    public static File getExternalCacheDir(Context context, boolean useInternal) {
        File dir = context.getExternalCacheDir();
        if (dir == null && useInternal) {
            dir = context.getCacheDir();
        }
        if (dir == null) {
            return null;
        }
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取外部存储文件目录
     *
     * @param context     Context
     * @param name        目录名称，为空时返回根目录
     * @param useInternal 外部存储不可用时是否使用内部存储，禁用内部存储时，外部存储不可用时返回null
     * @return 外部存储文件目录
     */
    public static File getExternalFilesDir(Context context, @Nullable String name,
                                           boolean useInternal) {
        File file = context.getExternalFilesDir(name);
        if (file == null && useInternal) {
            file = name == null ? context.getFilesDir() : new File(context.getFilesDir(), name);
        }
        if (file == null) {
            return null;
        }
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
        return file;
    }
}
