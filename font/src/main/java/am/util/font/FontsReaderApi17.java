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

import org.xmlpull.v1.XmlPullParser;

/**
 * API 17
 * 在API 16 中增加以fallback_fonts文件名加语言后缀的方式，如：fallback_fonts-ja.xml，其无较高读取价值，因此忽略。
 * 备选字体中file标签加入"lang"属性及"variant"属性"variant"
 * 去除以fallback_fonts文件名加语言后缀的方式，因此仅留下单独一个fallback_fonts.xml文件
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi17 extends FontsReaderApi14 {

    @SuppressWarnings("all")
    protected static final String ATTR_LANG = "lang";
    @SuppressWarnings("all")
    protected static final String ATTR_VARIANT = "variant";

    @Override
    protected void startFile(XmlPullParser parser) {
        super.startFile(parser);
        if (mFallback != null) {
            mFallback.setLang(parser.getAttributeValue(null, ATTR_LANG));
            mFallback.setVariant(parser.getAttributeValue(null, ATTR_VARIANT));
        }
    }
}
