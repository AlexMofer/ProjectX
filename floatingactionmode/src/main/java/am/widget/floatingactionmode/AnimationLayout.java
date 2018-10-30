/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.floatingactionmode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;


/**
 * 动画布局
 * Created by Alex on 2018/10/23.
 */
@SuppressLint("ViewConstructor")
final class AnimationLayout extends ViewGroup {

    private final View mContent;
    private final SwitchAnimation mAnimation;
    private final int mMargin;
    private final Point mAnimationSize = new Point();
    private final Point mSize = new Point();
    private boolean mReverse = false;
    private float mValue = 0;

    AnimationLayout(Context context, int margin, float elevation, boolean light,
                    SwitchAnimation.AnimationListener listener) {
        super(context);
        mContent = new View(context);
        mAnimation = new SwitchAnimation(context, listener);
        mMargin = margin;

        DrawableUtils.setRootBackground(mContent, light);
        if (Build.VERSION.SDK_INT >= 21)
            mContent.setElevation(elevation);
        addView(mContent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int margin = mMargin * 2;
        final int formX = mAnimationSize.x - margin;
        final int formY = mAnimationSize.y - margin;
        final int toX = mSize.x - margin;
        final int toY = mSize.y - margin;

        final int width = formX + (int) ((toX - formX) * mValue);
        final int height = formY + (int) ((toY - formY) * mValue);
        mContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();
        if (mReverse) {
            final int height = getHeight();
            mContent.layout(width - mMargin - mContent.getMeasuredWidth(),
                    height - mMargin - mContent.getMeasuredHeight(),
                    width - mMargin, height - mMargin);
        } else
            mContent.layout(width - mMargin - mContent.getMeasuredWidth(), mMargin,
                    width - mMargin, mMargin + mContent.getMeasuredHeight());
    }

    void setLightTheme(boolean light) {
        DrawableUtils.changeRootBackground(mContent, light);
    }

    void setReverse(boolean reverse) {
        mReverse = reverse;
        requestLayout();
        invalidate();
    }

    void setSize(Point size, boolean animate, long duration) {
        if (animate) {
            mAnimationSize.set(mSize.x, mSize.y);
            mSize.set(size.x, size.y);
            mValue = 0;
            mAnimation.setDuration(duration);
            mContent.startAnimation(mAnimation);
        } else {
            mAnimationSize.set(size.x, size.y);
            mSize.set(size.x, size.y);
            mValue = 0;
            requestLayout();
            invalidate();
        }
    }

    void setAnimationValue(float value) {
        mValue = value;
        requestLayout();
        invalidate();
    }

    void cancel() {
        mAnimation.cancel();
    }
}
