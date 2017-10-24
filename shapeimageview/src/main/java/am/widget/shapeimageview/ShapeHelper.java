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

package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * 辅助器
 * API 21 及以上 使用 {@link android.view.View#setOutlineProvider(ViewOutlineProvider)} 方式实现
 * API 18 及以上 使用 {@link Canvas#clipPath(Path)} 方式实现
 * API 18 以下   使用 {@link Paint#setXfermode(Xfermode)} 方式实现
 * Created by Alex on 2017/1/8.
 */

class ShapeHelper {

    private static Path mPath;
    private static Canvas mBitmapCanvas;
    private static PorterDuffXfermode mXfermode;
    private static Paint mBitmapPaint;
    private Bitmap mBitmap;

    private static Bitmap createBitmap(Bitmap bitmap, int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            return createBitmapKitkat(bitmap, width, height);
        }
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                bitmap.recycle();
                try {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                } finally {
                    System.gc();
                }
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }

    @TargetApi(19)
    private static Bitmap createBitmapKitkat(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                bitmap.eraseColor(Color.TRANSPARENT);
                try {
                    bitmap.reconfigure(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    bitmap.recycle();
                    return null;
                } finally {
                    System.gc();
                }
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }

    void draw(ShapeImageView view, Canvas canvas) {
        ImageShape shape = view.getImageShape();
        if (shape == null) {
            view.doSuperDraw(canvas);
            return;
        }
        if (shape.isOutlineEnable()) {
            view.doSuperDraw(canvas);
            return;
        }
        if (shape.isClipPathEnable()) {
            if (mPath == null)
                mPath = new Path();
            mPath.rewind();
            shape.makeShapeByClipPath(view, mPath);
            int saveCount = canvas.save();
            canvas.clipPath(mPath);
            view.doSuperDraw(canvas);
            canvas.restoreToCount(saveCount);
            return;
        }
        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();
        if (mBitmap == null || width == 0 || height == 0) {
            view.doSuperDraw(canvas);
            return;
        }
        int saveCount = canvas.saveLayer(0f, 0f, width, height, null, Canvas.ALL_SAVE_FLAG);
        view.doSuperDraw(canvas);
        mBitmapPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        mBitmapPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    void updateSize(ShapeImageView view, int w, int h) {
        ImageShape shape = view.getImageShape();
        if (shape == null)
            return;
        if (shape.isOutlineEnable())
            return;
        if (shape.isClipPathEnable())
            return;
        update(view, shape, w, h);
    }

    void updateImageShape(ShapeImageView view, ImageShape shape) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            view.setOutlineProvider(null);
            view.setClipToOutline(false);
        }
        if (shape == null)
            return;
        if (shape.isOutlineEnable()) {
            if (android.os.Build.VERSION.SDK_INT < 21)
                return;
            setOutlineProvider(view, shape);
            return;
        }
        update(view, shape, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    @TargetApi(21)
    private void setOutlineProvider(ShapeImageView view, ImageShape shape) {

        view.setOutlineProvider(new ImageShapeViewOutlineProvider(shape));
        view.setClipToOutline(true);
    }

    private void update(ShapeImageView view, ImageShape shape, int width, int height) {
        if (shape == null)
            return;
        if (width == 0 || height == 0)
            return;
        // 修改或创建Bitmap
        mBitmap = createBitmap(mBitmap, width, height);
        if (mBitmap == null)
            return;
        if (mBitmapCanvas == null)
            mBitmapCanvas = new Canvas();
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBitmapPaint.setColor(Color.BLACK);
        }
        if (mXfermode == null) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        }
        mBitmapCanvas.setBitmap(mBitmap);
        shape.makeShapeByPorterDuff(view, mBitmapCanvas, mBitmapPaint);
    }

    void invalidateImageShape(ShapeImageView view) {
        ImageShape shape = view.getImageShape();
        if (shape == null)
            return;
        if (shape.isOutlineEnable()) {
            Compat.invalidateOutline(view);
            view.invalidate();
            return;
        }
        if (shape.isClipPathEnable()) {
            view.invalidate();
            return;
        }
        if (mBitmap == null)
            return;
        mBitmap.eraseColor(Color.TRANSPARENT);
        mBitmapCanvas.setBitmap(mBitmap);
        shape.makeShapeByPorterDuff(view, mBitmapCanvas, mBitmapPaint);
    }

    void onDetachedFromView() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

    @TargetApi(21)
    private static class ImageShapeViewOutlineProvider extends ViewOutlineProvider {

        private ImageShape shape;

        ImageShapeViewOutlineProvider(ImageShape shape) {
            this.shape = shape;
        }

        @Override
        public void getOutline(View v, Outline outline) {
            ShapeImageView view = (ShapeImageView) v;
            shape.makeShapeByOutline(view, outline);
        }
    }
}
