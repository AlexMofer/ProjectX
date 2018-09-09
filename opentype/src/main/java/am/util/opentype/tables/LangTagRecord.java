package am.util.opentype.tables;

/**
 * 语言标志记录
 */
public class LangTagRecord {

    private final int mLength;// Language-tag string length (in bytes)
    private final int mOffset;// Language-tag string offset from start of storage area (in bytes).

    public LangTagRecord(int length, int offset) {
        mLength = length;
        mOffset = offset;
    }
}
