package am.project.x.widgets.circleprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import am.project.x.widgets.animators.BaseNextAnimator;


public class CircleProgressBar extends View {

	private static final int MAX_WIDTH = 300;
	private static final int PROGRESS_STROKEWIDTH = 10;
	private static final int COLOR_BACKCIRCLE = 0xfff4f3f1;
	private static final int COLOR_PROGRESS = 0xffd3463c;
	private static final int COLOR_TEXTBLACK = 0xde000000;
	private static final int TEXTSIZE_CENTER = 95;
	private static final int TEXTSIZE_TITLE = 16;
	private static final int COLOR_TEXTDARK = 0x47000000;
	private static final int GAP_TARGET = 9;
	private static final int ANGLE_START = -90;
	private static final int ANGLE_END = 270;
	private static final float ANGLE_MIN = 0.05f;
	private static final float ANGLE_MAX = 360f;
	private static final int[] ATTRS = new int[] { android.R.attr.drawablePadding };
	private int drawablePadding = 0;
	private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	private final RectF mRectF = new RectF();
	private float mScalePre = 1;// 缩放比例
	private boolean considerBackgroundArea = false;
	private final int maxWidth;
	private final float mProgressStrokeWidth;
	private final float mProgressRadio;
	private int mBackCircle;
	private int mProgressCircle;
	private final float mTextSizeCenter;
	private int mTextProgressColor;
	private int mTextCenterHeight;
	private int mTextCenterDesc;
	private int mProgress;
	private Typeface mNumberTypeface = Typeface.MONOSPACE;
	private final float mTextSizeTitle;
	private int mTextTitleColor;
	private int mTextTitleHeight;
	private int mTextTitleDesc;
	private String mTitle;
	private String mTargetString;
	private Drawable mTargetDrawable;
	private int mMax = 100;
	private float mGapTarget;
	private float mStartAngle;
	private float mCurrectSweepAngle;
	private float mStartSweepAngle;
	private float mAnimateSweepAngle;
	private final StartAnimator mStartAnimation;
	private final Interpolator mStartInterpolator;
	private boolean autoStart = true;
	private boolean isStartAnimatorEnd = false;
	private boolean isSetup = false;
	private final ProgressAnimator mProgressAnimator;
	private final Interpolator mProgressInterpolator;

	public CircleProgressBar(Context context) {
		this(context, null);
	}

