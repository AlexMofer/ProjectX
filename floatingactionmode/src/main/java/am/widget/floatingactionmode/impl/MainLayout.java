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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import am.widget.floatingactionmode.FloatingMenuItem;


/**
 * 主面板
 * Created by Alex on 2018/11/21.
 */
final class MainLayout extends LinearLayout implements View.OnClickListener {

    private final Path mCropPath = new Path();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mCornerCrop = new Path();
    private final RectF mCornerCropBound = new RectF();
    private final float[] mRadii = new float[8];

    private final Rect mCropBound = new Rect();
    private boolean mCropReverse;
    private boolean mCropStart;
    private final RectF mCropAnimateBound = new RectF();
    private float mCornerRadius;

    private OnMainListener mListener;

    MainLayout(Context context) {
        super(context);
        setWillNotDraw(false);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        mCornerCrop.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropPath.setFillType(Path.FillType.EVEN_ODD);
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
        if (mCornerRadius <= 0 && !mCropStart) {
            super.draw(canvas);
            return;
        }
        final int layer = Compat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        cropCorner(canvas);
        canvas.drawPath(mCropPath, mPaint);
        canvas.restoreToCount(layer);
    }

    private void cropCorner(Canvas canvas) {
        final int layer = Compat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        canvas.drawPath(mCornerCrop, mPaint);
        canvas.restoreToCount(layer);
    }

    // Listener
    @Override
    public void onClick(View v) {
        if (mListener != null)
            mListener.onMainItemClick((FloatingMenuItem) v.getTag());
    }

    void setData(FloatingMenuImpl menu, int maxWidth, int overflowButtonWidth) {
        int index = 0;
        int width = 0;
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            final boolean more = menu.hasMoreMenu();
            final MenuItemView button = getChildAt(index, item);
            button.setFirst(index == 0);
            button.setLast(!more);
            button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            final int itemWidth = button.getMeasuredWidth();
            if (more) {
                // 存在更多菜单
                if (width + itemWidth > maxWidth) {
                    // 宽度超过最大宽度
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
                if (width + itemWidth > maxWidth - overflowButtonWidth) {
                    // 宽度放不下
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
                width += itemWidth;
                index++;
            } else {
                // 最后一个菜单
                if (width + itemWidth > maxWidth) {
                    // 宽度放不下
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
            }
        }
        requestLayout();
        invalidate();
    }

    private MenuItemView getChildAt(int index, FloatingMenuItem item) {
        View child = getChildAt(index);
        if (child == null) {
            child = new MenuItemView(getContext());
            child.setOnClickListener(this);
            addView(child, index);
        }
        final MenuItemView view = (MenuItemView) child;
        view.setData(item);
        view.setTag(item);
        return (MenuItemView) child;
    }

    private void removeChild(int index) {
        int count = getChildCount();
        while (count > index) {
            final View child = getChildAt(index);
            child.setTag(null);
            child.setOnClickListener(null);
            removeViewInLayout(child);
            count = getChildCount();
        }
    }

    void setCornerRadius(float radius) {
        mCornerRadius = radius;
    }

    void setCorner(boolean all) {
        if (all)
            mRadii[0] = mRadii[1] = mRadii[2] = mRadii[3] =
                    mRadii[4] = mRadii[5] = mRadii[6] = mRadii[7] = mCornerRadius;
        else {
            mRadii[2] = mRadii[3] = mRadii[4] = mRadii[5] = 0;
            mRadii[0] = mRadii[1] = mRadii[6] = mRadii[7] = mCornerRadius;
        }
        updateCornerCropPath();
        invalidate();
    }

    void setSwitchAnimate(int left, int top, int right, int bottom, boolean reverse) {
        mCropBound.set(left, top, right, bottom);
        mCropReverse = reverse;
    }

    void setSwitchAnimateValue(float value) {
        if (!mCropStart)
            return;
        if (mCropReverse)
            value = 1 - value;
        final int width = getWidth();
        final int height = getHeight();
        mCropPath.reset();
        mCropPath.moveTo(0, 0);
        mCropPath.lineTo(width, 0);
        mCropPath.lineTo(width, height);
        mCropPath.lineTo(0, height);
        mCropPath.close();

        mCropAnimateBound.set(Math.round(mCropBound.left * value),
                Math.round(mCropBound.top * value),
                Math.round(width + (mCropBound.right - width) * value),
                Math.round(height + (mCropBound.bottom - height) * value));
        mCropPath.addRoundRect(mCropAnimateBound, mCornerRadius, mCornerRadius, Path.Direction.CW);
        setAlpha(1 - value);
        invalidate();
    }

    void start() {
        mCropStart = true;
    }

    void stop() {
        mCropStart = false;
        invalidate();
    }

    void setOnMainListener(OnMainListener listener) {
        mListener = listener;
    }

    public interface OnMainListener {
        void onMainItemClick(FloatingMenuItem item);
    }
}
