package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

/**
 * ShapeHelper
 * Created by Alex on 2016/6/2.
 */
class ShapeHelper {

    private Drawable mDrawable;
    private ImageView.ScaleType mScaleType;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Canvas mBitmapCanvas;
    private Matrix mMatrix;
    private Matrix mImageMatrix;
    private Paint mBorderPaint;

    private int mViewWidth;
    private int mViewHeight;
    private int paddingStart = 0;
    private int paddingEnd = 0;
    private int paddingTop = 0;
    private int paddingBottom = 0;

    ShapeHelper() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        if (!isLollipop()) {
            initShapeBase();
        }
    }

    void initShape(ShapeImageView view, ImageShape shape) {
        if (shape == null)
            return;
        if (canUseLollipopWay(shape)) {
            initShapeLollipop(view, shape);
        } else {
            if (isLollipop())
                initShapeBase();
        }
    }

    private void initShapeBase() {
        mScaleType = ImageView.ScaleType.FIT_CENTER;
        mBitmapCanvas = new Canvas();
        mMatrix = new Matrix();
        mImageMatrix = new Matrix();
    }

    @TargetApi(21)
    private void initShapeLollipop(final ShapeImageView imageView, final ImageShape shape) {

        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (shape != null)
                    shape.makeShapeLollipop(imageView, outline);
            }
        });
        imageView.setClipToOutline(true);
    }

    void sizeChanged(int width, int height, ImageShape shape, ShapeImageView view) {
        if (shape == null || canUseLollipopWay(shape))
            return;
        if (mViewWidth != width || mViewHeight != height) {
            mViewWidth = width;
            mViewHeight = height;
            if (!view.isCatchBitmapOnly()) {
                mBitmapPaint.setShader(null);
                mBitmap = view.getDrawable() == null ? null : configBitmap(mBitmap,
                        view.getWidth(), view.getHeight());
            }
            mImageMatrix.set(view.getImageMatrix());
            invalidateBitmap(mBitmapPaint, mBitmap, mMatrix, mBitmapCanvas, mImageMatrix,
                    width, height, view.isCatchBitmapOnly(), view.getDrawable(),
                    view.getScaleType(), ShapeCompat.getPaddingStart(view), view.getPaddingTop(),
                    ShapeCompat.getPaddingEnd(view), view.getPaddingBottom());
        }
    }


    void setScaleType(ImageView.ScaleType scaleType, ImageShape shape, ShapeImageView view) {
        if (shape == null || canUseLollipopWay(shape))
            return;
        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            if (!view.isCatchBitmapOnly() && view.getDrawable() != null && mBitmap == null) {
                mBitmap = ShapeHelper.configBitmap(null, view.getWidth(), view.getHeight());
            }
            mImageMatrix.set(view.getImageMatrix());
            invalidateBitmap(mBitmapPaint, mBitmap, mMatrix, mBitmapCanvas, mImageMatrix,
                    view.getWidth(), view.getHeight(), view.isCatchBitmapOnly(),
                    view.getDrawable(), scaleType, ShapeCompat.getPaddingStart(view),
                    view.getPaddingTop(), ShapeCompat.getPaddingEnd(view), view.getPaddingBottom());
        }
    }

    void setPadding(int left, int top, int right, int bottom, ImageShape shape,
                    ShapeImageView view) {
        if (shape == null || canUseLollipopWay(shape))
            return;
        if (paddingStart != 0 || paddingTop != top || paddingEnd != right
                || paddingBottom != right) {
            paddingStart = left;
            paddingTop = top;
            paddingEnd = right;
            paddingBottom = bottom;
            if (!view.isCatchBitmapOnly() && view.getDrawable() != null && mBitmap == null) {
                mBitmap = ShapeHelper.configBitmap(null, view.getWidth(), view.getHeight());
            }
            mImageMatrix.set(view.getImageMatrix());
            invalidateBitmap(mBitmapPaint, mBitmap, mMatrix, mBitmapCanvas, mImageMatrix,
                    view.getWidth(), view.getHeight(), view.isCatchBitmapOnly(),
                    view.getDrawable(), view.getScaleType(), left, top, right, bottom);
        }
    }

    void detachedFromWindow(ImageShape shape, ShapeImageView view) {
        if (shape == null || canUseLollipopWay(shape))
            return;
        if (!view.isCatchBitmapOnly()) {
            if (mBitmap != null && !mBitmap.isRecycled()
                    && mBitmap.isMutable()) {
                mBitmap.eraseColor(Color.TRANSPARENT);
                mBitmap.recycle();
                mBitmap = null;
                System.gc();
            }
        }
    }

    void updateBitmap(ImageShape shape, ShapeImageView view) {
        if (mDrawable != view.getDrawable()) {
            mDrawable = view.getDrawable();
            forceUpdateBitmap(shape, view);
        }
    }

    void forceUpdateBitmap(ImageShape shape, ShapeImageView view) {
        if (shape == null || canUseLollipopWay(shape))
            return;
        if (view.isCatchBitmapOnly()) {
            mBitmapPaint.setShader(null);
            mBitmap = getBitmap(view.getDrawable());
        }
        if (!view.isCatchBitmapOnly() && view.getDrawable() != null && mBitmap == null) {
            mBitmap = configBitmap(null, view.getWidth(), view.getHeight());
        }
        mImageMatrix.set(view.getImageMatrix());
        invalidateBitmap(mBitmapPaint, mBitmap, mMatrix, mBitmapCanvas, mImageMatrix,
                view.getWidth(), view.getHeight(), view.isCatchBitmapOnly(), view.getDrawable(),
                view.getScaleType(), ShapeCompat.getPaddingStart(view), view.getPaddingTop(),
                ShapeCompat.getPaddingEnd(view), view.getPaddingBottom());
    }


    boolean needOnDraw(ShapeImageView view, Canvas canvas, ImageShape shape) {
        if (shape == null || canUseLollipopWay(shape))
            return true;
        if (mBitmapPaint.getShader() == null) {
            return true;
        }
        shape.makeShapeBase(view, canvas, mBitmapPaint);
        return false;
    }

    void drawBorder(ShapeImageView view, Canvas canvas, ImageShape shape) {
        if (shape == null)
            return;
        if (canUseLollipopWay(shape)) {
            mBorderPaint.setColor(view.getBorderColor());
            shape.drawBorderLollipop(view, canvas, view.getBorderWidth(), mBorderPaint);
        } else {
            mBorderPaint.setColor(view.getBorderColor());
            shape.drawBorderBase(view, canvas, view.getBorderWidth(), mBorderPaint);
        }
    }

    void setColorFilter(ColorFilter cf) {
        mBitmapPaint.setColorFilter(cf);
        mBorderPaint.setColorFilter(cf);
    }

    private boolean canUseLollipopWay(ImageShape shape) {
        return !shape.alwaysUseBaseWay() && isLollipop();
    }

    private static boolean isLollipop() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }


    private static Bitmap getBitmap(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        return null;
    }

    private static Bitmap configBitmap(Bitmap bitmap, int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            return configBitmapKitkat(bitmap, width, height);
        }
        if (width == 0 || height == 0) {
            return null;
        }
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                bitmap.eraseColor(Color.TRANSPARENT);
                bitmap.recycle();
                try {
                    bitmap = Bitmap.createBitmap(width, height,
                            Bitmap.Config.ARGB_8888);

                } catch (OutOfMemoryError e) {
                    return null;
                }
                System.gc();
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }

    @TargetApi(19)
    private static Bitmap configBitmapKitkat(Bitmap bitmap, int width, int height) {
        if (width == 0 || height == 0) {
            return null;
        }
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                bitmap.eraseColor(Color.TRANSPARENT);
                try {
                    bitmap.reconfigure(width, height,
                            Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                }
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }

    private static void invalidateBitmap(Paint paint, Bitmap bitmap, Matrix matrix, Canvas canvas,
                                         Matrix imageMatrix, int width, int height,
                                         boolean isCatchBitmapOnly, Drawable drawable,
                                         ImageView.ScaleType scaleType, int paddingStart,
                                         int paddingTop, int paddingEnd, int paddingBottom) {
        if (paint == null)
            return;
        paint.setShader(null);
        if (bitmap == null)
            return;
        if (isCatchBitmapOnly) {
            configMatrix(matrix, width, height, bitmap.getWidth(), bitmap.getHeight());
            BitmapShader mBitmapShader = new BitmapShader(bitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapShader.setLocalMatrix(matrix);
            paint.setShader(mBitmapShader);
        } else {
            bitmap.eraseColor(Color.TRANSPARENT);
            canvas.setBitmap(bitmap);
            editBitmap(canvas, drawable, imageMatrix, width, height, scaleType,
                    paddingStart, paddingEnd, paddingTop, paddingBottom);
            BitmapShader mBitmapShader = new BitmapShader(bitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(mBitmapShader);
        }
    }

    private static void configMatrix(Matrix matrix, int width, int height,
                                     int bitmapWidth, int bitmapHeight) {
        if (width <= 0 || height <= 0 || bitmapWidth <= 0 || bitmapHeight <= 0) {
            return;
        }
        final float scaleX = width / (float) bitmapWidth;
        final float scaleY = height / (float) bitmapHeight;
        final float scale = scaleX > scaleY ? scaleX : scaleY;
        final float translateX = Math.round((width / scale - bitmapWidth) / 2f);
        final float translateY = Math.round((height / scale - bitmapHeight) / 2f);
        matrix.setScale(scale, scale);
        matrix.preTranslate(translateX, translateY);
    }

    private static void editBitmap(Canvas canvas, Drawable drawable, Matrix matrix, int width,
                                   int height, ImageView.ScaleType scaleType, int paddingStart,
                                   int paddingEnd, int paddingTop, int paddingBottom) {
        if (canvas == null || drawable == null || width == 0 || height == 0) {
            return;
        }
        switch (scaleType) {
            default:
            case MATRIX:
                canvas.setMatrix(matrix);
                drawable.setBounds(paddingStart, paddingTop, paddingStart
                                + drawable.getIntrinsicWidth(),
                        paddingTop + drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                canvas.setMatrix(null);
                break;
            case FIT_XY:
                drawable.setBounds(paddingStart, paddingTop,
                        width - paddingEnd, height - paddingBottom);
                drawable.draw(canvas);
                break;
            case FIT_START: {
                final double scaleX = (width - paddingStart - paddingEnd)
                        / (double) drawable.getIntrinsicWidth();
                final double scaleY = (height - paddingTop - paddingBottom)
                        / (double) drawable.getIntrinsicHeight();
                final double scale = scaleX < scaleY ? scaleX : scaleY;
                final int drawableWidth = (int) (scale * drawable
                        .getIntrinsicWidth());
                final int drawableHeight = (int) (scale * drawable
                        .getIntrinsicHeight());
                int left;
                int top;
                int right;
                int bottom;
                left = paddingStart;
                top = paddingTop;
                right = paddingStart + drawableWidth;
                bottom = paddingTop + drawableHeight;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
            case FIT_END: {
                final double scaleX = (width - paddingStart - paddingEnd)
                        / (double) drawable.getIntrinsicWidth();
                final double scaleY = (height - paddingTop - paddingBottom)
                        / (double) drawable.getIntrinsicHeight();
                final double scale = scaleX < scaleY ? scaleX : scaleY;
                final int drawableWidth = (int) (scale * drawable
                        .getIntrinsicWidth());
                final int drawableHeight = (int) (scale * drawable
                        .getIntrinsicHeight());
                int left;
                int top;
                int right;
                int bottom;
                left = width - paddingEnd - drawableWidth;
                top = height - paddingBottom - drawableHeight;
                right = width - paddingStart;
                bottom = height - paddingTop;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
            case FIT_CENTER: {
                final double scaleX = (width - paddingStart - paddingEnd)
                        / (double) drawable.getIntrinsicWidth();
                final double scaleY = (height - paddingTop - paddingBottom)
                        / (double) drawable.getIntrinsicHeight();
                final double scale = scaleX < scaleY ? scaleX : scaleY;
                final int drawableWidth = (int) (scale * drawable
                        .getIntrinsicWidth());
                final int drawableHeight = (int) (scale * drawable
                        .getIntrinsicHeight());
                int left = (width - drawableWidth) / 2;
                int top = (height - drawableHeight) / 2;
                int right = left + drawableWidth;
                int bottom = top + drawableHeight;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
            case CENTER: {
                int left = (width - drawable.getIntrinsicWidth()) / 2;
                int top = (height - drawable.getIntrinsicHeight()) / 2;
                int right = left + drawable.getIntrinsicWidth();
                int bottom = top + drawable.getIntrinsicHeight();
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
            case CENTER_CROP: {
                final double scaleX = (width - paddingStart - paddingEnd)
                        / (double) drawable.getIntrinsicWidth();
                final double scaleY = (height - paddingTop - paddingBottom)
                        / (double) drawable.getIntrinsicHeight();
                final double scale = scaleX > scaleY ? scaleX : scaleY;
                final int drawableWidth = (int) (scale * drawable
                        .getIntrinsicWidth());
                final int drawableHeight = (int) (scale * drawable
                        .getIntrinsicHeight());
                int left = (width - drawableWidth) / 2;
                int top = (height - drawableHeight) / 2;
                int right = left + drawableWidth;
                int bottom = top + drawableHeight;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
            case CENTER_INSIDE: {
                final double scaleX = (width - paddingStart - paddingEnd)
                        / (double) drawable.getIntrinsicWidth();
                final double scaleY = (height - paddingTop - paddingBottom)
                        / (double) drawable.getIntrinsicHeight();
                final double scale = Math.min(scaleX, scaleY) < 1 ? Math.min(
                        scaleX, scaleY) : 1;
                final int drawableWidth = (int) (scale * drawable
                        .getIntrinsicWidth());
                final int drawableHeight = (int) (scale * drawable
                        .getIntrinsicHeight());
                int left = (width - drawableWidth) / 2;
                int top = (height - drawableHeight) / 2;
                int right = left + drawableWidth;
                int bottom = top + drawableHeight;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
            break;
        }
    }
}
