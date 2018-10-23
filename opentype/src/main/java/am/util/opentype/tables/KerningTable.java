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
import java.util.ArrayList;
import java.util.List;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Kerning
 * The kerning table contains the values that control the inter-character spacing for
 * the glyphs in a font. There is currently no system level support for kerning
 * (other than returning the kern pairs and kern values). OpenType™ fonts containing CFF outlines
 * are not supported by the 'kern' table and must use the GPOS OpenType Layout table.
 * Each subtable varies in format, and can contain information for vertical or horizontal text,
 * and can contain kerning values or minimum values. Kerning values are used to adjust
 * inter-character spacing, and minimum values are used to limit the amount of adjustment
 * that the scaler applies by the combination of kerning and tracking.
 * Because the adjustments are additive, the order of the subtables containing kerning values
 * is not important. However, tables containing minimum values should usually be placed last,
 * so that they can be used to limit the total effect of other subtables.
 */
@SuppressWarnings("unused")
public class KerningTable extends BaseTable {

    private final int mVersion;
    private final int mNumberOfTables;
    private final List<SubTable> mSubTables;

    public KerningTable(OpenTypeReader reader, TableRecord record) throws IOException {
        super(record);
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_KERN)
            throw new IOException();
        reader.seek(record.getOffset());
        final int version = reader.readUnsignedShort();
        final int numberOfTables = reader.readUnsignedShort();
        final ArrayList<SubTable> subTables = new ArrayList<>();
        for (int i = 0; i < numberOfTables; i++) {
            final long pos = reader.getPointer();
            final int sVersion = reader.readUnsignedShort();
            final int length = reader.readUnsignedShort();
            final int coverage = reader.readUnsignedShort();
            final int format = coverage >> 8;
            if (format == 0) {
                // Format 0
                final int numberOfPairs = reader.readUnsignedShort();
                final int searchRange = reader.readUnsignedShort();
                final int entrySelector = reader.readUnsignedShort();
                final int rangeShift = reader.readUnsignedShort();
                final ArrayList<KerningItem> items = new ArrayList<>();
                for (int j = 0; j < numberOfPairs; j++) {
                    final int left = reader.readUnsignedShort();
                    final int right = reader.readUnsignedShort();
                    final int value = reader.readShort();
                    items.add(new KerningItem(left, right, value));
                }
                subTables.add(new SubTableWithFormat0(sVersion, length, coverage, numberOfPairs,
                        searchRange, entrySelector, rangeShift, items));
            } else if (format == 2) {
                // Format 2
                final int rowWidth = reader.readUnsignedShort();
                final int leftClassTableOffset = reader.readUnsignedShort();
                final int rightClassTableOffset = reader.readUnsignedShort();
                final int arrayOffset = reader.readUnsignedShort();
                reader.seek(pos + leftClassTableOffset);
                final int leftFirstGlyph = reader.readUnsignedShort();
                final int leftNumberOfGlyphs = reader.readUnsignedShort();
                reader.seek(pos + rightClassTableOffset);
                final int rightFirstGlyph = reader.readUnsignedShort();
                final int rightNumberOfGlyphs = reader.readUnsignedShort();
                subTables.add(new SubTableWithFormat1(sVersion, length, coverage, rowWidth,
                        leftClassTableOffset, rightClassTableOffset, arrayOffset,
                        leftFirstGlyph, leftNumberOfGlyphs, rightFirstGlyph, rightNumberOfGlyphs));
            } else {
                subTables.add(new SubTable(sVersion, length, coverage));
            }

        }
        mVersion = version;
        mNumberOfTables = numberOfTables;
        mSubTables = subTables;
    }

    /**
     * Table version number (0)
     *
     * @return Version.
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * Number of subtables in the kerning table.
     *
     * @return Number of subtables.
     */
    public int getNumberOfTables() {
        return mNumberOfTables;
    }

    /**
     * SubTables
     *
     * @return SubTables
     */
    public List<SubTable> getSubTables() {
        return mSubTables;
    }

    @Override
    public int getHashCode() {
        return Objects.hash(super.getHashCode(), mVersion, mNumberOfTables, mSubTables);
    }

    @Override
    public String getString() {
        return "KerningTable{" +
                "record=" + String.valueOf(getTableRecord()) +
                ", version=" + mVersion +
                ", numberOfTables=" + mNumberOfTables +
                ", subTables=" + String.valueOf(mSubTables) +
                '}';
    }

    /**
     * SubTable
     */
    public static class SubTable {
        private final int mVersion;
        private final int mLength;
        private final int mCoverage;

        @SuppressWarnings("WeakerAccess")
        public SubTable(int version, int length, int coverage) {
            mVersion = version;
            mLength = length;
            mCoverage = coverage;

            final int crossStream = coverage & 4;
            final int override = coverage & 8;
        }

        /**
         * Kern subtable version number
         *
         * @return Version.
         */
        @SuppressWarnings("WeakerAccess")
        public int getVersion() {
            return mVersion;
        }

        /**
         * Length of the subtable, in bytes (including this header).
         *
         * @return Length of the subtable.
         */
        @SuppressWarnings("WeakerAccess")
        public int getLength() {
            return mLength;
        }

        /**
         * What type of information is contained in this table.
         *
         * @return Coverage.
         */
        @SuppressWarnings("WeakerAccess")
        public int getCoverage() {
            return mCoverage;
        }

        /**
         * 1 if table has horizontal data, 0 if vertical.
         *
         * @return Horizontal.
         */
        public int getHorizontal() {
            return mCoverage & 1;
        }

        /**
         * If this bit is set to 1, the table has minimum values.
         * If set to 0, the table has kerning values.
         *
         * @return Minimum values.
         */
        public int getMinimum() {
            return mCoverage & 2;
        }

        /**
         * If set to 1, kerning is perpendicular to the flow of the text.
         * If the text is normally written horizontally, kerning will be done in the up and
         * down directions. If kerning values are positive, the text will be kerned upwards;
         * if they are negative, the text will be kerned downwards.
         * If the text is normally written vertically, kerning will be done in the left and
         * right directions. If kerning values are positive, the text will be kerned to the right;
         * if they are negative, the text will be kerned to the left.
         * The value 0x8000 in the kerning data resets the cross-stream kerning back to 0.
         *
         * @return Cross-stream.
         */
        public int getCrossStream() {
            return mCoverage & 4;
        }

        /**
         * If this bit is set to 1 the value in this table should replace
         * the value currently being accumulated.
         *
         * @return Override.
         */
        public int getOverride() {
            return mCoverage & 8;
        }

        /**
         * Format of the subtable. Only formats 0 and 2 have been defined.
         * Formats 1 and 3 through 255 are reserved for future use.
         *
         * @return Format.
         */
        public int getFormat() {
            return mCoverage >> 8;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubTable subTable = (SubTable) o;
            return mVersion == subTable.mVersion &&
                    mLength == subTable.mLength &&
                    mCoverage == subTable.mCoverage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mVersion, mLength, mCoverage);
        }

        @Override
        public String toString() {
            return "SubTable{" +
                    "version=" + mVersion +
                    ", length=" + mLength +
                    ", coverage=" + mCoverage +
                    '}';
        }
    }

    /**
     * Format 0
     */
    public static class SubTableWithFormat0 extends SubTable {
        private final int mNumberOfPairs;
        private final int mSearchRange;
        private final int mEntrySelector;
        private final int mRangeShift;
        private final List<KerningItem> mItems;

        @SuppressWarnings("WeakerAccess")
        public SubTableWithFormat0(int version, int length, int coverage, int numberOfPairs,
                                   int searchRange, int entrySelector, int rangeShift,
                                   List<KerningItem> items) {
            super(version, length, coverage);
            mNumberOfPairs = numberOfPairs;
            mSearchRange = searchRange;
            mEntrySelector = entrySelector;
            mRangeShift = rangeShift;
            mItems = items;
        }

        /**
         * This gives the number of kerning pairs in the table.
         *
         * @return Number of kerning pairs.
         */
        public int getNumberOfPairs() {
            return mNumberOfPairs;
        }

        /**
         * The largest power of two less than or equal to the value of nPairs,
         * multiplied by the size in bytes of an entry in the table.
         *
         * @return Search range.
         */
        public int getSearchRange() {
            return mSearchRange;
        }

        /**
         * This is calculated as log2 of the largest power of two less than or equal to the value
         * of nPairs. This value indicates how many iterations of the search loop will have to be
         * made. (For example, in a list of eight items, there would have to be three iterations
         * of the loop).
         *
         * @return Entry selector.
         */
        public int getEntrySelector() {
            return mEntrySelector;
        }

        /**
         * The value of nPairs minus the largest power of two less than or equal to nPairs,
         * and then multiplied by the size in bytes of an entry in the table.
         *
         * @return Range shift.
         */
        public int getRangeShift() {
            return mRangeShift;
        }

        /**
         * The list of kerning pairs and values.
         *
         * @return Items.
         */
        public List<KerningItem> getItems() {
            return mItems;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubTableWithFormat0)) return false;
            if (!super.equals(o)) return false;
            SubTableWithFormat0 that = (SubTableWithFormat0) o;
            return mNumberOfPairs == that.mNumberOfPairs &&
                    mSearchRange == that.mSearchRange &&
                    mEntrySelector == that.mEntrySelector &&
                    mRangeShift == that.mRangeShift &&
                    Objects.equals(mItems, that.mItems);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), mNumberOfPairs, mSearchRange, mEntrySelector,
                    mRangeShift, mItems);
        }

        @Override
        public String toString() {
            return "SubTableWithFormat0{" +
                    "version=" + getVersion() +
                    ", length=" + getLength() +
                    ", coverage=" + getCoverage() +
                    ", numberOfPairs=" + mNumberOfPairs +
                    ", searchRange=" + mSearchRange +
                    ", entrySelector=" + mEntrySelector +
                    ", rangeShift=" + mRangeShift +
                    ", items=" + String.valueOf(mItems) +
                    '}';
        }
    }

    /**
     * Kerning Item
     */
    public static class KerningItem {
        private final int mLeft;
        private final int mRight;
        private final int mValue;

        @SuppressWarnings("WeakerAccess")
        public KerningItem(int left, int right, int value) {
            mLeft = left;
            mRight = right;
            mValue = value;
        }

        /**
         * The glyph index for the left-hand glyph in the kerning pair.
         *
         * @return The glyph index for the left-hand glyph.
         */
        public int getLeft() {
            return mLeft;
        }

        /**
         * The glyph index for the right-hand glyph in the kerning pair.
         *
         * @return The glyph index for the right-hand glyph.
         */
        public int getRight() {
            return mRight;
        }

        /**
         * The kerning value for the above pair, in FUnits. If this value is greater than zero,
         * the characters will be moved apart. If this value is less than zero,
         * the character will be moved closer together.
         *
         * @return The kerning value for the above pair.
         */
        public int getValue() {
            return mValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KerningItem that = (KerningItem) o;
            return mLeft == that.mLeft &&
                    mRight == that.mRight &&
                    mValue == that.mValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mLeft, mRight, mValue);
        }

        @Override
        public String toString() {
            return "KerningItem{" +
                    "left=" + mLeft +
                    ", right=" + mRight +
                    ", value=" + mValue +
                    '}';
        }
    }

    /**
     * Format 2
     */
    public static class SubTableWithFormat1 extends SubTable {
        private final int mRowWidth;
        private final int mLeftClassTableOffset;
        private final int mRightClassTableOffset;
        private final int mArrayOffset;
        private final int mLeftFirstGlyph;
        private final int mLeftNumberOfGlyphs;
        private final int mRightFirstGlyph;
        private final int mRightNumberOfGlyphs;

        @SuppressWarnings("WeakerAccess")
        public SubTableWithFormat1(int version, int length, int coverage, int rowWidth,
                                   int leftClassTableOffset, int rightClassTableOffset,
                                   int arrayOffset, int leftFirstGlyph, int leftNumberOfGlyphs,
                                   int rightFirstGlyph, int rightNumberOfGlyphs) {
            super(version, length, coverage);
            mRowWidth = rowWidth;
            mLeftClassTableOffset = leftClassTableOffset;
            mRightClassTableOffset = rightClassTableOffset;
            mArrayOffset = arrayOffset;
            mLeftFirstGlyph = leftFirstGlyph;
            mLeftNumberOfGlyphs = leftNumberOfGlyphs;
            mRightFirstGlyph = rightFirstGlyph;
            mRightNumberOfGlyphs = rightNumberOfGlyphs;
        }

        /**
         * The width, in bytes, of a row in the table.
         *
         * @return The width.
         */
        public int getRowWidth() {
            return mRowWidth;
        }

        /**
         * Offset from beginning of this subtable to left-hand class table.
         *
         * @return Offset.
         */
        public int getLeftClassTableOffset() {
            return mLeftClassTableOffset;
        }

        /**
         * Offset from beginning of this subtable to right-hand class table.
         *
         * @return Offset.
         */
        public int getRightClassTableOffset() {
            return mRightClassTableOffset;
        }

        /**
         * Offset from beginning of this subtable to the start of the kerning array.
         *
         * @return Offset.
         */
        public int getArrayOffset() {
            return mArrayOffset;
        }

        /**
         * First glyph in left-hand class range.
         *
         * @return First glyph.
         */
        public int getLeftFirstGlyph() {
            return mLeftFirstGlyph;
        }

        /**
         * Number of glyph in left-hand class range.
         *
         * @return Number of glyph.
         */
        public int getLeftNumberOfGlyphs() {
            return mLeftNumberOfGlyphs;
        }

        /**
         * First glyph in right-hand class range.
         *
         * @return First glyph.
         */
        public int getRightFirstGlyph() {
            return mRightFirstGlyph;
        }

        /**
         * Number of glyph in right-hand class range.
         *
         * @return Number of glyph.
         */
        public int getRightNumberOfGlyphs() {
            return mRightNumberOfGlyphs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubTableWithFormat1)) return false;
            if (!super.equals(o)) return false;
            SubTableWithFormat1 that = (SubTableWithFormat1) o;
            return mRowWidth == that.mRowWidth &&
                    mLeftClassTableOffset == that.mLeftClassTableOffset &&
                    mRightClassTableOffset == that.mRightClassTableOffset &&
                    mArrayOffset == that.mArrayOffset &&
                    mLeftFirstGlyph == that.mLeftFirstGlyph &&
                    mLeftNumberOfGlyphs == that.mLeftNumberOfGlyphs &&
                    mRightFirstGlyph == that.mRightFirstGlyph &&
                    mRightNumberOfGlyphs == that.mRightNumberOfGlyphs;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), mRowWidth, mLeftClassTableOffset,
                    mRightClassTableOffset, mArrayOffset, mLeftFirstGlyph, mLeftNumberOfGlyphs,
                    mRightFirstGlyph, mRightNumberOfGlyphs);
        }

        @Override
        public String toString() {
            return "SubTableWithFormat1{" +
                    "version=" + getVersion() +
                    ", length=" + getLength() +
                    ", coverage=" + getCoverage() +
                    ", rowWidth=" + mRowWidth +
                    ", leftClassTableOffset=" + mLeftClassTableOffset +
                    ", rightClassTableOffset=" + mRightClassTableOffset +
                    ", arrayOffset=" + mArrayOffset +
                    ", leftFirstGlyph=" + mLeftFirstGlyph +
                    ", leftNumberOfGlyphs=" + mLeftNumberOfGlyphs +
                    ", rightFirstGlyph=" + mRightFirstGlyph +
                    ", rightNumberOfGlyphs=" + mRightNumberOfGlyphs +
                    '}';
        }
    }
}
