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

import android.content.Context;
import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 开关动画
 * Created by Alex on 2018/10/23.
 */
final class SwitchAnimation extends Animation implements Animation.AnimationListener {

    private final AnimationListener mListener;
    private boolean mStart = false;

    SwitchAnimation(Context context, AnimationListener listener) {
        mListener = listener;
        setInterpolator(context, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                android.R.interpolator.fast_out_slow_in :
                android.R.interpolator.accelerate_decelerate);
        setAnimationListener(this);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (!mStart)
            return;
        mListener.onAnimationChange(this, interpolatedTime);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mStart = true;
        mListener.onAnimationStart(this);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mStart = false;
        mListener.onAnimationEnd(this);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public interface AnimationListener {
        void onAnimationStart(SwitchAnimation animation);

        void onAnimationChange(SwitchAnimation animation, float interpolatedTime);

        void onAnimationEnd(SwitchAnimation animation);
    }
}
