/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.scalerecyclerview;

import android.support.v4.view.ViewCompat;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * 缩放辅助器
 * Created by Alex on 2017/11/8.
 */
class ScaleHelper implements Runnable {
    private final ScaleRecyclerView mView;
    private final AutoScale mAuto = new AutoScale();
    private Interpolator mInterpolator = ScaleRecyclerView.getScrollerInterpolator();
    private boolean mEatRunOnAnimationRequest = false;
    private boolean mReSchedulePostAnimationCallback = false;
    private float mFocusX;
    private float mFocusY;

    ScaleHelper(ScaleRecyclerView view) {
        mView = view;
    }

    @Override
    public void run() {
        if (mView.getLayoutManager() == null) {
            stop();
            return;
        }
        disableRunOnAnimationRequests();
        final AutoScale auto = mAuto;
        boolean first = auto.isFirst();
        if (auto.computeScale()) {
            final float scale = auto.getScale();
            if (first) {
                mView.onDoubleTapScaleBegin(scale, mFocusX, mFocusY);
            }
            if (auto.isFinished()) {
                mView.onDoubleTapScaleEnd(scale, mFocusX, mFocusY);
            } else {
                mView.onDoubleTapScale(scale, mFocusX, mFocusY);
                postOnAnimation();
            }
        }
        enableRunOnAnimationRequests();
    }

    private void disableRunOnAnimationRequests() {
        mReSchedulePostAnimationCallback = false;
        mEatRunOnAnimationRequest = true;
    }

    private void enableRunOnAnimationRequests() {
        mEatRunOnAnimationRequest = false;
        if (mReSchedulePostAnimationCallback) {
            postOnAnimation();
        }
    }

    private void postOnAnimation() {
        if (mEatRunOnAnimationRequest) {
            mReSchedulePostAnimationCallback = true;
        } else {
            mView.removeCallbacks(this);
            ViewCompat.postOnAnimation(mView, this);
        }
    }

    void stop() {
        mView.removeCallbacks(this);
        mAuto.abortAnimation();
    }

    void scale(float startScale, float targetScale, float focusX, float focusY) {
        mFocusX = focusX;
        mFocusY = focusY;
        mAuto.scale(startScale, targetScale, AutoScale.DEFAULT_DURATION);
        postOnAnimation();
    }

    private class AutoScale {
        private static final int DEFAULT_DURATION = 250;
        private boolean mFinished;
        private long mStartTime;
        private long mDuration;
        private boolean mFirst;
        private float mTargetScale;
        private float mStartScale;
        private float mScale;

        @SuppressWarnings("all")
        void scale(float startScale, float targetScale, long duration) {
            mFinished = false;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mDuration = duration;
            mScale = mStartScale = startScale;
            mTargetScale = targetScale;
        }

        boolean isFinished() {
            return mFinished;
        }

        boolean isFirst() {
            return mFirst;
        }

        void abortAnimation() {
            mScale = mTargetScale;
            mFinished = true;
            mFirst = true;
        }

        boolean computeScale() {
            if (isFinished()) {
                return false;
            }
            long time = AnimationUtils.currentAnimationTimeMillis();
            final long elapsedTime = time - mStartTime;

            final long duration = mDuration;
            if (elapsedTime < duration) {
                final float q = mInterpolator.getInterpolation(elapsedTime / (float) duration);
                mScale = mStartScale + (mTargetScale - mStartScale) * q;
            } else {
                mScale = mTargetScale;
                abortAnimation();
            }
            if (mFirst) {
                mFirst = false;
            }
            return true;
        }

        float getScale() {
            return mScale;
        }
    }
}
