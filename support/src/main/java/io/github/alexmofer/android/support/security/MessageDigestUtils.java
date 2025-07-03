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
package io.github.alexmofer.android.support.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageDigestUtils {

    private MessageDigestUtils() {
        //no instance
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    @Nullable
    public static byte[] getMessageDigest(@Nullable byte[] input, @NonNull String algorithm) {
        if (input == null) {
            return null;
        }
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 转换为16进制
     *
     * @param bytes     数据
     * @param minLength 16进制字符串长度
     * @return 16进制字符串
     */
    @Nullable
    public static String toHexString(@Nullable byte[] bytes, int minLength) {
        if (bytes == null) {
            return null;
        }
        final String str = new BigInteger(1, bytes).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (str.length() < minLength) {
            final int number = minLength - str.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(str);
        return builder.toString();
    }

    /**
     * 转换为16进制
     *
     * @param md        MessageDigest
     * @param minLength 16进制字符串长度
     * @return 16进制字符串
     */
    @Nullable
    public static String toHexString(MessageDigest md, int minLength) {
        return toHexString(md.digest(), minLength);
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @param minLength 16进制字节最小长度
     * @return 信息摘要
     */
    @Nullable
    public static String getMessageDigest(@Nullable byte[] input, @NonNull String algorithm,
                                          int minLength) {
        return toHexString(getMessageDigest(input, algorithm), minLength);
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    @Nullable
    public static byte[] getMessageDigest(@Nullable ByteBuffer input, @NonNull String algorithm) {
        if (input == null) {
            return null;
        }
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(input);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @param minLength 16进制字节最小长度
     * @return 信息摘要
     */
    @Nullable
    public static String getMessageDigest(@Nullable ByteBuffer input, @NonNull String algorithm,
                                          int minLength) {
        return toHexString(getMessageDigest(input, algorithm), minLength);
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    @Nullable
    public static byte[] getMessageDigest(@Nullable InputStream input, @NonNull String algorithm) {
        if (input == null) {
            return null;
        }
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
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
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @param minLength 16进制字节最小长度
     * @return 信息摘要
     */
    @Nullable
    public static String getMessageDigest(@Nullable InputStream input, @NonNull String algorithm,
                                          int minLength) {
        return toHexString(getMessageDigest(input, algorithm), minLength);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable byte[] input) {
        return getMessageDigest(input, "MD5");
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "MD5");
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable InputStream input) {
        return getMessageDigest(input, "MD5");
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable String input) {
        if (input == null) {
            return null;
        }
        return getMD5(input.getBytes());
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable String input, @NonNull String charsetName) {
        if (input == null) {
            return null;
        }
        try {
            return getMD5(input.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static byte[] getMD5(@Nullable String input, @NonNull Charset charset) {
        if (input == null) {
            return null;
        }
        return getMD5(input.getBytes(charset));
    }

    /**
     * 获取MD5
     *
     * @param input     数据源
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable byte[] input, int minLength) {
        return getMessageDigest(input, "MD5", minLength);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable byte[] input) {
        return getMD5String(input, 0);
    }

    /**
     * 获取MD5
     *
     * @param input     数据源
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable ByteBuffer input, int minLength) {
        return getMessageDigest(input, "MD5", minLength);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable ByteBuffer input) {
        return getMD5String(input, 0);
    }

    /**
     * 获取MD5
     *
     * @param input     数据源
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable InputStream input, int minLength) {
        return getMessageDigest(input, "MD5", minLength);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable InputStream input) {
        return getMD5String(input, 0);
    }

    /**
     * 获取字符MD5
     *
     * @param input     字符串
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input, int minLength) {
        if (input == null) {
            return null;
        }
        return getMD5String(input.getBytes(), minLength);
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input) {
        return getMD5String(input, 0);
    }

    /**
     * 获取字符MD5
     *
     * @param input     字符串
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input, @NonNull String charsetName,
                                      int minLength) {
        if (input == null) {
            return null;
        }
        try {
            return getMD5String(input.getBytes(charsetName), minLength);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input, @NonNull String charsetName) {
        return getMD5String(input, charsetName, 0);
    }

    /**
     * 获取字符MD5
     *
     * @param input     字符串
     * @param minLength 最小长度
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input, @NonNull Charset charset,
                                      int minLength) {
        if (input == null) {
            return null;
        }
        return getMD5String(input.getBytes(charset), minLength);
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    @Nullable
    public static String getMD5String(@Nullable String input, @NonNull Charset charset) {
        return getMD5String(input, charset, 0);
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static byte[] getSHA1(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-1");
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static byte[] getSHA1(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-1");
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static byte[] getSHA1(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-1");
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static String getSHA1String(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-1", 0);
    }


    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static String getSHA1String(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-1", 0);
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    @Nullable
    public static String getSHA1String(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-1", 0);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static byte[] getSHA224(@Nullable byte[] input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static byte[] getSHA224(@Nullable ByteBuffer input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static byte[] getSHA224(@Nullable InputStream input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static String getSHA224String(@Nullable byte[] input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224", 0);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static String getSHA224String(@Nullable ByteBuffer input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224", 0);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public static String getSHA224String(@Nullable InputStream input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static byte[] getSHA256(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-256");
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static byte[] getSHA256(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-256");
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static byte[] getSHA256(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-256");
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static String getSHA256String(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static String getSHA256String(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    @Nullable
    public static String getSHA256String(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static byte[] getSHA384(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-384");
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static byte[] getSHA384(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-384");
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static byte[] getSHA384(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-384");
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static String getSHA384String(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static String getSHA384String(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    @Nullable
    public static String getSHA384String(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static byte[] getSHA512(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-512");
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static byte[] getSHA512(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-512");
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static byte[] getSHA512(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-512");
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static String getSHA512String(@Nullable byte[] input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static String getSHA512String(@Nullable ByteBuffer input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    @Nullable
    public static String getSHA512String(@Nullable InputStream input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取文件 MD5
     *
     * @param file      文件
     * @param minLength 最少长度，不足在前面补0
     * @return MD5
     */
    @Nullable
    public static String getMD5(@Nullable File file, int minLength) {
        if (file == null) {
            return null;
        }
        try (final FileInputStream input = new FileInputStream(file)) {
            return getMD5String(input, minLength);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件 MD5
     *
     * @param file 文件
     * @return MD5
     */
    @Nullable
    public static String getMD5(@Nullable File file) {
        return getMD5(file, 0);
    }

    /**
     * 获取文件 MD5
     *
     * @param context   Context
     * @param uri       Uri
     * @param minLength 最少长度，如：32，长度不够时最前面补0
     * @return MD5
     */
    @Nullable
    public static String getMD5(@NonNull Context context, @Nullable Uri uri, int minLength) {
        if (uri == null) {
            return null;
        }
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                return null;
            }
            return getMD5String(input, minLength);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件 MD5
     *
     * @param context Context
     * @param uri     Uri
     * @return MD5
     */
    @Nullable
    public static String getMD5(@NonNull Context context, @Nullable Uri uri) {
        return getMD5(context, uri, 0);
    }
}