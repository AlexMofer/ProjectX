/*
 * Copyright (C) 2015 AlexMofer
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

package am.project.support.compat;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
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
    private static final FamilySet DEFAULT;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new FontCompatApi21Impl();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new FontCompatApi14Impl();
        } else {
            IMPL = new FontCompatBaseImpl();
        }
        DEFAULT = new FamilySet((String) null);
        DEFAULT.families.add(new Family("sans-serif"));
        DEFAULT.families.add(new Family("serif"));
        DEFAULT.families.add(new Family("monospace"));
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
    public static FamilySet getFamilySet() {
        return IMPL.getFamilySet();
    }

    private interface FontCompatImpl {

        String getConfigFilePath();

        FamilySet getFamilySet();
    }

    public static class FamilySet implements Parcelable {
        public static final Parcelable.Creator<FamilySet> CREATOR = new Parcelable.Creator<FamilySet>() {
            @Override
            public FamilySet createFromParcel(Parcel source) {
                return new FamilySet(source);
            }

            @Override
            public FamilySet[] newArray(int size) {
                return new FamilySet[size];
            }
        };
        private final ArrayList<Family> families = new ArrayList<>();
        private final ArrayList<Alias> aliases = new ArrayList<>();
        private String version;

        private FamilySet(String version) {
            this.version = version;
        }

        private FamilySet(Parcel in) {
            this.version = in.readString();
            this.families.addAll(in.createTypedArrayList(Family.CREATOR));
            this.aliases.addAll(in.createTypedArrayList(Alias.CREATOR));
        }

        public String getVersion() {
            return version;
        }

        public ArrayList<Family> getFamilies() {
            return families;
        }

        public ArrayList<Alias> getAliases() {
            return aliases;
        }

        @Override
        public String toString() {
            return "FamilySet{" +
                    "version='" + version + '\'' +
                    ", families=" + families +
                    ", aliases=" + aliases +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.version);
            dest.writeTypedList(this.families);
            dest.writeTypedList(this.aliases);
        }
    }

    public static class Family implements Parcelable {
        public static final Parcelable.Creator<Family> CREATOR = new Parcelable.Creator<Family>() {
            @Override
            public Family createFromParcel(Parcel source) {
                return new Family(source);
            }

            @Override
            public Family[] newArray(int size) {
                return new Family[size];
            }
        };
        private final ArrayList<Font> fonts = new ArrayList<>();
        private String name;
        private String lang;
        private String variant;

        private Family(String name) {
            this.name = name;
        }

        private Family(String name, String lang, String variant) {
            this.name = name;
            this.lang = lang;
            this.variant = variant;
        }

        private Family(Parcel in) {
            this.name = in.readString();
            this.lang = in.readString();
            this.variant = in.readString();
            this.fonts.addAll(in.createTypedArrayList(Font.CREATOR));
        }

        public String getName() {
            return name;
        }

        public String getLang() {
            return lang;
        }

        public String getVariant() {
            return variant;
        }

        public ArrayList<Font> getFonts() {
            return fonts;
        }

        @Override
        public String toString() {
            return "Family{" +
                    "name='" + name + '\'' +
                    ", lang='" + lang + '\'' +
                    ", variant='" + variant + '\'' +
                    ", fonts=" + fonts +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.lang);
            dest.writeString(this.variant);
            dest.writeTypedList(this.fonts);
        }
    }

    public static class Alias implements Parcelable {
        public static final Parcelable.Creator<Alias> CREATOR = new Parcelable.Creator<Alias>() {
            @Override
            public Alias createFromParcel(Parcel source) {
                return new Alias(source);
            }

            @Override
            public Alias[] newArray(int size) {
                return new Alias[size];
            }
        };
        private String name;
        private String to;
        private String weight;

        private Alias(String name, String to, String weight) {
            this.name = name;
            this.to = to;
            this.weight = weight;
        }

        private Alias(Parcel in) {
            this.name = in.readString();
            this.to = in.readString();
            this.weight = in.readString();
        }

        public String getName() {
            return name;
        }

        public String getTo() {
            return to;
        }

        public String getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "Alias{" +
                    "name='" + name + '\'' +
                    ", to='" + to + '\'' +
                    ", weight='" + weight + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.to);
            dest.writeString(this.weight);
        }
    }

    public static class Font implements Parcelable {
        public static final Parcelable.Creator<Font> CREATOR = new Parcelable.Creator<Font>() {
            @Override
            public Font createFromParcel(Parcel source) {
                return new Font(source);
            }

            @Override
            public Font[] newArray(int size) {
                return new Font[size];
            }
        };
        private String weight;
        private String style;
        private String ttf;

        private Font(String ttf) {
            this.ttf = ttf;
        }

        private Font(String weight, String style, String ttf) {
            this.weight = weight;
            this.style = style;
            this.ttf = ttf;
        }

        private Font(Parcel in) {
            this.weight = in.readString();
            this.style = in.readString();
            this.ttf = in.readString();
        }

        public String getWeight() {
            return weight;
        }

        public String getStyle() {
            return style;
        }

        public String getTtf() {
            return ttf;
        }

        @Override
        public String toString() {
            return "Font{" +
                    "weight='" + weight + '\'' +
                    ", style='" + style + '\'' +
                    ", ttf='" + ttf + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.weight);
            dest.writeString(this.style);
            dest.writeString(this.ttf);
        }
    }

    private static class FontCompatBaseImpl implements FontCompatImpl {

        @Override
        public String getConfigFilePath() {
            return "/system/etc/fonts.xml";
        }

        @Override
        public FamilySet getFamilySet() {
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
            FamilySet familySet = new FamilySet((String) null);
            for (FontBase font : fonts.fonts) {
                for (String name : font.names) {
                    Family family = new Family(name);
                    family.fonts.add(new Font(font.ttf + ".ttf"));
                    familySet.families.add(family);
                }
            }
            if (familySet.families.isEmpty())
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
        public FamilySet getFamilySet() {
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
            FamilySet familySetCompat = new FamilySet((String) null);
            for (FamilyApi14 family : familySet.families) {
                for (String name : family.names) {
                    Family familyCompat = new Family(name);
                    for (String file : family.files) {
                        familyCompat.fonts.add(new Font(file));
                    }
                    familySetCompat.families.add(familyCompat);
                }
            }
            if (familySetCompat.families.isEmpty())
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
        public FamilySet getFamilySet() {
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
            FamilySet familySetCompat = new FamilySet(familySet.version);
            for (FamilyApi21 family : familySet.families) {
                Family familyCompat = new Family(family.name, family.lang, family.variant);
                for (FontApi21 font : family.fonts) {
                    familyCompat.fonts.add(new Font(font.weight, font.style, font.ttf));
                }
                familySetCompat.families.add(familyCompat);
            }
            for (AliasApi21 alias : familySet.aliases) {
                familySetCompat.aliases.add(new Alias(alias.name, alias.to, alias.weight));
            }
            if (familySetCompat.families.isEmpty())
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
