package am.util.opentype.tables;

import java.io.IOException;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Index to Location Table
 * The indexToLoc table stores the offsets to the locations of the glyphs in the font,
 * relative to the beginning of the glyphData table. In order to compute the length of
 * the last glyph element, there is an extra entry after the last valid index.
 */
@SuppressWarnings("unused")
public class IndexToLocationTable {

    private final int[] mOffsets;

    public IndexToLocationTable(OpenTypeReader reader, TableRecord record, int indexToLocFormat,
                                int numGlyphs) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_LOCA)
            throw new IOException();
        reader.seek(record.getOffset());
        final int size = numGlyphs + 1;
        final int[] offsets = new int[size];
        if (indexToLocFormat == 0) {
            // Short version
            for (int i = 0; i < size; i++) {
                offsets[i] = reader.readUnsignedShort();
            }
        } else {
            // Long version
            for (int i = 0; i < size; i++) {
                offsets[i] = reader.readUnsignedInt();
            }
        }
        mOffsets = offsets;
    }

    /**
     * The actual local offset
     *
     * @return The actual local offset.
     */
    public int[] getOffsets() {
        return mOffsets;
    }
}
