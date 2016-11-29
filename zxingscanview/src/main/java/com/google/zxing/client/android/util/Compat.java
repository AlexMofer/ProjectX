package com.google.zxing.client.android.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

        void setHotspot(Drawable drawable, float x, float y);
    }

    private static class CompatBase implements CompatImpl {

        @Override
        public int checkSelfPermission(Context context, String permission) {
            return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            // do nothing
        }
    }

    @TargetApi(21)
    private static class CompatLollipop extends CompatBase {

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
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
        } else if (version >= 21) {
            IMPL = new CompatLollipop();
        } else {
            IMPL = new CompatBase();
        }
    }

    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     *
     * @param permission The name of the permission being checked.
     * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
     * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
     * @see android.content.pm.PackageManager#checkPermission(String, String)
     */
    public static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return IMPL.checkSelfPermission(context, permission);
    }

    /**
     * Specifies the hotspot's location within the drawable.
     *
     * @param drawable The Drawable against which to invoke the method.
     * @param x The X coordinate of the center of the hotspot
     * @param y The Y coordinate of the center of the hotspot
     */
    public static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }
}
