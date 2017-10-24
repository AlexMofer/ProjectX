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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 圆圈扩大图片
 * Created by Alex on 2016/9/9.
 */
@SuppressWarnings("unused")
public class CircleExpandDrawable extends Drawable {

    private static final long FRAME_DURATION = 1000 / 60;
    private static final float INCREASE = 0.02f;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
    private final RectF mOval = new RectF();
    private boolean toExpand = true;// 扩大动画
    private float offset = 0; //偏移
    private float offsetAnimation = 0;
    private boolean mIsRunning = false;// 是否正在动画
    private float mIncreaseValue;
    private Interpolator mInterpolator = new AccelerateInterpolator();
    private AnimationCallback callback;
    private boolean tellStart = false;
    private final Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                if (toExpand) {
                    if (offset >= 1) {
                        mIsRunning = false;
                        if (callback != null)
                            callback.onAnimationEnd(CircleExpandDrawable.this);
                        return;
                    } else {
                        offset += mIncreaseValue;
                        offset = offset > 1 ? 1 : offset;
                    }
                } else {
                    if (offset <= 0) {
                        mIsRunning = false;
                        if (callback != null)
                            callback.onAnimationEnd(CircleExpandDrawable.this);
                        return;
                    } else {
                        offset -= mIncreaseValue;
                        offset = offset < 0 ? 0 : offset;
                    }
                }
                offsetAnimation = mInterpolator.getInterpolation(offset);
                if (tellStart) {
                    tellStart = false;
                    if (callback != null)
                        callback.onAnimationStart(CircleExpandDrawable.this);
                }
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
                invalidateSelf();
            }
        }
    };

    public CircleExpandDrawable(int color) {
        this(color, INCREASE);
    }

    public CircleExpandDrawable(int color, float increaseValue) {
        setColor(color);
        setIncreaseValue(increaseValue);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds == null)
            return;
        final int smallSize = bounds.width() > bounds.height() ? bounds.height() : bounds.width();
        final float bigSize = (float) (Math.sqrt(bounds.width() * bounds.width()
                + bounds.height() * bounds.height()));
        final float realSize = smallSize + (bigSize - smallSize) * offsetAnimation;
        final float halfSize = realSize * 0.5f;
        final float centerX = bounds.exactCenterX();
        final float centerY = bounds.exactCenterY();
        mOval.set(centerX - halfSize, centerY - halfSize, centerX + halfSize, centerY + halfSize);
        canvas.drawOval(mOval, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    /**
     * 设置增进值
     *
     * @param value 增进值
     */
    public void setIncreaseValue(float value) {
        if (value <= 0 || value >= 1)
            return;
        mIncreaseValue = value;
    }

    /**
     * 设置补帧器
     *
     * @param interpolator 补帧器
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 缩小
     */
    public void zoomIn() {
        if (mIsRunning) {
            if (!toExpand)
                return;
            unscheduleSelf(mUpdater);
        }
        toExpand = false;
        mIsRunning = true;
        offset = 1 - mIncreaseValue;
        offsetAnimation = mInterpolator.getInterpolation(offset);
        if (callback != null)
            callback.onAnimationStart(this);
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();
    }

    /**
     * 缩小
     *
     * @param delayMillis 开始执行间隔
     */
    public void zoomIn(long delayMillis) {
        if (mIsRunning) {
            if (!toExpand)
                return;
            unscheduleSelf(mUpdater);
        }
        toExpand = false;
        mIsRunning = true;
        offset = 1;
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + delayMillis);
        tellStart = true;
    }

    /**
     * 放大
     */
    public void zoomOut() {
        if (mIsRunning) {
            if (toExpand)
                return;
            unscheduleSelf(mUpdater);
        }
        toExpand = true;
        mIsRunning = true;
        offset = mIncreaseValue;
        offsetAnimation = mInterpolator.getInterpolation(offset);
        if (callback != null)
            callback.onAnimationStart(this);
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();
    }

    /**
     * 放大
     *
     * @param delayMillis 开始执行间隔
     */
    public void zoomOut(long delayMillis) {
        if (mIsRunning) {
            if (toExpand)
                return;
            unscheduleSelf(mUpdater);
        }
        toExpand = true;
        mIsRunning = true;
        offset = 0;
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + delayMillis);
        tellStart = true;
    }

    /**
     * 是否为放大
     *
     * @return 是否为放大
     */
    public boolean isToExpand() {
        return toExpand;
    }

    public void setCallback(AnimationCallback callback) {
        this.callback = callback;
    }
}
