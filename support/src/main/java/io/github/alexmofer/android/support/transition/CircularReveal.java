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
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.TransitionValues;
import androidx.transition.Visibility;

import io.github.alexmofer.android.support.formulas.DistanceFormulas;

/**
 * 揭露动画转场
 * Created by Alex on 2026/4/24.
 */
public final class CircularReveal extends Visibility {

    private static final String PROPNAME_CIRCULAR_REVEAL_PARAMS = "io.github.alexmofer.android.support:CircularReveal:params";
    private static final Point POINT = new Point();

    public CircularReveal() {
    }

    public CircularReveal(@NonNull Context context,
                          @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public static boolean getCenter(@NonNull View view, @NonNull Point point) {
        if (view instanceof CenterProvider) {
            final CenterProvider provider = (CenterProvider) view;
            point.set(provider.getCircularRevealCenterX(),
                    provider.getCircularRevealCenterY());
            return true;
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        if (width > 0 && height > 0) {
            point.set(Math.round(width * 0.5f), Math.round(height * 0.5f));
            return true;
        }
        return false;
    }

    public static float getAppearStartRadius(@NonNull View view, int centerX, int centerY) {
        if (view instanceof AppearRadiusProvider) {
            return ((AppearRadiusProvider) view)
                    .getCircularRevealAppearStartRadius(centerX, centerY);
        }
        return 0;
    }

    public static float getAppearEndRadius(@NonNull View view, int centerX, int centerY) {
        if (view instanceof AppearRadiusProvider) {
            return ((AppearRadiusProvider) view)
                    .getCircularRevealAppearEndRadius(centerX, centerY);
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        final float dis1 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, 0, 0);
        final float dis2 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, width, 0);
        final float dis3 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, 0, height);
        final float dis4 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, width, height);
        return Math.max(Math.max(dis1, dis2), Math.max(dis3, dis4));
    }

    public static float getDisappearStartRadius(@NonNull View view, int centerX, int centerY) {
        if (view instanceof DisappearRadiusProvider) {
            return ((DisappearRadiusProvider) view)
                    .getCircularRevealDisappearStartRadius(centerX, centerY);
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        final float dis1 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, 0, 0);
        final float dis2 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, width, 0);
        final float dis3 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, 0, height);
        final float dis4 = (float) DistanceFormulas.calculatePointToPoint(centerX, centerY, width, height);
        return Math.max(Math.max(dis1, dis2), Math.max(dis3, dis4));
    }

    public static float getDisappearEndRadius(@NonNull View view, int centerX, int centerY) {
        if (view instanceof DisappearRadiusProvider) {
            return ((DisappearRadiusProvider) view)
                    .getCircularRevealDisappearEndRadius(centerX, centerY);
        }
        return 0;
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        final Point point = POINT;
        final View view = transitionValues.view;
        final  int visibility = view.getVisibility();
        if (visibility == View.VISIBLE) {
            // 消失
            if (getCenter(view, point)) {
                transitionValues.values.put(PROPNAME_CIRCULAR_REVEAL_PARAMS,
                        new CircularRevealParams(point.x, point.y,
                                getDisappearStartRadius(view, point.x, point.y),
                                getDisappearEndRadius(view, point.x, point.y)));
            }
        }
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        final Point point = POINT;
        final View view = transitionValues.view;
        final  int visibility = view.getVisibility();
        if (visibility == View.VISIBLE) {
            // 出现
            if (getCenter(view, point)) {
                transitionValues.values.put(PROPNAME_CIRCULAR_REVEAL_PARAMS,
                        new CircularRevealParams(point.x, point.y,
                                getAppearStartRadius(view, point.x, point.y),
                                getAppearEndRadius(view, point.x, point.y)));
            }
        }
    }

    @Nullable
    @Override
    public Animator onAppear(@NonNull ViewGroup sceneRoot,
                             @NonNull View view,
                             @Nullable TransitionValues startValues,
                             @Nullable TransitionValues endValues) {
        if (endValues != null) {
            final Object value = endValues.values.get(PROPNAME_CIRCULAR_REVEAL_PARAMS);
            if (value instanceof CircularRevealParams) {
                final CircularRevealParams params = (CircularRevealParams) value;
                return ViewAnimationUtils.createCircularReveal(view,
                        params.centerX, params.centerY, params.startRadius, params.endRadius);
            }
        }
        return super.onAppear(sceneRoot, view, startValues, endValues);
    }

    @Nullable
    @Override
    public Animator onDisappear(@NonNull ViewGroup sceneRoot,
                                @NonNull View view,
                                @Nullable TransitionValues startValues,
                                @Nullable TransitionValues endValues) {
        if (startValues != null) {
            final Object value = startValues.values.get(PROPNAME_CIRCULAR_REVEAL_PARAMS);
            if (value instanceof CircularRevealParams) {
                final CircularRevealParams params = (CircularRevealParams) value;
                return ViewAnimationUtils.createCircularReveal(view,
                        params.centerX, params.centerY, params.startRadius, params.endRadius);
            }
        }
        return super.onDisappear(sceneRoot, view, startValues, endValues);
    }

    private static class CircularRevealParams {
        public final int centerX;
        public final int centerY;
        public final float startRadius;
        public final float endRadius;

        public CircularRevealParams(int centerX, int centerY, float startRadius, float endRadius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.startRadius = startRadius;
            this.endRadius = endRadius;
        }
    }

    public interface CenterProvider {
        int getCircularRevealCenterX();

        int getCircularRevealCenterY();
    }

    public interface AppearRadiusProvider {
        float getCircularRevealAppearStartRadius(int centerX,  int centerY);

        float getCircularRevealAppearEndRadius(int centerX,  int centerY);
    }

    public interface DisappearRadiusProvider {
        float getCircularRevealDisappearStartRadius(int centerX,  int centerY);

        float getCircularRevealDisappearEndRadius(int centerX,  int centerY);
    }
}
