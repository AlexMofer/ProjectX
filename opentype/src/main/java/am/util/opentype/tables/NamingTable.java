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
package am.util.opentype.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import am.util.opentype.FileOpenTypeReader;
import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Naming Table
 * The naming table allows multilingual strings to be associated with the OpenType™ font.
 * These strings can represent copyright notices, font names, family names, style names, and so on.
 * To keep this table short, the font manufacturer may wish to make a limited set of entries
 * in some small set of languages; later, the font can be “localized” and the strings
 * translated or added. Other parts of the OpenType font that require these strings can
 * refer to them using a language-independent name ID. In addition to language variants,
 * the table also allows for platform-specific character-encoding variants. Clients that
 * need a particular string can look it up by its platform ID, encoding ID, language ID and name ID.
 * Note that different platforms may have different requirements for the encoding of strings.
 * Many newer platforms can use strings intended for different platforms if a font does not
 * include strings for that platform. Some applications might display incorrect strings, however,
 * if strings for the current platform are not included.
 */
@SuppressWarnings("unused")
public class NamingTable extends BaseTable {

    private final int mFormat;// Format selector (0 or 1).
    private final int mCount;// Number of name records.
    private final int mStringOffset;// Offset to start of string storage (from start of table).
    private final List<NameRecord> mNameRecords;
    private final List<LangTagRecord> mLangTagRecords;
    private final List<String> mLangTags;
    private final HashSet<String> mFamilyNames = new HashSet<>();
    private final HashSet<String> mTypographicFamilyNames = new HashSet<>();
    private final HashSet<String> mWWSFamilyNames = new HashSet<>();
    private String mCopyrightNotice;
    private String mSubfamilyName;
    private String mUID;
    private String mFullName;
    private String mVersion;
    private String mPostScriptName;
    private String mTrademark;
    private String mManufacturer;
    private String mDesigner;
    private String mDescription;
    private String mURLVendor;
    private String mURLDesigner;
    private String mLicense;
    private String mURLLicense;
    private String mTypographicSubfamilyName;
    private String mCompatible;
    private String mSample;
    private String mPostScriptCID;
    private String mWWSSubfamilyName;
    private String mBackgroundPaletteLight;
    private String mBackgroundPaletteDark;
    private String mVariationsPostScriptNamePrefix;

