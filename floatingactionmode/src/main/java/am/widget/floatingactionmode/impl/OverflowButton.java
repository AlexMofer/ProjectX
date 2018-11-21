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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import am.widget.floatingactionmode.R;


/**
 * 切换按钮
 * Created by Alex on 2018/11/21.
 */
final class OverflowButton extends FrameLayout {

    static final int TYPE_ALL = 0;
    static final int TYPE_START = 1;
    static final int TYPE_END = 2;
    private final ImageButton mButton;
    private final int mSize;
    private Drawable mOverflow;
    private Drawable mBack;
    private Drawable mOverflowToBack;
    private Drawable mBackToOverflow;

    private String mOverflowContentDescription;
    private String mBackContentDescription;

    private float mCornerRadius;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mCornerCrop = new Path();
    private final RectF mCornerCropBound = new RectF();
    private final float[] mRadii = new float[8];

    OverflowButton(Context context) {
        super(context);
        setWillNotDraw(false);
        mButton = new ImageButton(context);
        final Resources resources = context.getResources();
        final int paddingVertical = resources.getDimensionPixelOffset(
                R.dimen.switchButtonPaddingVertical);
        final int paddingHorizontal = resources.getDimensionPixelOffset(
                R.dimen.switchButtonPaddingHorizontal);
        int size = resources.getDimensionPixelOffset(R.dimen.floatingActionModeItemSize);
        mOverflowContentDescription = resources.getString(R.string.fam_cd_overflow);
        mBackContentDescription = resources.getString(R.string.fam_cd_back);

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
        final Resources.Theme theme = context.getTheme();
        if (custom.hasValue(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowIcon))
            mOverflow = custom.getDrawable(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowIcon);
        else
            mOverflow = Compat.getDrawable(resources,
                    R.drawable.floatingActionModeOverflowButtonOverflowIcon, theme);
        if (custom.hasValue(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackIcon))
            mBack = custom.getDrawable(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackIcon);
        else
            mBack = Compat.getDrawable(resources,
                    R.drawable.floatingActionModeOverflowButtonBackIcon, theme);
        if (custom.hasValue(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowToBackIcon))
            mOverflowToBack = custom.getDrawable(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowToBackIcon);
        else
            mOverflowToBack = Compat.getDrawable(resources,
                    R.drawable.floatingActionModeOverflowButtonOverflowToBackIcon, theme);
        if (custom.hasValue(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackToOverflowIcon))
            mBackToOverflow = custom.getDrawable(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackToOverflowIcon);
        else
            mBackToOverflow = Compat.getDrawable(resources,
                    R.drawable.floatingActionModeOverflowButtonBackToOverflowIcon, theme);
        if (custom.hasValue(
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonIconTint)) {
            final ColorStateList tint = custom.getColorStateList(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonIconTint);
            final int mode = custom.getInt(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonIconTintMode,
                    0);
            PorterDuff.Mode tintMode;
            switch (mode) {
                default:
                case 5:
                    tintMode = PorterDuff.Mode.SRC_IN;
                    break;
                case 3:
                    tintMode = PorterDuff.Mode.SRC_OVER;
                    break;
                case 9:
                    tintMode = PorterDuff.Mode.SRC_ATOP;
                    break;
                case 14:
                    tintMode = PorterDuff.Mode.MULTIPLY;
                    break;
                case 15:
                    tintMode = PorterDuff.Mode.SCREEN;
                    break;
                case 16:
                    tintMode = PorterDuff.Mode.ADD;
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mOverflow != null) {
                    mOverflow.setTintList(tint);
                    mOverflow.setTintMode(tintMode);
                }
                if (mBack != null) {
                    mBack.setTintList(tint);
                    mBack.setTintMode(tintMode);
                }
                if (mOverflowToBack != null) {
                    mOverflowToBack.setTintList(tint);
                    mOverflowToBack.setTintMode(tintMode);
                }
                if (mBackToOverflow != null) {
                    mBackToOverflow.setTintList(tint);
                    mBackToOverflow.setTintMode(tintMode);
                }
            } else {
                if (mOverflow != null) {
                    final TintAwareDrawable overflow = new TintAwareDrawable(mOverflow);
                    overflow.setTintList(tint);
                    overflow.setTintMode(tintMode);
                    mOverflow = overflow;
                }
                if (mBack != null) {
                    final TintAwareDrawable back = new TintAwareDrawable(mBack);
                    back.setTintList(tint);
                    back.setTintMode(tintMode);
                    mBack = back;
                }
                if (mOverflowToBack != null) {
                    final TintAwareDrawable overflowToBack = new TintAwareDrawable(mOverflowToBack);
                    overflowToBack.setTintList(tint);
                    overflowToBack.setTintMode(tintMode);
                    mOverflowToBack = overflowToBack;
                }
                if (mBackToOverflow != null) {
                    final TintAwareDrawable backToOverflow = new TintAwareDrawable(mBackToOverflow);
                    backToOverflow.setTintList(tint);
                    backToOverflow.setTintMode(tintMode);
                    mBackToOverflow = backToOverflow;
                }
            }
        }
        if (Compat.hasValueOrEmpty(custom,
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowContentDescription))
            mOverflowContentDescription = custom.getString(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonOverflowContentDescription);
        if (Compat.hasValueOrEmpty(custom,
                R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackContentDescription))
            mBackContentDescription = custom.getString(
                    R.styleable.FloatingActionMode_floatingActionModeOverflowButtonBackContentDescription);
        custom.recycle();

