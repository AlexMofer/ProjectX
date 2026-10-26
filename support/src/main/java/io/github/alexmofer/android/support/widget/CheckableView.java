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
package io.github.alexmofer.android.support.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Checkable;

import androidx.annotation.EmptySuper;
import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import io.github.alexmofer.android.support.view.RedrawHelper;
import io.github.alexmofer.android.support.widget.builders.CheckableViewBuilder;

/**
 * 带 Checkable 状态的 View
 * Created by Alex on 2026/10/26.
 */
public abstract class CheckableView extends View implements Checkable {
    private final FloatValueHolder mStateHolder = new FloatValueHolder();
    private final SpringAnimation mStateSpring;
    private final RedrawHelper mRedrawHelper;
    private final int mTouchSlop;
    private boolean mChecked;
    private boolean mSelectedAsChecked;
    private boolean mDragEnabled;
    private float mDownX;
    private float mDownY;
    private boolean mInClickArea;

    public CheckableView(@NonNull Context context) {
        super(context);
        mStateSpring = new SpringAnimation(mStateHolder);
        mStateSpring.setMinValue(0);
        mStateSpring.setMaxValue(1);
        mStateSpring.setSpring(new SpringForce()
                .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)// 无弹力
                .setStiffness(SpringForce.STIFFNESS_MEDIUM));
        mStateSpring.setMinimumVisibleChange(0.001f);
        mRedrawHelper = new RedrawHelper(this, mStateSpring::isRunning);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        if (mSelectedAsChecked) {
            setChecked(selected);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawState(canvas, mStateHolder.getValue());
    }

    protected abstract void drawState(@NonNull Canvas canvas, float state);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mDragEnabled || !isEnabled()) {
            return super.onTouchEvent(event);
        }
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (mStateSpring.isRunning()) {
                mStateSpring.cancel();
            }
            mDownX = event.getX();
            mDownY = event.getY();
            mInClickArea = true;
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            final float x = event.getX();
            final float y = event.getY();
            final float deltaX = x - mDownX;
            final float deltaY = y - mDownY;
            if (mInClickArea) {
                if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
                    mInClickArea = false;
                }
            }
            if (!mInClickArea) {
                onDragMove(x, y);
            }
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            if (mInClickArea) {
                return performClick();
            } else {
                onDragEnd(false);
            }
            mDownX = Float.NaN;
            mDownY = Float.NaN;
            return true;
        }
        if (action == MotionEvent.ACTION_CANCEL) {
            mDownX = Float.NaN;
            mDownY = Float.NaN;
            if (!mInClickArea) {
                onDragEnd(true);
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        mStateSpring.animateToFinalPosition(checked ? 1 : 0);
        mRedrawHelper.redraw();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public final void setSelectedAsChecked(boolean selectedAsChecked) {
        mSelectedAsChecked = selectedAsChecked;
        if (mSelectedAsChecked) {
            setChecked(isSelected());
        }
    }

    protected final void setDragEnabled(boolean dragEnabled) {
        mDragEnabled = dragEnabled;
    }

    private void onDragMove(float x, float y) {
        final float state = toState(x, y);
        if (mStateHolder.getValue() != state) {
            mStateHolder.setValue(state);
            mRedrawHelper.redraw();
        }
    }

    protected float toState(float x, float y) {
        return mStateHolder.getValue();
    }

    private void onDragEnd(boolean cancel) {
        if (!cancel) {
            final boolean checked = toChecked(mStateHolder.getValue());
            if (checked != mChecked) {
                onCheckChangedByDrag(checked);
            }
        }
        final float state = isChecked() ? 1 : 0;
        if (mStateHolder.getValue() != state) {
            mStateSpring.animateToFinalPosition(state);
            mRedrawHelper.redraw();
        }
    }

    protected boolean toChecked(float state) {
        return state > 0.5f;
    }

    @EmptySuper
    protected void onCheckChangedByDrag(boolean checked) {
    }

    public static class Builder extends CheckableViewBuilder<CheckableView> {
        private final CheckableView mView;

        public Builder(@NonNull CheckableView view) {
            super(view);
            mView = view;
        }

        @NonNull
        @Override
        public CheckableView build() {
            return mView;
        }

        @NonNull
        public Builder setSelectedAsChecked(boolean selectedAsChecked) {
            mView.setSelectedAsChecked(selectedAsChecked);
            return this;
        }
    }
}
