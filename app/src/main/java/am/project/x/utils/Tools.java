package am.project.x.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.List;

import am.project.support.compat.AMActivityManagerCompat;
import am.project.support.compat.AMApplicationInfoCompat;


/**
 * 工具
 * Created by Alex on 2016/1/23.
 */
public class Tools {

    /**
     * 判断是否为图片
     *
     * @param filePath 文件路径
     * @return 是否为图片
     */
    public static boolean isBitmapFile(String filePath) {
        return isBitmapFile(filePath, null);
    }

    /**
     * 判断是否为图片
     *
     * @param filePath 文件路径
     * @return 是否为图片
     */
    public static boolean isBitmapFile(String filePath, int[] size) {
        int width;
        int height;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            width = options.outWidth;
            height = options.outHeight;
        } catch (OutOfMemoryError e) {
            return false;
        }
        if (size != null && size.length >= 2) {
            size[0] = width;
            size[1] = height;
        }
        return width > 0 && height > 0;
    }

    /**
     * 获取最大运存
     * @param context Context
     * @return 最大运存
     */
    public static int getLargeMemoryClass(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        if (AMApplicationInfoCompat.isLargeHeap(context.getApplicationInfo())) {
            memoryClass = AMActivityManagerCompat.getLargeMemoryClass(am);
        }
        return memoryClass;
    }

    /**
     * 程序是否在前台运行
     *
     * @return 是否在前台运行
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager
                    .RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
