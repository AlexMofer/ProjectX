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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 状态帧布局
 *
 * @author Alex
 */
@SuppressWarnings({"unused", "NullableProblems", "WeakerAccess"})
public class StateFrameLayout extends FrameLayout {

    public static final int STATE_NORMAL = 0;// 普通
    public static final int STATE_LOADING = 1;// 载入
    public static final int STATE_ERROR = 2;// 错误
    public static final int STATE_EMPTY = 3;// 空白
    public static final int CHANGE_DRAW = 0;// 控制绘制
    public static final int CHANGE_GONE = 1;// 控制其是否Gone
    public static final int CHANGE_INVISIBLE = 2;// 控制其是否Invisible（默认）

    private Drawable mDrawableLoading;
    private Drawable mDrawableError;
    private Drawable mDrawableEmpty;

    private int mState = STATE_NORMAL;
    private final Rect tRect = new Rect();
    private int mChangeType = CHANGE_INVISIBLE;
    private boolean mForceIntercept = false;
    private boolean mNormalNeverHide = false;

    public StateFrameLayout(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public StateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public StateFrameLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public StateFrameLayout(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setWillNotDraw(false);
        final Drawable drawableLoading, drawableError, drawableEmpty;
        final int layoutIdLoading, layoutIdError, layoutIdEmpty;
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.StateFrameLayout, defStyleAttr, defStyleRes);
        drawableLoading = custom.getDrawable(R.styleable.StateFrameLayout_sflLoadingDrawable);
        drawableError = custom.getDrawable(R.styleable.StateFrameLayout_sflErrorDrawable);
        drawableEmpty = custom.getDrawable(R.styleable.StateFrameLayout_sflEmptyDrawable);
        layoutIdLoading = custom.getResourceId(R.styleable.StateFrameLayout_sflLoadingLayout,
                NO_ID);
        layoutIdError = custom.getResourceId(R.styleable.StateFrameLayout_sflErrorLayout, NO_ID);
        layoutIdEmpty = custom.getResourceId(R.styleable.StateFrameLayout_sflEmptyLayout, NO_ID);
        final int state = custom.getInt(R.styleable.StateFrameLayout_sflState, 0);
        final int changeType = custom.getInt(R.styleable.StateFrameLayout_sflChangeType,
                2);
        mForceIntercept = custom.getBoolean(R.styleable.StateFrameLayout_sflForceIntercept,
                mForceIntercept);
        mNormalNeverHide = custom.getBoolean(R.styleable.StateFrameLayout_sflNormalNeverHide,
                mNormalNeverHide);
        custom.recycle();
        if (drawableLoading != null) {
            drawableLoading.setCallback(this);
            mDrawableLoading = drawableLoading;
        }
        if (drawableError != null) {
            drawableError.setCallback(this);
            mDrawableError = drawableError;
        }
        if (drawableEmpty != null) {
            drawableEmpty.setCallback(this);
            mDrawableEmpty = drawableEmpty;
        }
        updateAnimateDrawable();
        final LayoutInflater inflater = LayoutInflater.from(context);
        if (layoutIdEmpty != NO_ID) {
            final View empty = inflater.inflate(layoutIdEmpty, this, false);
            if (empty != null) {
                final ViewGroup.LayoutParams lp = empty.getLayoutParams();
                final LayoutParams params;
                if (lp instanceof LayoutParams)
                    params = (LayoutParams) lp;
                else
                    params = generateDefaultLayoutParams();
                params.setState(STATE_EMPTY);
                addView(empty, params);
            }
        }
        if (layoutIdError != NO_ID) {
            final View error = inflater.inflate(layoutIdError, this, false);
            if (error != null) {
                final ViewGroup.LayoutParams lp = error.getLayoutParams();
                final LayoutParams params;
                if (lp instanceof LayoutParams)
                    params = (LayoutParams) lp;
                else
                    params = generateDefaultLayoutParams();
                params.setState(STATE_ERROR);
                addView(error, params);
            }
        }
        if (layoutIdLoading != NO_ID) {
            final View loading = inflater.inflate(layoutIdLoading, this, false);
            if (loading != null) {
                final ViewGroup.LayoutParams lp = loading.getLayoutParams();
                final LayoutParams params;
                if (lp instanceof LayoutParams)
                    params = (LayoutParams) lp;
                else
                    params = generateDefaultLayoutParams();
                params.setState(STATE_LOADING);
                addView(loading, params);
            }
        }
        switch (state) {
            default:
            case 0:
                mState = STATE_NORMAL;
                break;
            case 1:
                mState = STATE_LOADING;
                break;
            case 2:
                mState = STATE_ERROR;
                break;
            case 3:
                mState = STATE_EMPTY;
                break;
        }
        switch (changeType) {
            default:
            case 0:
                mChangeType = CHANGE_DRAW;
                break;
            case 1:
                mChangeType = CHANGE_GONE;
                break;
            case 2:
                mChangeType = CHANGE_INVISIBLE;
                break;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (lp instanceof LayoutParams)
                return new LayoutParams((LayoutParams) lp);
            if (lp instanceof FrameLayout.LayoutParams)
                return new LayoutParams((FrameLayout.LayoutParams) lp);
        }
        if (lp instanceof MarginLayoutParams)
            return new LayoutParams((MarginLayoutParams) lp);
        return new LayoutParams(lp);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mChangeType == CHANGE_INVISIBLE) {
            final int itemCount = getChildCount();
            for (int i = 0; i < itemCount; i++) {
                final View child = getChildAt(i);
                child.setVisibility(isVisible(child) ? VISIBLE : INVISIBLE);
            }
        } else if (mChangeType == CHANGE_GONE) {
            final int itemCount = getChildCount();
            for (int i = 0; i < itemCount; i++) {
                final View child = getChildAt(i);
                child.setVisibility(isVisible(child) ? VISIBLE : GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 判断子项是否可见
     *
     * @param child 子项
     * @return 是否可见
     */
    protected boolean isVisible(View child) {
        if (mNormalNeverHide && getViewLayoutState(child) == STATE_NORMAL)
            return true;
        return getViewLayoutState(child) == mState;
    }

    private int getViewLayoutState(View child) {
        final ViewGroup.LayoutParams params = child.getLayoutParams();
        return params instanceof LayoutParams ? ((LayoutParams) params).getState() : STATE_NORMAL;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDrawableLoading != null) {
            final Drawable loading = mDrawableLoading;
            final Rect padding = tRect;
            padding.setEmpty();
            loading.getPadding(padding);
            final int width = loading.getMinimumWidth() + padding.left + padding.right;
            final int height = loading.getMinimumHeight() + padding.top + padding.bottom;
            final int left = Math.round((w - width) * 0.5f);
            final int top = Math.round((h - height) * 0.5f);
            loading.setBounds(left, top, left + width, top + height);
        }
        if (mDrawableError != null) {
            final Drawable error = mDrawableError;
            final Rect padding = tRect;
            padding.setEmpty();
            error.getPadding(padding);
            final int width = error.getMinimumWidth() + padding.left + padding.right;
            final int height = error.getMinimumHeight() + padding.top + padding.bottom;
            final int left = Math.round((w - width) * 0.5f);
            final int top = Math.round((h - height) * 0.5f);
            error.setBounds(left, top, left + width, top + height);
        }
        if (mDrawableEmpty != null) {
            final Drawable empty = mDrawableEmpty;
            final Rect padding = tRect;
            padding.setEmpty();
            empty.getPadding(padding);
            final int width = empty.getMinimumWidth() + padding.left + padding.right;
            final int height = empty.getMinimumHeight() + padding.top + padding.bottom;
            final int left = Math.round((w - width) * 0.5f);
            final int top = Math.round((h - height) * 0.5f);
            empty.setBounds(left, top, left + width, top + height);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mChangeType != CHANGE_DRAW)
            return super.drawChild(canvas, child, drawingTime);
        if (isVisible(child))
            return super.drawChild(canvas, child, drawingTime);
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            drawState(canvas);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        drawState(canvas);
    }

    private void drawState(Canvas canvas) {
        switch (mState) {
            default:
            case STATE_NORMAL:
                break;
            case STATE_LOADING:
                if (mDrawableLoading != null)
                    mDrawableLoading.draw(canvas);
                break;
            case STATE_ERROR:
                if (mDrawableError != null)
                    mDrawableError.draw(canvas);
                break;
            case STATE_EMPTY:
                if (mDrawableEmpty != null)
                    mDrawableEmpty.draw(canvas);
                break;
        }
    }

    @Override
    protected void drawableStateChanged() {
        final int[] state = getDrawableState();
        switch (mState) {
            case STATE_LOADING:
                if (mDrawableLoading != null && mDrawableLoading.isStateful())
                    mDrawableLoading.setState(state);
                break;
            case STATE_ERROR:
                if (mDrawableError != null && mDrawableError.isStateful())
                    mDrawableError.setState(state);
                break;
            case STATE_EMPTY:
                if (mDrawableEmpty != null && mDrawableEmpty.isStateful())
                    mDrawableEmpty.setState(state);
                break;
        }
        super.drawableStateChanged();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (super.verifyDrawable(who))
            return true;
        return who == mDrawableLoading || who == mDrawableError || who == mDrawableEmpty;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void dispatchDrawableHotspotChanged(float x, float y) {
        super.dispatchDrawableHotspotChanged(x, y);
        switch (mState) {
            case STATE_LOADING:
                if (mDrawableLoading != null)
                    mDrawableLoading.setHotspot(x, y);
                break;
            case STATE_ERROR:
                if (mDrawableError != null)
                    mDrawableError.setHotspot(x, y);
                break;
            case STATE_EMPTY:
                if (mDrawableEmpty != null)
                    mDrawableEmpty.setHotspot(x, y);
                break;
        }
    }

    private void stop(Drawable drawable) {
        if (drawable instanceof Animatable)
            ((Animatable) drawable).stop();
    }

    private void start(Drawable drawable) {
        if (drawable instanceof Animatable)
            ((Animatable) drawable).start();
    }

    private void updateAnimateDrawable() {
        switch (mState) {
            default:
            case STATE_NORMAL:
                stop(mDrawableLoading);
                stop(mDrawableError);
                stop(mDrawableEmpty);
                break;
            case STATE_LOADING:
                start(mDrawableLoading);
                stop(mDrawableError);
                stop(mDrawableEmpty);
                break;
            case STATE_ERROR:
                stop(mDrawableLoading);
                start(mDrawableError);
                stop(mDrawableEmpty);
                break;
            case STATE_EMPTY:
                stop(mDrawableLoading);
                stop(mDrawableError);
                start(mDrawableEmpty);
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mChangeType == CHANGE_DRAW && mForceIntercept && mState != STATE_NORMAL)
            return true;
        return super.onInterceptTouchEvent(ev);
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
        if (mState == state)
            return;
        mState = state;
        if (mChangeType != CHANGE_DRAW)
            requestLayout();
        invalidate();
        updateAnimateDrawable();
    }

    /**
     * 修改状态为载入
     */
    public void loading() {
        setState(STATE_LOADING);
    }

    /**
     * 判断是否为载入状态
     *
     * @return 是否为载入状态
     */
    public boolean isLoading() {
        return getState() == STATE_LOADING;
    }

    /**
     * 修改状态为错误
     */
    public void error() {
        setState(STATE_ERROR);
    }

    /**
     * 判断是否为错误状态
     *
     * @return 是否为错误状态
     */
    public boolean isError() {
        return getState() == STATE_ERROR;
    }

    /**
     * 修改状态为空白
     */
    public void empty() {
        setState(STATE_EMPTY);
    }

    /**
     * 判断是否为空白状态
     *
     * @return 是否为空白状态
     */
    public boolean isEmpty() {
        return getState() == STATE_EMPTY;
    }

    /**
     * 修改状态为普通
     */
    public void normal() {
        setState(STATE_NORMAL);
    }

    /**
     * 判断是否为普通状态
     *
     * @return 是否为普通状态
     */
    public boolean isNormal() {
        return getState() == STATE_NORMAL;
    }

    /**
     * 获取变化类型
     *
     * @return 变化类型
     */
    public int getChangeType() {
        return mChangeType;
    }

    /**
     * 设置变化类型
     *
     * @param type 变化类型
     */
    public void setChangeType(int type) {
        if (mChangeType == type)
            return;
        mChangeType = type;
        requestLayout();
        invalidate();
    }

    /**
     * 判断在状态变化为控制绘图且状态不为普通的情况下状态改变是否强制拦截触摸事件
     *
     * @return 是否强制拦截触摸事件
     */
    public boolean isForceIntercept() {
        return mForceIntercept;
    }

    /**
     * 设置在状态变化为控制绘图且状态不为普通的情况下状态改变是否强制拦截触摸事件
     *
     * @param force 是否强制拦截触摸事件
     */
    public void setForceIntercept(boolean force) {
        mForceIntercept = force;
    }

    /**
     * 判断普通状态下的子项是否始终不隐藏
     *
     * @return 是否始终不隐藏
     */
    public boolean isNormalNeverHide() {
        return mNormalNeverHide;
    }

    /**
     * 设置普通状态下的子项是否始终不隐藏
     *
     * @param never 是否始终不隐藏
     */
    public void setNormalNeverHide(boolean never) {
        if (mNormalNeverHide == never)
            return;
        mNormalNeverHide = never;
        requestLayout();
        invalidate();
    }

    /**
     * 获取载入状态图片
     *
     * @return 载入状态图片
     */
    public Drawable getDrawableLoading() {
        return mDrawableLoading;
    }

    /**
     * 设置载入状态图片
     *
     * @param loading 载入状态图片
     */
    public void setDrawableLoading(Drawable loading) {
        if (mDrawableLoading == loading)
            return;
        if (mDrawableLoading != null)
            mDrawableLoading.setCallback(null);
        mDrawableLoading = loading;
        if (mDrawableLoading != null)
            mDrawableLoading.setCallback(this);
        invalidate();
        updateAnimateDrawable();
    }

    /**
     * 获取错误状态图片
     *
     * @return 错误状态图片
     */
    public Drawable getDrawableError() {
        return mDrawableError;
    }

    /**
     * 设置错误状态图片
     *
     * @param error 错误状态图片
     */
    public void setDrawableError(Drawable error) {
        if (mDrawableError == error)
            return;
        if (mDrawableError != null)
            mDrawableError.setCallback(null);
        mDrawableError = error;
        if (mDrawableError != null)
            mDrawableError.setCallback(this);
        invalidate();
        updateAnimateDrawable();
    }

    /**
     * 获取空白状态图片
     *
     * @return 空白状态图片
     */
    public Drawable getDrawableEmpty() {
        return mDrawableEmpty;
    }

    /**
     * 设置空白状态图片
     *
     * @param empty 空白状态图片
     */
    public void setDrawableEmpty(Drawable empty) {
        if (mDrawableEmpty == empty)
            return;
        if (mDrawableEmpty != null)
            mDrawableEmpty.setCallback(null);
        mDrawableEmpty = empty;
        if (mDrawableEmpty != null)
            mDrawableEmpty.setCallback(this);
        invalidate();
        updateAnimateDrawable();
    }

    /**
     * 设置图片
     *
     * @param loading 载入状态图片
     * @param error   错误状态图片
     * @param empty   空白状态图片
     */
    public void setDrawable(Drawable loading, Drawable error, Drawable empty) {
        if (mDrawableLoading == loading && mDrawableError == error && mDrawableEmpty == empty)
            return;
        if (mDrawableLoading != null)
            mDrawableLoading.setCallback(null);
        mDrawableLoading = loading;
        if (mDrawableLoading != null)
            mDrawableLoading.setCallback(this);
        if (mDrawableError != null)
            mDrawableError.setCallback(null);
        mDrawableError = error;
        if (mDrawableError != null)
            mDrawableError.setCallback(this);
        if (mDrawableEmpty != null)
            mDrawableEmpty.setCallback(null);
        mDrawableEmpty = empty;
        if (mDrawableEmpty != null)
            mDrawableEmpty.setCallback(this);
        invalidate();
        updateAnimateDrawable();
    }

    /**
     * 设置图片
     *
     * @param loading 载入状态图片
     * @param error   错误状态图片
     * @param empty   空白状态图片
     */
    public void setDrawable(int loading, int error, int empty) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Context context = getContext();
            setDrawable(context.getDrawable(loading), context.getDrawable(error),
                    context.getDrawable(empty));
        } else {
            final Resources res = getResources();
            setDrawable(res.getDrawable(loading), res.getDrawable(error), res.getDrawable(empty));
        }
    }

    /**
     * 布局参数
     */
    public static class LayoutParams extends FrameLayout.LayoutParams {

        private int mState = STATE_NORMAL;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray custom = context.obtainStyledAttributes(attrs,
                    R.styleable.StateFrameLayout_Layout);
            final int state = custom.getInt(R.styleable.StateFrameLayout_Layout_sflLayout_state,
                    0);
            custom.recycle();
            switch (state) {
                default:
                case 0:
                    mState = STATE_NORMAL;
                    break;
                case 1:
                    mState = STATE_LOADING;
                    break;
                case 2:
                    mState = STATE_ERROR;
                    break;
                case 3:
                    mState = STATE_EMPTY;
                    break;
            }
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

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
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
