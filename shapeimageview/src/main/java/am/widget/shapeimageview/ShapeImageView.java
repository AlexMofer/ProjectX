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
 * @author Mofer
 */
public class ShapeImageView extends ImageView {

    public static final int SCALE_TARGET_AUTO = -1;// 自适应缩放
    public static final int SCALE_TARGET_HEIGHT = 0;// 对高进行缩放
    public static final int SCALE_TARGET_WIDTH = 1;// 对宽进行缩放
    private static final int SHAPE_CIRCLE = 1;// 圆形裁剪
    private static final int SHAPE_ROUND_RECT = 2;// 圆角矩形裁剪
    private ShapeCompat mShapeCompat = new ShapeCompat();
    private ImageShape mShape;

    private Drawable mPressDrawable;
    private int mId;
    private boolean duplicateViewState = true;


    private int widthScale = 0;
    private int heightScale = 0;
    private int scaleTarget = SCALE_TARGET_AUTO;

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
        boolean duplicateViewState = custom.getBoolean(
                R.styleable.ShapeImageView_sivDuplicateViewState, true);
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
        setDuplicateViewState(duplicateViewState);
        setFixedSize(widthScale, heightScale);
        setScaleTarget(scaleTarget);
    }

    private void checkShapeCompat() {
        if (mShapeCompat == null)
            mShapeCompat = new ShapeCompat();
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
        checkShapeCompat();
        mShapeCompat.sizeChanged(w, h, mShape);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        checkShapeCompat();
        if (mShapeCompat.needOnDraw(this, canvas, mShape)) {
            super.onDraw(canvas);
        }
        mShapeCompat.drawBorder(this, canvas, mShape);
        if (mPressDrawable != null) {
            mPressDrawable.setBounds(getPaddingStart(this), getPaddingTop(),
                    getWidth() - getPaddingEnd(this), getHeight() - getPaddingBottom());
            mPressDrawable.draw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPressDrawable != null
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            setHotspot(mPressDrawable, event.getX(),
                    event.getY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void drawableStateChanged() {
        if (mPressDrawable != null && mPressDrawable.isStateful()) {
            if (duplicateViewState) {
                mPressDrawable.setState(getDrawableState());
            } else {
                mPressDrawable.setState(EMPTY_STATE_SET);
            }
        }
        super.drawableStateChanged();

    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean isPress = false;
        if (mPressDrawable != null && who == mPressDrawable) {
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
        if (mPressDrawable != null) {
            mPressDrawable.setCallback(this);
        }
        checkShapeCompat();
        mShapeCompat.setPadding(getPaddingStart(this), getPaddingTop(), getPaddingEnd(this),
                getPaddingBottom(), mShape);
    }

    @Override
    protected void onDetachedFromWindow() {
        checkShapeCompat();
        mShapeCompat.detachedFromWindow(mShape);
        if (mShape != null) {
            mShape.onDetached(this);
        }
        if (mPressDrawable != null) {
            mPressDrawable.setCallback(null);
        }
        super.onDetachedFromWindow();

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        checkShapeCompat();
        mShapeCompat.setColorFilter(cf);
        super.setColorFilter(cf);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        checkShapeCompat();
        mShapeCompat.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        checkShapeCompat();
        mShapeCompat.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        checkShapeCompat();
        mShapeCompat.changeBitmap(drawable, mShape);
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        checkShapeCompat();
        mShapeCompat.changeBitmap(getDrawable(), mShape);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        checkShapeCompat();
        mShapeCompat.setScaleType(scaleType, mShape);
        super.setScaleType(scaleType);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        checkShapeCompat();
        mShapeCompat.setPadding(left, top, right, bottom, mShape);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        checkShapeCompat();
        mShapeCompat.setPadding(start, top, end, bottom, mShape);
        super.setPaddingRelative(start, top, end, bottom);
    }

    /**
     * 设置图像形状
     *
     * @param shape 图像形状
     */
    public void setImageShape(ImageShape shape) {
        checkShapeCompat();
        if (mShape != shape) {
            if (mShape != null) {
                mShape.onDetached(this);
            }
            mShape = shape;
            mShapeCompat.init(this, mShape);
            mShapeCompat.changeBitmap(getDrawable(), mShape);
            mShapeCompat.setScaleType(getScaleType(), mShape);
            mShapeCompat.setPadding(getPaddingStart(this), getPaddingTop(), getPaddingEnd(this),
                    getPaddingBottom(), mShape);
            if (mShape != null) {
                mShape.onAttached(this);
            }
            invalidate();
        }
    }

    /**
     * 设置按压 Drawable
     *
     * @param id 资源ID
     */
    @SuppressWarnings("unused")
    public void setForeground(int id) {
        if (mId != id) {
            mId = id;
            setForeground(getDrawable(getContext(), mId));
        }
    }

    /**
     * 设置按压 Drawable
     *
     * @param foreground 图片
     */
    public void setForeground(Drawable foreground) {
//        super.setForeground(foreground);
        if (mPressDrawable != foreground) {
            if (mPressDrawable != null) {
                mPressDrawable.setCallback(null);
            }
            mPressDrawable = foreground;
            mPressDrawable.setCallback(this);
            invalidate();
        }
    }


    @SuppressWarnings("unused")
    public boolean isDuplicateViewState() {
        return duplicateViewState;
    }

    public void setDuplicateViewState(boolean duplicateViewState) {
        this.duplicateViewState = duplicateViewState;
    }


    @SuppressWarnings("unused")
    public int getScaleTarget() {
        return scaleTarget;
    }

    public void setScaleTarget(int scaleTarget) {
        this.scaleTarget = scaleTarget;
        requestLayout();
        invalidate();
    }

    public void setFixedSize(int widthScale, int heightScale) {
        this.widthScale = widthScale;
        this.heightScale = heightScale;
        requestLayout();
        invalidate();
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