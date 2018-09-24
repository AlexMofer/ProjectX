package am.util.opentype.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * Horizontal Metrics Table
 * Glyph metrics used for horizontal text layout include glyph advance widths, side bearings
 * and X-direction min and max values (xMin, xMax). These are derived using a combination of
 * the glyph outline data ('glyf', 'CFF ' or CFF2) and the horizontal metrics table.
 * The horizontal metrics ('hmtx') table provides glyph advance widths and left side bearings.
 */
@SuppressWarnings("unused")
public class HorizontalMetricsTable {

    private final List<LongHorMetricRecord> mHMetrics;
    private final short[] mLeftSideBearings;

    public HorizontalMetricsTable(OpenTypeReader reader, TableRecord record,
                                  int numberOfHMetrics, int numGlyphs) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_HMTX)
            throw new IOException();
        reader.seek(record.getOffset());
        final ArrayList<LongHorMetricRecord> hMetrics = new ArrayList<>();
        for (int i = 0; i < numberOfHMetrics; i++) {
            hMetrics.add(new LongHorMetricRecord(reader.readUnsignedShort(), reader.readShort()));
        }
        final short[] leftSideBearings;
        if (numGlyphs > numberOfHMetrics) {
            leftSideBearings = new short[numGlyphs - numberOfHMetrics];

            for (int i = numberOfHMetrics; i < numGlyphs; i++) {
                leftSideBearings[i - numberOfHMetrics] = reader.readShort();
            }
        } else {
            leftSideBearings = null;
        }
        mHMetrics = hMetrics;
        mLeftSideBearings = leftSideBearings;
    }

    /**
     * Paired advance width and left side bearing values for each glyph.
     * Records are indexed by glyph ID.
     *
     * @param index glyph ID
     * @return LongHorMetric Record.
     */
    public LongHorMetricRecord getLongHorMetricRecord(int index) {
        return mHMetrics.get(index);
    }

    /**
     * Left side bearings for glyph IDs greater than or equal to numberOfHMetrics.
     *
     * @param index The number of elements in the left side bearing will be derived from
     *              numberOfHMetrics field in the 'hhea' table plus
     *              the numGlyphs field in the 'maxp' table.
     * @return Left side bearing.
     */
    public short getLeftSideBearing(int index) {
        return mLeftSideBearings == null ? 0 : mLeftSideBearings[index];
    }
}
