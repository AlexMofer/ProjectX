package am.util.opentype.tables;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import am.util.opentype.FileOpenTypeReader;

/**
 * 命名表
 * Created by Alex on 2018/9/6.
 */
public class NameTable {

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

    public NameTable(int format, int count, int stringOffset, List<NameRecord> nameRecords,
                     List<LangTagRecord> langTagRecords, List<String> langTags) {
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
        System.out.println("lallal----------------------------------------------------------------------------------------------------------------------------------------------");
        for (NameRecord record : mNameRecords) {
            final byte[] data = record.getData();
            if (data == null || data.length <= 0)
                continue;
            final int platformID = record.getPlatformID();
            final int encodingID = record.getEncodingID();
            final int languageID = record.getLanguageID();
            final String text;
            if (platformID == NameRecord.PLATFORM_WINDOWS) {
                // TODO
                if ((encodingID == NameRecord.ENCODING_WINDOWS_0 ||
                        encodingID == NameRecord.ENCODING_WINDOWS_1) &&
                        languageID == NameRecord.LANGUAGE_WINDOWS_0409) {
                    text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                } else {
                    if (data[0] == 0) {
                        text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                    } else {
                        text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                    }
                }

            } else if (platformID == NameRecord.PLATFORM_MACINTOSH) {
                // TODO
                if (encodingID == NameRecord.ENCODING_MACINTOSH_0 &&
                        languageID == NameRecord.LANGUAGE_MACINTOSH_0) {
                    text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                } else {
                    if (data[0] == 0) {
                        text = new String(data, FileOpenTypeReader.CHARSET_UTF_16BE);
                    } else {
                        text = new String(data, FileOpenTypeReader.CHARSET_ISO_8859_15);
                    }
                }
            } else {
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
}
