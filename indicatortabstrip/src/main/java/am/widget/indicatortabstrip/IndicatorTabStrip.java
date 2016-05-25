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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
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
    public static final int DEFAULT_TEXT_COLOR = 0xff000000;// 默认字体颜色
    public static final int DEFAULT_TAG_TEXT_SIZE = 11;// 默认Tag字体大小dp
    public static final int DEFAULT_TAG_TEXT_COLOR = 0xffffffff;// 默认Tag文字颜色
    private final TextPaint mTextPaint;
    private float mTextSize;// 文字大小
    private ColorStateList mTextColor;// 文字颜色
    private float mTextScale;// 选中文字缩放
    private ColorStateList mGradient;// 渐变子项背景
    private Drawable mDivider;// 底部Divider
    private Drawable mIndicator;// 游标
    private Drawable mInterval;// 子项间隔
    private ItemTabAdapter mAdapter;// Tag
    private float mTagTextSize;// Tag文字大小
    private int mTagTextColor;// Tag文字颜色
    private Drawable mTagBackground;// Tag文字背景
    private TagLocation mTagLocation;// Tag布局
    private Rect mTextMeasureBounds = new Rect();// 文字测量


    private float itemWidth;

    private float textTop;// 文字最大高度
    private boolean showTextGradient;// 显示文字颜色渐变
    private int textColorNormal;// 文字普通颜色
    private int textColorSelected;// 文字选中时颜色
    private boolean showTextScale;// 显示文字缩放
    private float textSize;// 文字大小
    private float magnification = 0.2f;
    private float textSizeOffset = 1;
    private float heightOffset;// 高度偏移

    private int intervalWidth;

    private boolean showIndicator;
    private int indicatorWidth;
    private int indicatorHeight;
    private int indicatorColor = Color.BLACK;
    private float indicatorOffset = 0;

    private boolean showUnderLine;
    private int underLineHeight;
    private int underLineColor = Color.BLACK;

    private boolean showTabColor;
    private int tabColorSelected;
    private int tabColorNormal;

    private float textSizeTag;
    private Drawable backgroundTag;
    private int paddingStartTag;
    private int paddingEndTag;
    private int paddingTopTag;
    private int paddingBottomTag;
    private int marginStartTag;
    private int marginEndTag;
    private int marginTopTag;

    private int currentPager = 0;
    private int nextPager = 0;
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
        final float density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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
        Drawable interval = null;
        int tagTextSize = (int) (DEFAULT_TAG_TEXT_SIZE * density);
        int tagTextColor = DEFAULT_TAG_TEXT_COLOR;
        Drawable tagBackground = getDefaultTagBackground();
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
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsInterval))
            interval = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsInterval);
        tagTextSize = custom.getDimensionPixelSize(R.styleable.IndicatorTabStrip_ttsTagTextSize,
                tagTextSize);
        tagTextColor = custom.getColor(R.styleable.IndicatorTabStrip_ttsTagTextColor,
                tagTextColor);
        if (custom.hasValue(R.styleable.IndicatorTabStrip_ttsTagBackground))
            tagBackground = custom.getDrawable(R.styleable.IndicatorTabStrip_ttsTagBackground);
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
        setInterval(interval);
        setTagTextSize(tagTextSize);
        setTagTextColor(tagTextColor);
        setTagBackground(tagBackground);
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
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int textWidth = 0;
            for (int i = 0; i < getItemCount(); i++) {
                CharSequence charSequence = getItemText(i);
                String text = charSequence == null ? "" : charSequence.toString();
                mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
                textWidth = Math.max(textWidth, mTextMeasureBounds.width());
            }
            final int maxTextWidth = mTextScale > 1 ?
                    (int) (Math.ceil((float) textWidth * mTextScale) + 1) : textWidth;
            final int itemBackgroundWith = getMinItemBackgroundWidth();
            final int itemWidth = Math.max(maxTextWidth, itemBackgroundWith);
            final int intervalWidth = mInterval == null ? 0 : mInterval.getIntrinsicWidth();
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
            mTextPaint.getTextBounds(" ", 0, 1, mTextMeasureBounds);
            final int textHeight = mTextMeasureBounds.height();
            final int maxTextHeight = mTextScale > 1 ?
                    (int) (Math.ceil((float) textHeight * mTextScale) + 1) : textHeight;
            final int itemBackgroundHeight = getMinItemBackgroundHeight();
            final int intervalHeight = mInterval == null ? 0 : mInterval.getIntrinsicHeight();
            final int dividerHeight = mDivider == null ? 0 : mDivider.getIntrinsicHeight();
            final int itemHeight = Math.max(maxTextHeight,
                    Math.max(itemBackgroundHeight, intervalHeight));
            height = Math.max(dividerHeight + itemHeight + getPaddingTop() + getPaddingBottom(),
                    getMinHeight());
            if (heightMode == MeasureSpec.AT_MOST)
                height = Math.min(height, heightSize);
        }
        setMeasuredDimension(width, height);
