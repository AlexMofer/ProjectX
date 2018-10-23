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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 字体
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings("WeakerAccess")
class Font {
    public static final int STYLE_NORMAL = 0;// 常规
    public static final int STYLE_ITALIC = 1;// 斜体

    private final String mName;// 文件名，ttf、ttc、otf等类型的字体文件
    private final int mWeight;// 默认值为400，常规字体
    private final int mStyle;// 样式，常规或斜体
    private int mIndex = -1;// 字体集中的角标，该参数仅对ttc文件有效
    private final HashMap<String, Axis> mAxises = new HashMap<>();// 对称信息，可能为空
    private String mFallbackFor;// 作为备选字体，值为系统字体族名称（仅备选字体族中有效）

    Font(String name, int weight, int style) {
        mName = name;
        mWeight = weight;
        mStyle = style;
    }

    int getWeight() {
        return mWeight;
    }

    void setIndex(int index) {
        mIndex = index;
    }

    void putAxis(Axis axis) {
        if (axis == null)
            return;
        final String tag = axis.getTag();
        if (TextUtils.isEmpty(tag))
            return;
        mAxises.put(tag, axis);
    }

    void setFallbackFor(String fallbackFor) {
        mFallbackFor = fallbackFor;
    }

    String getFallbackFor() {
        return mFallbackFor;
    }

    TypefaceItem convert() {
        final int size = mAxises.size();
        final HashMap<String, TypefaceAxis> axises;
        if (size <= 0)
            axises = null;
        else {
            axises = new HashMap<>(size);
            final Set<Map.Entry<String, Axis>> entries = mAxises.entrySet();
            for (Map.Entry<String, Axis> entry : entries) {
                final TypefaceAxis axis = entry.getValue().convert();
                axises.put(axis.getTag(), axis);
            }
        }
        final int style = mStyle == STYLE_ITALIC ?
                TypefaceItem.STYLE_ITALIC : TypefaceItem.STYLE_NORMAL;
        return new TypefaceItem(mName, mWeight, style, mIndex, axises);
    }
}
