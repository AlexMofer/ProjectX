package am.util.opentype.tables;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import am.util.opentype.FileOpenTypeReader;

/**
 * 命名表
 * Created by Alex on 2018/9/6.
 */
@SuppressWarnings("unused")
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
}
