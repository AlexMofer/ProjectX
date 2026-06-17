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

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

/**
 * Device 工具类
 * Created by Alex on 2024/3/18.
 */
public final class DeviceUtils {

    private DeviceUtils() {
        //no instance
    }

    /**
     * 获取设备名称
     *
     * @param context  Context
     * @param supplier 保底方式
     * @return 设备名称
     */
    public static String getName(@NonNull Context context,
                                 @NonNull Supplier<String> supplier) {
        String name;
        try {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
        } catch (Throwable t) {
            // ignore
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            name = Settings.Global.getString(context.getContentResolver(),
                    Settings.Global.DEVICE_NAME);
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
        }
        name = Build.MANUFACTURER + " " + Build.MODEL;
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return supplier.get();
    }

    /**
     * 获取设备名称
     *
     * @param context Context
     * @return 设备名称
     */
    @NonNull
    public static String getName(Context context) {
        return getName(context, () -> "Android Device");
    }
}
