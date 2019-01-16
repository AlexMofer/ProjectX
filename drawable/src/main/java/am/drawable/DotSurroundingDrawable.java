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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 圆点环绕动图
 * Created by Alex on 2019/1/10.
 */
@SuppressWarnings({"NullableProblems", "unused", "WeakerAccess"})
public class DotSurroundingDrawable extends AnimationDrawableWrapper {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mAlpha = 0xFF;
    private int mSize;
    private ColorStateList mColor;

    public DotSurroundingDrawable() {
        this(null, 0, Color.BLACK);
    }

    public DotSurroundingDrawable(Drawable drawable, int size, int color) {
        super(drawable);
        mSize = size;
        mColor = ColorStateList.valueOf(color);
        setRepeatCount(INFINITE);
        setAutoStart(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.DotSurroundingDrawable);
        mSize = custom.getDimensionPixelSize(R.styleable.DotSurroundingDrawable_android_width,
                0);
        final ColorStateList color = custom.getColorStateList(
                R.styleable.DotSurroundingDrawable_android_color);
        custom.recycle();
        mColor = color == null ? ColorStateList.valueOf(Color.BLACK) : color;
        setWrappedDrawableFormText(resources, parser, attrs, theme);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mSize <= 0 || mColor == null)
            return;
        final float animate = getAnimatedValue();
        canvas.save();
        final Rect bounds = getBounds();
        canvas.rotate(360 * animate, bounds.exactCenterX(), bounds.exactCenterY());
        final float radius = mSize * 0.5f;
        mPaint.setColor(DrawableHelper.getColor(mColor, getState(), mAlpha));
        canvas.drawCircle(bounds.right - radius, bounds.exactCenterY(), radius, mPaint);
        canvas.restore();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return;
        drawable.setBounds(bounds.left + mSize, bounds.top + mSize,
                bounds.right + mSize, bounds.bottom + mSize);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        super.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
        mPaint.setColorFilter(cf);
    }

    @Override
    public boolean isStateful() {
        return super.isStateful() || (mColor != null && mColor.isStateful());
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        final int result = super.getIntrinsicWidth();
        if (result == -1)
            return -1;
        return result + mSize * 2;
    }

    @Override
    public int getIntrinsicHeight() {
        final int result = super.getIntrinsicHeight();
        if (result == -1)
            return -1;
        return result + mSize * 2;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        return isStateful();
    }

    @Override
    public long getDuration() {
        return super.getDuration();
    }

    @Override
    public void setDuration(long duration) {
        super.setDuration(duration);
    }

    @Override
    public long getFrameDelay() {
        return super.getFrameDelay();
    }

    @Override
    public void setFrameDelay(long frameDelay) {
        super.setFrameDelay(frameDelay);
    }

    @Override
    public int getRepeatMode() {
        return super.getRepeatMode();
    }

    @Override
    public void setRepeatMode(int mode) {
        super.setRepeatMode(mode);
    }

    @Override
    public void setRepeatCount(int count) {
        super.setRepeatCount(count);
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        super.setInterpolator(interpolator);
    }

    @Override
    public Interpolator getInterpolator() {
        return super.getInterpolator();
    }

    @Override
    public boolean isAutoStart() {
        return super.isAutoStart();
    }

    @Override
    public void setAutoStart(boolean auto) {
        super.setAutoStart(auto);
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public boolean isPaused() {
        return super.isPaused();
    }

    @Override
    public long getRepeatCompletedCount() {
        return super.getRepeatCompletedCount();
    }

    /**
     * 设置圆点直径
     *
     * @param size 圆点直径
     */
    public void setDotSize(int size) {
        if (mSize == size)
            return;
        mSize = size;
        invalidateSelf();
    }

    /**
     * 获取圆点直径
     *
     * @return 圆点直径
     */
    public int getDotSize() {
        return mSize;
    }

    /**
     * 设置圆点颜色
     *
     * @param color 颜色
     */
    public void setDotColor(ColorStateList color) {
        if (mColor == color)
            return;
        mColor = color;
        invalidateSelf();
    }

    /**
     * 获取圆点颜色
     *
     * @return 圆点颜色
     */
    public ColorStateList getColor() {
        return mColor;
    }
}
