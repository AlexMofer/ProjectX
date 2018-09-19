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

    public float getVersion() {
        return mVersion;
    }

    public int getNumGlyphs() {
        return mNumGlyphs;
    }

    public int getMaxPoints() {
        return mMaxPoints;
    }

    public int getMaxContours() {
        return mMaxContours;
    }

    public int getMaxCompositePoints() {
        return mMaxCompositePoints;
    }

    public int getMaxCompositeContours() {
        return mMaxCompositeContours;
    }

    public int getMaxZones() {
        return mMaxZones;
    }

    public int getMaxTwilightPoints() {
        return mMaxTwilightPoints;
    }

    public int getMaxStorage() {
        return mMaxStorage;
    }

    public int getMaxFunctionDefs() {
        return mMaxFunctionDefs;
    }

    public int getMaxInstructionDefs() {
        return mMaxInstructionDefs;
    }

    public int getMaxStackElements() {
        return mMaxStackElements;
    }

    public int getMaxSizeOfInstructions() {
        return mMaxSizeOfInstructions;
    }

    public int getMaxComponentElements() {
        return mMaxComponentElements;
    }

    public int getMaxComponentDepth() {
        return mMaxComponentDepth;
    }
}
