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
package am.util.font;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 备选字体族
 * Created by Alex on 2018/8/30.
 */
class Fallback {
    private final ArrayList<Font> mFonts = new ArrayList<>();// 字体
    private String mLang;// 语言，可能为空，带空格表示多种语言
    private String mVariant;// 变种，可能为空
    private final HashSet<String> mFallbackForSet = new HashSet<>();// 筛选
    private final ArrayList<Font> mFallbackForFonts = new ArrayList<>();// 字体

    Fallback() {
    }

    boolean isAvailable() {
        return mFonts.size() > 0;
    }

    void setLang(String lang) {
        mLang = lang;
    }

    void setVariant(String variant) {
        mVariant = variant;
    }

    void addFont(Font font) {
        if (font == null)
            return;
        final String fallbackFor = font.getFallbackFor();
        if (fallbackFor == null)
            mFonts.add(font);
        else {
            mFallbackForSet.add(fallbackFor);
            mFallbackForFonts.add(font);
        }
    }

    TypefaceFallback convert(String name) {
        final ArrayList<TypefaceItem> items = new ArrayList<>();
        final boolean capture = !mFallbackForSet.isEmpty() && mFallbackForSet.contains(name);
        if (capture) {
            for (Font font : mFallbackForFonts) {
                if (TextUtils.equals(font.getFallbackFor(), name))
                    items.add(font.convert());
            }
        } else {
            for (Font font : mFonts) {
                items.add(font.convert());
            }
        }
        if (items.isEmpty())
            return null;
        return new TypefaceFallback(mLang, mVariant, items);
    }
}
