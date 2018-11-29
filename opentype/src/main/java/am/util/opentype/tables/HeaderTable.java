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
package am.util.opentype.tables;

import java.io.IOException;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Font Header Table
 * This table gives global information about the font. The bounding box values should be computed
 * using only glyphs that have contours. Glyphs with no contours should be ignored for
 * the purposes of these calculations.
 */
@SuppressWarnings("unused")
public class HeaderTable extends BaseTable {

    private final int mMajorVersion;
    private final int mMinorVersion;
    private final int mFontRevision;
    private final int mCheckSumAdjustment;
    private final int mMagicNumber;
    private final int mFlags;
    private final int mUnitsPerEm;
    private final long mCreated;
    private final long mModified;
    private final int mXMin;
    private final int mYMin;
    private final int mXMax;
    private final int mYMax;
    private final int mMacStyle;
    private final int mLowestRecPPEM;
    private final int mFontDirectionHint;
    private final int mIndexToLocFormat;
    private final int mGlyphDataFormat;

    public HeaderTable(OpenTypeReader reader, TableRecord record) throws IOException {
        super(record);
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_HEAD)
            throw new IOException();
        reader.seek(record.getOffset());
        final int majorVersion = reader.readUnsignedShort();
        final int minorVersion = reader.readUnsignedShort();
        final int fontRevision = reader.readInt();
        final int checkSumAdjustment = reader.readUnsignedInt();
        final int magicNumber = reader.readUnsignedInt();
        final int flags = reader.readUnsignedShort();
        final int unitsPerEm = reader.readUnsignedShort();
        final long created = reader.readLong();
        final long modified = reader.readLong();
        final int xMin = reader.readShort();
        final int yMin = reader.readShort();
        final int xMax = reader.readShort();
        final int yMax = reader.readShort();
        final int macStyle = reader.readUnsignedShort();
        final int lowestRecPPEM = reader.readUnsignedInt();
        final int fontDirectionHint = reader.readShort();
        final int indexToLocFormat = reader.readShort();
        final int glyphDataFormat = reader.readShort();

        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
        mFontRevision = fontRevision;
        mCheckSumAdjustment = checkSumAdjustment;
        mMagicNumber = magicNumber;
        mFlags = flags;
        mUnitsPerEm = unitsPerEm;
        mCreated = created;
        mModified = modified;
        mXMin = xMin;
        mYMin = yMin;
        mXMax = xMax;
        mYMax = yMax;
        mMacStyle = macStyle;
        mLowestRecPPEM = lowestRecPPEM;
        mFontDirectionHint = fontDirectionHint;
        mIndexToLocFormat = indexToLocFormat;
        mGlyphDataFormat = glyphDataFormat;
    }

    /**
     * Major version number of the font header table — set to 1.
     *
     * @return Major version number.
     */
    public int getMajorVersion() {
        return mMajorVersion;
    }

    /**
     * Minor version number of the font header table — set to 0.
     *
     * @return Minor version number.
     */
    public int getMinorVersion() {
        return mMinorVersion;
    }

    /**
     * Set by font manufacturer.
     *
     * @return Font revision.
     */
    public int getFontRevision() {
        return mFontRevision;
    }

    /**
     * To compute: set it to 0, sum the entire font as uint32, then store 0xB1B0AFBA - sum.
     * If the font is used as a component in a font collection file, the value of this field
     * will be invalidated by changes to the file structure and font table directory,
     * and must be ignored.
     *
     * @return CheckSum adjustment.
     */
    public int getCheckSumAdjustment() {
        return mCheckSumAdjustment;
    }

    /**
     * Set to 0x5F0F3CF5.
     *
     * @return Magic number.
     */
    public int getMagicNumber() {
        return mMagicNumber;
    }

    /**
     * Bit 0: Baseline for font at y=0;
     * Bit 1: Left sidebearing point at x=0 (relevant only for TrueType rasterizers) —
     * see the note below regarding variable fonts;
     * Bit 2: Instructions may depend on point size;
     * Bit 3: Force ppem to integer values for all internal scaler math;
     * may use fractional ppem sizes if this bit is clear;
     * Bit 4: Instructions may alter advance width (the advance widths might not scale linearly);
     * Bit 5: This bit is not used in OpenType, and should not be set in order to
     * ensure compatible behavior on all platforms. If set, it may result in different behavior
     * for vertical layout in some platforms. (See Apple’s specification at
     * http://developer.apple.com/fonts//TrueType-Reference-Manual/RM06/Chap6head.html
     * for details regarding behavior in Apple platforms.)
     * Bits 6–10: These bits are not used in Opentype and should always be cleared.
     * (See Apple’s specification at
     * http://developer.apple.com/fonts//TrueType-Reference-Manual/RM06/Chap6head.html
     * for details regarding legacy used in Apple platforms.)
     * Bit 11: Font data is “lossless” as a result of having been subjected to optimizing
     * transformation and/or compression (such as e.g. compression mechanisms defined by
     * ISO/IEC 14496-18, MicroType Express, WOFF 2.0 or similar) where the original font
     * functionality and features are retained but the binary compatibility between input
     * and output font files is not guaranteed. As a result of the applied transform,
     * the DSIG table may also be invalidated.
     * Bit 12: Font converted (produce compatible metrics)
     * Bit 13: Font optimized for ClearType™. Note, fonts that rely on embedded bitmaps
     * (EBDT) for rendering should not be considered optimized for ClearType,
     * and therefore should keep this bit cleared.
     * Bit 14: Last Resort font. If set, indicates that the glyphs encoded in
     * the 'cmap' subtables are simply generic symbolic representations of code point ranges
     * and don’t truly represent support for those code points. If unset, indicates that
     * the glyphs encoded in the 'cmap' subtables represent proper support for those code points.
     * Bit 15: Reserved, set to 0
     *
     * @return Flags.
     */
    public int getFlags() {
        return mFlags;
    }

    /**
     * Set to a value from 16 to 16384. Any value in this range is valid.
     * In fonts that have TrueType outlines, a power of 2 is recommended as
     * this allows performance optimizations in some rasterizers.
     *
     * @return Units per em.
     */
    public int getUnitsPerEm() {
        return mUnitsPerEm;
    }

    /**
     * Number of seconds since 12:00 midnight that started January 1st 1904 in GMT/UTC time zone.
     *
     * @return Created time.
     */
    public long getCreated() {
        return mCreated;
    }

    /**
     * Number of seconds since 12:00 midnight that started January 1st 1904 in GMT/UTC time zone.
     *
     * @return Modified time.
     */
    public long getModified() {
        return mModified;
    }

    /**
     * For all glyph bounding boxes.
     *
     * @return xMin.
     */
    public int getXMin() {
        return mXMin;
    }

    /**
     * For all glyph bounding boxes.
     *
     * @return yMin.
     */
    public int getYMin() {
        return mYMin;
    }

    /**
     * For all glyph bounding boxes.
     *
     * @return xMax.
     */
    public int getXMax() {
        return mXMax;
    }

    /**
     * For all glyph bounding boxes.
     *
     * @return yMax.
     */
    public int getYMax() {
        return mYMax;
    }

    /**
     * Bit 0: Bold (if set to 1);
     * Bit 1: Italic (if set to 1)
     * Bit 2: Underline (if set to 1)
     * Bit 3: Outline (if set to 1)
     * Bit 4: Shadow (if set to 1)
     * Bit 5: Condensed (if set to 1)
     * Bit 6: Extended (if set to 1)
     * Bits 7–15: Reserved (set to 0).
     *
     * @return Mac style.
     */
    public int getMacStyle() {
        return mMacStyle;
    }

    public boolean isBold() {
        return (mMacStyle & 1) == 1;
    }

    public boolean isItalic() {
        return (mMacStyle & 2) == 2;
    }

    public boolean isUnderline() {
        return (mMacStyle & 4) == 4;
    }

    public boolean isOutline() {
        return (mMacStyle & 8) == 8;
    }

    public boolean isShadow() {
        return (mMacStyle & 16) == 16;
    }

    public boolean isCondensed() {
        return (mMacStyle & 32) == 32;
    }

    public boolean isExtended() {
        return (mMacStyle & 64) == 64;
    }

    /**
     * Smallest readable size in pixels.
     *
     * @return Lowest Rec PPEM.
     */
    public int getLowestRecPPEM() {
        return mLowestRecPPEM;
    }

    /**
     * Deprecated (Set to 2).
     * 0: Fully mixed directional glyphs;
     * 1: Only strongly left to right;
     * 2: Like 1 but also contains neutrals;
     * -1: Only strongly right to left;
     * -2: Like -1 but also contains neutrals.
     * (A neutral character has no inherent directionality; it is not a character with
     * zero (0) width. Spaces and punctuation are examples of neutral characters.
     * Non-neutral characters are those with inherent directionality. For example,
     * Roman letters (left-to-right) and Arabic letters (right-to-left) have directionality.
     * In a “normal” Roman font where spaces and punctuation are present,
     * the font direction hints should be set to two (2).)
     *
     * @return Font direction hint.
     */
    public int getFontDirectionHint() {
        return mFontDirectionHint;
    }

    /**
     * 0 for short offsets (Offset16), 1 for long (Offset32).
     *
     * @return Index to loc format.
     */
    public int getIndexToLocFormat() {
        return mIndexToLocFormat;
    }

    /**
     * 0 for current format.
     *
     * @return Glyph data format.
     */
    public int getGlyphDataFormat() {
        return mGlyphDataFormat;
    }

    @Override
    public int getHashCode() {
        return Objects.hash(super.getHashCode(), mMajorVersion, mMinorVersion, mFontRevision,
                mCheckSumAdjustment, mMagicNumber, mFlags, mUnitsPerEm, mCreated, mModified,
                mXMin, mYMin, mXMax, mYMax, mMacStyle, mLowestRecPPEM, mFontDirectionHint,
                mIndexToLocFormat, mGlyphDataFormat);
    }

    @Override
    public String getString() {
        return "HeaderTable{" +
                "record=" + String.valueOf(getTableRecord()) +
                ", majorVersion=" + mMajorVersion +
                ", minorVersion=" + mMinorVersion +
                ", fontRevision=" + mFontRevision +
                ", checkSumAdjustment=" + mCheckSumAdjustment +
                ", magicNumber=" + mMagicNumber +
                ", flags=" + mFlags +
                ", unitsPerEm=" + mUnitsPerEm +
                ", created=" + mCreated +
                ", modified=" + mModified +
                ", xMin=" + mXMin +
                ", yMin=" + mYMin +
                ", xMax=" + mXMax +
                ", yMax=" + mYMax +
                ", macStyle=" + mMacStyle +
                ", lowestRecPPEM=" + mLowestRecPPEM +
                ", fontDirectionHint=" + mFontDirectionHint +
                ", indexToLocFormat=" + mIndexToLocFormat +
                ", glyphDataFormat=" + mGlyphDataFormat +
                '}';
    }
}
