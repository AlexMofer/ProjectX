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

import java.util.ArrayList;

/**
 * 字体族
 * Created by Alex on 2018/8/30.
 */
class Family {
    private final String mName;// 名称
    private final ArrayList<Font> mFonts = new ArrayList<>();// 字体

    Family(String name) {
        mName = name;
    }

    boolean isAvailable() {
        return !TextUtils.isEmpty(mName) && mFonts.size() > 0;
    }

    void addFont(Font font) {
        if (font == null)
            return;
        mFonts.add(font);
    }

    String getName() {
        return mName;
    }

    ArrayList<TypefaceItem> convert(int weight) {
        final ArrayList<TypefaceItem> items = new ArrayList<>();
        for (Font font : mFonts) {
            if (weight == -1)
                items.add(font.convert());
            else {
                if (weight == font.getWeight())
                    items.add(font.convert());
            }
        }
        return items.isEmpty() ? null : items;
    }
}
