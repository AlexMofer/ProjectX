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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 状态帧布局
 *
 * @author Alex
 */
public class StateFrameLayout extends FrameLayout {

    public static final int STATE_NORMAL = 0;// 普通
    public static final int STATE_LOADING = 1;// 载入
    public static final int STATE_ERROR = 2;// 错误
    public static final int STATE_EMPTY = 3;// 空白
    public static final int CHANGE_DRAW = 0;// 控制绘制
    public static final int CHANGE_GONE = 1;// 控制其是否Gone
    public static final int CHANGE_INVISIBLE = 2;// 控制其是否Invisible
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mDrawableLoading;
    private Drawable mDrawableError;
    private Drawable mDrawableEmpty;
    private int mState = STATE_NORMAL;
    private int mChangeType = CHANGE_DRAW;

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
                0);
        custom.recycle();
        if (drawableLoading != null) {
            drawableLoading.setCallback(this);
            mDrawableLoading = drawableLoading;
            setClickable(true);
        }
        if (drawableError != null) {
            drawableError.setCallback(this);
            mDrawableError = drawableError;
            setClickable(true);
        }
        if (drawableEmpty != null) {
            drawableEmpty.setCallback(this);
            mDrawableEmpty = drawableEmpty;
            setClickable(true);
        }
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
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
                final int state = getViewLayoutState(child);
                if (state == mState)
                    child.setVisibility(VISIBLE);
                else
                    child.setVisibility(INVISIBLE);
            }
        } else if (mChangeType == CHANGE_GONE) {
            final int itemCount = getChildCount();
            for (int i = 0; i < itemCount; i++) {
                final View child = getChildAt(i);
                final int state = getViewLayoutState(child);
                if (state == mState)
                    child.setVisibility(VISIBLE);
                else
                    child.setVisibility(GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getViewLayoutState(View child) {
        final ViewGroup.LayoutParams params = child.getLayoutParams();
        return params instanceof LayoutParams ? ((LayoutParams) params).getState() : STATE_NORMAL;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mChangeType != CHANGE_DRAW)
            return super.drawChild(canvas, child, drawingTime);
        if (getViewLayoutState(child) == mState)
            return super.drawChild(canvas, child, drawingTime);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawState(canvas);
    }

    private void drawState(Canvas canvas) {
        switch (mState) {
            default:
            case STATE_NORMAL:
                break;
            case STATE_LOADING:
                break;
            case STATE_ERROR:
                break;
            case STATE_EMPTY:
                break;
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
