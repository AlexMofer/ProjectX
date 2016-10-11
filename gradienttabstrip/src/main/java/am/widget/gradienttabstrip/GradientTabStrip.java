package am.widget.gradienttabstrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import am.widget.basetabstrip.BaseTabStrip;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * 滑动渐变TabStrip，子项建议不超过5个
 *
 * @author Alex
 */
@ViewPager.DecorView
public class GradientTabStrip extends BaseTabStrip {

    private static final int DEFAULT_TEXT_SIZE = 14;// 默认字体大小dp
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;// 默认字体颜色
    private static final int DEFAULT_TAG_TEXT_SIZE = 11;// 默认Tag字体大小sp
    private static final int DEFAULT_TAG_TEXT_COLOR = 0xffffffff;// 默认Tag文字颜色
    private static final int DEFAULT_TAG_MIN_SIZE = 15;// 默认Tag最小大小dp
    public static final int TAG_MIN_SIZE_MODE_HAS_TEXT = 0;// 当图片最小宽高更小时，按图片计算
    public static final int TAG_MIN_SIZE_MODE_ALWAYS = 1;// 按照设置的最小宽高
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor, android.R.attr.drawablePadding};
    private final TextPaint mTextPaint;// 文字画笔
    private float mTextSize;// 文字大小
    private ColorStateList mTextColor;// 文字颜色
    private int mDrawablePadding;// 图文间距
    private Drawable mInterval;// 子项间隔
    private int mMinItemWidth;// 最小子项宽度
    private GradientTabAdapter mAdapter;// Tag
    private float mTagTextSize;// Tag文字大小
    private int mTagTextColor;// Tag文字颜色
    private Drawable mTagBackground;// Tag文字背景
    private int mTagMinSizeMode;// Tag文字大小模式
    private int mTagMinWidth;// Tag文字最小宽度
    private int mTagMinHeight;// Tag文字最小高度
    private TagLocation mTagLocation;// Tag布局
    private Rect mTextMeasureBounds = new Rect();// 文字测量
    private int mItemHeight;// 高度
    private float mItemWidth;// 宽度
    private int mMaxDrawableWidth;// 最大图片宽
    private int mMaxDrawableHeight;// 最大图片高
    private int mCenterGap;// 中间间隔
    private int mCurrentPager = 0;
    private int mNextPager = 0;
    private float mOffset = 1;

    public GradientTabStrip(Context context) {
        this(context, null);
    }

    public GradientTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTabStrip(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemClickable(true);
        final float density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);
        if (Build.VERSION.SDK_INT > 4) {
            updateTextPaintDensity();
        }
        mTagLocation = new TagLocation(TagLocation.LOCATION_CONTENT);
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyleAttr, 0);
        int n = a.getIndexCount();
        int textSize = (int) (DEFAULT_TEXT_SIZE * density);
        ColorStateList textColors = null;
        int drawablePadding = 0;
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    textSize = a.getDimensionPixelSize(attr, textSize);
                    break;
                case 1:
                    textColors = a.getColorStateList(attr);
                    break;
                case 2:
                    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
                    break;
            }
        }
        a.recycle();
        Drawable itemBackground = null;
        Drawable interval = null;
        int minItemWidth = 0;
        int tagTextSize = (int) (DEFAULT_TAG_TEXT_SIZE * density);
        int tagTextColor = DEFAULT_TAG_TEXT_COLOR;
        Drawable tagBackground = getDefaultTagBackground();
        int tagMinSizeMode = TAG_MIN_SIZE_MODE_HAS_TEXT;
        int tagMinWidth = (int) (DEFAULT_TAG_MIN_SIZE * density);
        int tagMinHeight = (int) (DEFAULT_TAG_MIN_SIZE * density);
        int tagPaddingLeft = 0;
        int tagPaddingTop = 0;
        int tagPaddingRight = 0;
        int tagPaddingBottom = 0;
        int tagMarginLeft = 0;
        int tagMarginTop = 0;
        int tagMarginRight = 0;
        int tagMarginBottom = 0;
        int centerGap = 0;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.GradientTabStrip);
        textSize = custom.getDimensionPixelSize(R.styleable.GradientTabStrip_gtsTextSize, textSize);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTextColor))
            textColors = custom.getColorStateList(R.styleable.GradientTabStrip_gtsTextColor);
        drawablePadding = custom.getDimensionPixelSize(
                R.styleable.GradientTabStrip_gtsDrawablePadding, drawablePadding);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsBackground))
            itemBackground = custom.getDrawable(R.styleable.GradientTabStrip_gtsBackground);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsInterval))
            interval = custom.getDrawable(R.styleable.GradientTabStrip_gtsInterval);
        minItemWidth = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsMinItemWidth,
                minItemWidth);
        tagTextSize = custom.getDimensionPixelSize(R.styleable.GradientTabStrip_gtsTagTextSize,
                tagTextSize);
        tagTextColor = custom.getColor(R.styleable.GradientTabStrip_gtsTagTextColor,
                tagTextColor);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagBackground))
            tagBackground = custom.getDrawable(R.styleable.GradientTabStrip_gtsTagBackground);
        tagMinSizeMode = custom.getInt(R.styleable.GradientTabStrip_gtsTagMinSizeMode,
                tagMinSizeMode);
        tagMinWidth = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsTagMinWidth,
                tagMinWidth);
        tagMinHeight = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsTagMinHeight,
                tagMinHeight);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagPadding)) {
            final int padding = custom.getDimensionPixelOffset(
                    R.styleable.GradientTabStrip_gtsTagPadding, 0);
            tagPaddingLeft = padding;
            tagPaddingTop = padding;
            tagPaddingRight = padding;
            tagPaddingBottom = padding;
        }
        tagPaddingLeft = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingLeft, tagPaddingLeft);
        tagPaddingTop = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingTop, tagPaddingTop);
        tagPaddingRight = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingRight, tagPaddingRight);
        tagPaddingBottom = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingBottom, tagPaddingBottom);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagMargin)) {
            final int margin = custom.getDimensionPixelOffset(
                    R.styleable.GradientTabStrip_gtsTagMargin, 0);
            tagMarginLeft = margin;
            tagMarginTop = margin;
            tagMarginRight = margin;
            tagMarginBottom = margin;
        }
        tagMarginLeft = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginLeft, tagMarginLeft);
        tagMarginTop = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginTop, tagMarginTop);
        tagMarginRight = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginRight, tagMarginRight);
        tagMarginBottom = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginBottom, tagMarginBottom);
        centerGap = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsCenterGap, centerGap);
        custom.recycle();
        setTextSize(textSize);
        if (textColors != null) {
            setTextColor(textColors);
        } else {
            setTextColor(DEFAULT_TEXT_COLOR);
        }
        setDrawablePadding(drawablePadding);
        setItemBackground(itemBackground);
        setInterval(interval);
        setMinItemWidth(minItemWidth);
        if (isInEditMode()) {
            setAdapter(new TestAdapter(context));
        }
        setTagTextSize(tagTextSize);
        setTagTextColor(tagTextColor);
        setTagBackground(tagBackground);
        setTagMinSizeMode(tagMinSizeMode);
        setTagMinWidth(tagMinWidth);
        setTagMinHeight(tagMinHeight);
        setTagPadding(tagPaddingLeft, tagPaddingTop, tagPaddingRight, tagPaddingBottom);
        setTagMargin(tagMarginLeft, tagMarginTop, tagMarginRight, tagMarginBottom);
        setCenterGap(centerGap);
    }

    @TargetApi(5)
    private void updateTextPaintDensity() {
        mTextPaint.density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.setTextSize(mTextSize);
        int maxTextWidth = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemText(i) != null) {
                String text = getItemText(i).toString();
                mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
                maxTextWidth = Math.max(maxTextWidth, mTextMeasureBounds.width());
            }
        }
        mMaxDrawableWidth = getMaxDrawableWidth();
        mMaxDrawableHeight = getMaxDrawableHeight();
        final int itemBackgroundWith = getMinItemBackgroundWidth();
        final int itemWidth = Math.max(Math.max(maxTextWidth, mMaxDrawableWidth),
                Math.max(mMinItemWidth, itemBackgroundWith));
        final int intervalWidth = getIntervalWidth();
        final int totalWidth = itemWidth * getItemCount() +
                intervalWidth * (getItemCount() - 1) +
                ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this) + mCenterGap;
        final int width = Math.max(totalWidth, getSuggestedMinimumWidth());

        mItemHeight = mMaxDrawableHeight + mDrawablePadding + mTextMeasureBounds.height();
        final int itemBackgroundHeight = getMinItemBackgroundHeight();
        final int intervalHeight = mInterval == null ? 0 : mInterval.getIntrinsicHeight();
        final int itemHeight = Math.max(Math.max(mItemHeight, intervalHeight),
                itemBackgroundHeight);
        final int height = Math.max(itemHeight + getPaddingTop() + getPaddingBottom(),
                getSuggestedMinimumHeight());
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
        mItemWidth = (float) (getMeasuredWidth() - ViewCompat.getPaddingStart(this)
                - ViewCompat.getPaddingEnd(this) - (getItemCount() % 2 == 0 ? mCenterGap : 0)
                - getIntervalWidth() * (getItemCount() - 1))
                / getItemCount();
    }

    /**
     * 获取最大图片宽度
     *
     * @return 最大图片宽度
     */
    protected int getMaxDrawableWidth() {
        if (mAdapter == null)
            return 0;
        int maxWidth = 0;
        for (int position = 0; position < getItemCount(); position++) {
            Drawable normal = mAdapter.getNormalDrawable(position, getContext());
            Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
            if (normal != null) {
                maxWidth = Math.max(maxWidth, normal.getIntrinsicWidth());
            }
            if (selected != null) {
                maxWidth = Math.max(maxWidth, selected.getIntrinsicWidth());
            }
        }
        return maxWidth;
    }

    /**
     * 获取最大图片高度
     *
     * @return 最大图片高度
     */
    protected int getMaxDrawableHeight() {
        if (mAdapter == null)
            return 0;
        int maxHeight = 0;
        for (int position = 0; position < getItemCount(); position++) {
            Drawable normal = mAdapter.getNormalDrawable(position, getContext());
            Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
            if (normal != null) {
                maxHeight = Math.max(maxHeight, normal.getIntrinsicHeight());
            }
            if (selected != null) {
                maxHeight = Math.max(maxHeight, selected.getIntrinsicHeight());
            }
        }
        return maxHeight;
    }

    /**
     * 获取间隔宽度
     *
     * @return 间隔宽度
     */
    protected int getIntervalWidth() {
        return mInterval == null ? 0 : mInterval.getIntrinsicWidth();
    }

    @Override
    protected void jumpTo(int current) {
        mCurrentPager = -1;
        mNextPager = current;
        mOffset = 1;
        invalidate();
    }

    @Override
    protected void gotoLeft(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = 1 - offset;
        invalidate();
    }

    @Override
    protected void gotoRight(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = offset;
        invalidate();
    }

    @Override
    protected int pointToPosition(float x, float y) {
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        if (y < paddingTop || y > getWidth() - paddingBottom) {
            return -1;
        }
        int position = -1;
        final int intervalWidth = getIntervalWidth();
        for (int i = 0; i < getItemCount(); i++) {
            float l = ViewCompat.getPaddingStart(this) + intervalWidth * i + mItemWidth * i +
                    ((i * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
            float r = l + mItemWidth;
            if (x >= l && x <= r) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return motionX - getPaddingLeft() - getIntervalWidth() * position - mItemWidth * position
                - ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
    }

    @Override
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return motionY;
    }

    @Override
    protected int getItemCount() {
        if (isInEditMode()) {
            return 4;
        }
        return super.getItemCount();
    }

    @Override
    protected CharSequence getItemText(int position) {
        if (isInEditMode()) {
            return "Tab" + position;
        }
        return super.getItemText(position);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int position = 0; position < getItemCount(); position++) {
            drawItem(canvas, position);
        }
    }

    /**
     * 绘制子项
     *
     * @param canvas 画布
     */
    protected void drawItem(Canvas canvas, int position) {
        drawItemBackground(canvas, position);
        drawInterval(canvas, position);
        drawDrawable(canvas, position);
        drawText(canvas, position);
        drawTag(canvas, position);
    }

    /**
     * 绘制背景
     *
     * @param canvas   画布
     * @param position 子项坐标
     */
    protected void drawItemBackground(Canvas canvas, int position) {
        if (!hasItemBackgrounds())
            return;
        Drawable tag = getItemBackground(position);
        if (position == getItemCount() - 1) {
            int restWidth = getWidth() - ViewCompat.getPaddingStart(this)
                    - ViewCompat.getPaddingEnd(this) - getIntervalWidth() * (getItemCount() - 1)
                    - (int) mItemWidth * (getItemCount() - 1);
            tag.setBounds(0, 0, restWidth, getHeight() - getPaddingTop() - getPaddingBottom());
        } else {
            tag.setBounds(0, 0, (int) mItemWidth,
                    getHeight() - getPaddingTop() - getPaddingBottom());
        }
        final float moveX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * position
                + ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float moveY = getPaddingTop();
        canvas.save();
        canvas.translate(moveX, moveY);
        tag.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制间隔
     *
     * @param canvas   画布
     * @param position 子项坐标
     */
    protected void drawInterval(Canvas canvas, int position) {
        if (mInterval == null || mInterval.getIntrinsicWidth() <= 0
                || position == getItemCount() - 1)
            return;
        if (getItemCount() % 2 == 0 && position + 1 == getItemCount() / 2)
            return;
        final int intervalHeight = mInterval.getIntrinsicHeight() <= 0 ? getHeight()
                - getPaddingTop() - getPaddingBottom() : mInterval.getIntrinsicHeight();
        mInterval.setBounds(0, 0, getIntervalWidth(), intervalHeight);
        final float moveX = ViewCompat.getPaddingStart(this) + mItemWidth * position
                + ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float moveY = getPaddingTop() +
                (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f - intervalHeight * 0.5f;
        canvas.save();
        canvas.translate(moveX, moveY);
        mInterval.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制图片
     *
     * @param canvas   画布
     * @param position 子项坐标
     */
    protected void drawDrawable(Canvas canvas, int position) {
        if (mAdapter == null)
            return;
        Drawable normal = mAdapter.getNormalDrawable(position, getContext());
        Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
        if (normal == null && selected == null)
            return;
        float alphaNormal;
        float alphaSelected;
        if (position == mNextPager) {
            alphaNormal = 1 - mOffset;
            alphaSelected = mOffset;
        } else if (position == mCurrentPager) {
            alphaNormal = mOffset;
            alphaSelected = 1 - mOffset;
        } else {
            alphaNormal = 1;
            alphaSelected = 0;
        }
        final float drawableCenterX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * position + mItemWidth * 0.5f +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float drawableCenterY = getPaddingTop() + (getHeight() - getPaddingTop()
                - getPaddingBottom()) * 0.5f - mItemHeight * 0.5f + mMaxDrawableHeight * 0.5f;
        canvas.save();
        canvas.translate(drawableCenterX, drawableCenterY);
        if (normal != null) {
            final int normalWidth = normal.getIntrinsicWidth();
            final int normalHeight = normal.getIntrinsicHeight();
            normal.setAlpha((int) Math.ceil(0xFF * alphaNormal));
            normal.setBounds(0, 0, normalWidth, normalHeight);
            canvas.save();
            canvas.translate(-normalWidth * 0.5f, -normalHeight * 0.5f);
            normal.draw(canvas);
            canvas.restore();
        }
        if (selected != null) {
            final int selectedWidth = selected.getIntrinsicWidth();
            final int selectedHeight = selected.getIntrinsicHeight();
            selected.setAlpha((int) Math.ceil(0xFF * alphaSelected));
            selected.setBounds(0, 0, selectedWidth, selectedHeight);
            canvas.save();
            canvas.translate(-selectedWidth * 0.5f, -selectedHeight * 0.5f);
            selected.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 绘制文字
     *
     * @param canvas   画布
     * @param position 子项坐标
     */
    protected void drawText(Canvas canvas, int position) {
        if (getItemText(position) == null)
            return;
        String text = getItemText(position).toString();
        if (text.length() <= 0)
            return;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
        final int textHeight = mTextMeasureBounds.height();
        if (mTextColor == null) {
            mTextPaint.setColor(DEFAULT_TEXT_COLOR);
        } else {
            final int normalColor = mTextColor.getDefaultColor();
            final int selectedColor = mTextColor.getColorForState(SELECTED_STATE_SET, normalColor);
            if (position == mNextPager) {
                mTextPaint.setColor(getColor(normalColor, selectedColor, mOffset));
            } else if (position == mCurrentPager) {
                mTextPaint.setColor(getColor(normalColor, selectedColor, 1 - mOffset));
            } else {
                mTextPaint.setColor(normalColor);
            }
        }
        final int contentHeight = mMaxDrawableHeight + mDrawablePadding + textHeight;
        final float centerX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * (position + 0.5f) +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float centerY = getPaddingTop()
                + (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        canvas.save();
        canvas.translate(centerX, centerY + contentHeight * 0.5f - textHeight);
        canvas.drawText(text, 0, -mTextMeasureBounds.top, mTextPaint);
        canvas.restore();
    }

    /**
     * 绘制Tag
     *
     * @param canvas   画布
     * @param position 子项坐标
     */
    protected void drawTag(Canvas canvas, int position) {
        if (mAdapter == null || !mAdapter.isTagEnable(position))
            return;
        String text = mAdapter.getTag(position) == null ? "" : mAdapter.getTag(position);
        mTextPaint.setTextSize(mTagTextSize);
        mTextPaint.setColor(mTagTextColor);
        mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
        final int textWidth = mTextMeasureBounds.width();
        final int textHeight = mTextMeasureBounds.height();
        final int tagBackgroundWidth = mTagBackground == null ?
                0 : mTagBackground.getIntrinsicWidth();
        final int tagBackgroundHeight = mTagBackground == null ?
                0 : mTagBackground.getIntrinsicHeight();
        int tagWidth;
        int tagHeight;
        switch (mTagMinSizeMode) {
            default:
            case TAG_MIN_SIZE_MODE_HAS_TEXT:
                if (text.length() == 0) {
                    tagWidth = Math.min(mTagMinWidth, tagBackgroundWidth);
                    tagHeight = Math.min(mTagMinHeight, tagBackgroundHeight);
                    break;
                }
            case TAG_MIN_SIZE_MODE_ALWAYS:
                tagWidth = Math.max(
                        textWidth + mTagLocation.getPaddingLeft() + mTagLocation.getPaddingRight(),
                        Math.max(mTagMinWidth, tagBackgroundWidth));
                tagHeight = Math.max(
                        textHeight + mTagLocation.getPaddingTop() + mTagLocation.getPaddingBottom(),
                        Math.max(mTagMinHeight, tagBackgroundHeight));
                break;
        }
        final float centerX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * ((float) position + 0.5f) +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float centerY = getPaddingTop()
                + (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        final float tagCenterX = centerX + mMaxDrawableWidth * 0.5f;
        final float tagCenterY = centerY - mItemHeight * 0.5f + tagHeight * 0.5f;
        final float tagLeft = tagCenterX - tagWidth * 0.5f + mTagLocation.getMarginLeft();
        final float tagTop = tagCenterY - tagHeight * 0.5f - mTagLocation.getMarginBottom();
        canvas.save();
        canvas.translate(tagLeft, tagTop);
        if (mTagBackground != null) {
            mTagBackground.setBounds(0, 0, tagWidth, tagHeight);
            mTagBackground.draw(canvas);
        }
        if (text.length() > 0) {
            canvas.translate(mTagLocation.getPaddingLeft() +
                    (tagWidth - mTagLocation.getPaddingLeft() - mTagLocation.getPaddingRight())
                            * 0.5f, mTagLocation.getPaddingTop());
            if (tagHeight > textHeight) {
                canvas.translate(0, (tagHeight - textHeight) * 0.5f);
            }
            canvas.drawText(text, 0, -mTextMeasureBounds.top, mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 获取文字大小
     *
     * @return 文字大小
     */
    @SuppressWarnings("unused")
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文字大小
     *
     * @param textSize 文字大小
     */
    public void setTextSize(int textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            requestLayout();
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
        if (color != null && color != mTextColor) {
            mTextColor = color;
            invalidate();
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(@ColorInt int color) {
        setTextColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取图文间距
     *
     * @return 图文间距
     */
    @SuppressWarnings("unused")
    public int getDrawablePadding() {
        return mDrawablePadding;
    }

    /**
     * 设置图文间距
     *
     * @param padding 图文间距
     */
    public void setDrawablePadding(int padding) {
        if (padding >= 0 && padding != mDrawablePadding) {
            mDrawablePadding = padding;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取子项间隔
     *
     * @return 子项间隔
     */
    @SuppressWarnings("unused")
    public Drawable getInterval() {
        return mInterval;
    }

    /**
     * 设置子项间隔
     *
     * @param interval 子项间隔
     */
    public void setInterval(Drawable interval) {
        if (mInterval != interval) {
            mInterval = interval;
            invalidate();
        }
    }

    /**
     * 设置子项间隔
     *
     * @param interval 子项间隔
     */
    @SuppressWarnings("unused")
    public void setInterval(@DrawableRes int interval) {
        setInterval(ContextCompat.getDrawable(getContext(), interval));
    }

    /**
     * 获取子项最小宽度
     *
     * @return 子项最小宽度
     */
    @SuppressWarnings("unused")
    public int getMinItemWidth() {
        return mMinItemWidth;
    }

    /**
     * 设置子项最小宽度
     *
     * @param width 子项最小宽度
     */
    public void setMinItemWidth(int width) {
        if (width >= 0 && width != mMinItemWidth) {
            mMinItemWidth = width;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取Adapter
     *
     * @return Adapter
     */
    public GradientTabAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(GradientTabAdapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取Tag文字大小
     *
     * @return Tag文字大小
     */
    @SuppressWarnings("unused")
    public float getTagTextSize() {
        return mTagTextSize;
    }

    /**
     * 设置Tag文字大小
     *
     * @param size Tag文字大小
     */
    public void setTagTextSize(float size) {
        if (mTagTextSize != size) {
            mTagTextSize = size;
            invalidate();
        }
    }

    /**
     * 获取Tag文字颜色
     *
     * @return Tag文字颜色
     */
    @SuppressWarnings("unused")
    public int getTagTextColor() {
        return mTagTextColor;
    }

    /**
     * 设置Tag文字颜色
     *
     * @param color Tag文字颜色
     */
    public void setTagTextColor(@ColorInt int color) {
        if (mTagTextColor != color) {
            mTagTextColor = color;
            invalidate();
        }
    }

    /**
     * 获取Tag背景
     *
     * @return Tag背景
     */
    @SuppressWarnings("unused")
    public Drawable getTagBackground() {
        return mTagBackground;
    }

    /**
     * 设置Tag背景
     *
     * @param background Tag背景
     */
    public void setTagBackground(Drawable background) {
        if (mTagBackground != background) {
            mTagBackground = background;
            invalidate();
        }
    }

    /**
     * 设置Tag背景
     *
     * @param background Tag背景
     */
    @SuppressWarnings("unused")
    public void setTagBackground(@DrawableRes int background) {
        setTagBackground(ContextCompat.getDrawable(getContext(), background));
    }

    /**
     * 获取Tag最小高宽计算模式
     *
     * @return Tag最小高宽计算模式
     */
    @SuppressWarnings("unused")
    public int getTagMinSizeMode() {
        return mTagMinSizeMode;
    }

    /**
     * 设置Tag最小高宽计算模式
     *
     * @param mode Tag最小高宽计算模式
     */
    public void setTagMinSizeMode(int mode) {
        if (mode != TAG_MIN_SIZE_MODE_HAS_TEXT && mode != TAG_MIN_SIZE_MODE_ALWAYS)
            return;
        if (mTagMinSizeMode != mode) {
            mTagMinSizeMode = mode;
            invalidate();
        }
    }

    /**
     * 获取Tag最小宽度
     *
     * @return Tag最小宽度
     */
    @SuppressWarnings("unused")
    public int getTagMinWidth() {
        return mTagMinWidth;
    }

    /**
     * 设置Tag最小宽度
     *
     * @param width Tag最小宽度
     */
    public void setTagMinWidth(int width) {
        if (width >= 0 && mTagMinWidth != width) {
            mTagMinWidth = width;
            invalidate();
        }
    }

    /**
     * 获取Tag最小高度
     *
     * @return Tag最小高度
     */
    @SuppressWarnings("unused")
    public int getTagMinHeight() {
        return mTagMinHeight;
    }

    /**
     * 设置Tag最小高度
     *
     * @param height Tag最小高度
     */
    public void setTagMinHeight(int height) {
        if (height >= 0 && mTagMinHeight != height) {
            mTagMinHeight = height;
            invalidate();
        }
    }

    /**
     * 设置Tag Padding
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setTagPadding(int left, int top, int right, int bottom) {
        if (mTagLocation.setPadding(left, top, right, bottom)) {
            invalidate();
        }
    }

    /**
     * 设置Tag Margin
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setTagMargin(int left, int top, int right, int bottom) {
        if (mTagLocation.setMargin(left, top, right, bottom)) {
            invalidate();
        }
    }

    /**
     * 设置中间间隔，仅双数Item有效，单数无效
     *
     * @param gap 间隔
     */
    public void setCenterGap(int gap) {
        if (gap > 0 && gap != mCenterGap) {
            mCenterGap = gap;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 数据容器Adapter
     *
     * @author Alex
     */
    public interface GradientTabAdapter extends ItemTabAdapter {

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

    private class TestAdapter extends SimpleGradientTabAdapter {

        private GradientDrawable selected;
        private GradientDrawable normal;

        TestAdapter(Context context) {
            final int size = (int) (context.getResources().getDisplayMetrics().density * 10);
            selected = new GradientDrawable();
            selected.setShape(GradientDrawable.OVAL);
            selected.setColor(0xff808080);
            selected.setSize(size, size);
            normal = new GradientDrawable();
            normal.setShape(GradientDrawable.OVAL);
            normal.setColor(0x80808080);
            normal.setSize(size, size);
        }

        @Override
        public Drawable getNormalDrawable(int position, Context context) {
            return normal;
        }

        @Override
        public Drawable getSelectedDrawable(int position, Context context) {
            return selected;
        }
    }
}