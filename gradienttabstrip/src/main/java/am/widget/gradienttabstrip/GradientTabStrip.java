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

package am.widget.gradienttabstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

import am.widget.tabstrip.TabStripDotAdapter;

/**
 * 渐变TabStrip，子项不宜过多
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class GradientTabStrip extends BaseTabStripViewGroup<GradientTabStripItem> {

    private static final int DEFAULT_TEXT_SIZE = 12;// 默认字体大小dp
    private static final int DEFAULT_TEXT_COLOR_NORMAL = Color.DKGRAY;// 默认字体默认颜色
    private static final int DEFAULT_TEXT_COLOR_SELECTED = Color.BLACK;// 默认字体选中颜色
    private static final int DEFAULT_DOT_MARGIN = 16;// 默认小圆点距离中心距离
    private static final int DEFAULT_DOT_BACKGROUND_COLOR = Color.RED;
    private static final int DEFAULT_DOT_BACKGROUND_SIZE = 10;
    private static final int DEFAULT_DOT_TEXT_SIZE = 10;
    private static final int DEFAULT_DOT_TEXT_COLOR = Color.WHITE;
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor, android.R.attr.divider, android.R.attr.showDividers,
            android.R.attr.dividerPadding};
    private int mPosition = 0;
    private float mOffset = 0;
    private Adapter mAdapter;
    private int mItemBackgroundId;// 子项背景资源ID
    private Drawable mItemBackgroundDrawable; // 子项背景图
    private float mTextSize;// 文字大小
    private int mTextColorNormal;// 文字默认颜色
    private int mTextColorSelected;// 文字选中颜色
    private int mDrawablePadding;// 图文间距
    private float mDotCenterToViewCenterX;// 小圆点中心距离View中心X轴距离（以中心点为直角坐标系原点）
    private float mDotCenterToViewCenterY;// 小圆点中心距离View中心Y轴距离（以中心点为直角坐标系原点）
    private boolean mDotCanGoOutside;// 小圆点是否可绘制到视图外部
    private boolean mDotAutoChangeWidth;// 小圆点是否自动修改宽度（宽度小于高度时调整宽度，使其为圆点）
    private Drawable mDotBackground;// 小圆点背景图
    private float mDotTextSize;// 小圆点文字大小
    private int mDotTextColor;// 小圆点文字颜色

    public GradientTabStrip(Context context) {
        super(context);
        initView(context, null);
    }

    public GradientTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GradientTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        final float density = getResources().getDisplayMetrics().density;
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.GradientTabStrip);
        final Drawable divider = custom.getDrawable(R.styleable.GradientTabStrip_gtsDivider);
        final int showDividers = custom.getInt(R.styleable.GradientTabStrip_gtsShowDividers,
                SHOW_DIVIDER_NONE);
        final int dividerPadding = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsDividerPadding, 0);
        final Drawable center = custom.getDrawable(R.styleable.GradientTabStrip_gtsCenterInterval);
        final boolean centerAsItem = custom.getBoolean(
                R.styleable.GradientTabStrip_gtsCenterIntervalAsItem, false);
        final int centerPadding = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsCenterIntervalPadding, 0);
        final boolean smoothScroll = custom.getBoolean(
                R.styleable.GradientTabStrip_gtsClickSmoothScroll, false);
        mItemBackgroundId = custom.getResourceId(R.styleable.GradientTabStrip_gtsItemBackground,
                NO_ID);
        mTextSize = custom.getDimension(R.styleable.GradientTabStrip_gtsTextSize,
                DEFAULT_TEXT_SIZE * density);
        mTextColorNormal = custom.getColor(R.styleable.GradientTabStrip_gtsTextColorNormal,
                DEFAULT_TEXT_COLOR_NORMAL);
        mTextColorSelected = custom.getColor(R.styleable.GradientTabStrip_gtsTextColorSelected,
                DEFAULT_TEXT_COLOR_SELECTED);
        mDrawablePadding = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsDrawablePadding, 0);
        mDotCenterToViewCenterX = custom.getDimension(
                R.styleable.GradientTabStrip_gtsDotCenterToViewCenterX,
                DEFAULT_DOT_MARGIN * density);
        mDotCenterToViewCenterY = custom.getDimension(
                R.styleable.GradientTabStrip_gtsDotCenterToViewCenterY, -mDotCenterToViewCenterX);
        mDotCanGoOutside = custom.getBoolean(R.styleable.GradientTabStrip_gtsDotCanGoOutside,
                false);
        mDotAutoChangeWidth = custom.getBoolean(R.styleable.GradientTabStrip_gtsDotAutoChangeWidth,
                true);
        final Drawable background =
                custom.getDrawable(R.styleable.GradientTabStrip_gtsDotBackground);
        mDotTextSize = custom.getDimension(R.styleable.GradientTabStrip_gtsDotTextSize,
                DEFAULT_DOT_TEXT_SIZE * density);
        mDotTextColor = custom.getColor(R.styleable.GradientTabStrip_gtsDotTextColor,
                DEFAULT_DOT_TEXT_COLOR);
        custom.recycle();
        initView(divider, showDividers, dividerPadding, center, centerAsItem, centerPadding);
        setSmoothScroll(smoothScroll);
        mDotBackground = background == null ? getDefaultDotBackground() : background;
    }

    private Drawable getDefaultDotBackground() {
        final GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(1000);
        background.setColor(DEFAULT_DOT_BACKGROUND_COLOR);
        final int size = Math.round(DEFAULT_DOT_BACKGROUND_SIZE *
                getResources().getDisplayMetrics().density);
        background.setSize(size, size);
        return background;
    }

    private Drawable getPreviewDrawable(int color) {
        final float density = getResources().getDisplayMetrics().density;
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(Math.round(2 * density), color);
        final int size = Math.round(24 * density);
        drawable.setSize(size, size);
        return drawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode() && getChildCount() == 0) {
            final int count = 4;
            for (int i = 0; i < count; i++) {
                GradientTabStripItem item = onCreateView();
                addViewInLayout(item, -1, generateDefaultLayoutParams());
                final CharSequence title = "Tab " + i;
                final String dot;
                if (i == 0)
                    dot = "999";
                else if (i == 1)
                    dot = "1";
                else if (i == 2)
                    dot = "";
                else
                    dot = null;
                final Drawable selected;
                item.set(title, dot, getPreviewDrawable(mTextColorNormal),
                        getPreviewDrawable(mTextColorSelected), i == 0 ? 1 : 0);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {
        if (mPosition == position && offset == mOffset)
            return;
        mPosition = position;
        mOffset = offset;
        notifyItemChanged();
    }

    @Override
    protected void onObservableChangeNotified(int id, int position, @Nullable Object tag) {
        super.onObservableChangeNotified(id, position, tag);
        if (id == Adapter.ID_DOT) {
            // 更新小圆点
            if (position < 0) {
                notifyItemChanged();
            } else {
                notifyItemChanged(position);
            }
        } else if (id == Adapter.ID_DRAWABLE) {
            // 更新图片
            if (position < 0) {
                notifyItemChanged();
            } else {
                notifyItemChanged(position);
            }
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected GradientTabStripItem onCreateView() {
        final GradientTabStripItem item = new GradientTabStripItem(getContext());
        setItemBackground(item);
        setTextSize(item);
        setTextColor(item);
        setDrawablePadding(item);
        setDotCenterToViewCenter(item);
        setDotCanGoOutside(item);
        setDotAutoChangeWidth(item);
        setDotBackground(item);
        setDotTextSize(item);
        setDotTextColor(item);
        return item;
    }

    private void setItemBackground(GradientTabStripItem item) {
        if (mItemBackgroundId != NO_ID) {
            item.setBackgroundResource(mItemBackgroundId);
        } else {
            if (mItemBackgroundDrawable != null) {
                final Drawable.ConstantState state = mItemBackgroundDrawable.getConstantState();
                Drawable background;
                if (state != null)
                    background = state.newDrawable(getResources()).mutate();
                else
                    background = mItemBackgroundDrawable;
                item.setBackgroundDrawable(background);
            }
        }
    }

    private void setTextSize(GradientTabStripItem item) {
        item.setTextSize(mTextSize);
    }

    private void setTextColor(GradientTabStripItem item) {
        item.setTextColor(mTextColorNormal, mTextColorSelected);
    }

    private void setDrawablePadding(GradientTabStripItem item) {
        item.setDrawablePadding(mDrawablePadding);
    }

    private void setDotCenterToViewCenter(GradientTabStripItem item) {
        item.setDotCenterToViewCenter(mDotCenterToViewCenterX, mDotCenterToViewCenterY);
    }

    private void setDotCanGoOutside(GradientTabStripItem item) {
        item.setDotCanGoOutside(mDotCanGoOutside);
    }

    private void setDotAutoChangeWidth(GradientTabStripItem item) {
        item.setDotAutoChangeWidth(mDotAutoChangeWidth);
    }

    private void setDotBackground(GradientTabStripItem item) {
        item.setDotBackground(mDotBackground);
    }

    private void setDotTextSize(GradientTabStripItem item) {
        item.setDotTextSize(mDotTextSize);
    }

    private void setDotTextColor(GradientTabStripItem item) {
        item.setDotTextColor(mDotTextColor);
    }

    @Override
    protected void onBindView(GradientTabStripItem item, int position) {
        final int count = getPageCount();
        final CharSequence title = getPageTitle(position);
        final String dot;
        final Drawable normal;
        final Drawable selected;
        if (mAdapter == null) {
            dot = null;
            normal = null;
            selected = null;
        } else {
            dot = mAdapter.getDotText(position, count);
            normal = mAdapter.getDrawableNormal(position, count);
            selected = mAdapter.getDrawableSelected(position, count);
        }
        final float offset;
        if (mOffset == 0) {
            offset = position == mPosition ? 1 : 0;
        } else {
            if (position == mPosition) {
                offset = 1 - mOffset;
            } else if (position == mPosition + 1) {
                offset = mOffset;
            } else {
                offset = 0;
            }
        }
        item.set(title, dot, normal, selected, offset);
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        setObservable(adapter);
        requestLayout();
        invalidate();
    }

    /**
     * 获取子项背景图
     *
     * @return 背景图
     */
    public Drawable getItemBackground() {
        if (mItemBackgroundId != NO_ID)
            return ContextCompat.getDrawable(getContext(), mItemBackgroundId);
        return mItemBackgroundDrawable;
    }

    /**
     * 设置子项背景
     *
     * @param resid 背景资源
     */
    public void setItemBackground(@DrawableRes int resid) {
        if (resid != NO_ID && resid == mItemBackgroundId)
            return;
        mItemBackgroundId = resid;
        mItemBackgroundDrawable = null;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setItemBackground(getChildAt(i));
        }
    }

    /**
     * 设置子项背景
     *
     * @param background 背景图
     */
    public void setItemBackground(Drawable background) {
        if (background != null && background == mItemBackgroundDrawable)
            return;
        mItemBackgroundId = NO_ID;
        mItemBackgroundDrawable = background;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setItemBackground(getChildAt(i));
        }
    }

    /**
     * 获取文字大小
     *
     * @return 文字大小
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(float size) {
        if (mTextSize == size)
            return;
        mTextSize = size;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setTextSize(getChildAt(i));
        }
        requestLayout();
    }

    /**
     * 获取文字默认颜色
     *
     * @return 颜色
     */
    public int getTextColorNormal() {
        return mTextColorNormal;
    }

    /**
     * 获取文字选中颜色
     *
     * @return 颜色
     */
    public int getTextColorSelected() {
        return mTextColorSelected;
    }

    /**
     * 设置文本颜色
     *
     * @param normal   默认颜色
     * @param selected 选中颜色
     */
    public void setTextColor(@ColorInt int normal, @ColorInt int selected) {
        if (mTextColorNormal == normal && mTextColorSelected == selected)
            return;
        mTextColorNormal = normal;
        mTextColorSelected = selected;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setTextColor(getChildAt(i));
        }
    }

    /**
     * 获取图文间隔
     *
     * @return 间隔
     */
    public int getDrawablePadding() {
        return mDrawablePadding;
    }

    /**
     * 设置图文间隔
     *
     * @param padding 间隔
     */
    public void setDrawablePadding(int padding) {
        if (padding == mDrawablePadding)
            return;
        mDrawablePadding = padding;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDrawablePadding(getChildAt(i));
        }
        requestLayout();
    }

    /**
     * 小圆点中心距离View中心X轴距离（以中心点为直角坐标系原点）
     *
     * @return 距离
     */
    public float getDotCenterToViewCenterX() {
        return mDotCenterToViewCenterX;
    }

    /**
     * 获取小圆点距离中心Y轴距离（以中心点为直角坐标系原点）
     *
     * @return 距离
     */
    public float getDotCenterToViewCenterY() {
        return mDotCenterToViewCenterY;
    }

    /**
     * 小圆点中心距离View中心Y轴距离（以中心点为直角坐标系原点）
     *
     * @param x X轴距离
     * @param y Y轴距离
     */
    public void setDotCenterToViewCenter(float x, float y) {
        if (mDotCenterToViewCenterX == x && mDotCenterToViewCenterY == y)
            return;
        mDotCenterToViewCenterX = x;
        mDotCenterToViewCenterY = y;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotCenterToViewCenter(getChildAt(i));
        }
    }

    /**
     * 判断小圆点是否可绘制到子项外部
     *
     * @return 是否可以
     */
    public boolean isDotCanGoOutside() {
        return mDotCanGoOutside;
    }

    /**
     * 设置小圆点是否可绘制到子项外部
     *
     * @param can 是否可以
     */
    public void setDotCanGoOutside(boolean can) {
        if (mDotCanGoOutside == can)
            return;
        mDotCanGoOutside = can;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotCanGoOutside(getChildAt(i));
        }
    }

    /**
     * 判断小圆点是否自动修改宽度（宽度小于高度时调整宽度，使其为圆点）
     *
     * @return 是否自动
     */
    public boolean isDotAutoChangeWidth() {
        return mDotAutoChangeWidth;
    }

    /**
     * 设置小圆点是否自动修改宽度（宽度小于高度时调整宽度，使其为圆点）
     *
     * @param auto 是否自动
     */
    public void setDotAutoChangeWidth(boolean auto) {
        if (mDotAutoChangeWidth == auto)
            return;
        mDotAutoChangeWidth = auto;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotAutoChangeWidth(getChildAt(i));
        }
    }

    /**
     * 获取小圆点背景图
     *
     * @return 背景图
     */
    public Drawable getDotBackground() {
        return mDotBackground;
    }

    /**
     * 设置小圆点背景图
     *
     * @param background 背景图
     */
    public void setDotBackground(Drawable background) {
        if (mDotBackground == background)
            return;
        mDotBackground = background;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotBackground(getChildAt(i));
        }
    }

    /**
     * 设置小圆点背景图
     *
     * @param resid 背景资源
     */
    public void setDotBackground(@DrawableRes int resid) {
        setDotBackground(ContextCompat.getDrawable(getContext(), resid));
    }

    /**
     * 获取小圆点文字大小
     *
     * @return 文字大小
     */
    public float getDotTextSize() {
        return mDotTextSize;
    }

    /**
     * 设置小圆点文字大小
     *
     * @param size 文字大小
     */
    public void setDotTextSize(float size) {
        if (mDotTextSize == size)
            return;
        mDotTextSize = size;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotTextSize(getChildAt(i));
        }
    }

    /**
     * 获取小圆点文字颜色
     *
     * @return 颜色
     */
    public int getDotTextColor() {
        return mDotTextColor;
    }

    /**
     * 设置小圆点文字颜色
     *
     * @param color 颜色
     */
    public void setDotTextColor(int color) {
        if (mDotTextColor == color)
            return;
        mDotTextColor = color;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotTextColor(getChildAt(i));
        }
    }

    /**
     * Adapter
     */
    public static abstract class Adapter extends TabStripDotAdapter {

        private static final int ID_DOT = 0;
        private static final int ID_DRAWABLE = 1;

        @Override
        protected int getDotNotifyId() {
            return ID_DOT;
        }

        /**
         * 获取普通状态下的 Drawable
         *
         * @param position 位置
         * @param count    总数
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableNormal(int position, int count);

        /**
         * 获取选中状态下的 Drawable
         *
         * @param position 位置
         * @param count    总数
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableSelected(int position, int count);

        /**
         * 通知Drawable已改变
         */
        public void notifyDrawableChanged() {
            notifyChanged(ID_DRAWABLE, PagerAdapter.POSITION_NONE, null);
        }

        /**
         * 通知Drawable已改变
         *
         * @param position 位置
         */
        public void notifyDrawableChanged(int position) {
            notifyChanged(ID_DRAWABLE, position, null);
        }
    }
}