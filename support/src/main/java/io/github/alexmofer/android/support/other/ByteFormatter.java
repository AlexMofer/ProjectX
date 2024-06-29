package io.github.alexmofer.android.support.other;

/**
 * 字节格式化
 * Created by Alex on 2023/3/8.
 */
public class ByteFormatter {

    @SuppressWarnings("PointlessBitwiseExpression")
    public static final int FLAG_SHORTER = 1 << 0;
    public static final int FLAG_CALCULATE_ROUNDED = 1 << 1;
    public static final int FLAG_SI_UNITS = 1 << 2;
    public static final int FLAG_IEC_UNITS = 1 << 3;
    public static final int FLAG_NO_ZERO_END = 1 << 4;
    private static final String BYTE_SHORT = "B";
    private static final String KILOBYTE_SHORT = "kB";
    private static final String MEGABYTE_SHORT = "MB";
    private static final String GIGABYTE_SHORT = "GB";
    private static final String TERABYTE_SHORT = "TB";
    private static final String PETABYTE_SHORT = "PB";

    private ByteFormatter() {
        //no instance
    }

    /**
     * Formats a content size to be in the form of bytes, kilobytes, megabytes, etc.
     *
     * @param sizeBytes size value to be formatted, in bytes
     * @param flags     {@link #FLAG_SHORTER}、{@link #FLAG_CALCULATE_ROUNDED}、{@link #FLAG_SI_UNITS}、{@link #FLAG_IEC_UNITS}
     * @return formatted string with the number
     */
    public static String formatFileSize(long sizeBytes, int flags) {
        final BytesResult res = formatBytes(sizeBytes, flags);
        return res.value + " " + res.units;
    }

    /**
     * Formats a content size to be in the form of bytes, kilobytes, megabytes, etc.
     *
     * @param sizeBytes size value to be formatted, in bytes
     * @return formatted string with the number
     */
    public static String formatFileSize(long sizeBytes) {
        return formatFileSize(sizeBytes, FLAG_IEC_UNITS);
    }

    /**
     * Like {@link #formatFileSize}, but trying to generate shorter numbers
     * (showing fewer digits of precision).
     */
    public static String formatShortFileSize(long sizeBytes) {
        final BytesResult res = formatBytes(sizeBytes, FLAG_IEC_UNITS | FLAG_SHORTER);
        return res.value + " " + res.units;
    }

    private static BytesResult formatBytes(long sizeBytes, int flags) {
        final int unit = ((flags & FLAG_IEC_UNITS) != 0) ? 1024 : 1000;
        final boolean isNegative = (sizeBytes < 0);
        float result = isNegative ? -sizeBytes : sizeBytes;
        String units = BYTE_SHORT;
        long mult = 1;
        if (result > 900) {
            units = KILOBYTE_SHORT;
            mult = unit;
            result = result / unit;
        }
        if (result > 900) {
            units = MEGABYTE_SHORT;
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = GIGABYTE_SHORT;
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = TERABYTE_SHORT;
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = PETABYTE_SHORT;
            mult *= unit;
            result = result / unit;
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        final int roundFactor;
        final String roundFormat;
        if (mult == 1 || result >= 100) {
            roundFactor = 1;
            roundFormat = "%.0f";
        } else if (result < 1) {
            roundFactor = 100;
            roundFormat = "%.2f";
        } else if (result < 10) {
            if ((flags & FLAG_SHORTER) != 0) {
                roundFactor = 10;
                roundFormat = "%.1f";
            } else {
                roundFactor = 100;
                roundFormat = "%.2f";
            }
        } else { // 10 <= result < 100
            if ((flags & FLAG_SHORTER) != 0) {
                roundFactor = 1;
                roundFormat = "%.0f";
            } else {
                roundFactor = 100;
                roundFormat = "%.2f";
            }
        }

        if (isNegative) {
            result = -result;
        }
        String roundedString = String.format(roundFormat, result);
        if ((flags & FLAG_NO_ZERO_END) == FLAG_NO_ZERO_END) {
            if (roundedString.contains(".")) {
                while (roundedString.endsWith("0")) {
                    roundedString = roundedString.substring(0, roundedString.length() - 1);
                }
                if (roundedString.endsWith(".")) {
                    roundedString = roundedString.substring(0, roundedString.length() - 1);
                }
            }
        }
        // Note this might overflow if abs(result) >= Long.MAX_VALUE / 100, but that's like 80PB so
        // it's okay (for now)...
        final long roundedBytes =
                (flags & FLAG_CALCULATE_ROUNDED) == 0 ? 0
                        : (((long) Math.round(result * roundFactor)) * mult / roundFactor);
        return new BytesResult(roundedString, units, roundedBytes);
    }

    private static class BytesResult {
        public final String value;
        public final String units;
        public final long roundedBytes;

        public BytesResult(String value, String units, long roundedBytes) {
            this.value = value;
            this.units = units;
            this.roundedBytes = roundedBytes;
        }
    }
}
