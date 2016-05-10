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

package com.am.widget.tabstrips;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.support.v4.view.BaseTabStrip;
import android.text.TextPaint;
import android.util.AttributeSet;


/**
 * 移动式下标TabStrip，Item不建议超过5个
 * 
 * @author Alex
 * 
 */
public class IndicatorTabStrip extends BaseTabStrip {

    final float density;
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
    private ItemTabAdapter mAdapter;
    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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

    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor};

    public IndicatorTabStrip(Context context) {
        this(context, null);
    }

    public IndicatorTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    @SuppressWarnings("ResourceType")
    public IndicatorTabStrip(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemClickable(true);
        density = getResources().getDisplayMetrics().density;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int textSize = a.getDimensionPixelSize(0, (int) (16 * density));
        int textColorSelected = a.getColor(1, Color.BLACK);
        int textColorNormal = Color.argb(Color.alpha(0x80000000),
                Color.red(textColorSelected), Color.green(textColorSelected),
                Color.blue(textColorSelected));
        a.recycle();
        mTextPaint.setAntiAlias(true);

        setItemInterval(0);
        showGradientItemBackground(false);
        setGradientItemBackground(0x00000000, 0x00000000);
        showUnderline(false);
        setUnderline(0x00000000, 0);
        showIndicator(false);
        setIndicator(0x00000000, 0, 0);
        setItemTextSize(textSize);
        showItemTextGradient(false);
        setItemTextGradient(textColorNormal, textColorSelected);
        showItemTextScale(false);
        setItemTextScaleMagnification(1);
        // TODO
        textSizeTag = 10 * density;
        backgroundTag = getDefaultTagBackground();
        paddingStartTag = (int) (2 * density);
        paddingEndTag = (int) (2 * density);
        paddingTopTag = 0;
        paddingBottomTag = 0;
        marginStartTag = (int) (4 * density);
        marginEndTag = (int) (4 * density);
        marginTopTag = (int) (4 * density);
    }

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            // TODO 使用不确定宽度处理
            throw new IllegalStateException("Must measure with an exact width");
        }
        itemWidth = (float) (widthSize - getPaddingLeft() - getPaddingRight() - intervalWidth
                * (getItemCount() <= 0 ? 1 : getItemCount() - 1))
                / (getItemCount() <= 0 ? 1 : getItemCount());
        mTextPaint.setTextSize(magnification <= 0 ? textSize : textSize
                + textSize * magnification);
        // TODO 当Tab文字长度超过itemWidth时需处理
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        textTop = textHeight
                - (-fontMetrics.ascent - fontMetrics.descent + (fontMetrics.bottom - fontMetrics.descent)
                * density);
        textHeight += textTop;
        int itemHeight = (int) Math.ceil(textHeight + indicatorHeight
                + underLineHeight);
        int minHeight = getMinHeight();
        if (minHeight > itemHeight + getPaddingTop() + getPaddingBottom()) {
            itemHeight = minHeight - getPaddingTop() - getPaddingBottom();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            heightOffset = (heightSize - (itemHeight + getPaddingTop() + getPaddingBottom()))
                    * 0.5f;
            setMeasuredDimension(widthSize, heightSize);
        } else {
            heightOffset = 0;
            setMeasuredDimension(widthSize, itemHeight + getPaddingTop()
                    + getPaddingBottom());
        }

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
    protected void jumpTo(int current) {
        indicatorOffset = 1;
        currentPager = current - 1;
        nextPager = current;
        textSizeOffset = 1;
        invalidate();
    }

    @Override
    protected void gotoLeft(int current, int next, float mPositionOffset) {
        indicatorOffset = mPositionOffset - 1;
        currentPager = current;
        nextPager = next;
        textSizeOffset = 1 - mPositionOffset;
        invalidate();
    }

    @Override
    protected void gotoRight(int current, int next, float mPositionOffset) {
        indicatorOffset = mPositionOffset;
        currentPager = current;
        nextPager = next;
        textSizeOffset = mPositionOffset;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawItemBackground(canvas);
        drawItemInterval(canvas);
        drawUnderLine(canvas);
        drawIndicator(canvas);
        drawItemText(canvas);
        drawItemTag(canvas);
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
                            * density);
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
     * 设置Item之间的间隙
     *
     * @param width 间隔宽度
     */
    public void setItemInterval(int width) {
        intervalWidth = width;
        invalidate();
    }

    /**
     * 获取Item之间的间隙
     *
     * @return 间隔宽度
     */
    @SuppressWarnings("unused")
    public int getItemInterval() {
        return intervalWidth;
    }

    /**
     * 是否显示Tab背景颜色
     * <p>渐变背景绘制在传统背景下面，其无点按效果</p>
     *
     * @param show 显示背景
     */
    public void showGradientItemBackground(boolean show) {
        showTabColor = show;
        invalidate();
    }

    /**
     * 设置Tab背景颜色
     * <p>渐变背景绘制在传统背景下面，其无点按效果</p>
     *
     * @param colorNormal   普通颜色
     * @param colorSelected 选中颜色
     */
    public void setGradientItemBackground(int colorNormal, int colorSelected) {
        tabColorNormal = colorNormal;
        tabColorSelected = colorSelected;
        invalidate();
    }

    /**
     * 显示下划线
     *
     * @param show 显示下划线
     */
    public void showUnderline(boolean show) {
        showUnderLine = show;
        invalidate();
    }

    /**
     * 设置下划线
     *
     * @param color  下划线颜色
     * @param height 下划线高度
     */
    public void setUnderline(int color, int height) {
        underLineColor = color;
        underLineHeight = height;
        invalidate();
    }

    /**
     * 显示标签下标
     *
     * @param show 显示下标
     */
    public void showIndicator(boolean show) {
        showIndicator = show;
        invalidate();
    }

    /**
     * 设置标签下标
     *
     * @param color  下标颜色
     * @param height 下标高度
     * @param width  下标宽度
     */
    public void setIndicator(int color, int width, int height) {
        indicatorColor = color;
        indicatorWidth = width;
        indicatorHeight = height;
        invalidate();
    }

    /**
     * 设置字体大小
     *
     * @param size 设置文字大小
     */
    public void setItemTextSize(int size) {
        textSize = size;
        invalidate();
    }

    /**
     * 显示文字颜色渐变
     *
     * @param show 显示文字渐变
     */
    public void showItemTextGradient(boolean show) {
        showTextGradient = show;
        invalidate();
    }

    /**
     * 设置文字颜色
     *
     * @param colorNormal   普通颜色
     * @param colorSelected 选中颜色
     */
    public void setItemTextGradient(int colorNormal, int colorSelected) {
        textColorNormal = colorNormal;
        textColorSelected = colorSelected;
        invalidate();
    }

    /**
     * 是否显示文字缩放
     *
     * @param show 是否显示文字缩放
     */
    public void showItemTextScale(boolean show) {
        showTextScale = show;
        invalidate();
    }


    /**
     * 设置文字放大发倍数
     *
     * @param magnification 文字缩放比
     */
    public void setItemTextScaleMagnification(float magnification) {
        if (magnification == 1)
            showItemTextScale(false);
        else {
            this.magnification = magnification - 1;
            invalidate();
        }
    }

    /**
     * 获取Adapter
     *
     * @return Adapter
     */
    @SuppressWarnings("unused")
    public final ItemTabAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public final void setAdapter(ItemTabAdapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            requestLayout();
            invalidate();
        }
    }

}