/*
 * Copyright (C) 2019 AlexMofer
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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 网格Drawable
 * Created by Alex on 2019/1/4.
 */
@SuppressWarnings("unused")
public class GridDrawable extends DrawableWrapper {

    private int mRowCount = 1;
    private int mColumnCount = 1;
    private float mHorizontalSpacing;
    private float mVerticalSpacing;
    private boolean mConstantSize;

    public GridDrawable() {
        super(null);
    }

    public GridDrawable(Drawable drawable) {
        super(drawable);
    }

    @Override
    public void setWrappedDrawable(Drawable drawable) {
        super.setWrappedDrawable(drawable);
        updateSize();
        invalidateSelf();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = resources.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.GridDrawable);
        final Drawable drawable = custom.getDrawable(R.styleable.GridDrawable_android_src);
        mRowCount = custom.getInt(R.styleable.GridDrawable_android_rowCount, 1);
        mColumnCount = custom.getInt(R.styleable.GridDrawable_android_columnCount, 1);
        mHorizontalSpacing = custom.getDimension(R.styleable.GridDrawable_android_horizontalSpacing,
                0);
        mVerticalSpacing = custom.getDimension(R.styleable.GridDrawable_android_verticalSpacing,
                0);
        mConstantSize = custom.getBoolean(R.styleable.GridDrawable_android_constantSize,
                false);
        custom.recycle();
        setWrappedDrawable(drawable);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mRowCount <= 0 || mColumnCount <= 0)
            return;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return;
        final Rect itemBound = drawable.getBounds();
        final int itemWidth = itemBound.width();
        final int itemHeight = itemBound.height();
        float dx;
        float dy;
        canvas.save();
        for (int i = 0; i < mRowCount; i++) {
            for (int j = 0; j < mColumnCount; j++) {
                dx = j * itemWidth + (j > 0 ? (mHorizontalSpacing * j) : 0);
                dy = i * itemHeight + (i > 0 ? (mVerticalSpacing * i) : 0);
                canvas.translate(dx, dy);
                drawable.draw(canvas);
                canvas.translate(-dx, -dy);
            }
        }
        canvas.restore();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        updateSize();
    }

    private void updateSize() {
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return;
        if (mConstantSize) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            return;
        }
        if (mRowCount <= 0 || mColumnCount <= 0)
            return;
        final Rect bounds = getBounds();
        if (bounds.isEmpty())
            return;
        final float horizontalSpacing = mHorizontalSpacing * (mColumnCount - 1);
        final float verticalSpacing = mVerticalSpacing * (mRowCount - 1);
        final float contentWidth = bounds.width() - horizontalSpacing;
        final float contentHeight = bounds.height() - verticalSpacing;
        final float width = contentWidth / mColumnCount;
        final float height = contentHeight / mRowCount;
        drawable.setBounds(0, 0, Math.round(width), Math.round(height));
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public Region getTransparentRegion() {
        if (mRowCount <= 0 || mColumnCount <= 0)
            return null;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return null;
        final Region ir = drawable.getTransparentRegion();
        if (ir == null)
            return null;
        final Region region = new Region();
        final Rect itemBound = drawable.getBounds();
        final int itemWidth = itemBound.width();
        final int itemHeight = itemBound.height();
        int dx;
        int dy;
        for (int i = 0; i < mRowCount; i++) {
            for (int j = 0; j < mColumnCount; j++) {
                dx = Math.round(j * itemWidth + (j > 0 ? (mHorizontalSpacing * j) : 0));
                dy = Math.round(i * itemHeight + (i > 0 ? (mVerticalSpacing * i) : 0));
                ir.translate(dx, dy);
                region.op(ir, Region.Op.UNION);
                ir.translate(-dx, -dy);
            }
        }
        return region;
    }

    @Override
    public int getIntrinsicWidth() {
        if (mColumnCount <= 0)
            return -1;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return -1;
        final int itemWidth = drawable.getIntrinsicWidth();
        return Math.round(mColumnCount * itemWidth + mHorizontalSpacing * (mColumnCount - 1));
    }

    @Override
    public int getIntrinsicHeight() {
        if (mRowCount <= 0)
            return -1;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return -1;
        final int itemHeight = drawable.getIntrinsicHeight();
        return Math.round(mRowCount * itemHeight + mVerticalSpacing * (mRowCount - 1));
    }

    @Override
    public int getMinimumWidth() {
        if (mColumnCount <= 0)
            return -1;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return -1;
        final int itemWidth = drawable.getMinimumWidth();
        return Math.round(mColumnCount * itemWidth + mHorizontalSpacing * (mColumnCount - 1));
    }

    @Override
    public int getMinimumHeight() {
        if (mRowCount <= 0)
            return -1;
        final Drawable drawable = getWrappedDrawable();
        if (drawable == null)
            return -1;
        final int itemHeight = drawable.getMinimumHeight();
        return Math.round(mRowCount * itemHeight + mVerticalSpacing * (mRowCount - 1));
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        // not support
    }

    @Override
    public boolean isAutoMirrored() {
        return false;
    }

    @Override
    public void setHotspot(float x, float y) {
        // not support
    }

    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        // not support
    }

    /**
     * 获取行数目
     *
     * @return 行数目
     */
    public int getRowCount() {
        return mRowCount;
    }

    /**
     * 设置行数目
     *
     * @param count 行数目
     */
    public void setRowCount(int count) {
        mRowCount = count;
        updateSize();
        invalidateSelf();
    }

    /**
     * 获取列数目
     *
     * @return 列数目
     */
    public int getColumnCount() {
        return mColumnCount;
    }

    /**
     * 设置列数目
     *
     * @param count 列数目
     */
    public void setColumnCount(int count) {
        mColumnCount = count;
        updateSize();
        invalidateSelf();
    }

    /**
     * 获取列间距
     *
     * @return 列间距
     */
    public float getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    /**
     * 设置列间距
     *
     * @param spacing 列间距
     */
    public void setHorizontalSpacing(float spacing) {
        mHorizontalSpacing = spacing;
        updateSize();
        invalidateSelf();
    }

    /**
     * 获取行间距
     *
     * @return 行间距
     */
    public float getVerticalSpacing() {
        return mVerticalSpacing;
    }

    /**
     * 设置行间距
     *
     * @param spacing 行间距
     */
    public void setVerticalSpacing(float spacing) {
        mVerticalSpacing = spacing;
        updateSize();
        invalidateSelf();
    }

    /**
     * 判断是否尺寸固定
     *
     * @return 尺寸固定
     */
    public boolean isConstantSize() {
        return mConstantSize;
    }

    /**
     * 设置尺寸是否固定
     *
     * @param constantSize 尺寸是否固定
     */
    public void setConstantSize(boolean constantSize) {
        mConstantSize = constantSize;
        updateSize();
        invalidateSelf();
    }
}
