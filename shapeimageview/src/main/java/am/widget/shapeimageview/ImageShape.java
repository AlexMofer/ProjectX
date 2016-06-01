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

import java.util.ArrayList;

/**
 * 形状
 * Created by Alex on 2015/12/9.
 */
public abstract class ImageShape {

    private Drawable mDrawable;
    private ImageView.ScaleType mScaleType;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Canvas mBitmapCanvas;
    private Matrix mMatrix;
    private Paint mBorderPaint;

    private int mViewWidth;
    private int mViewHeight;
    private int paddingStart = 0;
    private int paddingEnd = 0;
    private int paddingTop = 0;
    private int paddingBottom = 0;

    private ArrayList<ShapeImageView> views = new ArrayList<>();
    private boolean catchOnly = false;
    private int color = 0;
    private int width = 0;

    public ImageShape(int color, int width, boolean catchOnly) {
        this.color = color;
        this.width = width;
        this.catchOnly = catchOnly;
    }

    void initShape(View view) {
        if (isLollipop()) {
            initShapeLollipop(view);
        } else {
            initShapeBase();
        }
    }

    private void initShapeBase() {
        mScaleType = ImageView.ScaleType.FIT_CENTER;
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapCanvas = new Canvas();
        mMatrix = new Matrix();
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @TargetApi(21)
    private void initShapeLollipop(View view) {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                makeShapeLollipop(view, outline);
            }
        });
        view.setClipToOutline(true);
    }

    void sizeChanged(int width, int height, ImageShape shape) {
        if (isLollipop())
            return;
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
        if (isKitkat()) {
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
    private Bitmap configBitmapKitkat(Bitmap bitmap, int width, int height) {
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

    private void invalidateBitmap(ImageShape shape) {
        mBitmapPaint.setShader(null);
        if (shape.isCatchBitmapOnly()) {
            if (mBitmap != null) {
                configMatrix(mMatrix, mViewWidth, mViewHeight, mBitmap.getWidth(), mBitmap.getHeight());
                BitmapShader mBitmapShader = new BitmapShader(mBitmap,
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
                    BitmapShader mBitmapShader = new BitmapShader(mBitmap,
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

    void setScaleType(ImageView.ScaleType scaleType, ImageShape shape) {
        if (isLollipop())
            return;
        if (shape == null)
            return;
        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            invalidateBitmap(shape);
        }
    }

    void setPadding(int left, int top, int right, int bottom, ImageShape shape) {
        if (isLollipop())
            return;
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

    void detachedFromWindow(ImageShape shape) {
        if (isLollipop())
            return;
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

    void changeBitmap(Drawable drawable, ImageShape shape) {
        if (isLollipop())
            return;
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

    boolean needOnDraw(ImageView view, Canvas canvas, ImageShape shape) {
        if (isLollipop())
            return true;
        if (shape == null)
            return true;
        if (mBitmapPaint.getShader() == null) {
            return true;
        }
        shape.makeShapeBase(view, canvas, mBitmapPaint);
        return false;
    }

    void drawBorder(View view, Canvas canvas, ImageShape shape) {
        if (shape == null)
            return;
        if (isLollipop()) {
            mBorderPaint.setColor(shape.getBorderColor());
            shape.drawBorderLollipop(view, canvas, shape.getBorderWidth(), mBorderPaint);
        } else {
            mBorderPaint.setColor(shape.getBorderColor());
            shape.drawBorderBase(view, canvas, shape.getBorderWidth(), mBorderPaint);
        }
    }

    void setColorFilter(ColorFilter cf) {
        if (isLollipop()) {
            mBorderPaint.setColorFilter(cf);
        } else {
            mBitmapPaint.setColorFilter(cf);
            mBorderPaint.setColorFilter(cf);
        }

    }

    /**
     * 低版本下的形状处理
     *
     * @param view   ImageView
     * @param canvas 画布
     * @param paint  画笔
     */
    public abstract void makeShapeBase(ImageView view, Canvas canvas, Paint paint);

    /**
     * 低版本下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    public abstract void drawBorderBase(View view, Canvas canvas, float width, Paint paint);

    /**
     * Lollipop 下的形状处理
     *
     * @param view    View
     * @param outline Outline
     */
    @TargetApi(21)
    public abstract void makeShapeLollipop(View view, Outline outline);

    /**
     * Lollipop 下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    public abstract void drawBorderLollipop(View view, Canvas canvas, float width, Paint paint);

    /**
     * 是否需要进行Bitmap释放，一般在复用的过程中最好返回true
     *
     * @return 是否需要进行Bitmap释放
     */
    public boolean isCatchBitmapOnly() {
        return catchOnly;
    }

    /**
     * 设置是否需要进行Bitmap释放，一般在复用的过程中最好为true
     *
     * @param catchOnly 是否需要进行Bitmap释放
     */
    @SuppressWarnings("unused")
    public void setCatchBitmapOnly(boolean catchOnly) {
        if (this.catchOnly != catchOnly) {
            this.catchOnly = catchOnly;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取边框颜色
     *
     * @return 边框颜色
     */
    public int getBorderColor() {
        return color;
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框颜色
     */
    @SuppressWarnings("unused")
    public void setBorderColor(int color) {
        if (this.color != color) {
            this.color = color;
            notifyDataSetChanged();
        }
    }

    /**
     * 获取边框宽度
     *
     * @return 边框宽度
     */
    public float getBorderWidth() {
        return width;
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度
     */
    @SuppressWarnings("unused")
    public void setBorderWidth(int width) {
        if (this.width != width) {
            this.width = width;
            notifyDataSetChanged();
        }
    }

    /**
     * 捆绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    public void onAttached(ShapeImageView view) {
        views.add(view);
    }

    /**
     * 解绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    public void onDetached(ShapeImageView view) {
        views.remove(view);
    }

    /**
     * 数据变化，刷新界面
     */
    public void notifyDataSetChanged() {
        for (View view : views) {
            view.requestLayout();
            view.invalidate();
        }
    }

    private static boolean isLollipop() {
//        return android.os.Build.VERSION.SDK_INT >= 21;
        return false;
    }

    private static boolean isKitkat() {
//        return android.os.Build.VERSION.SDK_INT >= 19;
        return false;
    }
}
