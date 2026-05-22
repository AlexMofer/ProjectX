package io.github.alexmofer.projectx.features;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * 签名工具
 * Created by Alex on 2026/5/22.
 */
public final class AppSignatureUtil {

    private AppSignatureUtil() {
        //no instance
    }

    /**
     * 获取已安装应用的签名 MD5 / SHA-1 / SHA-256 指纹
     *
     * @param context     上下文
     * @param packageName 目标应用的包名
     * @param type        算法类型 "MD5" / "SHA-1" / "SHA-256"
     */
    @Nullable
    public static String getAppSignature(@NonNull Context context,
                                         @NonNull String packageName,
                                         @NonNull String type) {
        try {
            PackageManager pm = context.getPackageManager();
            Signature[] signatures;

            // 1. 适配 Android 9.0 (API 28) 及以上的高版本 API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                SigningInfo signingInfo = packageInfo.signingInfo;
                if (signingInfo == null) return null;

                // 关键点：如果是单签名应用，直接取 apkContentsSigners [1]
                // 这样可以有效过滤掉 Google Play 强制加入的 Source Stamp（源戳）二次签名干扰 [1]
                signatures = signingInfo.getApkContentsSigners();
            } else {
                // 2. 适配老版本 Android 系统
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                signatures = packageInfo.signatures;
            }

            if (signatures != null && signatures.length > 0) {
                // 取第一个主签名进行哈希计算
                return encryptionMD5(signatures[0].toByteArray(), type);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    // 将字节码转换为标准的冒号分隔十六进制字符串 (例如 AA:BB:CC...)
    @Nullable
    private static String encryptionMD5(@NonNull byte[] byteStr, @NonNull String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] digest = md.digest(byteStr);
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(0xFF & digest[i]);
                if (hex.length() == 1) {
                    res.append("0");
                }
                res.append(hex.toUpperCase(Locale.ENGLISH));
                if (i < digest.length - 1) {
                    res.append(":");
                }
            }
            return res.toString();
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
