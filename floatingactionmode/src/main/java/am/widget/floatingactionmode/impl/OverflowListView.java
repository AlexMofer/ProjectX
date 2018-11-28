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
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.R;

/**
 * 更多面板
 * Created by Alex on 2018/11/21.
 */
final class OverflowListView extends MenuListView implements AdapterView.OnItemClickListener {

    private final int mMaxHeight;
    private int mPaddingSpace;
    private boolean mReverse;

    private final Path mCropPath = new Path();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mCornerCrop = new Path();
    private final RectF mCornerCropBound = new RectF();

    private final Rect mCropBound = new Rect();
    private boolean mCropReverse;
    private boolean mCropStart;
    private final RectF mCropAnimateBound = new RectF();
    private float mCornerRadius;

    private boolean mHidden;

    private OnOverflowListener mListener;

    OverflowListView(Context context) {
        super(context);
        setWillNotDraw(false);
        final Resources resources = context.getResources();
        final TypedValue outValue = new TypedValue();
        resources.getValue(R.dimen.floatingActionModeOverflowItemMaxShowCount, outValue, true);
        float count = outValue.getFloat();
        int overflowMinimumWidth = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeOverflowItemMinimumWidth);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        count = custom.getFloat(
                R.styleable.FloatingActionMode_floatingActionModeOverflowItemMaxShowCount, count);
        overflowMinimumWidth = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeOverflowItemMinimumWidth,
                overflowMinimumWidth);
        custom.recycle();
        setItemMinimumWidth(overflowMinimumWidth);
        mMaxHeight = (int) (mCalculator.getSize() * count);
        mCornerCrop.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropPath.setFillType(Path.FillType.EVEN_ODD);
        setOnItemClickListener(this);
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
        mCornerCrop.addRoundRect(mCornerCropBound, mCornerRadius, mCornerRadius, Path.Direction.CW);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHidden)
            return;
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

    void setPaddingSpace(int space) {
        mPaddingSpace = space;
        if (mReverse)
            setPadding(0, 0, 0, mPaddingSpace);
        else
            setPadding(0, mPaddingSpace, 0, 0);
    }

    int setData(FloatingMenuImpl menu) {
        mAdapter.clear();
        int itemMaxWidth = Integer.MIN_VALUE;
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            mCalculator.setData(item);
            mCalculator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            itemMaxWidth = itemMaxWidth < mCalculator.getMeasuredWidth() ?
                    mCalculator.getMeasuredWidth() : itemMaxWidth;
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();
        return itemMaxWidth;
    }

    int getMaxHeight() {
        return mMaxHeight + mPaddingSpace;
    }

    void setReverse(boolean reverse) {
        if (mReverse == reverse)
            return;
        mReverse = reverse;
        if (mReverse)
            setPadding(0, 0, 0, mPaddingSpace);
        else
            setPadding(0, mPaddingSpace, 0, 0);
    }

    boolean isReverse() {
        return mReverse;
    }

    void clear() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    void setCornerRadius(float radius) {
        mCornerRadius = radius;
        updateCornerCropPath();
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

    void hide() {
        setEnabled(false);
        mHidden = true;
        invalidate();
    }

    void show() {
        setEnabled(true);
        mHidden = false;
        invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener == null)
            return;
        mListener.onOverflowItemClick(mAdapter.getItem(position));
    }

    void setOnOverflowListener(OnOverflowListener listener) {
        mListener = listener;
    }

    public interface OnOverflowListener {
        void onOverflowItemClick(FloatingMenuItem item);
    }
}
