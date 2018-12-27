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

package am.widget.multifunctionalimageview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 裁剪ImageView
 * Created by Alex on 2018/12/25.
 */
@SuppressWarnings("unused")
@SuppressLint("AppCompatCustomView")
public class ClipImageView extends ImageView {

    private final Path mClipPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private final Path mOutlinePath = new Path();
    private ClipOutlineProvider mProvider;
    private float mBorderWidth;
    private ColorStateList mBorderColor;

    public ClipImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.ClipImageView,
                defStyleAttr, defStyleRes);
        final int type = custom.getInt(R.styleable.ClipImageView_civClipType, 0);
        final float radius = custom.getDimension(R.styleable.ClipImageView_civRoundRectRadius,
                0);
        final float width = custom.getDimension(R.styleable.ClipImageView_civBorderWidth,
                0);
        final ColorStateList color = custom.getColorStateList(
                R.styleable.ClipImageView_civBorderColor);
        final String name = custom.getString(R.styleable.ClipImageView_civClipOutlineProvider);
        custom.recycle();
        mClipPath.setFillType(Path.FillType.EVEN_ODD);
        mOutlinePath.setFillType(Path.FillType.EVEN_ODD);
        switch (type) {
            default:
                break;
            case 1:
                mProvider = ClipOutlineProvider.CIRCLE;
                break;
            case 2:
                mProvider = ClipOutlineProvider.OVAL;
                break;
            case 3:
                mProvider = ClipOutlineProvider.FULL_ROUND_RECT;
                break;
            case 4:
                mProvider = new RoundRectClipOutlineProvider(radius);
                break;
        }
        final ClipOutlineProvider provider = ConstructorHelper.newInstance(context, name,
                isInEditMode(), this, ClipOutlineProvider.class,
                attrs, defStyleAttr, defStyleRes);
        if (provider != null)
            mProvider = provider;
        mBorderWidth = width;
        mBorderColor = color;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateClipOutline(false);
    }

    private void invalidateClipOutline(boolean redraw) {
        if (mProvider == null)
            return;
        mClipPath.reset();
        mClipPath.addRect(-1, -1, getWidth() + 1, getHeight() + 1,
                Path.Direction.CW);
        mOutlinePath.reset();
        mProvider.getOutline(this, mOutlinePath);
        mClipPath.addPath(mOutlinePath);
        if (redraw)
            invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mProvider == null) {
            super.draw(canvas);
            return;
        }
        final int layer = Compat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(mXfermode);
        final ColorFilter filter = mPaint.getColorFilter();
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(layer);
        mPaint.setColorFilter(filter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && mBorderWidth > 0) {
            mPaint.setColor(Color.BLACK);
            mPaint.setXfermode(null);
            if (mBorderColor != null)
                mPaint.setColor(mBorderColor.getColorForState(getDrawableState(),
                        mBorderColor.getDefaultColor()));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeMiter(10);
            mPaint.setStrokeWidth(mBorderWidth * 2);
            canvas.drawPath(mOutlinePath, mPaint);
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        if (mBorderWidth > 0) {
            mPaint.setColor(Color.BLACK);
            mPaint.setXfermode(null);
            if (mBorderColor != null)
                mPaint.setColor(mBorderColor.getColorForState(getDrawableState(),
                        mBorderColor.getDefaultColor()));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeMiter(10);
            mPaint.setStrokeWidth(mBorderWidth * 2);
            canvas.drawPath(mOutlinePath, mPaint);
        }
        super.onDrawForeground(canvas);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        super.setColorFilter(cf);
    }

    public void setClipOutlineProvider(ClipOutlineProvider provider) {
        mProvider = provider;
        invalidateClipOutline();
    }

    public void invalidateClipOutline() {
        invalidateClipOutline(true);
    }

    public void setBorderWidth(float width) {
        mBorderWidth = width;
        if (mProvider != null)
            invalidate();
    }

    public void setBorderColor(ColorStateList color) {
        mBorderColor = color;
        if (mProvider != null)
            invalidate();
    }

    public void setBorderColor(int color) {
        setBorderColor(ColorStateList.valueOf(color));
    }
}
