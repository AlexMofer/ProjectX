/*
 * Copyright (C) 2023 AlexMofer
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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * 外部存储辅助
 * Created by Alex on 2022/5/3.
 */
public class ExternalStorageUtils {

    private ExternalStorageUtils() {
        //no instance
    }

    /**
     * 判断是否为外部存储管理器
     *
     * @param context Context
     * @return 应用为外部存储管理器时返回true
     */
    public static boolean isManager(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ?
                Environment.isExternalStorageManager() : ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取外部存储管理器的配置器
     *
     * @param activity AppCompatActivity
     * @param callback 回调
     * @return 配置器
     */
    public static ActivityResultLauncher<?> getManagerConfigurator(AppCompatActivity activity,
                                                                   Callback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> callback.onConfigResult());
        } else {
            return activity.registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    result -> callback.onConfigResult());
        }
    }

    /**
     * 配置
     *
     * @param context      Context
     * @param configurator 配置器
     * @return 是否成功
     */
    public static boolean config(Context context, ActivityResultLauncher<?> configurator) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //noinspection unchecked
                ((ActivityResultLauncher<Intent>) configurator)
                        .launch(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                .setData(Uri.parse("package:" + context.getPackageName())));
            } else {
                //noinspection unchecked
                ((ActivityResultLauncher<String>) configurator)
                        .launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 回调
     */
    public interface Callback {

        /**
         * 配置结果
         */
        void onConfigResult();
    }
}
