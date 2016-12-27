package am.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.ImageView;

import am.drawable.MaterialProgressDrawable;

/**
 * 载入动画ImageView
 *
 * android.support.v4.widget.CircleImageView
 *
 * Created by Alex on 2016/12/27.
 */

public class MaterialProgressImageView extends ImageView implements Animatable {

    private static final int DEFAULT_COLOR = 0xFFFAFAFA;
    private static final int DEFAULT_RADIUS = 10;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;
    private int mShadowRadius;
    private boolean showShadowsCircle = false;
    private MaterialProgressDrawable drawable;

    public MaterialProgressImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public MaterialProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    @TargetApi(11)
    public MaterialProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MaterialProgressImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressImageView);
        boolean autoStart = custom.getBoolean(R.styleable.MaterialProgressImageView_mpiAutoStart, true);
        custom.recycle();

        Drawable background = getBackground();
        if (background == null)
            setShadowsCircleColorAndRadius(DEFAULT_COLOR,
                    getResources().getDisplayMetrics().density * DEFAULT_RADIUS,
                    SHADOW_ELEVATION);
        drawable = new MaterialProgressDrawable(getResources().getDisplayMetrics().density);
        drawable.setBackgroundColor(0x00000000);
        drawable.setAlpha(255);
        drawable.setCallback(this);
        setImageDrawable(drawable);
        if (autoStart && !isRunning())
            start();
    }

    /**
     * 设置圆形阴影背景颜色及阴影半径
     *
     * @param color  背景颜色
     * @param radius 阴影半径
     */
    @SuppressWarnings("all")
    public void setShadowsCircleColorAndRadius(int color, final float radius, final float elevation) {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int diameter = (int) (radius * density * 2);
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            Compat.setElevation(this, elevation * density);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius, diameter);
            circle = new ShapeDrawable(oval);
            Compat.setLayerType(this, Compat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(color);
        showShadowsCircle = true;
        super.setBackgroundDrawable(circle);
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 设置背景颜色
     * @param backgroundColor 背景颜色
     */
    public void setDrawableBackgroundColor(int backgroundColor) {
        drawable.setBackgroundColor(backgroundColor);
    }

    /**
     *  设置变换颜色
     * @param schemeColors 变换颜色
     */
    public void setColorSchemeColors(int... schemeColors) {
        drawable.setColorSchemeColors(schemeColors);
    }

    @Override
    @TargetApi(16)
    public void setBackground(Drawable background) {
        showShadowsCircle = false;
        super.setBackground(background);
    }

    @Override
    public void setBackgroundColor(int color) {
        showShadowsCircle = false;
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resId) {
        showShadowsCircle = false;
        super.setBackgroundResource(resId);
    }

    @Override
    @Deprecated
    @SuppressWarnings("all")
    public void setBackgroundDrawable(Drawable background) {
        showShadowsCircle = false;
        super.setBackgroundDrawable(background);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (showShadowsCircle && !elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
    }

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;
        private int mCircleDiameter;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                    mShadowRadius, new int[]{
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
            }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = MaterialProgressImageView.this.getWidth();
            final int viewHeight = MaterialProgressImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }

    @Override
    public void start() {
        if (!drawable.isRunning()) {
            drawable.start();
        }
    }

    @Override
    public void stop() {
        if (drawable.isRunning()) {
            drawable.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return drawable.isRunning();
    }
}
