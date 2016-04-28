package com.am.security;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
public class MessageDigestUtils {

    /**
     * 获取信息摘要
     *
     * @param src       数据源
     * @param algorithm 算法
     * @return Base64编码的信息摘要
     */
    public static String getMessageDigest(byte[] src, String algorithm) {
        if (src == null)
            return null;
        try {
            return Base64.encodeToString(MessageDigest.getInstance(algorithm).digest(src),
                    Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取MD5
     *
     * @param src 数据源
     * @return Base64编码的MD5
     */
    public static String getMD5(byte[] src) {
        return getMessageDigest(src, "MD5");
    }

    /**
     * 获取SHA-256
     *
     * @param src 数据源
     * @return Base64编码的SHA-256
     */
    public static String getSHA256(byte[] src) {
        return getMessageDigest(src, "SHA-256");
    }
}
