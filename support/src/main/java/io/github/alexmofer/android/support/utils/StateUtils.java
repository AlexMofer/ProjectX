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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import java.util.Objects;

/**
 * 状态辅助
 * Created by Alex on 2026/1/21.
 */
public final class StateUtils {
    public static final int[] STATE_NONE = new int[0];
    public static final int[] STATE_SELECTED = new int[]{android.R.attr.state_selected};
    public static final int[] STATE_ACTIVATED = new int[]{android.R.attr.state_activated};
    public static final int[] STATE_ENABLED = new int[]{android.R.attr.state_enabled};
    public static final int[][] STATES_SELECTED = new int[][]{STATE_SELECTED, STATE_NONE};
    public static final int[][] STATES_ACTIVATED = new int[][]{STATE_ACTIVATED, STATE_NONE};
    public static final int[][] STATES_ENABLED = new int[][]{STATE_ENABLED, STATE_NONE};

    private StateUtils() {
        //no instance
    }

    @NonNull
    public static StateListDrawable newDrawable(@NonNull int[][] states,
                                                @NonNull Drawable... drawables) {
        final StateListDrawable drawable = new StateListDrawable();
        final int count = drawables.length;
        for (int i = 0; i < count; i++) {
            drawable.addState(states[i], drawables[i]);
        }
        return drawable;
    }

    @NonNull
    public static StateListDrawable newDrawableSelected(@NonNull Drawable selected,
                                                        @NonNull Drawable fallback) {
        return newDrawable(STATES_SELECTED, selected, fallback);
    }

    @NonNull
    public static StateListDrawable newDrawableSelected(Context context,
                                                        @DrawableRes int selected,
                                                        @DrawableRes int fallback) {
        return newDrawableSelected(
                Objects.requireNonNull(AppCompatResources.getDrawable(context, selected)),
                Objects.requireNonNull(AppCompatResources.getDrawable(context, fallback)));
    }

    @NonNull
    public static StateListDrawable newDrawableActivated(@NonNull Drawable activated,
                                                         @NonNull Drawable fallback) {
        return newDrawable(STATES_ACTIVATED, activated, fallback);
    }

    @NonNull
    public static StateListDrawable newDrawableActivated(Context context,
                                                         @DrawableRes int activated,
                                                         @DrawableRes int fallback) {
        return newDrawableActivated(
                Objects.requireNonNull(AppCompatResources.getDrawable(context, activated)),
                Objects.requireNonNull(AppCompatResources.getDrawable(context, fallback)));
    }

    @NonNull
    public static StateListDrawable newDrawableEnabled(@NonNull Drawable enabled,
                                                       @NonNull Drawable fallback) {
        return newDrawable(STATES_ENABLED, enabled, fallback);
    }

    @NonNull
    public static StateListDrawable newDrawableEnabled(Context context,
                                                       @DrawableRes int enabled,
                                                       @DrawableRes int fallback) {
        return newDrawableEnabled(
                Objects.requireNonNull(AppCompatResources.getDrawable(context, enabled)),
                Objects.requireNonNull(AppCompatResources.getDrawable(context, fallback)));
    }

    @NonNull
    public static ColorStateList newColor(@NonNull int[][] states,
                                          @NonNull @ColorInt int... colors) {
        return new ColorStateList(states, colors);
    }

    @NonNull
    public static ColorStateList newColorSelected(@ColorInt int selected,
                                                  @ColorInt int fallback) {
        return newColor(STATES_SELECTED, selected, fallback);
    }

    @NonNull
    public static ColorStateList newColorSelected(Context context,
                                                  @ColorRes int selected,
                                                  @ColorRes int fallback) {
        return newColorSelected(ContextCompat.getColor(context, selected),
                ContextCompat.getColor(context, fallback));
    }

    @NonNull
    public static ColorStateList newColorActivated(@ColorInt int activated,
                                                   @ColorInt int fallback) {
        return newColor(STATES_ACTIVATED, activated, fallback);
    }

    @NonNull
    public static ColorStateList newColorActivated(Context context,
                                                   @ColorRes int activated,
                                                   @ColorRes int fallback) {
        return newColorActivated(ContextCompat.getColor(context, activated),
                ContextCompat.getColor(context, fallback));
    }

    @NonNull
    public static ColorStateList newColorEnabled(@ColorInt int enabled,
                                                 @ColorInt int fallback) {
        return newColor(STATES_ENABLED, enabled, fallback);
    }

    @NonNull
    public static ColorStateList newColorEnabled(Context context,
                                                 @ColorRes int enabled,
                                                 @ColorRes int fallback) {
        return newColorEnabled(ContextCompat.getColor(context, enabled),
                ContextCompat.getColor(context, fallback));
    }

    @NonNull
    public static Drawable newColorDrawableSelected(@ColorInt int selected,
                                                    @ColorInt int fallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ColorStateListDrawable(newColorSelected(selected, fallback));
        }
        return newDrawableSelected(new ColorDrawable(selected), new ColorDrawable(fallback));
    }

    @NonNull
    public static Drawable newColorDrawableActivated(@ColorInt int activated,
                                                     @ColorInt int fallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ColorStateListDrawable(newColorActivated(activated, fallback));
        }
        return newDrawableActivated(new ColorDrawable(activated), new ColorDrawable(fallback));
    }

    @NonNull
    public static Drawable newColorDrawableEnabled(@ColorInt int enabled,
                                                   @ColorInt int fallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ColorStateListDrawable(newColorEnabled(enabled, fallback));
        }
        return newDrawableEnabled(new ColorDrawable(enabled), new ColorDrawable(fallback));
    }
}
