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

/**
 * 字体对称轴
 * Created by Alex on 2018/8/30.
 */
class Axis {
    public static final String TAG_ITAL = "ital";// Italic
    public static final String TAG_OPSZ = "opsz";// Optical size
    public static final String TAG_SLNT = "slnt";// Slant
    public static final String TAG_WDTH = "wdth";// Width
    public static final String TAG_WGHT = "wght";// Weight
    private final String mTag;// 标签
    private final float mStyleValue;// 值

    Axis(String tag, float value) {
        mTag = tag;
        mStyleValue = value;
    }

    String getTag() {
        return mTag;
    }

    TypefaceAxis convert() {
        return new TypefaceAxis(mTag, mStyleValue);
    }
}
