package am.project.support.font;

import org.xmlpull.v1.XmlPullParser;

/**
 * API 17
 * 备选字体中file标签加入"lang"属性及"variant"属性"variant"
 * 去除以fallback_fonts文件名加语言后缀的方式，因此仅留下单独一个fallback_fonts.xml文件
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi17 extends FontsReaderApi16 {

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
