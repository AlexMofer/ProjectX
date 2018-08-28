/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.support.font;

import android.text.TextUtils;

import org.apache.fop.FontFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 字体文件辅助器
 * Created by Alex on 2018/8/29.
 */
@SuppressWarnings("unused")
public class FontFileHelper {

    private static File SYSTEM_FONTS_DIR;

    private FontFileHelper() {
        //no instance
    }

    /**
     * 获取字体全名
     *
     * @param input 字体输入流
     * @return 字体全名
     */
    @SuppressWarnings("all")
    public static String getFullName(InputStream input) {
        if (input == null)
            return null;
        try {
            return FontFileReader.readTTF(input).getFullName();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字体全名
     *
     * @param file 字体文件
     * @return 字体全名
     */
    @SuppressWarnings("all")
    public static String getFullName(File file) {
        if (file == null)
            return null;
        final FileInputStream input;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            return null;
        }
        final String name = getFullName(input);
        try {
            input.close();
        } catch (Exception e) {
            // ignore
        }
        return name;
    }

    /**
     * 获取字体全名
     *
     * @param name 字体文件名
     * @return 字体全名
     */
    public static String getFullName(String name) {
        if (TextUtils.isEmpty(name))
            return null;
        if (!name.toLowerCase().endsWith(".ttf") && !name.toLowerCase().endsWith(".ttc"))
            return null;
        if (SYSTEM_FONTS_DIR == null)
            SYSTEM_FONTS_DIR = new File("system/fonts");
        if (!SYSTEM_FONTS_DIR.exists() || !SYSTEM_FONTS_DIR.canRead() ||
                !SYSTEM_FONTS_DIR.isDirectory())
            return null;
        return getFullName(new File(SYSTEM_FONTS_DIR, name));
    }
}
