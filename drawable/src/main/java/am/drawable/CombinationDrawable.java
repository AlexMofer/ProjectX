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
import android.view.Gravity;

/**
 * 双层Drawable
 * 一般用于ImageView的src，保证缩放后，中心的Drawable不变形。
 * 用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
 */
@SuppressWarnings("unused")
public class CombinationDrawable extends Drawable {

    private Drawable mBackground;
    private Drawable mForeground;
    private int mGravity;
    private int mReservedLeft;
    private int mReservedTop;
    private int mReservedRight;
    private int mReservedBottom;

    public CombinationDrawable(Drawable background, Drawable foreground) {
        this(background, foreground, Gravity.CENTER);
    }

    public CombinationDrawable(Drawable background, Drawable foreground, int gravity) {
        this(background, foreground, gravity, 0, 0, 0, 0);
    }

    public CombinationDrawable(Drawable background, Drawable foreground, int gravity,
                               int reservedLeft, int reservedTop,
                               int reservedRight, int reservedBottom) {
        setBackground(background);
        setForeground(foreground);
        setGravity(gravity);
        setReservedSide(reservedLeft, reservedTop, reservedRight, reservedBottom);
    }

    @Override
    public int getMinimumHeight() {
        int minimumHeight = super.getMinimumHeight();
        if (mBackground != null) {
            minimumHeight = Math.max(mBackground.getIntrinsicHeight(), minimumHeight);
        }
        if (mForeground != null) {
            minimumHeight = Math.max(mForeground.getIntrinsicHeight(), minimumHeight);
        }
        return minimumHeight;
    }

    @Override
    public int getMinimumWidth() {
        int minimumWidth = super.getMinimumWidth();
        if (mBackground != null) {
            minimumWidth = Math.max(mBackground.getIntrinsicWidth(), minimumWidth);
        }
        if (mForeground != null) {
            minimumWidth = Math.max(mForeground.getIntrinsicWidth(), minimumWidth);
        }
        return minimumWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        drawBackground(canvas);
        drawForeground(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    protected void drawBackground(Canvas canvas) {
        if (mBackground != null) {
            mBackground.setBounds(getBounds().left + mReservedLeft,
                    getBounds().top + mReservedTop,
                    getBounds().right - mReservedRight,
                    getBounds().bottom - mReservedBottom);
            mBackground.draw(canvas);
        }
    }

    /**
     * 绘制中部Drawable
     *
     * @param canvas 画布
     */
    @SuppressWarnings("all")
    protected void drawForeground(Canvas canvas) {
        if (mForeground != null) {
            final int width = mForeground.getIntrinsicWidth();
            final int height = mForeground.getIntrinsicHeight();
            mForeground.setBounds(0, 0, width, height);
            canvas.save();
            switch (mGravity) {
                default:
                case Gravity.LEFT:
                case Gravity.LEFT | Gravity.TOP:
                case Gravity.TOP:
                    break;
                case Gravity.RIGHT:
                case Gravity.TOP | Gravity.RIGHT:
                    canvas.translate(getBounds().right - width, 0);
                    break;
                case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
                    canvas.translate(getBounds().right - width,
                            getBounds().centerY() - height * 0.5f);
                    break;
                case Gravity.RIGHT | Gravity.BOTTOM:
                    canvas.translate(getBounds().right - width, getBounds().bottom - height);
                    break;
                case Gravity.BOTTOM:
                case Gravity.LEFT | Gravity.BOTTOM:
                    canvas.translate(0, getBounds().bottom - height);
                    break;
                case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                    canvas.translate(getBounds().centerX() - width * 0.5f,
                            getBounds().bottom - height);
                    break;
                case Gravity.CENTER_HORIZONTAL:
                case Gravity.TOP | Gravity.CENTER_HORIZONTAL:
                    canvas.translate(getBounds().centerX() - width * 0.5f, 0);
                    break;
                case Gravity.CENTER:
                    canvas.translate(getBounds().centerX() - width * 0.5f,
                            getBounds().centerY() - height * 0.5f);
                    break;
                case Gravity.CENTER_VERTICAL:
                case Gravity.LEFT | Gravity.CENTER_VERTICAL:
                    canvas.translate(0, getBounds().centerY() - height * 0.5f);
                    break;
            }
            mForeground.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mBackground != null)
            mBackground.setAlpha(alpha);
        if (mForeground != null)
            mForeground.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mBackground != null)
            mBackground.setColorFilter(cf);
        if (mForeground != null)
            mForeground.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置背景
     *
     * @param background 背景
     */
    public void setBackground(Drawable background) {
        mBackground = background;
        invalidateSelf();
    }

    /**
     * 设置前景
     *
     * @param foreground 前景
     */
    public void setForeground(Drawable foreground) {
        mForeground = foreground;
        invalidateSelf();
    }

    /**
     * 设置前景布局
     *
     * @param gravity 布局
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        invalidateSelf();
    }

    /**
     * 设置留边
     *
     * @param reservedLeft   左
     * @param reservedTop    上
     * @param reservedRight  右
     * @param reservedBottom 下
     */
    public void setReservedSide(int reservedLeft, int reservedTop,
                                int reservedRight, int reservedBottom) {
        mReservedLeft = reservedLeft;
        mReservedTop = reservedTop;
        mReservedRight = reservedRight;
        mReservedBottom = reservedBottom;
        invalidateSelf();
    }
}
