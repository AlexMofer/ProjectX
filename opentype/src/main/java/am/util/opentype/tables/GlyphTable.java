package am.util.opentype.tables;

import java.io.IOException;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Glyph Data
 * This table contains information that describes the glyphs in the font in the TrueType outline
 * format. Information regarding the rasterizer (scaler) refers to the TrueType rasterizer.
 * The 'glyf' table is comprised of a list of glyph data blocks, each of which provides
 * the description for a single glyph. Glyphs are referenced by identifiers (glyph IDs),
 * which are sequential integers beginning at zero. The total number of glyphs is specified
 * by the numGlyphs field in the 'maxp' table. The 'glyf' table does not include any overall
 * table header or records providing offsets to glyph data blocks. Rather, the 'loca' table
 * provides an array of offsets, indexed by glyph IDs, which provide the location of each
 * glyph data block within the 'glyf' table. Note that the 'glyf' table must always be used
 * in conjunction with the 'loca' and 'maxp' tables. The size of each glyph data block is
 * inferred from the difference between two consecutive offsets in the 'loca' table
 * (with one extra offset provided to give the size of the last glyph data block).
 * As a result of the 'loca' format, glyph data blocks within the 'glyf' table must be
 * in glyph ID order.
 */
@SuppressWarnings("unused")
public class GlyphTable {

    private final int mNumberOfContours;
    private final int mXMin;
    private final int mYMin;
    private final int mXMax;
    private final int mYMax;
    private final Object mGlyphDescription;

