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
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * 主面板
 * Created by Alex on 2018/10/23.
 */
@SuppressLint("ViewConstructor")
final class MainLayout extends FrameLayout implements View.OnClickListener {

    private final FrameLayout mContent;
    private final LinearLayout mMain;
    private final int mSwitchWidth;
    private final int mHeight;
    private final int mMargin;
    private final Point mCropSize = new Point();
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final OnMainListener mListener;
    private boolean mLight;
    private boolean mUseTheme;
    private boolean mCrop = false;
    private float mValue = 0;

    MainLayout(Context context, int height, int margin, int switchWidth,
               boolean light, boolean useTheme, OnMainListener listener) {
        super(context);
        setWillNotDraw(false);
        mContent = new FrameLayout(context);
        mMain = new LinearLayout(context);
        mHeight = height;
        mMargin = margin;
        mSwitchWidth = switchWidth;
        mLight = light;
        mUseTheme = useTheme;
        mListener = listener;

        mMain.setOrientation(LinearLayout.HORIZONTAL);
        mMain.setGravity(Gravity.CENTER);

        mContent.addView(mMain, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));

        final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, height);
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
    public void onClick(View v) {
        mListener.onMainItemClick((FloatingMenuItem) v.getTag());
    }

    void setData(FloatingMenuImpl menu, int maxWidth) {
        maxWidth = maxWidth - mMargin * 2;
        int index = 0;
        int width = 0;
        mMain.setPadding(0, 0, 0, 0);
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            final MenuItemLayout button = getChild(index);
            button.setLightTheme(mLight);
            DrawableUtils.changeButtonBackground(button, mLight, mUseTheme);
            button.setFirst(index == 0);
            button.setLast(false);
            button.setOnClickListener(this);
            button.setData(item);
            button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int itemWidth = button.getMeasuredWidth();
            if (menu.hasMoreMenu()) {
                // 存在更多主面板菜单
                if (width + itemWidth + mSwitchWidth < maxWidth) {
                    button.setTag(item);
                    width += itemWidth;
                    index++;
                } else if (width + itemWidth + mSwitchWidth == maxWidth) {
                    button.setTag(item);
                    removeChild(index + 1);
                    mMain.setPadding(0, 0, mSwitchWidth, 0);
                    break;
                } else {
                    menu.pushItemBack(item);
                    removeChild(index);
                    mMain.setPadding(0, 0, mSwitchWidth, 0);
                    break;
                }
            } else {
                button.setLast(true);
                button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                itemWidth = button.getMeasuredWidth();
                if (width + itemWidth <= maxWidth) {
                    button.setTag(item);
                    removeChild(index + 1);
                    break;
                } else {
                    menu.pushItemBack(item);
                    removeChild(index);
                    mMain.setPadding(0, 0, mSwitchWidth, 0);
                    break;
                }
            }
        }
    }

    private MenuItemLayout getChild(int index) {
        View child = mMain.getChildAt(index);
        if (child == null) {
            child = new MenuItemLayout(getContext(), mHeight, mLight);
            DrawableUtils.setButtonBackground(child, mLight, mUseTheme);
            mMain.addView(child, index);
        }
        return (MenuItemLayout) child;
    }

    private void removeChild(int index) {
        int count = mMain.getChildCount();
        while (count > index) {
            mMain.removeViewAt(index);
            count = mMain.getChildCount();
        }
    }

    void setLightTheme(boolean light, boolean useTheme) {
        final int count = mMain.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = mMain.getChildAt(i);
            if (child instanceof MenuItemLayout) {
                ((MenuItemLayout) child).setLightTheme(light);
                DrawableUtils.changeButtonBackground(child, light, useTheme);
            }
        }
        DrawableUtils.changeRootBackground(mContent, light);
        mLight = light;
        mUseTheme = useTheme;
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
        final int top = formY - height + mMargin;
        final int right = formX - mMargin;
        final int bottom = formY - mMargin;
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

    public interface OnMainListener {
        void onMainItemClick(FloatingMenuItem item);
    }
}
