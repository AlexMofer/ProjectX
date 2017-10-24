/*
 * Copyright (C) 2015 AlexMofer
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

package am.widget.stateframelayout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 状态帧布局
 *
 * @author Alex
 */
@SuppressWarnings("all")
public class StateFrameLayout extends FrameLayout {

    public static final int STATE_NORMAL = 0;// 普通
    public static final int STATE_LOADING = 1;// 载入
    public static final int STATE_ERROR = 2;// 错误
    public static final int STATE_EMPTY = 3;// 空白
    private Drawable mLoadingDrawable;
    private Drawable mErrorDrawable;
    private Drawable mEmptyDrawable;
    private int mState;
    private boolean mAlwaysDrawChild;
    private OnStateClickListener mClickListener;
    private int mLoadingLayoutId;
    private int mErrorLayoutId;
    private int mEmptyLayoutId;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;

    public StateFrameLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public StateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public StateFrameLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public StateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        setClickable(true);
        Drawable loading, error, empty;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.StateFrameLayout);
        boolean alwaysDrawChild = custom.getBoolean(
                R.styleable.StateFrameLayout_sflAlwaysDrawChild, false);
        loading = custom.getDrawable(R.styleable.StateFrameLayout_sflLoadingDrawable);
        error = custom.getDrawable(R.styleable.StateFrameLayout_sflErrorDrawable);
        empty = custom.getDrawable(R.styleable.StateFrameLayout_sflEmptyDrawable);
        int state = custom.getInt(R.styleable.StateFrameLayout_sflState, STATE_NORMAL);
        mLoadingLayoutId = custom.getResourceId(R.styleable.StateFrameLayout_sflLoadingLayout,
                NO_ID);
        mErrorLayoutId = custom.getResourceId(R.styleable.StateFrameLayout_sflErrorLayout, NO_ID);
        mEmptyLayoutId = custom.getResourceId(R.styleable.StateFrameLayout_sflEmptyLayout, NO_ID);
        custom.recycle();
        setAlwaysDrawChild(alwaysDrawChild);
        setStateDrawables(loading, error, empty);
        setState(state);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT},
     * and a height of {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (Build.VERSION.SDK_INT >= 19) {
            if (lp instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) lp);
            } else if (lp instanceof FrameLayout.LayoutParams) {
                return new LayoutParams((FrameLayout.LayoutParams) lp);
            }
        }
        if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mLoadingLayoutId != NO_ID) {
            View vLoading = LayoutInflater.from(getContext()).inflate(
                    mLoadingLayoutId, this, false);
            setLoadingView(vLoading);
        }
        if (mErrorLayoutId != NO_ID) {
            View vError = LayoutInflater.from(getContext()).inflate(
                    mErrorLayoutId, this, false);
            setErrorView(vError);
        }
        if (mEmptyLayoutId != NO_ID) {
            View vEmpty = LayoutInflater.from(getContext()).inflate(
                    mEmptyLayoutId, this, false);
            setEmptyView(vEmpty);
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            switch (lp.getState()) {
                default:
                case STATE_NORMAL:
                    break;
                case STATE_LOADING:
                    mLoadingView = child;
                    break;
                case STATE_EMPTY:
                    mEmptyView = child;
                    break;
                case STATE_ERROR:
                    mErrorView = child;
                    break;
            }
        }
        checkViewVisibility();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        boolean reset = false;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (mLoadingDrawable != null && mLoadingDrawable.getIntrinsicWidth() > width) {
            width = mLoadingDrawable.getIntrinsicWidth();
            reset = true;
        }
        if (mLoadingDrawable != null && mLoadingDrawable.getIntrinsicHeight() > height) {
            height = mLoadingDrawable.getIntrinsicHeight();
            reset = true;
        }
        if (mErrorDrawable != null && mErrorDrawable.getIntrinsicWidth() > width) {
            width = mErrorDrawable.getIntrinsicWidth();
            reset = true;
        }
        if (mErrorDrawable != null && mErrorDrawable.getIntrinsicHeight() > height) {
            height = mErrorDrawable.getIntrinsicHeight();
            reset = true;
        }
        if (mEmptyDrawable != null && mEmptyDrawable.getIntrinsicWidth() > width) {
            width = mEmptyDrawable.getIntrinsicWidth();
            reset = true;
        }
        if (mEmptyDrawable != null && mEmptyDrawable.getIntrinsicHeight() > height) {
            height = mEmptyDrawable.getIntrinsicHeight();
            reset = true;
        }
        if (reset)
            setMeasuredDimension(width, height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        switch (mState) {
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
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mAlwaysDrawChild) {
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
                return child != mEmptyView || super.drawChild(canvas, child, drawingTime);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (mState) {
                    case STATE_LOADING:
                        if (mLoadingDrawable != null) {
                            Compat.setHotspot(mLoadingDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                    case STATE_ERROR:
                        if (mErrorDrawable != null) {
                            Compat.setHotspot(mErrorDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                    case STATE_EMPTY:
                        if (mEmptyDrawable != null) {
                            Compat.setHotspot(mEmptyDrawable, event.getX(),
                                    event.getY());
                        }
                        break;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        final boolean superResult = super.performClick();
        boolean result = false;
        if (mClickListener != null) {
            if (mClickListener instanceof OnAllStateClickListener) {
                OnAllStateClickListener listener = (OnAllStateClickListener) mClickListener;
                switch (mState) {
                    case STATE_NORMAL:
                        if (!superResult)
                            playSoundEffect(SoundEffectConstants.CLICK);
                        listener.onNormalClick(this);
                        result = true;
                        break;
                    case STATE_LOADING:
                        if (!superResult)
                            playSoundEffect(SoundEffectConstants.CLICK);
                        listener.onLoadingClick(this);
                        result = true;
                        break;
                    case STATE_ERROR:
                        if (!superResult)
                            playSoundEffect(SoundEffectConstants.CLICK);
                        listener.onErrorClick(this);
                        result = true;
                        break;
                    case STATE_EMPTY:
                        if (!superResult)
                            playSoundEffect(SoundEffectConstants.CLICK);
                        listener.onEmptyClick(this);
                        result = true;
                        break;
                }
            } else {
                switch (mState) {
                    case STATE_ERROR:
                        if (!superResult)
                            playSoundEffect(SoundEffectConstants.CLICK);
                        mClickListener.onErrorClick(this);
                        result = true;
                        break;
                }
            }
        }
        return superResult || result;
    }

    @Override
    protected void drawableStateChanged() {
        switch (mState) {
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
        if (mLoadingView != null) {
            removeView(mLoadingView);
            mLoadingView = null;
        }
        if (loadingView != null) {
            setStateDrawables(null, mErrorDrawable, mEmptyDrawable);
            mLoadingView = loadingView;
            addView(mLoadingView, layoutParams);
            checkViewVisibility();
        }
    }

    /**
     * 设置自定义载入View
     *
     * @param loadingView 载入View
     */
    public void setLoadingView(View loadingView) {
        if (loadingView == null) {
            setLoadingView(null, null);
            return;
        }
        ViewGroup.LayoutParams lpHas = loadingView.getLayoutParams();
        LayoutParams lp = lpHas == null ? new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER) :
                generateLayoutParams(lpHas);
        lp.setState(STATE_LOADING);
        setLoadingView(loadingView, lp);
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
        if (mErrorView != null) {
            removeView(mErrorView);
            mErrorView = null;
        }
        if (errorView != null) {
            setStateDrawables(mLoadingDrawable, null, mEmptyDrawable);
            mErrorView = errorView;
            addView(mErrorView, layoutParams);
            checkViewVisibility();
        }
    }

    /**
     * 设置自定义错误View
     *
     * @param errorView 错误View
     */
    public void setErrorView(View errorView) {
        if (errorView == null) {
            setErrorView(null, null);
            return;
        }
        ViewGroup.LayoutParams lpHas = errorView.getLayoutParams();
        LayoutParams lp = lpHas == null ? new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER) :
                generateLayoutParams(lpHas);
        lp.setState(STATE_ERROR);
        setErrorView(errorView, lp);
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
        if (mEmptyView != null) {
            removeView(mEmptyView);
            mEmptyView = null;
        }
        if (emptyView != null) {
            setStateDrawables(mLoadingDrawable, mErrorDrawable, null);
            mEmptyView = emptyView;
            addView(mEmptyView, layoutParams);
            checkViewVisibility();
        }
    }

    /**
     * 设置自定义空白View
     *
     * @param emptyView 空白View
     */
    public void setEmptyView(View emptyView) {
        if (emptyView == null) {
            setEmptyView(null, null);
            return;
        }
        ViewGroup.LayoutParams lpHas = emptyView.getLayoutParams();
        LayoutParams lp = lpHas == null ? new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER) :
                generateLayoutParams(lpHas);
        lp.setState(STATE_EMPTY);
        setEmptyView(emptyView, lp);
    }

    /**
     * 设置状态View
     *
     * @param loading 载入
     * @param error   错误
     * @param empty   空白
     */
    @SuppressWarnings("unused")
    public void setStateViews(View loading, View error, View empty) {
        setLoadingView(loading);
        setErrorView(error);
        setEmptyView(empty);
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
                showAllItems(true);
                break;
            case STATE_LOADING:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.VISIBLE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                showAllItems(mAlwaysDrawChild);
                break;
            case STATE_ERROR:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.VISIBLE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                showAllItems(mAlwaysDrawChild);
                break;
            case STATE_EMPTY:
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.VISIBLE);
                showAllItems(mAlwaysDrawChild);
                break;
        }
    }

    private void showAllItems(boolean show) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != mLoadingView && child != mEmptyView && child != mErrorView) {
                child.setVisibility(show ? VISIBLE : INVISIBLE);
            }
        }
    }

    /**
     * 设置载入 Drawable
     *
     * @param loading 载入
     */
    public void setLoadingDrawable(Drawable loading) {
        if (mLoadingDrawable != loading) {
            if (mLoadingDrawable != null) {
                if (mLoadingDrawable instanceof Animatable) {
                    ((Animatable) mLoadingDrawable).stop();
                }
                mLoadingDrawable.setCallback(null);
            }
            mLoadingDrawable = loading;
            if (mLoadingDrawable != null) {
                mLoadingDrawable.setCallback(this);
                setLoadingView(null);
            }
            requestLayout();
            invalidate();
            animateDrawable();
        }
    }

    /**
     * 设置载入 Drawable
     *
     * @param loading 载入
     */
    public void setLoadingDrawable(int loading) {
        setLoadingDrawable(Compat.getDrawable(getContext(), loading));
    }

    /**
     * 设置错误 Drawable
     *
     * @param error 错误
     */
    public void setErrorDrawable(Drawable error) {
        if (mErrorDrawable != error) {
            if (mErrorDrawable != null) {
                if (mErrorDrawable instanceof Animatable) {
                    ((Animatable) mErrorDrawable).stop();
                }
                mErrorDrawable.setCallback(null);
            }
            mErrorDrawable = error;
            if (mErrorDrawable != null) {
                mErrorDrawable.setCallback(this);
                setErrorView(null);
            }
            requestLayout();
            invalidate();
            animateDrawable();
        }
    }

    /**
     * 设置错误 Drawable
     *
     * @param error 错误
     */
    public void setErrorDrawable(int error) {
        setErrorDrawable(Compat.getDrawable(getContext(), error));
    }

    /**
     * 设置空白 Drawable
     *
     * @param empty 空白
     */
    public void setEmptyDrawable(Drawable empty) {
        if (mEmptyDrawable != empty) {
            if (mEmptyDrawable != null) {
                if (mEmptyDrawable instanceof Animatable) {
                    ((Animatable) mEmptyDrawable).stop();
                }
                mEmptyDrawable.setCallback(null);
            }
            mEmptyDrawable = empty;
            if (mEmptyDrawable != null) {
                mEmptyDrawable.setCallback(this);
                setEmptyView(null);
            }
            requestLayout();
            invalidate();
            animateDrawable();
        }
    }

    /**
     * 设置空白 Drawable
     *
     * @param empty 空白
     */
    public void setEmptyDrawable(int empty) {
        setEmptyDrawable(Compat.getDrawable(getContext(), empty));
    }

    /**
     * 设置状态 Drawable
     *
     * @param loading 载入
     * @param error   错误
     * @param empty   空白
     */
    public void setStateDrawables(Drawable loading, Drawable error, Drawable empty) {
        setLoadingDrawable(loading);
        setErrorDrawable(error);
        setEmptyDrawable(empty);
    }

    /**
     * 设置状态 Drawable
     *
     * @param loading 载入
     * @param error   错误
     * @param empty   空内容
     */
    @SuppressWarnings("unused")
    public void setStateDrawables(int loading, int error, int empty) {
        setLoadingDrawable(loading);
        setErrorDrawable(error);
        setEmptyDrawable(empty);
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

    /**
     * 获取状态
     *
     * @return 状态
     */
    public int getState() {
        return mState;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(int state) {
        if (mState != state) {
            mState = state;
            invalidateState();
        }
    }

    /**
     * 刷新状态
     */
    public void invalidateState() {
        checkViewVisibility();
        invalidate();
        animateDrawable();
    }

    /**
     * 修改状态为普通
     */
    @SuppressWarnings("unused")
    public void normal() {
        setState(STATE_NORMAL);
    }

    /**
     * 修改状态为载入
     */
    @SuppressWarnings("unused")
    public void loading() {
        setState(STATE_LOADING);
    }

    /**
     * 修改状态为错误
     */
    @SuppressWarnings("unused")
    public void error() {
        setState(STATE_ERROR);
    }

    /**
     * 修改状态为空白
     */
    @SuppressWarnings("unused")
    public void empty() {
        setState(STATE_EMPTY);
    }

    /**
     * 是否始终绘制子项
     *
     * @return 是否始终绘制子项
     */
    public boolean isAlwaysDrawChild() {
        return mAlwaysDrawChild;
    }

    /**
     * 设置是否始终绘制子项
     *
     * @param draw 是否始终绘制子项
     */
    public void setAlwaysDrawChild(boolean draw) {
        mAlwaysDrawChild = draw;
        invalidate();
    }

    /**
     * 状态点击监听
     *
     * @param listener 状态点击监听
     */
    @SuppressWarnings("unused")
    public void setOnStateClickListener(OnStateClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mState = getState();
        ss.mAlwaysDrawChild = isAlwaysDrawChild();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        setAlwaysDrawChild(ss.mAlwaysDrawChild);
        mState = ss.mState;
        invalidateState();
        super.onRestoreInstanceState(ss.getSuperState());
    }

    /**
     * 状态点击监听
     *
     * @author Alex
     */
    public interface OnAllStateClickListener extends OnStateClickListener {
        void onNormalClick(StateFrameLayout layout);

        void onLoadingClick(StateFrameLayout layout);

        void onEmptyClick(StateFrameLayout layout);
    }

    /**
     * 状态点击监听
     *
     * @author Alex
     */
    public interface OnStateClickListener {
        void onErrorClick(StateFrameLayout layout);
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private int mState = STATE_NORMAL;
        private boolean mAlwaysDrawChild = false;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mState = in.readInt();
            mAlwaysDrawChild = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mState);
            out.writeInt(mAlwaysDrawChild ? 1 : 0);
        }
    }

    /**
     * Per-child layout information associated with WrapLayout.
     */
    public static class LayoutParams extends FrameLayout.LayoutParams {

        private int mState = STATE_NORMAL;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int state = STATE_NORMAL;
            TypedArray custom = c.obtainStyledAttributes(attrs, R.styleable.StateFrameLayout_Layout);
            state = custom.getInt(R.styleable.StateFrameLayout_Layout_sflLayout_state, state);
            custom.recycle();
            mState = state;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(int width, int height, int gravity, int state) {
            super(width, height, gravity);
            mState = state;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        public LayoutParams(LayoutParams source) {
            super(source);
            mState = source.mState;
        }

        /**
         * 获取状态
         *
         * @return 状态
         */
        public int getState() {
            return mState;
        }

        /**
         * 设置状态
         *
         * @param state 状态
         */
        public void setState(int state) {
            if (mState != state) {
                mState = state;
            }
        }
    }
}
