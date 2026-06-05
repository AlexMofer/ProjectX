/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import io.github.alexmofer.android.support.function.FunctionPFloat;
import io.github.alexmofer.android.support.function.FunctionRFloat;

/**
 * ProgressBar 实现辅助
 * Created by Alex on 2026/6/5.
 */
public final class ProgressBarHelper {
    private final View mView;
    private final Callback mCallback;
    private final SpringAnimation mSpringAnimation;
    private final float mMax;
    private float mDrawProgress;
    private float mProgress;
    private long mAnimateDelayMillisWhenAttached;

    public ProgressBarHelper(@NonNull View view, float progress,
                             float min, float max, float minimumVisibleChange,
                             float dampingRatio, float stiffness,
                             @NonNull Callback callback) {
        mView = view;
        mCallback = callback;
        mDrawProgress = progress;
        mProgress = progress;
        mMax = max;
        mSpringAnimation = new SpringAnimation(this, new Property(view.getClass().getName()));
        mSpringAnimation.setMinValue(min);
        mSpringAnimation.setMaxValue(max);
        mSpringAnimation.setSpring(new SpringForce()
                .setDampingRatio(dampingRatio)
                .setStiffness(stiffness));
        mSpringAnimation.setMinimumVisibleChange(minimumVisibleChange);
        mSpringAnimation.setStartValue(mDrawProgress);
        mSpringAnimation.animateToFinalPosition(mDrawProgress);
        callback.onAttached(this::getDrawProgress, this::animateToFinalPosition);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                if (mAnimateDelayMillisWhenAttached > 0) {
                    final long delayMillis = mAnimateDelayMillisWhenAttached;
                    mAnimateDelayMillisWhenAttached = 0;
                    v.postDelayed(() -> {
                        final float progress = mProgress * max;
                        if (mDrawProgress != progress) {
                            mSpringAnimation.animateToFinalPosition(progress);
                        }
                    }, delayMillis);
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                // do nothing
            }
        });
    }

    public ProgressBarHelper(@NonNull View view, float progress,
                             @NonNull Callback callback) {
        this(view, progress, 0, 10000, 1f,
                SpringForce.DAMPING_RATIO_LOW_BOUNCY, SpringForce.STIFFNESS_VERY_LOW, callback);
    }

    private float getDrawProgress() {
        return mDrawProgress / mMax;
    }

    private void setDrawProgress(float progress) {
        if (mDrawProgress == progress) {
            return;
        }
        mDrawProgress = progress;
        mCallback.onDrawProgressChanged();
    }

    private void animateToFinalPosition(float progress) {
        if (progress < 0 || progress > 1) {
            return;
        }
        mSpringAnimation.animateToFinalPosition(progress);
    }

    /**
     * 获取进度
     *
     * @return 进度 0~1
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * 设置进度
     *
     * @param progress    进度 0~1，越界为临界值
     * @param animate     是否动画
     * @param delayMillis 动画延迟时间
     */
    public void setProgress(float progress, boolean animate, long delayMillis) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        if (mProgress == progress) {
            return;
        }
        mProgress = progress;
        final float p = mProgress * mMax;
        if (animate) {
            if (delayMillis <= 0) {
                mSpringAnimation.animateToFinalPosition(p);
            } else {
                if (mView.isAttachedToWindow()) {
                    mView.postDelayed(() -> {
                        if (mDrawProgress != p) {
                            mSpringAnimation.animateToFinalPosition(p);
                        }
                    }, delayMillis);
                } else {
                    mAnimateDelayMillisWhenAttached = delayMillis;
                }
            }
        } else {
            mSpringAnimation.animateToFinalPosition(p);
            try {
                mSpringAnimation.skipToEnd();
            } catch (Throwable r) {
                mDrawProgress = p;
                mCallback.onDrawProgressChanged();
            }
        }
    }

    /**
     * 设置进度
     *
     * @param owner       LifecycleOwner
     * @param progress    0~1，越界为临界值
     * @param animate     是否动画
     * @param delayMillis 动画延迟时间
     */
    public void setProgress(@NonNull LifecycleOwner owner,
                            @NonNull LiveData<Float> progress,
                            boolean animate, long delayMillis) {
        progress.observe(owner, p -> {
            if (p != null) {
                setProgress(p, animate, delayMillis);
            }
        });
    }

    public interface Callback {

        void onDrawProgressChanged();

        void onAttached(@NonNull FunctionRFloat stateGetter,
                        @NonNull FunctionPFloat stateSetter);
    }

    private static class Property extends FloatPropertyCompat<ProgressBarHelper> {

        public Property(String name) {
            super(name + "_Property");
        }

        @Override
        public float getValue(ProgressBarHelper helper) {
            return helper.mDrawProgress;
        }

        @Override
        public void setValue(ProgressBarHelper helper, float value) {
            helper.setDrawProgress(value);
        }
    }
}
