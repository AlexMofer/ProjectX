/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import io.github.alexmofer.android.support.R;
import io.github.alexmofer.android.support.utils.TypedArrayUtils;

/**
 * 旋转Drawable
 * Created by Alex on 2024/4/18.
 */
@Keep
public class AnimatedRotateDrawable extends DrawableWrapperCompat implements Animatable {
    private static final Drawable DEFAULT = new ColorDrawable(Color.TRANSPARENT);
    private boolean mPivotXRel = true;
    private float mPivotX = 0.5f;
    private boolean mPivotYRel = true;
    private float mPivotY = 0.5f;
    private int mFrameDuration = 10;
    private int mFramesCount = 120;
    private float mCurrentDegrees;
    private float mIncrement = 3;
    private boolean mReverse = false;
    private boolean mRunning;

    public AnimatedRotateDrawable() {
        super(DEFAULT);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        final Rect bounds = drawable.getBounds();
        final int w = bounds.right - bounds.left;
        final int h = bounds.bottom - bounds.top;

        final float px = mPivotXRel ? (w * mPivotX) : mPivotX;
        final float py = mPivotYRel ? (h * mPivotY) : mPivotY;

        final int saveCount = canvas.save();
        canvas.rotate(mReverse ? 360 - mCurrentDegrees : mCurrentDegrees,
                px + bounds.left, py + bounds.top);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /**
     * Starts the rotation animation.
     * <p>
     * The animation will run until {@link #stop()} is called. Calling this
     * method while the animation is already running has no effect.
     *
     * @see #stop()
     */
    @Override
    public void start() {
        if (!mRunning) {
            mRunning = true;
            nextFrame();
        }
    }

    /**
     * Stops the rotation animation.
     *
     * @see #start()
     */
    @Override
    public void stop() {
        mRunning = false;
        unscheduleSelf(mNextFrame);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    private void nextFrame() {
        unscheduleSelf(mNextFrame);
        scheduleSelf(mNextFrame, SystemClock.uptimeMillis() + mFrameDuration);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        final boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (changed || restart) {
                mCurrentDegrees = 0.0f;
                if (mRunning) {
                    nextFrame();
                }
            }
        } else {
            unscheduleSelf(mNextFrame);
        }
        return changed;
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser,
                        @NonNull AttributeSet attrs, @Nullable Resources.Theme theme)
            throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs, theme);
        TypedArrayUtils.handleTypedArray(() -> TypedArrayUtils.obtainAttributes(r, theme, attrs,
                        R.styleable.AnimatedRotateDrawable),
                custom -> {
                    setVisible(custom.getBoolean(R.styleable.AnimatedRotateDrawable_android_visible, isVisible()),
                            true);
                    setDrawable(custom.getDrawable(R.styleable.AnimatedRotateDrawable_android_drawable));
                    if (custom.hasValue(R.styleable.AnimatedRotateDrawable_android_pivotX)) {
                        final TypedValue tv = custom.peekValue(R.styleable.AnimatedRotateDrawable_android_pivotX);
                        mPivotXRel = tv.type == TypedValue.TYPE_FRACTION;
                        mPivotX = mPivotXRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
                    }
                    if (custom.hasValue(R.styleable.AnimatedRotateDrawable_android_pivotY)) {
                        final TypedValue tv = custom.peekValue(R.styleable.AnimatedRotateDrawable_android_pivotY);
                        mPivotYRel = tv.type == TypedValue.TYPE_FRACTION;
                        mPivotY = mPivotYRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
                    }
                    setFramesCount(custom.getInt(R.styleable.AnimatedRotateDrawable_android_progress,
                            mFramesCount));
                    setFrameDuration(custom.getInt(R.styleable.AnimatedRotateDrawable_android_duration,
                            mFrameDuration));
                    setReverse(custom.getInt(R.styleable.AnimatedRotateDrawable_android_repeatMode, 1) == 2);
                });
    }

    /**
     * 设置旋转一周的帧数
     *
     * @param framesCount 帧数
     */
    public void setFramesCount(int framesCount) {
        if (mFramesCount == framesCount) {
            return;
        }
        mFramesCount = framesCount;
        mIncrement = 360.0f / mFramesCount;
        invalidateSelf();
    }

    /**
     * 设置每一帧所需时间
     *
     * @param frameDuration 每一帧所需时间
     */
    public void setFrameDuration(int frameDuration) {
        if (mFrameDuration == frameDuration) {
            return;
        }
        mFrameDuration = frameDuration;
        invalidateSelf();
    }

    /**
     * 设置是否逆时针
     *
     * @param reverse 是否逆时针
     */
    public void setReverse(boolean reverse) {
        if (mReverse == reverse) {
            return;
        }
        mReverse = reverse;
        invalidateSelf();
    }

    private final Runnable mNextFrame = new Runnable() {
        @Override
        public void run() {
            // of time since the last frame drawn
            mCurrentDegrees += mIncrement;
            if (mCurrentDegrees > (360.0f - mIncrement)) {
                mCurrentDegrees = 0.0f;
            }
            invalidateSelf();
            if (mRunning) {
                nextFrame();
            }
        }
    };
}
