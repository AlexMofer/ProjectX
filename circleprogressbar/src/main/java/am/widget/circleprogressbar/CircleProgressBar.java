package am.widget.circleprogressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
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

    public static final int DIAL_GRAVITY_CENTER = 0;//长短刻度剧中显示
    public static final int DIAL_GRAVITY_TOP = 1;//长短刻度顶部对齐
    public static final int DIAL_GRAVITY_BOTTOM = 2;//长短刻度底部对齐
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
    private int mDialCount = 0;
    private int mDialAngle;
    private float mDialHeight;
    private float mDialWidth;
    private int mDialColor;
    private int mDialSpecialUnit;
    private float mDialSpecialHeight;
    private float mDialSpecialWidth;
    private int mDialSpecialColor;
    private int mDialGravity;
    private boolean mShowSpecialDialValue;
    private float mSpecialDialValueGap;
    private float mSpecialDialValueTextSize;
    private int mSpecialDialValueTextColor;
    private final Rect mTextMeasureBounds = new Rect();// 文字测量


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
        mPaint.setTextAlign(Paint.Align.CENTER);
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
        int dialAngle = 0;
        float dialHeight = 0;
        float dialWidth = 0;
        int dialColor = 0xff000000;
        int dialSpecialUnit = 0;
        float dialSpecialHeight;
        float dialSpecialWidth;
        int dialSpecialColor;
        int dialGravity = DIAL_GRAVITY_CENTER;
        boolean showSpecialDialValue;
        float specialDialValueGap = 0;
        float specialDialValueTextSize = 12 * getResources().getDisplayMetrics().density;
        int specialDialValueTextColor = 0xff000000;
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
        dialAngle = custom.getInteger(R.styleable.CircleProgressBar_cpbDialAngle, dialAngle);
        dialHeight = custom.getDimension(R.styleable.CircleProgressBar_cpbDialHeight, dialHeight);
        dialWidth = custom.getDimension(R.styleable.CircleProgressBar_cpbDialWidth, dialWidth);
        dialColor = custom.getColor(R.styleable.CircleProgressBar_cpbDialColor, dialColor);
        dialSpecialUnit = custom.getInteger(R.styleable.CircleProgressBar_cpbDialSpecialUnit,
                dialSpecialUnit);
        dialSpecialHeight = custom.getDimension(R.styleable.CircleProgressBar_cpbDialSpecialHeight,
                dialHeight);
        dialSpecialWidth = custom.getDimension(R.styleable.CircleProgressBar_cpbDialSpecialWidth,
                dialWidth);
        dialSpecialColor = custom.getColor(R.styleable.CircleProgressBar_cpbDialSpecialColor,
                dialColor);
        dialGravity = custom.getInt(R.styleable.CircleProgressBar_cpbDialGravity, dialGravity);
        showSpecialDialValue = custom.getBoolean(
                R.styleable.CircleProgressBar_cpbShowSpecialDialValue, false);
        specialDialValueGap = custom.getDimension(
                R.styleable.CircleProgressBar_cpbSpecialDialValueGap, specialDialValueGap);
        specialDialValueTextSize = custom.getDimension(
                R.styleable.CircleProgressBar_cpbSpecialDialValueTextSize,
                specialDialValueTextSize);
        specialDialValueTextColor = custom.getColor(
                R.styleable.CircleProgressBar_cpbSpecialDialValueTextColor,
                specialDialValueTextColor);
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
        setGradientColors(addColor(colors, colors[0]));
        setGradientPositions(positions);
        setDialGap(dialGap);
        setDialAngle(dialAngle);
        setDialHeight(dialHeight);
        setDialWidth(dialWidth);
        setDialColor(dialColor);
        setDialSpecialUnit(dialSpecialUnit);
        setDialSpecialHeight(dialSpecialHeight);
        setDialSpecialWidth(dialSpecialWidth);
        setDialSpecialColor(dialSpecialColor);
        setDialGravity(dialGravity);
        setShowSpecialDialValue(showSpecialDialValue);
        setSpecialDialValueGap(specialDialValueGap);
        setSpecialDialValueTextSize(specialDialValueTextSize);
        setSpecialDialValueTextColor(specialDialValueTextColor);
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
        if (mDialCount > 0 && (mDialHeight > 0 || mDialSpecialHeight > 0)) {
            itemSize += mDialGap * 2;
            itemSize += (mDialHeight > mDialSpecialHeight ? mDialHeight : mDialSpecialHeight) * 2;
        }
        if (mShowSpecialDialValue) {
            itemSize += mSpecialDialValueGap * 2;
            mPaint.setTextSize(mSpecialDialValueTextSize);
            mPaint.getTextBounds("88", 0, 2, mTextMeasureBounds);
            itemSize += mTextMeasureBounds.height() * 2;
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
        drawDial(canvas);
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
        if (mRadius == 0 || mProgressSize == 0 || mProgress < 0)
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
     * 绘制刻度
     *
     * @param canvas 画布
     */
    protected void drawDial(Canvas canvas) {
        if (mDialCount <= 0 || (mDialHeight <= 0 && mDialSpecialHeight <= 0))
            return;
        mPaint.setTextSize(mSpecialDialValueTextSize);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setShader(null);
        canvas.save();
        canvas.translate(mBaseX, mBaseY);


        canvas.rotate(mStartAngle);
        if (mDialSpecialUnit <= 0) {
            final float halfDialWidth = mDialWidth * 0.5f;
            mPaint.setStrokeWidth(mDialWidth);
            mPaint.setColor(mDialColor);
            for (int i = 0; i <= mDialCount; i++) {
                canvas.drawLine(mRadius + mDialGap + mDialHeight - halfDialWidth, 0,
                        mRadius + mDialGap + halfDialWidth, 0, mPaint);
                canvas.rotate(mDialAngle);
            }
        } else {
            final float halfDialWidth = mDialWidth * 0.5f;
            final float halfDialSpecialWidth = mDialSpecialWidth * 0.5f;
            final float maxDialHeight = mDialSpecialHeight > mDialHeight ?
                    mDialSpecialHeight : mDialHeight;
            final float centerX = mRadius + mDialGap + maxDialHeight * 0.5f;
            for (int i = 0; i <= mDialCount; i++) {
                if (i % mDialSpecialUnit == 0) {
                    mPaint.setStrokeWidth(mDialSpecialWidth);
                    mPaint.setColor(mDialSpecialColor);
                    switch (mDialGravity) {
                        default:
                        case DIAL_GRAVITY_CENTER:
                            canvas.drawLine(centerX + mDialSpecialHeight * 0.5f - halfDialSpecialWidth,
                                    0, centerX - mDialSpecialHeight * 0.5f + halfDialSpecialWidth, 0, mPaint);
                            break;
                        case DIAL_GRAVITY_TOP:
                            canvas.drawLine(mRadius + mDialGap + maxDialHeight - halfDialSpecialWidth,
                                    0, mRadius + mDialGap + maxDialHeight - mDialSpecialHeight + halfDialSpecialWidth, 0, mPaint);
                            break;
                        case DIAL_GRAVITY_BOTTOM:
                            canvas.drawLine(
                                    mRadius + mDialGap + mDialSpecialHeight - halfDialSpecialWidth,
                                    0, mRadius + mDialGap + halfDialSpecialWidth, 0, mPaint);
                            break;
                    }
                    canvas.rotate(90);
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(mSpecialDialValueTextColor);
                    canvas.drawText(getDialValue(i, mDialCount, mMax), 0,
                            -mRadius - mDialGap - mDialSpecialHeight - mSpecialDialValueGap - mTextMeasureBounds.bottom,
                            mPaint);
                    mPaint.setStyle(Paint.Style.STROKE);
                    canvas.rotate(-90);

                } else {
                    mPaint.setStrokeWidth(mDialWidth);
                    mPaint.setColor(mDialColor);
                    switch (mDialGravity) {
                        default:
                        case DIAL_GRAVITY_CENTER:
                            canvas.drawLine(centerX + mDialHeight * 0.5f - halfDialSpecialWidth,
                                    0, centerX - mDialHeight * 0.5f + halfDialSpecialWidth, 0, mPaint);
                            break;
                        case DIAL_GRAVITY_TOP:
                            canvas.drawLine(mRadius + mDialGap + maxDialHeight - halfDialSpecialWidth,
                                    0, mRadius + mDialGap + maxDialHeight - mDialHeight + halfDialSpecialWidth, 0, mPaint);
                            break;
                        case DIAL_GRAVITY_BOTTOM:
                            canvas.drawLine(mRadius + mDialGap + mDialHeight - halfDialWidth, 0,
                                    mRadius + mDialGap + halfDialWidth, 0, mPaint);
                            break;
                    }

                }
                canvas.rotate(mDialAngle);
            }
        }
        canvas.restore();
    }

    /**
     * 获取刻度值
     *
     * @param dialPosition 第几个刻度
     * @param dialCount    刻度总数
     * @param max          最大值
     * @return 刻度值
     */
    protected String getDialValue(int dialPosition, int dialCount, int max) {
        return Integer.toString(dialPosition * (max / dialCount));
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
        mDialCount = mDialAngle <= 0 ? 0 : mSweepAngle / mDialAngle;
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
     * 设置刻度角度
     *
     * @param angle 角度
     */
    public void setDialAngle(int angle) {
        mDialAngle = angle;
        mDialCount = mDialAngle <= 0 ? 0 : mSweepAngle / mDialAngle;
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

    /**
     * 设置刻度颜色
     *
     * @param color 颜色
     */
    public void setDialColor(int color) {
        mDialColor = color;
        invalidate();
    }

    /**
     * 设置特殊刻度与普通刻度的间隔
     * 单位以普通刻度为参考，设置为2时，第1个普通刻度、第3个普通刻度等每隔一个普通刻度都变为特殊刻度
     *
     * @param unit 刻度
     */
    public void setDialSpecialUnit(int unit) {
        mDialSpecialUnit = unit;
        invalidate();
    }

    /**
     * 设置特殊刻度的高
     *
     * @param height 高
     */
    public void setDialSpecialHeight(float height) {
        mDialSpecialHeight = height;
        requestLayout();
        invalidate();
    }

    /**
     * 设置特殊刻度的宽
     *
     * @param width 宽
     */
    public void setDialSpecialWidth(float width) {
        mDialSpecialWidth = width;
        invalidate();
    }

    /**
     * 设置特殊刻度颜色
     *
     * @param color 颜色
     */
    public void setDialSpecialColor(int color) {
        mDialSpecialColor = color;
        invalidate();
    }

    /**
     * 设置长短刻度的对齐方式
     *
     * @param gravity 对齐方式
     */
    public void setDialGravity(int gravity) {
        mDialGravity = gravity;
        invalidate();
    }

    /**
     * 设置是否显示特殊刻度值
     *
     * @param show 是否显示
     */
    public void setShowSpecialDialValue(boolean show) {
        mShowSpecialDialValue = show;
        requestLayout();
        invalidate();
    }

    /**
     * 设置特殊刻度值间距
     *
     * @param gap 间距
     */
    public void setSpecialDialValueGap(float gap) {
        mSpecialDialValueGap = gap;
        requestLayout();
        invalidate();
    }

    /**
     * 设置特殊刻度值文字大小
     *
     * @param textSize 文字大小
     */
    public void setSpecialDialValueTextSize(float textSize) {
        mSpecialDialValueTextSize = textSize;
        requestLayout();
        invalidate();
    }

    /**
     * 设置特殊刻度值文字颜色
     *
     * @param color 文字颜色
     */
    public void setSpecialDialValueTextColor(int color) {
        mSpecialDialValueTextColor = color;
        invalidate();
    }

    // TODO
}
