/*
 * Copyright (C) 2014 The Android Open Source Project
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

package am.widget.indicatortabstrip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.BaseTabStrip;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;


/**
 * 游标TabStrip，Item不建议超过5个
 *
 * @author Alex
 */
public class IndicatorTabStrip extends BaseTabStrip {

    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor, android.R.attr.divider};
    private static final int DEFAULT_TEXT_SIZE = 14;// 默认字体大小dp
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;// 默认字体颜色
    private static final int DEFAULT_ITEM_WIDTH = 64;// 最小子项宽度dp
    private static final int DEFAULT_ITEM_HEIGHT = 32;// 最小子项高度dp
    private static final int DEFAULT_TAG_TEXT_SIZE = 11;// 默认Tag字体大小dp
    private static final int DEFAULT_TAG_TEXT_COLOR = 0xffffffff;// 默认Tag文字颜色
    private static final int DEFAULT_TAG_MIN_SIZE = 15;// 默认Tag最小大小dp
    public static final int INDICATOR_WIDTH_MODE_SET = 0;// 按照设置宽度计算
    public static final int INDICATOR_WIDTH_MODE_TAB = 1;// 按照子项宽度计算
    public static final int INDICATOR_WIDTH_BY_DRAWABLE = -1;// 按照图片宽度计算
    public static final int INDICATOR_HEIGHT_BY_DRAWABLE = -1;// 按照图片高度计算
    public static final int TAG_MIN_SIZE_MODE_HAS_TEXT = 0;// 当图片最小宽高更小时，按图片计算
    public static final int TAG_MIN_SIZE_MODE_ALWAYS = 1;// 按照设置的最小宽高
    private final TextPaint mTextPaint;// 文字画笔
    private float mTextSize;// 文字大小
    private float mTextDesc;// 文字偏移
    private ColorStateList mTextColor;// 文字颜色
    private float mTextScale;// 选中文字缩放
    private ColorStateList mGradient;// 渐变子项背景
    private Drawable mDivider;// 底部Divider
    private Drawable mIndicator;// 游标
    private int mIndicatorWidthMode;// 游标宽度计算模式
    private int mIndicatorPadding;// 游标两端Padding（仅“按照子项宽度计算”模式下有效）
    private int mIndicatorWidth;// 游标高度
    private int mIndicatorHeight;// 游标高度
    private Drawable mInterval;// 子项间隔
    private int mMinItemWidth;// 最小子项宽度
    private int mMinItemHeight;// 最小子项高度
    private ItemTabAdapter mAdapter;// Tag
    private float mTagTextSize;// Tag文字大小
    private float mTagTextDesc;// Tag文字偏移
    private int mTagTextColor;// Tag文字颜色
    private Drawable mTagBackground;// Tag文字背景
    private int mTagMinSizeMode;// Tag文字大小模式
    private int mTagMinWidth;// Tag文字最小宽度
    private int mTagMinHeight;// Tag文字最小高度
    private TagLocation mTagLocation;// Tag布局
    private Rect mTextMeasureBounds = new Rect();// 文字测量
    private int mCurrentPager = 0;
    private int mNextPager = 0;
    private float mOffset = 1;

    public IndicatorTabStrip(Context context) {
        this(context, null);
    }

    public IndicatorTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorTabStrip(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemClickable(true);
        setClickSmoothScroll(true);
        final float density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);
        if (Build.VERSION.SDK_INT > 4) {
            updateTextPaintDensity();
        }
        mTagLocation = new TagLocation(TagLocation.LOCATION_EDGE);
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int n = a.getIndexCount();
        int textSize = (int) (DEFAULT_TEXT_SIZE * density);
        ColorStateList textColors = null;
        Drawable divider = null;
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
                    divider = a.getDrawable(attr);
                    break;
            }
        }
        a.recycle();
        float scale = 1;
        Drawable itemBackground = null;
        ColorStateList gradient = null;
        Drawable indicator = null;
        int indicatorWidthMode = INDICATOR_WIDTH_MODE_SET;
        int indicatorPadding = 0;
        int indicatorWidth = INDICATOR_WIDTH_BY_DRAWABLE;
        int indicatorHeight = INDICATOR_HEIGHT_BY_DRAWABLE;
        Drawable interval = null;
        int minItemWidth = (int) (DEFAULT_ITEM_WIDTH * density);
        int minItemHeight = (int) (DEFAULT_ITEM_HEIGHT * density);
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
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.IndicatorTabStrip);
        textSize = custom.getDimensionPixelSize(R.styleable.IndicatorTabStrip_ttsTextSize,
                textSize);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsTextColor))
            textColors = custom.getColorStateList(R.styleable.IndicatorTabStrip_ttsTextColor);
        scale = custom.getFloat(R.styleable.IndicatorTabStrip_ttsTextScale, scale);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsBackground))
            itemBackground = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsBackground);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsGradient))
            gradient = custom.getColorStateList(R.styleable.IndicatorTabStrip_ttsGradient);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsDivider))
            divider = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsDivider);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsIndicator))
            indicator = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsIndicator);
        indicatorWidthMode = custom.getInt(R.styleable.IndicatorTabStrip_ttsIndicatorWidthMode,
                indicatorWidthMode);
        indicatorPadding = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsIndicatorPadding, indicatorPadding);
        indicatorWidth = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsIndicatorWidth, indicatorWidth);
        indicatorHeight = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsIndicatorHeight, indicatorHeight);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsInterval))
            interval = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsInterval);
        minItemWidth = custom.getDimensionPixelOffset(R.styleable.IndicatorTabStrip_ttsMinItemWidth,
                minItemWidth);
        minItemHeight = custom.getDimensionPixelOffset(R.styleable.IndicatorTabStrip_ttsMinItemHeight,
                minItemHeight);
        tagTextSize = custom.getDimensionPixelSize(R.styleable.IndicatorTabStrip_ttsTagTextSize,
                tagTextSize);
        tagTextColor = custom.getColor(R.styleable.IndicatorTabStrip_ttsTagTextColor,
                tagTextColor);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsTagBackground))
            tagBackground = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsTagBackground);
        tagMinSizeMode = custom.getInt(R.styleable.IndicatorTabStrip_ttsTagMinSizeMode,
                tagMinSizeMode);
        tagMinWidth = custom.getDimensionPixelOffset(R.styleable.IndicatorTabStrip_ttsTagMinWidth,
                tagMinWidth);
        tagMinHeight = custom.getDimensionPixelOffset(R.styleable.IndicatorTabStrip_ttsTagMinHeight,
                tagMinHeight);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsTagPadding)) {
            final int padding = custom.getDimensionPixelOffset(
                    R.styleable.IndicatorTabStrip_ttsTagPadding, 0);
            tagPaddingLeft = padding;
            tagPaddingTop = padding;
            tagPaddingRight = padding;
            tagPaddingBottom = padding;
        }
        tagPaddingLeft = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagPaddingLeft, tagPaddingLeft);
        tagPaddingTop = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagPaddingTop, tagPaddingTop);
        tagPaddingRight = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagPaddingRight, tagPaddingRight);
        tagPaddingBottom = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagPaddingBottom, tagPaddingBottom);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsTagMargin)) {
            final int margin = custom.getDimensionPixelOffset(
                    R.styleable.IndicatorTabStrip_ttsTagMargin, 0);
            tagMarginLeft = margin;
            tagMarginTop = margin;
            tagMarginRight = margin;
            tagMarginBottom = margin;
        }
        tagMarginLeft = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagMarginLeft, tagMarginLeft);
        tagMarginTop = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagMarginTop, tagMarginTop);
        tagMarginRight = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagMarginRight, tagMarginRight);
        tagMarginBottom = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_ttsTagMarginBottom, tagMarginBottom);
        custom.recycle();
        setTextSize(textSize);
        if (textColors != null) {
            setTextColor(textColors);
        } else {
            setTextColor(DEFAULT_TEXT_COLOR);
        }
        setTextScale(scale);
        setItemBackground(itemBackground);
        setGradient(gradient);
        setDivider(divider);
        setIndicator(indicator);
        setIndicatorWidthMode(indicatorWidthMode);
        setIndicatorPadding(indicatorPadding);
        setIndicatorWidth(indicatorWidth);
        setIndicatorHeight(indicatorHeight);
        setInterval(interval);
        setMinItemWidth(minItemWidth);
        setMinItemHeight(minItemHeight);
        setTagTextSize(tagTextSize);
        setTagTextColor(tagTextColor);
        setTagBackground(tagBackground);
        setTagMinSizeMode(tagMinSizeMode);
        setTagMinWidth(tagMinWidth);
        setTagMinHeight(tagMinHeight);
        setTagPadding(tagPaddingLeft, tagPaddingTop, tagPaddingRight, tagPaddingBottom);
        setTagMargin(tagMarginLeft, tagMarginTop, tagMarginRight, tagMarginBottom);
    }

    @TargetApi(5)
    private void updateTextPaintDensity() {
        mTextPaint.density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        final int textHeight = metrics.bottom - metrics.top;
        mTextDesc = metrics.bottom;
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int textWidth = 0;
            for (int i = 0; i < getItemCount(); i++) {
                if (getItemText(i) != null) {
                    String text = getItemText(i).toString();
                    mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
                    textWidth = Math.max(textWidth, mTextMeasureBounds.width());
                }
            }
            final int maxTextWidth = mTextScale > 1 ?
                    (int) (Math.ceil((float) textWidth * mTextScale) + 1) : textWidth;
            final int itemBackgroundWith = getMinItemBackgroundWidth();
            final int itemWidth = Math.max(maxTextWidth,
                    Math.max(mMinItemWidth, itemBackgroundWith));
            final int intervalWidth = getIntervalWidth();
            final int totalWidth = itemWidth * getItemCount() +
                    intervalWidth * (getItemCount() - 1) +
                    ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this);
            final int dividerWidth = mDivider == null ? 0 : mDivider.getIntrinsicWidth() +
                    ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this);
            width = Math.max(Math.max(totalWidth, dividerWidth), getMinWidth());
            if (widthMode == MeasureSpec.AT_MOST)
                width = Math.min(width, widthSize);
        }
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            final int maxTextHeight = mTextScale > 1 ?
                    (int) (Math.ceil((float) textHeight * mTextScale) + 1) : textHeight;
            final int itemBackgroundHeight = getMinItemBackgroundHeight();
            final int intervalHeight = mInterval == null ? 0 : mInterval.getIntrinsicHeight();
            final int dividerHeight = getDividerHeight();
            final int itemHeight = Math.max(Math.max(maxTextHeight, intervalHeight),
                    Math.max(mMinItemHeight, itemBackgroundHeight));
            height = Math.max(dividerHeight + itemHeight + getPaddingTop() + getPaddingBottom(),
                    getMinHeight());
            if (heightMode == MeasureSpec.AT_MOST)
                height = Math.min(height, heightSize);
        }
        setMeasuredDimension(width, height);
        mTextPaint.setTextSize(mTagTextSize);
        metrics = mTextPaint.getFontMetricsInt();
        mTagTextDesc = metrics.bottom;
    }

    /**
     * 获取间隔宽度
     *
     * @return 间隔宽度
     */
    protected int getIntervalWidth() {
        return mInterval == null ? 0 : mInterval.getIntrinsicWidth();
    }

    /**
     * 获取子项宽度
     *
     * @return 子项宽度
     */
    protected float getItemWidth() {
        float width = getWidth() - ViewCompat.getPaddingStart(this) - ViewCompat.getPaddingEnd(this)
                - getIntervalWidth() * (getItemCount() - 1);
        return width / getItemCount();
    }

    /**
     * 获取子项宽度（非精确）
     *
     * @return 子项宽度（非精确）
     */
    protected int getDrawItemWidth() {
        return Math.round(getItemWidth());
    }

    /**
     * 获取最后一个子项宽度（非精确）
     *
     * @return 最后一个子项宽度（非精确）
     */
    protected int getLastItemWidth() {
        return getWidth() - ViewCompat.getPaddingStart(this) - ViewCompat.getPaddingEnd(this)
                - getIntervalWidth() * (getItemCount() - 1)
                - getDrawItemWidth() * (getItemCount() - 1);
    }

    /**
     * 获取Divider高度
     *
     * @return Divider高度
     */
    protected int getDividerHeight() {
        return mDivider == null ? 0 : mDivider.getIntrinsicHeight();
    }

    /**
     * 获取子项高度
     *
     * @return 子项高度
     */
    protected int getItemHeight() {
        return getHeight() - getDividerHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void jumpTo(int current) {
        mCurrentPager = current - 1;
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
        final float itemWidth = getItemWidth();
        final int intervalWidth = getIntervalWidth();
        for (int i = 0; i < getItemCount(); i++) {
            float l = ViewCompat.getPaddingStart(this) + intervalWidth * i + itemWidth * i;
            float r = l + itemWidth;
            if (x >= l && x <= r) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return motionX - getPaddingLeft() - getIntervalWidth() * position
                - getItemWidth() * position;
    }

    @Override
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return motionY;
    }

    @Override
    protected int getItemCount() {
        if (isInEditMode()) {
            return 3;
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
        drawDivider(canvas);
        drawItem(canvas);
        drawIndicator(canvas);
    }

    /**
     * 绘制Divider
     *
     * @param canvas 画布
     */
    protected void drawDivider(Canvas canvas) {
        if (mDivider == null || mDivider.getIntrinsicHeight() <= 0)
            return;
        final int dividerWidth = mDivider.getIntrinsicWidth() > 0 ? mDivider.getIntrinsicWidth() :
                getWidth() - ViewCompat.getPaddingStart(this) - ViewCompat.getPaddingEnd(this);
        final int dividerHeight = mDivider.getIntrinsicHeight();
        mDivider.setBounds(0, 0, dividerWidth, dividerHeight);
        final float moveX = ViewCompat.getPaddingStart(this);
        final float moveY = getItemHeight() + getPaddingTop();
        canvas.save();
        canvas.translate(moveX, moveY);
        mDivider.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制子项
     *
     * @param canvas 画布
     */
    protected void drawItem(Canvas canvas) {
        final int itemWidth = getDrawItemWidth();
        final int itemHeight = getItemHeight();
        for (int position = 0; position < getItemCount(); position++) {
            drawItemBackground(canvas, position, itemWidth, itemHeight);
            drawItemGradient(canvas, position, itemWidth, itemHeight);
            drawInterval(canvas, position, itemWidth, itemHeight);
            drawText(canvas, position, itemWidth, itemHeight);
            drawTag(canvas, position, itemWidth, itemHeight);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas     画布
     * @param position   子项坐标
     * @param itemWidth  子项宽
     * @param itemHeight 子项高
     */
    protected void drawItemBackground(Canvas canvas, int position, int itemWidth, int itemHeight) {
        if (!hasItemBackgrounds())
            return;
        Drawable tag = getItemBackground(position);
        if (position == getItemCount() - 1) {
            int restWidth = getLastItemWidth();
            tag.setBounds(0, 0, restWidth, itemHeight);
        } else {
            tag.setBounds(0, 0, itemWidth, itemHeight);
        }
        final float moveX = ViewCompat.getPaddingStart(this) +
                (itemWidth + getIntervalWidth()) * position;
        final float moveY = getPaddingTop();
        canvas.save();
        canvas.translate(moveX, moveY);
        tag.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制渐变
     *
     * @param canvas     画布
     * @param position   子项坐标
     * @param itemWidth  子项宽
     * @param itemHeight 子项高
     */
    protected void drawItemGradient(Canvas canvas, int position, int itemWidth, int itemHeight) {
        if (mGradient == null || !mGradient.isStateful())
            return;
        final int normalColor = mGradient.getDefaultColor();
        final int selectedColor = mGradient.getColorForState(SELECTED_STATE_SET, normalColor);
        if (position == mNextPager) {
            mTextPaint.setColor(getColor(normalColor, selectedColor, mOffset));
        } else if (position == mCurrentPager) {
            mTextPaint.setColor(getColor(normalColor, selectedColor, 1 - mOffset));
        } else {
            mTextPaint.setColor(normalColor);
        }
        final float moveX = ViewCompat.getPaddingStart(this) +
                (itemWidth + getIntervalWidth()) * position;
        final float moveY = getPaddingTop();
        final int restWidth = position == getItemCount() - 1 ? getLastItemWidth() : itemWidth;
        canvas.save();
        canvas.translate(moveX, moveY);
        canvas.drawRect(0, 0, restWidth, itemHeight, mTextPaint);
        canvas.restore();
    }

    /**
     * 绘制间隔
     *
     * @param canvas     画布
     * @param position   子项坐标
     * @param itemWidth  子项宽
     * @param itemHeight 子项高
     */
    protected void drawInterval(Canvas canvas, int position, int itemWidth, int itemHeight) {
        if (mInterval == null || mInterval.getIntrinsicWidth() <= 0
                || position == getItemCount() - 1)
            return;
        final int intervalHeight = mInterval.getIntrinsicHeight() <= 0 ? itemHeight :
                mInterval.getIntrinsicHeight();
        mInterval.setBounds(0, 0, getIntervalWidth(), intervalHeight);
        final int moveX = ViewCompat.getPaddingStart(this) + itemWidth * position;
        final float moveY = getPaddingTop() + (itemHeight - intervalHeight) * 0.5f;
        canvas.save();
        canvas.translate(moveX, moveY);
        mInterval.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制文字
     *
     * @param canvas     画布
     * @param position   子项坐标
     * @param itemWidth  子项宽
     * @param itemHeight 子项高
     */
    protected void drawText(Canvas canvas, int position, int itemWidth, int itemHeight) {
        if (getItemText(position) == null)
            return;
        String text = getItemText(position).toString();
        if (text.length() <= 0)
            return;
        mTextPaint.setTextSize(mTextSize);
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
        final float centerX = ViewCompat.getPaddingStart(this) +
                (itemWidth + getIntervalWidth()) * ((float) position + 0.5f);
        final float centerY = getPaddingTop() + itemHeight * 0.5f;
        float scale;
        if (position == mNextPager) {
            scale = 1 + (mTextScale - 1) * mOffset;
        } else if (position == mCurrentPager) {
            scale = 1 + (mTextScale - 1) * (1 - mOffset);
        } else {
            scale = 1;
        }
        canvas.save();
        canvas.translate(centerX, centerY + mTextDesc);
        if (scale != 1) {
            canvas.scale(scale, scale, 0, -mTextDesc);
        }
        canvas.drawText(text, 0, 0, mTextPaint);
        canvas.restore();
    }

    /**
     * 绘制Tag
     *
     * @param canvas     画布
     * @param position   子项坐标
     * @param itemWidth  子项宽
     * @param itemHeight 子项高
     */
    @SuppressWarnings("unused")
    protected void drawTag(Canvas canvas, int position, int itemWidth, int itemHeight) {
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
                if ("".equals(text)) {
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
        final int rightTabX = position == getItemCount() - 1 ?
                getWidth() - ViewCompat.getPaddingEnd(this)
                : ViewCompat.getPaddingStart(this) +
                (itemWidth + getIntervalWidth()) * position + itemWidth;
        final int rightTagX = rightTabX - mTagLocation.getMarginRight();
        final int tagY = getPaddingTop() + mTagLocation.getMarginTop();
        final int leftTagX = rightTagX - tagWidth;
        final float tagCenterX = leftTagX + tagWidth * 0.5f;
        final float tagCenterY = tagY + tagWidth * 0.5f;
        canvas.save();
        if (mTagBackground != null) {
            mTagBackground.setBounds(0, 0, tagWidth, tagHeight);
            canvas.translate(leftTagX, tagY);
            mTagBackground.draw(canvas);
            if (!"".equals(text)) {
                canvas.translate(tagWidth * 0.5f, tagHeight * 0.5f + mTagTextDesc);
                canvas.drawText(text, 0, 0, mTextPaint);
            }
        } else {
            canvas.translate(tagCenterX, tagCenterY + mTagTextDesc);
            canvas.drawText(text, 0, 0, mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 绘制游标
     *
     * @param canvas 画布
     */
    protected void drawIndicator(Canvas canvas) {
        if (mIndicator == null)
            return;
        final int indicatorWidth = getIndicatorWidth();
        final int indicatorHeight = getIndicatorHeight();
        if (indicatorWidth <= 0 || indicatorHeight <= 0)
            return;
        mIndicator.setBounds(0, 0, indicatorWidth, indicatorHeight);
        final float widthWithInterval = getItemWidth() + getIntervalWidth();
        final float currentCenter = ViewCompat.getPaddingStart(this) +
                mCurrentPager * widthWithInterval + widthWithInterval * 0.5f;
        final float nextCenter = ViewCompat.getPaddingStart(this) +
                mNextPager * widthWithInterval + widthWithInterval * 0.5f;
        final float moveCenter = currentCenter + (nextCenter - currentCenter) * mOffset;
        final float moveX = moveCenter - indicatorWidth * 0.5f;
        final float moveY = getHeight() - getPaddingBottom() - getDividerHeight() - indicatorHeight;
        canvas.save();
        canvas.translate(moveX, moveY);
        mIndicator.draw(canvas);
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
     * @param size 文字大小
     */
    public void setTextSize(float size) {
        if (mTextSize != size) {
            mTextSize = size;
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
     * 获取文字缩放比
     *
     * @return 文字缩放比
     */
    @SuppressWarnings("unused")
    public float getTextScale() {
        return mTextScale;
    }

    /**
     * 设置文字缩放比
     *
     * @param scale 文字缩放比
     */
    public void setTextScale(float scale) {
        if (scale > 0 && mTextScale != scale) {
            mTextScale = scale;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取渐变
     *
     * @return 渐变
     */
    @SuppressWarnings("unused")
    public ColorStateList getGradient() {
        return mGradient;
    }

    /**
     * 设置渐变
     *
     * @param gradient 渐变
     */
    public void setGradient(ColorStateList gradient) {
        if (gradient == null) {
            mGradient = null;
            invalidate();
        } else if (mGradient != gradient && gradient.isStateful()) {
            mGradient = gradient;
            invalidate();
        } else {
            setItemBackground(new ColorDrawable(gradient.getDefaultColor()));
        }
    }

    /**
     * 设置渐变
     *
     * @param gradient 渐变
     */
    @SuppressWarnings("unused")
    public void setGradient(@ColorRes int gradient) {
        setGradient(ContextCompat.getColorStateList(getContext(), gradient));
    }

    /**
     * 获取Divider
     *
     * @return Divider
     */
    @SuppressWarnings("unused")
    public Drawable getDivider() {
        return mDivider;
    }

    /**
     * 设置Divider
     *
     * @param divider Divider
     */
    public void setDivider(Drawable divider) {
        if (mDivider != divider) {
            mDivider = divider;
            invalidate();
        }
    }

    /**
     * 设置Divider
     *
     * @param divider Divider
     */
    @SuppressWarnings("unused")
    public void setDivider(@DrawableRes int divider) {
        setDivider(ContextCompat.getDrawable(getContext(), divider));
    }

    /**
     * 获取游标
     *
     * @return 游标
     */
    @SuppressWarnings("unused")
    public Drawable getIndicator() {
        return mIndicator;
    }

    /**
     * 设置游标
     *
     * @param indicator 游标
     */
    public void setIndicator(Drawable indicator) {
        if (mIndicator != indicator) {
            mIndicator = indicator;
            invalidate();
        }
    }

    /**
     * 设置游标
     *
     * @param indicator 游标
     */
    @SuppressWarnings("unused")
    public void setIndicator(@DrawableRes int indicator) {
        setIndicator(ContextCompat.getDrawable(getContext(), indicator));
    }

    /**
     * 获取游标宽度计算模式
     *
     * @return 游标宽度计算模式
     */
    @SuppressWarnings("unused")
    public int getIndicatorWidthMode() {
        return mIndicatorWidthMode;
    }

    /**
     * 设置获取游标宽度计算模式
     *
     * @param mode 获取游标宽度计算模式
     */
    public void setIndicatorWidthMode(int mode) {
        if (mode != INDICATOR_WIDTH_MODE_SET && mode != INDICATOR_WIDTH_MODE_TAB)
            return;
        if (mIndicatorWidthMode != mode) {
            mIndicatorWidthMode = mode;
            invalidate();
        }
    }

    /**
     * 获取游标两端Padding
     *
     * @return 游标两端Padding
     */
    @SuppressWarnings("unused")
    public int getIndicatorPadding() {
        return mIndicatorPadding;
    }

    /**
     * 设置游标两端Padding
     *
     * @param padding 游标两端Padding
     */
    public void setIndicatorPadding(int padding) {
        if (mIndicatorPadding != padding) {
            mIndicatorPadding = padding;
            invalidate();
        }
    }

    /**
     * 获取游标宽度
     *
     * @return 游标宽度
     */
    public int getIndicatorWidth() {
        if (mIndicator == null)
            return 0;
        switch (mIndicatorWidthMode) {
            default:
            case INDICATOR_WIDTH_MODE_SET:
                return mIndicatorWidth == INDICATOR_WIDTH_BY_DRAWABLE ?
                        mIndicator.getIntrinsicWidth() : mIndicatorWidth;
            case INDICATOR_WIDTH_MODE_TAB:
                return getDrawItemWidth() - mIndicatorPadding * 2;
        }
    }

    /**
     * 设置游标宽度
     *
     * @param width 游标宽度
     */
    public void setIndicatorWidth(int width) {
        if (width < INDICATOR_WIDTH_BY_DRAWABLE)
            return;
        if (mIndicatorWidth != width) {
            mIndicatorWidth = width;
            invalidate();
        }
    }

    /**
     * 获取游标高度
     *
     * @return 游标高度
     */
    public int getIndicatorHeight() {
        if (mIndicator == null)
            return 0;
        return mIndicatorHeight == INDICATOR_HEIGHT_BY_DRAWABLE ?
                mIndicator.getIntrinsicHeight() : mIndicatorHeight;
    }

    /**
     * 设置游标高度
     *
     * @param height 游标高度
     */
    public void setIndicatorHeight(int height) {
        if (height < INDICATOR_HEIGHT_BY_DRAWABLE)
            return;
        if (mIndicatorHeight != height) {
            mIndicatorHeight = height;
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
     * 获取子项最小高度
     *
     * @return 子项最小高度
     */
    @SuppressWarnings("unused")
    public int getMinItemHeight() {
        return mMinItemHeight;
    }

    /**
     * 设置子项最小高度
     *
     * @param height 子项最小高度
     */
    public void setMinItemHeight(int height) {
        if (height >= 0 && height != mMinItemHeight) {
            mMinItemHeight = height;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取Adapter
     *
     * @return Adapter
     */
    @SuppressWarnings("unused")
    public ItemTabAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(ItemTabAdapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
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
}