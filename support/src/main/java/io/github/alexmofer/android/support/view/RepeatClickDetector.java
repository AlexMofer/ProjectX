package io.github.alexmofer.android.support.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 长按触发持续点击事件
 * 通过 setOnTouchListener 设置，回调到 setOnClickListener 设置的监听器中
 * Created by Alex on 2025/10/11.
 */
public final class RepeatClickDetector implements View.OnTouchListener {
    private final InnerOnGestureListener mListener;
    private final GestureDetector mDetector;

    public RepeatClickDetector(Context context, long firstDelayMillis, long decreasingUnit) {
        mListener = new InnerOnGestureListener(firstDelayMillis, decreasingUnit);
        mDetector = new GestureDetector(context, mListener);
        mDetector.setIsLongpressEnabled(true);
    }

    public RepeatClickDetector(Context context) {
        this(context, ViewConfiguration.getLongPressTimeout(), 25);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mListener.setView(v);
        }
        final boolean result = mDetector.onTouchEvent(event);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mListener.setView(null);
        }
        return result;
    }

    private static class InnerOnGestureListener extends GestureDetector.SimpleOnGestureListener
            implements Runnable {
        private final long mFirstDelayMillis;
        private final long mDecreasingUnit;
        private final long mMinDelayMillis;
        private boolean mLongPressed;
        private View mView;
        private long mDelayMillis;

        public InnerOnGestureListener(long firstDelayMillis, long decreasingUnit) {
            mFirstDelayMillis = firstDelayMillis;
            mDecreasingUnit = decreasingUnit;
            final long least = Math.min(mFirstDelayMillis, ViewConfiguration.getKeyRepeatDelay());
            long min = mFirstDelayMillis;
            while (min > least) {
                min -= mDecreasingUnit;
            }
            mMinDelayMillis = min;
        }

        public void setView(@Nullable View v) {
            mView = v;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            mLongPressed = false;
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            if (mLongPressed) {
                return true;
            }
            if (mView != null && mView.isEnabled()) {
                mView.performClick();
            }
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            mLongPressed = true;
            if (mView != null && mView.isEnabled()) {
                mView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mView.callOnClick();
                mDelayMillis = mFirstDelayMillis;
                mView.postDelayed(this, mDelayMillis);
            }
        }

        @Override
        public void run() {
            if (mView != null && mView.isEnabled()) {
                mView.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK);
                mView.callOnClick();
                mDelayMillis = Math.max(mDelayMillis - mDecreasingUnit, mMinDelayMillis);
                mView.postDelayed(this, mDelayMillis);
            }
        }
    }
}
