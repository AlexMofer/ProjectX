package am.util.opentype.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Character to Glyph Index Mapping Table
 * This table defines the mapping of character codes to the glyph index values used in the font.
 * It may contain more than one subtable, in order to support more than one character
 * encoding scheme.
 */
@SuppressWarnings("unused")
public class CharacterMappingTable {

    private final int mVersion;
    private final int mNumTables;
    private final List<EncodingRecord> mEncodingRecords;
    private final SubTable mSubTable;

    public CharacterMappingTable(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_CMAP)
            throw new IOException();
        reader.seek(record.getOffset());
        final int version = reader.readUnsignedShort();
        final int numTables = reader.readUnsignedShort();
        final ArrayList<EncodingRecord> encodingRecords = new ArrayList<>();
        for (int i = 0; i < numTables; i++) {
            final int platformID = reader.readUnsignedShort();
            final int encodingID = reader.readUnsignedShort();
            final int offset = reader.readUnsignedInt();
            encodingRecords.add(new EncodingRecord(platformID, encodingID, offset));
        }
        final long point = reader.getPointer();
        final int format = reader.readUnsignedShort();
        final SubTable subTable;
        if (format == 0) {
            // Format 0: Byte encoding table
            // This is the Apple standard character to glyph index mapping table.
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int[] glyphIds = new int[256];
            for (int i = 0; i < glyphIds.length; i++) {
                glyphIds[i] = reader.readUnsignedByte();
            }
            subTable = new SubTable0(format, length, language, glyphIds);
        } else if (format == 2) {
            // Format 2: High-byte mapping through table
            // This subtable is useful for the national character code standards used for Japanese,
            // Chinese, and Korean characters. These code standards use a mixed 8-/16-bit encoding,
            // in which certain byte values signal the first byte of a 2-byte character (but these
            // values are also legal as the second byte of a 2-byte character).
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int[] subHeaderKeys = new int[256];
            for (int i = 0; i < subHeaderKeys.length; i++) {
                subHeaderKeys[i] = reader.readUnsignedShort();
            }
            final int firstCode = reader.readUnsignedShort();
            final int entryCount = reader.readUnsignedShort();
            final int idDelta = reader.readShort();
            final int idRangeOffset = reader.readUnsignedShort();
            final ArrayList<SubHeaderRecord> subHeaders = new ArrayList<>();
            subHeaders.add(new SubHeaderRecord(firstCode, entryCount, idDelta, idRangeOffset));
            for (int i = 1; i < entryCount; i++) {
                subHeaders.add(new SubHeaderRecord(reader.readUnsignedShort(),
                        reader.readUnsignedShort(), reader.readShort(),
                        reader.readUnsignedShort()));
            }
            final int[] glyphIndexArray;
            if (entryCount > 0) {
                glyphIndexArray = new int[entryCount];
                for (int i = 0; i < entryCount; i++) {
                    glyphIndexArray[i] = reader.readUnsignedShort();
                }
            } else {
                glyphIndexArray = null;
            }
            subTable = new SubTable2(format, length, language, subHeaderKeys, subHeaders,
                    glyphIndexArray);
        } else if (format == 4) {

            // Format 4: Segment mapping to delta values
            // This is the standard character-to-glyph-index mapping table for the Windows platform
            // for fonts that support Unicode BMP characters.
            // See Windows platform (platform ID = 3) above for additional details regarding
            // subtable formats for Unicode encoding on the Windows platform.
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int segCountX2 = reader.readUnsignedShort();
            final int searchRange = reader.readUnsignedShort();
            final int entrySelector = reader.readUnsignedShort();
            final int rangeShift = reader.readUnsignedShort();
            final int segCount = segCountX2 / 2;
            final int[] endCode = new int[segCount];
            for (int i = 0; i < segCount; i++) {
                endCode[i] = reader.readUnsignedShort();
            }
            reader.skip(2);// reservedPad
            final int[] startCode = new int[segCount];
            for (int i = 0; i < segCount; i++) {
                startCode[i] = reader.readUnsignedShort();
            }
            final int[] idDelta = new int[segCount];
            for (int i = 0; i < segCount; i++) {
                idDelta[i] = reader.readShort();
            }
            final int[] idRangeOffset = new int[segCount];
            for (int i = 0; i < segCount; i++) {
                idRangeOffset[i] = reader.readUnsignedShort();
            }
            final long pos = reader.getPointer();
            final int[] glyphIdArray;
            if (pos < record.getOffset() + length) {
                final int count = (int) (record.getOffset() + length - pos) / 2;
                glyphIdArray = new int[count];
                for (int i = 0; i < count; i++) {
                    glyphIdArray[i] = reader.readUnsignedShort();
                }
            } else {
                glyphIdArray = null;
            }
            subTable = new SubTable4(format, length, language, segCountX2, searchRange,
                    entrySelector, rangeShift, endCode, startCode, idDelta, idRangeOffset,
                    glyphIdArray);
        } else if (format == 6) {
            // Format 6: Trimmed table mapping
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int firstCode = reader.readUnsignedShort();
            final int entryCount = reader.readUnsignedShort();
            final int[] glyphIdArray = new int[entryCount];
            for (int i = 0; i < entryCount; i++) {
                glyphIdArray[i] = reader.readUnsignedShort();
            }
            subTable = new SubTable6(format, length, language, firstCode, entryCount, glyphIdArray);
        } else if (format == 8) {
            // Format 8: mixed 16-bit and 32-bit coverage
            reader.skip(2);// reserved
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int[] is32 = new int[8192];
            for (int i = 0; i < 8192; i++) {
                is32[i] = reader.readUnsignedByte();
            }
            final int numGroups = reader.readUnsignedInt();
            final ArrayList<SequentialMapGroupRecord> groups = new ArrayList<>();
            for (int i = 0; i < numGroups; i++) {
                final int startCharCode = reader.readUnsignedInt();
                final int endCharCode = reader.readUnsignedInt();
                final int startGlyphID = reader.readUnsignedInt();
                groups.add(new SequentialMapGroupRecord(startCharCode, endCharCode, startGlyphID));
            }
            subTable = new SubTable8(format, length, language, is32, numGroups, groups);
        } else if (format == 10) {
            // Format 10: Trimmed array
            // Format 10 is similar to format 6, in that it defines a trimmed array for a tight
            // range of character codes. It differs, however, in that is uses 32-bit character codes
            reader.skip(2);// reserved
            final int length = reader.readUnsignedInt();
            final int language = reader.readUnsignedInt();
            final int startCharCode = reader.readUnsignedInt();
            final int numChars = reader.readUnsignedInt();
            final int[] glyphs = new int[numChars];
            for (int i = 0; i < numChars; i++) {
                glyphs[i] = reader.readUnsignedShort();
            }
            subTable = new SubTable10(format, length, language, startCharCode, numChars, glyphs);
        } else if (format == 12) {
            // Format 12: Segmented coverage
            // This is the standard character-to-glyph-index mapping table for the Windows platform
            // for fonts supporting Unicode supplementary-plane characters (U+10000 to U+10FFFF).
            // See Windows platform (platform ID = 3) above for additional details regarding
            // subtable formats for Unicode encoding on the Windows platform.
            reader.skip(2);// reserved
            final int length = reader.readUnsignedInt();
            final int language = reader.readUnsignedInt();
            final int numGroups = reader.readUnsignedInt();
            final ArrayList<SequentialMapGroupRecord> groups = new ArrayList<>();
            for (int i = 0; i < numGroups; i++) {
                final int startCharCode = reader.readUnsignedInt();
                final int endCharCode = reader.readUnsignedInt();
                final int startGlyphID = reader.readUnsignedInt();
                groups.add(new SequentialMapGroupRecord(startCharCode, endCharCode, startGlyphID));
            }
            subTable = new SubTable12(format, length, language, numGroups, groups);
        } else if (format == 13) {
            // Format 13: Many-to-one range mappings
            // This subtable provides for situations in which the same glyph is used for hundreds
            // or even thousands of consecutive characters spanning across multiple ranges of
            // the code space. This subtable format may be useful for “last resort” fonts,
            // although these fonts may use other suitable subtable formats as well.
            // (For “last-resort” fonts, see also the 'head' table flags, bit 14.)
            reader.skip(2);// reserved
            final int length = reader.readUnsignedInt();
            final int language = reader.readUnsignedInt();
            final int numGroups = reader.readUnsignedInt();
            final ArrayList<ConstantMapGroupRecord> groups = new ArrayList<>();
            for (int i = 0; i < numGroups; i++) {
                final int startCharCode = reader.readUnsignedInt();
                final int endCharCode = reader.readUnsignedInt();
                final int glyphID = reader.readUnsignedInt();
                groups.add(new ConstantMapGroupRecord(startCharCode, endCharCode, glyphID));
            }
            subTable = new SubTable13(format, length, language, numGroups, groups);
        } else if (format == 14) {
            // Format 14: Unicode Variation Sequences
            // Subtable format 14 specifies the Unicode Variation Sequences (UVSes) supported
            // by the font. A Variation Sequence, according to the Unicode Standard,
            // comprises a base character followed by a variation selector.
            // For example, <U+82A6, U+E0101>.
            final int length = reader.readUnsignedInt();
            final int numVarSelectorRecords = reader.readUnsignedInt();
            final ArrayList<VariationSelectorRecord> varSelectors = new ArrayList<>();
            for (int i = 0; i < numVarSelectorRecords; i++) {
                final int varSelector = reader.readUnsignedInt24();
                final int defaultUVSOffset = reader.readUnsignedInt();
                final int nonDefaultUVSOffset = reader.readUnsignedInt();
                final long pos = reader.getPointer();
                final DefaultUVSTable defaultUVS;
                if (defaultUVSOffset > 0) {
                    reader.seek(point + defaultUVSOffset);
                    final int numUnicodeValueRanges = reader.readUnsignedInt();
                    final ArrayList<UnicodeRangeRecord> ranges = new ArrayList<>();
                    for (int j = 0; j < numUnicodeValueRanges; j++) {
                        final int startUnicodeValue = reader.readUnsignedInt24();
                        final int additionalCount = reader.readUnsignedByte();
                        ranges.add(new UnicodeRangeRecord(startUnicodeValue, additionalCount));
                    }
                    defaultUVS = new DefaultUVSTable(numUnicodeValueRanges, ranges);
                } else {
                    defaultUVS = null;
                }
                final NonDefaultUVSTable nonDefaultUVS;
                if (nonDefaultUVSOffset > 0) {
                    reader.seek(point + nonDefaultUVSOffset);
                    final int numUVSMappings = reader.readUnsignedInt();
                    final ArrayList<UVSMappingRecord> uvsMappings = new ArrayList<>();
                    for (int j = 0; j < numUVSMappings; j++) {
                        final int unicodeValue = reader.readUnsignedInt24();
                        final int glyphID = reader.readUnsignedShort();
                        uvsMappings.add(new UVSMappingRecord(unicodeValue, glyphID));
                    }
                    nonDefaultUVS = new NonDefaultUVSTable(numUVSMappings, uvsMappings);
                } else {
                    nonDefaultUVS = null;
                }
                reader.seek(pos);
                varSelectors.add(new VariationSelectorRecord(varSelector, defaultUVSOffset,
                        nonDefaultUVSOffset, defaultUVS, nonDefaultUVS));
            }
            subTable = new SubTable14(format, length, numVarSelectorRecords, varSelectors);
        } else {
            // Unknown
            subTable = new SubTable(format);
        }
        mVersion = version;
        mNumTables = numTables;
        mEncodingRecords = encodingRecords;
        mSubTable = subTable;
    }

    /**
     * Table version number (0).
     *
     * @return Version.
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * Number of encoding tables that follow.
     *
     * @return Number of encoding tables.
     */
    public int getNumTables() {
        return mNumTables;
    }

    /**
     * The array of encoding records specifies particular encodings and the offset to the subtable
     * for each encoding.
     *
     * @return The array of encoding records.
     */
    public List<EncodingRecord> getEncodingRecords() {
        return mEncodingRecords;
    }

    /**
     * Sub Table
     *
     * @return Sub Table.
     */
    public SubTable getSubTable() {
        return mSubTable;
    }

    /**
     * Encoding record
     */
    @SuppressWarnings("unused")
    public static class EncodingRecord {
        private final int mPlatformID;
        private final int mEncodingID;
        private final int mOffset;

        @SuppressWarnings("all")
        public EncodingRecord(int platformID, int encodingID, int offset) {
            this.mPlatformID = platformID;
            this.mEncodingID = encodingID;
            this.mOffset = offset;
        }

        /**
         * Platform ID.
         *
         * @return Platform ID.
         */
        public int getPlatformID() {
            return mPlatformID;
        }

        /**
         * Platform-specific encoding ID.
         *
         * @return Encoding ID.
         */
        public int getEncodingID() {
            return mEncodingID;
        }

        /**
         * Byte offset from beginning of table to the subtable for this encoding.
         *
         * @return Byte offset.
         */
        public int getOffset() {
            return mOffset;
        }
    }

    /**
     * SubHeader Record
     */
    @SuppressWarnings("unused")
    public static class SubHeaderRecord {
        private final int mFirstCode;
        private final int mEntryCount;
        private final int mIdDelta;
        private final int mIdRangeOffset;

        @SuppressWarnings("all")
        public SubHeaderRecord(int firstCode, int entryCount, int idDelta, int idRangeOffset) {
            mFirstCode = firstCode;
            mEntryCount = entryCount;
            mIdDelta = idDelta;
            mIdRangeOffset = idRangeOffset;
        }

        /**
         * First valid low byte for this SubHeader.
         *
         * @return First valid low byte.
         */
        public int getFirstCode() {
            return mFirstCode;
        }

        /**
         * Number of valid low bytes for this SubHeader.
         *
         * @return Number of valid low bytes.
         */
        public int getEntryCount() {
            return mEntryCount;
        }

        /**
         * If the value obtained from the subarray is not 0 (which indicates the missing
         * glyph), you should add idDelta to it in order to get the glyphIndex. The value idDelta
         * permits the same subarray to be used for several different subheaders.
         * The idDelta arithmetic is modulo 65536.
         *
         * @return ID delta.
         */
        public int getIdDelta() {
            return mIdDelta;
        }

        /**
         * The firstCode and entryCount values specify a subrange that begins at firstCode and
         * has a length equal to the value of entryCount. This subrange stays within the 0-255 range
         * of the byte being mapped. Bytes outside of this subrange are mapped to glyph
         * index 0 (missing glyph).The offset of the byte within this subrange is then used as
         * index into a corresponding subarray of glyphIndexArray. This subarray is also of length
         * entryCount. The value of the idRangeOffset is the number of bytes past the actual
         * location of the idRangeOffset word where the glyphIndexArray element corresponding
         * to firstCode appears.
         *
         * @return ID range offset.
         */
        public int getIdRangeOffset() {
            return mIdRangeOffset;
        }
    }

    /**
     * SequentialMapGroup Record
     */
    @SuppressWarnings("unused")
    public static class SequentialMapGroupRecord {
        private final int mStartCharCode;
        private final int mEndCharCode;
        private final int mStartGlyphID;

        @SuppressWarnings("all")
        public SequentialMapGroupRecord(int startCharCode, int endCharCode, int startGlyphID) {
            mStartCharCode = startCharCode;
            mEndCharCode = endCharCode;
            mStartGlyphID = startGlyphID;
        }

        /**
         * First character code in this group; note that if this group is for one or more 16-bit
         * character codes (which is determined from the is32 array), this 32-bit value will have
         * the high 16-bits set to zero
         *
         * @return First character code.
         */
        public int getStartCharCode() {
            return mStartCharCode;
        }

        /**
         * Last character code in this group; same condition as listed above for the startCharCode
         *
         * @return Last character code.
         */
        public int getEndCharCode() {
            return mEndCharCode;
        }

        /**
         * Glyph index corresponding to the starting character code
         *
         * @return Glyph index.
         */
        public int getStartGlyphID() {
            return mStartGlyphID;
        }
    }

    /**
     * ConstantMapGroup Record
     */
    @SuppressWarnings("unused")
    public static class ConstantMapGroupRecord {
        private final int mStartCharCode;
        private final int mEndCharCode;
        private final int mGlyphID;

        @SuppressWarnings("all")
        public ConstantMapGroupRecord(int startCharCode, int endCharCode, int glyphID) {
            mStartCharCode = startCharCode;
            mEndCharCode = endCharCode;
            mGlyphID = glyphID;
        }

        /**
         * First character code in this group; note that if this group is for one or more 16-bit
         * character codes (which is determined from the is32 array), this 32-bit value will have
         * the high 16-bits set to zero
         *
         * @return First character code.
         */
        public int getStartCharCode() {
            return mStartCharCode;
        }

        /**
         * Last character code in this group; same condition as listed above for the startCharCode
         *
         * @return Last character code.
         */
        public int getEndCharCode() {
            return mEndCharCode;
        }

        /**
         * Glyph index to be used for all the characters in the group’s range.
         *
         * @return Glyph index.
         */
        public int getGlyphID() {
            return mGlyphID;
        }
    }

    /**
     * VariationSelector Record
     */
    @SuppressWarnings("unused")
    public static class VariationSelectorRecord {
        private final int mVarSelector;
        private final int mDefaultUVSOffset;
        private final int mNonDefaultUVSOffset;
        private final DefaultUVSTable mDefaultUVS;
        private final NonDefaultUVSTable mNonDefaultUVS;

        @SuppressWarnings("all")
        public VariationSelectorRecord(int varSelector, int defaultUVSOffset,
                                       int nonDefaultUVSOffset, DefaultUVSTable defaultUVS,
                                       NonDefaultUVSTable nonDefaultUVS) {
            mVarSelector = varSelector;
            mDefaultUVSOffset = defaultUVSOffset;
            mNonDefaultUVSOffset = nonDefaultUVSOffset;
            mDefaultUVS = defaultUVS;
            mNonDefaultUVS = nonDefaultUVS;
        }

        /**
         * Variation selector
         *
         * @return Variation selector.
         */
        public int getVarSelector() {
            return mVarSelector;
        }

        /**
         * Offset from the start of the format 14 subtable to Default UVS Table. May be 0.
         *
         * @return Offset.
         */
        public int getDefaultUVSOffset() {
            return mDefaultUVSOffset;
        }

        /**
         * Offset from the start of the format 14 subtable to Non-Default UVS Table. May be 0.
         *
         * @return Offset.
         */
        public int getNonDefaultUVSOffset() {
            return mNonDefaultUVSOffset;
        }

        /**
         * Default UVS table
         *
         * @return Default UVS table.
         */
        public DefaultUVSTable getDefaultUVS() {
            return mDefaultUVS;
        }

        /**
         * Non-Default UVS table
         *
         * @return Non-Default UVS table.
         */
        public NonDefaultUVSTable getNonDefaultUVS() {
            return mNonDefaultUVS;
        }
    }

    /**
     * Default UVS table
     */
    @SuppressWarnings("unused")
    public static class DefaultUVSTable {
        private final int mNumUnicodeValueRanges;
        private final List<UnicodeRangeRecord> mRanges;

        @SuppressWarnings("all")
        public DefaultUVSTable(int numUnicodeValueRanges, List<UnicodeRangeRecord> ranges) {
            mNumUnicodeValueRanges = numUnicodeValueRanges;
            mRanges = ranges;
        }

        /**
         * Number of Unicode character ranges.
         *
         * @return Number of Unicode character ranges.
         */
        public int getNumUnicodeValueRanges() {
            return mNumUnicodeValueRanges;
        }

        /**
         * Array of UnicodeRange records.
         *
         * @return Array of UnicodeRange records.
         */
        public List<UnicodeRangeRecord> getRanges() {
            return mRanges;
        }
    }

    /**
     * UnicodeRange Record
     */
    @SuppressWarnings("unused")
    public static class UnicodeRangeRecord {
        private final int mStartUnicodeValue;
        private final int mAdditionalCount;

        @SuppressWarnings("all")
        public UnicodeRangeRecord(int startUnicodeValue, int additionalCount) {
            mStartUnicodeValue = startUnicodeValue;
            mAdditionalCount = additionalCount;
        }

        /**
         * First value in this range
         *
         * @return First value.
         */
        public int getStartUnicodeValue() {
            return mStartUnicodeValue;
        }

        /**
         * Number of additional values in this range
         *
         * @return Number of additional values.
         */
        public int getAdditionalCount() {
            return mAdditionalCount;
        }
    }

    /**
     * Non-Default UVS table
     */
    @SuppressWarnings("unused")
    public static class NonDefaultUVSTable {
        private final int mNumUVSMappings;
        private final List<UVSMappingRecord> mUvsMappings;

        @SuppressWarnings("all")
        public NonDefaultUVSTable(int numUVSMappings, List<UVSMappingRecord> uvsMappings) {
            mNumUVSMappings = numUVSMappings;
            mUvsMappings = uvsMappings;
        }

        /**
         * Number of UVS Mappings that follow
         *
         * @return Number of UVS Mappings.
         */
        public int getNumUVSMappings() {
            return mNumUVSMappings;
        }

        /**
         * Array of UVSMapping records.
         *
         * @return UVSMapping records.
         */
        public List<UVSMappingRecord> getUvsMappings() {
            return mUvsMappings;
        }
    }

    /**
     * UVSMapping Record
     */
    @SuppressWarnings("unused")
    public static class UVSMappingRecord {
        private final int mUnicodeValue;
        private final int mGlyphID;

        @SuppressWarnings("all")
        public UVSMappingRecord(int unicodeValue, int glyphID) {
            mUnicodeValue = unicodeValue;
            mGlyphID = glyphID;
        }

        /**
         * Base Unicode value of the UVS
         *
         * @return Base Unicode value of the UVS.
         */
        public int getUnicodeValue() {
            return mUnicodeValue;
        }

        /**
         * Glyph ID of the UVS
         *
         * @return Glyph ID of the UVS.
         */
        public int getGlyphID() {
            return mGlyphID;
        }
    }

    /**
     * Sub Table
     */
    @SuppressWarnings("unused")
    public static class SubTable {
        private final int format;

        @SuppressWarnings("all")
        public SubTable(int format) {
            this.format = format;
        }

        /**
         * Format number
         *
         * @return Format number.
         */
        public int getFormat() {
            return format;
        }
    }

    /**
     * Sub Table Format 0
     */
    @SuppressWarnings("unused")
    public static class SubTable0 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int[] mGlyphIds;

        @SuppressWarnings("all")
        public SubTable0(int format, int length, int language, int[] glyphIds) {
            super(format);
            mLength = length;
            mLanguage = language;
            mGlyphIds = glyphIds;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * An array that maps character codes to glyph index values.
         *
         * @return Glyph Ids.
         */
        public int[] getGlyphIds() {
            return mGlyphIds;
        }
    }

    /**
     * Sub Table Format 2
     */
    @SuppressWarnings("unused")
    public static class SubTable2 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int[] mSubHeaderKeys;
        private final List<SubHeaderRecord> mSubHeaders;
        private final int[] mGlyphIndexArray;

        @SuppressWarnings("all")
        public SubTable2(int format, int length, int language, int[] subHeaderKeys,
                         List<SubHeaderRecord> subHeaders, int[] glyphIndexArray) {
            super(format);
            mLength = length;
            mLanguage = language;
            mSubHeaderKeys = subHeaderKeys;
            mSubHeaders = subHeaders;
            mGlyphIndexArray = glyphIndexArray;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * Array that maps high bytes to subHeaders: value is subHeader index × 8.
         *
         * @return SubHeaders keys.
         */
        public int[] getSubHeaderKeys() {
            return mSubHeaderKeys;
        }

        /**
         * Variable-length array of SubHeader records.
         *
         * @return SubHeader records.
         */
        public List<SubHeaderRecord> getSubHeaders() {
            return mSubHeaders;
        }

        /**
         * Variable-length array containing subarrays
         * used for mapping the low byte of 2-byte characters.
         *
         * @return Glyph index array.
         */
        public int[] getGlyphIndexArray() {
            return mGlyphIndexArray;
        }
    }

    /**
     * Sub Table Format 4
     */
    @SuppressWarnings("unused")
    public static class SubTable4 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int mSegCountX2;
        private final int mSearchRange;
        private final int mEntrySelector;
        private final int mRangeShift;
        private final int[] mEndCode;
        private final int[] mStartCode;
        private final int[] mIdDelta;
        private final int[] mIdRangeOffset;
        private final int[] mGlyphIdArray;

        @SuppressWarnings("all")
        public SubTable4(int format, int length, int language, int segCountX2, int searchRange,
                         int entrySelector, int rangeShift, int[] endCode, int[] startCode,
                         int[] idDelta, int[] idRangeOffset, int[] glyphIdArray) {
            super(format);
            mLength = length;
            mLanguage = language;
            mSegCountX2 = segCountX2;
            mSearchRange = searchRange;
            mEntrySelector = entrySelector;
            mRangeShift = rangeShift;
            mEndCode = endCode;
            mStartCode = startCode;
            mIdDelta = idDelta;
            mIdRangeOffset = idRangeOffset;
            mGlyphIdArray = glyphIdArray;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * 2 × segCount.
         *
         * @return 2 × segCount.
         */
        public int getSegCountX2() {
            return mSegCountX2;
        }

        /**
         * 2 × (2**floor(log2(segCount)))
         *
         * @return Search range.
         */
        public int getSearchRange() {
            return mSearchRange;
        }

        /**
         * log2(searchRange/2)
         *
         * @return Entry selector.
         */
        public int getEntrySelector() {
            return mEntrySelector;
        }

        /**
         * 2 × segCount - searchRange
         *
         * @return Range shift.
         */
        public int getRangeShift() {
            return mRangeShift;
        }

        /**
         * End characterCode for each segment, last=0xFFFF.
         *
         * @return End characterCode.
         */
        public int[] getEndCode() {
            return mEndCode;
        }

        /**
         * Start character code for each segment.
         *
         * @return Start character.
         */
        public int[] getStartCode() {
            return mStartCode;
        }

        /**
         * Delta for all character codes in segment.
         *
         * @return Delta for all character codes.
         */
        public int[] getIdDelta() {
            return mIdDelta;
        }

        /**
         * Offsets into glyphIdArray or 0
         *
         * @return Offsets into glyphIdArray.
         */
        public int[] getIdRangeOffset() {
            return mIdRangeOffset;
        }

        /**
         * Glyph index array (arbitrary length)
         *
         * @return Glyph index array.
         */
        public int[] getGlyphIdArray() {
            return mGlyphIdArray;
        }
    }

    /**
     * Sub Table Format 6
     */
    @SuppressWarnings("unused")
    public static class SubTable6 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int mFirstCode;
        private final int mEntryCount;
        private final int[] mGlyphIdArray;

        @SuppressWarnings("all")
        public SubTable6(int format, int length, int language, int firstCode, int entryCount,
                         int[] glyphIdArray) {
            super(format);
            mLength = length;
            mLanguage = language;
            mFirstCode = firstCode;
            mEntryCount = entryCount;
            mGlyphIdArray = glyphIdArray;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * First character code of subrange.
         *
         * @return First character code.
         */
        public int getFirstCode() {
            return mFirstCode;
        }

        /**
         * Number of character codes in subrange.
         *
         * @return Number of character codes.
         */
        public int getEntryCount() {
            return mEntryCount;
        }

        /**
         * Array of glyph index values for character codes in the range.
         *
         * @return Glyph Id array.
         */
        public int[] getGlyphIdArray() {
            return mGlyphIdArray;
        }
    }

    /**
     * Sub Table Format 8
     */
    @SuppressWarnings("unused")
    public static class SubTable8 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int[] mIs32;
        private final int mNumGroups;
        private final List<SequentialMapGroupRecord> mGroups;

        @SuppressWarnings("all")
        public SubTable8(int format, int length, int language, int[] is32, int numGroups,
                         List<SequentialMapGroupRecord> groups) {
            super(format);
            mLength = length;
            mLanguage = language;
            mIs32 = is32;
            mNumGroups = numGroups;
            mGroups = groups;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * Tightly packed array of bits (8K bytes total) indicating whether the particular
         * 16-bit (index) value is the start of a 32-bit character code
         *
         * @return Tightly packed array of bits.
         */
        public int[] getIs32() {
            return mIs32;
        }

        /**
         * Number of groupings which follow
         *
         * @return Number of groupings.
         */
        public int getNumGroups() {
            return mNumGroups;
        }

        /**
         * Array of SequentialMapGroup records.
         *
         * @return SequentialMapGroup records.
         */
        public List<SequentialMapGroupRecord> getGroups() {
            return mGroups;
        }
    }

    /**
     * Sub Table Format 10
     */
    @SuppressWarnings("unused")
    public static class SubTable10 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int mStartCharCode;
        private final int mNumChars;
        private final int[] mGlyphs;

        @SuppressWarnings("all")
        public SubTable10(int format, int length, int language, int startCharCode, int numChars,
                          int[] glyphs) {
            super(format);
            mLength = length;
            mLanguage = language;
            mStartCharCode = startCharCode;
            mNumChars = numChars;
            mGlyphs = glyphs;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * First character code covered
         *
         * @return First character code.
         */
        public int getStartCharCode() {
            return mStartCharCode;
        }

        /**
         * Number of character codes covered
         *
         * @return Number of character codes.
         */
        public int getNumChars() {
            return mNumChars;
        }

        /**
         * Array of glyph indices for the character codes covered
         *
         * @return Glyphs.
         */
        public int[] getGlyphs() {
            return mGlyphs;
        }
    }

    /**
     * Sub Table Format 12
     */
    @SuppressWarnings("unused")
    public static class SubTable12 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int mNumGroups;
        private final List<SequentialMapGroupRecord> mGroups;

        @SuppressWarnings("all")
        public SubTable12(int format, int length, int language, int numGroups,
                          List<SequentialMapGroupRecord> groups) {
            super(format);
            mLength = length;
            mLanguage = language;
            mNumGroups = numGroups;
            mGroups = groups;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * Number of groupings which follow
         *
         * @return Number of groupings.
         */
        public int getNumGroups() {
            return mNumGroups;
        }

        /**
         * Array of SequentialMapGroup records.
         *
         * @return SequentialMapGroup records.
         */
        public List<SequentialMapGroupRecord> getGroups() {
            return mGroups;
        }
    }

    /**
     * Sub Table Format 13
     */
    @SuppressWarnings("unused")
    public static class SubTable13 extends SubTable {
        private final int mLength;
        private final int mLanguage;
        private final int mNumGroups;
        private final List<ConstantMapGroupRecord> mGroups;

        @SuppressWarnings("all")
        public SubTable13(int format, int length, int language, int numGroups,
                          List<ConstantMapGroupRecord> groups) {
            super(format);
            mLength = length;
            mLanguage = language;
            mNumGroups = numGroups;
            mGroups = groups;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * For requirements on use of the language field,
         * see “Use of the language field in 'cmap' subtables” in this document.
         *
         * @return Language.
         */
        public int getLanguage() {
            return mLanguage;
        }

        /**
         * Number of groupings which follow
         *
         * @return Number of groupings.
         */
        public int getNumGroups() {
            return mNumGroups;
        }

        /**
         * Array of SequentialMapGroup records.
         *
         * @return SequentialMapGroup records.
         */
        public List<ConstantMapGroupRecord> getGroups() {
            return mGroups;
        }
    }

    /**
     * Sub Table Format 14
     */
    @SuppressWarnings("unused")
    public static class SubTable14 extends SubTable {
        private final int mLength;
        private final int mNumVarSelectorRecords;
        private final List<VariationSelectorRecord> mVarSelectors;

        @SuppressWarnings("all")
        public SubTable14(int format, int length, int numVarSelectorRecords,
                          List<VariationSelectorRecord> varSelectors) {
            super(format);
            mLength = length;
            mNumVarSelectorRecords = numVarSelectorRecords;
            mVarSelectors = varSelectors;
        }

        /**
         * This is the length in bytes of the subtable.
         *
         * @return Length.
         */
        public int getLength() {
            return mLength;
        }

        /**
         * Number of variation Selector Records
         *
         * @return Number of variation Selector Records.
         */
        public int getNumVarSelectorRecords() {
            return mNumVarSelectorRecords;
        }

        /**
         * Array of VariationSelector records.
         *
         * @return Array of VariationSelector records.
         */
        public List<VariationSelectorRecord> getVarSelectors() {
            return mVarSelectors;
        }
    }
}
