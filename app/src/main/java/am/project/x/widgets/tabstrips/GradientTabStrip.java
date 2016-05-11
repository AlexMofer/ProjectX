package am.project.x.widgets.tabstrips;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.BaseTabStrip;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * 滑动渐变TabStrip，Item不建议超过5个
 * @author Alex
 */
public class GradientTabStrip extends BaseTabStrip {

    public static final int DEFAULT_TEXT_SIZE = 16;// dp
    public static final int DEFAULT_TAG_TEXT_SIZE = 11;// dp
    public static final int DEFAULT_TAG_TEXT_COLOR = 0xffffffff;
    public static final int DEFAULT_TAG_PADDING = 6;
    private final TextPaint mTextPaint;
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor, android.R.attr.drawablePadding};
    private int mGravity;
    private ColorStateList mTextColor;
    private int mTextColorNormal;
    private int mTextColorSelected;
    private float mTextColorOffset = 1;
    private float mTextSize;
    private float mItemWidth;
    private int mDrawableHeight;
    private int mDrawablePadding;
    private int mTextHeight;
    private int mDesc;
    private float mTopOffset = 0;
    private float mTagTextSize;
    private int mTagTextColor;
    private int mTagTextHeight;
    private int mTagDesc;
    private int mTagPadding;
    private Drawable mTagBackground;
    private final Rect mRefreshRect = new Rect();
    private GradientTabAdapter mAdapter;
    private int mCurrentPager = 0;
    private int mNextPager = 0;

    public GradientTabStrip(Context context) {
        this(context, null);
    }

    public GradientTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("ResourceType")
    public GradientTabStrip(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemClickable(true);
        final float density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);
        if (Build.VERSION.SDK_INT > 4) {
            mTextPaint.density = density;
        }
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int textSize = a.getDimensionPixelSize(0,
                (int) (DEFAULT_TEXT_SIZE * density));
        ColorStateList colors = a.getColorStateList(1);
        if (colors == null) {
            colors = ColorStateList.valueOf(a.getColor(1, Color.BLACK));
        }
        mDrawablePadding = a.getDimensionPixelSize(2, 0);
        a.recycle();
        setGravity(Gravity.CENTER);
        setTextSize(textSize);
        setTextColor(colors);
        setTagTextColor(DEFAULT_TAG_TEXT_COLOR);
        if (mTagTextSize == 0) {
            setTagTextSize(DEFAULT_TAG_TEXT_SIZE
                    * context.getResources().getDisplayMetrics().density);
        }
        if (mTagBackground == null) {
            setTagBackground(getDefaultTagBackground());
        }
        mTagPadding = (int) (DEFAULT_TAG_PADDING * density);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != View.MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Must measure with an exact width");
        }
        mItemWidth = (float) (widthSize - getPaddingLeft() - getPaddingRight())
                / (getItemCount() <= 0 ? 1 : getItemCount());
        mTextPaint.setTextSize(mTextSize);
        FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        mTextHeight = metrics.bottom - metrics.top;
        mDesc = mTextHeight + metrics.top;
        if (mAdapter != null) {
            mDrawableHeight = mAdapter.getSelectedDrawable(mCurrentPager,
                    getContext()).getIntrinsicHeight();
        } else {
            mDrawableHeight = -mDrawablePadding;
        }
        int height;
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
            setMeasuredDimension(widthSize, heightSize);
        } else {
            height = mDrawableHeight + mDrawablePadding + mTextHeight
                    + getPaddingTop() + getPaddingBottom();
            height = Math.max(height, getMinHeight());
            setMeasuredDimension(widthSize, height);
        }
        switch (mGravity) {
            default:
            case Gravity.CENTER:
                mTopOffset = (height - mDrawableHeight - mDrawablePadding
                        - mTextHeight - getPaddingTop() - getPaddingBottom()) * 0.5f;
                break;
            case Gravity.TOP:
                mTopOffset = 0;
                break;
            case Gravity.BOTTOM:
                mTopOffset = height - mDrawableHeight - mDrawablePadding
                        - mTextHeight - getPaddingTop() - getPaddingBottom();
                break;
        }
        mTextPaint.setTextSize(mTagTextSize);
        FontMetricsInt tagMetrics = mTextPaint.getFontMetricsInt();

        mTagTextHeight = tagMetrics.descent - tagMetrics.ascent;
        mTagDesc = (int) (mTagTextHeight - (-tagMetrics.ascent
                - tagMetrics.descent + (tagMetrics.bottom - tagMetrics.descent)
                * getResources().getDisplayMetrics().density));
        mTagTextHeight += mTagDesc;
    }

    private int getMinHeight() {
        int minHeight = 0;
        final Drawable bg = getBackground();
        if (bg != null) {
            minHeight = bg.getIntrinsicHeight();
        }
        return minHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), 0);
        for (int i = 0; i < getItemCount(); i++) {
            Drawable tag = getItemBackground(i);
            if (tag != null) {
                tag.setBounds(0, 0, (int) mItemWidth, getHeight());
                tag.draw(canvas);
            }
            canvas.translate(mItemWidth, 0);
        }
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        final float x = getPaddingLeft() + mItemWidth / 2;
        final float y = getPaddingTop() + mDrawableHeight + mDrawablePadding
                + mTopOffset;
        final int compoundTopOffset = -mDrawableHeight - mDrawablePadding;
        float alphaNormal;
        float alphaSelected;
        canvas.translate(x, y);
        int position = 0;
        for (int i = 0; i < getItemCount(); i++) {
            CharSequence charSequence = getItemText(i);
            String text = charSequence == null ? "" : charSequence.toString();
            mTextPaint.setColor(mTextColorNormal);
            mTextPaint.setTextSize(mTextSize);
            if (position == mNextPager) {
                mTextPaint.setColor(getColor(mTextColorNormal,
                        mTextColorSelected, mTextColorOffset));
            } else if (position == mCurrentPager) {
                mTextPaint.setColor(getColor(mTextColorNormal,
                        mTextColorSelected, 1 - mTextColorOffset));
            } else {
                mTextPaint.setColor(mTextColorNormal);
            }
            canvas.drawText(text, 0, mTextHeight - mDesc, mTextPaint);
            if (mAdapter != null) {
                final Drawable normalDrawable = mAdapter.getNormalDrawable(
                        position, getContext());
                final Drawable seletedDrawable = mAdapter.getSelectedDrawable(
                        position, getContext());
                if (position == mNextPager) {
                    alphaNormal = 1 - mTextColorOffset;
                    alphaSelected = mTextColorOffset;
                } else if (position == mCurrentPager) {
                    alphaNormal = mTextColorOffset;
                    alphaSelected = 1 - mTextColorOffset;
                } else {
                    alphaNormal = 1;
                    alphaSelected = 0;
                }
                normalDrawable.setAlpha((int) Math.ceil(0xFF * alphaNormal));
                seletedDrawable.setAlpha((int) Math.ceil(0xFF * alphaSelected));
                canvas.translate(normalDrawable.getIntrinsicWidth() * 0.5f, 0);
                normalDrawable
                        .setBounds(-normalDrawable.getIntrinsicWidth(),
                                compoundTopOffset, 0,
                                normalDrawable.getIntrinsicHeight()
                                        + compoundTopOffset);
                seletedDrawable
                        .setBounds(-normalDrawable.getIntrinsicWidth(),
                                compoundTopOffset, 0,
                                normalDrawable.getIntrinsicHeight()
                                        + compoundTopOffset);
                normalDrawable.draw(canvas);
                seletedDrawable.draw(canvas);
                if (mAdapter.isTagEnable(position)) {
                    mTextPaint.setColor(mTagTextColor);
                    mTextPaint.setTextSize(mTagTextSize);
                    int textWidth;
                    int textHeight = mTagTextHeight;
                    String tag = mAdapter.getTag(position) == null ? ""
                            : mAdapter.getTag(position);
                    if ("".equals(tag)) {
                        textHeight = 0;
                    }
                    textWidth = (int) (Math.ceil(mTextPaint.measureText(tag)));
                    int drawableWidth = mTagBackground == null ? 0
                            : mTagBackground.getMinimumWidth();
                    int drawableHeight = mTagBackground == null ? 0
                            : mTagBackground.getMinimumHeight();
                    drawableWidth = Math.max(textWidth, drawableWidth);
                    drawableHeight = Math.max(textHeight, drawableHeight);
                    drawableWidth = drawableWidth > drawableHeight ? drawableWidth + mTagPadding :
                            drawableHeight;
                    final float move = drawableWidth * 0.5f;
                    canvas.translate(-move, 0);
                    if (mTagBackground != null) {
                        mTagBackground.setBounds(0, compoundTopOffset,
                                drawableWidth, drawableHeight
                                        + compoundTopOffset);
                        mTagBackground.draw(canvas);
                    }
                    canvas.translate(move, 0);
                    canvas.drawText(tag, 0, mTagTextHeight - mTagDesc
                            + compoundTopOffset, mTextPaint);
                }
                canvas.translate(-normalDrawable.getIntrinsicWidth() * 0.5f, 0);
            }

            position++;
            canvas.translate(mItemWidth, 0);
        }
        canvas.restore();
    }

    /**
     * 颜色合成器
     *
     * @param normalColor   普通状态颜色
     * @param selectedColor 选中状态颜色
     * @param offset        偏移值
     * @return 合成色
     */
    private int getColor(int normalColor, int selectedColor, float offset) {
        int normalAlpha = Color.alpha(normalColor);
        int normalRed = Color.red(normalColor);
        int normalGreen = Color.green(normalColor);
        int normalBlue = Color.blue(normalColor);
        int selectedAlpha = Color.alpha(selectedColor);
        int selectedRed = Color.red(selectedColor);
        int selectedGreen = Color.green(selectedColor);
        int selectedBlue = Color.blue(selectedColor);
        int a = (int) Math.ceil((selectedAlpha - normalAlpha) * offset);
        int r = (int) Math.ceil((selectedRed - normalRed) * offset);
        int g = (int) Math.ceil((selectedGreen - normalGreen) * offset);
        int b = (int) Math.ceil((selectedBlue - normalBlue) * offset);
        return Color.argb(normalAlpha + a, normalRed + r, normalGreen + g,
                normalBlue + b);
    }

    @Override
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return motionX - mItemWidth * position;
    }

    @Override
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return motionY;
    }

    @Override
    protected void jumpTo(int current) {
        mRefreshRect.set((int) Math.floor(mItemWidth * mNextPager),
                getPaddingTop(), (int) Math.ceil(mItemWidth * (current + 1)),
                getHeight() - getPaddingBottom());
        mCurrentPager = -1;
        mNextPager = current;
        mTextColorOffset = 1;
        invalidate(mRefreshRect);
    }

    @Override
    protected void gotoLeft(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mTextColorOffset = 1 - offset;
        mRefreshRect.set((int) Math.floor(mItemWidth * mNextPager),
                getPaddingTop(),
                (int) Math.ceil(mItemWidth * (mCurrentPager + 1)), getHeight()
                        - getPaddingBottom());
        invalidate(mRefreshRect);
    }

    @Override
    protected void gotoRight(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mTextColorOffset = offset;
        mRefreshRect.set((int) Math.floor(mItemWidth * mCurrentPager),
                getPaddingTop(),
                (int) Math.ceil(mItemWidth * (mNextPager + 1)), getHeight()
                        - getPaddingBottom());
        invalidate(mRefreshRect);
    }

    @Override
    protected int pointToPosition(float x, float y) {
        int position = -1;
        for (int i = 0; i < getItemCount(); i++) {
            float l = getPaddingLeft() + mItemWidth * i;
            float r = l + mItemWidth;
            if (x >= l && x <= r) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 获取Adapter
     *
     * @return Adapter
     */
    @SuppressWarnings("unused")
    public final GradientTabAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public final void setAdapter(GradientTabAdapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 图像排版方式
     *
     * @return 排版方式
     */
    @SuppressWarnings("unused")
    public final int getGravity() {
        return mGravity;
    }

    /**
     * 设置图像排版方式
     *
     * @param gravity 图像排版方式
     */
    public final void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            invalidate();
        }
    }

    /**
     * 获取文字颜色
     *
     * @return 文字颜色
     */
    @SuppressWarnings("unused")
    public ColorStateList getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(ColorStateList color) {
        if (color != null && mTextColor != color) {
            mTextColor = color;
            mTextColorNormal = mTextColor.getDefaultColor();
            mTextColorSelected = mTextColor.getColorForState(
                    SELECTED_STATE_SET, mTextColorNormal);
            invalidate();
        }
    }

    /**
     * 获取文字大小
     *
     * @return 文字大小
     */
    @SuppressWarnings("unused")
    public final float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文字大小
     *
     * @param textSize 文字大小
     */
    public final void setTextSize(int textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取小标签文字颜色
     *
     * @return 小标签文字颜色
     */
    @SuppressWarnings("unused")
    public int getTagTextColor() {
        return mTagTextColor;
    }

    /**
     * 设置小标签文字颜色
     *
     * @param color 小标签文字颜色
     */
    public void setTagTextColor(int color) {
        if (mTagTextColor != color) {
            mTagTextColor = color;
            invalidate();
        }
    }

    /**
     * 获取小标签文字大小
     *
     * @return 小标签文字大小
     */
    @SuppressWarnings("unused")
    public float getTagTextSize() {
        return mTagTextSize;
    }

    /**
     * 设置小标签文字大小
     *
     * @param tagTextSize 小标签文字大小
     */
    public void setTagTextSize(float tagTextSize) {
        if (mTagTextSize != tagTextSize) {
            mTagTextSize = tagTextSize;
            invalidate();
        }
    }

    /**
     * 获取小标签背景
     *
     * @return 小标签背景
     */
    @SuppressWarnings("unused")
    public Drawable getTagBackground() {
        return mTagBackground;
    }

    /**
     * 设置小标签背景
     *
     * @param background 小标签背景
     */
    public void setTagBackground(Drawable background) {
        if (mTagBackground != background) {
            mTagBackground = background;
            invalidate();
        }
    }

    /**
     * 数据容器Adapter
     *
     * @author Alex
     */
    public interface GradientTabAdapter extends ItemTabAdapter{

        /**
         * 获取普通状态下的 Drawable
         *
         * @param position 位置
         * @param context  Context
         * @return 必须非空
         */
        Drawable getNormalDrawable(int position, Context context);

        /**
         * 获取选中状态下的 Drawable
         *
         * @param position 位置
         * @param context  Context
         * @return 必须非空
         */
        Drawable getSelectedDrawable(int position, Context context);
    }

    /**
     * 简单的数据容器Adapter
     *
     * @author Alex
     */
    @SuppressWarnings("unused")
    public static abstract class SimpleGradientTabAdapter implements GradientTabAdapter {

        /**
         * 获取普通状态下的 Drawable
         *
         * @param position 位置
         * @param context  Context
         * @return 必须非空
         */
        public abstract Drawable getNormalDrawable(int position, Context context);

        /**
         * 获取选中状态下的 Drawable
         *
         * @param position 位置
         * @param context  Context
         * @return 必须非空
         */
        public abstract Drawable getSelectedDrawable(int position, Context context);

        public boolean isTagEnable(int position) {
            return false;
        }

        public String getTag(int position) {
            return null;
        }
    }
}
