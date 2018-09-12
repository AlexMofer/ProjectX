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
}