    public NamingTable(OpenTypeReader reader, TableRecord record) throws IOException {
        super(record);
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_NAME)
            throw new IOException();
        reader.seek(record.getOffset());
        final int format = reader.readUnsignedShort();
        final int count = reader.readUnsignedShort();
        final int stringOffset = reader.readUnsignedShort();
        final ArrayList<NameRecord> nameRecords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int platformID = reader.readUnsignedShort();
            final int encodingID = reader.readUnsignedShort();
            final int languageID = reader.readUnsignedShort();
            final int nameID = reader.readUnsignedShort();
            final int length = reader.readUnsignedShort();
            final int offset = reader.readUnsignedShort();
            final long pos = reader.getPointer();
            final byte[] data = new byte[length];
            reader.seek(record.getOffset() + stringOffset + offset);
            reader.read(data, 0, length);
            reader.seek(pos);
            nameRecords.add(new NameRecord(platformID, encodingID, languageID, nameID,
                    length, offset, data));
        }
        ArrayList<LangTagRecord> langTagRecords = null;
        ArrayList<String> langTags = null;
        if (format == 1 && (record.getOffset() + stringOffset) > reader.getPointer()) {
            // Naming table format 1
            final int langTagCount = reader.readUnsignedShort();
            if (langTagCount > 0) {
                langTagRecords = new ArrayList<>();
                for (int i = 0; i < langTagCount; i++) {
                    final int length = reader.readUnsignedShort();
                    final int offset = reader.readUnsignedShort();
                    langTagRecords.add(new LangTagRecord(length, offset));
                }
                langTags = new ArrayList<>();
                for (LangTagRecord re : langTagRecords) {
                    reader.seek(record.getOffset() + stringOffset + re.getOffset());
                    langTags.add(reader.readString(re.getLength(),
                            FileOpenTypeReader.CHARSET_UTF_16BE));
                }
            }
        }
        mFormat = format;
        mCount = count;
        mStringOffset = stringOffset;
        mNameRecords = nameRecords;
        mLangTagRecords = langTagRecords;
        mLangTags = langTags;
        try {
            initNames();
        } catch (IOException e) {
            // ignore
        }
    }

    private void initNames() throws IOException {
        if (mNameRecords == null || mNameRecords.isEmpty())
            return;
        for (NameRecord record : mNameRecords) {
            final byte[] data = record.getData();
            if (data == null || data.length <= 0)
                continue;
            final int platformID = record.getPlatformID();
            final int encodingID = record.getEncodingID();
            final int languageID = record.getLanguageID();
            final String text;
            if (platformID == NameRecord.PLATFORM_WINDOWS) {
                if (encodingID == NameRecord.ENCODING_WINDOWS_0 ||
                        encodingID == NameRecord.ENCODING_WINDOWS_1) {
                    if (languageID == NameRecord.LANGUAGE_WINDOWS_0409)
                        // 已验证
                        text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                    else
                        // 已验证
                        text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                } else {
                    // 未验证
                    text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                }

            } else if (platformID == NameRecord.PLATFORM_MACINTOSH) {
                if (encodingID == NameRecord.ENCODING_MACINTOSH_0) {
                    if (languageID == NameRecord.LANGUAGE_MACINTOSH_0)
                        // 已验证
                        text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                    else
                        // 未验证
                        text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                } else {
                    // 未验证
                    text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                }
            } else {
                // 未验证，应该存在问题
                if (data[0] == 0) {
                    text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                } else {
                    text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                }
            }
            final int nameID = record.getNameID();
            switch (nameID) {
                case NameRecord.NAME_0:
                    mCopyrightNotice = text;
                    break;
                case NameRecord.NAME_1:
                    mFamilyNames.add(text);
                    break;
                case NameRecord.NAME_2:
                    mSubfamilyName = text;
                    break;
                case NameRecord.NAME_3:
                    mUID = text;
                    break;
                case NameRecord.NAME_4:
                    mFullName = text;
                    break;
                case NameRecord.NAME_5:
                    mVersion = text;
                    break;
                case NameRecord.NAME_6:
                    mPostScriptName = text;
                    break;
                case NameRecord.NAME_7:
                    mTrademark = text;
                    break;
                case NameRecord.NAME_8:
                    mManufacturer = text;
                    break;
                case NameRecord.NAME_9:
                    mDesigner = text;
                    break;
                case NameRecord.NAME_10:
                    mDescription = text;
                    break;
                case NameRecord.NAME_11:
                    mURLVendor = text;
                    break;
                case NameRecord.NAME_12:
                    mURLDesigner = text;
                    break;
                case NameRecord.NAME_13:
                    mLicense = text;
                    break;
                case NameRecord.NAME_14:
                    mURLLicense = text;
                    break;
                case NameRecord.NAME_16:
                    mTypographicFamilyNames.add(text);
                    break;
                case NameRecord.NAME_17:
                    mTypographicSubfamilyName = text;
                    break;
                case NameRecord.NAME_18:
                    mCompatible = text;
                    break;
                case NameRecord.NAME_19:
                    mSample = text;
                    break;
                case NameRecord.NAME_20:
                    mPostScriptCID = text;
                    break;
                case NameRecord.NAME_21:
                    mWWSFamilyNames.add(text);
                    break;
                case NameRecord.NAME_22:
                    mWWSSubfamilyName = text;
                    break;
                case NameRecord.NAME_23:
                    mBackgroundPaletteLight = text;
                    break;
                case NameRecord.NAME_24:
                    mBackgroundPaletteDark = text;
                    break;
                case NameRecord.NAME_25:
                    mVariationsPostScriptNamePrefix = text;
                    break;
            }
        }
    }

    /**
     * 获取语言标签
     * 语言ID大于等于0x8000时，匹配一个语言标签
     * 语言标签命名规范：IETF specification BCP 47(https://tools.ietf.org/html/bcp47)
     *
     * @param languageID 命名记录中的语言ID
     * @return 语言标签，不存在或错误的语言ID返回空
     */
    public String getLangTag(int languageID) {
        if (mLangTags == null)
            return null;
        final int index = languageID - NameRecord.LANGUAGE_DIVIDE;
        if (index < 0 || index >= mLangTags.size())
            return null;
        return mLangTags.get(index);
    }

    /**
     * 获取格式
     *
     * @return 格式
     */
    public int getFormat() {
        return mFormat;
    }

    /**
     * 获取条目总数
     *
     * @return 总数
     */
    public int getCount() {
        return mCount;
    }

    /**
     * 获取字符串起始偏移（从表头第一个字节开始计算）
     *
     * @return 字符串起始偏移
     */
    public int getStringOffset() {
        return mStringOffset;
    }

    /**
     * 或去命名记录条目
     *
     * @return 命名记录条目
     */
    public List<NameRecord> getNameRecords() {
        return mNameRecords;
    }

    /**
     * 获取语言标签记录条目
     *
     * @return 语言标签记录条目
     */
    public List<LangTagRecord> getLangTagRecords() {
        return mLangTagRecords;
    }

    /**
     * 获取家族名
     *
     * @return 家族名
     */
    public Set<String> getFamilyNames() {
        return mFamilyNames;
    }

    /**
     * 获取印刷家族名
     *
     * @return 印刷家族名
     */
    public Set<String> getTypographicFamilyNames() {
        return mTypographicFamilyNames;
    }

    /**
     * 获取WWS家族名
     *
     * @return WWS家族名
     */
    public Set<String> getWWSFamilyNames() {
        return mWWSFamilyNames;
    }

    /**
     * 获取版权声明
     *
     * @return 版权申明
     */
    public String getCopyrightNotice() {
        return mCopyrightNotice;
    }

    /**
     * 获取样式名称（粗体、斜体、粗斜体）
     *
     * @return 样式名称
     */
    public String getSubfamilyName() {
        return mSubfamilyName;
    }

    /**
     * 获取唯一标志
     *
     * @return 唯一标志
     */
    public String getUID() {
        return mUID;
    }

    /**
     * 获取字体全称
     *
     * @return 字体全称
     */
    public String getFullName() {
        return mFullName;
    }

    /**
     * 获取版本信息
     *
     * @return 版本信息
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * 获取PostScript名
     *
     * @return PostScript名
     */
    public String getPostScriptName() {
        return mPostScriptName;
    }

    /**
     * 获取商标
     *
     * @return 商标
     */
    public String getTrademark() {
        return mTrademark;
    }

    /**
     * 获取字体供应商
     *
     * @return 字体供应商
     */
    public String getManufacturer() {
        return mManufacturer;
    }

    /**
     * 获取设计者
     *
     * @return 设计者
     */
    public String getDesigner() {
        return mDesigner;
    }

    /**
     * 获取字体描述
     *
     * @return 字体描述
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * 获取供应商链接
     *
     * @return 供应商链接
     */
    public String getURLVendor() {
        return mURLVendor;
    }

    /**
     * 获取设计者链接
     *
     * @return 设计者链接
     */
    public String getURLDesigner() {
        return mURLDesigner;
    }

    /**
     * 获取字体许可
     *
     * @return 字体许可
     */
    public String getLicense() {
        return mLicense;
    }

    /**
     * 获取字体许可链接
     *
     * @return 字体许可链接
     */
    public String getURLLicense() {
        return mURLLicense;
    }

    /**
     * 获取印刷样式名称
     *
     * @return 印刷样式名称
     */
    public String getTypographicSubfamilyName() {
        return mTypographicSubfamilyName;
    }

    /**
     * 获取Compatible Full名称（仅Mac平台需要）
     *
     * @return Compatible Full名称
     */
    public String getCompatible() {
        return mCompatible;
    }

    /**
     * 获取示例文本（用于展示字体效果）
     *
     * @return 示例文本
     */
    public String getSample() {
        return mSample;
    }

    /**
     * 获取PostScript CID
     *
     * @return PostScript CID
     */
    public String getPostScriptCID() {
        return mPostScriptCID;
    }

    /**
     * 获取WWS样式名称
     *
     * @return WWS样式名称
     */
    public String getWWSSubfamilyName() {
        return mWWSSubfamilyName;
    }

    /**
     * 获取浅色背景调色板
     *
     * @return 浅色背景调色板
     */
    public String getBackgroundPaletteLight() {
        return mBackgroundPaletteLight;
    }

    /**
     * 获取深色背景调色板
     *
     * @return 深色背景调色板
     */
    public String getBackgroundPaletteDark() {
        return mBackgroundPaletteDark;
    }

    /**
     * 获取变体PostScript名称前缀
     *
     * @return 变体PostScript名称前缀
     */
    public String getVariationsPostScriptNamePrefix() {
        return mVariationsPostScriptNamePrefix;
    }

    @Override
    public int getHashCode() {
        return Objects.hash(super.getHashCode(), mFormat, mCount, mStringOffset, mNameRecords,
                mLangTagRecords, mLangTags, mFamilyNames, mTypographicFamilyNames, mWWSFamilyNames,
                mCopyrightNotice, mSubfamilyName, mUID, mFullName, mVersion, mPostScriptName,
                mTrademark, mManufacturer, mDesigner, mDescription, mURLVendor, mURLDesigner,
                mLicense, mURLLicense, mTypographicSubfamilyName, mCompatible, mSample,
                mPostScriptCID, mWWSSubfamilyName, mBackgroundPaletteLight, mBackgroundPaletteDark,
                mVariationsPostScriptNamePrefix);
    }

    @Override
    public String getString() {
        return "NamingTable{" +
                "record=" + String.valueOf(getTableRecord()) +
                ", format=" + mFormat +
                ", count=" + mCount +
                ", stringOffset=" + mStringOffset +
                ", copyrightNotice='" + mCopyrightNotice + '\'' +
                ", subfamilyName='" + mSubfamilyName + '\'' +
                ", UID='" + mUID + '\'' +
                ", fullName='" + mFullName + '\'' +
                ", version='" + mVersion + '\'' +
                ", postScriptName='" + mPostScriptName + '\'' +
                ", trademark='" + mTrademark + '\'' +
                ", manufacturer='" + mManufacturer + '\'' +
                ", designer='" + mDesigner + '\'' +
                ", description='" + mDescription + '\'' +
                ", URLVendor='" + mURLVendor + '\'' +
                ", URLDesigner='" + mURLDesigner + '\'' +
                ", license='" + mLicense + '\'' +
                ", URLLicense='" + mURLLicense + '\'' +
                ", typographicSubfamilyName='" + mTypographicSubfamilyName + '\'' +
                ", compatible='" + mCompatible + '\'' +
                ", sample='" + mSample + '\'' +
                ", postScriptCID='" + mPostScriptCID + '\'' +
                ", WWSSubfamilyName='" + mWWSSubfamilyName + '\'' +
                ", backgroundPaletteLight='" + mBackgroundPaletteLight + '\'' +
                ", backgroundPaletteDark='" + mBackgroundPaletteDark + '\'' +
                ", variationsPostScriptNamePrefix='" + mVariationsPostScriptNamePrefix + '\'' +
                ", nameRecords=" + String.valueOf(mNameRecords) +
                ", langTagRecords=" + String.valueOf(mLangTagRecords) +
                ", langTags=" + String.valueOf(mLangTags) +
                ", familyNames=" + String.valueOf(mFamilyNames) +
                ", typographicFamilyNames=" + String.valueOf(mTypographicFamilyNames) +
                ", WWSFamilyNames=" + String.valueOf(mWWSFamilyNames) +
                '}';
    }

    /**
     * 命名记录
     */
    @SuppressWarnings("WeakerAccess")
    public static class NameRecord {

        public static final int PLATFORM_UNICODE = 0;
        public static final int PLATFORM_MACINTOSH = 1;
        // Platform ID 2 (ISO) has been deprecated as of OpenType version v1.3.
        // It was intended to represent ISO/IEC 10646, as opposed to Unicode.
        // It is redundant, however, since both standards have identical character code assignments.
        @Deprecated
        public static final int PLATFORM_ISO = 2;
        public static final int PLATFORM_WINDOWS = 3;
        public static final int PLATFORM_CUSTOM = 4;

        // Platform-specific encoding and language IDs: Unicode platform (platform ID = 0)
        public static final int ENCODING_UNICODE_0 = 0;// Unicode 1.0 semantics
        public static final int ENCODING_UNICODE_1 = 1;// Unicode 1.1 semantics
        public static final int ENCODING_UNICODE_2 = 2;// ISO/IEC 10646 semantics
        public static final int ENCODING_UNICODE_3 = 3;// Unicode 2.0 and onwards semantics, Unicode BMP only ('cmap' subtable formats 0, 4, 6).
        public static final int ENCODING_UNICODE_4 = 4;// Unicode 2.0 and onwards semantics, Unicode full repertoire ('cmap' subtable formats 0, 4, 6, 10, 12).
        public static final int ENCODING_UNICODE_5 = 5;// Unicode Variation Sequences ('cmap' subtable format 14).
        public static final int ENCODING_UNICODE_6 = 6;// Unicode full repertoire ('cmap' subtable formats 0, 4, 6, 10, 12, 13).


        public static final int LANGUAGE_DIVIDE = 0x8000;//  If a language ID is less than 0x8000, it has a platform-specific interpretation as with a format 0 naming table. If a language ID is equal to or greater than 0x8000, it is associated with a language-tag record (LangTagRecord) that references a language-tag string.

        // Platform-specific encoding and language IDs: Macintosh platform (platform ID = 1)
        public static final int ENCODING_MACINTOSH_0 = 0;// Roman
        public static final int ENCODING_MACINTOSH_1 = 1;// Japanese
        public static final int ENCODING_MACINTOSH_2 = 2;// Chinese (Traditional)
        public static final int ENCODING_MACINTOSH_3 = 3;// Korean
        public static final int ENCODING_MACINTOSH_4 = 4;// Arabic
        public static final int ENCODING_MACINTOSH_5 = 5;// Hebrew
        public static final int ENCODING_MACINTOSH_6 = 6;// Greek
        public static final int ENCODING_MACINTOSH_7 = 7;// Russian
        public static final int ENCODING_MACINTOSH_8 = 8;// RSymbol
        public static final int ENCODING_MACINTOSH_9 = 9;// Devanagari
        public static final int ENCODING_MACINTOSH_10 = 10;// Gurmukhi
        public static final int ENCODING_MACINTOSH_11 = 11;// Gujarati
        public static final int ENCODING_MACINTOSH_12 = 12;// Oriya
        public static final int ENCODING_MACINTOSH_13 = 13;// Bengali
        public static final int ENCODING_MACINTOSH_14 = 14;// Tamil
        public static final int ENCODING_MACINTOSH_15 = 15;// Telugu
        public static final int ENCODING_MACINTOSH_16 = 16;// Kannada
        public static final int ENCODING_MACINTOSH_17 = 17;// Malayalam
        public static final int ENCODING_MACINTOSH_18 = 18;// Sinhalese
        public static final int ENCODING_MACINTOSH_19 = 19;// Burmese
        public static final int ENCODING_MACINTOSH_20 = 20;// Khmer
        public static final int ENCODING_MACINTOSH_21 = 21;// Thai
        public static final int ENCODING_MACINTOSH_22 = 22;// Laotian
        public static final int ENCODING_MACINTOSH_23 = 23;// Georgian
        public static final int ENCODING_MACINTOSH_24 = 24;// Armenian
        public static final int ENCODING_MACINTOSH_25 = 25;// Chinese (Simplified)
        public static final int ENCODING_MACINTOSH_26 = 26;// Tibetan
        public static final int ENCODING_MACINTOSH_27 = 27;// Mongolian
        public static final int ENCODING_MACINTOSH_28 = 28;// Geez
        public static final int ENCODING_MACINTOSH_29 = 29;// Slavic
        public static final int ENCODING_MACINTOSH_30 = 30;// Vietnamese
        public static final int ENCODING_MACINTOSH_31 = 31;// Sindhi
        public static final int ENCODING_MACINTOSH_32 = 32;// Uninterpreted

        public static final int LANGUAGE_MACINTOSH_0 = 0;// English
        public static final int LANGUAGE_MACINTOSH_1 = 1;// French
        public static final int LANGUAGE_MACINTOSH_2 = 2;// German
        public static final int LANGUAGE_MACINTOSH_3 = 3;// Italian
        public static final int LANGUAGE_MACINTOSH_4 = 4;// Dutch
        public static final int LANGUAGE_MACINTOSH_5 = 5;// Swedish
        public static final int LANGUAGE_MACINTOSH_6 = 6;// Spanish
        public static final int LANGUAGE_MACINTOSH_7 = 7;// Danish
        public static final int LANGUAGE_MACINTOSH_8 = 8;// Portuguese
        public static final int LANGUAGE_MACINTOSH_9 = 9;// Norwegian
        public static final int LANGUAGE_MACINTOSH_10 = 10;// Hebrew
        public static final int LANGUAGE_MACINTOSH_11 = 11;// Japanese
        public static final int LANGUAGE_MACINTOSH_12 = 12;// Arabic
        public static final int LANGUAGE_MACINTOSH_13 = 13;// Finnish
        public static final int LANGUAGE_MACINTOSH_14 = 14;// Greek
        public static final int LANGUAGE_MACINTOSH_15 = 15;// Icelandic
        public static final int LANGUAGE_MACINTOSH_16 = 16;// Maltese
        public static final int LANGUAGE_MACINTOSH_17 = 17;// Turkish
        public static final int LANGUAGE_MACINTOSH_18 = 18;// Croatian
        public static final int LANGUAGE_MACINTOSH_19 = 19;// Chinese (Traditional)
        public static final int LANGUAGE_MACINTOSH_20 = 20;// Urdu
        public static final int LANGUAGE_MACINTOSH_21 = 21;// Hindi
        public static final int LANGUAGE_MACINTOSH_22 = 22;// Thai
        public static final int LANGUAGE_MACINTOSH_23 = 23;// Korean
        public static final int LANGUAGE_MACINTOSH_24 = 24;// Lithuanian
        public static final int LANGUAGE_MACINTOSH_25 = 25;// Polish
        public static final int LANGUAGE_MACINTOSH_26 = 26;// Hungarian
        public static final int LANGUAGE_MACINTOSH_27 = 27;// Estonian
        public static final int LANGUAGE_MACINTOSH_28 = 28;// Latvian
        public static final int LANGUAGE_MACINTOSH_29 = 29;// Sami
        public static final int LANGUAGE_MACINTOSH_30 = 30;// Faroese
        public static final int LANGUAGE_MACINTOSH_31 = 31;// Farsi/Persian
        public static final int LANGUAGE_MACINTOSH_32 = 32;// Russian
        public static final int LANGUAGE_MACINTOSH_33 = 33;// Chinese (Simplified)
        public static final int LANGUAGE_MACINTOSH_34 = 34;// Flemish
        public static final int LANGUAGE_MACINTOSH_35 = 35;// Irish Gaelic
        public static final int LANGUAGE_MACINTOSH_36 = 36;// Albanian
        public static final int LANGUAGE_MACINTOSH_37 = 37;// Romanian
        public static final int LANGUAGE_MACINTOSH_38 = 38;// Czech
        public static final int LANGUAGE_MACINTOSH_39 = 39;// Slovak
        public static final int LANGUAGE_MACINTOSH_40 = 40;// Slovenian
        public static final int LANGUAGE_MACINTOSH_41 = 41;// Yiddish
        public static final int LANGUAGE_MACINTOSH_42 = 42;// Serbian
        public static final int LANGUAGE_MACINTOSH_43 = 43;// Macedonian
        public static final int LANGUAGE_MACINTOSH_44 = 44;// Bulgarian
        public static final int LANGUAGE_MACINTOSH_45 = 45;// Ukrainian
        public static final int LANGUAGE_MACINTOSH_46 = 46;// Byelorussian
        public static final int LANGUAGE_MACINTOSH_47 = 47;// Uzbek
        public static final int LANGUAGE_MACINTOSH_48 = 48;// Kazakh
        public static final int LANGUAGE_MACINTOSH_49 = 49;// Azerbaijani (Cyrillic script)
        public static final int LANGUAGE_MACINTOSH_50 = 50;// Azerbaijani (Arabic script)
        public static final int LANGUAGE_MACINTOSH_51 = 51;// Armenian
        public static final int LANGUAGE_MACINTOSH_52 = 52;// Georgian
        public static final int LANGUAGE_MACINTOSH_53 = 53;// Moldavian
        public static final int LANGUAGE_MACINTOSH_54 = 54;// Kirghiz
        public static final int LANGUAGE_MACINTOSH_55 = 55;// Tajiki
        public static final int LANGUAGE_MACINTOSH_56 = 56;// Turkmen
        public static final int LANGUAGE_MACINTOSH_57 = 57;// Mongolian (Mongolian script)
        public static final int LANGUAGE_MACINTOSH_58 = 58;// Mongolian (Cyrillic script)
        public static final int LANGUAGE_MACINTOSH_59 = 59;// Pashto
        public static final int LANGUAGE_MACINTOSH_60 = 60;// Kurdish
        public static final int LANGUAGE_MACINTOSH_61 = 61;// Kashmiri
        public static final int LANGUAGE_MACINTOSH_62 = 62;// Sindhi
        public static final int LANGUAGE_MACINTOSH_63 = 63;// Tibetan
        public static final int LANGUAGE_MACINTOSH_64 = 64;// Nepali
        public static final int LANGUAGE_MACINTOSH_65 = 65;// Sanskrit
        public static final int LANGUAGE_MACINTOSH_66 = 66;// Marathi
        public static final int LANGUAGE_MACINTOSH_67 = 67;// Bengali
        public static final int LANGUAGE_MACINTOSH_68 = 68;// Assamese
        public static final int LANGUAGE_MACINTOSH_69 = 69;// Gujarati
        public static final int LANGUAGE_MACINTOSH_70 = 70;// Punjabi
        public static final int LANGUAGE_MACINTOSH_71 = 71;// Oriya
        public static final int LANGUAGE_MACINTOSH_72 = 72;// Malayalam
        public static final int LANGUAGE_MACINTOSH_73 = 73;// Kannada
        public static final int LANGUAGE_MACINTOSH_74 = 74;// Tamil
        public static final int LANGUAGE_MACINTOSH_75 = 75;// Telugu
        public static final int LANGUAGE_MACINTOSH_76 = 76;// Sinhalese
        public static final int LANGUAGE_MACINTOSH_77 = 77;// Burmese
        public static final int LANGUAGE_MACINTOSH_78 = 78;// Khmer
        public static final int LANGUAGE_MACINTOSH_79 = 79;// Lao
        public static final int LANGUAGE_MACINTOSH_80 = 80;// Vietnamese
        public static final int LANGUAGE_MACINTOSH_81 = 81;// Indonesian
        public static final int LANGUAGE_MACINTOSH_82 = 82;// Tagalog
        public static final int LANGUAGE_MACINTOSH_83 = 83;// Malay (Roman script)
        public static final int LANGUAGE_MACINTOSH_84 = 84;// Malay (Arabic script)
        public static final int LANGUAGE_MACINTOSH_85 = 85;// Amharic
        public static final int LANGUAGE_MACINTOSH_86 = 86;// Tigrinya
        public static final int LANGUAGE_MACINTOSH_87 = 87;// Galla
        public static final int LANGUAGE_MACINTOSH_88 = 88;// Somali
        public static final int LANGUAGE_MACINTOSH_89 = 89;// Swahili
        public static final int LANGUAGE_MACINTOSH_90 = 90;// Kinyarwanda/Ruanda
        public static final int LANGUAGE_MACINTOSH_91 = 91;// Rundi
        public static final int LANGUAGE_MACINTOSH_92 = 92;// Nyanja/Chewa
        public static final int LANGUAGE_MACINTOSH_93 = 93;// Malagasy
        public static final int LANGUAGE_MACINTOSH_94 = 94;// Esperanto
        public static final int LANGUAGE_MACINTOSH_128 = 128;// Welsh
        public static final int LANGUAGE_MACINTOSH_129 = 129;// Basque
        public static final int LANGUAGE_MACINTOSH_130 = 130;// Catalan
        public static final int LANGUAGE_MACINTOSH_131 = 131;// Latin
        public static final int LANGUAGE_MACINTOSH_132 = 132;// Quechua
        public static final int LANGUAGE_MACINTOSH_133 = 133;// Guarani
        public static final int LANGUAGE_MACINTOSH_134 = 134;// Aymara
        public static final int LANGUAGE_MACINTOSH_135 = 135;// Tatar
        public static final int LANGUAGE_MACINTOSH_136 = 136;// Uighur
        public static final int LANGUAGE_MACINTOSH_137 = 137;// Dzongkha
        public static final int LANGUAGE_MACINTOSH_138 = 138;// Javanese (Roman script)
        public static final int LANGUAGE_MACINTOSH_139 = 139;// Sundanese (Roman script)
        public static final int LANGUAGE_MACINTOSH_140 = 140;// Galician
        public static final int LANGUAGE_MACINTOSH_141 = 141;// Afrikaans
        public static final int LANGUAGE_MACINTOSH_142 = 142;// Breton
        public static final int LANGUAGE_MACINTOSH_143 = 143;// Inuktitut
        public static final int LANGUAGE_MACINTOSH_144 = 144;// Scottish Gaelic
        public static final int LANGUAGE_MACINTOSH_145 = 145;// Manx Gaelic
        public static final int LANGUAGE_MACINTOSH_146 = 146;// Irish Gaelic (with dot above)
        public static final int LANGUAGE_MACINTOSH_147 = 147;// Tongan
        public static final int LANGUAGE_MACINTOSH_148 = 148;// Greek (polytonic)
        public static final int LANGUAGE_MACINTOSH_149 = 149;// Greenlandic
        public static final int LANGUAGE_MACINTOSH_150 = 150;// Azerbaijani (Roman script)

        // Platform-specific encoding and language IDs: ISO platform (platform ID=2) [Deprecated]
        @Deprecated
        public static final int ENCODING_ISO_0 = 0;// 7-bit ASCII
        @Deprecated
        public static final int ENCODING_ISO_1 = 1;// ISO 10646
        @Deprecated
        public static final int ENCODING_ISO_2 = 2;// ISO 8859-1

        // Platform-specific encoding and language IDs: Windows platform (platform ID= 3)
        public static final int ENCODING_WINDOWS_0 = 0;// Symbol
        public static final int ENCODING_WINDOWS_1 = 1;// Unicode BMP
        public static final int ENCODING_WINDOWS_2 = 2;// ShiftJIS
        public static final int ENCODING_WINDOWS_3 = 3;// PRC
        public static final int ENCODING_WINDOWS_4 = 4;// Big5
        public static final int ENCODING_WINDOWS_5 = 5;// Wansung
        public static final int ENCODING_WINDOWS_6 = 6;// Johab
        public static final int ENCODING_WINDOWS_7 = 7;// Reserved
        public static final int ENCODING_WINDOWS_8 = 8;// Reserved
        public static final int ENCODING_WINDOWS_9 = 9;// Reserved
        public static final int ENCODING_WINDOWS_10 = 10;// Unicode full repertoire

        public static final int LANGUAGE_WINDOWS_0436 = 0x0436;// Afrikaans South Africa
        public static final int LANGUAGE_WINDOWS_041C = 0x041C;// Albanian Albania
        public static final int LANGUAGE_WINDOWS_0484 = 0x0484;// Alsatian France
        public static final int LANGUAGE_WINDOWS_045E = 0x045E;// Amharic Ethiopia
        public static final int LANGUAGE_WINDOWS_1401 = 0x1401;// Arabic Algeria
        public static final int LANGUAGE_WINDOWS_3C01 = 0x3C01;// Arabic Bahrain
        public static final int LANGUAGE_WINDOWS_0C01 = 0x0C01;// Arabic Egypt
        public static final int LANGUAGE_WINDOWS_0801 = 0x0801;// Arabic Iraq
        public static final int LANGUAGE_WINDOWS_2C01 = 0x2C01;// Arabic Jordan
        public static final int LANGUAGE_WINDOWS_3401 = 0x3401;// Arabic Kuwait
        public static final int LANGUAGE_WINDOWS_3001 = 0x3001;// Arabic Lebanon
        public static final int LANGUAGE_WINDOWS_1001 = 0x1001;// Arabic Libya
        public static final int LANGUAGE_WINDOWS_1801 = 0x1801;// Arabic Morocco
        public static final int LANGUAGE_WINDOWS_2001 = 0x2001;// Arabic Oman
        public static final int LANGUAGE_WINDOWS_4001 = 0x4001;// Arabic Qatar
        public static final int LANGUAGE_WINDOWS_0401 = 0x0401;// Arabic Saudi Arabia
        public static final int LANGUAGE_WINDOWS_2801 = 0x2801;// Arabic Syria
        public static final int LANGUAGE_WINDOWS_1C01 = 0x1C01;// Arabic Tunisia
        public static final int LANGUAGE_WINDOWS_3801 = 0x3801;// Arabic U.A.E.
        public static final int LANGUAGE_WINDOWS_2401 = 0x2401;// Arabic Yemen
        public static final int LANGUAGE_WINDOWS_042B = 0x042B;// Armenian Armenia
        public static final int LANGUAGE_WINDOWS_044D = 0x044D;// Assamese India
        public static final int LANGUAGE_WINDOWS_082C = 0x082C;// Azeri (Cyrillic) Azerbaijan
        public static final int LANGUAGE_WINDOWS_042C = 0x042C;// Azeri (Latin) Azerbaijan
        public static final int LANGUAGE_WINDOWS_046D = 0x046D;// Bashkir Russia
        public static final int LANGUAGE_WINDOWS_042D = 0x042D;// Basque Basque
        public static final int LANGUAGE_WINDOWS_0423 = 0x0423;// Belarusian Belarus
        public static final int LANGUAGE_WINDOWS_0845 = 0x0845;// Bengali Bangladesh
        public static final int LANGUAGE_WINDOWS_0445 = 0x0445;// Bengali India
        public static final int LANGUAGE_WINDOWS_201A = 0x201A;// Bosnian (Cyrillic) Bosnia and Herzegovina
        public static final int LANGUAGE_WINDOWS_141A = 0x141A;// Bosnian (Latin) Bosnia and Herzegovina
        public static final int LANGUAGE_WINDOWS_047E = 0x047E;// Breton France
        public static final int LANGUAGE_WINDOWS_0402 = 0x0402;// Bulgarian Bulgaria
        public static final int LANGUAGE_WINDOWS_0403 = 0x0403;// Catalan Catalan
        public static final int LANGUAGE_WINDOWS_0C04 = 0x0C04;// Chinese Hong Kong S.A.R.
        public static final int LANGUAGE_WINDOWS_1404 = 0x1404;// Chinese Macao S.A.R.
        public static final int LANGUAGE_WINDOWS_0804 = 0x0804;// Chinese People’s Republic of China
        public static final int LANGUAGE_WINDOWS_1004 = 0x1004;// Chinese Singapore
        public static final int LANGUAGE_WINDOWS_0404 = 0x0404;// Chinese Taiwan
        public static final int LANGUAGE_WINDOWS_0483 = 0x0483;// Corsican France
        public static final int LANGUAGE_WINDOWS_041A = 0x041A;// Croatian Croatia
        public static final int LANGUAGE_WINDOWS_101A = 0x101A;// Croatian (Latin) Bosnia and Herzegovina
        public static final int LANGUAGE_WINDOWS_0405 = 0x0405;// Czech Czech Republic
        public static final int LANGUAGE_WINDOWS_0406 = 0x0406;// Danish Denmark
        public static final int LANGUAGE_WINDOWS_048C = 0x048C;// Dari Afghanistan
        public static final int LANGUAGE_WINDOWS_0465 = 0x0465;// Divehi Maldives
        public static final int LANGUAGE_WINDOWS_0813 = 0x0813;// Dutch Belgium
        public static final int LANGUAGE_WINDOWS_0413 = 0x0413;// Dutch Netherlands
        public static final int LANGUAGE_WINDOWS_0C09 = 0x0C09;// English Australia
        public static final int LANGUAGE_WINDOWS_2809 = 0x2809;// English Belize
        public static final int LANGUAGE_WINDOWS_1009 = 0x1009;// English Canada
        public static final int LANGUAGE_WINDOWS_2409 = 0x2409;// English Caribbean
        public static final int LANGUAGE_WINDOWS_4009 = 0x4009;// English India
        public static final int LANGUAGE_WINDOWS_1809 = 0x1809;// English Ireland
        public static final int LANGUAGE_WINDOWS_2009 = 0x2009;// English Jamaica
        public static final int LANGUAGE_WINDOWS_4409 = 0x4409;// English Malaysia
        public static final int LANGUAGE_WINDOWS_1409 = 0x1409;// English New Zealand
        public static final int LANGUAGE_WINDOWS_3409 = 0x3409;// English Republic of the Philippines
        public static final int LANGUAGE_WINDOWS_4809 = 0x4809;// English Singapore
        public static final int LANGUAGE_WINDOWS_1C09 = 0x1C09;// English South Africa
        public static final int LANGUAGE_WINDOWS_2C09 = 0x2C09;// English Trinidad and Tobago
        public static final int LANGUAGE_WINDOWS_0809 = 0x0809;// English United Kingdom
        public static final int LANGUAGE_WINDOWS_0409 = 0x0409;// English United States
        public static final int LANGUAGE_WINDOWS_3009 = 0x3009;// English Zimbabwe
        public static final int LANGUAGE_WINDOWS_0425 = 0x0425;// Estonian Estonia
        public static final int LANGUAGE_WINDOWS_0438 = 0x0438;// Faroese Faroe Islands
        public static final int LANGUAGE_WINDOWS_0464 = 0x0464;// Filipino Philippines
        public static final int LANGUAGE_WINDOWS_040B = 0x040B;// Finnish Finland
        public static final int LANGUAGE_WINDOWS_080C = 0x080C;// French Belgium
        public static final int LANGUAGE_WINDOWS_0C0C = 0x0C0C;// French Canada
        public static final int LANGUAGE_WINDOWS_040C = 0x040C;// French France
        public static final int LANGUAGE_WINDOWS_140C = 0x140C;// French Luxembourg
        public static final int LANGUAGE_WINDOWS_180C = 0x180C;// French Principality of Monaco
        public static final int LANGUAGE_WINDOWS_100C = 0x100C;// French Switzerland
        public static final int LANGUAGE_WINDOWS_0462 = 0x0462;// Frisian Netherlands
        public static final int LANGUAGE_WINDOWS_0456 = 0x0456;// Galician Galician
        public static final int LANGUAGE_WINDOWS_0437 = 0x0437;// Georgian Georgia
        public static final int LANGUAGE_WINDOWS_0C07 = 0x0C07;// German Austria
        public static final int LANGUAGE_WINDOWS_0407 = 0x0407;// German Germany
        public static final int LANGUAGE_WINDOWS_1407 = 0x1407;// German Liechtenstein
        public static final int LANGUAGE_WINDOWS_1007 = 0x1007;// German Luxembourg
        public static final int LANGUAGE_WINDOWS_0807 = 0x0807;// German Switzerland
        public static final int LANGUAGE_WINDOWS_0408 = 0x0408;// Greek Greece
        public static final int LANGUAGE_WINDOWS_046F = 0x046F;// Greenlandic Greenland
        public static final int LANGUAGE_WINDOWS_0447 = 0x0447;// Gujarati India
        public static final int LANGUAGE_WINDOWS_0468 = 0x0468;// Hausa (Latin) Nigeria
        public static final int LANGUAGE_WINDOWS_040D = 0x040D;// Hebrew Israel
        public static final int LANGUAGE_WINDOWS_0439 = 0x0439;// Hindi India
        public static final int LANGUAGE_WINDOWS_040E = 0x040E;// Hungarian Hungary
        public static final int LANGUAGE_WINDOWS_040F = 0x040F;// Icelandic Iceland
        public static final int LANGUAGE_WINDOWS_0470 = 0x0470;// Igbo Nigeria
        public static final int LANGUAGE_WINDOWS_0421 = 0x0421;// Indonesian Indonesia
        public static final int LANGUAGE_WINDOWS_045D = 0x045D;// Inuktitut Canada
        public static final int LANGUAGE_WINDOWS_085D = 0x085D;// Inuktitut (Latin) Canada
        public static final int LANGUAGE_WINDOWS_083C = 0x083C;// Irish Ireland
        public static final int LANGUAGE_WINDOWS_0434 = 0x0434;// isiXhosa South Africa
        public static final int LANGUAGE_WINDOWS_0435 = 0x0435;// isiZulu South Africa
        public static final int LANGUAGE_WINDOWS_0410 = 0x0410;// Italian Italy
        public static final int LANGUAGE_WINDOWS_0810 = 0x0810;// Italian Switzerland
        public static final int LANGUAGE_WINDOWS_0411 = 0x0411;// Japanese Japan
        public static final int LANGUAGE_WINDOWS_044B = 0x044B;// Kannada India
        public static final int LANGUAGE_WINDOWS_043F = 0x043F;// Kazakh Kazakhstan
        public static final int LANGUAGE_WINDOWS_0453 = 0x0453;// Khmer Cambodia
        public static final int LANGUAGE_WINDOWS_0486 = 0x0486;// K'iche Guatemala
        public static final int LANGUAGE_WINDOWS_0487 = 0x0487;// Kinyarwanda Rwanda
        public static final int LANGUAGE_WINDOWS_0441 = 0x0441;// Kiswahili Kenya
        public static final int LANGUAGE_WINDOWS_0457 = 0x0457;// Konkani India
        public static final int LANGUAGE_WINDOWS_0412 = 0x0412;// Korean Korea
        public static final int LANGUAGE_WINDOWS_0440 = 0x0440;// Kyrgyz Kyrgyzstan
        public static final int LANGUAGE_WINDOWS_0454 = 0x0454;// Lao Lao P.D.R.
        public static final int LANGUAGE_WINDOWS_0426 = 0x0426;// Latvian Latvia
        public static final int LANGUAGE_WINDOWS_0427 = 0x0427;// Lithuanian Lithuania
        public static final int LANGUAGE_WINDOWS_082E = 0x082E;// Lower Sorbian Germany
        public static final int LANGUAGE_WINDOWS_046E = 0x046E;// Luxembourgish Luxembourg
        public static final int LANGUAGE_WINDOWS_042F = 0x042F;// Macedonian (FYROM) Former Yugoslav Republic of Macedonia
        public static final int LANGUAGE_WINDOWS_083E = 0x083E;// Malay Brunei Darussalam
        public static final int LANGUAGE_WINDOWS_043E = 0x043E;// Malay Malaysia
        public static final int LANGUAGE_WINDOWS_044C = 0x044C;// Malayalam India
        public static final int LANGUAGE_WINDOWS_043A = 0x043A;// Maltese Malta
        public static final int LANGUAGE_WINDOWS_0481 = 0x0481;// Maori New Zealand
        public static final int LANGUAGE_WINDOWS_047A = 0x047A;// Mapudungun Chile
        public static final int LANGUAGE_WINDOWS_044E = 0x044E;// Marathi India
        public static final int LANGUAGE_WINDOWS_047C = 0x047C;// Mohawk Mohawk
        public static final int LANGUAGE_WINDOWS_0450 = 0x0450;// Mongolian (Cyrillic) Mongolia
        public static final int LANGUAGE_WINDOWS_0850 = 0x0850;// Mongolian (Traditional) People’s Republic of China
        public static final int LANGUAGE_WINDOWS_0461 = 0x0461;// Nepali Nepal
        public static final int LANGUAGE_WINDOWS_0414 = 0x0414;// Norwegian (Bokmal) Norway
        public static final int LANGUAGE_WINDOWS_0814 = 0x0814;// Norwegian (Nynorsk) Norway
        public static final int LANGUAGE_WINDOWS_0482 = 0x0482;// Occitan France
        public static final int LANGUAGE_WINDOWS_0448 = 0x0448;// Odia (formerly Oriya) India
        public static final int LANGUAGE_WINDOWS_0463 = 0x0463;// Pashto Afghanistan
        public static final int LANGUAGE_WINDOWS_0415 = 0x0415;// Polish Poland
        public static final int LANGUAGE_WINDOWS_0416 = 0x0416;// Portuguese Brazil
        public static final int LANGUAGE_WINDOWS_0816 = 0x0816;// Portuguese Portugal
        public static final int LANGUAGE_WINDOWS_0446 = 0x0446;// Punjabi India
        public static final int LANGUAGE_WINDOWS_046B = 0x046B;// Quechua Bolivia
        public static final int LANGUAGE_WINDOWS_086B = 0x086B;// Quechua Ecuador
        public static final int LANGUAGE_WINDOWS_0C6B = 0x0C6B;// Quechua Peru
        public static final int LANGUAGE_WINDOWS_0418 = 0x0418;// Romanian Romania
        public static final int LANGUAGE_WINDOWS_0417 = 0x0417;// Romansh Switzerland
        public static final int LANGUAGE_WINDOWS_0419 = 0x0419;// Russian Russia
        public static final int LANGUAGE_WINDOWS_243B = 0x243B;// Sami (Inari) Finland
        public static final int LANGUAGE_WINDOWS_103B = 0x103B;// Sami (Lule) Norway
        public static final int LANGUAGE_WINDOWS_143B = 0x143B;// Sami (Lule) Sweden
        public static final int LANGUAGE_WINDOWS_0C3B = 0x0C3B;// Sami (Northern) Finland
        public static final int LANGUAGE_WINDOWS_043B = 0x043B;// Sami (Northern) Norway
        public static final int LANGUAGE_WINDOWS_083B = 0x083B;// Sami (Northern) Sweden
        public static final int LANGUAGE_WINDOWS_203B = 0x203B;// Sami (Skolt) Finland
        public static final int LANGUAGE_WINDOWS_183B = 0x183B;// Sami (Southern) Norway
        public static final int LANGUAGE_WINDOWS_1C3B = 0x1C3B;// Sami (Southern) Sweden
        public static final int LANGUAGE_WINDOWS_044F = 0x044F;// Sanskrit India
        public static final int LANGUAGE_WINDOWS_1C1A = 0x1C1A;// Serbian (Cyrillic) Bosnia and Herzegovina
        public static final int LANGUAGE_WINDOWS_0C1A = 0x0C1A;// Serbian (Cyrillic) Serbia
        public static final int LANGUAGE_WINDOWS_181A = 0x181A;// Serbian (Latin) Bosnia and Herzegovina
        public static final int LANGUAGE_WINDOWS_081A = 0x081A;// Serbian (Latin) Serbia
        public static final int LANGUAGE_WINDOWS_046C = 0x046C;// Sesotho sa Leboa South Africa
        public static final int LANGUAGE_WINDOWS_0432 = 0x0432;// Setswana South Africa
        public static final int LANGUAGE_WINDOWS_045B = 0x045B;// Sinhala Sri Lanka
        public static final int LANGUAGE_WINDOWS_041B = 0x041B;// Slovak Slovakia
        public static final int LANGUAGE_WINDOWS_0424 = 0x0424;// Slovenian Slovenia
        public static final int LANGUAGE_WINDOWS_2C0A = 0x2C0A;// Spanish Argentina
        public static final int LANGUAGE_WINDOWS_400A = 0x400A;// Spanish Bolivia
        public static final int LANGUAGE_WINDOWS_340A = 0x340A;// Spanish Chile
        public static final int LANGUAGE_WINDOWS_240A = 0x240A;// Spanish Colombia
        public static final int LANGUAGE_WINDOWS_140A = 0x140A;// Spanish Costa Rica
        public static final int LANGUAGE_WINDOWS_1C0A = 0x1C0A;// Spanish Dominican Republic
        public static final int LANGUAGE_WINDOWS_300A = 0x300A;// Spanish Ecuador
        public static final int LANGUAGE_WINDOWS_440A = 0x440A;// Spanish El Salvador
        public static final int LANGUAGE_WINDOWS_100A = 0x100A;// Spanish Guatemala
        public static final int LANGUAGE_WINDOWS_480A = 0x480A;// Spanish Honduras
        public static final int LANGUAGE_WINDOWS_080A = 0x080A;// Spanish Mexico
        public static final int LANGUAGE_WINDOWS_4C0A = 0x4C0A;// Spanish Nicaragua
        public static final int LANGUAGE_WINDOWS_180A = 0x180A;// Spanish Panama
        public static final int LANGUAGE_WINDOWS_3C0A = 0x3C0A;// Spanish Paraguay
        public static final int LANGUAGE_WINDOWS_280A = 0x280A;// Spanish Peru
        public static final int LANGUAGE_WINDOWS_500A = 0x500A;// Spanish Puerto Rico
        public static final int LANGUAGE_WINDOWS_0C0A = 0x0C0A;// Spanish (Modern Sort) Spain
        public static final int LANGUAGE_WINDOWS_040A = 0x040A;// Spanish (Traditional Sort) Spain
        public static final int LANGUAGE_WINDOWS_540A = 0x540A;// Spanish United States
        public static final int LANGUAGE_WINDOWS_380A = 0x380A;// Spanish Uruguay
        public static final int LANGUAGE_WINDOWS_200A = 0x200A;// Spanish Venezuela
        public static final int LANGUAGE_WINDOWS_081D = 0x081D;// Sweden Finland
        public static final int LANGUAGE_WINDOWS_041D = 0x041D;// Swedish Sweden
        public static final int LANGUAGE_WINDOWS_045A = 0x045A;// Syriac Syria
        public static final int LANGUAGE_WINDOWS_0428 = 0x0428;// Tajik (Cyrillic) Tajikistan
        public static final int LANGUAGE_WINDOWS_085F = 0x085F;// Tamazight (Latin) Algeria
        public static final int LANGUAGE_WINDOWS_0449 = 0x0449;// Tamil India
        public static final int LANGUAGE_WINDOWS_0444 = 0x0444;// Tatar Russia
        public static final int LANGUAGE_WINDOWS_044A = 0x044A;// Telugu India
        public static final int LANGUAGE_WINDOWS_041E = 0x041E;// Thai Thailand
        public static final int LANGUAGE_WINDOWS_0451 = 0x0451;// Tibetan PRC
        public static final int LANGUAGE_WINDOWS_041F = 0x041F;// Turkish Turkey
        public static final int LANGUAGE_WINDOWS_0442 = 0x0442;// Turkmen Turkmenistan
        public static final int LANGUAGE_WINDOWS_0480 = 0x0480;// Uighur PRC
        public static final int LANGUAGE_WINDOWS_0422 = 0x0422;// Ukrainian Ukraine
        public static final int LANGUAGE_WINDOWS_042E = 0x042E;// Upper Sorbian Germany
        public static final int LANGUAGE_WINDOWS_0420 = 0x0420;// Urdu Islamic Republic of Pakistan
        public static final int LANGUAGE_WINDOWS_0843 = 0x0843;// Uzbek (Cyrillic) Uzbekistan
        public static final int LANGUAGE_WINDOWS_0443 = 0x0443;// Uzbek (Latin) Uzbekistan
        public static final int LANGUAGE_WINDOWS_042A = 0x042A;// Vietnamese Vietnam
        public static final int LANGUAGE_WINDOWS_0452 = 0x0452;// Welsh United Kingdom
        public static final int LANGUAGE_WINDOWS_0488 = 0x0488;// Wolof Senegal
        public static final int LANGUAGE_WINDOWS_0485 = 0x0485;// Yakut Russia
        public static final int LANGUAGE_WINDOWS_0478 = 0x0478;// Yi PRC
        public static final int LANGUAGE_WINDOWS_046A = 0x046A;// Yoruba Nigeria

        public static final int NAME_0 = 0;// Copyright notice.
        public static final int NAME_1 = 1;// Font Family name.
        public static final int NAME_2 = 2;// Font Subfamily name.
        public static final int NAME_3 = 3;// Unique font identifier.
        public static final int NAME_4 = 4;// Full font name that reflects all family and relevant subfamily descriptors.
        public static final int NAME_5 = 5;// Version string.
        public static final int NAME_6 = 6;// PostScript name for the font.
        public static final int NAME_7 = 7;// Trademark; this is used to save any trademark notice/information for this font.
        public static final int NAME_8 = 8;// Manufacturer Name.
        public static final int NAME_9 = 9;// Designer; name of the designer of the typeface.
        public static final int NAME_10 = 10;// Description; description of the typeface.
        public static final int NAME_11 = 11;// URL Vendor; URL of font vendor (with protocol, e.g., http://, ftp://).
        public static final int NAME_12 = 12;// URL Designer; URL of typeface designer (with protocol, e.g., http://, ftp://).
        public static final int NAME_13 = 13;// License Description; description of how the font may be legally used, or different example scenarios for licensed use.
        public static final int NAME_14 = 14;// License Info URL; URL where additional licensing information can be found.
        public static final int NAME_15 = 15;// Reserved.
        public static final int NAME_16 = 16;// Typographic Family name.
        public static final int NAME_17 = 17;// Typographic Subfamily name.
        public static final int NAME_18 = 18;// Compatible Full (Macintosh only).
        public static final int NAME_19 = 19;// Sample text.
        public static final int NAME_20 = 20;// PostScript CID findfont name
        public static final int NAME_21 = 21;// WWS Family Name.
        public static final int NAME_22 = 22;// WWS Subfamily Name.
        public static final int NAME_23 = 23;// Light Background Palette.
        public static final int NAME_24 = 24;// Dark Background Palette.
        public static final int NAME_25 = 25;// Variations PostScript Name Prefix.

        private final int mPlatformID;// Platform ID. Platform ID values 240 through 255 are reserved for user-defined platforms. This specification will never assign these values to a registered platform.
        private final int mEncodingID;// Platform-specific encoding ID.
        private final int mLanguageID;// Language ID.
        private final int mNameID;// Name ID.
        private final int mLength;// String length (in bytes).
        private final int mOffset;// String offset from start of storage area (in bytes).
        private final byte[] mData;

        public NameRecord(int platformID, int encodingID, int languageID, int nameID,
                          int length, int offset, byte[] data) {
            mPlatformID = platformID;
            mEncodingID = encodingID;
            mLanguageID = languageID;
            mNameID = nameID;
            mLength = length;
            mOffset = offset;
            mData = data;
        }

        /**
         * 获取平台ID
         *
         * @return 平台ID
         */
        public int getPlatformID() {
            return mPlatformID;
        }

        /**
         * 获取编码ID
         *
         * @return 编码ID
         */
        public int getEncodingID() {
            return mEncodingID;
        }

        /**
         * 获取语言ID（名字表格式0的语言ID必然小于0x8000，而名字表格式1中的语言ID可能大于或等于0x8000，此时需参照语言标签记录，以语言ID减去0x8000的结果作为List下标获取语言标签）
         *
         * @return 语言ID
         */
        public int getLanguageID() {
            return mLanguageID;
        }

        /**
         * 获取名字ID
         *
         * @return 名字ID
         */
        public int getNameID() {
            return mNameID;
        }

        /**
         * 获取字符串长度
         *
         * @return 长度
         */
        public int getLength() {
            return mLength;
        }

        /**
         * 获取字符串偏移（相对于名字表起始位置）
         *
         * @return 偏移
         */
        public int getOffset() {
            return mOffset;
        }

        /**
         * 获取字符串字节流
         *
         * @return 字节流
         */
        public byte[] getData() {
            return mData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NameRecord that = (NameRecord) o;
            return mPlatformID == that.mPlatformID &&
                    mEncodingID == that.mEncodingID &&
                    mLanguageID == that.mLanguageID &&
                    mNameID == that.mNameID &&
                    mLength == that.mLength &&
                    mOffset == that.mOffset &&
                    Arrays.equals(mData, that.mData);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(mPlatformID, mEncodingID, mLanguageID, mNameID, mLength, mOffset);
            result = 31 * result + Arrays.hashCode(mData);
            return result;
        }

        @Override
        public String toString() {
            return "NameRecord{" +
                    "platformID=" + mPlatformID +
                    ", encodingID=" + mEncodingID +
                    ", languageID=" + mLanguageID +
                    ", nameID=" + mNameID +
                    ", length=" + mLength +
                    ", offset=" + mOffset +
                    ", data=" + Arrays.toString(mData) +
                    '}';
        }
    }

    /**
     * 语言标志记录
     */
    public static class LangTagRecord {

        private final int mLength;// Language-tag string length (in bytes)
        private final int mOffset;// Language-tag string offset from start of storage area (in bytes).

        @SuppressWarnings("WeakerAccess")
        public LangTagRecord(int length, int offset) {
            mLength = length;
            mOffset = offset;
        }

        /**
         * 获取语言标记记录字符串长度
         *
         * @return 长度
         */
        public int getLength() {
            return mLength;
        }

        /**
         * 获取语言标记记录字符串起始偏移（相对于名字表的起始位置）
         *
         * @return 起始偏移
         */
        public int getOffset() {
            return mOffset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LangTagRecord that = (LangTagRecord) o;
            return mLength == that.mLength &&
                    mOffset == that.mOffset;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mLength, mOffset);
        }

        @Override
        public String toString() {
            return "LangTagRecord{" +
                    "length=" + mLength +
                    ", offset=" + mOffset +
                    '}';
        }
    }
}
