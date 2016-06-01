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
import android.view.View;
import android.widget.ImageView;

/**
 * 图形裁剪ImageView
 * <p/>
 * API 21 及以上 使用 setOutlineProvider 方式实现<br>
 * 以下使用 BitmapShader 方式实现<br>
 * API 21 及以上 支持动态图
 *
 * @author Alex
 */
public class ShapeImageView extends ImageView {

    public static final int SCALE_TARGET_AUTO = -1;// 自适应缩放
    public static final int SCALE_TARGET_HEIGHT = 0;// 对高进行缩放
    public static final int SCALE_TARGET_WIDTH = 1;// 对宽进行缩放
    private static final int SHAPE_CIRCLE = 1;// 圆形裁剪
    private static final int SHAPE_ROUND_RECT = 2;// 圆角矩形裁剪
    private ImageShape mShape;// 形状
    private Drawable mForeground;// 前景
    private int mForegroundId;// 前景ID
    private int widthScale = 0;// 宽度缩放比
    private int heightScale = 0;// 高度缩放比
    private int scaleTarget = SCALE_TARGET_AUTO;// 缩放目标

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
        int borderWidth = custom.getDimensionPixelSize(
                R.styleable.ShapeImageView_sivBorderWidth, 0);
        int borderColor = custom.getColor(R.styleable.ShapeImageView_sivBorderColor, 0x00000000);
        boolean catchOnly = custom.getBoolean(R.styleable.ShapeImageView_sivCatchOnly, false);
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
                shape = new CircleImageShape(borderColor, borderWidth, catchOnly);
                break;
            case SHAPE_ROUND_RECT:
                shape = new RoundRectImageShape(borderColor, borderWidth, catchOnly,
                        roundRectRadius);
                break;
            default:
                shape = null;
                break;
        }
        setImageShape(shape);
        setForeground(foreground);
        setFixedSize(widthScale, heightScale);
        setScaleTarget(scaleTarget);
    }

    private boolean hasForeground() {
        return mForeground != null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthScale <= 0 || heightScale <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measureWidth = getMeasuredWidth();
        final int measureHeight = getMeasuredHeight();
        switch (scaleTarget) {
            default:
            case SCALE_TARGET_AUTO:
                if (measureWidth < measureWidth * heightScale / widthScale) {
                    setMeasuredDimension(measureWidth, measureWidth * heightScale / widthScale);
                } else {
                    setMeasuredDimension(measureHeight * widthScale / heightScale, measureHeight);
                }
                break;
            case SCALE_TARGET_HEIGHT:
                setMeasuredDimension(measureWidth, measureWidth * heightScale / widthScale);
                break;
            case SCALE_TARGET_WIDTH:
                setMeasuredDimension(measureHeight * widthScale / heightScale, measureHeight);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mShape != null)
            mShape.sizeChanged(w, h, mShape);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShape != null) {
            if (mShape.needOnDraw(this, canvas, mShape)) {
                super.onDraw(canvas);
            }
            mShape.drawBorder(this, canvas, mShape);
        } else {
            super.onDraw(canvas);
        }
        if (hasForeground()) {
            mForeground.setBounds(getPaddingStart(this), getPaddingTop(),
                    getWidth() - getPaddingEnd(this), getHeight() - getPaddingBottom());
            mForeground.draw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasForeground() && event.getAction() == MotionEvent.ACTION_DOWN) {
            setHotspot(mForeground, event.getX(), event.getY());
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
        if (mShape != null)
            mShape.detachedFromWindow(mShape);
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
        if (mShape != null)
            mShape.setColorFilter(cf);
        super.setColorFilter(cf);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (mShape != null)
            mShape.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (mShape != null)
            mShape.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mShape != null)
            mShape.changeBitmap(drawable, mShape);
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (mShape != null)
            mShape.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (mShape != null)
            mShape.setScaleType(scaleType, mShape);
        super.setScaleType(scaleType);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (mShape != null)
            mShape.setPadding(left, top, right, bottom, mShape);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        if (mShape != null)
            mShape.setPadding(start, top, end, bottom, mShape);
        super.setPaddingRelative(start, top, end, bottom);
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
            if (mShape != null) {
                mShape.initShape(this);
                mShape.changeBitmap(getDrawable(), mShape);
                mShape.setScaleType(getScaleType(), mShape);
                mShape.setPadding(getPaddingStart(this), getPaddingTop(), getPaddingEnd(this),
                        getPaddingBottom(), mShape);
                mShape.onAttached(this);
            }
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
            setForeground(getDrawable(getContext(), mForegroundId));
        }
    }

    /**
     * 设置前景
     *
     * @param foreground 图片
     */
    public void setForeground(Drawable foreground) {
//        final int version = android.os.Build.VERSION.SDK_INT;
//        if (version >= 23) {
//            setForegroundAPI23(foreground);
//            return;
//        }
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
        return scaleTarget;
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
        if (scaleTarget != target) {
            scaleTarget = target;
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
        if (this.widthScale != widthScale || this.heightScale != heightScale) {
            this.widthScale = widthScale;
            this.heightScale = heightScale;
            requestLayout();
            invalidate();
        }
    }

    @TargetApi(23)
    private void setForegroundAPI23(Drawable foreground) {
        super.setForeground(foreground);
    }

    private static int getPaddingStart(View view) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            return getPaddingStartAPI17(view);
        }
        return view.getPaddingLeft();
    }

    @TargetApi(17)
    private static int getPaddingStartAPI17(View view) {
        return view.getPaddingStart();
    }

    private static int getPaddingEnd(View view) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            return getPaddingEndAPI17(view);
        }
        return view.getPaddingRight();
    }

    @TargetApi(17)
    private static int getPaddingEndAPI17(View view) {
        return view.getPaddingStart();
    }

    @SuppressWarnings("all")
    private static Drawable getDrawable(Context context, int id) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            return getDrawableAPI21(context, id);
        }
        return context.getResources().getDrawable(id);
    }

    @TargetApi(21)
    private static Drawable getDrawableAPI21(Context context, int id) {
        return context.getDrawable(id);
    }

    private static void setHotspot(Drawable drawable, float x, float y) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            setHotspotAPI21(drawable, x, y);
        }
    }

    @TargetApi(21)
    private static void setHotspotAPI21(Drawable drawable, float x, float y) {
        drawable.setHotspot(x, y);
    }
}