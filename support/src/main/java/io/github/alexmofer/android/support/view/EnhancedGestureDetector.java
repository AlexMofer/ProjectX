package io.github.alexmofer.android.support.view;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 增加对 Up 与 Cancel 的回调
 * Created by Alex on 2025/11/13.
 */
public final class EnhancedGestureDetector extends GestureDetector {

    private final OnGestureListener mListener;
    private final FlingHolder mFlingHolder;

    private EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener,
                                    FlingHolder holder) {
        super(context, new InnerOnGestureListener(listener, holder));
        mListener = listener;
        mFlingHolder = holder;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        if (listener instanceof OnContextClickListener) {
            setContextClickListener((OnContextClickListener) listener);
        }
    }

    public EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener) {
        this(context, listener, new FlingHolder());
    }

    private EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener,
                                    @Nullable Handler handler, FlingHolder holder) {
        super(context, new InnerOnGestureListener(listener, holder), handler);
        mListener = listener;
        mFlingHolder = holder;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        if (listener instanceof OnContextClickListener) {
            setContextClickListener((OnContextClickListener) listener);
        }
    }

    public EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener,
                                   @Nullable Handler handler) {
        this(context, listener, handler, new FlingHolder());
    }

    private EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener,
                                    @Nullable Handler handler, boolean unused, FlingHolder holder) {
        super(context, new InnerOnGestureListener(listener, holder), handler, unused);
        mListener = listener;
        mFlingHolder = holder;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        if (listener instanceof OnContextClickListener) {
            setContextClickListener((OnContextClickListener) listener);
        }
    }

    public EnhancedGestureDetector(@Nullable Context context, @NonNull OnGestureListener listener,
                                   @Nullable Handler handler, boolean unused) {
        this(context, listener, handler, unused, new FlingHolder());
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        final int action = ev.getAction();
        final boolean consumed = super.onTouchEvent(ev);
        if (action == MotionEvent.ACTION_UP) {
            final boolean up = mListener.onUp(ev, mFlingHolder.fling);
            return consumed || up;
        }
        if (action == MotionEvent.ACTION_CANCEL) {
            final boolean cancel = mListener.onCancel(ev);
            return consumed || cancel;
        }
        return consumed;
    }

    public interface OnGestureListener extends GestureDetector.OnGestureListener {

        /**
         * 抬手事件
         *
         * @param e     UP 触摸事件
         * @param fling 是否触发 onFling
         * @return 是否消耗事件
         */
        boolean onUp(@NonNull MotionEvent e, boolean fling);

        /**
         * 取消事件
         *
         * @param e CANCEL 触摸事件
         * @return 是否消耗事件
         */
        boolean onCancel(@NonNull MotionEvent e);
    }

    public static class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener
            implements OnGestureListener {

        @Override
        public boolean onUp(@NonNull MotionEvent e, boolean fling) {
            return false;
        }

        @Override
        public boolean onCancel(@NonNull MotionEvent e) {
            return false;
        }
    }

    private static class FlingHolder {
        public boolean fling;
    }

    private static class InnerOnGestureListener implements GestureDetector.OnGestureListener {

        private final OnGestureListener mListener;
        private final FlingHolder mFlingHolder;

        public InnerOnGestureListener(OnGestureListener listener, FlingHolder holder) {
            mListener = listener;
            mFlingHolder = holder;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            mFlingHolder.fling = false;
            return mListener.onDown(e);
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2,
                               float velocityX, float velocityY) {
            final boolean fling = mListener.onFling(e1, e2, velocityX, velocityY);
            mFlingHolder.fling = fling;
            return fling;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            mListener.onLongPress(e);
        }

        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2,
                                float distanceX, float distanceY) {
            return mListener.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {
            mListener.onShowPress(e);
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            return mListener.onSingleTapUp(e);
        }
    }
}
