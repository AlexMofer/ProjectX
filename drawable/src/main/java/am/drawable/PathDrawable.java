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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 路径图片
 * Created by Alex on 2018/12/28.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PathDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final Matrix mMatrix = new Matrix();
    private final Path mDrawPath = new Path();
    private float mWidth;
    private float mHeight;
    private ColorStateList mColor;

    public PathDrawable() {
        this(0, 0);
    }

    public PathDrawable(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = resources.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.PathDrawable);
        mWidth = custom.getDimension(R.styleable.PathDrawable_android_width, 0);
        mHeight = custom.getDimension(R.styleable.PathDrawable_android_height, 0);
        final String data = custom.getString(R.styleable.PathDrawable_android_pathData);
        final int fillType = custom.getInt(R.styleable.PathDrawable_pdFillType, 0);
        final int style = custom.getInt(R.styleable.PathDrawable_pdStyle, 0);
        mColor = custom.getColorStateList(R.styleable.PathDrawable_android_color);
        final float strokeWidth = custom.getDimension(R.styleable.PathDrawable_pdStrokeWidth,
                mPaint.getStrokeWidth());
        final int strokeLineCap = custom.getInt(R.styleable.PathDrawable_android_strokeLineCap,
                0);
        final int strokeLineJoin = custom.getInt(R.styleable.PathDrawable_android_strokeLineJoin,
                0);
        final float strokeMiterLimit = custom.getFloat(
                R.styleable.PathDrawable_android_strokeMiterLimit, 0);
        custom.recycle();
        switch (fillType) {
            default:
            case 0:
                mPath.setFillType(Path.FillType.WINDING);
                break;
            case 1:
                mPath.setFillType(Path.FillType.EVEN_ODD);
                break;
            case 2:
                mPath.setFillType(Path.FillType.INVERSE_WINDING);
                break;
            case 3:
                mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                break;
        }
        switch (style) {
            default:
            case 0:
                mPaint.setStyle(Paint.Style.FILL);
                break;
            case 1:
                mPaint.setStyle(Paint.Style.STROKE);
                break;
            case 2:
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
        }
        mPaint.setStrokeWidth(strokeWidth);
        switch (strokeLineCap) {
            default:
            case 0:
                mPaint.setStrokeCap(Paint.Cap.BUTT);
                break;
            case 1:
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case 2:
                mPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
        }
        switch (strokeLineJoin) {
            default:
            case 0:
                mPaint.setStrokeJoin(Paint.Join.MITER);
                break;
            case 1:
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                break;
            case 2:
                mPaint.setStrokeJoin(Paint.Join.BEVEL);
                break;
        }
        mPaint.setStrokeMiter(strokeMiterLimit);
        if (TextUtils.isEmpty(data))
            return;
        final String[] items = data.split(";");

        for (String item : items) {
            System.out.println("lalalalla----------------------item:" + item);
        }


        // TODO


        mPath.moveTo(mWidth * 0.5f, 0);
        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
    }

    private void setPathData(String data) {
        // TODO
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateDrawPath(bounds);
    }

    private void updateDrawPath(Rect bounds) {
        mDrawPath.reset();
        mDrawPath.setFillType(mPath.getFillType());
        if (bounds == null || bounds.isEmpty() || mWidth == 0 || mHeight == 0)
            return;
        mMatrix.reset();
        mMatrix.postTranslate(bounds.left, bounds.top);
        mMatrix.postScale(bounds.width() / mWidth, bounds.height() / mHeight);
        if (mPath.isEmpty())
            return;
        mPath.transform(mMatrix, mDrawPath);
    }

    @Override
    public void draw(@SuppressWarnings("NullableProblems") Canvas canvas) {
        if (mDrawPath.isEmpty())
            return;
        if (mColor != null)
            mPaint.setColor(mColor.getColorForState(getState(), mColor.getDefaultColor()));
        canvas.drawPath(mDrawPath, mPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return mPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.round(mWidth);
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.round(mHeight);
    }

    @Override
    public boolean isStateful() {
        return mColor != null && mColor.isStateful();
    }

    /**
     * 获取路径
     *
     * @return 路径
     */
    public Path getPath() {
        return mPath;
    }

    /**
     * 获取画笔
     *
     * @return 画笔
     */
    public Paint getPaint() {
        return mPaint;
    }

    /**
     * 设置尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(float width, float height) {
        if (mWidth == width && mHeight == height)
            return;
        mWidth = width;
        mHeight = height;
        invalidatePath();
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(ColorStateList color) {
        mColor = color;
        invalidateSelf();
    }

    /**
     * 刷新路径
     */
    public void invalidatePath() {
        updateDrawPath(getBounds());
        invalidateSelf();
    }
}
