package am.util.opentype;

import android.util.SparseArray;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 字体解析器
 * Created by Alex on 2018/9/5.
 */
@SuppressWarnings("unused")
public class OpenTypeParser {

    private static final int OTF = 0x00010000;// OpenType fonts that contain TrueType outlines
    private static final int OTTO = 0x4F54544F;// OpenType fonts containing CFF data (version 1 or 2)
    private static final int TTCF = 0x74746366;// An OpenType Font Collection (formerly known as TrueType Collection)

    private boolean mInvalid;// 无效字体文件
    private boolean mCollection;// 是否为字体集
    private OpenType mFont;
    private OpenTypeCollection mFonts;

    /**
     * 解析字体
     *
     * @param reader 字体数据读取器
     * @param tags   表集合
     */
    public void parse(OpenTypeReader reader, int... tags) {
        mInvalid = false;
        mCollection = false;
        mFont = null;
        mFonts = null;
        final int begin;
        try {
            reader.seek(0);
            begin = reader.readInt();
        } catch (Exception e) {
            mInvalid = true;
            return;
        }
        try {
            switch (begin) {
                default:
                    mInvalid = true;
                    break;
                case OTF:
                case OTTO:
                    mFont = parseOpenType(reader, 0, tags);
                    break;
                case TTCF:
                    mCollection = true;
                    mFonts = parseCollection(reader, tags);
                    break;
            }
        } catch (IOException e) {
            mInvalid = true;
        }
    }

    private OpenType parseOpenType(OpenTypeReader reader, long begin, int... tags)
            throws IOException {
        reader.seek(begin);
        final int sfntVersion = reader.readInt();
        final int numTables = reader.readUnsignedShort();
        final int searchRange = reader.readUnsignedShort();
        final int entrySelector = reader.readUnsignedShort();
        final int rangeShift = reader.readUnsignedShort();
        final SparseArray<TableRecord> records = new SparseArray<>();
        for (int i = 0; i < numTables; i++) {
            final int tableTag = reader.readInt();
            final int checkSum = reader.readUnsignedInt();
            final int offset = reader.readUnsignedInt();
            final int length = reader.readUnsignedInt();
            records.put(tableTag, new TableRecord(tableTag, checkSum, offset, length));
        }
        final OpenType ot = new OpenType(sfntVersion, numTables, searchRange, entrySelector,
                rangeShift, records);
        ot.parseTables(reader, tags);
        return ot;
    }

    /**
     * 判断是否为无效字体
     *
     * @return 是否为无效字体
     */
    public boolean isInvalid() {
        return mInvalid;
    }

    /**
     * 判断是否为字体集
     *
     * @return 是否为字体集
     */
    public boolean isCollection() {
        return mCollection;
    }

    /**
     * 获取解析后的字体对象
     *
     * @return 字体对象，在无效或者为字体集的情况下返回空
     */
    public OpenType getOpenType() {
        return mFont;
    }

    /**
     * 获取解析后的字体集对象
     *
     * @return 字体集对象，在无效或不为字体集的情况下返回空
     */
    public OpenTypeCollection getOpenTypeCollection() {
        return mFonts;
    }

    private OpenTypeCollection parseCollection(OpenTypeReader reader, int... tags)
            throws IOException {
        reader.seek(0);
        final int ttcTag = reader.readInt();
        final int majorVersion = reader.readUnsignedShort();
        final int minorVersion = reader.readUnsignedShort();
        final int numFonts = reader.readUnsignedInt();
        final int[] offsetTableOffsets = new int[numFonts];
        for (int i = 0; i < numFonts; i++) {
            offsetTableOffsets[i] = reader.readInt();
        }
        boolean DSIGTableEnable = false;
        int dsigLength = -1;
        int dsigOffset = -1;
        if (majorVersion == 2 && minorVersion == 0) {
            // TTC Header Version 2.0
            final int dsigTag = reader.readUnsignedInt();
            if (dsigTag == TableRecord.TAG_DSIG) {
                dsigLength = reader.readUnsignedInt();
                dsigOffset = reader.readUnsignedInt();
                if (dsigLength > 0 && dsigOffset > 0) {
                    DSIGTableEnable = true;
                }
            }
        }
        final ArrayList<OpenType> fonts = new ArrayList<>();
        for (int i = 0; i < numFonts; i++) {
            fonts.add(parseOpenType(reader, offsetTableOffsets[i], tags));
        }
        return new OpenTypeCollection(ttcTag, majorVersion, minorVersion, numFonts,
                offsetTableOffsets, DSIGTableEnable, dsigLength, dsigOffset, fonts);
    }
}
