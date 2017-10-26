package am.project.x.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.io.File;

import am.project.x.BuildConfig;


/**
 * Activity工具类
 * Created by Xiang Zhicheng on 2017/5/9.
 */

public class ContextUtil {

    /**
     * 获取状态栏高度
     *
     * @param context Context
     * @return 状态栏高度
     */
    public static int getStateBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取导航栏（虚拟键盘栏，并非所有设备都有）高度
     *
     * @param context Context
     * @return 导航栏高度
     */
    public static int getNavBarHeight(Context context) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = context.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(context)) {
                resourceId = resources.getIdentifier(orientation ==
                        Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" :
                        "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation ==
                        Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" :
                        "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 打开浏览器访问网址
     *
     * @param context Context
     * @param url     网址
     */
    public static boolean openBrowser(Context context, String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 打开电子邮件发送邮件
     *
     * @param context   Context
     * @param addresses 邮箱地址
     */
    public static void openEmail(Context context, String... addresses) {
        openEmail(null, context, addresses);
    }

    /**
     * 打开电子邮件发送邮件
     *
     * @param subject   主题
     * @param context   Context
     * @param addresses 邮箱地址
     */
    public static void openEmail(String subject, Context context, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (subject != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 发送PDF文件
     *
     * @param context  Context
     * @param filePath 文件路径
     * @param title    标题
     */
    public static void sendPDFFile(Context context, String filePath, String title) {
        // TODO
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri url = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                new File(filePath));
        shareIntent.putExtra(Intent.EXTRA_STREAM, url);
        shareIntent.setType("application/pdf");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(shareIntent, title));
        }
    }

    public static boolean isMainProcess(Context context) {
        final int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess :
                mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        return BuildConfig.APPLICATION_ID.equals(processName);
    }
}