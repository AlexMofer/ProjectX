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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 进展图
 * Created by Alex on 2016/12/29.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ProgressDrawable extends Drawable {

    public static final int TYPE_LEFT_TO_RIGHT = 1;
    public static final int TYPE_RIGHT_TO_LEFT = 2;
    public static final int TYPE_CENTER_HORIZONTAL = 3;
    public static final int TYPE_TOP_TO_BOTTOM = 4;
    public static final int TYPE_BOTTOM_TO_TOP = 8;
    public static final int TYPE_CENTER_VERTICAL = 12;
    public static final int TYPE_CENTER = TYPE_CENTER_HORIZONTAL | TYPE_CENTER_VERTICAL;
    public static final int SHAPE_RECT = 0;
    public static final int SHAPE_OVAL = 1;
    private static final int TYPE_NULL = 0;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mBackgroundDrawable;
    private Drawable mProgressDrawable;
    private int mMax = 100;
    private int mProgress = 0;
    private int mType = TYPE_LEFT_TO_RIGHT;
    private int mShape = SHAPE_RECT;
    private RectF mProgressBounds = new RectF();

    public ProgressDrawable(Drawable progress) {
        mProgressDrawable = progress;
        initProgressPaint();
    }

    public ProgressDrawable(Drawable background, Drawable progress) {
        this(progress);
        mBackgroundDrawable = background;
    }

    private void initProgressPaint() {
        mPaint.setShader(null);
        if (mProgressDrawable == null)
            return;
        Bitmap bitmap;
        if (mProgressDrawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mProgressDrawable;
            bitmap = bitmapDrawable.getBitmap();
        } else {
            try {
                bitmap = Bitmap.createBitmap(mProgressDrawable.getIntrinsicWidth(),
                        mProgressDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                mProgressDrawable.setBounds(0, 0,
                        mProgressDrawable.getIntrinsicWidth(),
                        mProgressDrawable.getIntrinsicHeight());
                mProgressDrawable.draw(canvas);
            } catch (OutOfMemoryError error) {
                bitmap = null;
            }
        }
        if (bitmap == null)
            return;
        BitmapShader mBitmapShader = new BitmapShader(bitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mPaint.setShader(mBitmapShader);
    }

    @Override
    public int getIntrinsicWidth() {
        return mProgressDrawable == null ?
                super.getIntrinsicWidth() : mProgressDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mProgressDrawable == null ?
                super.getIntrinsicHeight() : mProgressDrawable.getIntrinsicHeight();
    }

    @Override
    public void draw(@SuppressWarnings("NullableProblems") Canvas canvas) {
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setBounds(getBounds());
            mBackgroundDrawable.draw(canvas);
        }
        if (mProgressDrawable == null)
            return;
        final Rect bounds = getBounds();

        final int width = bounds.width();
        final int height = bounds.height();
        final int progressWidth = mProgressDrawable.getIntrinsicWidth();
        final int progressHeight = mProgressDrawable.getIntrinsicHeight();
        canvas.save();
        if (width != progressWidth || height != progressHeight) {
            float scaleX = (float) width / progressWidth;
            float scaleY = (float) height / progressHeight;
            canvas.scale(scaleX, scaleY);
        }
        getProgressBounds(mProgressBounds, mProgressDrawable, mType);
        onDrawProgress(canvas, mProgressBounds, mPaint, mShape);
        canvas.restore();
    }

    /**
     * 获取进度绘制区域
     * 不要使用getBounds()获取的Rect的right与bottom，因为存在缩放问题
     *
     * @param progressBounds 区域
     * @param progress       进度图
     * @param type           类型
     */
    protected void getProgressBounds(RectF progressBounds, Drawable progress, int type) {
        final Rect bounds = getBounds();
        final int left = bounds.left;
        final int top = bounds.top;
        final int progressWidth = progress.getIntrinsicWidth();
        final int progressHeight = progress.getIntrinsicHeight();
        float progressLeft;
        float progressTop;
        float progressRight;
        float progressBottom;
        float offset;
        switch (type & TYPE_CENTER_HORIZONTAL) {
            case TYPE_LEFT_TO_RIGHT:
                progressLeft = left;
                progressRight = left + (float) progressWidth * mProgress / mMax;
                break;
            case TYPE_RIGHT_TO_LEFT:
                progressRight = left + progressWidth;
                progressLeft = progressRight - (float) progressWidth * mProgress / mMax;
                break;
            case TYPE_CENTER_HORIZONTAL:
                offset = (progressWidth - (float) progressWidth * mProgress / mMax) * 0.5f;
                progressLeft = left + offset;
                progressRight = left + progressWidth - offset;
                break;
            default:
            case TYPE_NULL:
                progressLeft = left;
                progressRight = left + progressWidth;
                break;
        }
        switch (type & TYPE_CENTER_VERTICAL) {
            case TYPE_TOP_TO_BOTTOM:
                progressTop = top;
                progressBottom = top + (float) progressHeight * mProgress / mMax;
                break;
            case TYPE_BOTTOM_TO_TOP:
                progressBottom = top + progressHeight;
                progressTop = progressBottom - (float) progressHeight * mProgress / mMax;
                break;
            case TYPE_CENTER_VERTICAL:
                offset = (progressHeight - (float) progressHeight * mProgress / mMax) * 0.5f;
                progressTop = top + offset;
                progressBottom = top + progressHeight - offset;
                break;
            default:
            case TYPE_NULL:
                progressTop = top;
                progressBottom = top + progressHeight;
                break;
        }
        progressBounds.set(progressLeft, progressTop, progressRight, progressBottom);
    }

    /**
     * 绘制进度
     *
     * @param canvas         画布
     * @param progressBounds 区域
     * @param paint          画笔
     * @param shape          形状
     */
    protected void onDrawProgress(Canvas canvas, RectF progressBounds, Paint paint, int shape) {
        switch (shape) {
            case SHAPE_RECT:
                canvas.drawRect(progressBounds.left, progressBounds.top,
                        progressBounds.right, progressBounds.bottom, paint);
                break;
            case SHAPE_OVAL:
                canvas.drawOval(progressBounds, paint);
                break;
        }

    }

    @Override
    public void setAlpha(int alpha) {
        if (mBackgroundDrawable != null)
            mBackgroundDrawable.setAlpha(alpha);
        if (mProgressDrawable != null)
            mProgressDrawable.setAlpha(alpha);
        initProgressPaint();
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mBackgroundDrawable != null)
            mBackgroundDrawable.setColorFilter(colorFilter);
        if (mProgressDrawable != null)
            mProgressDrawable.setColorFilter(colorFilter);
        initProgressPaint();
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        if (mBackgroundDrawable != null)
            return mBackgroundDrawable.getOpacity();
        if (mProgressDrawable != null)
            return mProgressDrawable.getOpacity();
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        mMax = max;
        invalidateSelf();
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        mProgress = progress;
        invalidateSelf();
    }

    /**
     * 设置背景
     *
     * @param background 背景
     */
    public void setBackground(Drawable background) {
        mBackgroundDrawable = background;
        invalidateSelf();
    }

    /**
     * 设置进度图片
     *
     * @param progress 进度图片
     */
    public void setProgressDrawable(Drawable progress) {
        mProgressDrawable = progress;
        initProgressPaint();
        invalidateSelf();
    }

    /**
     * 修改绘图模式
     *
     * @param type 模式
     */
    public void setType(int type) {
        mType = type;
        invalidateSelf();
    }

    /**
     * 设置形状
     *
     * @param shape 形状
     */
    public void setShape(int shape) {
        mShape = shape;
        invalidateSelf();
    }
}
