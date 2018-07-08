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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import am.widget.tabstrip.TabStripTagAdapter;
import am.widget.tabstrip.TabStripViewGroup;

/**
 * 渐变TabStrip，子项不宜过多
 *
 * @author Alex
 */
public class GradientTabStripNew extends TabStripViewGroup {

    public GradientTabStripNew(Context context) {
        super(context);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {

    }

    @Override
    public void addView(View child) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int width, int height) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
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
    }
}