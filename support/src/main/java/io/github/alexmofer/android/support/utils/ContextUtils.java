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
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.Dimension;
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
     * Converts an dip data value holding a dimension to its final floating
     * point value. The parameter <var>value</var> are as in {@link TypedValue#COMPLEX_UNIT_DIP}.
     *
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @param value   A dip data value.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    public static float getDimension(DisplayMetrics metrics,
                                     @Dimension(unit = Dimension.DP) float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    /**
     * This is the same as {@link #getDimension(DisplayMetrics, float)}
     */
    public static float getDimension(Context context,
                                     @Dimension(unit = Dimension.DP) float value) {
        return getDimension(context.getResources().getDisplayMetrics(), value);
    }


    /**
     * Converts a dip data value holding a dimension to its final value
     * as an integer pixel size.  This is the same as
     * {@link #getDimension}, except the raw floating point value is
     * converted to an integer (pixel) value for use as a size.  A size
     * conversion involves rounding the base value, and ensuring that a
     * non-zero base value is at least one pixel in size.
     * The given <var>value</var> must be structured as a
     * {@link TypedValue#COMPLEX_UNIT_DIP}.
     *
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @param value   A dip data value.
     * @return The number of pixels specified by the data and its desired
     * multiplier and units.
     */
    public static int getDimensionPixelOffset(DisplayMetrics metrics,
                                              @Dimension(unit = Dimension.DP) float value) {
        final float f = getDimension(metrics, value);
        final int res = (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
        if (res != 0) return res;
        if (value == 0) return 0;
        if (value > 0) return 1;
        return -1;
    }

    /**
     * This is the same as {@link #getDimensionPixelOffset(DisplayMetrics, float)}
     */
    public static int getDimensionPixelOffset(Context context,
                                              @Dimension(unit = Dimension.DP) float value) {
        return getDimensionPixelOffset(context.getResources().getDisplayMetrics(), value);
    }

    /**
     * Converts a dip data value holding a dimension to its final value
     * as an integer pixel offset.  This is the same as
     * {@link #getDimension}, except the raw floating point value is
     * truncated to an integer (pixel) value.
     * The given <var>value</var> must be structured as a
     * {@link TypedValue#COMPLEX_UNIT_DIP}.
     *
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @param value   A dip data value.
     * @return The number of pixels specified by the data and its desired
     * multiplier and units.
     */
    public static int getDimensionPixelSize(DisplayMetrics metrics,
                                            @Dimension(unit = Dimension.DP) float value) {
        return (int) getDimension(metrics, value);
    }

    /**
     * This is the same as {@link #getDimensionPixelSize(DisplayMetrics, float)}
     */
    public static int getDimensionPixelSize(Context context,
                                            @Dimension(unit = Dimension.DP) float value) {
        return getDimensionPixelSize(context.getResources().getDisplayMetrics(), value);
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
