/*
 * Copyright (C) 2017 AlexMofer
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

import android.os.Build;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 字体兼容器
 * Created by Alex on 2017/9/6.
 */
@SuppressWarnings("all")
public class AMFontCompat {

    private static final FontCompatImpl IMPL;
    private static final FontFamilySet DEFAULT;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new FontCompatApi21Impl();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new FontCompatApi14Impl();
        } else {
            IMPL = new FontCompatBaseImpl();
        }
        DEFAULT = new FontFamilySet((String) null);
        DEFAULT.getFamilies().add(new FontFamily("sans-serif"));
        DEFAULT.getFamilies().add(new FontFamily("serif"));
        DEFAULT.getFamilies().add(new FontFamily("monospace"));
    }

    private AMFontCompat() {
        //no instance
    }

    /**
     * 获取字体配置文件路径
     *
     * @return 字体配置文件路径
     */
    public static String getConfigFilePath() {
        return IMPL.getConfigFilePath();
    }

    /**
     * 获取字体信息
     *
     * @return 字体信息
     */
    public static FontFamilySet getFamilySet() {
        return IMPL.getFamilySet();
    }

    private interface FontCompatImpl {

        String getConfigFilePath();

        FontFamilySet getFamilySet();
    }

    private static class FontCompatBaseImpl implements FontCompatImpl {

        @Override
        public String getConfigFilePath() {
            return "/system/etc/fonts.xml";
        }

        @Override
        public FontFamilySet getFamilySet() {
            File configFilename = new File(getConfigFilePath());
            FontsBase fonts = null;
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(new FileReader(configFilename));
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            if (FontsBase.TAG.equals(parser.getName())) {
                                fonts = new FontsBase(parser);
                            }
                            break;
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                return DEFAULT;
            }
            if (fonts == null || fonts.fonts.isEmpty()) {
                return DEFAULT;
            }
            FontFamilySet familySet = new FontFamilySet((String) null);
            for (FontBase font : fonts.fonts) {
                for (String name : font.names) {
                    FontFamily family = new FontFamily(name);
                    family.getFonts().add(new Font(font.ttf + ".ttf"));
                    familySet.getFamilies().add(family);
                }
            }
            if (familySet.getFamilies().isEmpty())
                return DEFAULT;
            return familySet;
        }
    }

    private static class FontsBase {
        static final String TAG = "fonts";
        private final ArrayList<FontBase> fonts = new ArrayList<>();

        FontsBase(XmlPullParser parser) throws Exception {
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (FontBase.TAG.equals(parser.getName())) {
                            fonts.add(new FontBase(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class FontBase {
        static final String TAG = "font";
        private final ArrayList<String> names = new ArrayList<>();
        private String ttf;

        FontBase(XmlPullParser parser) throws Exception {
            ttf = parser.getAttributeValue(null, "ttf");
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("name".equals(parser.getName())) {
                            names.add(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class FontCompatApi14Impl extends FontCompatBaseImpl {

        @Override
        public String getConfigFilePath() {
            return "/system/etc/system_fonts.xml";
        }

        @Override
        public FontFamilySet getFamilySet() {
            File configFilename = new File(getConfigFilePath());
            FamilySetApi14 familySet = null;
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(new FileReader(configFilename));
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            if (FamilySetApi14.TAG.equals(parser.getName())) {
                                familySet = new FamilySetApi14(parser);
                            }
                            break;
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                return DEFAULT;
            }
            if (familySet == null || familySet.families.isEmpty()) {
                return DEFAULT;
            }
            FontFamilySet familySetCompat = new FontFamilySet((String) null);
            for (FamilyApi14 family : familySet.families) {
                for (String name : family.names) {
                    FontFamily familyCompat = new FontFamily(name);
                    for (String file : family.files) {
                        familyCompat.getFonts().add(new Font(file));
                    }
                    familySetCompat.getFamilies().add(familyCompat);
                }
            }
            if (familySetCompat.getFamilies().isEmpty())
                return DEFAULT;
            return familySetCompat;
        }
    }

    private static class FamilySetApi14 {
        static final String TAG = "familyset";
        private final ArrayList<FamilyApi14> families = new ArrayList<>();

        FamilySetApi14(XmlPullParser parser) throws Exception {
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (FamilyApi14.TAG.equals(parser.getName())) {
                            families.add(new FamilyApi14(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class FamilyApi14 {
        static final String TAG = "family";
        private final ArrayList<String> names = new ArrayList<>();
        private final ArrayList<String> files = new ArrayList<>();

        FamilyApi14(XmlPullParser parser) throws Exception {
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("name".equals(parser.getName())) {
                            names.add(parser.nextText());
                        } else if ("file".equals(parser.getName())) {
                            files.add(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class FontCompatApi21Impl extends FontCompatApi14Impl {

        @Override
        public String getConfigFilePath() {
            return "/system/etc/fonts.xml";
        }

        @Override
        public FontFamilySet getFamilySet() {
            File configFilename = new File(getConfigFilePath());
            FontFamilySetApi21 familySet = null;
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(new FileReader(configFilename));
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            if (FontFamilySetApi21.TAG.equals(parser.getName())) {
                                familySet = new FontFamilySetApi21(parser);
                            }
                            break;
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                return DEFAULT;
            }
            if (familySet == null || familySet.families.isEmpty()) {
                return DEFAULT;
            }
            FontFamilySet familySetCompat = new FontFamilySet(familySet.version);
            for (FamilyApi21 family : familySet.families) {
                FontFamily familyCompat = new FontFamily(family.name, family.lang, family.variant);
                for (FontApi21 font : family.fonts) {
                    familyCompat.getFonts().add(new Font(font.weight, font.style, font.ttf));
                }
                familySetCompat.getFamilies().add(familyCompat);
            }
            for (AliasApi21 alias : familySet.aliases) {
                familySetCompat.getAliases().add(new FontAlias(alias.name, alias.to, alias.weight));
            }
            if (familySetCompat.getFamilies().isEmpty())
                return DEFAULT;
            return familySetCompat;
        }
    }

    private static class FontFamilySetApi21 {
        static final String TAG = "familyset";
        private final ArrayList<FamilyApi21> families = new ArrayList<>();
        private final ArrayList<AliasApi21> aliases = new ArrayList<>();
        private String version;

        FontFamilySetApi21(XmlPullParser parser) throws Exception {
            version = parser.getAttributeValue(null, "version");
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (FamilyApi21.TAG.equals(parser.getName())) {
                            families.add(new FamilyApi21(parser));
                        } else if (AliasApi21.TAG.equals(parser.getName())) {
                            aliases.add(new AliasApi21(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class FamilyApi21 {
        public static final String TAG = "family";
        private final ArrayList<FontApi21> fonts = new ArrayList<>();
        private String name;
        private String lang;
        private String variant;

        FamilyApi21(XmlPullParser parser) throws Exception {
            name = parser.getAttributeValue(null, "name");
            lang = parser.getAttributeValue(null, "lang");
            variant = parser.getAttributeValue(null, "variant");
            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (FontApi21.TAG.equals(parser.getName())) {
                            fonts.add(new FontApi21(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(parser.getName())) {
                            return;
                        }
                }
                event = parser.next();
            }
        }
    }

    private static class AliasApi21 {
        static final String TAG = "alias";
        private String name;
        private String to;
        private String weight;

        AliasApi21(XmlPullParser parser) {
            name = parser.getAttributeValue(null, "name");
            to = parser.getAttributeValue(null, "to");
            weight = parser.getAttributeValue(null, "weight");
        }
    }

    private static class FontApi21 {
        public static final String TAG = "font";
        private String weight;
        private String style;
        private String ttf;

        FontApi21(XmlPullParser parser) throws Exception {
            weight = parser.getAttributeValue(null, "weight");
            style = parser.getAttributeValue(null, "style");
            try {
                ttf = parser.nextText();
            } catch (Exception e) {
                ttf = null;
            }
        }
    }
}
