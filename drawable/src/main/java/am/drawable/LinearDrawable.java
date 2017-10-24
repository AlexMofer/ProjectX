/*
 * Copyright (C) 2015 AlexMofer
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

package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 线性Drawable
 */
@SuppressWarnings("unused")
public class LinearDrawable extends Drawable {


    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Drawable mItem;
    private int mGap;
    private int mOrientation;
    private int mCount;

    public LinearDrawable(Drawable item) {
        this(item, 0);
    }

    public LinearDrawable(Drawable item, int gap) {
        this(item, gap, HORIZONTAL);
    }

    public LinearDrawable(Drawable item, int gap, int orientation) {
        this(item, gap, orientation, 0);
    }

    public LinearDrawable(Drawable item, int gap, int orientation, int count) {
        setItemDrawable(item);
        setGap(gap);
        setOrientation(orientation);
        setCount(count);
    }

    @Override
    public int getIntrinsicHeight() {
        if (mItem == null)
            return super.getIntrinsicHeight();
        if (mOrientation == VERTICAL) {
            return mCount <= 0 ? 0 : mItem.getIntrinsicHeight() * mCount + mGap * (mCount - 1);
        } else {
            return mItem.getIntrinsicHeight();
        }
    }

    @Override
    public int getIntrinsicWidth() {
        if (mItem == null)
            return super.getIntrinsicWidth();
        if (mOrientation == VERTICAL) {
            return mItem.getIntrinsicWidth();
        } else {
            return mCount <= 0 ? 0 : mItem.getIntrinsicWidth() * mCount + mGap * (mCount - 1);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mCount <= 0 || mItem == null)
            return;
        canvas.save();
        if (mOrientation == VERTICAL) {
            final int itemWidth = getBounds().width();
            final int itemHeight = (getBounds().height() - mGap * (mCount - 1)) / mCount;
            mItem.setBounds(0, 0, itemWidth, itemHeight);
            for (int i = 0; i < mCount; i++) {
                mItem.draw(canvas);
                canvas.translate(0, itemHeight + mGap);
            }
        } else {
            final int itemWidth = (getBounds().width() - mGap * (mCount - 1)) / mCount;
            final int itemHeight = getBounds().height();
            mItem.setBounds(0, 0, itemWidth, itemHeight);
            for (int i = 0; i < mCount; i++) {
                mItem.draw(canvas);
                canvas.translate(itemWidth + mGap, 0);
            }
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        if (mItem != null) {
            mItem.setAlpha(alpha);
        }
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mItem != null) {
            mItem.setColorFilter(colorFilter);
        }
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mItem == null ? PixelFormat.TRANSLUCENT : mItem.getOpacity();
    }

    /**
     * 设置子项Drawable
     *
     * @param item 子项Drawable
     */
    public void setItemDrawable(Drawable item) {
        mItem = item;
        invalidateSelf();
        DrawableHelper.requestCallbackLayout(this);
    }

    /**
     * 设置子项数目
     *
     * @param count 子项数目
     */
    public void setCount(int count) {
        if (mCount == count)
            return;
        mCount = count;
        invalidateSelf();
        DrawableHelper.requestCallbackLayout(this);
    }

    /**
     * 设置方向
     *
     * @param orientation 方向
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;
        invalidateSelf();
        DrawableHelper.requestCallbackLayout(this);
    }

    /**
     * 设置子项间隔
     *
     * @param gap 子项间隔
     */
    public void setGap(int gap) {
        mGap = gap;
        invalidateSelf();
        DrawableHelper.requestCallbackLayout(this);
    }
}
