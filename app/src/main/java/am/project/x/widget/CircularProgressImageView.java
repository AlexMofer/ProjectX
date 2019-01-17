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

package am.project.x.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.ArrayList;

import am.project.x.R;

/**
 * 载入动画ImageView
 * Created by Alex on 2016/12/27.
 */
@SuppressWarnings("unused")
public class CircularProgressImageView extends AppCompatImageView implements Animatable {

    public static final int LARGE = CircularProgressDrawable.LARGE;
    public static final int DEFAULT = CircularProgressDrawable.DEFAULT;
    private static final int SIZE_LARGE = 56;
    private static final int SIZE_DEFAULT = 40;
    private static final int DEFAULT_COLOR = 0xFFFAFAFA;
    private static final int SHADOW_ELEVATION = 2;
    private float mSizeLarge;
    private float mSizeNormal;
    private float mSize;
    private int mPaddingEdge;
    private boolean mShowShadowsCircle = false;
    private float mElevation;
    private CircularProgressDrawable mProgress;
    private ShapeDrawable mBackground;

    public CircularProgressImageView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public CircularProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public CircularProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mProgress = new CircularProgressDrawable(context);
        mProgress.setBackgroundColor(0x00000000);
        mProgress.setAlpha(255);
        mProgress.setCallback(this);
        mProgress.setArrowEnabled(false);
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSizeLarge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SIZE_LARGE,
                metrics);
        mSizeNormal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SIZE_DEFAULT,
                metrics);
        mSize = mSizeNormal;
        final ArrayList<Integer> colors = new ArrayList<>();
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.CircularProgressImageView, defStyleAttr, 0);
        final int style = custom.getInt(R.styleable.CircularProgressImageView_cpStyle,
                DEFAULT);
        mProgress.setStyle(style);
        if (style == LARGE)
            mSize = mSizeLarge;
        else
            mSize = mSizeNormal;
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpStrokeWidth)) {
            mProgress.setStrokeWidth(
                    custom.getDimension(R.styleable.CircularProgressImageView_cpStrokeWidth,
                            1f));
            mSize = -1;
        }
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpCenterRadius)) {
            mProgress.setCenterRadius(
                    custom.getDimension(R.styleable.CircularProgressImageView_cpCenterRadius,
                            1f));
            mSize = -1;
        }
        mPaddingEdge = custom.getDimensionPixelSize(
                R.styleable.CircularProgressImageView_cpPaddingEdge, mPaddingEdge);
        boolean autoStart = custom.getBoolean(
                R.styleable.CircularProgressImageView_cpAutoStart, true);
        int color = custom.getColor(
                R.styleable.CircularProgressImageView_cpShadowsCircleColor, DEFAULT_COLOR);
        float elevation = custom.getDimension(R.styleable.CircularProgressImageView_cpElevation,
                getResources().getDisplayMetrics().density * SHADOW_ELEVATION);
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpSchemeColor1)) {
            colors.add(custom.getColor(
                    R.styleable.CircularProgressImageView_cpSchemeColor1, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpSchemeColor2)) {
            colors.add(custom.getColor(
                    R.styleable.CircularProgressImageView_cpSchemeColor2, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpSchemeColor3)) {
            colors.add(custom.getColor(
                    R.styleable.CircularProgressImageView_cpSchemeColor3, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpSchemeColor4)) {
            colors.add(custom.getColor(
                    R.styleable.CircularProgressImageView_cpSchemeColor4, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.CircularProgressImageView_cpSchemeColor5)) {
            colors.add(custom.getColor(
                    R.styleable.CircularProgressImageView_cpSchemeColor5, Color.BLACK));
        }
        custom.recycle();

        setElevationCompat(elevation);
        Drawable background = getBackground();
        if (background == null)
            setShadowsCircleBackground(color);
        final int size = colors.size();
        if (size > 0) {
            int[] colorArray = new int[size];
            for (int i = 0; i < size; i++) {
                colorArray[i] = colors.get(i);
            }
            mProgress.setColorSchemeColors(colorArray);
        }
        setImageDrawable(mProgress);
        if (autoStart)
            start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size;
        if (mSize <= 0)
            size = (int) Math.ceil(
                    (mProgress.getCenterRadius() + mProgress.getStrokeWidth() + mPaddingEdge) * 2);
        else
            size = (int) Math.ceil(mSize);
        if (mShowShadowsCircle && !elevationSupported()) {
            size += Math.round(mElevation * 2);
        }
        setMeasuredDimension(resolveSize(size, widthMeasureSpec),
                resolveSize(size, heightMeasureSpec));
    }

    @Override
    @TargetApi(16)
    public void setBackground(Drawable background) {
        mShowShadowsCircle = false;
        super.setBackground(background);
    }

    @Override
    public void setBackgroundColor(int color) {
        mShowShadowsCircle = false;
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resId) {
        mShowShadowsCircle = false;
        super.setBackgroundResource(resId);
    }

    @Deprecated
    @Override
    public void setBackgroundDrawable(Drawable background) {
        mShowShadowsCircle = false;
        super.setBackgroundDrawable(background);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mProgress.setAlpha((int) (255 * alpha));
    }

    @Override
    public void start() {
        if (!mProgress.isRunning()) {
            mProgress.start();
        }
    }

    @Override
    public void stop() {
        if (mProgress.isRunning()) {
            mProgress.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return mProgress.isRunning();
    }

    /**
     * 设置圆形阴影背景颜色
     *
     * @param color 背景颜色
     */
    public void setShadowsCircleBackground(int color) {
        if (mBackground == null) {
            if (elevationSupported()) {
                mBackground = new ShapeDrawable(new OvalShape());
            } else {
                mBackground = new ShapeDrawable(new OvalShadow());
            }
        }
        mBackground.getPaint().setColor(color);
        mShowShadowsCircle = true;
        super.setBackgroundDrawable(mBackground);
    }

    /**
     * Sets the base elevation of this view, in pixels.
     * 仅对使用默认圆形背景有效{@link #setShadowsCircleBackground(int)}
     */
    public void setElevationCompat(float elevation) {
        if (elevationSupported()) {
            setElevation(elevation);
        } else {
            mElevation = elevation;
        }
    }

    private boolean elevationSupported() {
        return Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景颜色
     */
    public void setDrawableBackgroundColor(int backgroundColor) {
        mProgress.setBackgroundColor(backgroundColor);
    }

    /**
     * 设置样式
     *
     * @param style 样式
     */
    public void setStyle(int style) {
        mProgress.setStyle(style);
        if (style == LARGE)
            mSize = mSizeLarge;
        else
            mSize = mSizeNormal;
        requestLayout();
        invalidate();
    }

    /**
     * 设置变换颜色
     *
     * @param schemeColors 变换颜色
     */
    public void setColorSchemeColors(int... schemeColors) {
        mProgress.setColorSchemeColors(schemeColors);
    }

    /**
     * Returns the stroke width for the progress spinner in pixels.
     *
     * @return stroke width in pixels
     */
    public float getStrokeWidth() {
        return mProgress.getStrokeWidth();
    }

    /**
     * Sets the stroke width for the progress spinner in pixels.
     *
     * @param strokeWidth stroke width in pixels
     */
    public void setStrokeWidth(float strokeWidth) {
        mProgress.setStrokeWidth(strokeWidth);
        requestLayout();
        invalidate();
    }

    /**
     * Returns the center radius for the progress spinner in pixels.
     *
     * @return center radius in pixels
     */
    public float getCenterRadius() {
        return mProgress.getCenterRadius();
    }

    /**
     * Sets the center radius for the progress spinner in pixels. If set to 0, this mProgress will
     * fill the bounds when drawn.
     *
     * @param centerRadius center radius in pixels
     */
    public void setCenterRadius(float centerRadius) {
        mProgress.setCenterRadius(centerRadius);
        requestLayout();
        invalidate();
    }

    private class OvalShadow extends OvalShape {

        private static final int FILL_SHADOW_COLOR = 0x3D000000;
        private final Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF mOvalRectF = new RectF();
        private final Matrix mMatrix = new Matrix();
        private RadialGradient mRadialGradient;

        OvalShadow() {
            mRadialGradient = new RadialGradient(10, 10, 10,
                    new int[]{FILL_SHADOW_COLOR, FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    new float[]{0, 0.6f, 1f}, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final float elevation = mElevation;
            final RectF rect = rect();
            mMatrix.reset();
            mMatrix.setScale(rect.width() * 0.05f, rect.height() * 0.05f);
            mOvalRectF.left = rect.left + elevation;
            mOvalRectF.top = rect.top + elevation;
            mOvalRectF.right = rect.right - elevation;
            mOvalRectF.bottom = rect.bottom - elevation;
            mRadialGradient.setLocalMatrix(mMatrix);
            canvas.drawOval(rect, mShadowPaint);
            canvas.drawOval(mOvalRectF, paint);
        }
    }
}