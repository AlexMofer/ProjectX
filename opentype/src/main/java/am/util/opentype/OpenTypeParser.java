package am.util.opentype;

import android.util.SparseArray;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 字体解析器
 * Created by Alex on 2018/9/5.
 */
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
     * @param reader 数据源
     */
    public void parse(OpenTypeReader reader) {
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
                    mFont = parseOpenType(reader, 0);
                    break;
                case TTCF:
                    mCollection = true;
                    mFonts = parseCollection(reader);
                    break;
            }
        } catch (IOException e) {
            mInvalid = true;
        }
    }

    private OpenType parseOpenType(OpenTypeReader reader, long begin) throws IOException {
        reader.seek(begin);
        final int sfntVersion = reader.readInt();
        final int numTables = reader.readUnsignedShort();
        final int searchRange = reader.readUnsignedShort();
        final int entrySelector = reader.readUnsignedShort();
        final int rangeShift = reader.readUnsignedShort();
        final SparseArray<TableRecordEntry> entries = new SparseArray<>();
        for (int i = 0; i < numTables; i++) {
            final int tableTag = reader.readInt();
            final int checkSum = reader.readUnsignedInt();
            final int offset = reader.readUnsignedInt();
            final int length = reader.readUnsignedInt();
            entries.put(tableTag, new TableRecordEntry(tableTag, checkSum, offset, length));
        }
        return new OpenType(begin, sfntVersion, numTables, searchRange, entrySelector,
                rangeShift, entries);
    }

    private OpenTypeCollection parseCollection(OpenTypeReader reader) throws IOException {
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
            if (dsigTag == TableRecordEntry.TAG_DSIG) {
                dsigLength = reader.readUnsignedInt();
                dsigOffset = reader.readUnsignedInt();
                if (dsigLength > 0 && dsigOffset > 0) {
                    DSIGTableEnable = true;
                }
            }
        }
        final ArrayList<OpenType> fonts = new ArrayList<>();
        for (int i = 0; i < numFonts; i++) {
            fonts.add(parseOpenType(reader, offsetTableOffsets[i]));
        }
        return new OpenTypeCollection(ttcTag, majorVersion, minorVersion, numFonts,
                offsetTableOffsets, DSIGTableEnable, dsigLength, dsigOffset, fonts);
    }

    public boolean isInvalid() {
        return mInvalid;
    }

    public boolean isCollection() {
        return mCollection;
    }

    public OpenType getOpenType() {
        return mFont;
    }

    public OpenTypeCollection getOpenTypeCollection() {
        return mFonts;
    }
}
