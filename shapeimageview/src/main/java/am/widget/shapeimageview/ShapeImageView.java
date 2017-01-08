package am.widget.shapeimageview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 图形裁剪ImageView
 */
public class ShapeImageView extends ImageView {

    public static final int SCALE_TARGET_HEIGHT = 0;// 对高进行缩放
    public static final int SCALE_TARGET_WIDTH = 1;// 对宽进行缩放
    public static final int SCALE_TARGET_EXPAND = 2;// 扩大方式（宽不足拉伸宽，高不足拉伸高）
    public static final int SCALE_TARGET_INSIDE = 3;// 缩小方式（缩小到一条边刚好与原尺寸一样，另一条小于原尺寸）
    private static final int SHAPE_CIRCLE = 1;// 圆形裁剪
    private static final int SHAPE_ROUND_RECT = 2;// 圆角矩形裁剪
    private static final ImageShape CIRCLE = new CircleImageShape();// 圆形
    private static final ImageShape ROUND_RECT = new RoundRectImageShape();// 圆角矩形
    private static final ShapeHelper HELPER = new ShapeHelper();// 辅助器
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ImageShape mShape;// 形状
    private float mRoundRectRadius;// 圆角矩形半径
    private float mBorderWidth;// 边线宽度
    private Drawable mForeground;// 前景
    private int mForegroundId;// 前景ID
    private int mWidthScale = 0;// 宽度缩放比
    private int mHeightScale = 0;// 高度缩放比
    private int mScaleTarget = SCALE_TARGET_INSIDE;// 缩放目标

    public ShapeImageView(Context context) {
        super(context);
        initView(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @TargetApi(11)
    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
        int shapeType = custom.getInt(R.styleable.ShapeImageView_sivShape, 0);
        int roundRectRadius = custom.getDimensionPixelSize(
                R.styleable.ShapeImageView_sivRoundRectRadius, 0);
        int borderWidth = custom.getDimensionPixelSize(
                R.styleable.ShapeImageView_sivBorderWidth, 0);
        int borderColor = custom.getColor(R.styleable.ShapeImageView_sivBorderColor, 0x00000000);
        Drawable foreground = null;
        if (custom.hasValue(R.styleable.ShapeImageView_sivForeground))
            foreground = custom.getDrawable(R.styleable.ShapeImageView_sivForeground);
        int widthScale = custom.getInteger(R.styleable.ShapeImageView_sivWidthScale, 0);
        int heightScale = custom.getInteger(R.styleable.ShapeImageView_sivHeightScale, 0);
        int scaleTarget = custom.getInt(
                R.styleable.ShapeImageView_sivScaleTarget, SCALE_TARGET_INSIDE);
        custom.recycle();
        ImageShape shape;
        switch (shapeType) {
            case SHAPE_CIRCLE:
                shape = CIRCLE;
                break;
            case SHAPE_ROUND_RECT:
                shape = ROUND_RECT;
                break;
            default:
                shape = null;
                break;
        }
        setImageShape(shape);
        setRoundRectRadius(roundRectRadius);
        setBorderColor(borderColor);
        setBorderWidth(borderWidth);
        setForeground(foreground);
        setFixedSize(widthScale, heightScale);
        setScaleTarget(scaleTarget);
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
            case SCALE_TARGET_HEIGHT:
                setMeasuredDimension(measureWidth, measureWidth * mHeightScale / mWidthScale);
                break;
            case SCALE_TARGET_WIDTH:
                setMeasuredDimension(measureHeight * mWidthScale / mHeightScale, measureHeight);
                break;
            case SCALE_TARGET_EXPAND:
                if (measureWidth < measureHeight * mWidthScale / mHeightScale) {
                    // 宽不足
                    setMeasuredDimension(measureHeight * mWidthScale / mHeightScale, measureHeight);
                } else {
                    // 高不足
                    setMeasuredDimension(measureWidth, measureWidth * mHeightScale / mWidthScale);
                }
                break;
            default:
            case SCALE_TARGET_INSIDE:
                if (measureWidth > measureHeight / mHeightScale * mWidthScale) {
                    setMeasuredDimension(measureHeight * mWidthScale / mHeightScale, measureHeight);
                } else {
                    setMeasuredDimension(measureWidth, measureWidth * mHeightScale / mWidthScale);
                }
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        HELPER.updateSize(this, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        HELPER.draw(this, canvas);
    }

    void doSuperDraw(Canvas canvas) {
        super.draw(canvas);
        if (hasForeground()) {
            mForeground.setBounds(Compat.getPaddingStart(this), getPaddingTop(),
                    getWidth() - Compat.getPaddingEnd(this), getHeight() - getPaddingBottom());
            mForeground.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBorderWidth > 0 && mShape != null) {
            mShape.drawBorder(this, canvas, mBorderPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasForeground() && event.getAction() == MotionEvent.ACTION_DOWN) {
            Compat.setHotspot(mForeground, event.getX(), event.getY());
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
    @SuppressWarnings("all")
    protected boolean verifyDrawable(Drawable who) {
        boolean isPress = false;
        if (hasForeground() && who == mForeground) {
            isPress = true;
        }
        return isPress || super.verifyDrawable(who);
    }

    @Override
    protected void onAttachedToWindow() {
        HELPER.onAttachedToView(this);
        super.onAttachedToWindow();
        if (hasForeground()) {
            mForeground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (hasForeground()) {
            mForeground.setCallback(null);
        }
        super.onDetachedFromWindow();
        HELPER.onDetachedFromView(this);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBorderPaint.setColorFilter(cf);
        super.setColorFilter(cf);
    }

    /**
     * 获取图像形状
     *
     * @return 图像形状
     */
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
            mShape = shape;
            HELPER.updateImageShape(this, shape);
            Compat.invalidateOutline(this);
            invalidate();
        }
    }

    /**
     * 刷新Shape
     */
    @SuppressWarnings("unused")
    public void invalidateImageShape() {
        HELPER.invalidateImageShape(this);

    }

    /**
     * 获取圆角矩形圆角半径
     *
     * @return 圆角矩形圆角半径
     */
    public float getRoundRectRadius() {
        return mRoundRectRadius;
    }

    /**
     * 设置圆角矩形圆角半径
     * 仅圆角矩形Shape下有效
     *
     * @param radius 圆角矩形圆角半径
     */
    public void setRoundRectRadius(float radius) {
        if (mRoundRectRadius != radius) {
            mRoundRectRadius = radius;
            Compat.invalidateOutline(this);
            invalidate();
        }
    }

    /**
     * 获取边框颜色
     *
     * @return 边框颜色
     */
    @SuppressWarnings("unused")
    public int getBorderColor() {
        return mBorderPaint.getColor();
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框颜色
     */
    public void setBorderColor(int color) {
        if (mBorderPaint.getColor() != color) {
            mBorderPaint.setColor(color);
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
            mBorderPaint.setStrokeWidth(mBorderWidth * 2);
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
            setForeground(Compat.getDrawable(getContext(), mForegroundId));
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
     * 绘制前景图
     *
     * @return 前景图
     */
    public Drawable getForeground() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            return getForegroundAPI23();
        }
        return mForeground;
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
        if (target != SCALE_TARGET_INSIDE && target != SCALE_TARGET_HEIGHT
                && target != SCALE_TARGET_WIDTH && target != SCALE_TARGET_EXPAND)
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

    @TargetApi(23)
    private Drawable getForegroundAPI23() {
        return super.getForeground();
    }


}