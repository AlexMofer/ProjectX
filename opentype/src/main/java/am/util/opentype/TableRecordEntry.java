package am.util.opentype;

/**
 * 表记录条目
 * Created by Alex on 2018/9/5.
 */
public class TableRecordEntry {

    public static final int TAG_DSIG = 0x44534947;// DSIG table

    private final int mTableTag;// Table identifier.
    private final int mCheckSum;// CheckSum for this table.
    private final int mOffset;// Offset from beginning of TrueType font file.
    private final int mLength;// Length of this table.

    public TableRecordEntry(int tableTag, int checkSum, int offset, int length) {
        mTableTag = tableTag;
        mCheckSum = checkSum;
        mOffset = offset;
        mLength = length;
    }

    public int getTableTag() {
        return mTableTag;
    }

    public int getCheckSum() {
        return mCheckSum;
    }

    public int getOffset() {
        return mOffset;
    }

    public int getLength() {
        return mLength;
    }
}
