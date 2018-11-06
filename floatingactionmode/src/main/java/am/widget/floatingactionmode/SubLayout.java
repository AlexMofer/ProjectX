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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.FrameLayout;


/**
 * 次级菜单面板
 * Created by Alex on 2018/10/23.
 */
@SuppressLint("ViewConstructor")
final class SubLayout extends FrameLayout implements AdapterView.OnItemClickListener {

    private final FrameLayout mContent;
    private final SubListView mSubList;
    private final LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
    private final int mMargin;
    private final int mSwitchHeight;
    private final Point mCropSize = new Point();
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final OnSubListener mListener;
    private boolean mReverse = false;
    private boolean mCrop = false;
    private float mValue = 0;

    SubLayout(Context context, int height, int margin, int switchHeight,
              boolean light, boolean useTheme, OnSubListener listener) {
        super(context);
        setWillNotDraw(false);
        mContent = new FrameLayout(context);
        mSubList = new SubListView(context, height, light, useTheme);
        mMargin = margin;
        mSwitchHeight = switchHeight;
        mListener = listener;

        mSubList.setOnItemClickListener(this);
        final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.leftMargin = params.topMargin = params.rightMargin = params.bottomMargin = margin;
        addView(mContent, params);

        mPath.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void draw(Canvas canvas) {
        if (mValue == 0 || !mCrop) {
            super.draw(canvas);
            return;
        }
        final int layerCount = CanvasCompat.saveLayer(canvas,
                0, 0, getWidth(), getHeight(), null);
        super.draw(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.restoreToCount(layerCount);
    }

    // Listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onSubItemClick((FloatingMenuItem) view.getTag());
    }

    @SuppressWarnings("UnusedReturnValue")
    boolean setData(FloatingMenuImpl menu, Point size, FloatingActionMode mode) {
        boolean hasSubMenu = false;
        int width = 0;
        int height = 0;
        final int count = menu.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = menu.getItem(i);
            if (!item.hasSubMenu())
                continue;
            final FloatingSubMenu subMenu = item.getSubMenu();
            if (subMenu.isCustomMenu()) {
                final View custom = subMenu.getCustomView();
                if (custom instanceof FloatingSubMenuCustomView)
                    ((FloatingSubMenuCustomView) custom).onAttachedToFloatingActionMode(mode);
                custom.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                final int cw = custom.getMeasuredWidth();
                final int ch = custom.getMeasuredHeight();
                width = width > cw ? width : cw;
                height = height > ch ? height : ch;
            } else {
                mSubList.setData(subMenu, size, true);

                width = width > size.x ? width : size.x;
                height = height > size.y ? height : size.y;
            }
            hasSubMenu = true;
        }
        if (hasSubMenu)
            size.set(width + mMargin * 2, height + mSwitchHeight + mMargin * 2);
        else
            size.set(0, 0);
        return hasSubMenu;
    }

    void setData(FloatingMenuItem item, Point size) {
        size.set(0, 0);
        final FloatingSubMenu subMenu = item.getSubMenu();
        if (subMenu.isCustomMenu()) {
            final View custom = subMenu.getCustomView();
            custom.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            size.set(custom.getMeasuredWidth(), custom.getMeasuredHeight());
            final ViewParent parent = custom.getParent();
            if (parent instanceof ViewGroup)
                ((ViewGroup) parent).removeView(custom);
            mContent.addView(custom, mParams);
        } else {
            mSubList.setData(subMenu, size, false);
            mContent.addView(mSubList, mParams);
        }
        size.set(size.x + mMargin * 2, size.y + mSwitchHeight + mMargin * 2);
    }

    void clear() {
        mContent.removeAllViews();
    }

    void setLightTheme(boolean light, boolean useTheme) {
        mSubList.setLightTheme(light, useTheme);
    }

    void setReverse(boolean reverse) {
        if (reverse)
            mContent.setPadding(0, 0, 0, mSwitchHeight);
        else
            mContent.setPadding(0, mSwitchHeight, 0, 0);
        mReverse = reverse;
    }

    void awakenScrollBar() {
        mSubList.awakenScrollBar();
    }

    void setCrop(Point size) {
        if (size == null) {
            mCrop = false;
            mCropSize.set(0, 0);
            mValue = 0;
        } else {
            mCrop = true;
            mCropSize.set(size.x, size.y);
            mValue = 0;
        }
    }

    void setAnimationValue(float value) {
        mValue = value;
        mPath.reset();
        final int formX = getWidth();
        final int formY = getHeight();
        final int toX = mCropSize.x;
        final int toY = mCropSize.y;
        final int width = formX + (int) ((toX - formX) * mValue);
        final int height = formY + (int) ((toY - formY) * mValue);
        final int left = formX - width + mMargin;
        final int top = mReverse ? formY - height + mMargin : 0;
        final int right = formX - mMargin;
        final int bottom = mReverse ? formY : height - mMargin;
        mPath.moveTo(0, 0);
        mPath.lineTo(formX, 0);
        mPath.lineTo(formX, formY);
        mPath.lineTo(0, formY);
        mPath.close();
        mPath.moveTo(left, top);
        mPath.lineTo(right, top);
        mPath.lineTo(right, bottom);
        mPath.lineTo(left, bottom);
        mPath.close();
        requestLayout();
        invalidate();
    }

    public interface OnSubListener {
        void onSubItemClick(FloatingMenuItem item);
    }
}
