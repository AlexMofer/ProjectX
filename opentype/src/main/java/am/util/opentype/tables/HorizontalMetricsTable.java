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
    private final int[] mLeftSideBearings;

    public HorizontalMetricsTable(OpenTypeReader reader, TableRecord record,
                                  int numberOfHMetrics, int numGlyphs) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_HMTX)
            throw new IOException();
        reader.seek(record.getOffset());
        final ArrayList<LongHorMetricRecord> hMetrics = new ArrayList<>();
        for (int i = 0; i < numberOfHMetrics; i++) {
            hMetrics.add(new LongHorMetricRecord(reader.readUnsignedShort(), reader.readShort()));
        }
        final int[] leftSideBearings;
        if (numGlyphs > numberOfHMetrics) {
            leftSideBearings = new int[numGlyphs - numberOfHMetrics];

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
    public int getLeftSideBearing(int index) {
        return mLeftSideBearings == null ? 0 : mLeftSideBearings[index];
    }

    /**
     * LongHorMetric Record
     * In a font with TrueType outlines, xMin and xMax values for each glyph are given in the 'glyf'
     * table. The advance width (“aw”) and left side bearing (“lsb”) can be derived from the glyph
     * “phantom points”, which are computed by the TrueType rasterizer; or they can be obtained from
     * the 'hmtx' table. In a font with CFF or CFF2 outlines, xMin (= left side bearing) and xMax
     * values can be obtained from the CFF / CFF2 rasterizer.
     */
    @SuppressWarnings("unused")
    public static class LongHorMetricRecord {
        private final int mAdvanceWidth;
        private final int mLsb;

        @SuppressWarnings("all")
        public LongHorMetricRecord(int advanceWidth, int lsb) {
            mAdvanceWidth = advanceWidth;
            mLsb = lsb;
        }

        /**
         * Advance width, in font design units.
         *
         * @return Advance width.
         */
        public int getAdvanceWidth() {
            return mAdvanceWidth;
        }

        /**
         * Glyph left side bearing, in font design units.
         *
         * @return Glyph left side bearing.
         */
        public int getLsb() {
            return mLsb;
        }
    }
}
