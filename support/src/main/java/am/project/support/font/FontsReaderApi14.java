package am.project.support.font;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * API 14
 * 大改版
 * 新增system_fonts.xml与fallback_fonts.xml配置文件
 * 结构完全改变，参照：SDK\platforms\android-14\data\fonts\system_fonts.xml
 * 增加大量字体，更易于解析及匹配
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi14 extends FontsReaderBase {

    @SuppressWarnings("all")
    protected static final String NAME_FAMILYSET = "familyset";
    @SuppressWarnings("all")
    protected static final String NAME_FAMILY = "family";
    @SuppressWarnings("all")
    protected static final String NAME_NAMESET = "nameset";
    @SuppressWarnings("all")
    protected static final String NAME_FILESET = "fileset";
    @SuppressWarnings("all")
    protected static final String NAME_FILE = "file";
    @SuppressWarnings("all")
    protected Family mFamily;
    @SuppressWarnings("all")
    protected Fallback mFallback;
    @SuppressWarnings("all")
    protected boolean mIsFallback = false;
    private ArrayList<String> mNames = new ArrayList<>();

    @Override
    public String getConfigDir() {
        return DIR_CONFIG;
    }

    @Override
    public String getFontsDir() {
        return DIR_FONTS;
    }

    @Override
    public FamilySet readConfig() {
        final File systemFile = new File(getConfigDir(), "system_fonts.xml");
        final File fallbackFile = new File(getConfigDir(), "fallback_fonts.xml");
        if (!systemFile.exists() || !systemFile.isFile() || !systemFile.canRead() ||
                !fallbackFile.exists() || !fallbackFile.isFile() || !fallbackFile.canRead())
            return null;
        final Reader system;
        try {
            system = new FileReader(systemFile);
        } catch (Exception e) {
            return null;
        }
        mIsFallback = false;
        readConfig(system);
        try {
            system.close();
        } catch (Exception e) {
            // ignore
        }
        final Reader fallback;
        try {
            fallback = new FileReader(fallbackFile);
        } catch (Exception e) {
            return null;
        }
        mIsFallback = true;
        readConfig(fallback);
        try {
            fallback.close();
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
            case NAME_NAMESET:
                startNameSet();
                return true;
            case NAME_NAME:
                startName(parser);
                return true;
            case NAME_FILESET:
                // do nothing
                return true;
            case NAME_FILE:
                startFile(parser);
                return true;
        }
        return false;
    }

    protected void startFamilySet(XmlPullParser parser) {
        // familyset
        if (!mIsFallback)
            mSet = new FamilySet(null);
    }

    protected void startFamily(XmlPullParser parser) {
        // family
        if (mIsFallback) {
            // fallback font
            mFallback = new Fallback();
        }
    }

    @SuppressWarnings("all")
    protected void startNameSet() {
        mNames.clear();
    }

    @Override
    protected void startName(XmlPullParser parser) {
        try {
            if (parser.next() != XmlPullParser.TEXT)
                return;
        } catch (Exception e) {
            return;
        }
        mNames.add(parser.getText());
    }

    protected void startFile(XmlPullParser parser) {
        try {
            if (parser.next() != XmlPullParser.TEXT)
                return;
        } catch (Exception e) {
            return;
        }
        final String name = parser.getText();
        if (TextUtils.isEmpty(name))
            return;
        final int weight;
        final int style;
        if (name.contains("-BoldItalic.")) {
            weight = 700;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-Bold.")) {
            weight = 700;
            style = Font.STYLE_NORMAL;
        } else if (name.contains("-BlackItalic.")) {
            weight = 900;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-Black.")) {
            weight = 900;
            style = Font.STYLE_NORMAL;
        } else if (name.contains("-MediumItalic.")) {
            weight = 500;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-Medium.")) {
            weight = 500;
            style = Font.STYLE_NORMAL;
        } else if (name.contains("-Italic.")) {
            weight = 400;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-LightItalic.")) {
            weight = 300;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-Light.")) {
            weight = 300;
            style = Font.STYLE_NORMAL;
        } else if (name.contains("-ThinItalic.")) {
            weight = 100;
            style = Font.STYLE_ITALIC;
        } else if (name.contains("-Thin.")) {
            weight = 100;
            style = Font.STYLE_NORMAL;
        } else {
            weight = 400;
            style = Font.STYLE_NORMAL;
        }
        if (mFallback != null) {
            mFallback.addFont(new Font(name, weight, style));
            return;
        }
        if (mFamily != null)
            mFamily.addFont(new Font(name, weight, style));
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
            case NAME_NAMESET:
                endNameSet();
                return true;
            case NAME_NAME:
                // do nothing
                return true;
            case NAME_FILESET:
                // do nothing
                return true;
            case NAME_FILE:
                // do nothing
                return true;
        }
        return false;
    }

    @SuppressWarnings("all")
    protected void endFamilySet() {
        // familyset
        if (!mIsFallback)
            return;
        if (mSet != null && mSet.isAvailable())
            return;
        mSet = null;
    }

    @SuppressWarnings("all")
    protected void endFamily() {
        // family
        if (mSet != null) {
            if (mFamily != null) {
                mSet.putFamily(mFamily);
                final String to = mFamily.getName();
                final int count = mNames.size();
                for (int i = 1; i < count; i++) {
                    mSet.putAlias(new Alias(mNames.get(i), to, -1));
                }
            }
            mSet.putFallback(mFallback);
        }
        mFamily = null;
        mFallback = null;
    }

    @SuppressWarnings("all")
    protected void endNameSet() {
        if (mNames.isEmpty())
            return;
        mFamily = new Family(mNames.get(0));
    }
}
