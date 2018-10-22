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
import java.util.Arrays;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Index to Location Table
 * The indexToLoc table stores the offsets to the locations of the glyphs in the font,
 * relative to the beginning of the glyphData table. In order to compute the length of
 * the last glyph element, there is an extra entry after the last valid index.
 */
@SuppressWarnings("unused")
public class IndexToLocationTable extends BaseTable {

    private final int[] mOffsets;

    public IndexToLocationTable(OpenTypeReader reader, TableRecord record, int indexToLocFormat,
                                int numGlyphs) throws IOException {
        super(record);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexToLocationTable)) return false;
        if (!super.equals(o)) return false;
        IndexToLocationTable that = (IndexToLocationTable) o;
        return Arrays.equals(mOffsets, that.mOffsets);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mOffsets);
        return result;
    }

    @Override
    public String toString() {
        return "IndexToLocationTable{" +
                "record=" + String.valueOf(getTableRecord()) +
                ", offsets=" + Arrays.toString(mOffsets) +
                '}';
    }
}
