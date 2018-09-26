package am.util.opentype;

import android.util.SparseArray;

import java.io.IOException;

import am.util.opentype.tables.CharacterMappingTable;
import am.util.opentype.tables.GlyphTable;
import am.util.opentype.tables.HeaderTable;
import am.util.opentype.tables.HorizontalHeaderTable;
import am.util.opentype.tables.HorizontalMetricsTable;
import am.util.opentype.tables.IndexToLocationTable;
import am.util.opentype.tables.KerningTable;
import am.util.opentype.tables.MaximumProfileTable;
import am.util.opentype.tables.NamingTable;
import am.util.opentype.tables.OS2Table;
import am.util.opentype.tables.PCL5Table;
import am.util.opentype.tables.PostScriptTable;

/**
 * OpenType字体
 * Created by Alex on 2018/9/6.
 */
@SuppressWarnings("unused")
public class OpenType {
    private final int mSFNTVersion;// 0x00010000 or 0x4F54544F ('OTTO')
    private final int mNumTables;// Number of tables.
    private final int mSearchRange;// (Maximum power of 2 <= numTables) x 16.
    private final int mEntrySelector;// Log2(maximum power of 2 <= numTables).
    private final int mRangeShift;// NumTables x 16-searchRange.
    private final SparseArray<TableRecord> mRecords;
    private NamingTable mName;
    private OS2Table mOS2;
    private HeaderTable mHead;
    private HorizontalHeaderTable mHhea;
    private MaximumProfileTable mMaxp;
    private PostScriptTable mPost;
    private HorizontalMetricsTable mHmtx;
    private CharacterMappingTable mCmap;
    private IndexToLocationTable mLoca;
    private PCL5Table mPclt;
    private GlyphTable mGlyf;
    private KerningTable mKern;

    public OpenType(int sfntVersion, int numTables, int searchRange, int entrySelector,
                    int rangeShift, SparseArray<TableRecord> records) {
        mSFNTVersion = sfntVersion;
        mNumTables = numTables;
        mSearchRange = searchRange;
        mEntrySelector = entrySelector;
        mRangeShift = rangeShift;
        mRecords = records;
    }

    /**
     * 解析表
     *
     * @param reader 字体数据读取器
     * @param tags   表集合
     */
    @SuppressWarnings("all")
    public void parseTables(OpenTypeReader reader, int... tags) throws IOException {
        if (tags == null || tags.length <= 0 || mRecords == null)
            return;
        for (int tag : tags) {
            final TableRecord record = mRecords.get(tag);
            if (record == null)
                continue;// 不包含所需要解析的表
            switch (record.getTableTag()) {
                default:
                    // 暂不支持的表
                    break;
                // Required Tables
                case TableRecord.TAG_CMAP:
                    mCmap = new CharacterMappingTable(reader, record);
                    break;
                case TableRecord.TAG_HEAD:
                    mHead = new HeaderTable(reader, record);
                    break;
                case TableRecord.TAG_HHEA:
                    mHhea = new HorizontalHeaderTable(reader, record);
                    break;
                case TableRecord.TAG_HMTX:
                    if (mHhea != null && mMaxp != null) {
                        final int numberOfHMetrics = mHhea.getNumberOfHMetrics();
                        final int numGlyphs = mMaxp.getNumGlyphs();
                        mHmtx = new HorizontalMetricsTable(reader, record,
                                numberOfHMetrics, numGlyphs);
                    }
                    break;
                case TableRecord.TAG_MAXP:
                    mMaxp = new MaximumProfileTable(reader, record);
                    break;
                case TableRecord.TAG_NAME:
                    mName = new NamingTable(reader, record);
                    break;
                case TableRecord.TAG_OS2:
                    mOS2 = new OS2Table(reader, record);
                    break;
                case TableRecord.TAG_POST:
                    mPost = new PostScriptTable(reader, record);
                    break;
                // Tables Related to TrueType Outlines
                case TableRecord.TAG_CVT:
                case TableRecord.TAG_FPGM:
                    // 暂不支持的表
                    break;
                case TableRecord.TAG_GLYF:
                    mGlyf = new GlyphTable(reader, record);
                    break;
                case TableRecord.TAG_LOCA:
                    if (mHead != null && mMaxp != null) {
                        mLoca = new IndexToLocationTable(reader, record,
                                mHead.getIndexToLocFormat(), mMaxp.getNumGlyphs());
                    }
                    break;
                case TableRecord.TAG_PREP:
                case TableRecord.TAG_GASP:
                    // 暂不支持的表
                    break;
                // Tables Related to CFF Outlines
                case TableRecord.TAG_CFF:
                case TableRecord.TAG_CFF2:
                case TableRecord.TAG_VORG:
                    // 暂不支持的表
                    break;
                // Table Related to SVG Outlines
                case TableRecord.TAG_SVG:
                    // 暂不支持的表
                    break;
                // Tables Related to Bitmap Glyphs
                case TableRecord.TAG_EBDT:
                case TableRecord.TAG_EBLC:
                case TableRecord.TAG_EBSC:
                case TableRecord.TAG_CBDT:
                case TableRecord.TAG_CBLC:
                case TableRecord.TAG_SBIX:
                    // 暂不支持的表
                    break;
                // Advanced Typographic Tables
                case TableRecord.TAG_BASE:
                case TableRecord.TAG_GDEF:
                case TableRecord.TAG_GPOS:
                case TableRecord.TAG_GSUB:
                case TableRecord.TAG_JSTF:
                case TableRecord.TAG_MATH:
                    // 暂不支持的表
                    break;
                // Tables used for OpenType Font Variations
                case TableRecord.TAG_AVAR:
                case TableRecord.TAG_CVAR:
                case TableRecord.TAG_FVAR:
                case TableRecord.TAG_GVAR:
                case TableRecord.TAG_HVAR:
                case TableRecord.TAG_MVAR:
                case TableRecord.TAG_STAT:
                case TableRecord.TAG_VVAR:
                    // 暂不支持的表
                    break;
                // Tables Related to Color Fonts
                case TableRecord.TAG_COLR:
                case TableRecord.TAG_CPAL:
                    // 暂不支持的表
                    break;
                // Other OpenType Tables
                case TableRecord.TAG_HDMX:
                    // 暂不支持的表
                    break;
                case TableRecord.TAG_KERN:
                    mKern = new KerningTable(reader, record);
                    break;
                case TableRecord.TAG_LTSH:
                case TableRecord.TAG_MERG:
                case TableRecord.TAG_META:
                    // 暂不支持的表
                    break;
                case TableRecord.TAG_PCLT:
                    mPclt = new PCL5Table(reader, record);
                    break;
                case TableRecord.TAG_VDMX:
                case TableRecord.TAG_VHEA:
                case TableRecord.TAG_VMTX:
                    // 暂不支持的表
                    break;
            }
        }
    }

