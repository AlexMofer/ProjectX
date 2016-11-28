package com.google.zxing.client.android.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 工具类
 * Created by Alex on 2016/11/28.
 */

public class Utils {
    public static final String PERMISSION_VIBRATE = "android.permission.VIBRATE";//振动器权限
    public static final String PERMISSION_CAMERA = "android.permission.CAMERA";//相机权限

    private Utils() {
    }

    /**
     * 检查权限
     *
     * @param context    Context
     * @param permission 权限
     * @return 是否拥有权限
     */
    public static boolean lacksPermission(Context context, String permission) {
        return Compat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }
}
