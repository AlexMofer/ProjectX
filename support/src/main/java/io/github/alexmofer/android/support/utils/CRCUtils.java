package io.github.alexmofer.android.support.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/**
 * CRC工具
 * Created by Alex on 2024/6/5.
 */
public class CRCUtils {

    public static final long CRC_INVALID = 0;

    private CRCUtils() {
        //no instance
    }

    /**
     * 获取校验码
     *
     * @param input 输入
     * @param off   偏移
     * @param len   长度
     * @return 校验码
     */
    public static long getValue(@Nullable byte[] input, int off, int len) {
        if (input == null) {
            return CRC_INVALID;
        }
        Checksum cs = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                //noinspection Since15
                cs = new CRC32C();
            } catch (Throwable t) {
                // ignore
            }
        }
        if (cs == null) {
            cs = new CRC32();
        }
        cs.update(input, off, len);
        return cs.getValue();
    }

    /**
     * 获取校验码
     *
     * @param input 输入
     * @return 校验码
     */
    public static long getValue(@Nullable byte[] input) {
        return getValue(input, 0, input == null ? 0 : input.length);
    }

    /**
     * 获取校验码
     *
     * @param input 输入
     * @return 校验码
     */
    public static long getValue(@Nullable InputStream input) {
        if (input == null) {
            return CRC_INVALID;
        }
        Checksum cs = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                //noinspection Since15
                cs = new CRC32C();
            } catch (Throwable t) {
                // ignore
            }
        }
        if (cs == null) {
            cs = new CRC32();
        }
        final byte[] buffer = new byte[4096];
        int count;
        try {
            while ((count = input.read(buffer)) != -1) {
                if (count == 0) {
                    count = input.read();
                    if (count < 0)
                        break;
                    cs.update(count);
                    continue;
                }
                cs.update(buffer, 0, count);
            }
        } catch (Throwable t) {
            return CRC_INVALID;
        }
        return cs.getValue();
    }

    /**
     * 获取校验码
     *
     * @param input 输入
     * @return 校验码
     */
    public static long getValue(@Nullable String input) {
        if (input == null) {
            return getValue((byte[]) null);
        }
        return getValue(input.getBytes());
    }

    /**
     * 获取校验码
     *
     * @param input       输入
     * @param charsetName 编码方式
     * @return 校验码
     */
    public static long getValue(@Nullable String input, @NonNull String charsetName) {
        if (input == null) {
            return getValue((byte[]) null);
        }
        try {
            return getValue(input.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            return getValue((byte[]) null);
        }
    }

    /**
     * 获取校验码
     *
     * @param input   输入
     * @param charset 编码方式
     * @return 校验码
     */
    public static long getValue(@Nullable String input, @NonNull Charset charset) {
        if (input == null) {
            return getValue((byte[]) null);
        }
        return getValue(input.getBytes(charset));
    }

    /**
     * 获取校验码
     *
     * @param input 输入
     * @return 校验码
     */
    public static long getValue(@Nullable File input) {
        if (input == null) {
            return getValue((byte[]) null);
        }
        //noinspection IOStreamConstructor
        try (final InputStream is = new FileInputStream(input)) {
            return getValue(is);
        } catch (Throwable t) {
            return getValue((byte[]) null);
        }
    }

    /**
     * 获取校验码
     *
     * @param context Context
     * @param input   输入
     * @return 校验码
     */
    public static long getValue(@NonNull Context context, @Nullable Uri input) {
        if (input == null) {
            return getValue((byte[]) null);
        }
        try (final InputStream is = context.getContentResolver().openInputStream(input)) {
            return getValue(is);
        } catch (Exception e) {
            return getValue((byte[]) null);
        }
    }
}
