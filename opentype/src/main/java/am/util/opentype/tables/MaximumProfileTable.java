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
 * Maximum Profile Table
 * This table establishes the memory requirements for this font. Fonts with CFF data must use
 * Version 0.5 of this table, specifying only the numGlyphs field. Fonts with TrueType outlines
 * must use Version 1.0 of this table, where all data is required.
 */
@SuppressWarnings("unused")
public class MaximumProfileTable {

    private final float mVersion;
    private final int mNumGlyphs;
    private final int mMaxPoints;
    private final int mMaxContours;
    private final int mMaxCompositePoints;
    private final int mMaxCompositeContours;
    private final int mMaxZones;
    private final int mMaxTwilightPoints;
    private final int mMaxStorage;
    private final int mMaxFunctionDefs;
    private final int mMaxInstructionDefs;
    private final int mMaxStackElements;
    private final int mMaxSizeOfInstructions;
    private final int mMaxComponentElements;
    private final int mMaxComponentDepth;

    public MaximumProfileTable(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_MAXP)
            throw new IOException();
        reader.seek(record.getOffset());
        final float version = reader.readFixed();
        final int numGlyphs = reader.readUnsignedShort();
        final int maxPoints;
        final int maxContours;
        final int maxCompositePoints;
        final int maxCompositeContours;
        final int maxZones;
        final int maxTwilightPoints;
        final int maxStorage;
        final int maxFunctionDefs;
        final int maxInstructionDefs;
        final int maxStackElements;
        final int maxSizeOfInstructions;
        final int maxComponentElements;
        final int maxComponentDepth;
        if (version < 1.0f) {
            maxPoints = -1;
            maxContours = -1;
            maxCompositePoints = -1;
            maxCompositeContours = -1;
            maxZones = -1;
            maxTwilightPoints = -1;
            maxStorage = -1;
            maxFunctionDefs = -1;
            maxInstructionDefs = -1;
            maxStackElements = -1;
            maxSizeOfInstructions = -1;
            maxComponentElements = -1;
            maxComponentDepth = -1;
        } else {
            maxPoints = reader.readUnsignedShort();
            maxContours = reader.readUnsignedShort();
            maxCompositePoints = reader.readUnsignedShort();
            maxCompositeContours = reader.readUnsignedShort();
            maxZones = reader.readUnsignedShort();
            maxTwilightPoints = reader.readUnsignedShort();
            maxStorage = reader.readUnsignedShort();
            maxFunctionDefs = reader.readUnsignedShort();
            maxInstructionDefs = reader.readUnsignedShort();
            maxStackElements = reader.readUnsignedShort();
            maxSizeOfInstructions = reader.readUnsignedShort();
            maxComponentElements = reader.readUnsignedShort();
            maxComponentDepth = reader.readUnsignedShort();
        }
        mVersion = version;
        mNumGlyphs = numGlyphs;
        mMaxPoints = maxPoints;
        mMaxContours = maxContours;
        mMaxCompositePoints = maxCompositePoints;
        mMaxCompositeContours = maxCompositeContours;
        mMaxZones = maxZones;
        mMaxTwilightPoints = maxTwilightPoints;
        mMaxStorage = maxStorage;
        mMaxFunctionDefs = maxFunctionDefs;
        mMaxInstructionDefs = maxInstructionDefs;
        mMaxStackElements = maxStackElements;
        mMaxSizeOfInstructions = maxSizeOfInstructions;
        mMaxComponentElements = maxComponentElements;
        mMaxComponentDepth = maxComponentDepth;
    }

    /**
     * version 0.5 is 0x00005000
     * version 1.0 is 0x00010000
     *
     * @return Version.
     */
    public float getVersion() {
        return mVersion;
    }

    /**
     * The number of glyphs in the font.
     *
     * @return Number of glyphs.
     */
    public int getNumGlyphs() {
        return mNumGlyphs;
    }

    /**
     * Maximum points in a non-composite glyph.
     * (version 1.0 only)
     *
     * @return Maximum points.
     */
    public int getMaxPoints() {
        return mMaxPoints;
    }

    /**
     * Maximum contours in a non-composite glyph.
     * (version 1.0 only)
     *
     * @return Maximum contours.
     */
    public int getMaxContours() {
        return mMaxContours;
    }

    /**
     * Maximum points in a composite glyph.
     * (version 1.0 only)
     *
     * @return Maximum composite points.
     */
    public int getMaxCompositePoints() {
        return mMaxCompositePoints;
    }

    /**
     * Maximum contours in a composite glyph.
     * (version 1.0 only)
     *
     * @return Maximum composite contours.
     */
    public int getMaxCompositeContours() {
        return mMaxCompositeContours;
    }

    /**
     * 1 if instructions do not use the twilight zone (Z0), or 2 if instructions do use Z0;
     * should be set to 2 in most cases.
     * (version 1.0 only)
     *
     * @return Maximum zones.
     */
    public int getMaxZones() {
        return mMaxZones;
    }

    /**
     * Maximum points used in Z0.
     * (version 1.0 only)
     *
     * @return Maximum points used in Z0.
     */
    public int getMaxTwilightPoints() {
        return mMaxTwilightPoints;
    }

    /**
     * Number of Storage Area locations.
     * (version 1.0 only)
     *
     * @return Maximum storage.
     */
    public int getMaxStorage() {
        return mMaxStorage;
    }

    /**
     * Number of FDEFs, equal to the highest function number + 1.
     * (version 1.0 only)
     *
     * @return Number of FDEFs.
     */
    public int getMaxFunctionDefs() {
        return mMaxFunctionDefs;
    }

    /**
     * Number of IDEFs.
     * (version 1.0 only)
     *
     * @return Number of IDEFs.
     */
    public int getMaxInstructionDefs() {
        return mMaxInstructionDefs;
    }

    /**
     * Maximum stack depth across Font Program ('fpgm' table), CVT Program ('prep' table) and
     * all glyph instructions (in the 'glyf' table).
     * (version 1.0 only)
     *
     * @return Maximum stack depth.
     */
    public int getMaxStackElements() {
        return mMaxStackElements;
    }

    /**
     * Maximum byte count for glyph instructions.
     * (version 1.0 only)
     *
     * @return Maximum byte count for glyph instructions.
     */
    public int getMaxSizeOfInstructions() {
        return mMaxSizeOfInstructions;
    }

    /**
     * Maximum number of components referenced at “top level” for any composite glyph.
     * (version 1.0 only)
     *
     * @return Maximum number of components referenced.
     */
    public int getMaxComponentElements() {
        return mMaxComponentElements;
    }

    /**
     * Maximum levels of recursion; 1 for simple components.
     * (version 1.0 only)
     *
     * @return Maximum levels of recursion.
     */
    public int getMaxComponentDepth() {
        return mMaxComponentDepth;
    }
}
