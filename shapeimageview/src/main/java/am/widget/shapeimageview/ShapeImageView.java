package am.widget.shapeimageview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 图形裁剪ImageView
 * API 21 及以上 使用 setOutlineProvider 方式实现
 * 以下使用 BitmapShader 方式实现
 * API 21 及以上 支持动态图
 */
public class ShapeImageView extends ImageView {

    public static final int SCALE_TARGET_AUTO = -1;// 自适应缩放
    public static final int SCALE_TARGET_HEIGHT = 0;// 对高进行缩放
    public static final int SCALE_TARGET_WIDTH = 1;// 对宽进行缩放
    private static final int SHAPE_CIRCLE = 1;// 圆形裁剪
    private static final int SHAPE_ROUND_RECT = 2;// 圆角矩形裁剪
    private ShapeHelper mHelper;// 辅助器
    private ImageShape mShape;// 形状
    private boolean mCatchBitmapOnly;// Bitmap释放，一般在复用的过程中最好为true
    private int mBorderColor;// 边线颜色
    private float mBorderWidth;// 边线宽度
    private Drawable mForeground;// 前景
    private int mForegroundId;// 前景ID
    private int mWidthScale = 0;// 宽度缩放比
    private int mHeightScale = 0;// 高度缩放比
    private int mScaleTarget = SCALE_TARGET_AUTO;// 缩放目标

