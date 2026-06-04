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
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import io.github.alexmofer.android.support.function.FunctionPFloat;
import io.github.alexmofer.android.support.function.FunctionRFloat;

/**
 * Checkable View 实现辅助
 * Created by Alex on 2026/6/4.
 */
public final class CheckableViewHelper {
    private final Callback mCallback;
    private final SpringAnimation mSpringAnimation;
    private boolean mChecked;
    private float mState;// 0~1

    public <T extends View & Checkable> CheckableViewHelper(@NonNull T view, boolean checked,
                                                            @NonNull Callback callback) {
        mCallback = callback;
        mSpringAnimation = new SpringAnimation(this,
                new Property(view.getClass().getName()));
        mSpringAnimation.setMinValue(0);
        mSpringAnimation.setMaxValue(1);
        mSpringAnimation.setSpring(new SpringForce()
                .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)// 无弹力
                .setStiffness(SpringForce.STIFFNESS_MEDIUM));
        mSpringAnimation.setMinimumVisibleChange(0.001f);
        mChecked = checked;
        mState = checked ? 1 : 0;
        mSpringAnimation.setStartValue(mState);
        mSpringAnimation.animateToFinalPosition(mState);
        callback.onAttached(this::getState, this::animateToFinalPosition);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked, boolean animate) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        final float state = checked ? 1 : 0;
        if (animate) {
            mSpringAnimation.animateToFinalPosition(state);
        } else {
            mSpringAnimation.animateToFinalPosition(state);
            try {
                mSpringAnimation.skipToEnd();
            } catch (Throwable r) {
                mState = state;
                mCallback.onStateChanged();
            }
        }
    }

    private float getState() {
        return mState;
    }

    private void setState(float state) {
        if (mState == state) {
            return;
        }
        mState = state;
        mCallback.onStateChanged();
    }

    private void animateToFinalPosition(float state) {
        if (state < 0 || state > 1) {
            return;
        }
        mSpringAnimation.animateToFinalPosition(state);
    }

    public interface Callback {

        void onStateChanged();

        void onAttached(@NonNull FunctionRFloat stateGetter,
                        @NonNull FunctionPFloat stateSetter);
    }

    private static class Property extends FloatPropertyCompat<CheckableViewHelper> {

        public Property(String name) {
            super(name + "_Property");
        }

        @Override
        public float getValue(CheckableViewHelper helper) {
            return helper.mState;
        }

        @Override
        public void setValue(CheckableViewHelper helper, float value) {
            helper.setState(value);
        }
    }
}
