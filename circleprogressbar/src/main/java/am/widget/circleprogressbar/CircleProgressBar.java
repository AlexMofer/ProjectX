package am.widget.circleprogressbar;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
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
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * 环形进度条
 * Created by Alex on 2016/10/21.
 */
@SuppressWarnings("unused")
public class CircleProgressBar extends View {

    public static final int DIAL_GRAVITY_CENTER = 0;//长短刻度剧中显示
    public static final int DIAL_GRAVITY_TOP = 1;//长短刻度顶部对齐
    public static final int DIAL_GRAVITY_BOTTOM = 2;//长短刻度底部对齐
    public static final int ST_NONE = 0;// 无缩放
    public static final int ST_INSIDE = 1;// 内部缩放
    public static final int ST_CROP = 2;// 环绕
    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 画笔
    private final PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0,
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);// 画布抗锯齿
    private final RectF mRectF = new RectF();// 进度区域
    private final Matrix mRotateMatrix = new Matrix();// 渐变旋转
    private final Rect mTextMeasureBounds = new Rect();// 文字测量
    private float mItemSize;//子项所需大小
    private int mGravity;
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
    private float mDialGap;// 刻度条与进度条间隔
    private int mDialCount = 0; // 刻度条数目
    private int mDialAngle;// 刻度角度
    private float mDialHeight;// 刻度条高
    private float mDialWidth;// 刻度条宽
    private int mDialColor;// 刻度条颜色
    private int mDialSpecialUnit;// 特殊刻度条单位
    private float mDialSpecialHeight;// 特殊刻度条高度
    private float mDialSpecialWidth;// 特殊刻度条宽度
    private int mDialSpecialColor;// 特殊刻度条颜色
    private int mDialGravity;// 刻度条与特殊刻度条对齐模式
    private boolean mShowSpecialDialValue;// 显示特殊刻度条刻度值开关
    private float mSpecialDialValueGap;// 刻度条与特殊刻度值间隔
    private float mSpecialDialValueTextSize;// 特殊刻度值文字大小
    private int mSpecialDialValueTextColor;// 特殊刻度值文字颜色
    private int mSpecialDialValueTextBottom = 0;// 特殊刻度值文字基线
    private boolean mShowProgressValue;// 是否显示进度值
    private float mProgressValueTextSize;// 进度值文字大小
    private int mProgressValueTextColor;// 进度值文字颜色
    private int mProgressValueTextHeight = 0;// 进度值文字高度
    private int mProgressValueTextBottom = 0;// 进度值文字基线
    private String mTopText;// 进度值顶部文字
    private float mTopTextGap;// 进度值顶部文字与进度值间距
    private float mTopTextSize;// 进度值顶部文字大小
    private int mTopTextColor;// 进度值顶部文字颜色
    private String mBottomText;// 进度值底部文字
    private float mBottomTextGap;//进度值底部文字与进度值间距
    private float mBottomTextSize;// 进度值底部文字大小
    private int mBottomTextColor;// 进度值底部文字颜色
    private int mScaleType;// 缩放类型

    private ValueAnimator mProgressAnimator;// 进度动画
    private int mAnimatorProgress = 0;//动画进度
    private float mAnimatorProgressAngle = 0;// 动画进度角度
    private long mProgressDuration;// 进度动画时长
    private TimeInterpolator mProgressInterpolator;// 进度动画补帧器
    private ValueAnimator.AnimatorUpdateListener mAnimatorListener;//动画更新监听

    private int mLoadingStartAngle;// 载入开始角度
    private int mLoadingSweepAngle;// 载入扫描角度
    private final ValueAnimator mLoadingAnimator = ValueAnimator.ofFloat(0f, 1f);// 载入动画
    private float mLoadingOffset = 0;// 动画偏移
    private boolean mLoadingDrawOther = false;// 是否绘制其他
    private String mLoadingText = null;// 载入进度值

    public enum ProgressMode {
        PROGRESS(0),
        LOADING(1);

        ProgressMode(int ni) {
            nativeInt = ni;
        }

        public static ProgressMode valueOf(int value) {
            switch (value) {
                case 0:
                    return PROGRESS;
                case 1:
                    return LOADING;
                default:
                    return null;
            }
        }

        final int nativeInt;
    }

    private ProgressMode mProgressMode = ProgressMode.PROGRESS;//进度条模式

    /**
     * 计算器
     */
    public interface Calculator {
        String getDialValue(CircleProgressBar bar, int dialPosition, int dialCount, int max);

        String getProgressValue(CircleProgressBar bar, int animatorProgress,
                                ProgressMode mode, String loadingText, int progress);
    }

    private Calculator mCalculator = null;//文字计算器

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
        mAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator == mProgressAnimator) {
                    mAnimatorProgress = (int) animator.getAnimatedValue();
                    mAnimatorProgressAngle = mSweepAngle * ((float) mAnimatorProgress / mMax);
                    invalidate();
                } else if (animator == mLoadingAnimator) {
                    mLoadingOffset = (float) animator.getAnimatedValue();
                    invalidate();
                }
            }
        };
        mLoadingAnimator.addUpdateListener(mAnimatorListener);
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnimator.setRepeatMode(ValueAnimator.RESTART);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.density = getResources().getDisplayMetrics().density;
        final float density = getResources().getDisplayMetrics().density;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        int gravity = Gravity.CENTER;
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
        float specialDialValueTextSize = 12 * density;
        int specialDialValueTextColor = 0xff000000;
        boolean showProgressValue;
        float progressValueTextSize = 48 * density;
        int progressValueTextColor = 0xff000000;
        String topText;
        float topTextGap = 0;
        float topTextSize = 18 * density;
        int topTextColor = 0xff000000;
        String bottomText;
        float bottomTextGap = 0;
        float bottomTextSize = 18 * density;
        int bottomTextColor = 0xff000000;
        int scaleType = ST_NONE;
        int progressDuration = 1000;
        int progressMode = ProgressMode.PROGRESS.nativeInt;
        int loadingStartAngle;
        int loadingSweepAngle;
        int loadingDuration = 1000;
        int loadingRepeatMode = 1;
        boolean loadingDrawOther;
        String loadingText;
        gravity = custom.getInt(R.styleable.CircleProgressBar_cpbGravity, gravity);
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
        showProgressValue = custom.getBoolean(
                R.styleable.CircleProgressBar_cpbShowProgressValue, false);
        progressValueTextSize = custom.getDimension(
                R.styleable.CircleProgressBar_cpbProgressValueTextSize, progressValueTextSize);
        progressValueTextColor = custom.getColor(
                R.styleable.CircleProgressBar_cpbProgressValueTextColor, progressValueTextColor);
        topText = custom.getString(R.styleable.CircleProgressBar_cpbTopText);
        topTextGap = custom.getDimension(R.styleable.CircleProgressBar_cpbTopTextGap, topTextGap);
        topTextSize = custom.getDimension(R.styleable.CircleProgressBar_cpbTopTextSize, topTextSize);
        topTextColor = custom.getColor(R.styleable.CircleProgressBar_cpbTopTextColor, topTextColor);
        bottomText = custom.getString(R.styleable.CircleProgressBar_cpbBottomText);
        bottomTextGap = custom.getDimension(R.styleable.CircleProgressBar_cpbBottomTextGap,
                bottomTextGap);
        bottomTextSize = custom.getDimension(R.styleable.CircleProgressBar_cpbBottomTextSize,
                bottomTextSize);
        bottomTextColor = custom.getColor(R.styleable.CircleProgressBar_cpbBottomTextColor,
                bottomTextColor);
        scaleType = custom.getInt(R.styleable.CircleProgressBar_cpbScaleType, scaleType);
        progressDuration = custom.getInteger(R.styleable.CircleProgressBar_cpbProgressDuration,
                progressDuration);
        progressMode = custom.getInt(R.styleable.CircleProgressBar_cpbProgressMode, progressMode);
        loadingStartAngle = custom.getInteger(R.styleable.CircleProgressBar_cpbLoadingStartAngle,
                startAngle);
        loadingSweepAngle = custom.getInteger(R.styleable.CircleProgressBar_cpbLoadingSweepAngle,
                sweepAngle);
        loadingDuration = custom.getInteger(R.styleable.CircleProgressBar_cpbLoadingDuration,
                loadingDuration);
        loadingRepeatMode = custom.getInt(R.styleable.CircleProgressBar_cpbLoadingRepeatMode,
                loadingRepeatMode);
        loadingDrawOther = custom.getBoolean(R.styleable.CircleProgressBar_cpbLoadingDrawOther,
                false);
        loadingText = custom.getString(R.styleable.CircleProgressBar_cpbLoadingText);
        custom.recycle();
        setProgressDuration(progressDuration);
        setLoadingDuration(loadingDuration);
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
        setShowProgressValue(showProgressValue);
        setProgressValueTextSize(progressValueTextSize);
        setProgressValueTextColor(progressValueTextColor);
        setTopText(topText);
        setTopTextGap(topTextGap);
        setTopTextSize(topTextSize);
        setTopTextColor(topTextColor);
        setBottomText(bottomText);
        setBottomTextGap(bottomTextGap);
        setBottomTextSize(bottomTextSize);
        setBottomTextColor(bottomTextColor);
        setScaleType(scaleType);
        setLoadingStartAngle(loadingStartAngle);
        setLoadingSweepAngle(loadingSweepAngle);
        setLoadingRepeatMode(loadingRepeatMode == 1 ? ValueAnimator.RESTART
                : ValueAnimator.REVERSE);
        setLoadingDrawOther(loadingDrawOther);
        setLoadingText(loadingText);
        setProgressMode(ProgressMode.valueOf(progressMode));
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
        mItemSize = mRadius * 2;
        if (mDialCount > 0 && (mDialHeight > 0 || mDialSpecialHeight > 0)) {
            mItemSize += mDialGap * 2;
            mItemSize += (mDialHeight > mDialSpecialHeight ? mDialHeight : mDialSpecialHeight) * 2;
        }
        final String TEMP = "88";
        if (mShowSpecialDialValue) {
            mItemSize += mSpecialDialValueGap * 2;
            mPaint.setTextSize(mSpecialDialValueTextSize);
            mPaint.getTextBounds(TEMP, 0, 2, mTextMeasureBounds);
            final int mSpecialDialValueTextHeight = mTextMeasureBounds.height();
            mSpecialDialValueTextBottom = mTextMeasureBounds.bottom;
            mItemSize += mSpecialDialValueTextHeight * 2;
        }
        final int width = Math.max((int) Math.floor(mItemSize) + paddingStart + paddingEnd,
                suggestedMinimumWidth);
        final int height = Math.max((int) Math.floor(mItemSize) + paddingTop + paddingBottom,
                suggestedMinimumHeight);
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        editCanvas(canvas);
        switch (mProgressMode) {
            default:
            case PROGRESS:
                drawBackground(canvas);
                drawDial(canvas);
                drawProgress(canvas);
                drawProgressValue(canvas);
                drawTopText(canvas);
                drawBottomText(canvas);
                break;
            case LOADING:
                if (mLoadingDrawOther) {
                    drawBackground(canvas);
                    drawDial(canvas);
                }
                drawLoading(canvas);
                if (mLoadingDrawOther) {
                    drawProgressValue(canvas);
                    drawTopText(canvas);
                    drawBottomText(canvas);
                }
                break;
        }
        canvas.restore();
    }

    /**
     * 编辑画布
     *
     * @param canvas 画布
     */
    @SuppressWarnings("all")
    protected void editCanvas(Canvas canvas) {
        float scale = 1;
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int widthSize = getWidth() - paddingStart - paddingEnd;
        final int heightSize = getHeight() - paddingTop - paddingBottom;
        final int minSize = widthSize < heightSize ? widthSize : heightSize;
        switch (mScaleType) {
            default:
            case ST_NONE:
                break;
            case ST_INSIDE:
                if (minSize < mItemSize) {
                    scale = minSize / mItemSize;
                }
                break;
            case ST_CROP:
                if (minSize > mItemSize) {
                    scale = minSize / mItemSize;
                }
                break;
            case ST_INSIDE | ST_CROP:
                scale = minSize / mItemSize;
                break;
        }
        float mBaseX;// 基准点X轴
        float mBaseY;// 基准点Y轴
        switch (mGravity) {
            default:
            case Gravity.CENTER:
                mBaseX = paddingStart + (getMeasuredWidth() - paddingStart - paddingEnd) * 0.5f;
                mBaseY = paddingTop + (getMeasuredHeight() - paddingTop - paddingBottom) * 0.5f;
                break;
            case Compat.START:
            case Gravity.LEFT:
            case Gravity.TOP:
            case Compat.START | Gravity.TOP:
            case Gravity.LEFT | Gravity.TOP:
                mBaseX = paddingStart + mItemSize * 0.5f * scale;
                mBaseY = paddingTop + mItemSize * 0.5f * scale;
                break;
            case Gravity.CENTER_HORIZONTAL:
            case Gravity.CENTER_HORIZONTAL | Gravity.TOP:
                mBaseX = paddingStart + (getMeasuredWidth() - paddingStart - paddingEnd) * 0.5f;
                mBaseY = paddingTop + mItemSize * 0.5f * scale;
                break;
            case Compat.END:
            case Gravity.RIGHT:
            case Compat.END | Gravity.TOP:
            case Gravity.RIGHT | Gravity.TOP:
                mBaseX = getMeasuredWidth() - paddingEnd - mItemSize * 0.5f * scale;
                mBaseY = paddingTop + mItemSize * 0.5f * scale;
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | Compat.START:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                mBaseX = paddingStart + mItemSize * 0.5f * scale;
                mBaseY = paddingTop + (getMeasuredHeight() - paddingTop - paddingBottom) * 0.5f;
                break;
            case Gravity.CENTER_VERTICAL | Compat.END:
            case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                mBaseX = getMeasuredWidth() - paddingEnd - mItemSize * 0.5f * scale;
                mBaseY = paddingTop + (getMeasuredHeight() - paddingTop - paddingBottom) * 0.5f;
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Compat.START:
            case Gravity.BOTTOM | Gravity.LEFT:
                mBaseX = paddingStart + mItemSize * 0.5f * scale;
                mBaseY = getMeasuredHeight() - paddingBottom - mItemSize * 0.5f * scale;
                break;
            case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                mBaseX = paddingStart + (getMeasuredWidth() - paddingStart - paddingEnd) * 0.5f;
                mBaseY = getMeasuredHeight() - paddingBottom - mItemSize * 0.5f * scale;
                break;
            case Gravity.BOTTOM | Compat.END:
            case Gravity.BOTTOM | Gravity.RIGHT:
                mBaseX = getMeasuredWidth() - paddingEnd - mItemSize * 0.5f * scale;
                mBaseY = getMeasuredHeight() - paddingBottom - mItemSize * 0.5f * scale;
                break;
        }
        canvas.setDrawFilter(mDrawFilter);
        canvas.translate(mBaseX, mBaseY);
        if (scale != 1) {
            canvas.scale(scale, scale);
        }
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
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mPaint);
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
                            -mRadius - mDialGap - mDialSpecialHeight - mSpecialDialValueGap - mSpecialDialValueTextBottom,
                            mPaint);
                    canvas.rotate(-90);
                    mPaint.setStyle(Paint.Style.STROKE);

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
        if (mCalculator != null)
            return mCalculator.getDialValue(this, dialPosition, dialCount, max);
        return Integer.toString(dialPosition * (max / dialCount));
    }

    /**
     * 绘制进度
     *
     * @param canvas 画布
     */
    protected void drawProgress(Canvas canvas) {
        if (mRadius == 0 || mProgressSize == 0 || mAnimatorProgress < 0)
            return;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mProgressSize);
        final float halfCircleSize = mProgressSize * 0.5f;
        mRectF.set(-mRadius + halfCircleSize, -mRadius + halfCircleSize,
                mRadius - halfCircleSize, mRadius - halfCircleSize);
        SweepGradient sweepGradient = new SweepGradient(0, 0, mGradientColors, mGradientPositions);
        mRotateMatrix.setRotate(mStartAngle, 0, 0);
        sweepGradient.setLocalMatrix(mRotateMatrix);
        mPaint.setShader(sweepGradient);
        canvas.save();
        canvas.drawArc(mRectF, mStartAngle, mAnimatorProgressAngle, false, mPaint);
        canvas.restore();
    }

    /**
     * 绘制进度值
     *
     * @param canvas 画布
     */
    protected void drawProgressValue(Canvas canvas) {
        if (!mShowProgressValue)
            return;
        String value = getProgressValue(mAnimatorProgress);
        if (value == null)
            return;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        mPaint.setTextSize(mProgressValueTextSize);
        mPaint.setColor(mProgressValueTextColor);
        canvas.save();
        canvas.drawText(value, 0, mProgressValueTextHeight * 0.5f - mProgressValueTextBottom,
                mPaint);
        canvas.restore();
    }

    /**
     * 获取进度值
     *
     * @param progress 进度值
     * @return 进度值
     */
    protected String getProgressValue(int progress) {
        if (mCalculator != null)
            return mCalculator.getProgressValue(this, progress, mProgressMode, mLoadingText,
                    mProgress);
        if (mProgressMode == ProgressMode.LOADING)
            return mLoadingText;
        return Integer.toString(progress);
    }

    /**
     * 绘制进度值顶部文字
     *
     * @param canvas 画布
     */
    protected void drawTopText(Canvas canvas) {
        if (mTopText == null)
            return;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        mPaint.setTextSize(mTopTextSize);
        mPaint.setColor(mTopTextColor);
        mPaint.getTextBounds(mTopText, 0, mTopText.length(), mTextMeasureBounds);
        canvas.save();
        canvas.drawText(mTopText, 0, -mProgressValueTextHeight * 0.5f - mTopTextGap
                        - mTextMeasureBounds.bottom,
                mPaint);
        canvas.restore();
    }

    /**
     * 绘制进度值底部文字
     *
     * @param canvas 画布
     */
    protected void drawBottomText(Canvas canvas) {
        if (mBottomText == null)
            return;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        mPaint.setTextSize(mBottomTextSize);
        mPaint.setColor(mBottomTextColor);
        mPaint.getTextBounds(mBottomText, 0, mBottomText.length(), mTextMeasureBounds);
        canvas.save();
        canvas.drawText(mBottomText, 0, mProgressValueTextHeight * 0.5f + mBottomTextGap
                + mTextMeasureBounds.height() - mTextMeasureBounds.bottom, mPaint);
        canvas.restore();
    }


    /**
     * 创建进度动画
     *
     * @param start 动画起始进度
     * @param end   动画结束进度
     */
    private void makeProgressAnimation(int start, int end) {
        if (mProgressMode == ProgressMode.LOADING) {
            mProgress = end;
            mProgressAngle = mSweepAngle * ((float) mProgress / mMax);
            mAnimatorProgress = mProgress;
            mAnimatorProgressAngle = mProgressAngle;
            return;
        }
        if (mProgressAnimator != null && mProgressAnimator.isRunning())
            mProgressAnimator.end();
        mProgressAnimator = ValueAnimator.ofInt(start, end);
        mProgressAnimator.setDuration(mProgressDuration);
        if (mProgressInterpolator != null)
            mProgressAnimator.setInterpolator(mProgressInterpolator);
        mProgressAnimator.addUpdateListener(mAnimatorListener);
        mProgressAnimator.start();
    }

    /**
     * 绘制载入
     *
     * @param canvas 画布
     */
    protected void drawLoading(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mProgressSize);
        final float halfCircleSize = mProgressSize * 0.5f;
        SweepGradient sweepGradient = new SweepGradient(0, 0, mGradientColors, mGradientPositions);
        mRotateMatrix.setRotate(mStartAngle - mLoadingStartAngle -
                mLoadingSweepAngle * mLoadingOffset, 0, 0);
        sweepGradient.setLocalMatrix(mRotateMatrix);
        mPaint.setShader(sweepGradient);
        canvas.save();
        canvas.rotate(mLoadingStartAngle);
        canvas.rotate(mLoadingSweepAngle * mLoadingOffset);
        canvas.drawPoint(mRadius - halfCircleSize, 0, mPaint);
        canvas.restore();
    }

    /**
     * 设置排版方式
     *
     * @param gravity 排版方式
     */
    public void setGravity(int gravity) {
        if (mGravity == gravity)
            return;
        mGravity = gravity;
        requestLayout();
        invalidate();
    }

    /**
     * 获取排版方式
     *
     * @return 排版方式
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置环形半径
     *
     * @param radius 环形半径
     */
    public void setRadius(float radius) {
        if (mRadius == radius)
            return;
        mRadius = radius;
        requestLayout();
        invalidate();
    }

    /**
     * 获取环形半径
     *
     * @return 环形半径
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * 设置起始角度
     *
     * @param angle 角度
     */
    public void setStartAngle(int angle) {
        if (mStartAngle == angle)
            return;
        mStartAngle = angle;
        invalidate();
    }

    /**
     * 获取起始角度
     *
     * @return 起始角度
     */
    public int getStartAngle() {
        return mStartAngle;
    }

    /**
     * 设置扫描角度
     *
     * @param angle 角度
     */
    public void setSweepAngle(int angle) {
        if (mSweepAngle == angle)
            return;
        mSweepAngle = angle;
        mDialCount = mDialAngle <= 0 ? 0 : mSweepAngle / mDialAngle;
        invalidate();
    }

    /**
     * 获取扫描角度
     *
     * @return 扫描角度
     */
    public int getSweepAngle() {
        return mSweepAngle;
    }

    /**
     * 设置背景尺寸
     *
     * @param size 尺寸
     */
    public void setBackgroundSize(float size) {
        if (mBackgroundSize == size)
            return;
        mBackgroundSize = size;
        invalidate();
    }

    /**
     * 获取背景尺寸
     *
     * @return 背景尺寸
     */
    public float getBackgroundSize() {
        return mBackgroundSize;
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景颜色
     */
    public void setBackgroundColor(int color) {
        if (mBackgroundColor == color)
            return;
        mBackgroundColor = color;
        invalidate();
    }

    /**
     * 获取背景颜色
     *
     * @return 背景颜色
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * 设置环形尺寸
     *
     * @param size 尺寸
     */
    public void setProgressSize(float size) {
        if (mProgressSize == size)
            return;
        mProgressSize = size;
        requestLayout();
        invalidate();
    }

    /**
     * 获取环形尺寸
     *
     * @return 环形尺寸
     */
    public float getProgressSize() {
        return mProgressSize;
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        if (mMax == max)
            return;
        mMax = max;
        if (mProgress > mMax) {
            mProgress = mMax;
            mProgressAngle = mSweepAngle * ((float) mProgress / mMax);
            if (mProgressAnimator == null || !mProgressAnimator.isRunning()) {
                mAnimatorProgress = mProgress;
                mAnimatorProgressAngle = mProgressAngle;
            }
        }
        invalidate();
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    public int getMax() {
        return mMax;
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (mProgress == progress)
            return;
        if (mProgressAnimator != null && mProgressAnimator.isRunning())
            mProgressAnimator.end();
        mProgress = progress > mMax ? mMax : progress;
        mProgressAngle = mSweepAngle * ((float) mProgress / mMax);
        mAnimatorProgress = mProgress;
        mAnimatorProgressAngle = mProgressAngle;
        invalidate();
    }

    /**
     * 获取进度
     *
     * @return 进度
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 动画到某一进度
     *
     * @param progress 结束进度
     */
    public void animationToProgress(int progress) {
        animationToProgress(mProgress, progress);
    }

    /**
     * 动画到某一进度
     *
     * @param start    开始进度
     * @param progress 结束进度
     */
    public void animationToProgress(int start, int progress) {
        if (mProgress == progress)
            return;
        mProgress = progress > mMax ? mMax : progress;
        mProgressAngle = mSweepAngle * ((float) mProgress / mMax);
        makeProgressAnimation(start, progress > mMax ? mMax : progress);
    }

    /**
     * 设置渐变色
     *
     * @param colors 渐变色
     */
    public void setGradientColors(int... colors) {
        if (mGradientColors == colors)
            return;
        mGradientColors = colors;
        invalidate();
    }

    /**
     * 设置渐变点
     *
     * @param positions 渐变点
     */
    public void setGradientPositions(float... positions) {
        if (mGradientPositions == positions)
            return;
        mGradientPositions = positions;
        invalidate();
    }

    /**
     * 设置刻度间隔
     *
     * @param gap 间隔
     */
    public void setDialGap(float gap) {
        if (mDialGap == gap)
            return;
        mDialGap = gap;
        requestLayout();
        invalidate();
    }

    /**
     * 获取刻度间隔
     *
     * @return 刻度间隔
     */
    public float getDialGap() {
        return mDialGap;
    }

    /**
     * 设置刻度角度
     *
     * @param angle 角度
     */
    public void setDialAngle(int angle) {
        if (mDialAngle == angle)
            return;
        mDialAngle = angle;
        mDialCount = mDialAngle <= 0 ? 0 : mSweepAngle / mDialAngle;
        requestLayout();
        invalidate();
    }

    /**
     * 获取刻度角度
     *
     * @return 刻度角度
     */
    public int getDialAngle() {
        return mDialAngle;
    }

    /**
     * 设置刻度高
     *
     * @param height 高
     */
    public void setDialHeight(float height) {
        if (mDialHeight == height)
            return;
        mDialHeight = height;
        requestLayout();
        invalidate();
    }

    /**
     * 获取刻度高
     *
     * @return 刻度高
     */
    public float getDialHeight() {
        return mDialHeight;
    }

    /**
     * 设置刻度宽
     *
     * @param width 宽
     */
    public void setDialWidth(float width) {
        if (mDialWidth == width)
            return;
        mDialWidth = width;
        requestLayout();
        invalidate();
    }

    /**
     * 获取刻度宽
     *
     * @return 刻度宽
     */
    public float getDialWidth() {
        return mDialWidth;
    }

    /**
     * 设置刻度颜色
     *
     * @param color 颜色
     */
    public void setDialColor(int color) {
        if (mDialColor == color)
            return;
        mDialColor = color;
        invalidate();
    }

    /**
     * 获取刻度颜色
     *
     * @return 刻度颜色
     */
    public int getDialColor() {
        return mDialColor;
    }

    /**
     * 设置特殊刻度之间间隔
     * 单位以普通刻度为参考，设置为2时，第1个普通刻度、第3个普通刻度等每隔一个普通刻度都变为特殊刻度
     *
     * @param unit 刻度
     */
    public void setDialSpecialUnit(int unit) {
        if (mDialSpecialUnit == unit)
            return;
        mDialSpecialUnit = unit;
        invalidate();
    }

    /**
     * 获取特殊刻度之间间隔
     *
     * @return 特殊刻度之间间隔
     */
    public int getDialSpecialUnit() {
        return mDialSpecialUnit;
    }

    /**
     * 设置特殊刻度的高
     *
     * @param height 高
     */
    public void setDialSpecialHeight(float height) {
        if (mDialSpecialHeight == height)
            return;
        mDialSpecialHeight = height;
        requestLayout();
        invalidate();
    }

    /**
     * 获取特殊刻度的高
     *
     * @return 特殊刻度的高
     */
    public float getDialSpecialHeight() {
        return mDialSpecialHeight;
    }

    /**
     * 设置特殊刻度的宽
     *
     * @param width 宽
     */
    public void setDialSpecialWidth(float width) {
        if (mDialSpecialWidth == width)
            return;
        mDialSpecialWidth = width;
        invalidate();
    }

    /**
     * 获取特殊刻度的宽
     *
     * @return 特殊刻度的宽
     */
    public float getDialSpecialWidth() {
        return mDialSpecialWidth;
    }

    /**
     * 设置特殊刻度颜色
     *
     * @param color 颜色
     */
    public void setDialSpecialColor(int color) {
        if (mDialSpecialColor == color)
            return;
        mDialSpecialColor = color;
        invalidate();
    }

    /**
     * 获取特殊刻度颜色
     *
     * @return 特殊刻度颜色
     */
    public int getDialSpecialColor() {
        return mDialSpecialColor;
    }

    /**
     * 设置长短刻度的对齐方式
     *
     * @param gravity 对齐方式
     */
    public void setDialGravity(int gravity) {
        if (mDialGravity == gravity)
            return;
        mDialGravity = gravity;
        invalidate();
    }

    /**
     * 获取长短刻度的对齐方式
     *
     * @return 长短刻度的对齐方式
     */
    public int getDialGravity() {
        return mDialGravity;
    }

    /**
     * 设置是否显示特殊刻度值
     *
     * @param show 是否显示
     */
    public void setShowSpecialDialValue(boolean show) {
        if (mShowSpecialDialValue == show)
            return;
        mShowSpecialDialValue = show;
        requestLayout();
        invalidate();
    }

    /**
     * 判断是否显示特殊刻度值
     *
     * @return 是否显示特殊刻度值
     */
    public boolean isShowSpecialDialValue() {
        return mShowSpecialDialValue;
    }

    /**
     * 设置特殊刻度值间距
     *
     * @param gap 间距
     */
    public void setSpecialDialValueGap(float gap) {
        if (mSpecialDialValueGap == gap)
            return;
        mSpecialDialValueGap = gap;
        requestLayout();
        invalidate();
    }

    /**
     * 获取特殊刻度值间距
     *
     * @return 特殊刻度值间距
     */
    public float getSpecialDialValueGap() {
        return mSpecialDialValueGap;
    }

    /**
     * 设置特殊刻度值文字大小
     *
     * @param textSize 文字大小
     */
    public void setSpecialDialValueTextSize(float textSize) {
        if (mSpecialDialValueTextSize == textSize)
            return;
        mSpecialDialValueTextSize = textSize;
        requestLayout();
        invalidate();
    }

    /**
     * 获取特殊刻度值文字大小
     *
     * @return 特殊刻度值文字大小
     */
    public float getSpecialDialValueTextSize() {
        return mSpecialDialValueTextSize;
    }

    /**
     * 设置特殊刻度值文字颜色
     *
     * @param color 文字颜色
     */
    public void setSpecialDialValueTextColor(int color) {
        if (mSpecialDialValueTextColor == color)
            return;
        mSpecialDialValueTextColor = color;
        invalidate();
    }

    /**
     * 获取特殊刻度值文字颜色
     *
     * @return 特殊刻度值文字颜色
     */
    public int getSpecialDialValueTextColor() {
        return mSpecialDialValueTextColor;
    }

    /**
     * 设置是否显示进度值
     *
     * @param show 是否显示
     */
    public void setShowProgressValue(boolean show) {
        if (mShowProgressValue == show)
            return;
        mShowProgressValue = show;
        String value = getProgressValue(mProgress);
        if (value == null)
            return;
        if (mShowProgressValue) {
            mPaint.setTextSize(mProgressValueTextSize);
            mPaint.getTextBounds(value, 0, value.length(), mTextMeasureBounds);
            mProgressValueTextHeight = mTextMeasureBounds.height();
            mProgressValueTextBottom = mTextMeasureBounds.bottom;
        }
        invalidate();
    }

    /**
     * 判断是否显示进度值
     *
     * @return 是否显示进度值
     */
    public boolean isShowProgressValue() {
        return mShowProgressValue;
    }

    /**
     * 设置进度值文字
     *
     * @param textSize 进度值文字
     */
    public void setProgressValueTextSize(float textSize) {
        if (mProgressValueTextSize == textSize)
            return;
        mProgressValueTextSize = textSize;
        String value = getProgressValue(mProgress);
        if (value == null) {
            value = "88";
        }
        mPaint.setTextSize(mProgressValueTextSize);
        mPaint.getTextBounds(value, 0, value.length(), mTextMeasureBounds);
        mProgressValueTextHeight = mTextMeasureBounds.height();
        mProgressValueTextBottom = mTextMeasureBounds.bottom;
        invalidate();
    }

    /**
     * 获取进度值文字
     *
     * @return 进度值文字
     */
    public float getProgressValueTextSize() {
        return mProgressValueTextSize;
    }

    /**
     * 设置进度值文字颜色
     *
     * @param color 文字颜色
     */
    public void setProgressValueTextColor(int color) {
        if (mProgressValueTextColor == color)
            return;
        mProgressValueTextColor = color;
        invalidate();
    }

    /**
     * 获取进度值文字颜色
     *
     * @return 进度值文字颜色
     */
    public int getProgressValueTextColor() {
        return mProgressValueTextColor;
    }

    /**
     * 设置进度值顶部文字
     *
     * @param text 文字
     */
    public void setTopText(String text) {
        if (mTopText != null && mTopText.equals(text))
            return;
        mTopText = text;
        invalidate();
    }

    /**
     * 获取进度值顶部文字
     *
     * @return 进度值顶部文字
     */
    public String getTopText() {
        return mTopText;
    }

    /**
     * 设置进度值顶部文字间隔
     *
     * @param gap 间隔
     */
    public void setTopTextGap(float gap) {
        if (mTopTextGap == gap)
            return;
        mTopTextGap = gap;
        invalidate();
    }

    /**
     * 获取进度值顶部文字间隔
     *
     * @return 进度值顶部文字间隔
     */
    public float getTopTextGap() {
        return mTopTextGap;
    }

    /**
     * 设置进度值顶部文字大小
     *
     * @param textSize 大小
     */
    public void setTopTextSize(float textSize) {
        if (mTopTextSize == textSize)
            return;
        mTopTextSize = textSize;
        invalidate();
    }

    /**
     * 获取进度值顶部文字大小
     *
     * @return 进度值顶部文字大小
     */
    public float getTopTextSize() {
        return mTopTextSize;
    }

    /**
     * 设置进度值顶部文字颜色
     *
     * @param color 文字颜色
     */
    public void setTopTextColor(int color) {
        if (mTopTextColor == color)
            return;
        mTopTextColor = color;
        invalidate();
    }

    /**
     * 获取进度值顶部文字颜色
     *
     * @return 进度值顶部文字颜色
     */
    public int getTopTextColor() {
        return mTopTextColor;
    }

    /**
     * 设置进度值底部文字
     *
     * @param text 文字
     */
    public void setBottomText(String text) {
        if (mBottomText != null && mBottomText.equals(text))
            return;
        mBottomText = text;
        invalidate();
    }

    /**
     * 获取进度值底部文字
     *
     * @return 进度值底部文字
     */
    public String getBottomText() {
        return mBottomText;
    }

    /**
     * 设置进度值底部文字间隔
     *
     * @param gap 间隔
     */
    public void setBottomTextGap(float gap) {
        if (mBottomTextGap == gap)
            return;
        mBottomTextGap = gap;
        invalidate();
    }

    /**
     * 获取进度值底部文字间隔
     *
     * @return 进度值底部文字间隔
     */
    public float getBottomTextGap() {
        return mBottomTextGap;
    }

    /**
     * 设置进度值底部文字大小
     *
     * @param textSize 大小
     */
    public void setBottomTextSize(float textSize) {
        if (mBottomTextSize == textSize)
            return;
        mBottomTextSize = textSize;
        invalidate();
    }

    /**
     * 获取进度值底部文字大小
     *
     * @return 进度值底部文字大小
     */
    public float getBottomTextSize() {
        return mBottomTextSize;
    }

    /**
     * 设置进度值底部文字颜色
     *
     * @param color 文字颜色
     */
    public void setBottomTextColor(int color) {
        if (mBottomTextColor == color)
            return;
        mBottomTextColor = color;
        invalidate();
    }

    /**
     * 获取进度值底部文字颜色
     *
     * @return 进度值底部文字颜色
     */
    public int getBottomTextColor() {
        return mBottomTextColor;
    }

    /**
     * 设置缩放类型
     *
     * @param scaleType 缩放类型
     */
    public void setScaleType(int scaleType) {
        if (mScaleType == scaleType)
            return;
        mScaleType = scaleType;
        invalidate();
    }

    /**
     * 获取缩放类型
     *
     * @return 缩放类型
     */
    public int getScaleType() {
        return mScaleType;
    }

    /**
     * 设置进度动画时长
     * 下次动画才有效
     *
     * @param duration 动画时长
     */
    public void setProgressDuration(long duration) {
        mProgressDuration = duration;
    }

    /**
     * 获取进度动画时长
     *
     * @return 进度动画时长
     */
    public long getProgressDuration() {
        return mProgressDuration;
    }

    /**
     * 设置进度动画补帧器
     * 下次动画才有效
     *
     * @param interpolator 动画补帧器
     */
    public void setProgressInterpolator(TimeInterpolator interpolator) {
        mProgressInterpolator = interpolator;
    }

    /**
     * 获取进度动画补帧器
     *
     * @return 进度动画补帧器
     */
    public TimeInterpolator getProgressInterpolator() {
        return mProgressInterpolator;
    }

    /**
     * 设置进度模式
     *
     * @param mode 模式
     */
    public void setProgressMode(ProgressMode mode) {
        if (mode == null) {
            throw new NullPointerException();
        }
        if (mProgressMode == mode)
            return;
        mProgressMode = mode;
        if (mProgressMode == ProgressMode.LOADING) {
            if (mProgressAnimator != null && mProgressAnimator.isRunning())
                mProgressAnimator.end();
            mLoadingAnimator.start();
        }
        if (mProgressMode == ProgressMode.PROGRESS)
            mLoadingAnimator.end();
        invalidate();
    }

    /**
     * 获取进度模式
     *
     * @return 进度模式
     */
    public ProgressMode getProgressMode() {
        return mProgressMode;
    }

    /**
     * 设置字体样式
     *
     * @param tf 字体样式
     */
    public void setTypeface(Typeface tf) {
        mPaint.setTypeface(tf);
        requestLayout();
        invalidate();
    }

    /**
     * 设置载入起始角度
     *
     * @param angle 角度
     */
    public void setLoadingStartAngle(int angle) {
        mLoadingStartAngle = angle;
    }

    /**
     * 获取载入起始角度
     *
     * @return 载入起始角度
     */
    public int getLoadingStartAngle() {
        return mLoadingStartAngle;
    }

    /**
     * 设置载入扫描角度
     *
     * @param angle 角度
     */
    public void setLoadingSweepAngle(int angle) {
        mLoadingSweepAngle = angle;
    }

    /**
     * 获取载入扫描角度
     *
     * @return 载入扫描角度
     */
    public int getLoadingSweepAngle() {
        return mLoadingSweepAngle;
    }

    /**
     * 设置载入动画循环时长
     *
     * @param duration 时长
     */
    public void setLoadingDuration(long duration) {
        mLoadingAnimator.setDuration(duration);
    }

    /**
     * 获取载入动画循环时长
     *
     * @return 载入动画循环时长
     */
    public long getLoadingDuration() {
        return mLoadingAnimator.getDuration();
    }

    /**
     * 设置载入动画补帧器
     *
     * @param interpolator 补帧器
     */
    public void setLoadingInterpolator(TimeInterpolator interpolator) {
        mLoadingAnimator.setInterpolator(interpolator);
    }

    /**
     * 获取载入动画补帧器
     *
     * @return 载入动画补帧器
     */
    public TimeInterpolator getLoadingInterpolator() {
        return mLoadingAnimator.getInterpolator();
    }

    /**
     * 设置载入动画循环模式
     *
     * @param mode 模式
     */
    public void setLoadingRepeatMode(int mode) {
        if (mode != ValueAnimator.RESTART && mode != ValueAnimator.REVERSE)
            return;
        mLoadingAnimator.setRepeatMode(mode);
    }

    public int getLoadingRepeatMode() {
        return mLoadingAnimator.getRepeatMode();
    }

    /**
     * 载入模式下是否绘制其他元素
     *
     * @param draw 是否绘制
     */
    public void setLoadingDrawOther(boolean draw) {
        if (mLoadingDrawOther == draw)
            return;
        mLoadingDrawOther = draw;
        invalidate();
    }

    /**
     * 判断载入模式下是否绘制其他元素
     *
     * @return 载入模式下是否绘制其他元素
     */
    public boolean isLoadingDrawOther() {
        return mLoadingDrawOther;
    }

    /**
     * 设置载入时进度文字
     *
     * @param text 文字
     */
    public void setLoadingText(String text) {
        if (mLoadingText != null && mLoadingText.equals(text))
            return;
        mLoadingText = text;
        invalidate();
    }

    /**
     * 获取载入时进度文字
     *
     * @return 载入时进度文字
     */
    public String getLoadingText() {
        return mLoadingText;
    }

    /**
     * 设置自定义刻度与进度输出计算器
     *
     * @param calculator 输出计算器
     */
    public void setCalculator(Calculator calculator) {
        if (mCalculator == calculator)
            return;
        mCalculator = calculator;
        invalidate();
    }
}
