package am.util.opentype.tables;

/**
 * LongHorMetric Record
 * In a font with TrueType outlines, xMin and xMax values for each glyph are given in the 'glyf'
 * table. The advance width (“aw”) and left side bearing (“lsb”) can be derived from the glyph
 * “phantom points”, which are computed by the TrueType rasterizer; or they can be obtained from
 * the 'hmtx' table. In a font with CFF or CFF2 outlines, xMin (= left side bearing) and xMax
 * values can be obtained from the CFF / CFF2 rasterizer.
 */
@SuppressWarnings("unused")
public class LongHorMetricRecord {
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
