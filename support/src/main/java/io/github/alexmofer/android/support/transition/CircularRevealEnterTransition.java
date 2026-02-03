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
package io.github.alexmofer.android.support.transition;

import android.animation.Animator;
import android.graphics.Point;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import io.github.alexmofer.android.support.function.FunctionPFloat;

/**
 * 揭露动画进入转场
 * <p>只有入场动画，揭露动画需要保证布局在移除之前进行动画，所以没有出场动画。
 * Created by Alex on 2026/1/16.
 */
public class CircularRevealEnterTransition extends Transition {
    private static final String UNUSED =
            "io.github.alexmofer.android.support:CircularRevealEnterTransition:unused";
    private static final Point POINT = new Point();
    private final FunctionPFloat mStartRadiusSetter = this::setStartRadius;
    private View mValueHolder;
    private float mStartRadius;

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        // 只设置一组变化值，保证 createAnimator 只调用一次
        if (mValueHolder == null) {
            mValueHolder = transitionValues.view;
            transitionValues.values.put(UNUSED, 0);
        }
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        if (transitionValues.view == mValueHolder) {
            transitionValues.values.put(UNUSED, 1);
        }
    }

    @Override
    public boolean isSeekingSupported() {
        return true;
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot,
                                   @Nullable TransitionValues startValues,
                                   @Nullable TransitionValues endValues) {
        final Point point = POINT;
        if (getCircularRevealParams(sceneRoot, point, mStartRadiusSetter)) {
            return ViewAnimationUtils.createCircularReveal(
                    sceneRoot, point.x, point.y, mStartRadius,
                    (float) Math.hypot(sceneRoot.getWidth(), sceneRoot.getHeight()));
        }
        return null;
    }

    /**
     * 获取揭露动画参数
     *
     * @param sceneRoot   转场根布局
     * @param center      中心点
     * @param startRadius 开始半径
     * @return 参数获取成功时返回 true
     */
    protected boolean getCircularRevealParams(@NonNull ViewGroup sceneRoot,
                                              @NonNull Point center,
                                              @NonNull FunctionPFloat startRadius) {
        center.set(0, 0);
        startRadius.execute(0);
        return true;
    }

    private void setStartRadius(float startRadius) {
        mStartRadius = startRadius;
    }
}
