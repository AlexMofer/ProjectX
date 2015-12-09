package com.am.widget.stateframelayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 状态帧布局
 *
 * @author Alex
 */
public class StateFrameLayout extends FrameLayout {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    private static final int TOUCH_SLOP = 20;
    private Drawable mLoadingDrawable;
    private Drawable mErrorDrawable;
    private Drawable mEmptyDrawable;
    private int mState = STATE_NORMAL;
    private boolean alwaysDrawChild = false;
    private boolean alwaysTouchable = false;
    private OnStateClickListener mClickListener;
    private OnClickListener mErrorListener;
    private float downX;
    private float downY;
    private boolean allViewCanClick = false;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;

    public StateFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
        setClickable(true);
    }

    public StateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setClickable(true);
    }

    public StateFrameLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setClickable(true);
    }

    @TargetApi(21)
    public StateFrameLayout(Context context, AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        setClickable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        switch (mState) {
            default:
            case STATE_NORMAL:
                // do nothing
                break;
            case STATE_LOADING:
                drawLoading(canvas);
                break;
            case STATE_ERROR:
                drawError(canvas);
                break;
            case STATE_EMPTY:
                drawEmpty(canvas);
                break;
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (alwaysDrawChild) {
            return super.drawChild(canvas, child, drawingTime);
        }
        switch (mState) {
            default:
            case STATE_NORMAL:
                return child == mLoadingView || child == mErrorView || super.drawChild(canvas, child, drawingTime);
            case STATE_LOADING:
                return child != mLoadingView || super.drawChild(canvas, child, drawingTime);
            case STATE_ERROR:
                return child != mErrorView || super.drawChild(canvas, child, drawingTime);
            case STATE_EMPTY:
                return super.drawChild(canvas, child, drawingTime);
        }
    }

    private void drawLoading(Canvas canvas) {
        if (mLoadingDrawable != null) {
            canvas.save();
            canvas.translate(getWidth() * 0.5f, getHeight() * 0.5f);
            final int width = mLoadingDrawable.getIntrinsicWidth();
            final int height = mLoadingDrawable.getIntrinsicHeight();
            canvas.translate(-width * 0.5f, -height * 0.5f);
            mLoadingDrawable.setBounds(0, 0, width, height);
            mLoadingDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawError(Canvas canvas) {
        if (mErrorDrawable != null) {
            canvas.save();
            canvas.translate(getWidth() * 0.5f, getHeight() * 0.5f);
            final int width = mErrorDrawable.getIntrinsicWidth();
            final int height = mErrorDrawable.getIntrinsicHeight();
            canvas.translate(-width * 0.5f, -height * 0.5f);
            mErrorDrawable.setBounds(0, 0, width, height);
            mErrorDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawEmpty(Canvas canvas) {
        if (mEmptyDrawable != null) {
            canvas.save();
            canvas.translate(getWidth() * 0.5f, getHeight() * 0.5f);
            final int width = mEmptyDrawable.getIntrinsicWidth();
            final int height = mEmptyDrawable.getIntrinsicHeight();
            canvas.translate(-width * 0.5f, -height * 0.5f);
            mEmptyDrawable.setBounds(0, 0, width, height);
            mEmptyDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!alwaysTouchable) {
            switch (mState) {
                default:
                case STATE_NORMAL:
                    // do nothing
                    break;
                case STATE_LOADING:
                    return mLoadingView == null || !mLoadingView.dispatchTouchEvent(ev);
                case STATE_ERROR:
                    return mErrorView == null || !mErrorView.dispatchTouchEvent(ev);
                case STATE_EMPTY:
                    return mEmptyView != null && !mEmptyView.dispatchTouchEvent(ev);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (mState) {
                    default:
                    case STATE_NORMAL:
                        // do nothing
                        break;
                    case STATE_LOADING:
                        if (mLoadingDrawable != null) {
                            DrawableCompat.setHotspot(mLoadingDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                    case STATE_ERROR:
                        if (mErrorDrawable != null) {
                            DrawableCompat.setHotspot(mErrorDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                    case STATE_EMPTY:
                        if (mEmptyDrawable != null) {
                            DrawableCompat.setHotspot(mEmptyDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                }
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (event.getX() < downX + TOUCH_SLOP
                        && event.getX() > downX - TOUCH_SLOP
                        && event.getY() < downY + TOUCH_SLOP
                        && event.getY() > downY - TOUCH_SLOP) {
                    performClick(event.getX(), event.getY());
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void performClick(float x, float y) {
        final float centerX = getWidth() * 0.5f;
        final float centerY = getHeight() * 0.5f;
        switch (mState) {
            default:
            case STATE_NORMAL:
                // do nothing
                break;
            case STATE_LOADING:
                break;
            case STATE_ERROR:
                if (allViewCanClick && mClickListener != null) {
                    mClickListener.onError(this);
                    break;
                }
                if (mErrorDrawable != null && mClickListener != null) {
                    final float drawableCX = mErrorDrawable.getIntrinsicWidth() * 0.5f;
                    final float drawableCY = mErrorDrawable.getIntrinsicHeight() * 0.5f;
                    final float startX = centerX - drawableCX;
                    final float startY = centerY - drawableCY;
                    final float endX = centerX + drawableCX;
                    final float endY = centerY + drawableCY;
                    if (x > startX && x < endX && y > startY && y < endY) {
                        mClickListener.onError(this);
                    }
                }
                break;
        }
    }

    @Override
    protected void drawableStateChanged() {
        switch (mState) {
            default:
            case STATE_NORMAL:
                // do nothing
                break;
            case STATE_LOADING:
                if (mLoadingDrawable != null && mLoadingDrawable.isStateful()) {
                    mLoadingDrawable.setState(getDrawableState());
                }
                break;
            case STATE_ERROR:
                if (mErrorDrawable != null && mErrorDrawable.isStateful()) {
                    mErrorDrawable.setState(getDrawableState());
                }
                break;
            case STATE_EMPTY:
                if (mEmptyDrawable != null && mEmptyDrawable.isStateful()) {
                    mEmptyDrawable.setState(getDrawableState());
                }
                break;
        }
        super.drawableStateChanged();

    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean shouldVerify = false;
        switch (mState) {
            default:
            case STATE_NORMAL:
                // do nothing
                break;
            case STATE_LOADING:
                if (mLoadingDrawable != null && who == mLoadingDrawable) {
                    shouldVerify = true;
                }
                break;
            case STATE_ERROR:
                if (mErrorDrawable != null && who == mErrorDrawable) {
                    shouldVerify = true;
                }
                break;
            case STATE_EMPTY:
                if (mEmptyDrawable != null && who == mEmptyDrawable) {
                    shouldVerify = true;
                }
                break;
        }
        return shouldVerify || super.verifyDrawable(who);
    }

    /**
     * 设置自定义载入View
     *
     * @param loadingView  载入View
     * @param layoutParams 布局方式
     */
    public void setLoadingView(View loadingView, LayoutParams layoutParams) {
        if (mLoadingView == loadingView) {
            return;
        }
        if (loadingView == null) {
            removeView(mLoadingView);
            mLoadingView = null;
        } else {
            mLoadingView = loadingView;
            addView(mLoadingView, layoutParams);
            checkViewVisibility();
        }
    }

    /**
     * 设置自定义错误View
     *
     * @param errorView    错误View
     * @param layoutParams 布局方式
     */
    public void setErrorView(View errorView, LayoutParams layoutParams) {
        if (mErrorView == errorView) {
            return;
        }
        if (errorView == null) {
            removeView(mErrorView);
            mErrorView = null;
        } else {
            mErrorView = errorView;
            addView(mErrorView, layoutParams);
            mErrorView.setOnClickListener(getErrorListener());
            checkViewVisibility();
        }
    }

    /**
     * 设置自定义空白View
     *
     * @param emptyView    空白View
     * @param layoutParams 布局方式
     */
    public void setEmptyView(View emptyView, LayoutParams layoutParams) {
        if (mEmptyView == emptyView) {
            return;
        }
        if (emptyView == null) {
            removeView(mEmptyView);
            mEmptyView = null;
        } else {
            mEmptyView = emptyView;
            addView(mEmptyView, layoutParams);
            checkViewVisibility();
        }
    }

    private OnClickListener getErrorListener() {
        if (mErrorListener == null) {
            mErrorListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null)
                        mClickListener.onError(StateFrameLayout.this);
                }
            };
        }
        return mErrorListener;
    }

    private void checkViewVisibility() {
        switch (mState) {
            default:
            case STATE_NORMAL:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.VISIBLE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.VISIBLE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                break;
            case STATE_EMPTY:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 设置状态 Drawable
     *
     * @param loading 载入
     * @param error   错误
     * @param empty   空内容
     */
    public void setStateDrawables(Drawable loading, Drawable error, Drawable empty) {
        boolean shouldInvalidate = false;
        if (mLoadingDrawable != loading) {
            if (mLoadingDrawable != null) {
                if (mLoadingDrawable instanceof Animatable) {
                    ((Animatable) mLoadingDrawable).stop();
                }
                mLoadingDrawable.setCallback(null);
            }
            mLoadingDrawable = loading;
            mLoadingDrawable.setCallback(this);
            shouldInvalidate = true;
        }
        if (mErrorDrawable != error) {
            if (mErrorDrawable != null) {
                mErrorDrawable.setCallback(null);
            }
            mErrorDrawable = error;
            mErrorDrawable.setCallback(this);
            shouldInvalidate = true;
        }
        if (mEmptyDrawable != empty) {
            if (mEmptyDrawable != null) {
                mEmptyDrawable.setCallback(null);
            }
            mEmptyDrawable = empty;
            mEmptyDrawable.setCallback(this);
            shouldInvalidate = true;
        }
        if (shouldInvalidate) {
            invalidate();
        }
        animateDrawable();
    }

    /**
     * 设置状态 Drawable
     *
     * @param loading 载入
     * @param error   错误
     * @param empty   空内容
     */
    public void setStateDrawables(int loading, int error, int empty) {
        setStateDrawables(ContextCompat.getDrawable(getContext(), loading),
                ContextCompat.getDrawable(getContext(), error), ContextCompat.getDrawable(getContext(), empty));
    }

    private void animateDrawable() {
        switch (mState) {
            default:
            case STATE_NORMAL:
                if (mLoadingDrawable != null
                        && mLoadingDrawable instanceof Animatable) {
                    ((Animatable) mLoadingDrawable).stop();
                }
                if (mErrorDrawable != null
                        && mErrorDrawable instanceof Animatable) {
                    ((Animatable) mErrorDrawable).stop();
                }
                if (mEmptyDrawable != null
                        && mEmptyDrawable instanceof Animatable) {
                    ((Animatable) mEmptyDrawable).stop();
                }
                break;
            case STATE_ERROR:
                if (mLoadingDrawable != null
                        && mLoadingDrawable instanceof Animatable) {
                    ((Animatable) mLoadingDrawable).stop();
                }
                if (mErrorDrawable != null
                        && mErrorDrawable instanceof Animatable) {
                    ((Animatable) mErrorDrawable).start();
                }
                if (mEmptyDrawable != null
                        && mEmptyDrawable instanceof Animatable) {
                    ((Animatable) mEmptyDrawable).stop();
                }
                break;
            case STATE_LOADING:
                if (mLoadingDrawable != null
                        && mLoadingDrawable instanceof Animatable) {
                    ((Animatable) mLoadingDrawable).start();
                }
                if (mErrorDrawable != null
                        && mErrorDrawable instanceof Animatable) {
                    ((Animatable) mErrorDrawable).stop();
                }
                if (mEmptyDrawable != null
                        && mEmptyDrawable instanceof Animatable) {
                    ((Animatable) mEmptyDrawable).stop();
                }
                break;
        }
    }

    public void setState(int state) {
        if (mState != state) {
            mState = state;
            checkViewVisibility();
            invalidate();
            animateDrawable();
        }
    }

    public boolean isAlwaysDrawChild() {
        return alwaysDrawChild;
    }

    public void setAlwaysDrawChild(boolean draw) {
        alwaysDrawChild = draw;
        invalidate();
    }

    public boolean isAlwaysTouchable() {
        return alwaysTouchable;
    }

    public void setAlwaysTouchable(boolean touchable) {
        alwaysTouchable = touchable;
    }

    public void setOnStateClickListener(OnStateClickListener listener) {
        mClickListener = listener;
    }

    public boolean isAllViewCanClick() {
        return allViewCanClick;
    }

    public void setAllViewCanClick(boolean all) {
        allViewCanClick = all;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mState = mState;
        ss.alwaysDrawChild = alwaysDrawChild;
        ss.alwaysTouchable = alwaysTouchable;
        ss.allViewCanClick = allViewCanClick;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        mState = ss.mState;
        alwaysDrawChild = ss.alwaysDrawChild;
        alwaysTouchable = ss.alwaysTouchable;
        allViewCanClick = ss.allViewCanClick;
        invalidate();
        super.onRestoreInstanceState(ss.getSuperState());
    }

    static class SavedState extends BaseSavedState {
        private int mState = STATE_NORMAL;
        private boolean alwaysDrawChild = false;
        private boolean alwaysTouchable = false;
        private boolean allViewCanClick = false;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mState = in.readInt();
            alwaysDrawChild = in.readInt() == 1;
            alwaysTouchable = in.readInt() == 1;
            allViewCanClick = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mState);
            out.writeInt(alwaysDrawChild ? 1 : 0);
            out.writeInt(alwaysTouchable ? 1 : 0);
            out.writeInt(allViewCanClick ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * 状态点击监听
     *
     * @author Alex
     */
    public interface OnStateClickListener {
        void onError(StateFrameLayout layout);
    }
}
