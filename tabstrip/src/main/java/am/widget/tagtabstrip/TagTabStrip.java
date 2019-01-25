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

package am.widget.tagtabstrip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import am.widget.tabstrip.R;
import am.widget.tabstrip.TabStripView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;

/**
 * ViewPager 小点
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class TagTabStrip extends TabStripView {

    private final static int DEFAULT_SIZE = 8;// 默认图片dp
    private final static int DEFAULT_DRAWABLE_SELECTED = 0xff808080;
    private final static int DEFAULT_DRAWABLE_NORMAL = 0x80808080;
    private static final int[] ATTRS = new int[]{android.R.attr.gravity,
            android.R.attr.drawablePadding};
    private int mPosition = 0;
    private float mOffset = 0;
    private int mCount = 0;
    private Drawable mSelected;
    private Drawable mNormal;
    private float mScale = 1;
    private int mPadding;
    private int mGravity = Gravity.CENTER;
    private float mFirstCenterX;
    private float mFirstCenterY;
    private float mItemCenterOffset;

    public TagTabStrip(Context context) {
        super(context);
        initView(context, null);
    }

    public TagTabStrip(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TagTabStrip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        int gravity = Gravity.CENTER;
        int padding = 0;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    gravity = a.getInt(attr, gravity);
                    break;
                case 1:
                    padding = a.getDimensionPixelSize(attr, padding);
                    break;
            }
        }
        a.recycle();
        final TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.TagTabStrip);
        final int single = custom.getResourceId(R.styleable.TagTabStrip_ttsDrawable, NO_ID);
        final Drawable normal = custom.getDrawable(R.styleable.TagTabStrip_ttsDrawableNormal);
        final Drawable selected = custom.getDrawable(R.styleable.TagTabStrip_ttsDrawableSelected);
        padding = custom.getDimensionPixelSize(R.styleable.TagTabStrip_ttsDrawablePadding, padding);
        float scale = custom.getFloat(R.styleable.TagTabStrip_ttsScale, 1);
        custom.recycle();
        mScale = scale;
        mPadding = padding;
        mGravity = gravity;
        if (normal != null && selected != null) {
            mNormal = normal;
            mSelected = selected;
        } else if (normal != null) {
            mNormal = normal;
            mSelected = normal;
        } else if (selected != null) {
            mNormal = getDefaultDrawable(Color.TRANSPARENT);
            mSelected = selected;
        } else {
            if (single != NO_ID) {
                mNormal = ContextCompat.getDrawable(context, single);
                mSelected = ContextCompat.getDrawable(context, single);
                if (mSelected != null)
                    mSelected.setState(SELECTED_STATE_SET);
            }
            if (mNormal == null)
                mNormal = getDefaultDrawable(DEFAULT_DRAWABLE_NORMAL);
            if (mSelected == null)
                mSelected = getDefaultDrawable(DEFAULT_DRAWABLE_SELECTED);
        }
    }

    private Drawable getDefaultDrawable(int color) {
        final int size = (int) (getResources().getDisplayMetrics().density * DEFAULT_SIZE);
        final GradientDrawable mBackground = new GradientDrawable();
        mBackground.setShape(GradientDrawable.OVAL);
        mBackground.setColor(color);
        mBackground.setSize(size, size);
        return mBackground;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            mCount = 5;
            mPosition = 2;
        }
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int itemWidth = Math.max(mNormal.getIntrinsicWidth(),
                mSelected.getIntrinsicWidth());
        final int itemHeight = Math.max(mNormal.getIntrinsicHeight(),
                mSelected.getIntrinsicHeight());
        final int count = mCount;
        final int padding = mPadding;
        final float scale = mScale;
        final int width = count <= 0 ? 0 : count * itemWidth + padding * (count - 1) +
                (scale > 1 ? (int) Math.ceil(itemWidth * (scale - 1)) : 0);
        final int height = scale > 1 ? (int) Math.ceil(itemHeight * scale) : itemHeight;
        setMeasuredDimension(
                resolveSize(Math.max(width + paddingStart + paddingEnd, suggestedMinimumWidth),
                        widthMeasureSpec),
                resolveSize(Math.max(height + paddingTop + paddingBottom, suggestedMinimumHeight),
                        heightMeasureSpec));
        applyGravity(itemWidth, itemHeight);
    }

    @SuppressLint("RtlHardcoded")
    private void applyGravity(int itemWidth, int itemHeight) {
        final int count = mCount;
        if (count == 0) {
            mFirstCenterX = 0;
            mFirstCenterY = 0;
            mItemCenterOffset = 0;
            return;
        }
        final int padding = mPadding;
        final float scale = mScale > 1 ? mScale : 1;
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int contentWidth = width - paddingStart - paddingEnd;
        final int contentHeight = height - paddingTop - paddingBottom;
        mItemCenterOffset = itemWidth + padding;
        switch (GravityCompat.getAbsoluteGravity(mGravity,
                ViewCompat.getLayoutDirection(this))) {
            case Gravity.LEFT:
            case Gravity.TOP:
            case Gravity.LEFT | Gravity.TOP:
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = paddingTop + itemHeight * scale * 0.5f;
                break;
            case Gravity.CENTER_HORIZONTAL:
            case Gravity.CENTER_HORIZONTAL | Gravity.TOP:
                mFirstCenterX = paddingStart + contentWidth * 0.5f -
                        mItemCenterOffset * (count - 1) * 0.5f;
                mFirstCenterY = paddingTop + itemHeight * scale * 0.5f;
                break;
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
                mFirstCenterX = width - paddingEnd - itemWidth * scale * 0.5f -
                        mItemCenterOffset * (count - 1);
                mFirstCenterY = paddingTop + itemHeight * scale * 0.5f;
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = paddingTop + contentHeight * 0.5f;
                break;
            default:
            case Gravity.CENTER:
                mFirstCenterX = paddingStart + contentWidth * 0.5f -
                        mItemCenterOffset * (count - 1) * 0.5f;
                mFirstCenterY = paddingTop + contentHeight * 0.5f;
                break;
            case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                mFirstCenterX = width - paddingEnd - itemWidth * scale * 0.5f -
                        mItemCenterOffset * (count - 1);
                mFirstCenterY = paddingTop + contentHeight * 0.5f;
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Gravity.LEFT:
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = height - paddingBottom - itemHeight * scale * 0.5f;
                break;
            case Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM:
                mFirstCenterX = paddingStart + contentWidth * 0.5f -
                        mItemCenterOffset * (count - 1) * 0.5f;
                mFirstCenterY = height - paddingBottom - itemHeight * scale * 0.5f;
                break;
            case Gravity.RIGHT | Gravity.BOTTOM:
                mFirstCenterX = width - paddingEnd - itemWidth * scale * 0.5f -
                        mItemCenterOffset * (count - 1);
                mFirstCenterY = height - paddingBottom - itemHeight * scale * 0.5f;
                break;
            // 水平方向上沾满
            case Gravity.FILL:
                mItemCenterOffset = count == 1 ? 0 :
                        (contentWidth - itemWidth * scale) / (count - 1);
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = paddingTop + contentHeight * 0.5f;
                break;
            case Gravity.FILL_HORIZONTAL:
            case Gravity.FILL_HORIZONTAL | Gravity.TOP:
                mItemCenterOffset = count == 1 ? 0 :
                        (contentWidth - itemWidth * scale) / (count - 1);
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = paddingTop + itemHeight * scale * 0.5f;
                break;
            case Gravity.FILL_HORIZONTAL | Gravity.BOTTOM:
                mItemCenterOffset = count == 1 ? 0 :
                        (contentWidth - itemWidth * scale) / (count - 1);
                mFirstCenterX = paddingStart + itemWidth * scale * 0.5f;
                mFirstCenterY = height - paddingBottom - itemHeight * scale * 0.5f;
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int count = mCount;
        final int position = mPosition;
        final float offset = mOffset;
        final int anotherPosition = mOffset == 0 ? PagerAdapter.POSITION_NONE : mPosition + 1;
        final Drawable selected = mSelected;
        final Drawable normal = mNormal;
        selected.setBounds(0, 0, selected.getIntrinsicWidth(), selected.getIntrinsicHeight());
        normal.setBounds(0, 0, normal.getIntrinsicWidth(), normal.getIntrinsicHeight());
        canvas.save();
        canvas.translate(mFirstCenterX, mFirstCenterY);
        float dx;
        float dy;
        float scale;
        for (int i = 0; i < count; i++) {
            if (i == position) {
                if (offset == 0) {
                    scale = mScale;
                    dx = selected.getIntrinsicWidth() * 0.5f;
                    dy = selected.getIntrinsicHeight() * 0.5f;
                    selected.setAlpha(255);
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    selected.draw(canvas);
                    canvas.restore();
                } else {
                    scale = 1 + (mScale - 1) * (1 - offset);
                    dx = normal.getIntrinsicWidth() * 0.5f;
                    dy = normal.getIntrinsicHeight() * 0.5f;
                    normal.setAlpha((int) Math.ceil(0xFF * offset));
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    normal.draw(canvas);
                    canvas.restore();
                    dx = selected.getIntrinsicWidth() * 0.5f;
                    dy = selected.getIntrinsicHeight() * 0.5f;
                    selected.setAlpha((int) Math.ceil(0xFF * (1 - offset)));
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    selected.draw(canvas);
                    canvas.restore();
                }
            } else if (i == anotherPosition) {
                scale = 1 + (mScale - 1) * offset;
                dx = normal.getIntrinsicWidth() * 0.5f;
                dy = normal.getIntrinsicHeight() * 0.5f;
                normal.setAlpha((int) Math.ceil(0xFF * (1 - offset)));
                canvas.save();
                canvas.translate(-dx, -dy);
                canvas.scale(scale, scale, dx, dy);
                normal.draw(canvas);
                canvas.restore();
                dx = selected.getIntrinsicWidth() * 0.5f;
                dy = selected.getIntrinsicHeight() * 0.5f;
                selected.setAlpha((int) Math.ceil(0xFF * offset));
                canvas.save();
                canvas.translate(-dx, -dy);
                canvas.scale(scale, scale, dx, dy);
                selected.draw(canvas);
                canvas.restore();
            } else {
                dx = normal.getIntrinsicWidth() * 0.5f;
                dy = normal.getIntrinsicHeight() * 0.5f;
                normal.setAlpha(255);
                canvas.save();
                canvas.translate(-dx, -dy);
                normal.draw(canvas);
                canvas.restore();
            }
            canvas.translate(mItemCenterOffset, 0);
        }
        canvas.restore();
    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {
        if (mPosition == position && offset == mOffset)
            return;
        mPosition = position;
        mOffset = offset;
        invalidate();
    }

    @Override
    protected void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                             @Nullable PagerAdapter newAdapter) {
        super.onViewPagerAdapterChanged(oldAdapter, newAdapter);
        final int count = getPageCount();
        if (count == mCount)
            return;
        mCount = getPageCount();
        requestLayout();
        invalidate();
    }

    @Override
    protected void onViewPagerAdapterDataChanged() {
        super.onViewPagerAdapterDataChanged();
        final int count = getPageCount();
        if (count == mCount)
            return;
        mCount = getPageCount();
        requestLayout();
        invalidate();
    }

    /**
     * 获取排版方式
     *
     * @return 排版方式
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置排版方式
     *
     * @param gravity 排版方式
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        invalidate();
    }

    /**
     * 获取子项间距
     *
     * @return 子项间距
     */
    public int getDrawablePadding() {
        return mPadding;
    }

    /**
     * 设置子项间距
     *
     * @param padding 子项间距
     */
    public void setDrawablePadding(int padding) {
        mPadding = padding;
        requestLayout();
        invalidate();
    }

    /**
     * 获取选中子项缩放比
     *
     * @return 选中子项缩放比
     */
    public float getScale() {
        return mScale;
    }

    /**
     * 设置选中子项缩放比
     *
     * @param scale 选中子项缩放比
     */
    public void setScale(float scale) {
        if (scale > 0 && scale != mScale) {
            mScale = scale;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置图片
     *
     * @param normal   普通图
     * @param selected 选中图
     */
    public void setDrawables(Drawable normal, Drawable selected) {
        if (normal == null || selected == null)
            return;
        mNormal = normal;
        mSelected = selected;
        requestLayout();
        invalidate();
    }

    /**
     * 设置图片
     *
     * @param normal   普通图
     * @param selected 选中图
     */

    public void setDrawables(int normal, int selected) {
        setDrawables(ContextCompat.getDrawable(getContext(), normal),
                ContextCompat.getDrawable(getContext(), selected));
    }

    /**
     * 设置子项图片
     *
     * @param drawable 子项图片
     */
    public void setDrawable(int drawable) {
        final Drawable normal = ContextCompat.getDrawable(getContext(), drawable);
        final Drawable selected = ContextCompat.getDrawable(getContext(), drawable);
        if (selected != null)
            selected.setState(SELECTED_STATE_SET);
        setDrawables(normal, selected);
    }

    /**
     * 设置子项图片
     *
     * @param item 子项图片
     */
    public void setDrawable(Drawable item) {
        if (item == null)
            item = getDefaultDrawable(Color.TRANSPARENT);
        final Drawable.ConstantState state = item.getConstantState();
        final Drawable normal = item;
        final Drawable selected;
        if (state != null) {
            selected = state.newDrawable(getResources()).mutate();
            selected.setState(SELECTED_STATE_SET);
        } else {
            selected = item;
        }
        setDrawables(normal, selected);
    }
}
