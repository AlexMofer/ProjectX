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
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import am.project.support.utils.ByteUtils;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageDigestUtils {
    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static byte[] getMessageDigest(byte[] input, String algorithm) {
        if (input == null)
            return null;
        try {
            return MessageDigest.getInstance(algorithm).digest(input);
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
    public static String getMessageDigest(byte[] input, String algorithm, int minLength) {
        return ByteUtils.toHexString(getMessageDigest(input, algorithm), minLength);
    }

    /**
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static byte[] getMessageDigest(ByteBuffer input, String algorithm) {
        if (input == null)
            return null;
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
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
    public static String getMessageDigest(ByteBuffer input, String algorithm, int minLength) {
        return ByteUtils.toHexString(getMessageDigest(input, algorithm), minLength);
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
     * 获取信息摘要
     *
     * @param input     数据源
     * @param algorithm 算法
     * @param minLength 16进制字节最小长度
     * @return 信息摘要
     */
    public static String getMessageDigest(InputStream input, String algorithm, int minLength) {
        return ByteUtils.toHexString(getMessageDigest(input, algorithm), minLength);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static byte[] getMD5(byte[] input) {
        return getMessageDigest(input, "MD5");
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static byte[] getMD5(ByteBuffer input) {
        return getMessageDigest(input, "MD5");
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
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    public static byte[] getMD5(String input) {
        return getMD5(input.getBytes());
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static String getMD5String(byte[] input) {
        return getMessageDigest(input, "MD5", 0);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static String getMD5String(ByteBuffer input) {
        return getMessageDigest(input, "MD5", 0);
    }

    /**
     * 获取MD5
     *
     * @param input 数据源
     * @return MD5
     */
    public static String getMD5String(InputStream input) {
        return getMessageDigest(input, "MD5", 0);
    }

    /**
     * 获取字符MD5
     *
     * @param input 字符串
     * @return MD5
     */
    public static String getMD5String(String input) {
        return getMD5String(input.getBytes());
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static byte[] getSHA1(byte[] input) {
        return getMessageDigest(input, "SHA-1");
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static byte[] getSHA1(ByteBuffer input) {
        return getMessageDigest(input, "SHA-1");
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
     * 获取SHA-1
     *
     * @param input 字符串
     * @return SHA-1
     */
    public static byte[] getSHA1(String input) {
        return getSHA1(input.getBytes());
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static String getSHA1String(byte[] input) {
        return getMessageDigest(input, "SHA-1", 0);
    }


    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static String getSHA1String(ByteBuffer input) {
        return getMessageDigest(input, "SHA-1", 0);
    }

    /**
     * 获取SHA-1
     *
     * @param input 数据源
     * @return SHA-1
     */
    public static String getSHA1String(InputStream input) {
        return getMessageDigest(input, "SHA-1", 0);
    }

    /**
     * 获取SHA-1
     *
     * @param input 字符串
     * @return SHA-1
     */
    public static String getSHA1String(String input) {
        return getSHA1String(input.getBytes());
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    public static byte[] getSHA224(byte[] input) {
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
    public static byte[] getSHA224(ByteBuffer input) {
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
    public static byte[] getSHA224(InputStream input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224");
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 字符串
     * @return SHA-224
     */
    public static byte[] getSHA224(String input) {
        return getSHA224(input.getBytes());
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 数据源
     * @return SHA-224
     */
    @SuppressLint("ObsoleteSdkInt")
    public static String getSHA224String(byte[] input) {
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
    public static String getSHA224String(ByteBuffer input) {
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
    public static String getSHA224String(InputStream input) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(input, "SHA-224", 0);
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param input 字符串
     * @return SHA-224
     */
    public static String getSHA224String(String input) {
        return getSHA224String(input.getBytes());
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static byte[] getSHA256(byte[] input) {
        return getMessageDigest(input, "SHA-256");
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static byte[] getSHA256(ByteBuffer input) {
        return getMessageDigest(input, "SHA-256");
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
     * 获取SHA-256
     *
     * @param input 字符串
     * @return SHA-256
     */
    public static byte[] getSHA256(String input) {
        return getSHA256(input.getBytes());
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static String getSHA256String(byte[] input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static String getSHA256String(ByteBuffer input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 数据源
     * @return SHA-256
     */
    public static String getSHA256String(InputStream input) {
        return getMessageDigest(input, "SHA-256", 0);
    }

    /**
     * 获取SHA-256
     *
     * @param input 字符串
     * @return SHA-256
     */
    public static String getSHA256String(String input) {
        return getSHA256String(input.getBytes());
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static byte[] getSHA384(byte[] input) {
        return getMessageDigest(input, "SHA-384");
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static byte[] getSHA384(ByteBuffer input) {
        return getMessageDigest(input, "SHA-384");
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
     * 获取SHA-384
     *
     * @param input 字符串
     * @return SHA-384
     */
    public static byte[] getSHA384(String input) {
        return getSHA384(input.getBytes());
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static String getSHA384String(byte[] input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static String getSHA384String(ByteBuffer input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 数据源
     * @return SHA-384
     */
    public static String getSHA384String(InputStream input) {
        return getMessageDigest(input, "SHA-384", 0);
    }

    /**
     * 获取SHA-384
     *
     * @param input 字符串
     * @return SHA-384
     */
    public static String getSHA384String(String input) {
        return getSHA384String(input.getBytes());
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static byte[] getSHA512(byte[] input) {
        return getMessageDigest(input, "SHA-512");
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static byte[] getSHA512(ByteBuffer input) {
        return getMessageDigest(input, "SHA-512");
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

    /**
     * 获取SHA-512
     *
     * @param input 字符串
     * @return SHA-512
     */
    public static byte[] getSHA512(String input) {
        return getSHA512(input.getBytes());
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static String getSHA512String(byte[] input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static String getSHA512String(ByteBuffer input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 数据源
     * @return SHA-512
     */
    public static String getSHA512String(InputStream input) {
        return getMessageDigest(input, "SHA-512", 0);
    }

    /**
     * 获取SHA-512
     *
     * @param input 字符串
     * @return SHA-512
     */
    public static String getSHA512String(String input) {
        return getSHA512String(input.getBytes());
    }
}
