package am.util.opentype;

import android.util.SparseArray;

import java.io.IOException;

import am.util.opentype.tables.NameTable;

/**
 * OpenType字体
 * Created by Alex on 2018/9/6.
 */
public class OpenType {
    private final long mBegin;// 起始偏移量（该字段仅针对字体集有效，单个字体必然为0）
    private final int mSFNTVersion;// 0x00010000 or 0x4F54544F ('OTTO')
    private final int mNumTables;// Number of tables.
    private final int mSearchRange;// (Maximum power of 2 <= numTables) x 16.
    private final int mEntrySelector;// Log2(maximum power of 2 <= numTables).
    private final int mRangeShift;// NumTables x 16-searchRange.
    private final SparseArray<TableRecord> mRecords;
    private NameTable mName;

    public OpenType(long begin, int sfntVersion, int numTables, int searchRange, int entrySelector,
                    int rangeShift, SparseArray<TableRecord> records) {
        mBegin = begin;
        mSFNTVersion = sfntVersion;
        mNumTables = numTables;
        mSearchRange = searchRange;
        mEntrySelector = entrySelector;
        mRangeShift = rangeShift;
        mRecords = records;
    }

    /**
     * 解析表
     *
     * @param reader 字体数据读取器
     * @param tags   表集合
     */
    public void parseTables(OpenTypeReader reader, int... tags) throws IOException {
        if (tags == null || tags.length <= 0 || mRecords == null)
            return;
        for (int tag : tags) {
            final TableRecord record = mRecords.get(tag);
            if (record == null)
                continue;// 不包含所需要解析的表
            switch (record.getTableTag()) {
                default:
                    // 暂不支持的表
                    break;
                case TableRecord.TAG_NAME:
                    mName = OpenTypeParser.parseNameTable(reader, mBegin, record);
                    break;
            }
        }
    }

    /**
     * 获取起始偏移（该字段仅针对字体集有效，单个字体必然为0）
     *
     * @return 偏移
     */
    public long getBegin() {
        return mBegin;
    }

    /**
     * sfnt
     *
     * @return sfnt
     */
    public int getSFNTVersion() {
        return mSFNTVersion;
    }

    /**
     * 获取表数目
     *
     * @return 表数目
     */
    public int getTablesSize() {
        return mNumTables;
    }

    /**
     * 获取搜索范围
     *
     * @return 搜索范围(Maximum power of 2 < = numTables) x 16.
     */
    public int getSearchRange() {
        return mSearchRange;
    }

    /**
     * 获取条目选择器
     *
     * @return 条目选择器 Log2(maximum power of 2 <= numTables).
     */
    public int getEntrySelector() {
        return mEntrySelector;
    }

    /**
     * 获取范围转换
     *
     * @return 范围转换 NumTables x 16-searchRange.
     */
    public int getRangeShift() {
        return mRangeShift;
    }

    /**
     * 判断是否包含CFF数据（版本1或者2）
     *
     * @return 是否包含CFF数据
     */
    public boolean containsCFF() {
        return mSFNTVersion == 0x4F54544F;
    }

    /**
     * 判断是否包含表
     *
     * @param tag 表唯一标签
     * @return 是否包含表
     */
    public boolean containsTable(int tag) {
        return mRecords != null && mRecords.get(tag) != null;
    }

    /**
     * 获取表记录集
     *
     * @return 表记录集
     */
    public SparseArray<TableRecord> getTableRecords() {
        return mRecords;
    }

    /**
     * 获取表记录
     *
     * @param tag 表唯一标志
     * @return 表记录，不包含该表时返回空
     */
    public TableRecord getTableRecord(int tag) {
        return mRecords == null ? null : mRecords.get(tag);
    }

    /**
     * 获取命名表
     *
     * @return 命名表，不包含、未解析或解析出错的情况下为空
     */
    public NameTable getNamingTable() {
        return mName;
    }
}
