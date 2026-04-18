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
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import java.util.Objects;

/**
 * 重新绘制转场
 * Created by Alex on 2026/4/18.
 */
public final class ChangeDrawTransform extends Transition {
    private static final String VALUE = "io.github.alexmofer.android.support:ChangeDrawTransform:value";
    private static final String[] sTransitionProperties = {
            VALUE
    };
    private static final Property<DrawChangeable, Object> ANIMATED_TRANSFORM =
            new Property<DrawChangeable, Object>(null, null) {
                @Override
                public void set(DrawChangeable view, Object value) {
                    view.setTransitionValue(value);
                }

                @Override
                public Object get(DrawChangeable view) {
                    return view.getTransitionValue();
                }
            };
    private static final Property<DrawChangeableFloat, Float> ANIMATED_TRANSFORM_FLOAT =
            new Property<DrawChangeableFloat, Float>(null, null) {
                @Override
                public void set(DrawChangeableFloat view, Float value) {
                    view.setTransitionValue(value);
                }

                @Override
                public Float get(@NonNull DrawChangeableFloat view) {
                    return view.getTransitionValue();
                }
            };
    private static final Property<DrawChangeableInt, Integer> ANIMATED_TRANSFORM_INT =
            new Property<DrawChangeableInt, Integer>(null, null) {
                @Override
                public void set(DrawChangeableInt view, Integer value) {
                    view.setTransitionValue(value);
                }

                @Override
                public Integer get(@NonNull DrawChangeableInt view) {
                    return view.getTransitionValue();
                }
            };
    private static final TypeEvaluator<Float> EVALUATOR_FLOAT =
            (fraction, startValue, endValue)
                    -> startValue + (endValue - startValue) * fraction;
    private static final TypeEvaluator<Integer> EVALUATOR_INT =
            (fraction, startValue, endValue)
                    -> Math.round(startValue + (endValue - startValue) * fraction);

    public ChangeDrawTransform() {
        addTarget(DrawChangeableFloat.class);
        addTarget(DrawChangeableInt.class);
        addTarget(DrawChangeable.class);
    }

    public ChangeDrawTransform(@NonNull Context context,
                               @NonNull AttributeSet attrs) {
        super(context, attrs);
        addTarget(DrawChangeableFloat.class);
        addTarget(DrawChangeableInt.class);
        addTarget(DrawChangeable.class);
    }

    @Override
    public boolean isSeekingSupported() {
        return true;
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (view instanceof DrawChangeableFloat) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeableFloat) view).getTransitionStartValue());
            return;
        }
        if (view instanceof DrawChangeableInt) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeableInt) view).getTransitionStartValue());
            return;
        }
        if (view instanceof DrawChangeable) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeable) view).getTransitionStartValue());
        }
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        if (view instanceof DrawChangeableFloat) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeableFloat) view).getTransitionEndValue());
            return;
        }
        if (view instanceof DrawChangeableInt) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeableInt) view).getTransitionEndValue());
            return;
        }
        if (view instanceof DrawChangeable) {
            transitionValues.values.put(VALUE,
                    ((DrawChangeable) view).getTransitionEndValue());
        }
    }

    @NonNull
    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot,
                                   @Nullable TransitionValues startValues,
                                   @Nullable TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final Object startValue = startValues.values.get(VALUE);
        final Object endValue = endValues.values.get(VALUE);
        if (Objects.equals(startValue, endValue)) {
            return null;
        }
        if (endValues.view instanceof DrawChangeableFloat) {
            return ObjectAnimator.ofObject((DrawChangeableFloat) endValues.view,
                    ANIMATED_TRANSFORM_FLOAT, EVALUATOR_FLOAT,
                    (Float) startValue, (Float) endValue);
        }
        if (endValues.view instanceof DrawChangeableInt) {
            return ObjectAnimator.ofObject((DrawChangeableInt) endValues.view,
                    ANIMATED_TRANSFORM_INT, EVALUATOR_INT,
                    (Integer) startValue, (Integer) endValue);
        }
        if (endValues.view instanceof DrawChangeable) {
            final DrawChangeable changeable = (DrawChangeable) endValues.view;
            final TypeEvaluator<Object> evaluator = changeable.getTypeEvaluator();
            if (evaluator == null) {
                return null;
            }
            return ObjectAnimator.ofObject(changeable, ANIMATED_TRANSFORM,
                    evaluator, startValue, endValue);
        }
        return null;
    }

    public interface DrawChangeable {

        @Nullable
        Object getTransitionStartValue();

        @Nullable
        Object getTransitionEndValue();

        @Nullable
        Object getTransitionValue();

        void setTransitionValue(@Nullable Object value);

        @Nullable
        TypeEvaluator<Object> getTypeEvaluator();
    }

    public interface DrawChangeableFloat {

        float getTransitionStartValue();

        float getTransitionEndValue();

        float getTransitionValue();

        void setTransitionValue(float value);
    }

    public interface DrawChangeableInt {

        int getTransitionStartValue();

        int getTransitionEndValue();

        int getTransitionValue();

        void setTransitionValue(int value);
    }
}
