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
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import am.widget.itemanimatorcontrollablerecyclerview.ItemAnimatorControllableRecyclerView;


/**
 * 带滚动条的RecyclerView
 * Created by Alex on 2017/10/18.
 */
@SuppressWarnings("all")
public class ScrollbarRecyclerView extends ItemAnimatorControllableRecyclerView {

    private Scrollbar mScrollbar;// 滚动条
    private IndicatorAdapter mAdapter;

    private boolean mInterceptTouch;

    public ScrollbarRecyclerView(Context context) {
        super(context);
        initView(context, null);
    }

    public ScrollbarRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ScrollbarRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        if (useDefaultSlider()) {
            setScrollbar(new DefaultScrollbar());
        }
        onConstruct(context, attrs);
    }

    /**
     * 是否使用默认的滚动条
     *
     * @return 是否使用默认
     */
    protected boolean useDefaultSlider() {
        return true;
    }

    /**
     * 初始化View
     *
     * @param context Context
     * @param attrs   属性
     */
    protected void onConstruct(Context context, @Nullable AttributeSet attrs) {
        if (mScrollbar != null)
            mScrollbar.onConstruct(this, context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < 23 && mScrollbar != null) {
            mScrollbar.onDraw(this, canvas);
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        if (mScrollbar != null) {
            mScrollbar.onDraw(this, canvas);
        }
        super.onDrawForeground(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInterceptTouch = false;
                if (mScrollbar != null && mScrollbar.onInterceptTouchEvent(this, event)) {
                    mInterceptTouch = true;
                    return mScrollbar.onTouchEvent(this, event);
                }
                break;
            default:
                if (mInterceptTouch) {
                    return mScrollbar.onTouchEvent(this, event);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (mScrollbar != null) {
            mScrollbar.onScrollStateChanged(this, state);
        }
    }

    protected String getScrollbarIndicator() {
        if (isInEditMode())
            return "O";
        return mAdapter == null ? null : mAdapter.getIndicator(this);
    }

    /**
     * 获取滚动条
     *
     * @return 滚动条
     */
    public Scrollbar getScrollbar() {
        return mScrollbar;
    }

    /**
     * 设置滚动条
     *
     * @param scrollbar 滚动条
     */
    public void setScrollbar(Scrollbar scrollbar) {
        if (mScrollbar == scrollbar)
            return;
        if (mScrollbar != null)
            mScrollbar.onDetachedFromView(this);
        mScrollbar = scrollbar;
        if (mScrollbar != null)
            mScrollbar.onAttachedToView(this);
        invalidate();
    }

    /**
     * 设置触摸指示标内容提供者
     *
     * @param adapter 触摸指示标内容提供者
     */
    public void setIndicatorAdapter(IndicatorAdapter adapter) {
        mAdapter = adapter;
        invalidate();
    }

    /**
     * 触摸指示标内容提供者
     */
    public interface IndicatorAdapter {

        /**
         * 获取指示标文本内容
         *
         * @param view 视图
         * @return 文本内容
         */
        String getIndicator(ScrollbarRecyclerView view);
    }

    /**
     * 滚动条
     */
    public static abstract class Scrollbar {

        public static final int SHOW_NONE = 0;// 不显示
        public static final int SHOW_HORIZONTAL = 1;// 显示水平滚动条
        public static final int SHOW_VERTICAL = 2;// 显示垂直滚动条
        public static final int SHOW_ALL = 3;// 显示全部滚动条
        private ScrollbarRecyclerView mView;

        /**
         * 附着视图
         *
         * @param view 视图
         */
        protected void onAttachedToView(ScrollbarRecyclerView view) {
            mView = view;
        }

        /**
         * 脱离视图
         *
         * @param view 视图
         */
        protected void onDetachedFromView(ScrollbarRecyclerView view) {
            mView = null;
        }

        /**
         * 获取附着的视图
         *
         * @return 附着的视图
         */
        @Nullable
        protected ScrollbarRecyclerView getAttachedView() {
            return mView;
        }

        /**
         * 视图控件初始化
         * 仅在ScrollbarRecyclerView尚未构建之前就已经设置了Scrollbar，其才会调用。具体方法：
         * 重写{@link ScrollbarRecyclerView#useDefaultSlider()}方法返回flase，保证不使用默认滚动条。
         * 重写{@link ScrollbarRecyclerView#onConstruct(Context, AttributeSet)}方法，
         * 并在调用其super方法之前设置自己的Scrollbar。
         *
         * @param view    视图
         * @param context Context
         * @param attrs   属性
         */
        protected abstract void onConstruct(ScrollbarRecyclerView view, Context context,
                                            @Nullable AttributeSet attrs);

        /**
         * 绘制
         *
         * @param view   视图
         * @param canvas 画布
         */
        protected abstract void onDraw(ScrollbarRecyclerView view, Canvas canvas);

        /**
         * 拦截触摸事件
         *
         * @param view  视图
         * @param event 触摸事件
         * @return 是否拦截
         */
        protected abstract boolean onInterceptTouchEvent(ScrollbarRecyclerView view, MotionEvent event);

        /**
         * 响应触摸事件
         *
         * @param view  视图
         * @param event 触摸事件
         * @return 是否响应
         */
        protected abstract boolean onTouchEvent(ScrollbarRecyclerView view, MotionEvent event);

        /**
         * 滚动状态已改变
         *
         * @param view  视图
         * @param state 滚动状态
         */
        protected abstract void onScrollStateChanged(ScrollbarRecyclerView view, int state);

        @IntDef({SHOW_NONE, SHOW_HORIZONTAL, SHOW_VERTICAL, SHOW_ALL})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ShowType {
        }
    }
}
