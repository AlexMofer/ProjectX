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
 * 开关布局 TODO 按钮资源颜色
 * Created by Alex on 2018/10/23.
 */
final class OverflowButton extends FrameLayout {

    static final int TYPE_ALL = 0;
    static final int TYPE_START = 1;
    static final int TYPE_END = 2;
    private final ImageButton mButton;
    private final int mSize;
    private final Drawable mArrow;
    private final Drawable mOverflow;
    private final Drawable mToArrow;
    private final Drawable mToOverflow;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mCornerCrop = new Path();
    private final RectF mCornerCropBound = new RectF();
    private final float[] mRadii = new float[8];
    private float mCornerRadius;

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
        final int layer = CanvasCompat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        canvas.drawPath(mCornerCrop, mPaint);
        canvas.restoreToCount(layer);
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
