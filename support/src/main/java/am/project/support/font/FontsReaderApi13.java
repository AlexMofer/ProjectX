package am.project.support.font;

import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * API 13
 * Created by Alex on 2018/8/31.
 */
class FontsReaderApi13 implements FontsReader {

    protected static final String NAME_FONTS = "fonts";
    protected static final String NAME_FONT = "font";
    protected static final String NAME_NAME = "name";
    protected static final String NAME_FALLBACK = "fallback";
    protected static final String ATTR_TTF = "ttf";
    protected FamilySet mSet;
    private ArrayList<Font> mFonts = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();

    @Override
    public String getConfigDir() {
        return DIR_CONFIG;
    }

    @Override
    public String getFontsDir() {
        return DIR_FONTS;
    }

    private Reader getReader() {
//        final String xml = "";
        final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- Copyright (C) 2008 The Android Open Source Project\n" +
                "\n" +
                "     Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "     you may not use this file except in compliance with the License.\n" +
                "     You may obtain a copy of the License at\n" +
                "  \n" +
                "          http://www.apache.org/licenses/LICENSE-2.0\n" +
                "  \n" +
                "     Unless required by applicable law or agreed to in writing, software\n" +
                "     distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "     See the License for the specific language governing permissions and\n" +
                "     limitations under the License.\n" +
                "-->\n" +
                "<!--\n" +
                "\tThis is only used by the layoutlib to display\n" +
                "\tlayouts in ADT.\n" +
                "-->\n" +
                "<fonts>\n" +
                "    <font ttf=\"DroidSans\">\n" +
                "        <name>sans-serif</name>\n" +
                "        <name>arial</name>\n" +
                "        <name>helvetica</name>\n" +
                "        <name>tahoma</name>\n" +
                "        <name>verdana</name>\n" +
                "    </font>\n" +
                "   <font ttf=\"DroidSerif\">\n" +
                "        <name>serif</name>\n" +
                "        <name>times</name>\n" +
                "        <name>times new roman</name>\n" +
                "        <name>palatino</name>\n" +
                "        <name>georgia</name>\n" +
                "        <name>baskerville</name>\n" +
                "        <name>goudy</name>\n" +
                "        <name>fantasy</name>\n" +
                "        <name>cursive</name>\n" +
                "        <name>ITC Stone Serif</name>\n" +
                "    </font>\n" +
                "    <font ttf=\"DroidSansMono\">\n" +
                "        <name>monospace</name>\n" +
                "        <name>courier</name>\n" +
                "        <name>courier new</name>\n" +
                "        <name>monaco</name>\n" +
                "    </font>\n" +
                "    <fallback ttf=\"DroidSansFallback\" />\n" +
                "    <fallback ttf=\"MTLmr3m\" />\n" +
                "</fonts>";
        return new StringReader(xml);
    }

    @Override
    public FamilySet readConfig() {
//        final File config = new File(getConfigDir(), "fonts.xml");
//        if (!config.exists() || !config.isFile() || !config.canRead())
//            return null;
//        final Reader reader;
//        try {
//            reader = new FileReader(config);
//        } catch (Exception e) {
//            return null;
//        }
        final Reader reader = getReader();// TODO
        readConfig(reader);
        try {
            reader.close();
        } catch (Exception e) {
            // ignore
        }
        return mSet;
    }

    protected void readConfig(Reader reader) {
        final XmlPullParser parser;
        try {
            parser = Xml.newPullParser();
            parser.setInput(reader);
        } catch (Exception e) {
            return;
        }
        try {
            int event = parser.getEventType();
            while (true) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        return;
                    case XmlPullParser.START_TAG:
                        startTag(parser);
                        break;
                    case XmlPullParser.END_TAG:
                        endTag(parser);
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    protected boolean startTag(XmlPullParser parser) {
        final String name = parser.getName();
        switch (name) {
            case NAME_FONTS:
                startFonts(parser);
                return true;
            case NAME_FONT:
                startFont(parser);
                return true;
            case NAME_NAME:
                startName(parser);
                return true;
            case NAME_FALLBACK:
                startFallback(parser);
                return true;
        }
        return false;
    }

    protected void startFonts(XmlPullParser parser) {
        mSet = new FamilySet(null);
    }

    protected void startFont(XmlPullParser parser) {
        if (mSet == null)
            return;
        final String ttf = parser.getAttributeValue(null, ATTR_TTF);
        mFonts.clear();
        // TODO 通过ttf搜集字体文件
    }

    protected void startName(XmlPullParser parser) {
        try {
            if (parser.next() != XmlPullParser.TEXT)
                return;
        } catch (Exception e) {
            return;
        }
        mNames.add(parser.getText());
    }

    protected void startFallback(XmlPullParser parser) {
        if (mSet == null)
            return;
        final String ttf = parser.getAttributeValue(null, ATTR_TTF);
        // TODO 通过ttf搜集字体文件
    }

    protected boolean endTag(XmlPullParser parser) {
        final String name = parser.getName();
        switch (name) {
            case NAME_FONTS:
                endFonts();
                return true;
            case NAME_FONT:
                endFont();
                return true;
            case NAME_NAME:
                // do nothing
                return true;
            case NAME_FALLBACK:
                // do nothing
                return true;
        }
        return false;
    }

    protected void endFonts() {
        if (mSet != null && mSet.isAvailable())
            return;
        mSet = null;
    }

    protected void endFont() {
        if (mSet == null || mNames.isEmpty() || mFonts.isEmpty())
            return;
        final String name = mNames.get(0);
        final Family family = new Family(name);
        for (Font font : mFonts) {
            family.addFont(font);
        }
        mSet.putFamily(family);
        final int count = mNames.size();
        for (int i = 1; i < count; i++) {
            mSet.putAlias(new Alias(mNames.get(i), name, -1));
        }
    }
}
