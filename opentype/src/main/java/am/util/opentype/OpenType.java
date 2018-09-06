package am.util.opentype;

import android.util.SparseArray;

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
    private final SparseArray<TableRecordEntry> mEntries;

    public OpenType(long begin, int sfntVersion, int numTables, int searchRange, int entrySelector,
                    int rangeShift, SparseArray<TableRecordEntry> entries) {
        mBegin = begin;
        mSFNTVersion = sfntVersion;
        mNumTables = numTables;
        mSearchRange = searchRange;
        mEntrySelector = entrySelector;
        mRangeShift = rangeShift;
        mEntries = entries;
    }

    public long getBegin() {
        return mBegin;
    }

    public int getSFNTVersion() {
        return mSFNTVersion;
    }

    public int getNumTables() {
        return mNumTables;
    }

    public int getSearchRange() {
        return mSearchRange;
    }

    public int getEntrySelector() {
        return mEntrySelector;
    }

    public int getRangeShift() {
        return mRangeShift;
    }

    public boolean containsCFF() {
        return mSFNTVersion == 0x4F54544F;
    }
}