        mButton.setPadding(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal);
        mButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        addView(mButton, new LayoutParams(size, size));

        mSize = size;

        mCornerCrop.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateCornerCropPath();
    }

    private void updateCornerCropPath() {
        final int width = getWidth();
        final int height = getHeight();
        mCornerCropBound.set(0, 0, width, height);
        mCornerCrop.reset();
        mCornerCrop.moveTo(0, 0);
        mCornerCrop.lineTo(width, 0);
        mCornerCrop.lineTo(width, height);
        mCornerCrop.lineTo(0, height);
        mCornerCrop.close();
        mCornerCrop.addRoundRect(mCornerCropBound, mRadii, Path.Direction.CW);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mCornerRadius <= 0) {
            super.draw(canvas);
            return;
        }
        final int layer = Compat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        canvas.drawPath(mCornerCrop, mPaint);
        canvas.restoreToCount(layer);
    }

    void setOverflow(boolean animate) {
        if (animate && mBackToOverflow != null) {
            mButton.setImageDrawable(mBackToOverflow);
            if (mBackToOverflow instanceof Animatable)
                ((Animatable) mBackToOverflow).start();
        } else {
            mButton.setImageDrawable(mOverflow);
        }
        mButton.setContentDescription(mOverflowContentDescription);
    }

    void setBack(boolean animate) {
        if (animate && mOverflowToBack != null) {
            mButton.setImageDrawable(mOverflowToBack);
            if (mOverflowToBack instanceof Animatable)
                ((Animatable) mOverflowToBack).start();
        } else {
            mButton.setImageDrawable(mBack);
        }
        mButton.setContentDescription(mBackContentDescription);
    }

    int getSize() {
        return mSize;
    }

    void setCornerRadius(float radius) {
        mCornerRadius = radius;
    }

    void setCorner(int type) {
        switch (type) {
            default:
            case TYPE_ALL:
                mRadii[0] = mRadii[1] = mRadii[2] = mRadii[3] =
                        mRadii[4] = mRadii[5] = mRadii[6] = mRadii[7] = mCornerRadius;
                break;
            case TYPE_START:
                mRadii[0] = mRadii[1] = mRadii[6] = mRadii[7] = mCornerRadius;
                mRadii[2] = mRadii[3] = mRadii[4] = mRadii[5] = 0;
                break;
            case TYPE_END:
                mRadii[0] = mRadii[1] = mRadii[6] = mRadii[7] = 0;
                mRadii[2] = mRadii[3] = mRadii[4] = mRadii[5] = mCornerRadius;
                break;
        }
        updateCornerCropPath();
        invalidate();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mButton.setOnClickListener(l);
    }
}
