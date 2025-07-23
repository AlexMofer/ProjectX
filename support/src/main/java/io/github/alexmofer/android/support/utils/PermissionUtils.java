/*
 * Copyright (C) 2025 AlexMofer
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import java.util.Arrays;

/**
 * 权限工具
 * Created by Alex on 2025/7/22.
 */
public class PermissionUtils {

    private PermissionUtils() {
        //no instance
    }

    /**
     * 判断应用是否声明权限
     *
     * @param context     Context
     * @param packageName 包名
     * @param permissions 权限
     * @return 应用声明了对应权限时返回true
     */
    public static boolean hasDeclarePermissions(@NonNull Context context,
                                                @NonNull String packageName,
                                                String... permissions) {
        final PackageManager manager = context.getPackageManager();
        final PackageInfo info;
        try {
            info = manager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (Throwable t) {
            return false;
        }
        final String[] requestedPermissions = info.requestedPermissions;
        if (requestedPermissions == null) {
            return false;
        }
        final ArraySet<String> ps = new ArraySet<>(Arrays.asList(permissions));
        for (String permission : requestedPermissions) {
            while (ps.remove(permission)) {
                if (ps.isEmpty()) {
                    return true;
                }
            }
        }
        return ps.isEmpty();
    }

    /**
     * 判断当前应用是否声明权限
     *
     * @param context     Context
     * @param permissions 权限
     * @return 当前应用声明了对应权限时返回 true
     */
    public static boolean hasSelfDeclarePermissions(@NonNull Context context,
                                                    String... permissions) {
        return hasDeclarePermissions(context, context.getPackageName(), permissions);
    }
}
