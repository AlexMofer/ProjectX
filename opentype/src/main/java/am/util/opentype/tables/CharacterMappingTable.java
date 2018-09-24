package am.util.opentype.tables;

import java.io.IOException;
import java.util.ArrayList;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Character to Glyph Index Mapping Table
 * This table defines the mapping of character codes to the glyph index values used in the font.
 * It may contain more than one subtable, in order to support more than one character
 * encoding scheme.
 */
public class CharacterMappingTable {

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
        final int format = reader.readUnsignedShort();
        if (format == 0) {
            // Format 0: Byte encoding table
            // This is the Apple standard character to glyph index mapping table.
            final int length = reader.readUnsignedShort();
            final int language = reader.readUnsignedShort();
            final int[] glyphIds = new int[256];
            for (int i = 0; i < glyphIds.length; i++) {
                glyphIds[i] = reader.readUnsignedByte();
            }
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
                varSelectors.add(new VariationSelectorRecord(varSelector, defaultUVSOffset,
                        nonDefaultUVSOffset));
            }
        } else {
            // Unknown

        }
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

        @SuppressWarnings("all")
        public VariationSelectorRecord(int varSelector, int defaultUVSOffset,
                                       int nonDefaultUVSOffset) {
            mVarSelector = varSelector;
            mDefaultUVSOffset = defaultUVSOffset;
            mNonDefaultUVSOffset = nonDefaultUVSOffset;
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
    }
}
