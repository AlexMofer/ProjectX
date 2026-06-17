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
package io.github.alexmofer.android.support.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 动画工具类
 * Created by Alex on 2026/6/17.
 */
public final class AnimatorUtils {

    private AnimatorUtils() {
        //no instance
    }

    @Nullable
    public static Animator createBackgroundColorAppear(@NonNull View view) {
        // 从透明到当前背景色
        final Drawable background = view.getBackground();
        if (!(background instanceof ColorDrawable)) {
            return null;
        }
        final int color = ((ColorDrawable) background).getColor();
        final int startColor = ColorUtils.getColor(color, 0);
        return ObjectAnimator.ofObject(
                view,
                "backgroundColor",
                new ArgbEvaluator(),
                startColor,
                color);
    }

    @Nullable
    public static Animator createBackgroundColorDisappear(@NonNull View view) {
        // 从当前背景色到透明
        final Drawable background = view.getBackground();
        if (!(background instanceof ColorDrawable)) {
            return null;
        }
        final int color = ((ColorDrawable) background).getColor();
        final int endColor = ColorUtils.getColor(color, 0);
        return ObjectAnimator.ofObject(
                view,
                "backgroundColor",
                new ArgbEvaluator(),
                color,
                endColor);
    }
}
