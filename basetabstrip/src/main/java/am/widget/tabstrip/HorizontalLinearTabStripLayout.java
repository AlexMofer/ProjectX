/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.tabstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 水平线性TabStrip布局
 *
 * @param <V> 子View
 */
@SuppressWarnings("unused")
public abstract class HorizontalLinearTabStripLayout<V extends View> extends TabStripLayout<V> {

    public static final int SHOW_DIVIDER_NONE = 0;// 不显示
    public static final int SHOW_DIVIDER_BEGINNING = 1;// 头部
    public static final int SHOW_DIVIDER_MIDDLE = 1 << 1;// 中间
    public static final int SHOW_DIVIDER_END = 1 << 2;// 尾部
    private int mChildWidth;
    private int mChildHeight;
    private Drawable mDivider;// 子项间隔
    private int mShowDividers = SHOW_DIVIDER_NONE;// 显示方式
    private int mDividerPadding;
    private Drawable mCenter;// 中间间隔（偶数个子项时有效）
    private boolean mCenterAsItem = false;
    private int mCenterPadding;

    public HorizontalLinearTabStripLayout(Context context) {
        super(context);
    }

    public HorizontalLinearTabStripLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalLinearTabStripLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void set(Drawable divider, int showDividers, int dividerPadding,
                       Drawable center, boolean centerAsItem, int centerPadding) {
        mDivider = divider;
        mShowDividers = showDividers;
        mDividerPadding = dividerPadding;
        mCenter = center;
        mCenterAsItem = centerAsItem;
        mCenterPadding = centerPadding;
        setWillDraw();
    }

    private void setWillDraw() {
        if (willNotDraw() && (isShowingDividers() || mCenter != null))
            setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        if (getChildCount() <= 0) {
            setMeasuredDimension(widthSize,
                    resolveSize(Math.max(paddingTop + paddingBottom, suggestedMinimumHeight),
                            heightMeasureSpec));
            return;
        }
        final int count = getChildCount();
        int contentWidth;
        if (count % 2 == 0 && mCenter != null) {
            // 中间预留空间
            contentWidth = widthSize - paddingStart - paddingEnd - mCenter.getIntrinsicWidth();
            if (isShowingDividers()) {
                final int divider = mDivider.getIntrinsicWidth();
                if ((mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE) {
                    contentWidth -= divider * (count - 2);
                    if (mCenterAsItem)
                        contentWidth = contentWidth - divider - divider;
                }
            }
        } else {
            // 无需预留
            contentWidth = widthSize - paddingStart - paddingEnd;
            if (isShowingDividers()) {
                final int divider = mDivider.getIntrinsicWidth();
                if ((mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE)
                    contentWidth -= divider * (count - 1);
            }
        }
        final int childWidth = Math.max(0, contentWidth) / count;
        final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        final int size = Math.max(0,
                MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom);
        int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.getMode(heightMeasureSpec));
        int childHeight = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (childHeight == 0) {
                childHeight = child.getMeasuredHeight();
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            }
        }
        setMeasuredDimension(widthSize,
                resolveSize(Math.max(childHeight + paddingTop + paddingBottom,
                        suggestedMinimumHeight), heightMeasureSpec));
        mChildWidth = childWidth;
        mChildHeight = childHeight;
    }

