package am.util.opentype.tables;

import java.io.IOException;

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
    }
}