    /**
     * sfnt
     *
     * @return sfnt
     */
    public int getSFNTVersion() {
        return mSFNTVersion;
    }

    /**
     * 获取表数目
     *
     * @return 表数目
     */
    public int getTablesSize() {
        return mNumTables;
    }

    /**
     * 获取搜索范围
     *
     * @return 搜索范围(Maximum power of 2 < = numTables) x 16.
     */
    public int getSearchRange() {
        return mSearchRange;
    }

    /**
     * 获取条目选择器
     *
     * @return 条目选择器 Log2(maximum power of 2 <= numTables).
     */
    public int getEntrySelector() {
        return mEntrySelector;
    }

    /**
     * 获取范围转换
     *
     * @return 范围转换 NumTables x 16-searchRange.
     */
    public int getRangeShift() {
        return mRangeShift;
    }

    /**
     * 判断是否包含CFF数据（版本1或者2）
     *
     * @return 是否包含CFF数据
     */
    public boolean containsCFF() {
        return mSFNTVersion == 0x4F54544F;
    }

    /**
     * 判断是否包含表
     *
     * @param tag 表唯一标签
     * @return 是否包含表
     */
    public boolean containsTable(int tag) {
        return mRecords != null && mRecords.get(tag) != null;
    }

    /**
     * 获取表记录集
     *
     * @return 表记录集
     */
    public SparseArray<TableRecord> getTableRecords() {
        return mRecords;
    }

    /**
     * 获取表记录
     *
     * @param tag 表唯一标志
     * @return 表记录，不包含该表时返回空
     */
    public TableRecord getTableRecord(int tag) {
        return mRecords == null ? null : mRecords.get(tag);
    }

    /**
     * 获取命名表
     *
     * @return 命名表，不包含、未解析或解析出错的情况下为空
     */
    public NamingTable getNamingTable() {
        return mName;
    }

    /**
     * 获取OS/2表
     *
     * @return OS/2表
     */
    public OS2Table getOS2Table() {
        return mOS2;
    }

    /**
     * 获取首表
     *
     * @return 首表
     */
    public HeaderTable getHeadTable() {
        return mHead;
    }

    /**
     * 获取Horizontal Header Table
     *
     * @return Horizontal Header Table
     */
    public HorizontalHeaderTable getHorizontalHeaderTable() {
        return mHhea;
    }

    /**
     * 获取Maximum Profile Table
     *
     * @return Maximum Profile Table
     */
    public MaximumProfileTable getMaximumProfileTable() {
        return mMaxp;
    }

    /**
     * 获取PostScript Table
     *
     * @return PostScript Table
     */
    public PostScriptTable getPostScriptTable() {
        return mPost;
    }

    /**
     * 获取Horizontal Metrics Table
     *
     * @return Horizontal Metrics Table
     */
    public HorizontalMetricsTable getHorizontalMetricsTable() {
        return mHmtx;
    }

    /**
     * 获取Character Mapping Table
     *
     * @return Character Mapping Table
     */
    public CharacterMappingTable getCharacterMappingTable() {
        return mCmap;
    }

    /**
     * 获取Glyph Table
     *
     * @return Glyph Table
     */
    public GlyphTable getGlyphTable() {
        return mGlyf;
    }

    /**
     * 获取Index To Location Table
     *
     * @return Index To Location Table
     */
    public IndexToLocationTable getIndexToLocationTable() {
        return mLoca;
    }

    /**
     * 获取PCL 5 Table
     *
     * @return PCL 5 Table
     */
    public PCL5Table getPCL5Table() {
        return mPclt;
    }

    /**
     * 获取Kerning Table
     *
     * @return Kerning Table
     */
    public KerningTable getKerningTable() {
        return mKern;
    }
}
