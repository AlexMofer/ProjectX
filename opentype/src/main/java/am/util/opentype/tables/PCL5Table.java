package am.util.opentype.tables;

import java.io.IOException;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * PCL 5 Table
 * The PCLT table is strongly discouraged for OpenType™ fonts with TrueType outlines.
 * Extra information on many of these fields can be found in the HP PCL 5 Printer Language
 * Technical Reference Manual available from Hewlett-Packard Boise Printer Division.
 */
@SuppressWarnings("unused")
public class PCL5Table {

    private final int mMajorVersion;
    private final int mMinorVersion;
    private final int mFontNumber;
    private final int mPitch;
    private final int mXHeight;
    private final int mStyle;
    private final int mTypeFamily;
    private final int mCapHeight;
    private final int mSymbolSet;
    private final int[] mTypeface;
    private final int[] mCharacterComplement;
    private final int[] mFileName;
    private final int mStrokeWeight;
    private final int mWidthType;
    private final int mSerifStyle;

    public PCL5Table(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_PCLT)
            throw new IOException();
        reader.seek(record.getOffset());
        final int majorVersion = reader.readUnsignedShort();
        final int minorVersion = reader.readUnsignedShort();
        final int fontNumber = reader.readUnsignedInt();
        final int pitch = reader.readUnsignedShort();
        final int xHeight = reader.readUnsignedShort();
        final int style = reader.readUnsignedShort();
        final int typeFamily = reader.readUnsignedShort();
        final int capHeight = reader.readUnsignedShort();
        final int symbolSet = reader.readUnsignedShort();
        final int[] typeface = new int[16];
        for (int i = 0; i < typeface.length; i++) {
            typeface[i] = reader.read();
        }
        final int[] characterComplement = new int[8];
        for (int i = 0; i < characterComplement.length; i++) {
            characterComplement[i] = reader.read();
        }
        final int[] fileName = new int[6];
        for (int i = 0; i < fileName.length; i++) {
            fileName[i] = reader.read();
        }
        final int strokeWeight = reader.read();
        final int widthType = reader.read();
        final int serifStyle = reader.readUnsignedByte();
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
        mFontNumber = fontNumber;
        mPitch = pitch;
        mXHeight = xHeight;
        mStyle = style;
        mTypeFamily = typeFamily;
        mCapHeight = capHeight;
        mSymbolSet = symbolSet;
        mTypeface = typeface;
        mCharacterComplement = characterComplement;
        mFileName = fileName;
        mStrokeWeight = strokeWeight;
        mWidthType = widthType;
        mSerifStyle = serifStyle;
    }

    /**
     * Major Version
     *
     * @return Major Version
     */
    public int getMajorVersion() {
        return mMajorVersion;
    }

    /**
     * Minor Version
     *
     * @return Minor Version
     */
    public int getMinorVersion() {
        return mMinorVersion;
    }

    /**
     * This 32-bit number is segmented in two parts. The most significant bit indicates native
     * versus converted format. Only font vendors should create fonts with this bit zeroed.
     * The 7 next most significant bits are assigned by Hewlett-Packard Boise Printer Division
     * to major font vendors. The least significant 24 bits are assigned by the vendor.
     * Font vendors should attempt to insure that each of their fonts are marked with unique values.
     *
     * @return Font Number
     */
    public int getFontNumber() {
        return mFontNumber;
    }

    public int getPitch() {
        return mPitch;
    }

    public int getXHeight() {
        return mXHeight;
    }

    public int getStyle() {
        return mStyle;
    }

    public int getTypeFamily() {
        return mTypeFamily;
    }

    public int getCapHeight() {
        return mCapHeight;
    }

    public int getSymbolSet() {
        return mSymbolSet;
    }

    public int[] getTypeface() {
        return mTypeface;
    }

    public int[] getCharacterComplement() {
        return mCharacterComplement;
    }

    public int[] getFileName() {
        return mFileName;
    }

    public int getStrokeWeight() {
        return mStrokeWeight;
    }

    public int getWidthType() {
        return mWidthType;
    }

    public int getSerifStyle() {
        return mSerifStyle;
    }
}
