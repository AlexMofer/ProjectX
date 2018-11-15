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
package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import am.widget.floatingactionmode.R;


/**
 * 开关布局 TODO 按钮资源颜色
 * Created by Alex on 2018/10/23.
 */
final class OverflowButton extends FrameLayout {

    private final ImageButton mButton;
    private final int mSize;
    private final Drawable mArrow;
    private final Drawable mOverflow;
    private final Drawable mToArrow;
    private final Drawable mToOverflow;

    OverflowButton(Context context) {
        super(context);
        mButton = new ImageButton(context);
        final Resources resources = context.getResources();
        final int paddingVertical = resources.getDimensionPixelOffset(
                R.dimen.switchButtonPaddingVertical);
        final int paddingHorizontal = resources.getDimensionPixelOffset(
                R.dimen.switchButtonPaddingHorizontal);
        int size = resources.getDimensionPixelOffset(R.dimen.floatingActionModeItemSize);

        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        size = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemSize, size);
        final Drawable background = custom.getDrawable(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackground);
        if (background == null) {
            final TypedArray a = context.obtainStyledAttributes(
                    new int[]{android.R.attr.actionBarItemBackground});
            mButton.setBackgroundResource(a.getResourceId(0, 0));
            a.recycle();
        } else {
            mButton.setBackgroundDrawable(background);
        }

        custom.recycle();

        mButton.setPadding(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal);
        mButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        mArrow = resources.getDrawable(R.drawable.fam_avd_tooverflow);
        mOverflow = resources.getDrawable(R.drawable.fam_avd_toarrow);
        if (Build.VERSION.SDK_INT >= 21) {
            mToArrow = context.getDrawable(R.drawable.fam_avd_toarrow_animation);
            mToOverflow = context.getDrawable(R.drawable.fam_avd_tooverflow_animation);
        } else {
            mToArrow = null;
            mToOverflow = null;
        }

        addView(mButton, new LayoutParams(size, size));

        mSize = size;
    }

    void setOverflow(boolean animate) {
        if (animate && mToOverflow != null) {
            mButton.setImageDrawable(mToOverflow);
            if (mToOverflow instanceof Animatable)
                ((Animatable) mToOverflow).start();
        } else {
            mButton.setImageDrawable(mOverflow);
        }
        mButton.setContentDescription(getResources().getString(R.string.fam_cd_more));
    }

    void setArrow(boolean animate) {
        if (animate && mToArrow != null) {
            mButton.setImageDrawable(mToArrow);
            if (mToArrow instanceof Animatable)
                ((Animatable) mToArrow).start();
        } else {
            mButton.setImageDrawable(mArrow);
        }
        mButton.setContentDescription(getResources().getString(R.string.fam_cd_close));
    }

    int getSize() {
        return mSize;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mButton.setOnClickListener(l);
    }
}
