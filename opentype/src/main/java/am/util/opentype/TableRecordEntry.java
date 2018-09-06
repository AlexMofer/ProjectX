package am.util.opentype;

/**
 * 表记录条目
 * Created by Alex on 2018/9/5.
 */
@SuppressWarnings("all")
public class TableRecordEntry {

    // Required Tables
    // Whether TrueType or CFF outlines are used in an OpenType font,
    // the following tables are required for the font to function correctly:
    public static final int TAG_CMAP = 0x636D6170;// cmap, Character to glyph mapping
    public static final int TAG_HEAD = 0x68656164;// head, Font header
    public static final int TAG_HHEA = 0x68686561;// hhea, Horizontal header
    public static final int TAG_HMTX = 0x686D7478;// hmtx, Horizontal metrics
    public static final int TAG_MAXP = 0x6D617870;// maxp, Maximum profile
    public static final int TAG_NAME = 0x6E616D65;// name, Naming table
    public static final int TAG_OS2 = 0x4F532F32;// OS/2, OS/2 and Windows specific metrics
    public static final int TAG_POST = 0x706F7374;// post, PostScript information
    // Tables Related to TrueType Outlines
    // For OpenType fonts based on TrueType outlines, the following tables are used:
    public static final int TAG_CVT = 0x63767420;// cvt , Control Value Table (optional table)
    public static final int TAG_FPGM = 0x6670676D;// fpgm, Font program (optional table)
    public static final int TAG_GLYF = 0x676c7966;// glyf, Glyph data
    public static final int TAG_LOCA = 0x6C6F6361;// loca, Index to location
    public static final int TAG_PREP = 0x70726570;// prep, CVT Program (optional table)
    public static final int TAG_GASP = 0x67617370;// gasp, Grid-fitting/Scan-conversion (optional table)
    // Tables Related to CFF Outlines
    // For OpenType fonts based on CFF outlines, the following tables are used:
    public static final int TAG_CFF = 0x43464620;// CFF , Compact Font Format 1.0
    public static final int TAG_CFF2 = 0x43464632;// CFF2, Compact Font Format 2.0
    public static final int TAG_VORG = 0x564F5247;// VORG, Vertical Origin (optional table)
    // Table Related to SVG Outlines
    public static final int TAG_SVG = 0x53564720;// SVG , The SVG (Scalable Vector Graphics) table
    // Tables Related to Bitmap Glyphs
    public static final int TAG_EBDT = 0x45424454;// EBDT, Embedded bitmap data
    public static final int TAG_EBLC = 0x45424C43;// EBLC, Embedded bitmap location data
    public static final int TAG_EBSC = 0x45425343;// EBSC, Embedded bitmap scaling data
    public static final int TAG_CBDT = 0x43424454;// CBDT, Color bitmap data
    public static final int TAG_CBLC = 0x43424C43;// CBLC, Color bitmap location data
    public static final int TAG_SBIX = 0x73626978;// sbix, Standard bitmap graphics
    // Advanced Typographic Tables
    // Several optional tables support advanced typographic functions:
    public static final int TAG_BASE = 0x42415345;// BASE, Baseline data
    public static final int TAG_GDEF = 0x47444546;// GDEF, Glyph definition data
    public static final int TAG_GPOS = 0x47504F53;// GPOS, Glyph positioning data
    public static final int TAG_GSUB = 0x47535542;// GSUB, Glyph substitution data
    public static final int TAG_JSTF = 0x4A535446;// JSTF, Justification data
    public static final int TAG_MATH = 0x4D415448;// MATH, Math layout data
    // Tables used for OpenType Font Variations
    public static final int TAG_AVAR = 0x61766172;// avar, Axis variations
    public static final int TAG_CVAR = 0x63766172;// cvar, CVT variations (TrueType outlines only)
    public static final int TAG_FVAR = 0x66766172;// fvar, Font variations
    public static final int TAG_GVAR = 0x67766172;// gvar, Glyph variations (TrueType outlines only)
    public static final int TAG_HVAR = 0x48564152;// HVAR, Horizontal metrics variations
    public static final int TAG_MVAR = 0x4D564152;// MVAR, Metrics variations
    public static final int TAG_STAT = 0x53544154;// STAT, Style attributes (required for variable fonts, optional for non-variable fonts)
    public static final int TAG_VVAR = 0x56564152;// VVAR, Vertical metrics variations
    // Tables Related to Color Fonts
    public static final int TAG_COLR = 0x434F4C52;// COLR, Color table
    public static final int TAG_CPAL = 0x4350414C;// CPAL, Color palette table
    // Other OpenType Tables
    public static final int TAG_DSIG = 0x44534947;// DSIG, Digital signature
    public static final int TAG_HDMX = 0x68646D78;// hdmx, Horizontal device metrics
    public static final int TAG_KERN = 0x6B65726E;// kern, Kerning
    public static final int TAG_LTSH = 0x4C545348;// LTSH, Linear threshold data
    public static final int TAG_MERG = 0x4D455247;// MERG, Merge
    public static final int TAG_META = 0x6D657461;// meta, Metadata
    public static final int TAG_PCLT = 0x50434C54;// PCLT, PCL 5 data
    public static final int TAG_VDMX = 0x56444D58;// VDMX, Vertical device metrics
    public static final int TAG_VHEA = 0x76686561;// vhea, Vertical Metrics header
    public static final int TAG_VMTX = 0x766D7478;// vmtx, Vertical Metrics

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

    /**
     * 获取表ID
     *
     * @return ID
     */
    public int getTableTag() {
        return mTableTag;
    }

    /**
     * 获取校验码
     *
     * @return 校验码
     */
    public int getCheckSum() {
        return mCheckSum;
    }

    /**
     * 获取偏移量
     *
     * @return 偏移量，注意常规字体与字体集的区别，常规字体为从第0个字节开始，而字体集则需要加上字体所处位置的偏移量
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * 获取长度
     *
     * @return 长度
     */
    public int getLength() {
        return mLength;
    }
}