//
//
//        if (heightMode == MeasureSpec.EXACTLY) {
//            heightOffset = (heightSize - (itemHeight + getPaddingTop() + getPaddingBottom()))
//                    * 0.5f;
//            setMeasuredDimension(widthSize, heightSize);
//        } else {
//            height
//        }
//
//
//        if (widthMode != MeasureSpec.EXACTLY) {
//            // TODO 使用不确定宽度处理
//            throw new IllegalStateException("Must measure with an exact width");
//        }
//        itemWidth = (float) (widthSize - getPaddingLeft() - getPaddingRight() - intervalWidth
//                * (getItemCount() <= 0 ? 1 : getItemCount() - 1))
//                / (getItemCount() <= 0 ? 1 : getItemCount());
//        mTextPaint.setTextSize(magnification <= 0 ? textSize : textSize
//                + textSize * magnification);
//        // TODO 当Tab文字长度超过itemWidth时需处理
//        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        float textHeight = fontMetrics.descent - fontMetrics.ascent;
//        textTop = textHeight - (-fontMetrics.ascent - fontMetrics.descent
//                + (fontMetrics.bottom - fontMetrics.descent) * getResources().getDisplayMetrics().density);
//        textHeight += textTop;
//        int itemHeight = (int) Math.ceil(textHeight + indicatorHeight
//                + underLineHeight);
//        int minHeight = getMinHeight();
//        if (minHeight > itemHeight + getPaddingTop() + getPaddingBottom()) {
//            itemHeight = minHeight - getPaddingTop() - getPaddingBottom();
//        }
//        if (heightMode == MeasureSpec.EXACTLY) {
//            heightOffset = (heightSize - (itemHeight + getPaddingTop() + getPaddingBottom()))
//                    * 0.5f;
//            setMeasuredDimension(widthSize, heightSize);
//        } else {
//            heightOffset = 0;
//            setMeasuredDimension(widthSize, itemHeight + getPaddingTop()
//                    + getPaddingBottom());
//        }
    }


    @Override
    protected void jumpTo(int current) {
        indicatorOffset = 1;
        currentPager = current - 1;
        nextPager = current;
        mOffset = 1;
        textSizeOffset = 1;
        invalidate();
    }

    @Override
    protected void gotoLeft(int current, int next, float offset) {
        indicatorOffset = offset - 1;
        currentPager = current;
        nextPager = next;
        mOffset = 1 - offset;
        textSizeOffset = 1 - offset;
        invalidate();
    }

    @Override
    protected void gotoRight(int current, int next, float offset) {
        indicatorOffset = offset;
        currentPager = current;
        nextPager = next;
        mOffset = offset;
        textSizeOffset = offset;
        invalidate();
    }

    @Override
    protected int pointToPosition(float x, float y) {
        int position = -1;
        for (int i = 0; i < getItemCount(); i++) {
            float l = getPaddingLeft() + intervalWidth * i + itemWidth * i;
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
        return motionX - getPaddingLeft() - intervalWidth * position - itemWidth * position;
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
        // TODO 绘制子项背景
        //drawItemBackground(canvas);
        //drawItemInterval(canvas);
        //drawUnderLine(canvas);
        //drawIndicator(canvas);
        //drawItemText(canvas);
        //drawItemTag(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    private void drawItemBackground(Canvas canvas) {
        if (showTabColor) {
            mTextPaint.setColor(tabColorNormal);
            canvas.save();
            canvas.translate(getPaddingLeft(), 0);
            if (getItemCount() > 0)
                for (int i = 0; i < getItemCount(); i++) {
                    if (i == nextPager) {
                        mTextPaint.setColor(getColor(tabColorNormal,
                                tabColorSelected, textSizeOffset));
                    } else if (i == currentPager) {
                        mTextPaint.setColor(getColor(tabColorNormal,
                                tabColorSelected, 1 - textSizeOffset));
                    } else {
                        mTextPaint.setColor(tabColorNormal);
                    }
                    canvas.drawRect(0, getPaddingTop(), itemWidth, getHeight()
                            - getPaddingBottom(), mTextPaint);
                    canvas.translate(itemWidth + intervalWidth, 0);
                }
            else {
                mTextPaint.setColor(tabColorSelected);
                canvas.drawRect(0, getPaddingTop(), itemWidth + intervalWidth
                                + getPaddingLeft(), getHeight() - getPaddingBottom(),
                        mTextPaint);
            }
            canvas.restore();
        }
        // TODO 两种类型的背景，传统背景及双色渐变背景
    }

    /**
     * 绘制间隔图案
     *
     * @param canvas 画布
     */
    private void drawItemInterval(Canvas canvas) {
        // TODO 绘制间隔图案
    }

    /**
     * 绘制下划线
     *
     * @param canvas 画布
     */
    private void drawUnderLine(Canvas canvas) {
        if (showUnderLine) {
            mTextPaint.setColor(underLineColor);
            canvas.drawRect(getPaddingLeft(), getHeight() - underLineHeight
                            - getPaddingBottom(), getWidth() - getPaddingRight(),
                    getHeight() - getPaddingBottom(), mTextPaint);
        }
    }

    /**
     * 绘制指示标
     *
     * @param canvas 画布
     */
    private void drawIndicator(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft() + currentPager
                * (itemWidth + intervalWidth) + ((itemWidth - indicatorWidth) * 0.5f)
                + indicatorOffset * (itemWidth + intervalWidth), getHeight()
                - underLineHeight - getPaddingBottom() - indicatorHeight);
        if (showIndicator) {
            mTextPaint.setColor(indicatorColor);
            if (getItemCount() > 1)
                canvas.drawRect(0, 0, indicatorWidth, indicatorHeight, mTextPaint);
            else if (getItemCount() > 0)
                canvas.drawRect(itemWidth / 4, 0, indicatorWidth - itemWidth / 4,
                        indicatorHeight, mTextPaint);
            else
                canvas.drawRect(itemWidth / 4, 0, indicatorWidth - itemWidth / 4,
                        indicatorHeight, mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawItemText(Canvas canvas) {
        canvas.save();
        canvas.translate(0, -heightOffset);
        float x = getPaddingLeft() + itemWidth / 2;
        int y = getHeight() - getPaddingBottom() - indicatorHeight
                - underLineHeight;
        int position = 0;
        for (int i = 0; i < getItemCount(); i++) {
            CharSequence charSequence = getItemText(i);
            String text = charSequence == null ? "" : charSequence.toString();
            canvas.save();
            mTextPaint.setColor(textColorNormal);
            mTextPaint.setTextAlign(Align.LEFT);
            mTextPaint.setTextSize(textSize);
            if (showTextGradient) {
                if (position == nextPager) {
                    mTextPaint.setColor(getColor(textColorNormal,
                            textColorSelected, textSizeOffset));
                } else if (position == currentPager) {
                    mTextPaint.setColor(getColor(textColorNormal,
                            textColorSelected, 1 - textSizeOffset));
                } else {
                    mTextPaint.setColor(textColorNormal);
                }
            }
            if (showTextScale) {
                if (position == nextPager) {
                    canvas.translate(x - mTextPaint.measureText(text)
                            * (1 + textSizeOffset * magnification) / 2, y
                            - textTop);
                    canvas.scale(1 + textSizeOffset * magnification, 1
                            + textSizeOffset * magnification);
                } else if (position == currentPager) {
                    canvas.translate(x - mTextPaint.measureText(text)
                            * (1 + (1 - textSizeOffset) * magnification) / 2, y
                            - textTop);
                    canvas.scale(1 + (1 - textSizeOffset) * magnification, 1
                            + (1 - textSizeOffset) * magnification);
                } else {
                    canvas.translate(x - mTextPaint.measureText(text) / 2, y
                            - textTop);
                    canvas.scale(1, 1);
                }
            } else {
                canvas.translate(x - mTextPaint.measureText(text) / 2, y
                        - textTop);
            }
            canvas.drawText(text, 0, 0, mTextPaint);
            x += itemWidth + intervalWidth;
            position++;
            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 绘制角标
     *
     * @param canvas 画布
     */
    private void drawItemTag(Canvas canvas) {
        // TODO
        if (mAdapter != null) {
            canvas.save();
            float canvasOffset = 0;
            int x = getPaddingLeft();
            int y = getPaddingTop();
            float textTop;
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTextAlign(Align.LEFT);
            for (int position = 0; position < getItemCount(); position++) {
                canvas.translate(canvasOffset, 0);
                canvasOffset = itemWidth + intervalWidth;
                canvas.save();
                if (mAdapter.isTagEnable(position)) {
                    int textWidth;
                    int textHeight;
                    mTextPaint.setTextSize(textSizeTag);
                    String tag = mAdapter.getTag(position) == null ? ""
                            : mAdapter.getTag(position);
                    textWidth = (int) Math.ceil(mTextPaint.measureText(tag));
                    FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                    textHeight = (int) Math.ceil(fontMetrics.descent
                            - fontMetrics.ascent);
                    textTop = textHeight
                            - (-fontMetrics.ascent - fontMetrics.descent + (fontMetrics.bottom - fontMetrics.descent)
                            * getResources().getDisplayMetrics().density);
                    textHeight += textTop;
                    if ("".equals(tag)) {
                        textHeight = 0;
                    }
                    int drawableWidth = backgroundTag == null ? 0 : backgroundTag.getMinimumWidth();
                    int drawableHeight = backgroundTag == null ? 0 : backgroundTag.getMinimumHeight();
                    float offsetX = Math.max(
                            textWidth + paddingStartTag + paddingEndTag,
                            drawableWidth);
                    float offsetTextX = (offsetX - (textWidth
                            + paddingStartTag + paddingEndTag)) * 0.5f;
                    float offsetY = Math.max(
                            textHeight + paddingBottomTag + paddingTopTag,
                            drawableHeight);
                    float offsetTextY = (offsetY - (textHeight
                            + paddingBottomTag + paddingTopTag)) * 0.5f;
                    mTextPaint.setTextSize(showTextScale ? textSize
                            * (1 + magnification) : textSize);
                    CharSequence charSequence = getItemText(position);
                    String text = charSequence == null ? "" : charSequence.toString();
                    float myTextWidth = mTextPaint.measureText(text);
                    // 只做右上角
                    // 文字右上角
//                    canvas.translate((itemWidth + myTextWidth) / 2 + marginStartTag, marginTopTag);
                    // Item右上角
                    canvas.translate(itemWidth - offsetX - marginEndTag, marginTopTag);
                    mTextPaint.setTextSize(textSizeTag);
                    if (backgroundTag != null) {
                        backgroundTag.setBounds(x, y, (int) (x + offsetX),
                                (int) (y + offsetY));
                        backgroundTag.draw(canvas);
                    }
                    canvas.drawText(
                            tag,
                            x + offsetTextX + paddingStartTag,
                            y + offsetY - textTop - offsetTextY - paddingTopTag,
                            mTextPaint);
                }
                canvas.restore();
            }
            canvas.restore();
        }
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