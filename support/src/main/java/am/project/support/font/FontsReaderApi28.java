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
