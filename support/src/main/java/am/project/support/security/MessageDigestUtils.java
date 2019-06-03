/*
 * Copyright (C) 2015 AlexMofer
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

package am.project.support.security;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageDigestUtils {
    /**
     * 获取信息摘要
     *
     * @param src       数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static byte[] getMessageDigest(byte[] src, String algorithm) {
        if (src == null)
            return null;
        try {
            return MessageDigest.getInstance(algorithm).digest(src);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static byte[] getMessageDigest(InputStream input, String algorithm) {
        if (input == null)
            return null;
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            int len;
            final byte[] buffer = new byte[1024];
            try {
                while ((len = input.read(buffer, 0, buffer.length)) != -1) {
                    md.update(buffer, 0, len);
                }
            } catch (Exception e) {
                return null;
            }
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取MD5
     *
     * @param src 数据源
     * @return MD5
     */
    public static byte[] getMD5(byte[] src) {
        return getMessageDigest(src, "MD5");
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static byte[] getMD5(InputStream input) {
        return getMessageDigest(input, "MD5");
    }

    /**
     * 获取SHA-1
     *
     * @param src 数据源
     * @return SHA-1
     */
    public static byte[] getSHA1(byte[] src) {
        return getMessageDigest(src, "SHA-1");
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static byte[] getSHA1(InputStream input) {
        return getMessageDigest(input, "SHA-1");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param src 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    public static byte[] getSHA224(byte[] src) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(src, "SHA-224");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    public static byte[] getSHA224(InputStream input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224");
    }

    /**
     * 获取SHA-256
     *
     * @param src 数据源
     * @return SHA-256
     */
    public static byte[] getSHA256(byte[] src) {
        return getMessageDigest(src, "SHA-256");
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static byte[] getSHA256(InputStream input) {
        return getMessageDigest(input, "SHA-256");
    }

    /**
     * 获取SHA-384
     *
     * @param src 数据源
     * @return SHA-384
     */
    public static byte[] getSHA384(byte[] src) {
        return getMessageDigest(src, "SHA-384");
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static byte[] getSHA384(InputStream input) {
        return getMessageDigest(input, "SHA-384");
    }

    /**
     * 获取SHA-512
     *
     * @param src 数据源
     * @return SHA-512
     */
    public static byte[] getSHA512(byte[] src) {
        return getMessageDigest(src, "SHA-512");
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static byte[] getSHA512(InputStream input) {
        return getMessageDigest(input, "SHA-512");
    }

    private static String toHexString(byte[] output, int length) {
        final String str = new BigInteger(1, output).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (str.length() < length) {
            final int number = length - str.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(str);
        return builder.toString();
    }

    private static String getMD5(byte[] output, int length) {
        final String md5 = toHexString(output, 32);
        if (length == 16) {
            return md5.substring(8, 24);
        }
        return md5;
    }

    /**
     * 获取MD5
     *
     * @param src    数据源
     * @param length 位数（16位或32位）
     * @return MD5
     */
    public static String getMD5String(byte[] src, int length) {
        return getMD5(getMD5(src), length);
    }

    /**
     * 获取MD5
     *
     * @param src 数据源
     * @return MD5
     */
    public static String getMD5String(byte[] src) {
        return getMD5(getMD5(src), 32);
    }

    /**
     * 获取MD5
     *
     * @param input  数据源
     * @param length 位数（16位或32位）
     * @return MD5
     */
    public static String getMD5String(InputStream input, int length) {
        return getMD5(getMD5(input), length);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static String getMD5String(InputStream input) {
        return getMD5(getMD5(input), 32);
    }

    /**
     * 获取字符MD5
     *
     * @param str 字符串
     * @return MD5
     */
    public static String getMD5String(String str) {
        return getMD5String(str.getBytes(), 32);
    }

    /**
     * 获取SHA-1
     *
     * @param src 数据源
     * @return SHA-1
     */
    public static String getSHA1String(byte[] src) {
        return toHexString(getSHA1(src), 40);
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static String getSHA1String(InputStream input) {
        return toHexString(getSHA1(input), 40);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param src 数据源
     * @return SHA-224
     */
    public static String getSHA224String(byte[] src) {
        return toHexString(getSHA224(src), 56);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    public static String getSHA224String(InputStream input) {
        return toHexString(getSHA224(input), 56);
    }

    /**
     * 获取SHA-256
     *
     * @param src 数据源
     * @return SHA-256
     */
    public static String getSHA256String(byte[] src) {
        return toHexString(getSHA256(src), 64);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static String getSHA256String(InputStream input) {
        return toHexString(getSHA256(input), 64);
    }

    /**
     * 获取SHA-384
     *
     * @param src 数据源
     * @return SHA-384
     */
    public static String getSHA384String(byte[] src) {
        return toHexString(getSHA384(src), 96);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static String getSHA384String(InputStream input) {
        return toHexString(getSHA384(input), 96);
    }

    /**
     * 获取SHA-512
     *
     * @param src 数据源
     * @return SHA-512
     */
    public static String getSHA512String(byte[] src) {
        return toHexString(getSHA512(src), 128);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static String getSHA512String(InputStream input) {
        return toHexString(getSHA512(input), 128);
    }
}
