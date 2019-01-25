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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import am.widget.multifunctionalrecyclerview.R;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

/**
 * 默认的滚动条
 * Created by Alex on 2017/11/1.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DefaultScrollbar extends ScrollbarRecyclerView.Scrollbar {

    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_START = 1;
    public static final int GRAVITY_END = 2;
    public static final int ANIMATOR_TYPE_NONE = 0;
    public static final int ANIMATOR_TYPE_HIDE = 1;
    public static final int ANIMATOR_TYPE_MOVE = 2;
    public static final int ANIMATOR_TYPE_ALL = 3;
    static final int[] PRESS =
            new int[]{android.R.attr.state_focused, android.R.attr.state_pressed};
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

    /**
     * 设置水平滚动条的边距
     *
     * @param edge  底边缘间距
     * @param start 左边边缘间距
     * @param end   右边边缘间距
     */
    public void setHorizontalPadding(int edge, int start, int end) {
        mHorizontalScrollbar.setPadding(edge, start, end);
    }

    /**
     * 获取水平滚动条的底边缘间距
     *
     * @return 底边缘间距
     */
    public int getHorizontalPaddingEdge() {
        return mHorizontalScrollbar.getPaddingEdge();
    }

    /**
     * 设置水平滚动条的底边缘间距
     *
     * @param padding 底边缘间距
     */
    public void setHorizontalPaddingEdge(int padding) {
        mHorizontalScrollbar.setPaddingEdge(padding);
    }

    /**
     * 获取水平滚动条的左边边缘间距
     *
     * @return 左边边缘间距
     */
    public int getHorizontalPaddingStart() {
        return mHorizontalScrollbar.getPaddingStart();
    }

    /**
     * 设置水平滚动条的左边边缘间距
     *
     * @param padding 左边边缘间距
     */
    public void setHorizontalPaddingStart(int padding) {
        mHorizontalScrollbar.setPaddingStart(padding);
    }

    /**
     * 获取水平滚动条的右边边缘间距
     *
     * @return 右边边缘间距
     */
    public int getHorizontalPaddingEnd() {
        return mHorizontalScrollbar.getPaddingEnd();
    }

    /**
     * 设置水平滚动条的右边边缘间距
     *
     * @param padding 右边边缘间距
     */
    public void setHorizontalPaddingEnd(int padding) {
        mHorizontalScrollbar.setPaddingEnd(padding);
    }

    /**
     * 设置垂直滚动条的边距
     *
     * @param edge  底边缘间距
     * @param start 左边边缘间距
     * @param end   右边边缘间距
     */
    public void setVerticalPadding(int edge, int start, int end) {
        mVerticalScrollbar.setPadding(edge, start, end);
    }

    /**
     * 获取垂直滚动条的底边缘间距
     *
     * @return 底边缘间距
     */
    public int getVerticalPaddingEdge() {
        return mVerticalScrollbar.getPaddingEdge();
    }

    /**
     * 设置垂直滚动条的底边缘间距
     *
     * @param padding 底边缘间距
     */
    public void setVerticalPaddingEdge(int padding) {
        mVerticalScrollbar.setPaddingEdge(padding);
    }

    /**
     * 获取垂直滚动条的左边边缘间距
     *
     * @return 左边边缘间距
     */
    public int getVerticalPaddingStart() {
        return mVerticalScrollbar.getPaddingStart();
    }

    /**
     * 设置垂直滚动条的左边边缘间距
     *
     * @param padding 左边边缘间距
     */
    public void setVerticalPaddingStart(int padding) {
        mVerticalScrollbar.setPaddingStart(padding);
    }

    /**
     * 获取垂直滚动条的右边边缘间距
     *
     * @return 右边边缘间距
     */
    public int getVerticalPaddingEnd() {
        return mVerticalScrollbar.getPaddingEnd();
    }

    /**
     * 设置垂直滚动条的右边边缘间距
     *
     * @param padding 右边边缘间距
     */
    public void setVerticalPaddingEnd(int padding) {
        mVerticalScrollbar.setPaddingEnd(padding);
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

        int getPaddingEdge();

        void setPaddingEdge(int padding);

        int getPaddingStart();

        void setPaddingStart(int padding);

        int getPaddingEnd();

        void setPaddingEnd(int padding);
    }
}
