/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget.maxsize;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * 控制最大尺寸
 * Created by Alex on 2026/1/23.
 */
public class LinearLayout extends android.widget.LinearLayout implements MaxSizeControllable {

    private int mMaxWidth = 0;
    private int mMaxHeight = 0;

    public LinearLayout(Context context) {
        super(context);
    }

    public LinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MaxSizeUtils.handleMeasureSpec(widthMeasureSpec, mMaxWidth),
                MaxSizeUtils.handleMeasureSpec(heightMeasureSpec, mMaxHeight));
    }

    @Override
    public int getMaximumHeight() {
        return mMaxHeight;
    }

    @Override
    public void setMaximumHeight(int maxHeight) {
        if (getMaximumHeight() == maxHeight) return;
        mMaxHeight = maxHeight;
        requestLayout();
    }

    @Override
    public int getMaximumWidth() {
        return mMaxWidth;
    }

    @Override
    public void setMaximumWidth(int maxWidth) {
        if (getMaximumWidth() == maxWidth) return;
        mMaxWidth = maxWidth;
        requestLayout();
    }
}
