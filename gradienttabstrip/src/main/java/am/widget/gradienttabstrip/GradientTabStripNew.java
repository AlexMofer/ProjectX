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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import am.widget.tabstrip.TabStripTagAdapter;

/**
 * 渐变TabStrip，子项不宜过多
 *
 * @author Alex
 */
public class GradientTabStripNew extends BaseTabStripViewGroup<GradientTabStripItem> {

    private int mPosition = 0;
    private float mOffset = 0;

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
    protected void onObservableChangeNotified(Object tag) {
        super.onObservableChangeNotified(tag);
        // TODO
    }

    @Override
    protected GradientTabStripItem onCreateView() {
        final GradientTabStripItem item = new GradientTabStripItem(getContext());
        item.setBackgroundColor(0xffff00ff);
        return item;
    }

    @Override
    protected void onBindView(GradientTabStripItem item, int position) {
        // TODO
        System.out.println("lalalla----------------------------------position:" + position);
    }

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(Adapter adapter) {
        setObservable(adapter);
    }

    /**
     * Adapter
     */
    public static abstract class Adapter extends TabStripTagAdapter {

        private static final int FLAG_DRAWABLE = 0;

        @Override
        protected void notifyChanged(Object tag) {
            // do nothing
        }

        /**
         * 获取普通状态下的 Drawable
         *
         * @param position 位置
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableNormal(int position);

        /**
         * 获取选中状态下的 Drawable
         *
         * @param position 位置
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableSelected(int position);

        /**
         * 通知
         */
        public void notifyDrawableChanged() {
            super.notifyChanged(FLAG_DRAWABLE);
        }

//        @SuppressWarnings("unchecked")
//        protected void notifyItemRangeChanged(int positionStart, int itemCount) {
//            final int count = getChildCount();
//            final int last = positionStart + itemCount;
//            for (int i = positionStart; i < count && i < last; i++) {
//                final V child = (V) getChildAt(i);
//                onBindView(child, i);
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        protected void notifyItemChanged(int position) {
//            final V child = (V) getChildAt(position);
//            if (child == null)
//                return;
//            onBindView(child, position);
//        }
    }
}