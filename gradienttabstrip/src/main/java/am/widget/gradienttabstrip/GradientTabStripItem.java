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

package am.widget.gradienttabstrip;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

final class GradientTabStripItem extends View {

    private float mTextSize;// 文字大小
    private int mTextColorNormal;// 文字默认颜色
    private int mTextColorSelected;// 文字选中颜色
    private int mDrawablePadding;// 图文间距
    private int mDotMarginCenterX;// 小圆点距离中心X轴距离（以中心点为直角坐标系原点）
    private int mDotMarginCenterY;// 小圆点距离中心Y轴距离（以中心点为直角坐标系原点）
    private boolean mDotCanGoOutside;// 小圆点是否可绘制到视图外部
    private Drawable mDotBackground;// 小圆点背景图
    private float mDotTextSize;// 小圆点文字大小
    private int mDotTextColor;// 小圆点文字颜色
    private String mTitle;
    private String mDot;
    private Drawable mNormal;
    private Drawable mSelected;
    private float mOffset = 0;
    GradientTabStripItem(Context context) {
        super(context);
    }

    void setTextSize(float size) {
        if (mTextSize == size)
            return;
        mTextSize = size;
    }

    void setTextColor(int normal, int selected) {
        if (mTextColorNormal == normal && mTextColorSelected == selected)
            return;
        mTextColorNormal = normal;
        mTextColorSelected = selected;
        invalidate();
    }

    void setDrawablePadding(int padding) {
        if (padding == mDrawablePadding)
            return;
        mDrawablePadding = padding;
    }

    void setDotMarginCenter(int x, int y) {
        if (mDotMarginCenterX == x && mDotMarginCenterY == y)
            return;
        mDotMarginCenterX = x;
        mDotMarginCenterY = y;
        invalidate();
    }

    void setDotCanGoOutside(boolean can) {
        if (mDotCanGoOutside == can)
            return;
        mDotCanGoOutside = can;
        invalidate();
    }

    void setDotBackground(Drawable background) {
        if (mDotBackground == background)
            return;
        mDotBackground = background;
        invalidate();
    }

    void setDotTextSize(float size) {
        if (mDotTextSize == size)
            return;
        mDotTextSize = size;
        invalidate();
    }

    void setDotTextColor(int color) {
        if (mDotTextColor == color)
            return;
        mDotTextColor = color;
        invalidate();
    }

    void set(CharSequence title, String dot, Drawable normal, Drawable selected, float offset) {
        final String ts = title == null ? null : title.toString();
        if (TextUtils.equals(mTitle, ts) && TextUtils.equals(mDot, dot) && mNormal == normal &&
                mSelected == selected && mOffset == offset)
            return;
        mTitle = ts;
        mDot = dot;
        mNormal = normal;
        mSelected = selected;
        mOffset = offset;
        invalidate();
    }
}
