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

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 动画图片
 * Created by Alex on 2019/1/8.
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "NullableProblems", "BooleanMethodIsAlwaysInverted"})
abstract class AnimationDrawable extends Drawable implements Animatable {

    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int INFINITE = -1;

    private static final long FRAME_DELAY = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ValueAnimator.getFrameDelay() : 10;
    private static final int DEFAULT_DURATION = 250;
    private long mStartTime;
    private boolean mRunning = false;
    private long mDuration = DEFAULT_DURATION;
    private long mFrameDelay = FRAME_DELAY;
    private int mRepeatMode = 0;
    private int mRepeatCount = 0;
    private long mRepeatCompletedCount = 0;
    private final Animation mAnimation = new Animation();
    private float mInterpolation = 0;
    private Interpolator mInterpolator;
    private boolean mPaused = false;
    private long mPausedTime;
    private long mTimeOffset = 0;
    private boolean mAutoStart = false;

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.AnimationDrawable);
        if (custom.hasValue(R.styleable.AnimationDrawable_android_repeatMode))
            mRepeatMode = custom.getInt(R.styleable.AnimationDrawable_android_repeatMode, RESTART);
        if (custom.hasValue(R.styleable.AnimationDrawable_android_repeatCount))
            mRepeatCount = custom.getInt(R.styleable.AnimationDrawable_android_repeatCount,
                    INFINITE);
        if (custom.hasValue(R.styleable.AnimationDrawable_android_duration))
            mDuration = custom.getInteger(R.styleable.AnimationDrawable_android_duration,
                    DEFAULT_DURATION);
        if (custom.hasValue(R.styleable.AnimationDrawable_android_autoStart))
            mAutoStart = custom.getBoolean(R.styleable.AnimationDrawable_android_autoStart,
                    false);
        custom.recycle();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (mAutoStart)
            if (!mRunning)
                start();
    }

    /**
     * 获取时长
     *
     * @return 时长
     */
    protected long getDuration() {
        return mDuration;
    }

    /**
     * 设置时长
     *
     * @param duration 时长
     */
    protected void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * 获取帧间隔时间
     *
     * @return 帧间隔时间
     */
    protected long getFrameDelay() {
        return mFrameDelay;
    }

    /**
     * 设置帧间隔时间
     *
     * @param frameDelay 帧间隔时间
     */
    protected void setFrameDelay(long frameDelay) {
        mFrameDelay = frameDelay;
    }

    /**
     * 获取重复模式
     *
     * @return 重复模式
     */
    protected int getRepeatMode() {
        return mRepeatMode;
    }

    /**
     * 设置重复模式
     *
     * @param mode 重复模式 {@link #RESTART} 或 {@link #REVERSE}
     */
    protected void setRepeatMode(int mode) {
        mRepeatMode = mode;
    }

    /**
     * 设置是否为重复动画
     *
     * @param count 重复次数 大于0或{@link #INFINITE}
     */
    protected void setRepeatCount(int count) {
        mRepeatCount = count;
    }

    /**
     * 设置补帧器
     *
     * @param interpolator 补帧器
     */
    protected void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 获取补帧器
     *
     * @return 补帧器
     */
    protected Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * 判断是否为自动运行动画
     *
     * @return 是否为自动运行动画
     */
    protected boolean isAutoStart() {
        return mAutoStart;
    }

    /**
     * 设置是否为自动运行动画
     *
     * @param auto 是否为自动运行动画
     */
    protected void setAutoStart(boolean auto) {
        mAutoStart = auto;
    }

    /**
     * 开始动画
     */
    @Override
    public void start() {
        if (mRunning)
            return;
        mRunning = true;
        mStartTime = -1;
        mRepeatCompletedCount = 0;
        mTimeOffset = 0;
        onAnimationStart();
        scheduleSelf(mAnimation, AnimationUtils.currentAnimationTimeMillis());
    }

    /**
     * 结束动画
     */
    @Override
    public void stop() {
        end();
    }

    /**
     * 取消动画
     */
    protected void cancel() {
        if (!mRunning)
            return;
        mRunning = false;
        unscheduleSelf(mAnimation);
        onAnimationCancel();
        onAnimationEnd();
    }

    /**
     * 结束动画
     */
    protected void end() {
        if (!mRunning)
            return;
        mRunning = false;
        unscheduleSelf(mAnimation);
        mInterpolation = 1;
        onAnimationEnd();
    }

    /**
     * 暂停动画
     */
    protected void pause() {
        if (!mRunning)
            return;
        if (mPaused)
            return;
        mPaused = true;
        mPausedTime = AnimationUtils.currentAnimationTimeMillis();
        unscheduleSelf(mAnimation);
        onAnimationPause();
    }

    /**
     * 恢复动画
     */
    protected void resume() {
        if (!mRunning)
            return;
        if (!mPaused)
            return;
        mPaused = false;
        final long time = AnimationUtils.currentAnimationTimeMillis();
        mTimeOffset += time - mPausedTime;
        onAnimationResume();
        scheduleSelf(mAnimation, time);
    }

    /**
     * 判断动画是否正在运行
     *
     * @return 动画是否正在运行
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * 判断动画是否已暂停
     *
     * @return 动画是否已暂停
     */
    protected boolean isPaused() {
        return mPaused;
    }

    private boolean isRepeat() {
        if (mRepeatMode == RESTART || mRepeatMode == REVERSE)
            return mRepeatCount == INFINITE || mRepeatCount > 0;
        return false;
    }

    /**
     * 运行动画
     */
    protected void runAnimate() {
        if (!mRunning)
            return;
        if (mPaused)
            return;
        final long time = AnimationUtils.currentAnimationTimeMillis();
        if (mStartTime == -1)
            mStartTime = time;
        if (isRepeat()) {
            final long dt = time - mStartTime - mTimeOffset;
            final float interpolation = (float) (dt % mDuration) / mDuration;
            final long completedCount = dt / mDuration;
            if (mRepeatCompletedCount != completedCount) {
                mRepeatCompletedCount = completedCount;
                if (mRepeatCount <= INFINITE) {
                    onAnimationRepeat();
                } else {
                    if (completedCount <= mRepeatCount)
                        onAnimationRepeat();
                    else {
                        mInterpolation = 1;
                        onAnimationUpdate();
                        mRunning = false;
                        onAnimationEnd();
                        return;
                    }
                }
            }
            mInterpolation = interpolation;
            onAnimationUpdate();
            scheduleSelf(mAnimation, time + mFrameDelay);
        } else {
            if (time >= mStartTime + mDuration) {
                mInterpolation = 1;
                onAnimationUpdate();
                mRunning = false;
                onAnimationEnd();
                return;
            }
            final long dt = time - mStartTime;
            mInterpolation = (float) (dt % mDuration) / mDuration;
            onAnimationUpdate();
            scheduleSelf(mAnimation, time + mFrameDelay);
        }
    }

    /**
     * 获取动画值
     *
     * @return 动画值
     */
    protected float getAnimatedValue() {
        float value = mInterpolation;
        if (mRepeatMode == REVERSE) {
            if (mRepeatCompletedCount % 2 == 1) {
                value = 1 - value;
            }
        }
        return mInterpolator == null ? value : mInterpolator.getInterpolation(value);
    }

    /**
     * 获取重复已完成次数
     *
     * @return 已完成次数
     */
    protected long getRepeatCompletedCount() {
        return mRepeatCompletedCount;
    }

    /**
     * 动画进行中
     */
    protected void onAnimationUpdate() {
        invalidateSelf();
    }

    /**
     * 动画开始
     */
    protected void onAnimationStart() {
    }

    /**
     * 动画结束
     */
    protected void onAnimationEnd() {
    }

    /**
     * 动画重复
     */
    protected void onAnimationRepeat() {
    }

    /**
     * 动画取消
     */
    protected void onAnimationCancel() {
    }

    /**
     * 动画暂停
     */
    protected void onAnimationPause() {
    }

    /**
     * 动画恢复
     */
    protected void onAnimationResume() {
    }

    private class Animation implements Runnable {

        @Override
        public void run() {
            runAnimate();
        }
    }
}