    public GlyphTable(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_GLYF)
            throw new IOException();
        reader.seek(record.getOffset());
        final int numberOfContours = reader.readShort();
        final int xMin = reader.readShort();
        final int yMin = reader.readShort();
        final int xMax = reader.readShort();
        final int yMax = reader.readShort();
        final Object glyphDescription;
        if (numberOfContours >= 0) {
            // Simple Glyph Description
            final int[] endPtsOfContours = new int[numberOfContours];
            for (int i = 0; i < endPtsOfContours.length; i++) {
                endPtsOfContours[i] = reader.readUnsignedShort();
            }
            final int instructionLength = reader.readUnsignedShort();
            final int[] instructions = new int[instructionLength];
            for (int i = 0; i < instructions.length; i++) {
                instructions[i] = reader.readUnsignedByte();
            }
            // TODO: 2018/9/27 flags xCoordinates yCoordinates
            // uint8            flags[variable]          Array of flag elements.
            // uint8 or int16   xCoordinates[variable]   Contour point x-coordinates.
            // uint8 or int16   yCoordinates[variable]   Contour point y-coordinates.
            glyphDescription = new SimpleGlyphDescription(endPtsOfContours, instructionLength,
                    instructions, null, null, null);
        } else {
            // Composite Glyph Description
            final int flags = reader.readUnsignedShort();
            final int glyphIndex = reader.readUnsignedShort();
            // TODO: 2018/9/27  argument1 argument2 and so on
            // uint8, int8, uint16 or int16   argument1   x-offset for component or point number.
            // uint8, int8, uint16 or int16   argument2   y-offset for component or point number.
            glyphDescription = new CompositeGlyphDescription(flags, glyphIndex,
                    0, 0);
        }
        mNumberOfContours = numberOfContours;
        mXMin = xMin;
        mYMin = yMin;
        mXMax = xMax;
        mYMax = yMax;
        mGlyphDescription = glyphDescription;
    }

    /**
     * If the number of contours is greater than or equal to zero, this is a simple glyph.
     * If negative, this is a composite glyph — the value -1 should be used for composite glyphs.
     *
     * @return The number of contours.
     */
    public int getNumberOfContours() {
        return mNumberOfContours;
    }

    /**
     * Minimum x for coordinate data.
     *
     * @return Minimum x for coordinate data.
     */
    public int getXMin() {
        return mXMin;
    }

    /**
     * Minimum y for coordinate data.
     *
     * @return Minimum y for coordinate data.
     */
    public int getYMin() {
        return mYMin;
    }

    /**
     * Maximum x for coordinate data.
     *
     * @return Maximum x for coordinate data.
     */
    public int getXMax() {
        return mXMax;
    }

    /**
     * Maximum y for coordinate data.
     *
     * @return Maximum y for coordinate data.
     */
    public int getYMax() {
        return mYMax;
    }

    /**
     * Glyph Description
     *
     * @return Glyph Description
     */
    public Object getGlyphDescription() {
        return mGlyphDescription;
    }

    /**
     * Simple Glyph Description
     */
    @SuppressWarnings("unused")
    public static class SimpleGlyphDescription {

        private final int[] mEndPtsOfContours;
        private final int mInstructionLength;
        private final int[] mInstructions;
        private final int[] mFlags;
        private final int[] mXCoordinates;
        private final int[] mYCoordinates;

        @SuppressWarnings("all")
        public SimpleGlyphDescription(int[] endPtsOfContours, int instructionLength,
                                      int[] instructions,
                                      int[] flags, int[] xCoordinates, int[] yCoordinates) {
            mEndPtsOfContours = endPtsOfContours;
            mInstructionLength = instructionLength;
            mInstructions = instructions;
            mFlags = flags;
            mXCoordinates = xCoordinates;
            mYCoordinates = yCoordinates;
        }

        /**
         * Array of point indices for the last point of each contour, in increasing numeric order.
         *
         * @return Array of point indices for the last point of each contour.
         */
        public int[] getEndPtsOfContours() {
            return mEndPtsOfContours;
        }

        /**
         * Total number of bytes for instructions. If instructionLength is zero,
         * no instructions are present for this glyph, and this field is followed directly
         * by the flags field.
         *
         * @return Total number of bytes for instructions.
         */
        public int getInstructionLength() {
            return mInstructionLength;
        }

        /**
         * Array of instruction byte code for the glyph.
         *
         * @return Instructions.
         */
        public int[] getInstructions() {
            return mInstructions;
        }

        /**
         * Array of flag elements.
         *
         * @return Flags.
         */
        public int[] getFlags() {
            return mFlags;
        }

        /**
         * Contour point x-coordinates. Coordinate for the first point is relative to (0,0);
         * others are relative to previous point.
         *
         * @return Contour point x-coordinates.
         */
        public int[] getXCoordinates() {
            return mXCoordinates;
        }

        /**
         * Contour point y-coordinates. Coordinate for the first point is relative to (0,0);
         * others are relative to previous point.
         *
         * @return Contour point y-coordinates.
         */
        public int[] getYCoordinates() {
            return mYCoordinates;
        }
    }

    /**
     * Composite Glyph Description
     */
    @SuppressWarnings("unused")
    public static class CompositeGlyphDescription {
        private final int mFlags;
        private final int mGlyphIndex;
        private final int mArgument1;
        private final int mArgument2;

        @SuppressWarnings("all")
        public CompositeGlyphDescription(int flags, int glyphIndex, int argument1, int argument2) {
            mFlags = flags;
            mGlyphIndex = glyphIndex;
            mArgument1 = argument1;
            mArgument2 = argument2;
        }

        /**
         * Component flag
         *
         * @return Component flag.
         */
        public int getFlags() {
            return mFlags;
        }

        /**
         * Glyph index of component
         *
         * @return Glyph index of component.
         */
        public int getGlyphIndex() {
            return mGlyphIndex;
        }

        /**
         * x-offset for component or point number; type depends on bits 0 and 1 in component flags
         *
         * @return x-offset for component or point number.
         */
        public int getArgument1() {
            return mArgument1;
        }

        /**
         * y-offset for component or point number; type depends on bits 0 and 1 in component flags
         *
         * @return y-offset for component or point number.
         */
        public int getArgument2() {
            return mArgument2;
        }
    }
}
