/*
 * Copyright (C) 2020 AlexMofer
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

/**
 * 安全绘制的ImageView
 * Created by Alex on 2020/1/20.
 */
@SuppressWarnings("unused")
public class SafeDrawImageView extends FixedSizeImageView {

    private Drawable mDrawableError;

    public SafeDrawImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public SafeDrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public SafeDrawImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SafeDrawImageView(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final Drawable drawableError;
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.SafeDrawImageView, defStyleAttr, defStyleRes);
        drawableError = custom.getDrawable(R.styleable.SafeDrawImageView_sdiError);
        custom.recycle();
        if (drawableError != null) {
            drawableError.setCallback(this);
            mDrawableError = drawableError;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawableError == null)
            return;
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        final int intrinsicWidth = mDrawableError.getIntrinsicWidth();
        final int intrinsicHeight = mDrawableError.getIntrinsicHeight();
        int drawableWidth = intrinsicWidth < 0 ? getMeasuredWidth() : intrinsicWidth;
        int drawableHeight = intrinsicHeight < 0 ? getMeasuredHeight() : intrinsicHeight;
        if (drawableWidth > measuredWidth || drawableHeight > measuredHeight) {
            // 缩小
            final float scale = Math.min(measuredWidth * 1f / drawableWidth,
                    measuredHeight * 1f / drawableHeight);
            drawableWidth = Math.round(drawableWidth * scale);
            drawableHeight = Math.round(drawableHeight * scale);
        }
        final int left = Math.round(measuredWidth * 0.5f - drawableWidth * 0.5f);
        final int top = Math.round(measuredHeight * 0.5f - drawableHeight * 0.5f);
        mDrawableError.setBounds(left, top,
                left + drawableWidth, top + drawableHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            if (mDrawableError != null)
                mDrawableError.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        final int[] state = getDrawableState();
        if (mDrawableError != null && mDrawableError.isStateful())
            mDrawableError.setState(state);
        super.drawableStateChanged();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mDrawableError;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void dispatchDrawableHotspotChanged(float x, float y) {
        super.dispatchDrawableHotspotChanged(x, y);
        if (mDrawableError != null)
            mDrawableError.setHotspot(x, y);
    }

    /**
     * 获取错误状态图片
     *
     * @return 错误状态图片
     */
    public Drawable getDrawableError() {
        return mDrawableError;
    }

    /**
     * 设置错误状态图片
     *
     * @param error 错误状态图片
     */
    public void setDrawableError(Drawable error) {
        if (mDrawableError == error)
            return;
        if (mDrawableError != null)
            mDrawableError.setCallback(null);
        mDrawableError = error;
        if (mDrawableError != null)
            mDrawableError.setCallback(this);
        invalidate();
    }
}
