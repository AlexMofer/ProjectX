package com.am.utils;

import android.graphics.BitmapFactory;

/**
 * 工具
 * Created by Alex on 2016/1/23.
 */
public class Tools {

    /**
     * 判断是否为图片
     *
     * @param filePath 文件路径
     * @return 是否为图片
     */
    public static boolean isBitmapFile(String filePath) {
        return isBitmapFile(filePath, null);
    }

    /**
     * 判断是否为图片
     *
     * @param filePath 文件路径
     * @return 是否为图片
     */
    public static boolean isBitmapFile(String filePath, int[] size) {
        int width;
        int height;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            width = options.outWidth;
            height = options.outHeight;
        } catch (OutOfMemoryError e) {
            return false;
        }
        if (size != null && size.length >= 2) {
            size[0] = width;
            size[1] = height;
        }
        return width > 0 && height > 0;
    }
}
