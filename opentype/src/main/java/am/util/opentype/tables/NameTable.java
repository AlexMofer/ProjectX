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
}