    public ShapeImageView(Context context) {
        this(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
        int shapeType = custom.getInt(R.styleable.ShapeImageView_sivShape, 0);
        int roundRectRadius = custom.getDimensionPixelSize(
                R.styleable.ShapeImageView_sivRoundRectRadius, 0);
        boolean catchBitmapOnly = custom.getBoolean(R.styleable.ShapeImageView_sivCatchBitmapOnly,
                false);
        int borderWidth = custom.getDimensionPixelSize(
                R.styleable.ShapeImageView_sivBorderWidth, 0);
        int borderColor = custom.getColor(R.styleable.ShapeImageView_sivBorderColor, 0x00000000);
        Drawable foreground = null;
        if (custom.hasValue(R.styleable.ShapeImageView_sivForeground))
            foreground = custom.getDrawable(R.styleable.ShapeImageView_sivForeground);
        int widthScale = custom.getInteger(R.styleable.ShapeImageView_sivWidthScale, 0);
        int heightScale = custom.getInteger(R.styleable.ShapeImageView_sivHeightScale, 0);
        int scaleTarget = custom.getInt(
                R.styleable.ShapeImageView_sivScaleTarget, SCALE_TARGET_AUTO);
        custom.recycle();
        ImageShape shape;
        switch (shapeType) {
            case SHAPE_CIRCLE:
                shape = new CircleImageShape();
                break;
            case SHAPE_ROUND_RECT:
                shape = new RoundRectImageShape(roundRectRadius);
                break;
            default:
                shape = null;
                break;
        }
        setImageShape(shape);
        setCatchBitmapOnly(catchBitmapOnly);
        setBorderColor(borderColor);
        setBorderWidth(borderWidth);
        setForeground(foreground);
        setFixedSize(widthScale, heightScale);
        setScaleTarget(scaleTarget);
    }

    private void checkHelper() {
        if (mHelper == null)
            mHelper = new ShapeHelper();
    }

    private boolean hasForeground() {
        return mForeground != null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthScale <= 0 || mHeightScale <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measureWidth = getMeasuredWidth();
        final int measureHeight = getMeasuredHeight();
        switch (mScaleTarget) {
            default:
            case SCALE_TARGET_AUTO:
                if (measureWidth < measureWidth * mHeightScale / mWidthScale) {
                    setMeasuredDimension(measureWidth, measureWidth * mHeightScale / mWidthScale);
                } else {
                    setMeasuredDimension(measureHeight * mWidthScale / mHeightScale, measureHeight);
                }
                break;
            case SCALE_TARGET_HEIGHT:
                setMeasuredDimension(measureWidth, measureWidth * mHeightScale / mWidthScale);
                break;
            case SCALE_TARGET_WIDTH:
                setMeasuredDimension(measureHeight * mWidthScale / mHeightScale, measureHeight);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        checkHelper();
        mHelper.sizeChanged(getWidth(), getHeight(), mShape, this);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        checkHelper();
        if (mShape != null) {
            if (mHelper.needOnDraw(this, canvas, mShape)) {
                super.onDraw(canvas);
            }
            mHelper.drawBorder(this, canvas, mShape);
        } else {
            super.onDraw(canvas);
        }
        if (hasForeground()) {
            mForeground.setBounds(ShapeCompat.getPaddingStart(this), getPaddingTop(),
                    getWidth() - ShapeCompat.getPaddingEnd(this), getHeight() - getPaddingBottom());
            mForeground.draw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasForeground() && event.getAction() == MotionEvent.ACTION_DOWN) {
            ShapeCompat.setHotspot(mForeground, event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void drawableStateChanged() {
        if (hasForeground() && mForeground.isStateful()) {
            mForeground.setState(getDrawableState());
        }
        super.drawableStateChanged();

    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean isPress = false;
        if (hasForeground() && who == mForeground) {
            isPress = true;
        }
        return isPress || super.verifyDrawable(who);
    }

    @Override
    protected void onAttachedToWindow() {
        if (mShape != null) {
            mShape.onAttached(this);
        }
        super.onAttachedToWindow();
        if (hasForeground()) {
            mForeground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHelper != null)
            mHelper.detachedFromWindow(mShape, this);
        if (mShape != null) {
            mShape.onDetached(this);
        }
        if (hasForeground()) {
            mForeground.setCallback(null);
        }
        super.onDetachedFromWindow();

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        checkHelper();
        mHelper.setColorFilter(cf);
        super.setColorFilter(cf);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        checkHelper();
        mHelper.updateBitmap(mShape, this);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        checkHelper();
        mHelper.updateBitmap(mShape, this);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        checkHelper();
        mHelper.updateBitmap(mShape, this);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        checkHelper();
        mHelper.updateBitmap(mShape, this);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        checkHelper();
        mHelper.setScaleType(scaleType, mShape, this);
        super.setScaleType(scaleType);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        checkHelper();
        mHelper.setPadding(left, top, right, bottom, mShape, this);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        checkHelper();
        mHelper.setPadding(start, top, end, bottom, mShape, this);
        super.setPaddingRelative(start, top, end, bottom);
    }

    /**
     * 获取图像形状
     *
     * @return 图像形状
     */
    @SuppressWarnings("unused")
    public ImageShape getImageShape() {
        return mShape;
    }

    /**
     * 设置图像形状
     *
     * @param shape 图像形状
     */
    public void setImageShape(ImageShape shape) {
        if (mShape != shape) {
            if (mShape != null) {
                mShape.onDetached(this);
            }
            mShape = shape;
            checkHelper();
            mHelper.initShape(this, mShape);
            mHelper.updateBitmap(mShape, this);
            mHelper.setScaleType(getScaleType(), mShape, this);
            mHelper.setPadding(ShapeCompat.getPaddingStart(this), getPaddingTop(),
                    ShapeCompat.getPaddingEnd(this), getPaddingBottom(), mShape, this);
            if (mShape != null) {
                mShape.onAttached(this);
            }
            invalidate();
        }
    }

    /**
     * 是否需要进行Bitmap释放，一般在复用的过程中最好返回true
     *
     * @return 是否需要进行Bitmap释放
     */
    public boolean isCatchBitmapOnly() {
        return mCatchBitmapOnly;
    }

    /**
     * 设置是否需要进行Bitmap释放，一般在复用的过程中最好为true
     *
     * @param catchOnly 是否需要进行Bitmap释放
     */
    public void setCatchBitmapOnly(boolean catchOnly) {
        if (mCatchBitmapOnly != catchOnly) {
            mCatchBitmapOnly = catchOnly;
            checkHelper();
            mHelper.forceUpdateBitmap(mShape, this);
        }
    }

    /**
     * 获取边框颜色
     *
     * @return 边框颜色
     */
    @SuppressWarnings("unused")
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框颜色
     */
    public void setBorderColor(int color) {
        if (mBorderColor != color) {
            mBorderColor = color;
            invalidate();
        }
    }

    /**
     * 获取边框宽度
     *
     * @return 边框宽度
     */
    @SuppressWarnings("unused")
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度
     */
    public void setBorderWidth(int width) {
        if (mBorderWidth != width) {
            mBorderWidth = width;
            invalidate();
        }
    }

    /**
     * 设置前景
     *
     * @param id 资源ID
     */
    @SuppressWarnings("unused")
    public void setForeground(int id) {
        if (mForegroundId != id) {
            mForegroundId = id;
            setForeground(ShapeCompat.getDrawable(getContext(), mForegroundId));
        }
    }

    /**
     * 设置前景
     *
     * @param foreground 图片
     */
    public void setForeground(Drawable foreground) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            setForegroundAPI23(foreground);
            return;
        }
        if (mForeground != foreground) {
            if (mForeground != null) {
                mForeground.setCallback(null);
            }
            mForeground = foreground;
            mForeground.setCallback(this);
            invalidate();
        }
    }

    /**
     * 获取缩放目标
     *
     * @return 缩放目标
     */
    @SuppressWarnings("unused")
    public int getScaleTarget() {
        return mScaleTarget;
    }

    /**
     * 设置缩放目标
     *
     * @param target 缩放目标
     */
    public void setScaleTarget(int target) {
        if (target != SCALE_TARGET_AUTO && target != SCALE_TARGET_HEIGHT
                && target != SCALE_TARGET_WIDTH)
            return;
        if (mScaleTarget != target) {
            mScaleTarget = target;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置缩放比（任意值小于等于0则关闭该功能）
     *
     * @param widthScale  宽度缩放比
     * @param heightScale 高度缩放比
     */
    public void setFixedSize(int widthScale, int heightScale) {
        if (mWidthScale != widthScale || mHeightScale != heightScale) {
            mWidthScale = widthScale;
            mHeightScale = heightScale;
            requestLayout();
            invalidate();
        }
    }

    @TargetApi(23)
    private void setForegroundAPI23(Drawable foreground) {
        super.setForeground(foreground);
    }

}