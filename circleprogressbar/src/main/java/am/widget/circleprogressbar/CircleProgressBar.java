package am.widget.circleprogressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * 环形进度条
 * Created by Alex on 2016/10/21.
 */

public class CircleProgressBar extends View {

    private static final int[] ATTRS = new int[]{android.R.attr.gravity};
    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 画笔
    private final PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0,
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);// 画布抗锯齿
    private final RectF mRectF = new RectF();// 进度区域
    private int mGravity;
    private float mBaseX;// 基准点X轴
    private float mBaseY;// 基准点Y轴
    private float mRadius;// 环形半径
    private int mStartAngle;// 开始角度
    private int mSweepAngle;// 扫描角度
    private float mBackgroundSize;// 背景大小
    private int mBackgroundColor;// 背景颜色
    private float mProgressSize;// 环形大小
    private int mMax;// 进度最大值
    private int mProgress;// 进度
    private float mProgressAngle;// 进度角度
    private int[] mGradientColors;// 渐变颜色
    private float[] mGradientPositions;// 渐变点
    private final Matrix rotateMatrix = new Matrix();// 渐变旋转
    private float mDialGap;
    private int mDialUnit;
    private int mDialSpecialGap;
    private float mDialHeight;
    private float mDialWidth;
    private float mDialSpecialHeight;
    private float mDialSpecialWidth;
    private int mDialGravity;


    public CircleProgressBar(Context context) {
        super(context);
        initView(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT > 4) {
            updateTextPaintDensity();
        }
        int gravity = Gravity.CENTER;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    gravity = a.getInt(attr, gravity);
                    break;
            }
        }
        a.recycle();
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        float radius = 0;
        int startAngle = 0;
        int sweepAngle = 0;
        float backgroundSize = 0;
        int backgroundColor = 0xff000000;
        float progressSize = 0;
        int max = 100;
        int progress = 0;
        int[] colors = new int[]{};
        float[] positions = null;
        float dialGap = 0;
        int dialUnit = 0;
        float dialHeight = 0;
        float dialWidth = 0;
        radius = custom.getDimension(R.styleable.CircleProgressBar_cpbRadius, radius);
        startAngle = custom.getInteger(R.styleable.CircleProgressBar_cpbStartAngle, startAngle);
        sweepAngle = custom.getInteger(R.styleable.CircleProgressBar_cpbSweepAngle, sweepAngle);
        backgroundSize = custom.getDimension(R.styleable.CircleProgressBar_cpbBackgroundSize, backgroundSize);
        backgroundColor = custom.getColor(R.styleable.CircleProgressBar_cpbBackgroundColor, backgroundColor);
        progressSize = custom.getDimension(R.styleable.CircleProgressBar_cpbProgressSize, progressSize);
        max = custom.getInteger(R.styleable.CircleProgressBar_cpbMax, max);
        progress = custom.getInteger(R.styleable.CircleProgressBar_cpbProgress, progress);
        if (!custom.hasValue(R.styleable.CircleProgressBar_cpbFirstGradientColors)
                && !custom.hasValue(R.styleable.CircleProgressBar_cpbSecondGradientColors)
                && !custom.hasValue(R.styleable.CircleProgressBar_cpbThirdGradientColors)
                && !custom.hasValue(R.styleable.CircleProgressBar_cpbFourthGradientColors)) {
            colors = new int[]{0xff000000};
        } else {
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbFirstGradientColors))
                colors = addColor(colors, custom.getColor(
                        R.styleable.CircleProgressBar_cpbFirstGradientColors, 0xff000000));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbSecondGradientColors))
                colors = addColor(colors, custom.getColor(
                        R.styleable.CircleProgressBar_cpbSecondGradientColors, 0xff000000));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbThirdGradientColors))
                colors = addColor(colors, custom.getColor(
                        R.styleable.CircleProgressBar_cpbThirdGradientColors, 0xff000000));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbFourthGradientColors))
                colors = addColor(colors, custom.getColor(
                        R.styleable.CircleProgressBar_cpbFourthGradientColors, 0xff000000));
        }
        if (custom.hasValue(R.styleable.CircleProgressBar_cpbFirstGradientPositions)
                || custom.hasValue(R.styleable.CircleProgressBar_cpbSecondGradientPositions)
                || custom.hasValue(R.styleable.CircleProgressBar_cpbThirdGradientPositions)
                || custom.hasValue(R.styleable.CircleProgressBar_cpbFourthGradientPositions)) {
            positions = new float[]{};
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbFirstGradientPositions))
                positions = addPosition(positions, custom.getFloat(
                        R.styleable.CircleProgressBar_cpbFirstGradientPositions, 0));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbSecondGradientPositions))
                positions = addPosition(positions, custom.getFloat(
                        R.styleable.CircleProgressBar_cpbSecondGradientPositions, 0));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbThirdGradientPositions))
                positions = addPosition(positions, custom.getFloat(
                        R.styleable.CircleProgressBar_cpbThirdGradientPositions, 0));
            if (custom.hasValue(R.styleable.CircleProgressBar_cpbFourthGradientPositions))
                positions = addPosition(positions, custom.getFloat(
                        R.styleable.CircleProgressBar_cpbFourthGradientPositions, 0));
            positions = addPosition(positions, 1);
        }
        dialGap = custom.getDimension(R.styleable.CircleProgressBar_cpbDialGap, dialGap);
        dialUnit = custom.getInteger(R.styleable.CircleProgressBar_cpbDialUnit, dialUnit);

        dialHeight = custom.getDimension(R.styleable.CircleProgressBar_cpbDialHeight, dialHeight);
        dialWidth = custom.getDimension(R.styleable.CircleProgressBar_cpbDialWidth, dialWidth);


        custom.recycle();
        setGravity(gravity);
        setRadius(radius);
        setStartAngle(startAngle);
        setSweepAngle(sweepAngle);
        setBackgroundSize(backgroundSize);
        setBackgroundColor(backgroundColor);
        setProgressSize(progressSize);
        setMax(max);
        setProgress(progress);
        setDialGap(dialGap);
        setDialUnit(dialUnit);

        setDialHeight(dialHeight);
        setDialWidth(dialWidth);
        setGradientColors(addColor(colors, colors[0]));
        setGradientPositions(positions);

    }

    @TargetApi(5)
    private void updateTextPaintDensity() {
        mPaint.density = getResources().getDisplayMetrics().density;
    }

    private int[] addColor(int[] colors, int color) {
        int length = colors.length;
        int[] result = new int[length + 1];
        System.arraycopy(colors, 0, result, 0, length);
        result[length] = color;
        return result;
    }

    private float[] addPosition(float[] positions, float position) {
        int length = positions.length;
        float[] result = new float[length + 1];
        System.arraycopy(positions, 0, result, 0, length);
        result[length] = position;
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        float itemSize = mRadius * 2;
        if (mDialUnit != 0 && (mDialHeight != 0 || mDialSpecialHeight != 0)) {
            itemSize += mDialGap * 2;
            itemSize += mDialHeight * 2;
        }
        final int width = Math.max((int) Math.floor(itemSize) + paddingStart + paddingEnd,
                suggestedMinimumWidth);
        final int height = Math.max((int) Math.floor(itemSize) + paddingTop + paddingBottom,
                suggestedMinimumHeight);
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
        switch (mGravity) {
            default:
            case Gravity.CENTER:
                mBaseX = paddingStart + (getMeasuredWidth() - paddingStart - paddingEnd) * 0.5f;
                mBaseY = paddingTop + (getMeasuredHeight() - paddingTop - paddingBottom) * 0.5f;
                break;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        drawBackground(canvas);
        drawProgress(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    protected void drawBackground(Canvas canvas) {
        if (mBackgroundSize == 0)
            return;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBackgroundSize);
        mPaint.setColor(mBackgroundColor);
        mPaint.setShader(null);
        final float halfSize = mProgressSize * 0.5f;
        mRectF.set(-mRadius + halfSize, -mRadius + halfSize, mRadius - halfSize, mRadius - halfSize);
        canvas.save();
        canvas.translate(mBaseX, mBaseY);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mPaint);
        canvas.restore();
    }

    /**
     * 绘制进度
     *
     * @param canvas 画布
     */
    protected void drawProgress(Canvas canvas) {
        if (mRadius == 0 || mProgressSize == 0)
            return;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mProgressSize);
        final float halfCircleSize = mProgressSize * 0.5f;
        mRectF.set(-mRadius + halfCircleSize, -mRadius + halfCircleSize,
                mRadius - halfCircleSize, mRadius - halfCircleSize);
        SweepGradient sweepGradient = new SweepGradient(0, 0, mGradientColors, mGradientPositions);
        rotateMatrix.setRotate(mStartAngle, 0, 0);
        sweepGradient.setLocalMatrix(rotateMatrix);
        mPaint.setShader(sweepGradient);
        canvas.save();
        canvas.translate(mBaseX, mBaseY);
        canvas.drawArc(mRectF, mStartAngle, mProgressAngle, false, mPaint);
        canvas.restore();
    }

    /**
     * 设置排版方式
     *
     * @param gravity 排版方式
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        requestLayout();
        invalidate();
    }

    /**
     * 设置环形半径
     *
     * @param radius 环形半径
     */
    public void setRadius(float radius) {
        mRadius = radius;
        requestLayout();
        invalidate();
    }

    /**
     * 设置起始角度
     *
     * @param angle 角度
     */
    public void setStartAngle(int angle) {
        mStartAngle = angle;
        invalidate();
    }

    /**
     * 设置扫描角度
     *
     * @param angle 角度
     */
    public void setSweepAngle(int angle) {
        mSweepAngle = angle;
        invalidate();
    }

    /**
     * 设置背景尺寸
     *
     * @param size 尺寸
     */
    public void setBackgroundSize(float size) {
        mBackgroundSize = size;
        invalidate();
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景颜色
     */
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    /**
     * 设置环形尺寸
     *
     * @param size 尺寸
     */
    public void setProgressSize(float size) {
        mProgressSize = size;
        requestLayout();
        invalidate();
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (mProgress == progress)
            return;
        mProgress = progress > mMax ? mMax : progress;
        mProgressAngle = mSweepAngle * ((float) mProgress / mMax);
        invalidate();
    }

    /**
     * 设置渐变色
     *
     * @param colors 渐变色
     */
    public void setGradientColors(int... colors) {
        mGradientColors = colors;
        invalidate();
    }

    /**
     * 设置渐变点
     *
     * @param positions 渐变点
     */
    public void setGradientPositions(float... positions) {
        mGradientPositions = positions;
        invalidate();
    }

    /**
     * 设置刻度间隔
     *
     * @param gap 间隔
     */
    public void setDialGap(float gap) {
        mDialGap = gap;
        requestLayout();
        invalidate();
    }

    /**
     * 设置刻度单位
     *
     * @param unit 单位
     */
    public void setDialUnit(int unit) {
        mDialUnit = unit;
        requestLayout();
        invalidate();
    }

    /**
     * 设置刻度高
     *
     * @param height 高
     */
    public void setDialHeight(float height) {
        mDialHeight = height;
        requestLayout();
        invalidate();
    }

    /**
     * 设置刻度宽
     *
     * @param width 宽
     */
    public void setDialWidth(float width) {
        mDialWidth = width;
        requestLayout();
        invalidate();
    }

    // TODO
}
