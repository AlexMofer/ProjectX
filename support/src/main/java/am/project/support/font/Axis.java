package am.project.support.font;

/**
 * 字体对称轴
 * Created by Alex on 2018/8/30.
 */
class Axis {
    public static final String TAG_ITAL = "ital";// Italic
    public static final String TAG_OPSZ = "opsz";// Optical size
    public static final String TAG_SLNT = "slnt";// Slant
    public static final String TAG_WDTH = "wdth";// Width
    public static final String TAG_WGHT = "wght";// Weight
    private final String mTag;// 标签
    private final float mStyleValue;// 值

    Axis(String tag, float value) {
        mTag = tag;
        mStyleValue = value;
    }

    String getTag() {
        return mTag;
    }

    TypefaceAxis convert() {
        return new TypefaceAxis(mTag, mStyleValue);
    }
}
