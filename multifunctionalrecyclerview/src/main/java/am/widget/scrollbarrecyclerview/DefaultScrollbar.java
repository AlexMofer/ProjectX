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

package am.widget.scrollbarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import am.widget.multifunctionalrecyclerview.R;

/**
 * 默认的滚动条
 * TODO 图片触摸状态可优化
 * Created by Alex on 2017/11/1.
 */
@SuppressWarnings("all")
public class DefaultScrollbar extends ScrollbarRecyclerView.Scrollbar {

    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_START = 1;
    public static final int GRAVITY_END = 2;
    public static final int ANIMATOR_TYPE_NONE = 0;
    public static final int ANIMATOR_TYPE_HIDE = 1;
    public static final int ANIMATOR_TYPE_MOVE = 2;
    public static final int ANIMATOR_TYPE_ALL = 3;
    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mTextBound = new Rect();
    private final RectF mTouchBound = new RectF();
    private int mShowType = SHOW_NONE;
    private Scrollbar mHorizontalScrollbar;
    private Scrollbar mVerticalScrollbar;
    private int mTouchSlop;
    private boolean mTouchHorizontalSlider;

    public DefaultScrollbar() {
        mHorizontalScrollbar = new DefaultScrollbarHorizontal(this);
        mVerticalScrollbar = new DefaultScrollbarVertical(this);
    }

    @Override
    protected void onConstruct(ScrollbarRecyclerView view, Context context,
                               @Nullable AttributeSet attrs) {
        mPaint.density = context.getResources().getDisplayMetrics().density;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.ScrollbarRecyclerView);
        mShowType = custom.getInt(R.styleable.ScrollbarRecyclerView_dsShowScrollbar, SHOW_NONE);
        mHorizontalScrollbar.onConstruct(view, context, custom);
        mVerticalScrollbar.onConstruct(view, context, custom);
        custom.recycle();
    }

    @Override
    protected void onAttachedToView(ScrollbarRecyclerView view) {
        super.onAttachedToView(view);
        mHorizontalScrollbar.onAttachedToView(view);
        mVerticalScrollbar.onAttachedToView(view);
    }

    @Override
    protected void onDetachedFromView(ScrollbarRecyclerView view) {
        super.onDetachedFromView(view);
        mHorizontalScrollbar.onDetachedFromView(view);
        mVerticalScrollbar.onDetachedFromView(view);
    }

    @Override
    protected void onDraw(ScrollbarRecyclerView view, Canvas canvas) {
        if (isDisable())
            return;
        if (mShowType == SHOW_HORIZONTAL || mShowType == SHOW_ALL) {
            mHorizontalScrollbar.onDraw(view, canvas, mPaint, mTextBound);
        }
        if (mShowType == SHOW_VERTICAL || mShowType == SHOW_ALL) {
            mVerticalScrollbar.onDraw(view, canvas, mPaint, mTextBound);
        }
    }

    @Override
    protected boolean onInterceptTouchEvent(ScrollbarRecyclerView view, MotionEvent event) {
        if (isDisable())
            return false;
        final float x = event.getX();
        final float y = event.getY();
        mHorizontalScrollbar.getTouchBound(view, mTouchBound, mTouchSlop);
        mTouchHorizontalSlider = true;
        if (mTouchBound.contains(x, y)) {
            return true;
        }
        mVerticalScrollbar.getTouchBound(view, mTouchBound, mTouchSlop);
        mTouchHorizontalSlider = false;
        return mTouchBound.contains(x, y);
    }

    @Override
    protected boolean onTouchEvent(ScrollbarRecyclerView view, MotionEvent event) {
        if (mTouchHorizontalSlider) {
            return mHorizontalScrollbar.onTouch(view, event);
        } else {
            return mVerticalScrollbar.onTouch(view, event);
        }
    }

    @Override
    protected void onScrollStateChanged(ScrollbarRecyclerView view, int state) {
        if (isDisable())
            return;
        mHorizontalScrollbar.onScrollStateChanged(state);
        mVerticalScrollbar.onScrollStateChanged(state);
    }

    private boolean isDisable() {
        return mShowType == SHOW_NONE;
    }

    void invalidate() {
        ScrollbarRecyclerView view = getAttachedView();
        if (view != null) {
            view.invalidate();
        }
    }

    void invalidate(int left, int top, int right, int bottom) {
        ScrollbarRecyclerView view = getAttachedView();
        if (view != null) {
            view.invalidate(left, top, right, bottom);
        }
    }

    /**
     * 设置显示方式
     *
     * @param type 显示方式
     */
    public void setShowType(@ShowType int type) {
        if (mShowType != type) {
            mShowType = type;
            ScrollbarRecyclerView view = getAttachedView();
            if (view != null)
                view.invalidate();
        }
    }

    public void setHorizontalScrollbarPadding(int edge, int start, int end) {
        mHorizontalScrollbar.setPadding(edge, start, end);
    }

    public void setVerticalScrollbarPadding(int edge, int start, int end) {
        mHorizontalScrollbar.setPadding(edge, start, end);
    }

    @IntDef({GRAVITY_CENTER, GRAVITY_START, GRAVITY_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {
    }

    @IntDef({ANIMATOR_TYPE_NONE, ANIMATOR_TYPE_HIDE, ANIMATOR_TYPE_MOVE, ANIMATOR_TYPE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimatorType {
    }

    interface Scrollbar {
        void onConstruct(ScrollbarRecyclerView view, Context context, TypedArray custom);

        void onAttachedToView(ScrollbarRecyclerView view);

        void onDetachedFromView(ScrollbarRecyclerView view);

        void onDraw(ScrollbarRecyclerView view, Canvas canvas, Paint paint, Rect bound);

        void getTouchBound(ScrollbarRecyclerView view, RectF bound, int slop);

        boolean onTouch(ScrollbarRecyclerView view, MotionEvent event);

        void onScrollStateChanged(int state);

        void setPadding(int edge, int start, int end);
    }
}
