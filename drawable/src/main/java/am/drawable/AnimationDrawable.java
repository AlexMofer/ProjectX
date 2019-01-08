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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;

/**
 * 动画图片
 * Created by Alex on 2019/1/8.
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
abstract class AnimationDrawable extends Drawable {

    private static final long FRAME_DELAY = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ValueAnimator.getFrameDelay() : 10;
    private static final int DEFAULT_DURATION = 250;
    private long mStartTime;
    private boolean mRunning = false;
    private long mDuration = DEFAULT_DURATION;
    private long mFrameDelay = FRAME_DELAY;
    private boolean mRepeat = false;
    private int mRepeatCount = 0;
    private final Animation mAnimation = new Animation();
    private float mInterpolation = 0;

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
     * 判断是否为重复动画
     *
     * @return 是否为重复动画
     */
    protected boolean isRepeat() {
        return mRepeat;
    }

    /**
     * 设置是否为重复动画
     *
     * @param repeat 是否为重复动画
     */
    protected void setRepeat(boolean repeat) {
        setRepeat(repeat, 0);
    }

    /**
     * 设置是否为重复动画
     *
     * @param repeat 是否为重复动画
     * @param count  重复次数（小于等于0时表示无限重复）
     */
    protected void setRepeat(boolean repeat, int count) {
        mRepeat = repeat;
        mRepeatCount = count;
    }

    /**
     * 开始动画
     */
    protected void start() {
        if (mRunning)
            return;
        mRunning = true;
        mStartTime = SystemClock.currentThreadTimeMillis();
        onAnimationStart();
        scheduleSelf(mAnimation, mStartTime + mFrameDelay);
    }

    /**
     * 结束动画
     */
    protected void stop() {
        if (!mRunning)
            return;
        mRunning = false;
        onAnimationStop();
        unscheduleSelf(mAnimation);
    }

    /**
     * 获取动画插值
     *
     * @return 插值
     */
    protected float getInterpolation() {
        return mInterpolation;
    }

    /**
     * 设置动画插值
     *
     * @param interpolation 插值
     */
    protected void setInterpolation(float interpolation) {
        mInterpolation = interpolation;
    }

    /**
     * 运行动画
     */
    protected void runAnimate() {
        if (!mRunning)
            return;
        final long time = SystemClock.currentThreadTimeMillis();
        if (!mRepeat) {
            if (time >= mStartTime + mDuration) {
                setInterpolation(1);
                onAnimate(1);
                stop();
                return;
            }
        }
        final long dt = time - mStartTime;
        final float interpolation = (float) (dt % mDuration) / mDuration;
        setInterpolation(interpolation);
        onAnimate(interpolation);
        scheduleSelf(mAnimation, time + mFrameDelay);
    }

    /**
     * 动画开始
     */
    protected void onAnimationStart() {
    }

    /**
     * 动画进行中
     *
     * @param interpolation 插值
     */
    protected void onAnimate(float interpolation) {
    }

    /**
     * 动画结束
     */
    protected void onAnimationStop() {
    }

    /**
     * 动画重复
     *
     * @param time 次数，从0开始
     */
    protected void onAnimationRepeat(int time) {
    }

    private class Animation implements Runnable {

        @Override
        public void run() {
            runAnimate();
        }
    }
}
