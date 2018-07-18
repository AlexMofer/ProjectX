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

package am.widget.indicatortabstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import am.widget.tabstrip.DotDrawable;
import am.widget.tabstrip.HorizontalLinearTabStripViewGroup;
import am.widget.tabstrip.TabStripDotAdapter;


/**
 * 游标TabStrip，子项不宜过多
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class IndicatorTabStrip extends HorizontalLinearTabStripViewGroup<IndicatorTabStripItem> {

    private static final int DEFAULT_TEXT_SIZE = 14;// 默认字体大小dp
    private static final int DEFAULT_TEXT_COLOR_NORMAL = Color.DKGRAY;// 默认字体默认颜色
    private static final int DEFAULT_TEXT_COLOR_SELECTED = Color.BLACK;// 默认字体选中颜色
    private static final int DEFAULT_DOT_MARGIN = 16;// 默认小圆点距离中心距离
    private static final int DEFAULT_DOT_BACKGROUND_COLOR = Color.RED;
    private static final int DEFAULT_DOT_BACKGROUND_SIZE = 10;
    private static final int DEFAULT_DOT_BACKGROUND_PADDING = 3;
    private static final int DEFAULT_DOT_TEXT_SIZE = 10;
    private static final int DEFAULT_DOT_TEXT_COLOR = Color.WHITE;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mPosition = 0;
    private float mOffset = 0;
    private Adapter mAdapter;
    private int mItemBackgroundId;// 子项背景资源ID
    private Drawable mItemBackgroundDrawable; // 子项背景图
    private int mItemColorBackgroundNormal;// 默认颜色背景
    private float mTextSize;// 文字大小
    private int mItemColorBackgroundSelected;// 选中颜色背景
    private int mTextColorNormal;// 文字默认颜色
    private float mTextScale;// 选中文字缩放
    private int mTextColorSelected;// 文字选中颜色
    private float mDotCenterToViewCenterX;// 小圆点中心距离View中心X轴距离（以中心点为直角坐标系原点）
    private float mDotCenterToViewCenterY;// 小圆点中心距离View中心Y轴距离（以中心点为直角坐标系原点）
    private boolean mDotCanGoOutside;// 小圆点是否可绘制到视图外部
    private boolean mDotAutoChangeWidth;// 小圆点是否自动修改宽度（宽度小于高度时调整宽度，使其为圆点）
    private Drawable mDotBackground;// 小圆点背景图
    private float mDotTextSize;// 小圆点文字大小
    private int mDotTextColor;// 小圆点文字颜色
    private int mUnderlineColor;// 下划线颜色
    private int mUnderlineHeight;// 下划线高度
    private int mUnderlinePadding;// 下划线两端间距
    private int mIndicatorHeight;// 游标高度
    private int mIndicatorColor;// 游标颜色
    private int mIndicatorPadding;// 游标两端间距

    public IndicatorTabStrip(Context context) {
        super(context);
        initView(context, null);
    }

    public IndicatorTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public IndicatorTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        final float density = getResources().getDisplayMetrics().density;
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.IndicatorTabStrip);
        final Drawable divider = custom.getDrawable(R.styleable.IndicatorTabStrip_itsDivider);
        final int showDividers = custom.getInt(R.styleable.IndicatorTabStrip_itsShowDividers,
                SHOW_DIVIDER_NONE);
        final int dividerPadding = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_itsDividerPadding, 0);
        final boolean smoothScroll = custom.getBoolean(
                R.styleable.IndicatorTabStrip_itsClickSmoothScroll, true);
        mItemBackgroundId = custom.getResourceId(R.styleable.IndicatorTabStrip_itsItemBackground,
                NO_ID);
        mItemColorBackgroundNormal = custom.getColor(
                R.styleable.IndicatorTabStrip_itsItemColorBackgroundNormal, 0);
        mItemColorBackgroundSelected = custom.getColor(
                R.styleable.IndicatorTabStrip_itsItemColorBackgroundSelected, 0);
        mTextSize = custom.getDimension(R.styleable.IndicatorTabStrip_itsTextSize,
                DEFAULT_TEXT_SIZE * density);
        mTextColorNormal = custom.getColor(R.styleable.IndicatorTabStrip_itsTextColorNormal,
                DEFAULT_TEXT_COLOR_NORMAL);
        mTextColorSelected = custom.getColor(R.styleable.IndicatorTabStrip_itsTextColorSelected,
                DEFAULT_TEXT_COLOR_SELECTED);
        mTextScale = custom.getFloat(R.styleable.IndicatorTabStrip_itsTextScale, 1f);
        mDotCenterToViewCenterX = custom.getDimension(
                R.styleable.IndicatorTabStrip_itsDotCenterToViewCenterX,
                DEFAULT_DOT_MARGIN * density);
        mDotCenterToViewCenterY = custom.getDimension(
                R.styleable.IndicatorTabStrip_itsDotCenterToViewCenterY,
                -DEFAULT_DOT_MARGIN * density);
        mDotCanGoOutside = custom.getBoolean(R.styleable.IndicatorTabStrip_itsDotCanGoOutside,
                false);
        mDotAutoChangeWidth = custom.getBoolean(R.styleable.IndicatorTabStrip_itsDotAutoChangeWidth,
                true);
        final int color = custom.getColor(R.styleable.IndicatorTabStrip_itsDotColor,
                DEFAULT_DOT_BACKGROUND_COLOR);
        final Drawable background =
                custom.getDrawable(R.styleable.IndicatorTabStrip_itsDotBackground);
        mDotTextSize = custom.getDimension(R.styleable.IndicatorTabStrip_itsDotTextSize,
                DEFAULT_DOT_TEXT_SIZE * density);
        mDotTextColor = custom.getColor(R.styleable.IndicatorTabStrip_itsDotTextColor,
                DEFAULT_DOT_TEXT_COLOR);
        mUnderlineColor = custom.getColor(R.styleable.IndicatorTabStrip_itsUnderlineColor,
                Color.TRANSPARENT);
        mUnderlineHeight = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_itsUnderlineHeight, 0);
        mUnderlinePadding = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_itsUnderlinePadding, 0);
        mIndicatorColor = custom.getColor(R.styleable.IndicatorTabStrip_itsIndicatorColor,
                Color.TRANSPARENT);
        mIndicatorHeight = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_itsIndicatorHeight, 0);
        mIndicatorPadding = custom.getDimensionPixelOffset(
                R.styleable.IndicatorTabStrip_itsIndicatorPadding, 0);
        custom.recycle();
        initView(divider, showDividers, dividerPadding, null, false,
                0);
        setItemClickSmoothScroll(smoothScroll);
        mDotBackground = background == null ? getDefaultDotBackground(color) : background;
        setWillDraw();
    }

    private void setWillDraw() {
        if (willNotDraw() && ((mUnderlineColor != Color.TRANSPARENT && mUnderlineHeight > 0) ||
                (mIndicatorColor != Color.TRANSPARENT && mIndicatorHeight > 0)))
            setWillNotDraw(false);
    }

    private Drawable getDefaultDotBackground(int color) {
        final float density = getResources().getDisplayMetrics().density;
        final int size = Math.round(DEFAULT_DOT_BACKGROUND_SIZE * density);
        final int padding = Math.round(DEFAULT_DOT_BACKGROUND_PADDING * density);
        return new DotDrawable(color, size, size, padding, 0, padding, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode() && getChildCount() == 0) {
            final int count = 4;
            for (int i = 0; i < count; i++) {
                IndicatorTabStripItem item = onCreateView();
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
                item.set(title, dot, i == 0 ? 1 : 0);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < 23) {
            drawThings(canvas);
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        drawThings(canvas);
        super.onDrawForeground(canvas);
    }

    private void drawThings(Canvas canvas) {
        final int viewHeight = getHeight();
        final int paddingBottom = getPaddingBottom();
        if (mUnderlineColor != Color.TRANSPARENT && mUnderlineHeight > 0) {
            final int left = getPaddingLeft() + mUnderlinePadding;
            final int top = viewHeight - paddingBottom - mUnderlineHeight;
            final int right = getWidth() - getPaddingRight() - mUnderlinePadding;
            final int bottom = viewHeight - paddingBottom;
            mPaint.setColor(mUnderlineColor);
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
        if (mIndicatorColor != Color.TRANSPARENT && mIndicatorHeight > 0) {
            final int width = getChildWidth() - mIndicatorPadding - mIndicatorPadding;
            final int top = viewHeight - paddingBottom - mUnderlineHeight - mIndicatorHeight;
            final float left;
            if (mOffset == 0) {
                if (getChildCount() <= mPosition)
                    return;
                final IndicatorTabStripItem item = getChildAtRaw(mPosition);
                if (item == null)
                    return;
                left = item.getLeft() + (item.getWidth() - width) * 0.5f;
            } else if (mOffset == 1) {
                if (getChildCount() <= mPosition + 1)
                    return;
                final IndicatorTabStripItem next = getChildAtRaw(mPosition + 1);
                if (next == null)
                    return;
                left = next.getLeft() + (next.getWidth() - width) * 0.5f;
            } else {
                if (getChildCount() <= mPosition && getChildCount() <= mPosition + 1)
                    return;
                final IndicatorTabStripItem item = getChildAtRaw(mPosition);
                final IndicatorTabStripItem next = getChildAtRaw(mPosition + 1);
                if (item == null || next == null)
                    return;
                final int offset = next.getLeft() - item.getLeft();
                left = item.getLeft() + (item.getWidth() - width) * 0.5f + offset * mOffset;
            }
            mPaint.setColor(mIndicatorColor);
            canvas.save();
            canvas.translate(left, top);
            canvas.drawRect(0, 0, width, mIndicatorHeight, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {
        if (mPosition == position && offset == mOffset)
            return;
        mPosition = position;
        mOffset = offset;
        notifyItemChanged();
        invalidate();
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
        }
    }

    @Override
    protected IndicatorTabStripItem onCreateView() {
        final IndicatorTabStripItem item = new IndicatorTabStripItem(getContext());
        setItemBackground(item);
        setItemColorBackground(item);
        setTextSize(item);
        setTextColor(item);
        setTextScale(item);
        setDotCenterToViewCenter(item);
        setDotCanGoOutside(item);
        setDotAutoChangeWidth(item);
        setDotBackground(item);
        setDotTextSize(item);
        setDotTextColor(item);
        return item;
    }

    private void setItemBackground(IndicatorTabStripItem item) {
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

    private void setItemColorBackground(IndicatorTabStripItem item) {
        item.setColorBackground(mItemColorBackgroundNormal, mItemColorBackgroundSelected);
    }

    private void setTextSize(IndicatorTabStripItem item) {
        item.setTextSize(mTextSize);
    }

    private void setTextColor(IndicatorTabStripItem item) {
        item.setTextColor(mTextColorNormal, mTextColorSelected);
    }

    private void setTextScale(IndicatorTabStripItem item) {
        item.setTextScale(mTextScale);
    }

    private void setDotCenterToViewCenter(IndicatorTabStripItem item) {
        item.setDotCenterToViewCenter(mDotCenterToViewCenterX, mDotCenterToViewCenterY);
    }

    private void setDotCanGoOutside(IndicatorTabStripItem item) {
        item.setDotCanGoOutside(mDotCanGoOutside);
    }

    private void setDotAutoChangeWidth(IndicatorTabStripItem item) {
        item.setDotAutoChangeWidth(mDotAutoChangeWidth);
    }

    private void setDotBackground(IndicatorTabStripItem item) {
        item.setDotBackground(mDotBackground);
    }

    private void setDotTextSize(IndicatorTabStripItem item) {
        item.setDotTextSize(mDotTextSize);
    }

    private void setDotTextColor(IndicatorTabStripItem item) {
        item.setDotTextColor(mDotTextColor);
    }

    @Override
    protected void onBindView(IndicatorTabStripItem item, int position) {
        final int count = getPageCount();
        final CharSequence title = getPageTitle(position);
        final String dot;
        if (mAdapter == null) {
            dot = null;
        } else {
            dot = mAdapter.getDotText(position, count);
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
        item.set(title, dot, offset);
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
            setItemBackground(getChildAtRaw(i));
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
            setItemBackground(getChildAtRaw(i));
        }
    }

    /**
     * 获取默认颜色背景颜色
     *
     * @return 颜色
     */
    @ColorInt
    public int getItemColorBackgroundNormal() {
        return mItemColorBackgroundNormal;
    }

    /**
     * 获取选中颜色背景颜色
     *
     * @return 颜色
     */
    @ColorInt
    public int getItemColorBackgroundSelected() {
        return mItemColorBackgroundSelected;
    }

    /**
     * 设置颜色背景
     *
     * @param normal   默认颜色
     * @param selected 选中颜色
     */
    public void setItemColorBackground(@ColorInt int normal, @ColorInt int selected) {
        if (mItemColorBackgroundNormal == normal && mItemColorBackgroundSelected == selected)
            return;
        mItemColorBackgroundNormal = normal;
        mItemColorBackgroundSelected = selected;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setItemColorBackground(getChildAtRaw(i));
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
            setTextSize(getChildAtRaw(i));
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
            setTextColor(getChildAtRaw(i));
        }
    }

    /**
     * 获取文字缩放比
     *
     * @return 文字缩放比
     */
    public float getTextScale() {
        return mTextScale;
    }

    /**
     * 设置文字缩放比
     *
     * @param scale 文字缩放比
     */
    public void setTextScale(float scale) {
        if (mTextScale == scale)
            return;
        mTextScale = scale;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setTextScale(getChildAtRaw(i));
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
            setDotCenterToViewCenter(getChildAtRaw(i));
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
            setDotCanGoOutside(getChildAtRaw(i));
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
            setDotAutoChangeWidth(getChildAtRaw(i));
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
            setDotBackground(getChildAtRaw(i));
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
     * 设置小圆点背景图为默认背景
     *
     * @param color 颜色
     */
    public void setDotBackgroundUseDefault(int color) {
        mDotBackground = getDefaultDotBackground(color);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            setDotBackground(getChildAtRaw(i));
        }
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
            setDotTextSize(getChildAtRaw(i));
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
            setDotTextColor(getChildAtRaw(i));
        }
    }

    /**
     * 获取下划线颜色
     *
     * @return 颜色
     */
    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    /**
     * 设置下划线颜色
     *
     * @param color 颜色
     */
    public void setUnderlineColor(@ColorInt int color) {
        if (mUnderlineColor == color)
            return;
        mUnderlineColor = color;
        setWillDraw();
        invalidate();
    }

    /**
     * 获取下划线高度
     *
     * @return 高度
     */
    public int getUnderlineHeight() {
        return mUnderlineHeight;
    }

    /**
     * 设置下划线高度
     *
     * @param height 高度
     */
    public void setUnderlineHeight(int height) {
        if (mUnderlineHeight == height)
            return;
        mUnderlineHeight = height;
        setWillDraw();
        invalidate();
    }

    /**
     * 获取下划线两端间距
     *
     * @return 间距
     */
    public int getUnderlinePadding() {
        return mUnderlinePadding;
    }

    /**
     * 设置下划线两端间距
     *
     * @param padding 间距
     */
    public void setUnderlinePadding(int padding) {
        if (mUnderlinePadding == padding)
            return;
        mUnderlinePadding = padding;
        invalidate();
    }

    /**
     * 获取游标颜色
     *
     * @return 颜色
     */
    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    /**
     * 设置游标颜色
     *
     * @param color 颜色
     */
    public void setIndicatorColor(@ColorInt int color) {
        if (mIndicatorColor == color)
            return;
        mIndicatorColor = color;
        setWillDraw();
        invalidate();
    }

    /**
     * 获取游标高度
     *
     * @return 高度
     */
    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    /**
     * 设置游标高度
     *
     * @param height 高度
     */
    public void setIndicatorHeight(int height) {
        if (mIndicatorHeight == height)
            return;
        mIndicatorHeight = height;
        setWillDraw();
        invalidate();
    }

    /**
     * 获取游标两端间距
     *
     * @return 间距
     */
    public int getIndicatorPadding() {
        return mIndicatorPadding;
    }

    /**
     * 设置游标两端间距
     *
     * @param padding 间距
     */
    public void setIndicatorPadding(int padding) {
        if (mIndicatorPadding == padding)
            return;
        mIndicatorPadding = padding;
        invalidate();
    }

    @Override
    @DividerMode
    public int getShowDividers() {
        return super.getShowDividers();
    }

    @Override
    public void setShowDividers(@DividerMode int showDividers) {
        super.setShowDividers(showDividers);
    }

    @Override
    public Drawable getDividerDrawable() {
        return super.getDividerDrawable();
    }

    @Override
    public void setDividerDrawable(Drawable divider) {
        super.setDividerDrawable(divider);
    }

    @Override
    public int getDividerPadding() {
        return super.getDividerPadding();
    }

    @Override
    public void setDividerPadding(int padding) {
        super.setDividerPadding(padding);
    }

    @Override
    public boolean isItemClickSmoothScroll() {
        return super.isItemClickSmoothScroll();
    }

    @Override
    public void setItemClickSmoothScroll(boolean smoothScroll) {
        super.setItemClickSmoothScroll(smoothScroll);
    }

    /**
     * Adapter
     */
    public static abstract class Adapter extends TabStripDotAdapter {

        private static final int ID_DOT = 0;

        @Override
        protected int getDotNotifyId() {
            return ID_DOT;
        }
    }
}