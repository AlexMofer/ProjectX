/*
 * Copyright (C) 2019 AlexMofer
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

package am.drawable;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 文字图片
 * Created by Alex on 2019/1/11.
 */
@SuppressWarnings({"NullableProblems", "WeakerAccess", "unused"})
public class TextDrawable extends Drawable {

    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mMeasureRect = new Rect();
    private float mTextSize;
    private int mAlpha = 0xFF;
    private String mText;
    private int mWidth;
    private int mHeight;
    private boolean mAutoScale;
    private ColorStateList mTextColor;

    public TextDrawable() {
        this(null, 0, Color.BLACK);
    }

    public TextDrawable(String text, float textSize, int textColor) {
        this(text, textSize, ColorStateList.valueOf(textColor));
    }

    public TextDrawable(String text, float textSize, ColorStateList textColor) {
        this(text, textSize, textColor, false);
    }

    public TextDrawable(String text, float textSize, ColorStateList textColor, boolean autoScale) {
        this(text, textSize, textColor, autoScale, -1, -1);
    }

    public TextDrawable(String text, float textSize, ColorStateList textColor, boolean autoScale,
                        int width, int height) {
        mText = text;
        mTextSize = textSize;
        mTextColor = textColor;
        mAutoScale = autoScale;
        mWidth = width;
        mHeight = height;
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final float density = resources.getDisplayMetrics().density;
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.TextDrawable);
        mWidth = custom.getDimensionPixelSize(R.styleable.TextDrawable_android_width, -1);
        mHeight = custom.getDimensionPixelSize(R.styleable.TextDrawable_android_height,
                -1);
        mTextSize = custom.getDimension(R.styleable.TextDrawable_android_textSize,
                14 * density);
        mTextColor = custom.getColorStateList(R.styleable.TextDrawable_android_textColor);
        mText = custom.getString(R.styleable.TextDrawable_android_text);
        mAutoScale = custom.getBoolean(R.styleable.TextDrawable_tdAutoScale, false);
        if (custom.hasValue(R.styleable.TextDrawable_android_typeface)) {
            final int typeface = custom.getInteger(R.styleable.TextDrawable_android_typeface,
                    0);
            switch (typeface) {
                default:
                case 0:
                    mPaint.setTypeface(Typeface.DEFAULT);
                    break;
                case 1:
                    mPaint.setTypeface(Typeface.SANS_SERIF);
                    break;
                case 2:
                    mPaint.setTypeface(Typeface.SERIF);
                    break;
                case 3:
                    mPaint.setTypeface(Typeface.MONOSPACE);
                    break;
            }
        }
        if (custom.hasValue(R.styleable.TextDrawable_android_fontFamily) ||
                custom.hasValue(R.styleable.TextDrawable_android_textStyle)) {
            final String fontFamily = custom.getString(R.styleable.TextDrawable_android_fontFamily);
            final int style = custom.getInteger(R.styleable.TextDrawable_android_textStyle,
                    0);
            mPaint.setTypeface(Typeface.create(fontFamily, style));
        }
        custom.recycle();
        mPaint.density = density;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mText == null || mText.length() <= 0)
            return;
        mPaint.setColor(DrawableHelper.getColor(mTextColor, getState(), mAlpha));
        canvas.save();
        if (mAutoScale) {
            final Rect bounds = getBounds();
            float scale = Math.min((float) bounds.width() / mMeasureRect.width(),
                    (float) bounds.height() / mMeasureRect.height());
            canvas.translate(getBounds().centerX(), getBounds().centerY());
            canvas.scale(scale, scale, 0, 0);
            canvas.translate(0, mMeasureRect.height() * 0.5f);
            canvas.drawText(mText, 0, -mMeasureRect.bottom, mPaint);
        } else {
            canvas.translate(getBounds().centerX(), getBounds().centerY());
            canvas.translate(0, mMeasureRect.height() * 0.5f);
            canvas.drawText(mText, 0, -mMeasureRect.bottom, mPaint);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha == alpha)
            return;
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        update();
    }

    private void update() {
        if (mText == null || mText.length() <= 0)
            return;
        final Rect bounds = getBounds();
        if (mAutoScale)
            mPaint.setTextSize(Math.min(bounds.width(), bounds.height()));
        else
            mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth <= 0 ? super.getIntrinsicWidth() : mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight <= 0 ? super.getIntrinsicHeight() : mHeight;
    }

    @Override
    public boolean isStateful() {
        return mTextColor != null && mTextColor.isStateful();
    }

    /**
     * 设置尺寸
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setSize(int width, int height) {
        if (mWidth == width && mHeight == height)
            return;
        mWidth = width;
        mHeight = height;
        update();
        invalidateSelf();
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(float size) {
        if (mTextSize == size)
            return;
        mTextSize = size;
        update();
        invalidateSelf();
    }

    /**
     * 获取文字大小
     *
     * @return 文字大小
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(ColorStateList color) {
        if (mTextColor == color)
            return;
        mTextColor = color;
        invalidateSelf();
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(int color) {
        setTextColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取文字颜色
     *
     * @return 文字颜色
     */
    public ColorStateList getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        if (TextUtils.equals(mText, text))
            return;
        mText = text;
        update();
        invalidateSelf();
    }

    /**
     * 获取文字
     *
     * @return 文字
     */
    public String getText() {
        return mText;
    }

    /**
     * 设置是否自动缩放
     *
     * @param auto 是否自动缩放
     */
    public void setAutoScale(boolean auto) {
        if (mAutoScale == auto)
            return;
        mAutoScale = auto;
        update();
        invalidateSelf();
    }

    /**
     * 判断是否自动缩放
     *
     * @return 是否自动缩放
     */
    public boolean isAutoScale() {
        return mAutoScale;
    }

    /**
     * 设置字体
     *
     * @param typeface 字体
     */
    public void setTypeface(Typeface typeface) {
        if (mPaint.getTypeface() == typeface)
            return;
        mPaint.setTypeface(typeface);
        update();
        invalidateSelf();
    }

    /**
     * 获取字体
     *
     * @return 字体
     */
    public Typeface getTypeface() {
        return mPaint.getTypeface();
    }

    /**
     * 设置密度
     *
     * @param density 密度
     */
    public void setDensity(float density) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            if (mPaint.density == density)
                return;
            mPaint.density = density;
            update();
            invalidateSelf();
        }
    }
}
