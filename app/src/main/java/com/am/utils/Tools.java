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
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            return options.outHeight > 0 && options.outWidth > 0;
        } catch (OutOfMemoryError e) {
            return false;
        }
    }
}
