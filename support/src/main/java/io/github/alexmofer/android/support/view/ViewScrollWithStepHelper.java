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

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import io.github.alexmofer.android.support.other.FlingCalculator;

/**
 * 滚动辅助
 * Created by Alex on 2026/6/11.
 */
public final class ViewScrollWithStepHelper {
    private final PointF tPointF = new PointF();
    private final Callback mCallback;
    private final FloatValueHolder mValueHolderX = new ScrollXHolder();
    private final FloatValueHolder mValueHolderY = new ScrollYHolder();
    private final SpringAnimation mSpringX;
    private final SpringAnimation mSpringY;
    private final FlingCalculator mFlingCalculator;
    private final int mTouchSlop;
    private final int mMaximumVelocity;
    private final int mMinimumVelocity;
    private final boolean mClickable;

    private int mState = Callback.SCROLL_STATE_IDLE;
    private VelocityTracker mVelocityTracker;
    private float mDownX;
    private float mDownY;
    private boolean mInClickArea;
    private float mDownScrollX;
    private float mDownScrollY;
    private float mLastScrollX;
    private float mLastScrollY;

    private boolean mIsSpringXRunning = false;
    private boolean mIsSpringYRunning = false;

    public ViewScrollWithStepHelper(@NonNull Context context,
                                    float dampingRatio, boolean clickable,
                                    @NonNull Callback callback) {
        mCallback = callback;
        mClickable = clickable;

        // 初始化系统的标准硬件判定参数
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        mMinimumVelocity = vc.getScaledMinimumFlingVelocity();

        mSpringX = new SpringAnimation(mValueHolderX);
        mSpringX.setSpring(new SpringForce()
                .setDampingRatio(dampingRatio)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM));
        mSpringX.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS);

        mSpringY = new SpringAnimation(mValueHolderY);
        mSpringY.setSpring(new SpringForce()
                .setDampingRatio(dampingRatio)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM));
        mSpringY.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS);

        mSpringX.addEndListener((animation, canceled, value, velocity) -> {
            mIsSpringXRunning = false;
            checkSpringAnimationEnd();
        });
        mSpringY.addEndListener((animation, canceled, value, velocity) -> {
            mIsSpringYRunning = false;
            checkSpringAnimationEnd();
        });

        mFlingCalculator = new FlingCalculator(context);
    }

    public ViewScrollWithStepHelper(@NonNull Context context, @NonNull Callback callback) {
        this(context, SpringForce.DAMPING_RATIO_NO_BOUNCY,
                !(callback instanceof AllScrollableCallback), callback);
    }

    private void checkSpringAnimationEnd() {
        if (!mIsSpringXRunning && !mIsSpringYRunning) {
            if (mState == Callback.SCROLL_STATE_SETTLING) {
                mState = Callback.SCROLL_STATE_IDLE;
                notifyScrollStateChanged();
            }
        }
    }

    public void setScrollX(float min, float max) {
        mSpringX.setMinValue(min);
        mSpringX.setMaxValue(max);
    }

    public void setScrollY(float min, float max) {
        mSpringY.setMinValue(min);
        mSpringY.setMaxValue(max);
    }

    /**
     * 触摸事件
     */
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = ev.getX();
                mDownY = ev.getY();
                mInClickArea = true;
                onTouchDown(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float deltaX = x - mDownX;
                final float deltaY = y - mDownY;
                if (mInClickArea) {
                    if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
                        mInClickArea = false;
                    }
                }
                if (mState == Callback.SCROLL_STATE_DRAGGING) {
                    handleScroll(mDownScrollX - deltaX, mDownScrollY - deltaY);
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                onTouchCancel();
                recycleVelocityTracker();
                break;
            }

            case MotionEvent.ACTION_UP: {
                boolean isFlingStarted = false;
                if (!mInClickArea) {
                    final VelocityTracker vt = mVelocityTracker;
                    vt.computeCurrentVelocity(1000, mMaximumVelocity);
                    final float velocityX = vt.getXVelocity();
                    final float velocityY = vt.getYVelocity();

                    // 检查释放时的瞬时速度是否达到了系统的 Fling 门槛
                    if (Math.abs(velocityX) > mMinimumVelocity || Math.abs(velocityY) > mMinimumVelocity) {
                        isFlingStarted = fling(-velocityX, -velocityY);
                    }
                }

                // 如果没有成功触发 Fling 动画，分发到抬手对齐/点击处理逻辑
                if (!isFlingStarted) {
                    onTouchUp(ev);
                }
                recycleVelocityTracker();
                break;
            }


        }
        return true;
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 获取当前滚动状态
     *
     * @return 滚动状态
     */
    public int getScrollState() {
        return mState;
    }

    private void onTouchDown(@NonNull MotionEvent e) {
        final boolean consumed = mCallback.onTouchDown(e);
        if (consumed) {
            if (mState == Callback.SCROLL_STATE_SETTLING) {
                mSpringX.cancel();
                mSpringY.cancel();
                mIsSpringXRunning = false;
                mIsSpringYRunning = false;
                mState = Callback.SCROLL_STATE_IDLE;
                notifyScrollStateChanged();
            }
            mDownScrollX = 0;
            mDownScrollY = 0;
            if (mCallback instanceof HorizontalScrollableCallback) {
                mDownScrollX = ((HorizontalScrollableCallback) mCallback).getScrollX();
            }
            if (mCallback instanceof VerticalScrollableCallback) {
                mDownScrollY = ((VerticalScrollableCallback) mCallback).getScrollY();
            }
            if (mCallback instanceof AllScrollableCallback) {
                mDownScrollX = ((AllScrollableCallback) mCallback).getScrollX();
                mDownScrollY = ((AllScrollableCallback) mCallback).getScrollY();
            }
            mLastScrollX = mDownScrollX;
            mLastScrollY = mDownScrollY;
            mState = Callback.SCROLL_STATE_DRAGGING;
            notifyScrollStateChanged();
        }
    }

    private void onTouchCancel() {
        if (mState != Callback.SCROLL_STATE_IDLE) {
            if (mCallback instanceof HorizontalScrollableCallback) {
                ((HorizontalScrollableCallback) mCallback).onCancel(mDownScrollX);
            }
            if (mCallback instanceof VerticalScrollableCallback) {
                ((VerticalScrollableCallback) mCallback).onCancel(mDownScrollY);
            }
            if (mCallback instanceof AllScrollableCallback) {
                ((AllScrollableCallback) mCallback).onCancel(mDownScrollX, mDownScrollY);
            }
            mState = Callback.SCROLL_STATE_IDLE;
            notifyScrollStateChanged();
        }
    }

    private void onTouchUp(@NonNull MotionEvent ev) {
        boolean startSpringX = false;
        boolean startSpringY = false;
        if (mInClickArea && mClickable) {
            if (mCallback instanceof HorizontalScrollableCallback) {
                final HorizontalScrollableCallback callback = (HorizontalScrollableCallback) mCallback;
                final float targetX = callback.onClick(ev.getX(), ev.getY());
                final float startX = callback.getScrollX();
                if (!Float.isNaN(targetX) && targetX != startX) {
                    mValueHolderX.setValue(startX);
                    mSpringX.setStartVelocity(0);
                    mSpringX.getSpring().setFinalPosition(targetX);
                    startSpringX = true;
                }
            } else if (mCallback instanceof VerticalScrollableCallback) {
                final VerticalScrollableCallback callback = (VerticalScrollableCallback) mCallback;
                final float targetY = callback.onClick(ev.getX(), ev.getY());
                final float startY = callback.getScrollY();
                if (!Float.isNaN(targetY) && targetY != startY) {
                    mValueHolderY.setValue(startY);
                    mSpringY.setStartVelocity(0);
                    mSpringY.getSpring().setFinalPosition(targetY);
                    startSpringY = true;
                }
            } else if (mCallback instanceof AllScrollableCallback) {
                final AllScrollableCallback callback = (AllScrollableCallback) mCallback;
                final float x = ev.getX();
                final float y = ev.getY();
                final PointF point = tPointF;
                callback.onClick(x, y, point);
                final float targetX = point.x;
                final float targetY = point.y;
                final float startX = callback.getScrollX();
                final float startY = callback.getScrollY();
                if (!Float.isNaN(targetX) && targetX != startX) {
                    mValueHolderX.setValue(startX);
                    mSpringX.setStartVelocity(0);
                    mSpringX.getSpring().setFinalPosition(targetX);
                    startSpringX = true;
                }
                if (!Float.isNaN(targetY) && targetY != startY) {
                    mValueHolderY.setValue(startY);
                    mSpringY.setStartVelocity(0);
                    mSpringY.getSpring().setFinalPosition(targetY);
                    startSpringY = true;
                }
            }
        } else {
            if (mCallback instanceof HorizontalScrollableCallback) {
                final HorizontalScrollableCallback callback = (HorizontalScrollableCallback) mCallback;
                final float startX = callback.getScrollX();
                final float targetX = callback.onCalculateSnapTarget(startX);
                if (targetX != startX) {
                    mValueHolderX.setValue(startX);
                    mSpringX.setStartVelocity(0);
                    mSpringX.getSpring().setFinalPosition(targetX);
                    startSpringX = true;
                }
            } else if (mCallback instanceof VerticalScrollableCallback) {
                final VerticalScrollableCallback callback = (VerticalScrollableCallback) mCallback;
                final float startY = callback.getScrollY();
                final float targetY = callback.onCalculateSnapTarget(startY);
                if (targetY != startY) {
                    mValueHolderY.setValue(startY);
                    mSpringY.setStartVelocity(0);
                    mSpringY.getSpring().setFinalPosition(targetY);
                    startSpringY = true;
                }
            } else if (mCallback instanceof AllScrollableCallback) {
                final AllScrollableCallback callback = (AllScrollableCallback) mCallback;
                final float startX = callback.getScrollX();
                final float startY = callback.getScrollY();
                final PointF out = tPointF;
                callback.onCalculateSnapTarget(startX, startY, out);
                final float targetX = out.x;
                final float targetY = out.y;
                if (targetX != startX) {
                    mValueHolderX.setValue(startX);
                    mSpringX.setStartVelocity(0);
                    mSpringX.getSpring().setFinalPosition(targetX);
                    startSpringX = true;
                }
                if (targetY != startY) {
                    mValueHolderY.setValue(startY);
                    mSpringY.setStartVelocity(0);
                    mSpringY.getSpring().setFinalPosition(targetY);
                    startSpringY = true;
                }
            }
        }
        if (startSpringX || startSpringY) {
            mState = Callback.SCROLL_STATE_SETTLING;
            notifyScrollStateChanged();
            if (startSpringX) {
                mIsSpringXRunning = true;
                mSpringX.start();
            }
            if (startSpringY) {
                mIsSpringYRunning = true;
                mSpringY.start();
            }
        } else {
            mState = Callback.SCROLL_STATE_IDLE;
            notifyScrollStateChanged();
        }
    }

    private void notifyScrollStateChanged() {
        mCallback.onScrollStateChanged(mState);
    }

    private void handleScroll(float scrollX, float scrollY) {
        mLastScrollX = scrollX;
        mLastScrollY = scrollY;
        if (mCallback instanceof HorizontalScrollableCallback) {
            ((HorizontalScrollableCallback) mCallback).onHorizontalScrollTo(scrollX);
        } else if (mCallback instanceof VerticalScrollableCallback) {
            ((VerticalScrollableCallback) mCallback).onVerticalScrollTo(scrollY);
        } else if (mCallback instanceof AllScrollableCallback) {
            ((AllScrollableCallback) mCallback).onScrollTo(scrollX, scrollY);
        }
    }

    private void dispatchFlingX(float scrollX) {
        mLastScrollX = scrollX;
        if (mCallback instanceof HorizontalScrollableCallback) {
            ((HorizontalScrollableCallback) mCallback).onHorizontalScrollTo(scrollX);
            return;
        }
        if (mCallback instanceof AllScrollableCallback) {
            ((AllScrollableCallback) mCallback).onScrollTo(scrollX, mLastScrollY);
        }
    }

    private void dispatchFlingY(float scrollY) {
        mLastScrollY = scrollY;
        if (mCallback instanceof VerticalScrollableCallback) {
            ((VerticalScrollableCallback) mCallback).onVerticalScrollTo(scrollY);
            return;
        }
        if (mCallback instanceof AllScrollableCallback) {
            ((AllScrollableCallback) mCallback).onScrollTo(mLastScrollX, scrollY);
        }
    }

    private boolean fling(float velocityX, float velocityY) {
        boolean startSpringX = false;
        boolean startSpringY = false;
        if (mCallback instanceof HorizontalScrollableCallback) {
            final HorizontalScrollableCallback callback = (HorizontalScrollableCallback) mCallback;
            final float startX = callback.getScrollX();
            final float distanceX = (float) mFlingCalculator.getFlingDistance(velocityX) * Math.signum(velocityX);
            final float targetX = callback.onCalculateSnapTarget(startX + distanceX);
            mValueHolderX.setValue(startX);
            mSpringX.setStartVelocity(velocityX);
            mSpringX.getSpring().setFinalPosition(targetX);
            startSpringX = true;
        } else if (mCallback instanceof VerticalScrollableCallback) {
            final VerticalScrollableCallback callback = (VerticalScrollableCallback) mCallback;
            final float startY = callback.getScrollY();
            final float distanceY = (float) mFlingCalculator.getFlingDistance(velocityY) * Math.signum(velocityY);
            final float targetY = callback.onCalculateSnapTarget(startY + distanceY);
            mValueHolderY.setValue(startY);
            mSpringY.setStartVelocity(velocityY);
            mSpringY.getSpring().setFinalPosition(targetY);
            startSpringY = true;
        } else if (mCallback instanceof AllScrollableCallback) {
            final AllScrollableCallback callback = (AllScrollableCallback) mCallback;
            final float startX = callback.getScrollX();
            final float startY = callback.getScrollY();
            final float distanceX = (float) mFlingCalculator.getFlingDistance(velocityX) * Math.signum(velocityX);
            final float distanceY = (float) mFlingCalculator.getFlingDistance(velocityY) * Math.signum(velocityY);
            final PointF out = tPointF;
            callback.onCalculateSnapTarget(startX + distanceX, startY + distanceY, out);
            final float targetX = out.x;
            final float targetY = out.y;

            mValueHolderX.setValue(startX);
            mSpringX.setStartVelocity(velocityX);
            mSpringX.getSpring().setFinalPosition(targetX);

            mValueHolderY.setValue(startY);
            mSpringY.setStartVelocity(velocityY);
            mSpringY.getSpring().setFinalPosition(targetY);

            startSpringX = true;
            startSpringY = true;
        } else {
            return false;
        }
        mState = Callback.SCROLL_STATE_SETTLING;
        notifyScrollStateChanged();
        if (startSpringX) {
            mIsSpringXRunning = true;
            mSpringX.start();
        }
        if (startSpringY) {
            mIsSpringYRunning = true;
            mSpringY.start();
        }
        return true;
    }

    public interface Callback {
        int SCROLL_STATE_IDLE = 0;
        int SCROLL_STATE_DRAGGING = 1;
        int SCROLL_STATE_SETTLING = 2;

        boolean onTouchDown(@NonNull MotionEvent e);

        default void onScrollStateChanged(int state) {
        }
    }

    public interface HorizontalScrollableCallback extends Callback {
        void onHorizontalScrollTo(float scrollX);

        float getScrollX();

        float onCalculateSnapTarget(float predictScrollX);

        default float onClick(float x, float y) {
            return Float.NaN;
        }

        default void onCancel(float downScrollX) {
        }
    }

    public interface VerticalScrollableCallback extends Callback {
        void onVerticalScrollTo(float scrollY);

        float getScrollY();

        float onCalculateSnapTarget(float predictY);

        default float onClick(float x, float y) {
            return Float.NaN;
        }

        default void onCancel(float downScrollY) {
        }
    }

    public interface AllScrollableCallback extends Callback {
        void onScrollTo(float scrollX, float scrollY);

        float getScrollX();

        float getScrollY();

        void onCalculateSnapTarget(float predictX, float predictY, @NonNull PointF out);

        default void onClick(float x, float y, @NonNull PointF out) {
            out.set(Float.NaN, Float.NaN);
        }

        default void onCancel(float downScrollX, float downScrollY) {
        }
    }

    private class ScrollXHolder extends FloatValueHolder {
        @Override
        public void setValue(float value) {
            super.setValue(value);
            dispatchFlingX(value);
        }
    }

    private class ScrollYHolder extends FloatValueHolder {
        @Override
        public void setValue(float value) {
            super.setValue(value);
            dispatchFlingY(value);
        }
    }
}