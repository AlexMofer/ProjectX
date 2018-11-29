/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.util.opentype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import am.util.opentype.tables.BaseTable;
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
    private final Map<Integer, TableRecord> mRecords;
    private final ArrayList<TableRecord> mRecordArray = new ArrayList<>();
    private CharacterMappingTable mCmap;
    private HeaderTable mHead;
    private HorizontalHeaderTable mHhea;
    private HorizontalMetricsTable mHmtx;
    private MaximumProfileTable mMaxp;
    private NamingTable mName;
    private OS2Table mOS2;
    private PostScriptTable mPost;
    private BaseTable mCvt;
    private BaseTable mFpgm;
    private GlyphTable mGlyf;
    private IndexToLocationTable mLoca;
    private BaseTable mPrep;
    private BaseTable mGasp;
    private BaseTable mCff;
    private BaseTable mCff2;
    private BaseTable mVorg;
    private BaseTable mSvg;
    private BaseTable mEbdt;
    private BaseTable mEblc;
    private BaseTable mEbsc;
    private BaseTable mCbdt;
    private BaseTable mCblc;
    private BaseTable mSbix;
    private BaseTable mBase;
    private BaseTable mGdef;
    private BaseTable mGpos;
    private BaseTable mGsub;
    private BaseTable mJstf;
    private BaseTable mMath;
    private BaseTable mAvar;
    private BaseTable mCvar;
    private BaseTable mFvar;
    private BaseTable mGvar;
    private BaseTable mHvar;
    private BaseTable mMvar;
    private BaseTable mStat;
    private BaseTable mVvar;
    private BaseTable mColr;
    private BaseTable mCpal;
    private BaseTable mDsig;
    private BaseTable mHdmx;
    private KerningTable mKern;
    private BaseTable mLtsh;
    private BaseTable mMerg;
    private BaseTable mMeta;
    private PCL5Table mPclt;
    private BaseTable mVdmx;
    private BaseTable mVhea;
    private BaseTable mVmtx;

    public OpenType(int sfntVersion, int numTables, int searchRange, int entrySelector,
                    int rangeShift, Map<Integer, TableRecord> records) {
        mSFNTVersion = sfntVersion;
        mNumTables = numTables;
        mSearchRange = searchRange;
        mEntrySelector = entrySelector;
        mRangeShift = rangeShift;
        mRecords = records;
        if (records != null)
            mRecordArray.addAll(records.values());
    }

    /**
     * 解析表
     *
     * @param reader 字体数据读取器
     * @param tags   表集合
     * @throws IOException 读写错误
     */
    // TODO: 2018/10/21 完成剩余的表支持
    @SuppressWarnings("WeakerAccess")
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
                    mCvt = null;
                    break;
                case TableRecord.TAG_FPGM:
                    mFpgm = null;
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
                    mPrep = null;
                    break;
                case TableRecord.TAG_GASP:
                    mGasp = null;
                    break;
                // Tables Related to CFF Outlines
                case TableRecord.TAG_CFF:
                    mCff = null;
                    break;
                case TableRecord.TAG_CFF2:
                    mCff2 = null;
                    break;
                case TableRecord.TAG_VORG:
                    mVorg = null;
                    break;
                // Table Related to SVG Outlines
                case TableRecord.TAG_SVG:
                    mSvg = null;
                    break;
                // Tables Related to Bitmap Glyphs
                case TableRecord.TAG_EBDT:
                    mEbdt = null;
                    break;
                case TableRecord.TAG_EBLC:
                    mEblc = null;
                    break;
                case TableRecord.TAG_EBSC:
                    mEbsc = null;
                    break;
                case TableRecord.TAG_CBDT:
                    mCbdt = null;
                    break;
                case TableRecord.TAG_CBLC:
                    mCblc = null;
                    break;
                case TableRecord.TAG_SBIX:
                    mSbix = null;
                    break;
                // Advanced Typographic Tables
                case TableRecord.TAG_BASE:
                    mBase = null;
                    break;
                case TableRecord.TAG_GDEF:
                    mGdef = null;
                    break;
                case TableRecord.TAG_GPOS:
                    mGpos = null;
                    break;
                case TableRecord.TAG_GSUB:
                    mGsub = null;
                    break;
                case TableRecord.TAG_JSTF:
                    mJstf = null;
                    break;
                case TableRecord.TAG_MATH:
                    mMath = null;
                    break;
                // Tables used for OpenType Font Variations
                case TableRecord.TAG_AVAR:
                    mAvar = null;
                    break;
                case TableRecord.TAG_CVAR:
                    mCvar = null;
                    break;
                case TableRecord.TAG_FVAR:
                    mFvar = null;
                    break;
                case TableRecord.TAG_GVAR:
                    mGvar = null;
                    break;
                case TableRecord.TAG_HVAR:
                    mHvar = null;
                    break;
                case TableRecord.TAG_MVAR:
                    mMvar = null;
                    break;
                case TableRecord.TAG_STAT:
                    mStat = null;
                    break;
                case TableRecord.TAG_VVAR:
                    mVvar = null;
                    break;
                // Tables Related to Color Fonts
                case TableRecord.TAG_COLR:
                    mColr = null;
                    break;
                case TableRecord.TAG_CPAL:
                    mCpal = null;
                    break;
                // Other OpenType Tables
                case TableRecord.TAG_HDMX:
                    mHdmx = null;
                    break;
                case TableRecord.TAG_KERN:
                    mKern = new KerningTable(reader, record);
                    break;
                case TableRecord.TAG_LTSH:
                    mLtsh = null;
                    break;
                case TableRecord.TAG_MERG:
                    mMerg = null;
                    break;
                case TableRecord.TAG_META:
                    mMeta = null;
                    break;
                case TableRecord.TAG_PCLT:
                    mPclt = new PCL5Table(reader, record);
                    break;
                case TableRecord.TAG_VDMX:
                    mVdmx = null;
                    break;
                case TableRecord.TAG_VHEA:
                    mVhea = null;
                    break;
                case TableRecord.TAG_VMTX:
                    mVmtx = null;
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
     * @return 搜索范围(Maximum power of 2 &lt;= numTables) x 16.
     */
    public int getSearchRange() {
        return mSearchRange;
    }

    /**
     * 获取条目选择器
     *
     * @return 条目选择器 Log2(maximum power of 2 &lt;= numTables).
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
    public Map<Integer, TableRecord> getTableRecords() {
        return mRecords;
    }

    /**
     * 获取表记录
     *
     * @param index 表记录集合位置
     * @return 表记录
     */
    public TableRecord getTableRecordByIndex(int index) {
        return mRecordArray.get(index);
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

    /**
     * 获取表
     *
     * @param tag 表记录标签
     * @return 表，返回空时，可能未解析、不包含该表或者暂未支持解析该类型的表
     */
    public BaseTable getTable(int tag) {
        switch (tag) {
            default:
                // 暂不支持的表
                return null;
            // Required Tables
            case TableRecord.TAG_CMAP:
                return mCmap;
            case TableRecord.TAG_HEAD:
                return mHead;
            case TableRecord.TAG_HHEA:
                return mHhea;
            case TableRecord.TAG_HMTX:
                return mHmtx;
            case TableRecord.TAG_MAXP:
                return mMaxp;
            case TableRecord.TAG_NAME:
                return mName;
            case TableRecord.TAG_OS2:
                return mOS2;
            case TableRecord.TAG_POST:
                return mPost;
            // Tables Related to TrueType Outlines
            case TableRecord.TAG_CVT:
                return mCvt;
            case TableRecord.TAG_FPGM:
                return mFpgm;
            case TableRecord.TAG_GLYF:
                return mGlyf;
            case TableRecord.TAG_LOCA:
                return mLoca;
            case TableRecord.TAG_PREP:
                return mPrep;
            case TableRecord.TAG_GASP:
                return mGasp;
            // Tables Related to CFF Outlines
            case TableRecord.TAG_CFF:
                return mCff;
            case TableRecord.TAG_CFF2:
                return mCff2;
            case TableRecord.TAG_VORG:
                return mVorg;
            // Table Related to SVG Outlines
            case TableRecord.TAG_SVG:
                return mSvg;
            // Tables Related to Bitmap Glyphs
            case TableRecord.TAG_EBDT:
                return mEbdt;
            case TableRecord.TAG_EBLC:
                return mEblc;
            case TableRecord.TAG_EBSC:
                return mEbsc;
            case TableRecord.TAG_CBDT:
                return mCbdt;
            case TableRecord.TAG_CBLC:
                return mCblc;
            case TableRecord.TAG_SBIX:
                return mSbix;
            // Advanced Typographic Tables
            case TableRecord.TAG_BASE:
                return mBase;
            case TableRecord.TAG_GDEF:
                return mGdef;
            case TableRecord.TAG_GPOS:
                return mGpos;
            case TableRecord.TAG_GSUB:
                return mGsub;
            case TableRecord.TAG_JSTF:
                return mJstf;
            case TableRecord.TAG_MATH:
                return mMath;
            // Tables used for OpenType Font Variations
            case TableRecord.TAG_AVAR:
                return mAvar;
            case TableRecord.TAG_CVAR:
                return mCvar;
            case TableRecord.TAG_FVAR:
                return mFvar;
            case TableRecord.TAG_GVAR:
                return mGvar;
            case TableRecord.TAG_HVAR:
                return mHvar;
            case TableRecord.TAG_MVAR:
                return mMvar;
            case TableRecord.TAG_STAT:
                return mStat;
            case TableRecord.TAG_VVAR:
                return mVvar;
            // Tables Related to Color Fonts
            case TableRecord.TAG_COLR:
                return mColr;
            case TableRecord.TAG_CPAL:
                return mCpal;
            // Other OpenType Tables
            case TableRecord.TAG_HDMX:
                return mHdmx;
            case TableRecord.TAG_KERN:
                return mKern;
            case TableRecord.TAG_LTSH:
                return mLtsh;
            case TableRecord.TAG_MERG:
                return mMerg;
            case TableRecord.TAG_META:
                return mMeta;
            case TableRecord.TAG_PCLT:
                return mPclt;
            case TableRecord.TAG_VDMX:
                return mVdmx;
            case TableRecord.TAG_VHEA:
                return mVhea;
            case TableRecord.TAG_VMTX:
                return mVmtx;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenType openType = (OpenType) o;
        return mSFNTVersion == openType.mSFNTVersion &&
                mNumTables == openType.mNumTables &&
                mSearchRange == openType.mSearchRange &&
                mEntrySelector == openType.mEntrySelector &&
                mRangeShift == openType.mRangeShift &&
                Objects.equals(mRecords, openType.mRecords) &&
                Objects.equals(mRecordArray, openType.mRecordArray) &&
                Objects.equals(mCmap, openType.mCmap) &&
                Objects.equals(mHead, openType.mHead) &&
                Objects.equals(mHhea, openType.mHhea) &&
                Objects.equals(mHmtx, openType.mHmtx) &&
                Objects.equals(mMaxp, openType.mMaxp) &&
                Objects.equals(mName, openType.mName) &&
                Objects.equals(mOS2, openType.mOS2) &&
                Objects.equals(mPost, openType.mPost) &&
                Objects.equals(mCvt, openType.mCvt) &&
                Objects.equals(mFpgm, openType.mFpgm) &&
                Objects.equals(mGlyf, openType.mGlyf) &&
                Objects.equals(mLoca, openType.mLoca) &&
                Objects.equals(mPrep, openType.mPrep) &&
                Objects.equals(mGasp, openType.mGasp) &&
                Objects.equals(mCff, openType.mCff) &&
                Objects.equals(mCff2, openType.mCff2) &&
                Objects.equals(mVorg, openType.mVorg) &&
                Objects.equals(mSvg, openType.mSvg) &&
                Objects.equals(mEbdt, openType.mEbdt) &&
                Objects.equals(mEblc, openType.mEblc) &&
                Objects.equals(mEbsc, openType.mEbsc) &&
                Objects.equals(mCbdt, openType.mCbdt) &&
                Objects.equals(mCblc, openType.mCblc) &&
                Objects.equals(mSbix, openType.mSbix) &&
                Objects.equals(mBase, openType.mBase) &&
                Objects.equals(mGdef, openType.mGdef) &&
                Objects.equals(mGpos, openType.mGpos) &&
                Objects.equals(mGsub, openType.mGsub) &&
                Objects.equals(mJstf, openType.mJstf) &&
                Objects.equals(mMath, openType.mMath) &&
                Objects.equals(mAvar, openType.mAvar) &&
                Objects.equals(mCvar, openType.mCvar) &&
                Objects.equals(mFvar, openType.mFvar) &&
                Objects.equals(mGvar, openType.mGvar) &&
                Objects.equals(mHvar, openType.mHvar) &&
                Objects.equals(mMvar, openType.mMvar) &&
                Objects.equals(mStat, openType.mStat) &&
                Objects.equals(mVvar, openType.mVvar) &&
                Objects.equals(mColr, openType.mColr) &&
                Objects.equals(mCpal, openType.mCpal) &&
                Objects.equals(mDsig, openType.mDsig) &&
                Objects.equals(mHdmx, openType.mHdmx) &&
                Objects.equals(mKern, openType.mKern) &&
                Objects.equals(mLtsh, openType.mLtsh) &&
                Objects.equals(mMerg, openType.mMerg) &&
                Objects.equals(mMeta, openType.mMeta) &&
                Objects.equals(mPclt, openType.mPclt) &&
                Objects.equals(mVdmx, openType.mVdmx) &&
                Objects.equals(mVhea, openType.mVhea) &&
                Objects.equals(mVmtx, openType.mVmtx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSFNTVersion, mNumTables, mSearchRange, mEntrySelector, mRangeShift,
                mRecords, mRecordArray, mCmap, mHead, mHhea, mHmtx, mMaxp, mName, mOS2, mPost,
                mCvt, mFpgm, mGlyf, mLoca, mPrep, mGasp, mCff, mCff2, mVorg, mSvg, mEbdt, mEblc,
                mEbsc, mCbdt, mCblc, mSbix, mBase, mGdef, mGpos, mGsub, mJstf, mMath, mAvar, mCvar,
                mFvar, mGvar, mHvar, mMvar, mStat, mVvar, mColr, mCpal, mDsig, mHdmx, mKern, mLtsh,
                mMerg, mMeta, mPclt, mVdmx, mVhea, mVmtx);
    }

    @Override
    public String toString() {
        return "OpenType{" +
                "SFNTVersion=" + mSFNTVersion +
                ", numTables=" + mNumTables +
                ", searchRange=" + mSearchRange +
                ", entrySelector=" + mEntrySelector +
                ", rangeShift=" + mRangeShift +
                ", records=" + String.valueOf(mRecordArray) +
                '}';
    }
}
