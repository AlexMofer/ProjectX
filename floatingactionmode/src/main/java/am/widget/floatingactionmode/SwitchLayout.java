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
package am.widget.floatingactionmode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;


/**
 * 开关布局
 * Created by Alex on 2018/10/23.
 */
@SuppressLint("ViewConstructor")
final class SwitchLayout extends FrameLayout {

    private static final int DIP_PADDING_VERTICAL = 11;
    private static final int DIP_PADDING_HORIZONTAL = 12;
    private static final int TYPE_ARROW = 1;
    private static final int TYPE_OVERFLOW = 2;

    private final ImageButton mSwitch;
    private Drawable mArrow;
    private Drawable mOverflow;
    private Drawable mToArrow;
    private Drawable mToOverflow;
    private int mType = 0;

    SwitchLayout(Context context, int height, boolean light, boolean useTheme) {
        super(context);
        mSwitch = new ImageButton(context);

        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int paddingVertical =
                TypedValueUtils.complexToDimensionPixelOffset(DIP_PADDING_VERTICAL, metrics);
        final int paddingHorizontal =
                TypedValueUtils.complexToDimensionPixelOffset(DIP_PADDING_HORIZONTAL, metrics);

        mSwitch.setPadding(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal);
        mSwitch.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        DrawableUtils.setOverflowButtonBackground(mSwitch, light, useTheme);

        mArrow = DrawableUtils.getOverflowButtonArrow(mSwitch, light, useTheme);
        mOverflow = DrawableUtils.getOverflowButtonOverflow(mSwitch, light, useTheme);
        mToArrow = DrawableUtils.getOverflowButtonToArrow(mSwitch, light, useTheme);
        mToOverflow = DrawableUtils.getOverflowButtonToOverflow(mSwitch, light, useTheme);

        //noinspection SuspiciousNameCombination
        addView(mSwitch, new LayoutParams(height, height));
    }

    void setOverflow(boolean animate) {
        mType = TYPE_OVERFLOW;
        if (animate && mToOverflow != null) {
            mSwitch.setImageDrawable(mToOverflow);
            if (mToOverflow instanceof Animatable)
                ((Animatable) mToOverflow).start();
        } else {
            mSwitch.setImageDrawable(mOverflow);
        }
        mSwitch.setContentDescription("More options");
    }

    void setArrow(boolean animate) {
        mType = TYPE_ARROW;
        if (animate && mToArrow != null) {
            mSwitch.setImageDrawable(mToArrow);
            if (mToArrow instanceof Animatable)
                ((Animatable) mToArrow).start();
        } else {
            mSwitch.setImageDrawable(mArrow);
        }
        mSwitch.setContentDescription("Close overflow");
    }

    void setLightTheme(boolean light, boolean useTheme) {
        DrawableUtils.changeOverflowButtonBackground(mSwitch, light, useTheme);
        mArrow = DrawableUtils.getOverflowButtonArrow(mSwitch, light, useTheme);
        mOverflow = DrawableUtils.getOverflowButtonOverflow(mSwitch, light, useTheme);
        mToArrow = DrawableUtils.getOverflowButtonToArrow(mSwitch, light, useTheme);
        mToOverflow = DrawableUtils.getOverflowButtonToOverflow(mSwitch, light, useTheme);
        if (mType == TYPE_ARROW)
            mSwitch.setImageDrawable(mArrow);
        else if (mType == TYPE_OVERFLOW)
            mSwitch.setImageDrawable(mOverflow);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mSwitch.setOnClickListener(l);
    }
}
