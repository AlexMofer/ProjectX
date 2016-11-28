package com.google.zxing.client.android.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Process;

/**
 * 版本兼容器
 * Created by Alex on 2016/11/28.
 */

public class Compat {

    private Compat() {
    }

    private interface CompatImpl {
        int checkSelfPermission(Context context, String permission);
    }

    private static class CompatBase implements CompatImpl {

        @Override
        public int checkSelfPermission(Context context, String permission) {
            return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
        }
    }

    @TargetApi(23)
    private static class CompatAPI23 extends CompatBase {
        @Override
        public int checkSelfPermission(Context context, String permission) {
            return context.checkSelfPermission(permission);
        }
    }

    private static final CompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new CompatAPI23();
        } else {
            IMPL = new CompatBase();
        }
    }

    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     *
     * @param permission The name of the permission being checked.
     *
     * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
     * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
     *
     * @see android.content.pm.PackageManager#checkPermission(String, String)
     */
    public static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return IMPL.checkSelfPermission(context, permission);
    }
}
