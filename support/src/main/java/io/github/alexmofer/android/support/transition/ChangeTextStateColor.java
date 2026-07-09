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
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import java.util.Objects;

/**
 * 文字颜色由状态切换转场
 * Created by Alex on 2026/7/9.
 */
public final class ChangeTextStateColor extends Transition {
    private static final String PROPNAME_COLOR = "android:ChangeTextSelectableColor:color";
    private static final String[] sTransitionProperties = {PROPNAME_COLOR};
    private static final ColorEvaluator sColorEvaluator = new ColorEvaluator();

    @Override
    public boolean isSeekingSupported() {
        return true;
    }

    @NonNull
    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues values) {
        final View view = values.view;
        if (!(view instanceof TextView)) {
            return;
        }
        final TextView text = (TextView) view;
        final ColorStateList colors = text.getTextColors();
        if (colors == null) {
            return;
        }
        final int color = colors.getColorForState(text.getDrawableState(), colors.getDefaultColor());
        values.values.put(PROPNAME_COLOR, color);
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot,
                                   @Nullable TransitionValues startValues,
                                   @Nullable TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final int startColor = (int) Objects.requireNonNull(startValues.values.get(PROPNAME_COLOR));
        final int endColor = (int) Objects.requireNonNull(endValues.values.get(PROPNAME_COLOR));
        final View view = endValues.view;
        if (!(view instanceof TextView)) {
            return null;
        }
        final TextView text = (TextView) view;
        final ColorStateList originalColors = text.getTextColors();
        final Animator animator = ObjectAnimator.ofObject(text, "textColor", sColorEvaluator,
                startColor, endColor);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                text.setTextColor(originalColors);
            }
        });
        return animator;
    }
}