	public CircleProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		setDrawablePadding(a.getDimensionPixelSize(0, 0));
		a.recycle();
		final float density = getResources().getDisplayMetrics().density;
		IMPL.setDensity(mTextPaint, density);
		maxWidth = (int) (MAX_WIDTH * density);
		mProgressStrokeWidth = PROGRESS_STROKEWIDTH * density;
		mProgressRadio = (maxWidth - mProgressStrokeWidth - mProgressStrokeWidth) * 0.5f;
		mBackCircle = COLOR_BACKCIRCLE;
		mProgressCircle = COLOR_PROGRESS;
		mTextSizeCenter = TEXTSIZE_CENTER * density;
		mTextProgressColor = COLOR_TEXTBLACK;
		mTextSizeTitle = TEXTSIZE_TITLE * density;
		mTextTitleColor = COLOR_TEXTDARK;
		mGapTarget = GAP_TARGET * density;
		mStartAngle = ANGLE_START;
		calculate();
		mStartInterpolator = new AccelerateInterpolator();
		mStartAnimation = new StartAnimator(this, 600, mStartInterpolator);
		mProgressInterpolator = new OvershootInterpolator();
		mProgressAnimator = new ProgressAnimator(this, 800,
				mProgressInterpolator);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int needWidth = maxWidth;
		float widthScale = 1f;
		int height;
		int needHeight = maxWidth;
		float heightScale = 1f;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize - ViewCompat.getPaddingStart(this)
					- ViewCompat.getPaddingEnd(this);
			if (width < needWidth) {
				widthScale = (float) width / (float) needWidth;
			}
		} else {
			width = needWidth;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize - getPaddingTop() - getPaddingBottom();
			if (height < needHeight) {
				heightScale = (float) height / (float) needHeight;
			}
		} else {
			height = needHeight;
		}
		mScalePre = Math.min(widthScale, heightScale);
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = (int) (needWidth * mScalePre)
					+ ViewCompat.getPaddingStart(this)
					+ ViewCompat.getPaddingEnd(this);
			if (considerBackgroundArea) {
				width = Math.max(width, getMinWidth());
			}
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = (int) (needHeight * mScalePre) + getPaddingTop()
					+ getPaddingBottom();
			if (considerBackgroundArea) {
				height = Math.max(height, getMinHeight());
			}
		}
		setMeasuredDimension(width, height);
		getTextHeight();
	}

	/**
	 * 获取文字高度
	 */
	private void getTextHeight() {
		mTextPaint.setTextSize(mTextSizeCenter);
		FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
		mTextCenterHeight = metrics.bottom - metrics.top;
		mTextCenterDesc = mTextCenterHeight + metrics.top;
		mTextPaint.setTextSize(mTextSizeTitle);
		metrics = mTextPaint.getFontMetricsInt();
		mTextTitleHeight = metrics.bottom - metrics.top;
		mTextTitleDesc = mTextTitleHeight + metrics.top;
	}

	/**
	 * 根据背景图获取最小宽度
	 * 
	 * @return
	 */
	private int getMinWidth() {
		int minWidth = 0;
		final Drawable bg = getBackground();
		if (bg != null) {
			minWidth = bg.getIntrinsicWidth();
		}
		return minWidth;
	}

	/**
	 * 根据背景图获取最小高度
	 * 
	 * @return
	 */
	private int getMinHeight() {
		int minHeight = 0;
		final Drawable bg = getBackground();
		if (bg != null) {
			minHeight = bg.getIntrinsicHeight();
		}
		return minHeight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final int width = getWidth();
		final int height = getHeight();
		canvas.save();
		canvas.translate(width * 0.5f, height * 0.5f);
		canvas.scale(mScalePre, mScalePre);
		drawCircle(canvas);
		drawCenter(canvas);
		drawTitle(canvas);
		drawTarget(canvas);
		canvas.restore();
		if (autoStart) {
			startStartAnimation();
		} else {
			mStartAnimation.next();
			mProgressAnimator.next();
		}
	}

	/**
	 * 绘制目标
	 * 
	 * @param canvas
	 */
	private void drawTarget(Canvas canvas) {
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(mTextSizeTitle);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setTypeface(mNumberTypeface);
		mTextPaint.setFakeBoldText(true);
		final float widthMax = mTextPaint.measureText(Integer.toString(mMax));
		mTextPaint.setTypeface(Typeface.MONOSPACE);
		mTextPaint.setFakeBoldText(false);
		final float widthTarget = mTargetString == null ? 0 : mTextPaint
				.measureText(mTargetString);
		final float widthDrawable = mTargetDrawable == null ? 0
				: mTargetDrawable.getIntrinsicWidth();
		float width = 0;
		if (mTargetDrawable != null) {
			width += widthDrawable + drawablePadding;
		}
		if (mTargetString != null) {
			width += widthTarget + mGapTarget;
		}
		width += widthMax;
		final float radio = width * 0.5f;
		if (mTargetDrawable != null) {
			final int left = (int) -radio;
			final int top = (int) ((mProgressRadio - mTargetDrawable
					.getIntrinsicHeight()) * 0.5f);
			mTargetDrawable.setBounds(left, top,
					left + mTargetDrawable.getIntrinsicWidth(), top
							+ mTargetDrawable.getIntrinsicHeight());
			mTargetDrawable.draw(canvas);
		}
		final float offsetTarget = mTargetDrawable == null ? -radio : -radio
				+ widthDrawable + drawablePadding;
		if (mTargetString != null) {
			mTextPaint.setColor(mTextTitleColor);
			canvas.drawText(mTargetString, offsetTarget, mTextTitleHeight
					- mTextTitleDesc - mTextTitleHeight * 0.5f + mProgressRadio
					* 0.5f, mTextPaint);
		}
		mTextPaint.setColor(mTextProgressColor);
		mTextPaint.setTypeface(mNumberTypeface);
		mTextPaint.setFakeBoldText(true);
		final float offsetMax = mTargetString == null ? offsetTarget
				: offsetTarget + widthTarget + mGapTarget;
		canvas.drawText(Integer.toString(mMax), offsetMax, mTextTitleHeight
				- mTextTitleDesc - mTextTitleHeight * 0.5f + mProgressRadio
				* 0.5f, mTextPaint);
	}

	/**
	 * 绘制标题
	 * 
	 * @param canvas
	 */
	private void drawTitle(Canvas canvas) {
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(mTextSizeTitle);
		mTextPaint.setColor(mTextTitleColor);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTypeface(Typeface.MONOSPACE);
		mTextPaint.setFakeBoldText(false);
		if (mTitle != null) {
			canvas.drawText(mTitle, 0, mTextTitleHeight - mTextTitleDesc
					- mTextTitleHeight * 0.5f - mProgressRadio * 0.5f,
					mTextPaint);
		}

	}

	/**
	 * 绘制中间进度文字
	 * 
	 * @param canvas
	 */
	private void drawCenter(Canvas canvas) {
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(mTextSizeCenter);
		mTextPaint.setColor(mTextProgressColor);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTypeface(mNumberTypeface);
		mTextPaint.setFakeBoldText(true);
		canvas.drawText(Integer.toString(mProgress), 0, mTextCenterHeight
				- mTextCenterDesc - mTextCenterHeight * 0.5f, mTextPaint);
	}

	/**
	 * 圆盘及进度条
	 * 
	 * @param canvas
	 */
	private void drawCircle(Canvas canvas) {
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setStrokeJoin(Paint.Join.ROUND);
		mTextPaint.setStrokeCap(Paint.Cap.ROUND);
		mTextPaint.setStrokeWidth(mProgressStrokeWidth);
		mTextPaint.setColor(mBackCircle);
		canvas.drawCircle(0, 0, mProgressRadio, mTextPaint);
		mRectF.set(-mProgressRadio, -mProgressRadio, mProgressRadio,
				mProgressRadio);
		mTextPaint.setColor(mProgressCircle);
		canvas.drawArc(mRectF, mStartAngle, mAnimateSweepAngle, false,
				mTextPaint);
	}

	/**
	 * 计算
	 */
	private void calculate() {
		mCurrectSweepAngle = ((float) mProgress / (float) mMax) * 360f;
		mCurrectSweepAngle = mCurrectSweepAngle < ANGLE_MIN ? ANGLE_MIN
				: mCurrectSweepAngle;
		mCurrectSweepAngle = mCurrectSweepAngle > ANGLE_MAX ? ANGLE_MAX
				: mCurrectSweepAngle;
	}

	/**
	 * 应用开始动画
	 */
	private void applyStartAnimation(float interpolatedTime) {
		mStartAngle = ANGLE_START + (ANGLE_END - ANGLE_START)
				* interpolatedTime;
		mAnimateSweepAngle = ANGLE_MIN;
		invalidate();
	}

	/**
	 * 应用进度动画
	 * 
	 * @param interpolatedTime
	 */
	private void applyProgressAnimation(float interpolatedTime) {
		mStartAngle = ANGLE_END;
		mAnimateSweepAngle = mStartSweepAngle
				+ (mCurrectSweepAngle - mStartSweepAngle) * interpolatedTime;
		invalidate();
	}

	/**
	 * 开始进度动画
	 * 
	 * @param start
	 */
	private void startProgressAnimation(float start) {
		mStartSweepAngle = start;
		mProgressAnimator.start();
	}

	/**
	 * 获取图文间距
	 * 
	 * @return
	 */
	public final int getDrawablePadding() {
		return drawablePadding;
	}

	/**
	 * 设置图文间距
	 * 
	 * @param padding
	 */
	public final void setDrawablePadding(int padding) {
		drawablePadding = padding;
		invalidate();
	}

	/**
	 * 是否考虑背景图尺寸
	 * 
	 * @return
	 */
	public boolean isConsiderBackgroundArea() {
		return considerBackgroundArea;
	}

	/**
	 * 设置是否考虑背景图尺寸
	 * 
	 * @param considerBackgroundArea
	 */
	public void setConsiderBackgroundArea(boolean considerBackgroundArea) {
		this.considerBackgroundArea = considerBackgroundArea;
		requestLayout();
		invalidate();
	}

	/**
	 * 设置进度痕迹颜色
	 * 
	 * @param color
	 */
	public void setBackCircleColor(int color) {
		mBackCircle = color;
		invalidate();
	}

	/**
	 * 设置进度条颜色
	 * 
	 * @param color
	 */
	public void setProgressCircleColor(int color) {
		mProgressCircle = color;
		invalidate();
	}

	/**
	 * 获取进度
	 * 
	 * @return
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * 设置进度
	 * <p>
	 * 默认带动画
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		setProgress(progress, true);
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 * @param withAnimator
	 */
	public void setProgress(int progress, boolean withAnimator) {
		if (progress >= 0) {
			this.mProgress = progress;
			calculate();
			if (mStartAnimation.isRunning() || mProgressAnimator.isRunning()) {
				return;
			}
			if (withAnimator) {
				startProgressAnimation(mAnimateSweepAngle);
			} else {
				mAnimateSweepAngle = mCurrectSweepAngle;
				invalidate();
			}
		}
	}

	/**
	 * 设置进度文字颜色
	 * 
	 * @param color
	 */
	public void setTextProgressColor(int color) {
		mTextProgressColor = color;
	}

	/**
	 * 设置字体样式
	 * 
	 * @param tf
	 */
	public void setTypeface(Typeface tf) {
		if (tf != null && mNumberTypeface != tf) {
			mNumberTypeface = tf;
			requestLayout();
			invalidate();
		}
	}

	/**
	 * 设置标题文字颜色
	 * 
	 * @param mTextTitleColor
	 */
	public void setTextTitleColor(int mTextTitleColor) {
		this.mTextTitleColor = mTextTitleColor;
		invalidate();
	}

	/**
	 * 设置标题
	 * 
	 * @param id
	 */
	public void setTitle(int id) {
		setTitle(getResources().getString(id));
	}

	/**
	 * 设置标题
	 * 
	 * @param str
	 */
	public void setTitle(String str) {
		this.mTitle = str;
		invalidate();
	}

	/**
	 * 设置目标文字
	 * 
	 * @param id
	 */
	public void setTargetString(int id) {
		setTargetString(getResources().getString(id));
	}

	/**
	 * 设置目标文字
	 * 
	 * @param str
	 */
	public void setTargetString(String str) {
		this.mTargetString = str;
		invalidate();
	}

	/**
	 * 获取目标值
	 * 
	 * @return
	 */
	public int getMax() {
		return mMax;
	}

	/**
	 * 设置目标值
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		if (max != 0) {
			this.mMax = max;
			calculate();
			invalidate();
		}
	}

	/**
	 * 设置初值
	 * 
	 * @param progress
	 * @param max
	 */
	public void setup(int progress, int max) {
		if (progress >= 0 && max != 0) {
			isStartAnimatorEnd = false;
			isSetup = true;
			mProgress = progress;
			mMax = max;
			calculate();
			invalidate();
		}
	}
	
	/**
	 * 更新
	 * @param progress
	 * @param max
	 */
	public void update(int progress, int max) {
		if (max != 0) {
			mMax = max;
			calculate();
			setProgress(progress);
		}
	}
	
	public void setupOrUpdate(int progress, int max) {
		if (!isSetup) {
			setup(progress, max);
		} else {
			update(progress, max);
		}
	}

	/**
	 * 开始出现动画
	 */
	public void startStartAnimation() {
		autoStart = false;
		if (!isStartAnimatorEnd) {
			mStartAnimation.start();
		}
	}

	/**
	 * 开始进度动画
	 * <p>
	 * 从头开始
	 */
	public void startProgressAnimation() {
		startProgressAnimation(ANGLE_MIN);
	}

	/**
	 * 设置目标ICON
	 * 
	 * @param id
	 */
	public void setTargetDrawable(int id) {
		setTargetDrawable(ContextCompat.getDrawable(getContext(), id));
	}

	/**
	 * 设置目标ICON
	 * 
	 * @param drawable
	 */
	public void setTargetDrawable(Drawable drawable) {
		this.mTargetDrawable = drawable;
		invalidate();
	}

	/**
	 * 设置目标文字间距
	 * 
	 * @param gap
	 */
	public void setGapTarget(float gap) {
		mGapTarget = gap;
	}

	/**
	 * 是否自动开始动画
	 * 
	 * @return
	 */
	public boolean isAutoStart() {
		return autoStart;
	}

	/**
	 * 设置是否自动开始动画
	 * 
	 * @param auto
	 */
	public void setAutoStart(boolean auto) {
		autoStart = auto;
		invalidate();
	}
	
	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.drawablePadding = drawablePadding;
		ss.mBackCircle = mBackCircle;
		ss.mProgressCircle = mProgressCircle;
		ss.mTextProgressColor = mTextProgressColor;
		ss.mProgress = mProgress;
		ss.mTextTitleColor = mTextTitleColor;
		ss.mMax = mMax;
		ss.mGapTarget = mGapTarget;
		ss.mStartAngle = mStartAngle;
		ss.mCurrectSweepAngle = mCurrectSweepAngle;
		ss.mStartSweepAngle = mStartSweepAngle;
		ss.mAnimateSweepAngle = mAnimateSweepAngle;
		ss.mTitle = mTitle;
		ss.mTargetString = mTargetString;
		ss.considerBackgroundArea = considerBackgroundArea;
		ss.autoStart = autoStart;
		ss.isStartAnimatorEnd = isStartAnimatorEnd;
		ss.isSetup = isSetup;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		drawablePadding = ss.drawablePadding;
		mBackCircle = ss.mBackCircle;
		mProgressCircle = ss.mProgressCircle;
		mTextProgressColor = ss.mTextProgressColor;
		mProgress = ss.mProgress;
		mTextTitleColor = ss.mTextTitleColor;
		mMax = ss.mMax;
		mGapTarget = ss.mGapTarget;
		mStartAngle = ss.mStartAngle;
		mCurrectSweepAngle = ss.mCurrectSweepAngle;
		mStartSweepAngle = ss.mStartSweepAngle;
		mAnimateSweepAngle = ss.mAnimateSweepAngle;
		mTitle = ss.mTitle;
		mTargetString = ss.mTargetString;
		considerBackgroundArea = ss.considerBackgroundArea;
		autoStart = ss.autoStart;
		isStartAnimatorEnd = ss.isStartAnimatorEnd;
		isSetup = ss.isSetup;
		invalidate();
		super.onRestoreInstanceState(ss.getSuperState());
	}

	public boolean isSetup() {
		return isSetup;
	}

	/**
	 * 开始动画
	 * <p>
	 * 需下一步确认，否则动画会循环等待
	 * 
	 * @author Mofer
	 * 
	 */
	class StartAnimator extends BaseNextAnimator {

		public StartAnimator(View view, long duration, Interpolator interpolator) {
			super(view, duration, interpolator);
		}

		@Override
		protected void animator(float p) {
			applyStartAnimation(p);
		}

		@Override
		public void end() {
			super.end();
			isStartAnimatorEnd = true;
			startProgressAnimation();
		}
	}

	/**
	 * 进度动画
	 * <p>
	 * 需下一步确认，否则动画会循环等待
	 * 
	 * @author Mofer
	 * 
	 */
	class ProgressAnimator extends BaseNextAnimator {

		public ProgressAnimator(View view, long duration,
				Interpolator interpolator) {
			super(view, duration, interpolator);
		}

		@Override
		protected void animator(float p) {
			applyProgressAnimation(p);
		}

	}
	
	static class SavedState extends BaseSavedState {
		private int drawablePadding;
		private int mBackCircle;
		private int mProgressCircle;
		private int mTextProgressColor;
		private int mProgress;
		private int mTextTitleColor;
		private int mMax;
		private float mGapTarget;
		private float mStartAngle;
		private float mCurrectSweepAngle;
		private float mStartSweepAngle;
		private float mAnimateSweepAngle;
		private String mTitle;
		private String mTargetString;
		private boolean considerBackgroundArea;
		private boolean autoStart;
		private boolean isStartAnimatorEnd;
		private boolean isSetup;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			drawablePadding = in.readInt();
			mBackCircle = in.readInt();
			mProgressCircle = in.readInt();
			mTextProgressColor = in.readInt();
			mProgress = in.readInt();
			mTextTitleColor = in.readInt();
			mMax = in.readInt();
			mGapTarget = in.readFloat();
			mStartAngle = in.readFloat();
			mCurrectSweepAngle = in.readFloat();
			mStartSweepAngle = in.readFloat();
			mAnimateSweepAngle = in.readFloat();
			mTitle = in.readString();
			considerBackgroundArea = in.readInt() == 1;
			autoStart = in.readInt() == 1;
			isStartAnimatorEnd = in.readInt() == 1;
			isSetup = in.readInt() == 1;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(drawablePadding);
			out.writeInt(mBackCircle);
			out.writeInt(mProgressCircle);
			out.writeInt(mTextProgressColor);
			out.writeInt(mProgress);
			out.writeInt(mTextTitleColor);
			out.writeInt(mMax);
			out.writeFloat(mGapTarget);
			out.writeFloat(mStartAngle);
			out.writeFloat(mCurrectSweepAngle);
			out.writeFloat(mStartSweepAngle);
			out.writeFloat(mAnimateSweepAngle);
			out.writeString(mTitle);
			out.writeString(mTargetString);
			out.writeInt(considerBackgroundArea ? 1 : 0);
			out.writeInt(autoStart ? 1 : 0);
			out.writeInt(isStartAnimatorEnd ? 1 : 0);
			out.writeInt(isSetup ? 1 : 0);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	// 版本控制器
	interface TextPaintImpl {
		public void setDensity(TextPaint paint, float density);
	}

	static class BaseTextPaintImpl implements TextPaintImpl {

		@Override
		public void setDensity(TextPaint paint, float density) {
		}
	}

	@SuppressLint("NewApi")
	static class EclairViewCompatImpl extends BaseTextPaintImpl {

		@Override
		public void setDensity(TextPaint paint, float density) {
			paint.density = density;
		}
	}

	static final TextPaintImpl IMPL;
	static {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 5) {
			IMPL = new EclairViewCompatImpl();
		} else {
			IMPL = new BaseTextPaintImpl();
		}
	}
}
