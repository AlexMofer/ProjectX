package am.project.x.widgets.shapeimageview;

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
 * ShapeCompat
 * Created by Alex on 2015/12/9.
 */
class ShapeCompat {

    interface ShapeCompatImpl {

        void init(View view, ImageShape shape);

        void sizeChanged(int width, int height, ImageShape shape);

        void setScaleType(ImageView.ScaleType scaleType, ImageShape shape);

        void setPadding(int left, int top, int right, int bottom, ImageShape shape);

        void detachedFromWindow(ImageShape shape);

        void changeBitmap(Drawable drawable, ImageShape shape);

        boolean needOnDraw(ImageView view, Canvas canvas, ImageShape shape);

        void drawBorder(View view, Canvas canvas, ImageShape shape);

        void setColorFilter(ColorFilter cf);

    }

    static class BaseShapeCompatImpl implements
            ShapeCompatImpl {

        private Drawable mDrawable;
        private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
        private Bitmap mBitmap;
        private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Canvas mBitmapCanvas = new Canvas();
        private BitmapShader mBitmapShader;
        private final Matrix mMatrix = new Matrix();
        private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private int mViewWidth;
        private int mViewHeight;
        private int paddingStart = 0;
        private int paddingEnd = 0;
        private int paddingTop = 0;
        private int paddingBottom = 0;

        @Override
        public void init(View view, ImageShape shape) {
            if (shape == null)
                return;
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            mBorderPaint.setColor(shape.getBorderColor());
        }

        @Override
        public void sizeChanged(int width, int height, ImageShape shape) {
            if (shape == null)
                return;
            if (mViewWidth != width || mViewHeight != height) {
                mViewWidth = width;
                mViewHeight = height;
                if (!shape.isCatchBitmapOnly()) {
                    mBitmapPaint.setShader(null);
                    mBitmap = mDrawable == null ? null : configBitmap(
                            mBitmap, mViewWidth, mViewHeight);
                }
                invalidateBitmap(shape);
            }
        }

        protected Bitmap configBitmap(Bitmap bitmap, int width, int height) {
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

        @Override
        public void setScaleType(ImageView.ScaleType scaleType, ImageShape shape) {
            if (shape == null)
                return;
            if (mScaleType != scaleType) {
                mScaleType = scaleType;
                invalidateBitmap(shape);
            }
        }

        @Override
        public void setPadding(int left, int top, int right, int bottom, ImageShape shape) {
            if (shape == null)
                return;
            if (paddingStart != 0 || paddingTop != top || paddingEnd != right
                    || paddingBottom != right) {
                paddingStart = left;
                paddingTop = top;
                paddingEnd = right;
                paddingBottom = bottom;
                invalidateBitmap(shape);
            }
        }

        @Override
        public void detachedFromWindow(ImageShape shape) {
            if (shape == null)
                return;
            if (!shape.isCatchBitmapOnly()) {
                if (mBitmap != null && !mBitmap.isRecycled()
                        && mBitmap.isMutable()) {
                    mBitmap.eraseColor(Color.TRANSPARENT);
                    mBitmap.recycle();
                    mBitmap = null;
                    System.gc();
                }
            }
        }

        @Override
        public void changeBitmap(Drawable drawable, ImageShape shape) {
            if (shape == null)
                return;
            if (mDrawable != drawable) {
                mDrawable = drawable;
                if (shape.isCatchBitmapOnly()) {
                    mBitmapPaint.setShader(null);
                    mBitmap = getBitmap(mDrawable);
                }
                invalidateBitmap(shape);
            }
        }

        private Bitmap getBitmap(Drawable drawable) {
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                return bitmapDrawable.getBitmap();
            }
            return null;
        }

        private void invalidateBitmap(ImageShape shape) {
            mBitmapPaint.setShader(null);
            if (shape.isCatchBitmapOnly()) {
                if (mBitmap != null) {
                    configMatrix(mMatrix, mViewWidth, mViewHeight, mBitmap.getWidth(), mBitmap.getHeight());
                    mBitmapShader = new BitmapShader(mBitmap,
                            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    mBitmapShader.setLocalMatrix(mMatrix);
                    mBitmapPaint.setShader(mBitmapShader);
                }
            } else {
                if (mDrawable != null) {
                    if (mBitmap == null) {
                        mBitmap = configBitmap(null, mViewWidth,
                                mViewHeight);
                    }
                    if (mBitmap != null) {
                        mBitmap.eraseColor(Color.TRANSPARENT);
                        mBitmapCanvas.setBitmap(mBitmap);
                        editBitmap(mBitmapCanvas, mDrawable, mViewWidth,
                                mViewHeight, mScaleType, paddingStart,
                                paddingEnd, paddingTop, paddingBottom);
                        mBitmapShader = new BitmapShader(mBitmap,
                                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        mBitmapPaint.setShader(mBitmapShader);
                    }
                }
            }
        }

        private void configMatrix(Matrix matrix, int width, int height,
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

        private void editBitmap(Canvas canvas, Drawable drawable, int width,
                                int height, ImageView.ScaleType scaleType, int paddingStart,
                                int paddingEnd, int paddingTop, int paddingBottom) {
            if (canvas == null || drawable == null || width == 0 || height == 0) {
                return;
            }
            switch (scaleType) {
                default:
                case MATRIX:
                    drawable.setBounds(paddingStart, paddingTop, paddingStart
                                    + drawable.getIntrinsicWidth(),
                            paddingTop + drawable.getIntrinsicHeight());
                    drawable.draw(canvas);
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

        @Override
        public boolean needOnDraw(ImageView view, Canvas canvas, ImageShape shape) {
            if (shape == null)
                return true;
            if (mBitmapPaint.getShader() == null) {
                return true;
            }
            shape.makeShapeBase(view, canvas, mBitmapPaint);
            return false;
        }

        @Override
        public void drawBorder(View view, Canvas canvas, ImageShape shape) {
            if (shape == null)
                return;
            shape.drawBorderBase(view, canvas, shape.getBorderWidth(), mBorderPaint);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mBitmapPaint.setColorFilter(cf);
            mBorderPaint.setColorFilter(cf);
        }
    }

    @TargetApi(19)
    static class KitkatShapeCompatImpl extends
            BaseShapeCompatImpl {

        @Override
        protected Bitmap configBitmap(Bitmap bitmap, int width, int height) {
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

    }

    @TargetApi(21)
    static class LollipopShapeCompatImpl implements ShapeCompatImpl {
        private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void init(View view, final ImageShape shape) {
            if (shape == null)
                return;
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
            mBorderPaint.setColor(shape.getBorderColor());
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    shape.makeShapeLollipop(view, outline);
                }
            });
            view.setClipToOutline(true);
        }

        @Override
        public void sizeChanged(int width, int height, ImageShape shape) {

        }

        @Override
        public void setScaleType(ImageView.ScaleType scaleType, ImageShape shape) {

        }

        @Override
        public void setPadding(int left, int top, int right, int bottom, ImageShape shape) {

        }

        @Override
        public void detachedFromWindow(ImageShape shape) {

        }

        @Override
        public void changeBitmap(Drawable drawable, ImageShape shape) {

        }

        @Override
        public boolean needOnDraw(ImageView view, Canvas canvas, ImageShape shape) {
            return true;
        }

        @Override
        public void drawBorder(View view, Canvas canvas, ImageShape shape) {
            if (shape == null)
                return;
            shape.drawBorderLollipop(view, canvas, shape.getBorderWidth(), mBorderPaint);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mBorderPaint.setColorFilter(cf);
        }
    }

    private ShapeCompatImpl IMPL;

    ShapeCompat() {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopShapeCompatImpl();
        } else if (version >= 19) {
            IMPL = new KitkatShapeCompatImpl();
        } else {
            IMPL = new BaseShapeCompatImpl();
        }
    }

    void init(View view, ImageShape shape) {
        IMPL.init(view, shape);
    }

    void sizeChanged(int width, int height, ImageShape shape) {
        IMPL.sizeChanged(width, height, shape);
    }

    void setScaleType(ImageView.ScaleType scaleType, ImageShape shape) {
        IMPL.setScaleType(scaleType, shape);
    }

    void setPadding(int left, int top, int right, int bottom, ImageShape shape) {
        IMPL.setPadding(left, top, right, bottom, shape);
    }

    void detachedFromWindow(ImageShape shape) {
        IMPL.detachedFromWindow(shape);
    }

    void changeBitmap(Drawable drawable, ImageShape shape) {
        IMPL.changeBitmap(drawable, shape);
    }

    boolean needOnDraw(ImageView view, Canvas canvas, ImageShape shape) {
        return IMPL.needOnDraw(view, canvas, shape);
    }

    void drawBorder(View view, Canvas canvas, ImageShape shape) {
        IMPL.drawBorder(view, canvas, shape);
    }

    void setColorFilter(ColorFilter cf) {
        IMPL.setColorFilter(cf);
    }


}
