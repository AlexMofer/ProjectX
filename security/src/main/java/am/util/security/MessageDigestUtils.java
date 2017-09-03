package am.util.security;

import android.os.Build;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
@SuppressWarnings("all")
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
     * 获取MD5
     *
     * @param src 数据源
     * @return MD5
     */
    public static byte[] getMD5(byte[] src) {
        return getMessageDigest(src, "MD5");
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
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param src 数据源
     * @return SHA-224
     */
    public static byte[] getSHA224(byte[] src) {
        if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
            throw new UnsupportedOperationException("SHA-224 Supported API Levels 1-8,22+");
        }
        return getMessageDigest(src, "SHA-224");
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
     * 获取SHA-384
     *
     * @param src 数据源
     * @return SHA-384
     */
    public static byte[] getSHA384(byte[] src) {
        return getMessageDigest(src, "SHA-384");
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
     * 获取MD5
     *
     * @param src    数据源
     * @param length 位数（16位或32位）
     * @return MD5
     */
    public static String getMD5String(byte[] src, int length) {
        final String md5 = new BigInteger(1, getMD5(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (md5.length() < 32) {
            final int number = 32 - md5.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(md5);
        if (length == 16) {
            return builder.toString().substring(8, 24);
        }
        return builder.toString();
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
        final String sha1 = new BigInteger(1, getSHA1(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (sha1.length() < 40) {
            final int number = 40 - sha1.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(sha1);
        return builder.toString();
    }

    /**
     * 获取SHA-224
     * 仅支持API 1-8,22+
     *
     * @param src 数据源
     * @return SHA-224
     */
    public static String getSHA224String(byte[] src) {
        final String sha224 = new BigInteger(1, getSHA224(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (sha224.length() < 56) {
            final int number = 56 - sha224.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(sha224);
        return builder.toString();
    }

    /**
     * 获取SHA-256
     *
     * @param src 数据源
     * @return SHA-256
     */
    public static String getSHA256String(byte[] src) {
        final String sha256 = new BigInteger(1, getSHA256(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (sha256.length() < 64) {
            final int number = 64 - sha256.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(sha256);
        return builder.toString();
    }

    /**
     * 获取SHA-384
     *
     * @param src 数据源
     * @return SHA-384
     */
    public static String getSHA384String(byte[] src) {
        final String sha384 = new BigInteger(1, getSHA384(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (sha384.length() < 96) {
            final int number = 96 - sha384.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(sha384);
        return builder.toString();
    }

    /**
     * 获取SHA-512
     *
     * @param src 数据源
     * @return SHA-512
     */
    public static String getSHA512String(byte[] src) {
        final String sha512 = new BigInteger(1, getSHA512(src)).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (sha512.length() < 128) {
            final int number = 128 - sha512.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(sha512);
        return builder.toString();
    }
}
