package am.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 载入动画ImageView
 *
 * android.support.v4.widget.CircleImageView
 */
public class MaterialProgressCircleImageView extends ImageView implements Animatable {

    private static final int DEFAULT_COLOR = 0xFFFAFAFA;
    private static final int DEFAULT_RADIUS = 10;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final int DEFAULT_PROGRESS_BACKGROUND = 0x00000000;
    private static final int[] DEFAULT_PROGRESS_COLORS = new int[]{0xff33b5e5, 0xff99cc00,
            0xffff4444, 0xffffbb33};
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;
    private int mShadowRadius;
    private boolean showShadowsCircle = false;

    private MaterialLoadingProgressDrawable drawable;
    private boolean isRunning = false;

    public MaterialProgressCircleImageView(Context context) {
        this(context, null);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Drawable background = getBackground();
        if (background == null)
            setShadowsCircleColorAndRadius(DEFAULT_COLOR,
                    (int) (context.getResources().getDisplayMetrics().density * DEFAULT_RADIUS));
        if (getDrawable() == null) {
            setMaterialLoadingProgressDrawable(DEFAULT_PROGRESS_BACKGROUND, true,
                    DEFAULT_PROGRESS_COLORS);
        }
    }

    /**
     * 设置圆形阴影背景颜色及阴影半径
     *
     * @param color  背景颜色
     * @param radius 阴影半径
     */
    @SuppressWarnings("all")
    public void setShadowsCircleColorAndRadius(int color, final float radius) {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int diameter = (int) (radius * density * 2);
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius, diameter);
            circle = new ShapeDrawable(oval);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
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
     * 设置载入动画
     *
     * @param backgroundColor 背景颜色
     * @param start           立即开始
     * @param schemeColors    变换颜色
     */
    public void setMaterialLoadingProgressDrawable(@ColorInt int backgroundColor,
                                                   boolean start,
                                                   int... schemeColors) {
        drawable = new MaterialLoadingProgressDrawable(this, backgroundColor, start, schemeColors);
        isRunning = true;
        setImageDrawable(drawable);
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
            final int viewWidth = MaterialProgressCircleImageView.this.getWidth();
            final int viewHeight = MaterialProgressCircleImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if ((visibility == GONE || visibility == INVISIBLE) && isRunning && drawable != null)
            drawable.stop();
        super.setVisibility(visibility);
        if (visibility == VISIBLE && isRunning && drawable != null)
            drawable.start();
    }

    @Override
    public void start() {
        if (drawable != null) {
            isRunning = true;
            drawable.start();
        }
    }

    @Override
    public void stop() {
        if (drawable != null) {
            isRunning = false;
            drawable.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