    private boolean isShowingDividers() {
        return (mShowDividers != SHOW_DIVIDER_NONE) && (mDivider != null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int childWidth = mChildWidth;
        final int childHeight = mChildHeight;
        final boolean show = isShowingDividers();
        final int divider = show ? mDivider.getIntrinsicWidth() : 0;
        final int count = getChildCount();
        int start = paddingStart;
        if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
            start += divider;
        }
        final boolean middle = (mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (i == count - 1) {
                final int right;
                if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    right = getWidth() - ViewCompat.getPaddingEnd(this) - divider;
                else
                    right = getWidth() - ViewCompat.getPaddingEnd(this);
                child.layout(start, paddingTop, right, paddingTop + childHeight);
                break;
            } else {
                child.layout(start, paddingTop, start + childWidth, paddingTop + childHeight);
            }
            start += childWidth;
            if (count % 2 == 0 && i == (count / 2) - 1 && mCenter != null) {
                start += mCenter.getIntrinsicWidth();
                if (show && middle && mCenterAsItem)
                    start = start + divider + divider;
            } else {
                if (show && middle)
                    start += divider;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int count = getChildCount();
        if (mCenter == null && !isShowingDividers() && count <= 0)
            return;
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int childWidth = mChildWidth;
        final boolean show = isShowingDividers();
        final Drawable dd = mDivider;
        final int divider = show ? dd.getIntrinsicWidth() : 0;
        final int padding = mDividerPadding;
        int start = paddingStart;
        if (show)
            dd.setBounds(1, paddingTop + padding, divider + 1,
                    getHeight() - paddingBottom - padding);
        if (count == 1) {
            if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
                canvas.save();
                canvas.translate(start, 0);
                dd.draw(canvas);
                canvas.restore();
                start += divider;
            }
            start += childWidth;
            if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END) {
                canvas.save();
                canvas.translate(start, 0);
                dd.draw(canvas);
                canvas.restore();
            }
        } else {
            final boolean middle = (mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE;
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        start += divider;
                    }
                    start += childWidth;
                } else if (i == count - 1) {
                    if (show && middle) {
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        start += divider;
                    }
                    start += childWidth;
                    if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END) {
                        start = getWidth() - ViewCompat.getPaddingEnd(this) - divider;
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        break;
                    }
                } else {
                    if (count % 2 == 0 && count / 2 == i && mCenter != null) {
                        if (show && middle && mCenterAsItem) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                        final Drawable center = mCenter;
                        final int p = mCenterPadding;
                        center.setBounds(0, paddingTop + p, center.getIntrinsicWidth(),
                                getHeight() - paddingBottom - p);
                        canvas.save();
                        canvas.translate(start, 0);
                        center.draw(canvas);
                        canvas.restore();
                        start += center.getIntrinsicWidth();
                        if (show && middle && mCenterAsItem) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                    } else {
                        if (show && middle) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                    }
                    start += childWidth;
                }
            }
        }
    }

    /**
     * 获取子项宽度（为了填充满，由于像素无法均分的问题，最后一个子项可能宽一点点或者窄一点点）
     *
     * @return 宽度
     */
    protected int getChildWidth() {
        return mChildWidth;
    }

    /**
     * 获取子项高度
     *
     * @return 高度
     */
    public int getChildHeight() {
        return mChildHeight;
    }

    /**
     * 获取分割显示方式
     *
     * @return 显示方式
     */
    @DividerMode
    protected int getShowDividers() {
        return mShowDividers;
    }

    /**
     * 设置分割显示方式
     *
     * @param showDividers 显示方式
     */
    protected void setShowDividers(@DividerMode int showDividers) {
        if (showDividers == mShowDividers) {
            return;
        }
        mShowDividers = showDividers;
        setWillDraw();
        requestLayout();
    }

    /**
     * 获取分割图片
     *
     * @return 图片
     */
    protected Drawable getDividerDrawable() {
        return mDivider;
    }

    /**
     * 设置分割图片
     *
     * @param divider 图片
     */
    protected void setDividerDrawable(Drawable divider) {
        if (divider == mDivider) {
            return;
        }
        mDivider = divider;
        setWillDraw();
        requestLayout();
    }

    /**
     * 获取分割两端间距
     *
     * @return 间距
     */
    protected int getDividerPadding() {
        return mDividerPadding;
    }

    /**
     * 设置分割两端间距
     *
     * @param padding 间距
     */
    protected void setDividerPadding(int padding) {
        if (padding == mDividerPadding) {
            return;
        }
        mDividerPadding = padding;
        if (isShowingDividers()) {
            invalidate();
        }
    }

    /**
     * 获取中间图片
     *
     * @return 图片
     */
    protected Drawable getCenterDrawable() {
        return mCenter;
    }

    /**
     * 设置中间图片
     *
     * @param center 图片
     */
    protected void setCenterDrawable(Drawable center) {
        if (center == mCenter) {
            return;
        }
        mCenter = center;
        setWillDraw();
        requestLayout();
    }

    /**
     * 判断中间图片是否作为子项（中间模式下，控制分割是否绘制在其两边）
     *
     * @return 是否作为
     */
    protected boolean isCenterAsItem() {
        return mCenterAsItem;
    }

    /**
     * 设置中间图片是否作为子项（中间模式下，控制分割是否绘制在其两边）
     *
     * @param centerAsItem 是否作为
     */
    protected void setCenterAsItem(boolean centerAsItem) {
        if (mCenterAsItem == centerAsItem)
            return;
        mCenterAsItem = centerAsItem;
        requestLayout();
    }

    /**
     * 获取中间图片两端间距
     *
     * @return 间距
     */
    protected int getCenterPadding() {
        return mCenterPadding;
    }

    /**
     * 设置中间图片两端间距
     *
     * @param padding 间距
     */
    protected void setCenterPadding(int padding) {
        if (padding == mCenterPadding) {
            return;
        }
        mCenterPadding = padding;
        if (mCenter != null) {
            invalidate();
        }
    }

    @SuppressWarnings("all")
    @IntDef(flag = true,
            value = {
                    SHOW_DIVIDER_NONE,
                    SHOW_DIVIDER_BEGINNING,
                    SHOW_DIVIDER_MIDDLE,
                    SHOW_DIVIDER_END
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }
}
