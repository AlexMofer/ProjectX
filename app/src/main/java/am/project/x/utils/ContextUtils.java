/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Context工具类
 */
public class ContextUtils {

    private ContextUtils() {
        //no instance
    }

    /**
     * 打开浏览器访问网址
     *
     * @param context Context
     * @param url     网址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 打开电子邮件发送邮件
     *
     * @param context    Context
     * @param subject    主题
     * @param attachment 附件
     * @param addresses  邮箱地址
     */
    public static void sendEmail(Context context, @Nullable String subject,
                                 @Nullable Uri attachment, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (subject != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (attachment != null)
            intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 判断是否拥有文件读写权限
     *
     * @param context Context
     * @return 是否拥有权限
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasWriteExternalStoragePermission(Context context) {
        return Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断WIFI是否连接
     *
     * @param context Context
     * @return true:连接， false:未连接
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isWifiConnected(Context context) {
        final WifiManager manager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (manager == null)
            return false;
        final DhcpInfo info = manager.getDhcpInfo();
        return info != null && info.ipAddress != 0;
    }

    /**
     * 获取WIFI IP地址
     *
     * @param context Context
     * @return IP地址
     */
    public static String getWifiIp(Context context) {
        final WifiManager manager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (manager == null)
            return "0.0.0.0";
        final DhcpInfo info = manager.getDhcpInfo();
        if (info == null)
            return "0.0.0.0";
        final int ip = info.ipAddress;
        return (0xFF & ip) + "." + (0xFF & ip >> 8) + "." + (0xFF & ip >> 16) + "."
                + (0xFF & ip >> 24);
    }

    /**
     * 获取启动Intent
     *
     * @param context Context
     * @return 启动Intent
     */
    @SuppressWarnings("unused")
    public static Intent getLaunchIntent(Context context) {
        //noinspection ConstantConditions
        return context.getPackageManager().getLaunchIntentForPackage(
                context.getPackageName()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
