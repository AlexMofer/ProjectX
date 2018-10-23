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

import org.xmlpull.v1.XmlPullParser;

/**
 * API 26
 * 注释增加警告，提示第三方程序解析该文件存在较大风险，重大Android版本更新时，该文件会出现较大改动（数据或/和结构）。
 * font标签增加子标签axis
 * <axis tag="wdth" stylevalue="100.0" />
 * fallback 字体限定仅一个无语言参数，其他均包含语言参数
 * Created by Alex on 2018/8/31.
 */
@SuppressWarnings("WeakerAccess")
class FontsReaderApi26 extends FontsReaderApi24 {

    protected static final String NAME_AXIS = "axis";
    protected static final String ATTR_TAG = "tag";
    protected static final String ATTR_STYLEVALUE = "stylevalue";
    protected Axis mAxis;

    @Override
    protected boolean startTag(XmlPullParser parser) {
        if (super.startTag(parser))
            return true;
        final String name = parser.getName();
        switch (name) {
            case NAME_AXIS:
                startAxis(parser);
                return true;
        }
        return false;
    }

    protected void startAxis(XmlPullParser parser) {
        // axis
        final String tag = parser.getAttributeValue(null, ATTR_TAG);
        if (TextUtils.isEmpty(tag))
            return;
        float value;
        try {
            value = Float.parseFloat(
                    parser.getAttributeValue(null, ATTR_STYLEVALUE));
        } catch (Exception e) {
            return;
        }
        if (Axis.TAG_ITAL.equals(tag) || Axis.TAG_OPSZ.equals(tag) ||
                Axis.TAG_SLNT.equals(tag) || Axis.TAG_WDTH.equals(tag) ||
                Axis.TAG_WGHT.equals(tag))
            mAxis = new Axis(tag, value);
    }

    @Override
    protected boolean endTag(XmlPullParser parser) {
        if (super.endTag(parser))
            return true;
        final String name = parser.getName();
        switch (name) {
            case NAME_AXIS:
                endAxis();
                return true;
        }
        return false;
    }

    protected void endAxis() {
        // axis
        if (mFont != null)
            mFont.putAxis(mAxis);
        mAxis = null;
    }
}

