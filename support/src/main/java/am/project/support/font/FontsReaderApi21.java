package am.project.support.font;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

/**
 * API 21
 * 大改版
 * 新增fonts.xml配置文件
 * 暂时保留system_fonts.xml及fallback_fonts.xml配置文件，但系统已不再读取使用
 * 所有Fallback字体均可在Windows下正常打开
 * 结构完全改变，参照：SDK\platforms\android-21\data\fonts\fonts.xml
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi21 extends FontsReaderApi20 {

    @SuppressWarnings("all")
    protected static final String NAME_ALIAS = "alias";
    @SuppressWarnings("all")
    protected static final String ATTR_VERSION = "version";
    @SuppressWarnings("all")
    protected static final String ATTR_NAME = "name";
    @SuppressWarnings("all")
    protected static final String ATTR_TO = "to";
    @SuppressWarnings("all")
    protected static final String ATTR_WEIGHT = "weight";
    @SuppressWarnings("all")
    protected static final String ATTR_STYLE = "style";
    @SuppressWarnings("all")
    protected Alias mAlias;
    @SuppressWarnings("all")
    protected Font mFont;

    @Override
    public FamilySet readConfig() {
        final File config = new File(getConfigDir(), "fonts.xml");
        if (!config.exists() || !config.isFile() || !config.canRead())
            return null;
        final Reader reader;
        try {
            reader = new FileReader(config);
        } catch (Exception e) {
            return null;
        }
        readConfig(reader);
        try {
            reader.close();
        } catch (Exception e) {
            // ignore
        }
        return mSet;
    }

    @Override
    protected boolean startTag(XmlPullParser parser) {
        final String name = parser.getName();
        switch (name) {
            case NAME_FAMILYSET:
                startFamilySet(parser);
                return true;
            case NAME_FAMILY:
                startFamily(parser);
                return true;
            case NAME_ALIAS:
                startAlias(parser);
                return true;
            case NAME_FONT:
                startFont(parser);
                return true;
        }
        return false;
    }

    @Override
    protected void startFamilySet(XmlPullParser parser) {
        // familyset
        final String version = parser.getAttributeValue(null, ATTR_VERSION);
        mSet = new FamilySet(version);
    }

    @Override
    protected void startFamily(XmlPullParser parser) {
        // family
        final String name = parser.getAttributeValue(null, ATTR_NAME);
        if (TextUtils.isEmpty(name)) {
            // fallback font
            mFallback = new Fallback();
            mFallback.setLang(parser.getAttributeValue(null, ATTR_LANG));
            mFallback.setVariant(parser.getAttributeValue(null, ATTR_VARIANT));
        } else {
            // system font
            mFamily = new Family(name);
        }
    }

    @SuppressWarnings("all")
    protected void startAlias(XmlPullParser parser) {
        // alias
        final String name = parser.getAttributeValue(null, ATTR_NAME);
        if (TextUtils.isEmpty(name))
            return;
        final String to = parser.getAttributeValue(null, ATTR_TO);
        if (TextUtils.isEmpty(to))
            return;
        int weight;
        try {
            weight = Integer.parseInt(parser.getAttributeValue(null, ATTR_WEIGHT));
        } catch (Exception e) {
            weight = -1;
        }
        mAlias = new Alias(name, to, weight);
    }

    @Override
    protected void startFont(XmlPullParser parser) {
        // font
        final int weight;
        try {
            weight = Integer.parseInt(parser.getAttributeValue(null, ATTR_WEIGHT));
        } catch (Exception e) {
            return;
        }
        try {
            if (parser.next() != XmlPullParser.TEXT)
                return;
        } catch (Exception e) {
            return;
        }
        final String name = parser.getText();
        if (TextUtils.isEmpty(name))
            return;
        final int style = "italic".equals(parser.getAttributeValue(null, ATTR_STYLE))
                ? Font.STYLE_ITALIC : Font.STYLE_NORMAL;
        mFont = new Font(name, weight, style);
    }

    @Override
    protected boolean endTag(XmlPullParser parser) {
        final String name = parser.getName();
        switch (name) {
            case NAME_FAMILYSET:
                endFamilySet();
                return true;
            case NAME_FAMILY:
                endFamily();
                return true;
            case NAME_ALIAS:
                endAlias();
                return true;
            case NAME_FONT:
                endFont();
                return true;
        }
        return false;
    }

    @SuppressWarnings("all")
    protected void endAlias() {
        // alias
        if (mSet != null) {
            mSet.putAlias(mAlias);
        }
        mAlias = null;
    }

    @Override
    protected void endFont() {
        // font
        if (mFamily != null) {
            mFamily.addFont(mFont);
            mFont = null;
            return;
        }
        if (mFallback != null) {
            mFallback.addFont(mFont);
            mFont = null;
            return;
        }
        mFont = null;
    }
}
