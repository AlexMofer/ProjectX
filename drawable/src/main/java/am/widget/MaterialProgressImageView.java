package am.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

import am.drawable.MaterialProgressDrawable;

/**
 * 载入动画ImageView
 * <p>
 * android.support.v4.widget.CircleImageView
 * <p>
 * Created by Alex on 2016/12/27.
 */

public class MaterialProgressImageView extends ImageView implements Animatable {

    private static final int DEFAULT_COLOR = 0xFFFAFAFA;
    private static final int SHADOW_ELEVATION = 2;
    private boolean showShadowsCircle = false;
    private float mElevation;
    private MaterialProgressDrawable drawable;
    private ShapeDrawable background;

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
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final float density = getResources().getDisplayMetrics().density;
        ArrayList<Integer> colors = new ArrayList<>();
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressImageView,
                defStyleAttr, defStyleRes);
        boolean autoStart = custom.getBoolean(R.styleable.MaterialProgressImageView_mpiAutoStart, true);
        int color = custom.getColor(R.styleable.MaterialProgressImageView_mpiShadowsCircleColor, DEFAULT_COLOR);
        float elevation = custom.getDimension(R.styleable.MaterialProgressImageView_mpiElevation, density * SHADOW_ELEVATION);
        if (custom.hasValue(R.styleable.MaterialProgressImageView_mpiSchemeColor1)) {
            colors.add(custom.getColor(R.styleable.MaterialProgressImageView_mpiSchemeColor1, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.MaterialProgressImageView_mpiSchemeColor2)) {
            colors.add(custom.getColor(R.styleable.MaterialProgressImageView_mpiSchemeColor2, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.MaterialProgressImageView_mpiSchemeColor3)) {
            colors.add(custom.getColor(R.styleable.MaterialProgressImageView_mpiSchemeColor3, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.MaterialProgressImageView_mpiSchemeColor4)) {
            colors.add(custom.getColor(R.styleable.MaterialProgressImageView_mpiSchemeColor4, Color.BLACK));
        }
        if (custom.hasValue(R.styleable.MaterialProgressImageView_mpiSchemeColor5)) {
            colors.add(custom.getColor(R.styleable.MaterialProgressImageView_mpiSchemeColor5, Color.BLACK));
        }
        custom.recycle();
        setElevationCompat(elevation);
        Drawable background = getBackground();
        if (background == null)
            setShadowsCircleBackground(color);
        drawable = new MaterialProgressDrawable(density);
        drawable.setBackgroundColor(0x00000000);
        drawable.setAlpha(255);
        drawable.setCallback(this);
        final int size=colors.size();
        if (size > 0) {
            int[] colorArray=new int[size];
            for(int i=0;i<size;i++){
                colorArray[i]=colors.get(i);
            }
            drawable.setColorSchemeColors(colorArray);
        }
        setImageDrawable(drawable);
        if (autoStart)
            start();
    }

    /**
     * 设置圆形阴影背景颜色
     *
     * @param color 背景颜色
     */
    @SuppressWarnings("all")
    public void setShadowsCircleBackground(int color) {
        if (background == null) {
            if (elevationSupported()) {
                background = new ShapeDrawable(new OvalShape());
            } else {
                background = new ShapeDrawable(new OvalShadow());
            }
        }
        background.getPaint().setColor(color);
        showShadowsCircle = true;
        super.setBackgroundDrawable(background);
    }

    /**
     * Sets the base elevation of this view, in pixels.
     * 仅对使用默认圆形背景有效{@link #setShadowsCircleBackground(int)}
     */
    public void setElevationCompat(float elevation) {
        if (elevationSupported()) {
            setElevation(elevation);
        } else {
            mElevation = elevation;
        }
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景颜色
     */
    @SuppressWarnings("unused")
    public void setDrawableBackgroundColor(int backgroundColor) {
        drawable.setBackgroundColor(backgroundColor);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        drawable.setAlpha((int) (255 * alpha));
    }

    /**
     * 设置变换颜色
     *
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
            final int moreSize = Math.round(mElevation * 2);
            setMeasuredDimension(resolveSize(getMeasuredWidth() + moreSize, widthMeasureSpec),
                    resolveSize(getMeasuredHeight() + moreSize, heightMeasureSpec));

        }
    }

    private class OvalShadow extends OvalShape {

        private static final int FILL_SHADOW_COLOR = 0x3D000000;

        private RadialGradient mRadialGradient;
        private final Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF mOvalRectF = new RectF();
        private final Matrix mMatrix = new Matrix();

        OvalShadow() {
            mRadialGradient = new RadialGradient(10, 10, 10,
                    new int[]{FILL_SHADOW_COLOR, FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    new float[]{0, 0.6f, 1f}, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final float elevation = mElevation;
            final RectF rect = rect();
            mMatrix.reset();
            mMatrix.setScale(rect.width() * 0.05f, rect.height() * 0.05f);
            mOvalRectF.left = rect.left + elevation;
            mOvalRectF.top = rect.top + elevation;
            mOvalRectF.right = rect.right - elevation;
            mOvalRectF.bottom = rect.bottom - elevation;
            mRadialGradient.setLocalMatrix(mMatrix);
            canvas.drawOval(rect, mShadowPaint);
            canvas.drawOval(mOvalRectF, paint);
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
