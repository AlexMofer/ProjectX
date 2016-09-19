package am.project.x.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * 版本工具类
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class PackageInfoUtils {

    /**
     * 获取版本号
     *
     * @param context Context
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取版本名
     *
     * @param context Context
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
