package am.util.opentype.tables;

import java.io.IOException;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * OS/2 and Windows Metrics Table
 */
@SuppressWarnings("unused")
public class OS2Table {
    private final int mVersion;
    private final int mAvgCharWidth;
    private final int mWeightClass;
    private final int mWidthClass;
    private final int mType;
    private final int mSubscriptXSize;
    private final int mSubscriptYSize;
    private final int mSubscriptXOffset;
    private final int mSubscriptYOffset;
    private final int mSuperscriptXSize;
    private final int mSuperscriptYSize;
    private final int mSuperscriptXOffset;
    private final int mSuperscriptYOffset;
    private final int mStrikeoutSize;
    private final int mStrikeoutPosition;
    private final int mFamilyClass;
    private final int[] mPanose;
    private final int mUnicodeRange1;
    private final int mUnicodeRange2;
    private final int mUnicodeRange3;
    private final int mUnicodeRange4;
    private final int mVendID;
    private final int mSelection;
    private final int mFirstCharIndex;
    private final int mLastCharIndex;
    private final boolean mLegacy;
    private final int mTypoAscender;
    private final int mTypoDescender;
    private final int mTypoLineGap;
    private final int mWinAscent;
    private final int mWinDescent;
    private final int mCodePageRange1;
    private final int mCodePageRange2;
    private final int mHeight;
    private final int mCapHeight;
    private final int mDefaultChar;
    private final int mBreakChar;
    private final int mMaxContext;
    private final int mLowerOpticalPointSize;
    private final int mUpperOpticalPointSize;

    public OS2Table(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_OS2)
            throw new IOException();
        reader.seek(record.getOffset());
        final int version = reader.readUnsignedShort();
        // Version 0
        final int xAvgCharWidth = reader.readShort();
        final int usWeightClass = reader.readUnsignedShort();
        final int usWidthClass = reader.readUnsignedShort();
        final int fsType = reader.readUnsignedShort();
        final int ySubscriptXSize = reader.readShort();
        final int ySubscriptYSize = reader.readShort();
        final int ySubscriptXOffset = reader.readShort();
        final int ySubscriptYOffset = reader.readShort();
        final int ySuperscriptXSize = reader.readShort();
        final int ySuperscriptYSize = reader.readShort();
        final int ySuperscriptXOffset = reader.readShort();
        final int ySuperscriptYOffset = reader.readShort();
        final int yStrikeoutSize = reader.readShort();
        final int yStrikeoutPosition = reader.readShort();
        final int sFamilyClass = reader.readShort();
        final int[] panose = new int[10];
        for (int i = 0; i < 10; i++) {
            panose[i] = reader.readUnsignedByte();
        }
        final int ulUnicodeRange1 = reader.readUnsignedInt();
        final int ulUnicodeRange2 = reader.readUnsignedInt();
        final int ulUnicodeRange3 = reader.readUnsignedInt();
        final int ulUnicodeRange4 = reader.readUnsignedInt();
        final int achVendID = reader.readInt();
        final int fsSelection = reader.readUnsignedShort();
        final int usFirstCharIndex = reader.readUnsignedShort();
        final int usLastCharIndex = reader.readUnsignedShort();
        final boolean legacy;
        final int sTypoAscender;
        final int sTypoDescender;
        final int sTypoLineGap;
        final int usWinAscent;
        final int usWinDescent;
        // Microsoft Version 0
        if (version <= 0 && record.getLength() == 68) {
            legacy = true;
            sTypoAscender = 0;
            sTypoDescender = 0;
            sTypoLineGap = 0;
            usWinAscent = -1;
            usWinDescent = -1;
        } else {
            legacy = false;
            sTypoAscender = reader.readShort();
            sTypoDescender = reader.readShort();
            sTypoLineGap = reader.readShort();
            usWinAscent = reader.readUnsignedShort();
            usWinDescent = reader.readUnsignedShort();
        }
        // Version 1
        final int ulCodePageRange1;
        final int ulCodePageRange2;
        if (version <= 0) {
            ulCodePageRange1 = -1;
            ulCodePageRange2 = -1;
        } else {
            ulCodePageRange1 = reader.readUnsignedInt();
            ulCodePageRange2 = reader.readUnsignedInt();
        }
        // Version 2/3/4
        final int sxHeight;
        final int sCapHeight;
        final int usDefaultChar;
        final int usBreakChar;
        final int usMaxContext;
        if (version <= 1) {
            sxHeight = 0;
            sCapHeight = 0;
            usDefaultChar = -1;
            usBreakChar = -1;
            usMaxContext = -1;
        } else {
            sxHeight = reader.readShort();
            sCapHeight = reader.readShort();
            usDefaultChar = reader.readUnsignedShort();
            usBreakChar = reader.readUnsignedShort();
            usMaxContext = reader.readUnsignedShort();
        }
        // Version 5
        final int usLowerOpticalPointSize;
        final int usUpperOpticalPointSize;
        if (version <= 4) {
            usLowerOpticalPointSize = -1;
            usUpperOpticalPointSize = -1;
        } else {
            usLowerOpticalPointSize = reader.readUnsignedShort();
            usUpperOpticalPointSize = reader.readUnsignedShort();
        }
        mVersion = version;
        mAvgCharWidth = xAvgCharWidth;
        mWeightClass = usWeightClass;
        mWidthClass = usWidthClass;
        mType = fsType;
        mSubscriptXSize = ySubscriptXSize;
        mSubscriptYSize = ySubscriptYSize;
        mSubscriptXOffset = ySubscriptXOffset;
        mSubscriptYOffset = ySubscriptYOffset;
        mSuperscriptXSize = ySuperscriptXSize;
        mSuperscriptYSize = ySuperscriptYSize;
        mSuperscriptXOffset = ySuperscriptXOffset;
        mSuperscriptYOffset = ySuperscriptYOffset;
        mStrikeoutSize = yStrikeoutSize;
        mStrikeoutPosition = yStrikeoutPosition;
        mFamilyClass = sFamilyClass;
        mPanose = panose;
        mUnicodeRange1 = ulUnicodeRange1;
        mUnicodeRange2 = ulUnicodeRange2;
        mUnicodeRange3 = ulUnicodeRange3;
        mUnicodeRange4 = ulUnicodeRange4;
        mVendID = achVendID;
        mSelection = fsSelection;
        mFirstCharIndex = usFirstCharIndex;
        mLastCharIndex = usLastCharIndex;
        mLegacy = legacy;
        mTypoAscender = sTypoAscender;
        mTypoDescender = sTypoDescender;
        mTypoLineGap = sTypoLineGap;
        mWinAscent = usWinAscent;
        mWinDescent = usWinDescent;
        mCodePageRange1 = ulCodePageRange1;
        mCodePageRange2 = ulCodePageRange2;
        mHeight = sxHeight;
        mCapHeight = sCapHeight;
        mDefaultChar = usDefaultChar;
        mBreakChar = usBreakChar;
        mMaxContext = usMaxContext;
        mLowerOpticalPointSize = usLowerOpticalPointSize;
        mUpperOpticalPointSize = usUpperOpticalPointSize;

