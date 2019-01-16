/*
 * Copyright (C) 2015 AlexMofer
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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 进度Drawable
 * Created by Alex on 2019/1/15.
 */
@SuppressWarnings({"NullableProblems", "unused", "WeakerAccess"})
public class ProgressDrawable extends DrawableWrapper {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mProgressDrawable;
    private int mMax = 100;
    private int mProgress;
    private final Path mClear = new Path();

    public ProgressDrawable() {
        this(null, null);
    }

    public ProgressDrawable(Drawable progress, Drawable background) {
        super(background);
        mProgressDrawable = progress;
        if (progress != null) {
            progress.setCallback(this);
            progress.setBounds(0, 0, Math.max(1, progress.getMinimumWidth()),
                    Math.max(1, progress.getMinimumHeight()));
        }
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClear.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.ProgressDrawable);
        final Drawable background = custom.getDrawable(
                R.styleable.ProgressDrawable_android_background);
        if (custom.hasValue(R.styleable.ProgressDrawable_android_max))
            mMax = custom.getInteger(R.styleable.ProgressDrawable_android_max, mMax);
        mProgress = custom.getInteger(R.styleable.ProgressDrawable_android_progress, mProgress);
        final Drawable progress = custom.getDrawable(
                R.styleable.ProgressDrawable_android_progressDrawable);
        custom.recycle();
        if (background != null) {
            setWrappedDrawable(background);
        }
        if (progress != null) {
            mProgressDrawable = progress;
            progress.setCallback(this);
            progress.setBounds(0, 0, Math.max(1, progress.getMinimumWidth()),
                    Math.max(1, progress.getMinimumHeight()));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mProgressDrawable == null)
            return;
        final Rect bounds = getBounds();
        final int layer = Compat.saveLayer(canvas, bounds.left, bounds.top, bounds.right,
                bounds.bottom, null);
        final Drawable progress = mProgressDrawable;
        final int progressWidth = Math.max(1, progress.getMinimumWidth());
        final int progressHeight = Math.max(1, progress.getMinimumHeight());
        canvas.save();
        canvas.translate(0, bounds.exactCenterY() - progressHeight * 0.5f);
        final int total = bounds.width();
        int width = 0;
        while (width <= total) {
            canvas.save();
            canvas.translate(width, 0);
            progress.draw(canvas);
            canvas.restore();
            width += progressWidth;
        }
        canvas.restore();
        canvas.drawPath(mClear, mPaint);
        canvas.restoreToCount(layer);
    }

    @Override
    public void setChangingConfigurations(int configs) {
        super.setChangingConfigurations(configs);
        if (mProgressDrawable == null)
            return;
        mProgressDrawable.setChangingConfigurations(configs);
    }

    @Override
    public void setDither(boolean dither) {
        super.setDither(dither);
        if (mProgressDrawable == null)
            return;
        mProgressDrawable.setDither(dither);
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        super.setFilterBitmap(filter);
        if (mProgressDrawable == null)
            return;
        mProgressDrawable.setFilterBitmap(filter);
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        if (mProgressDrawable == null)
            return;
        mProgressDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
        if (mProgressDrawable == null)
            return;
        mProgressDrawable.setColorFilter(cf);
    }

    @Override
    public boolean isStateful() {
        return super.isStateful() || (mProgressDrawable != null && mProgressDrawable.isStateful());
    }

    @Override
    public boolean setState(int[] stateSet) {
        if (mProgressDrawable == null)
            return super.setState(stateSet);
        final boolean result = mProgressDrawable.setState(stateSet);
        return super.setState(stateSet) || result;
    }

    @Override
    public void jumpToCurrentState() {
        super.jumpToCurrentState();
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mProgressDrawable.jumpToCurrentState();
    }

    @Override
    public Drawable getCurrent() {
        return this;
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if (mProgressDrawable == null)
            return super.setVisible(visible, restart);
        final boolean result = mProgressDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart) || result;
    }

    @Override
    protected boolean onLevelChange(int level) {
        if (mProgressDrawable == null)
            return super.onLevelChange(level);
        final boolean result = mProgressDrawable.setLevel(level);
        return super.onLevelChange(level) || result;
    }

    @Override
    public void setTint(int tint) {
        super.setTint(tint);
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mProgressDrawable.setTint(tint);
    }

    @Override
    public void setTintList(ColorStateList tint) {
        super.setTintList(tint);
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mProgressDrawable.setTintList(tint);
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        super.setTintMode(tintMode);
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mProgressDrawable.setTintMode(tintMode);
    }

    @Override
    public void setHotspot(float x, float y) {
        super.setHotspot(x, y);
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mProgressDrawable.setHotspot(x, y);
    }

    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        super.setHotspotBounds(left, top, right, bottom);
        if (mProgressDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mProgressDrawable.setHotspotBounds(left, top, right, bottom);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        update();
    }

    private void update() {
        mClear.reset();
        final Rect bounds = getBounds();
        final float width = (float) bounds.width() * mProgress / mMax;
        if (Compat.isLayoutDirectionLTR(this))
            mClear.addRect(bounds.left + width, bounds.top, bounds.right,
                    bounds.bottom, Path.Direction.CW);
        else
            mClear.addRect(bounds.left, bounds.top, bounds.right - width,
                    bounds.bottom, Path.Direction.CW);
    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return true;
    }

    /**
     * 设置背景图
     *
     * @param background 背景图
     */
    public void setBackground(Drawable background) {
        if (getWrappedDrawable() == background)
            return;
        setWrappedDrawable(background);
        update();
        invalidateSelf();
    }

    /**
     * 获取背景图
     *
     * @return 背景图
     */
    public Drawable getBackground() {
        return getWrappedDrawable();
    }

    /**
     * 设置进度图片
     *
     * @param progress 进度图片
     */
    public void getProgressDrawable(Drawable progress) {
        if (mProgressDrawable == progress)
            return;
        if (mProgressDrawable != null) {
            mProgressDrawable.setCallback(null);
            mProgressDrawable = null;
        }
        mProgressDrawable = progress;
        if (progress != null) {
            progress.setCallback(this);
            progress.setBounds(0, 0, Math.max(1, progress.getMinimumWidth()),
                    Math.max(1, progress.getMinimumHeight()));
        }
        invalidateSelf();
    }

    /**
     * 获取进度图片
     *
     * @return 进度图片
     */
    public Drawable getProgressDrawable() {
        return mProgressDrawable;
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        if (mMax == max)
            return;
        mMax = max;
        update();
        invalidateSelf();
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    public int getMax() {
        return mMax;
    }

    /**
     * 设置进度值
     *
     * @param progress 进度值
     */
    public void setProgress(int progress) {
        if (mProgress == progress)
            return;
        mProgress = progress;
        update();
        invalidateSelf();
    }

    /**
     * 获取进度值
     *
     * @return 进度值
     */
    public int getProgress() {
        return mProgress;
    }
}
