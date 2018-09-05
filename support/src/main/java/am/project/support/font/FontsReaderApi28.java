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

import org.xmlpull.v1.XmlPullParser;

/**
 * API 28
 * 版本增加到23
 * font标签增加fallbackFor字段
 * Created by Alex on 2018/8/30.
 */
class FontsReaderApi28 extends FontsReaderApi26 {

    @SuppressWarnings("all")
    protected static final String ATTR_FALLBACKFOR = "fallbackFor";

    @Override
    protected void startFont(XmlPullParser parser) {
        super.startFont(parser);
        if (mFont != null)
            mFont.setFallbackFor(parser.getAttributeValue(null, ATTR_FALLBACKFOR));
    }
}
