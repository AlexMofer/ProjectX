package am.util.opentype.tables;

import java.util.List;

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

    public NameTable(int format, int count, int stringOffset, List<NameRecord> nameRecords,
                     List<LangTagRecord> langTagRecords, List<String> langTags) {
        mFormat = format;
        mCount = count;
        mStringOffset = stringOffset;
        mNameRecords = nameRecords;
        mLangTagRecords = langTagRecords;
        mLangTags = langTags;
    }

    /**
     * 获取语言标签
     * 语言ID大于等于0x8000时，匹配一个语言标签
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
