package am.util.opentype;

import java.util.List;

/**
 * OpenType字体集
 * Created by Alex on 2018/9/6.
 */
public class OpenTypeCollection {

    private final int mTtcTag;// Font Collection ID string: 'ttcf'
    private final int mMajorVersion;// Major version of the TTC Header, = 1 or 2.
    private final int mMinorVersion;// Minor version of the TTC Header, = 0.
    private final int mNumFonts;// Number of fonts in TTC
    private final int[] mOffsetTableOffsets;// Array of offsets to the OffsetTable for each font from the beginning of the file
    private final boolean mDSIGTableEnable;// Tag indicating that a DSIG table exists, 0x44534947 ('DSIG') (null if no signature)
    private final int mDSIGLength;// The length (in bytes) of the DSIG table (null if no signature)
    private final int mDSIGOffset;// The offset (in bytes) of the DSIG table from the beginning of the TTC file (null if no signature)
    private final List<OpenType> mFonts;

    public OpenTypeCollection(int ttcTag, int majorVersion, int minorVersion, int numFonts,
                              int[] offsetTableOffsets,
                              boolean DSIGTableEnable, int DSIGLength, int DSIGOffset,
                              List<OpenType> fonts) {
        mTtcTag = ttcTag;
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
        mNumFonts = numFonts;
        mOffsetTableOffsets = offsetTableOffsets;
        mDSIGTableEnable = DSIGTableEnable;
        mDSIGLength = DSIGLength;
        mDSIGOffset = DSIGOffset;
        mFonts = fonts;
    }
}
