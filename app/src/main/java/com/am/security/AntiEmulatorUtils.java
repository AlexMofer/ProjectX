package com.am.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import diff.strazzere.anti.AntiEmulator;

/**
 * 应用安全工具
 * Created by Alex on 2016/4/5.
 */
public class AntiEmulatorUtils {

    private static final int SIGNATURE = 2074025482;// TODO 修改应用签名Hash
    /**
     * 检查安全性
     *
     * @param context Context
     */
    public static void checkSecurity(Context context) {
        if (!isDebuggable(context)) {
            if (AntiEmulator.isQEmuEnvDetected(context)
                    || AntiEmulator.isDebugged()
                    || AntiEmulator.isMonkeyDetected()
                    || AntiEmulator.isTaintTrackingDetected(context)) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else if (SIGNATURE != getSignature(context)) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    /**
     * 程序是否可调试
     *
     * @param context Context
     * @return 程序是否可调试
     */
    public static boolean isDebuggable(Context context) {
        return (context.getApplicationInfo().flags &=
                ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 获取签名
     *
     * @param context Context
     * @return 签名哈希值
     */
    public static int getSignature(Context context) {
        int sig;
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] s = pi.signatures;
            sig = s[0].hashCode();
        } catch (Exception e) {
            sig = 0;
        }
        return sig;
    }
}
