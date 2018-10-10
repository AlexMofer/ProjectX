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
 * API 24
 * 去除system_fonts.xml及fallback_fonts.xml配置文件
 * 提高fallback的family标签的lang字段的作用，可通过其匹配特定字符集的字体，无该字段的则为匹配不到的备选项，一般至少有一个，但不会很多（原先仅少数存在该属性）
 * font增加ttc文件支持，增加index属性，用于获取ttc角标
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi24 extends FontsReaderApi21 {

    @SuppressWarnings("all")
    protected static final String ATTR_INDEX = "index";

    @Override
    protected void startFont(XmlPullParser parser) {
        super.startFont(parser);
        if (mFont != null) {
            try {
                mFont.setIndex(Integer.parseInt(
                        parser.getAttributeValue(null, ATTR_INDEX)));
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
