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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

import am.widget.tabstrip.TabStripDotAdapter;

/**
 * 渐变TabStrip，子项不宜过多
 *
 * @author Alex
 */
public class GradientTabStripNew extends BaseTabStripViewGroup<GradientTabStripItem> {

    private int mPosition = 0;
    private float mOffset = 0;
    private Adapter mAdapter;
    private int mItemBackgroundId;
    private Drawable mItemBackgroundDrawable;

    public GradientTabStripNew(Context context) {
        super(context);
        initView(context, null);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        // TODO
        mItemBackgroundId = NO_ID;
        mItemBackgroundDrawable = new ColorDrawable(0xffff00ff);
        initView(getDefaultDrawable(0xff00ff00, 10),
                SHOW_DIVIDER_MIDDLE | SHOW_DIVIDER_BEGINNING | SHOW_DIVIDER_END,
                10,
                getDefaultDrawable(0xffff0000, 46), true, 10);
        setSmoothScroll(true);
    }

    private Drawable getDefaultDrawable(int color, int size) {
        final GradientDrawable mBackground = new GradientDrawable();
        mBackground.setShape(GradientDrawable.RECTANGLE);
        mBackground.setColor(color);
        mBackground.setSize(size, 0);
        return mBackground;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            // TODO
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
        return item;
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
            offset = 0;
        } else {
            if (position == mPosition) {
                offset = mOffset;
            } else if (position == mPosition + 1) {
                offset = 1 - mOffset;
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
     * Adapter
     */
    @SuppressWarnings("unused")
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