        System.out.println("lallalall-----------------------------:" + toString());
    }

    public int getVersion() {
        return mVersion;
    }

    public int getAvgCharWidth() {
        return mAvgCharWidth;
    }

    public int getWeightClass() {
        return mWeightClass;
    }

    public int getWidthClass() {
        return mWidthClass;
    }

    public int getType() {
        return mType;
    }

    public int getSubscriptXSize() {
        return mSubscriptXSize;
    }

    public int getSubscriptYSize() {
        return mSubscriptYSize;
    }

    public int getSubscriptXOffset() {
        return mSubscriptXOffset;
    }

    public int getSubscriptYOffset() {
        return mSubscriptYOffset;
    }

    public int getSuperscriptXSize() {
        return mSuperscriptXSize;
    }

    public int getSuperscriptYSize() {
        return mSuperscriptYSize;
    }

    public int getSuperscriptXOffset() {
        return mSuperscriptXOffset;
    }

    public int getSuperscriptYOffset() {
        return mSuperscriptYOffset;
    }

    public int getStrikeoutSize() {
        return mStrikeoutSize;
    }

    public int getStrikeoutPosition() {
        return mStrikeoutPosition;
    }

    public int getFamilyClass() {
        return mFamilyClass;
    }

    public void getPanose(int[] panose) {
        if (panose == null)
            return;
        final int length = panose.length;
        for (int i = 0; i < length && i < 10; i++) {
            panose[i] = mPanose[i];
        }
    }

    public int getUnicodeRange1() {
        return mUnicodeRange1;
    }

    public int getUnicodeRange2() {
        return mUnicodeRange2;
    }

    public int getUnicodeRange3() {
        return mUnicodeRange3;
    }

    public int getUnicodeRange4() {
        return mUnicodeRange4;
    }

    public int getVendID() {
        return mVendID;
    }

    public int getSelection() {
        return mSelection;
    }

    public int getFirstCharIndex() {
        return mFirstCharIndex;
    }

    public int getLastCharIndex() {
        return mLastCharIndex;
    }

    public boolean isLegacy() {
        return mLegacy;
    }

    public int getTypoAscender() {
        return mTypoAscender;
    }

    public int getTypoDescender() {
        return mTypoDescender;
    }

    public int getTypoLineGap() {
        return mTypoLineGap;
    }

    public int getWinAscent() {
        return mWinAscent;
    }

    public int getWinDescent() {
        return mWinDescent;
    }

    public int getCodePageRange1() {
        return mCodePageRange1;
    }

    public int getCodePageRange2() {
        return mCodePageRange2;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getCapHeight() {
        return mCapHeight;
    }

    public int getDefaultChar() {
        return mDefaultChar;
    }

    public int getBreakChar() {
        return mBreakChar;
    }

    public int getMaxContext() {
        return mMaxContext;
    }

    public int getLowerOpticalPointSize() {
        return mLowerOpticalPointSize;
    }

    public int getUpperOpticalPointSize() {
        return mUpperOpticalPointSize;
    }
}
