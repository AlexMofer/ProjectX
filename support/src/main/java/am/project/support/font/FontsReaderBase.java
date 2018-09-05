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

import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * API 9
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings("all")
class FontsReaderBase implements FontsReader {

    protected static final String NAME_FONTS = "fonts";
    protected static final String NAME_FONT = "font";
    protected static final String NAME_NAME = "name";
    protected static final String NAME_FALLBACK = "fallback";
    protected static final String ATTR_TTF = "ttf";
    protected FamilySet mSet;
    private String mTTF;
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
        mTTF = parser.getAttributeValue(null, ATTR_TTF);
        mNames.clear();
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
        if (TextUtils.isEmpty(ttf))
            return;
        final Fallback fallback = new Fallback();
        fallback.addFont(getFont(ttf + "-Thin.ttf", 100, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-ThinItalic.ttf", 100, Font.STYLE_ITALIC));
        fallback.addFont(getFont(ttf + "-Light.ttf", 300, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-LightItalic.ttf", 300, Font.STYLE_ITALIC));
        fallback.addFont(getFont(ttf + ".ttf", 400, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-Regular.ttf", 400, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-Italic.ttf", 400, Font.STYLE_ITALIC));
        fallback.addFont(getFont(ttf + "-Medium.ttf", 500, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-MediumItalic.ttf", 500, Font.STYLE_ITALIC));
        fallback.addFont(getFont(ttf + "-Black.ttf", 900, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-BlackItalic.ttf", 900, Font.STYLE_ITALIC));
        fallback.addFont(getFont(ttf + "-Bold.ttf", 700, Font.STYLE_NORMAL));
        fallback.addFont(getFont(ttf + "-BoldItalic.ttf", 700, Font.STYLE_ITALIC));
        mSet.putFallback(fallback);
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
        if (mSet == null || mNames.isEmpty() || TextUtils.isEmpty(mTTF)) {
            mNames.clear();
            mTTF = null;
            return;
        }
        final String to = mNames.get(0);
        final Family family = new Family(to);
        family.addFont(getFont(mTTF + "-Thin.ttf", 100, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-ThinItalic.ttf", 100, Font.STYLE_ITALIC));
        family.addFont(getFont(mTTF + "-Light.ttf", 300, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-LightItalic.ttf", 300, Font.STYLE_ITALIC));
        family.addFont(getFont(mTTF + ".ttf", 400, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-Regular.ttf", 400, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-Italic.ttf", 400, Font.STYLE_ITALIC));
        family.addFont(getFont(mTTF + "-Medium.ttf", 500, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-MediumItalic.ttf", 500, Font.STYLE_ITALIC));
        family.addFont(getFont(mTTF + "-Black.ttf", 900, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-BlackItalic.ttf", 900, Font.STYLE_ITALIC));
        family.addFont(getFont(mTTF + "-Bold.ttf", 700, Font.STYLE_NORMAL));
        family.addFont(getFont(mTTF + "-BoldItalic.ttf", 700, Font.STYLE_ITALIC));
        if (mSet.putFamily(family)) {
            final int count = mNames.size();
            for (int i = 1; i < count; i++) {
                mSet.putAlias(new Alias(mNames.get(i), to, -1));
            }
        }
        mTTF = null;
    }

    private Font getFont(String name, int weight, int style) {
        final File font = new File(getFontsDir(), name);
        if (font.exists() && font.isFile())
            return new Font(name, weight, style);
        return null;
    }
}
