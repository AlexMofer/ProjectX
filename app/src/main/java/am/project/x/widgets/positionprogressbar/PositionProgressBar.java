package am.project.x.widgets.positionprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 位置进度条
 * 
 * @author Mofer
 * 
 */
public class PositionProgressBar extends View {

	private static final int COLOR_DEFAULT = 0x66FFFFFF;
	private static final float SIZE_DEFAULT = 2.5f;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private PositionProgressAdapter mAdapter;
	private int lineHeight;
	private int lineColor;

	private float mProgress = 0;

	public PositionProgressBar(Context context) {
		super(context);
		init();
	}

	public PositionProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PositionProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		if (isInEditMode()) {
			mAdapter = new SimplePositionProgressAdapter(0, 0.25f, 0.5f, 1);
			mProgress = 0.75f;
		}
		setLineHeight((int) (getResources().getDisplayMetrics().density * SIZE_DEFAULT));
		setLineColor(COLOR_DEFAULT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (isEmpty()) {
			setMeasuredDimension(0, 0);
			return;
		}
		final int maxDrawableHeight = mAdapter.getMaxDrawableHeight();
		int layoutHeight = 0;
		switch (heightMode) {
		case MeasureSpec.EXACTLY:
			layoutHeight = heightSize;
			break;
		case MeasureSpec.UNSPECIFIED:
		case MeasureSpec.AT_MOST:
			layoutHeight = maxDrawableHeight + getPaddingTop()
					+ getPaddingBottom() + lineHeight;
			break;
		}
		int layoutWidth = 0;
		switch (widthMode) {
		case MeasureSpec.EXACTLY:
			layoutWidth = widthSize;
			break;
		case MeasureSpec.UNSPECIFIED:
		case MeasureSpec.AT_MOST:
			layoutWidth = mAdapter.getCount() * mAdapter.getMaxDrawableWidth()
					+ (mAdapter.getCount() - 1)
					* mAdapter.getProgressDrawableWidth()
					+ ViewCompat.getPaddingStart(this)
					+ ViewCompat.getPaddingEnd(this);
			break;
		}
		setMeasuredDimension(layoutWidth, layoutHeight);
	}

	private boolean isEmpty() {
		if (mAdapter == null || mAdapter.getCount() == 0) {
			return true;
		}
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isEmpty()) {
			return;
		}
		drawLine(canvas);
		drawMarks(canvas);
		drawProgress(canvas);
	}

	private void drawLine(Canvas canvas) {
		mPaint.setColor(lineColor);
		final float left = ViewCompat.getPaddingStart(this)
				+ mAdapter.getLeftOffset();
		final float right = getWidth() - ViewCompat.getPaddingEnd(this)
				- mAdapter.getRightOffset();
		final float bottom = getHeight() - getPaddingBottom();
		final float top = bottom - lineHeight;
		canvas.drawRect(left, top, right, bottom, mPaint);
	}

	private void drawMarks(Canvas canvas) {
		canvas.save();
		float lineWidth = getWidth() - ViewCompat.getPaddingStart(this)
				- ViewCompat.getPaddingEnd(this) - mAdapter.getLeftOffset()
				- mAdapter.getRightOffset();
		canvas.translate(
				ViewCompat.getPaddingStart(this) + mAdapter.getLeftOffset(),
				getHeight() - getPaddingBottom() - lineHeight);
		for (int i = 0; i < mAdapter.getCount(); i++) {
			float location = mAdapter.getMarkLocation(i);
			canvas.save();
			canvas.translate(lineWidth * location, 0);
			Drawable drawable;
			if (mProgress > location) {
				drawable = mAdapter.getMarkDrawableDone(i);
			} else {
				drawable = mAdapter.getMarkDrawableNormal(i);
			}
			if (drawable != null) {
				canvas.translate(-drawable.getIntrinsicWidth() * 0.5f, 0);
				drawable.setBounds(0, -drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
				drawable.draw(canvas);
			}
			
			canvas.restore();
		}
		canvas.restore();

	}

	private void drawProgress(Canvas canvas) {
		if (mProgress >= 0 && mProgress <= 1) {
			canvas.save();
			float lineWidth = getWidth() - ViewCompat.getPaddingStart(this)
					- ViewCompat.getPaddingEnd(this) - mAdapter.getLeftOffset()
					- mAdapter.getRightOffset();
			canvas.translate(
					ViewCompat.getPaddingStart(this) + mAdapter.getLeftOffset(),
					getHeight() - getPaddingBottom() - lineHeight);
			canvas.translate(lineWidth * mProgress, 0);
			Drawable drawable = mAdapter.getProgressDrawable();
			if (drawable != null) {
				canvas.translate(-drawable.getIntrinsicWidth() * 0.5f, 0);
				drawable.setBounds(0, -drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth(), 0);
				drawable.draw(canvas);
			}
			canvas.restore();
		}
	}

	public void setProgress(float value) {
		mProgress = value;
		invalidate();
	}

	public void setAdapter(PositionProgressAdapter adapter) {
		mAdapter = adapter;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int height) {
		lineHeight = height;
		requestLayout();
		invalidate();
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int color) {
		lineColor = color;
		invalidate();
	}

	public interface PositionProgressAdapter {
		public int getCount();

		public int getMaxDrawableHeight();

		public int getMaxDrawableWidth();

		public int getProgressDrawableWidth();

		public float getLeftOffset();

		public float getRightOffset();

		public float getMarkLocation(int position);

		public Drawable getMarkDrawableNormal(int position);
		
		public Drawable getMarkDrawableDone(int position);
		
		public Drawable getProgressDrawable();
	}

	public static class SimplePositionProgressAdapter implements
			PositionProgressAdapter {

		private float[] mMarks;
		private final ShapeDrawable mProgressShape;
		private final ShapeDrawable mMarkShapeNormal;
		private final ShapeDrawable mMarkShapeDone;

		public SimplePositionProgressAdapter(float... marks) {
			mMarks = marks;
			ProgressShape shape = new ProgressShape(0xff33b5e5);
			mProgressShape = new ShapeDrawable(shape);
			mProgressShape.setIntrinsicWidth((int) shape.getWidth());
			mProgressShape.setIntrinsicHeight((int) shape.getHeight());

			MarkShape shapeNormal = new MarkShape(Color.GRAY);
			mMarkShapeNormal = new ShapeDrawable(shapeNormal);
			mMarkShapeNormal.setIntrinsicWidth((int) shapeNormal.getWidth());
			mMarkShapeNormal.setIntrinsicHeight((int) shapeNormal.getHeight());

			MarkShape shapeDone = new MarkShape(Color.WHITE);
			mMarkShapeDone = new ShapeDrawable(shapeDone);
			mMarkShapeDone.setIntrinsicWidth((int) shapeDone.getWidth());
			mMarkShapeDone.setIntrinsicHeight((int) shapeDone.getHeight());
		}

		@Override
		public int getCount() {
			return mMarks == null ? 0 : mMarks.length;
		}

		@Override
		public int getMaxDrawableHeight() {
			return mProgressShape.getIntrinsicHeight();
		}

		@Override
		public int getMaxDrawableWidth() {
			return mProgressShape.getIntrinsicWidth();
		}

		@Override
		public int getProgressDrawableWidth() {
			return getMaxDrawableWidth();
		}

		@Override
		public float getLeftOffset() {
			return mMarkShapeDone.getIntrinsicWidth() * 0.5f;
		}

		@Override
		public float getRightOffset() {
			return mMarkShapeNormal.getIntrinsicWidth() * 0.5f;
		}

		@Override
		public float getMarkLocation(int position) {
			return mMarks == null ? 0 : mMarks[position];
		}

		@Override
		public Drawable getMarkDrawableNormal(int position) {
			return mMarkShapeNormal;
		}
		
		@Override
		public Drawable getMarkDrawableDone(int position) {
			return mMarkShapeDone;
		}

		@Override
		public Drawable getProgressDrawable() {
			return mProgressShape;
		}

	}

	private static class MarkShape extends Shape {
		private int color;
		private Path clipPath = new Path();
		private Path mPath = new Path();
		private RectF mOval = new RectF();

		MarkShape(int color) {
			this.color = color;
			resize(18, 38);
		}

		@Override
		public void draw(Canvas canvas, Paint paint) {
			paint.setColor(color);
			clipPath.addCircle(9, 9, 4, Direction.CCW);
			canvas.clipPath(clipPath, Region.Op.XOR);
			canvas.drawCircle(9, 9, 9, paint);
			mPath.moveTo(9, 0);
			mOval.set(-63, 0, 9, 72);
			mPath.arcTo(mOval, -32, 32);
			mOval.set(9, 0, 81, 72);
			mPath.arcTo(mOval, 180, 32);
			mPath.lineTo(9, 0);
			mPath.close();
			canvas.drawPath(mPath, paint);
		}
	}

	private static class ProgressShape extends Shape {
		private int color;
		private Path mPath = new Path();

		ProgressShape(int color) {
			this.color = color;
			resize(24, 48);
		}

		@Override
		public void draw(Canvas canvas, Paint paint) {
			paint.setColor(color);
			mPath.addCircle(7, 17, 2, Direction.CCW);
			mPath.addCircle(17, 17, 2, Direction.CCW);
			canvas.clipPath(mPath, Region.Op.XOR);
			canvas.rotate(35, 12, 15);
			canvas.drawRect(11, 2, 13, 8, paint);
			canvas.rotate(-35, 12, 15);
			canvas.rotate(-35, 12, 15);
			canvas.drawRect(11, 2, 13, 8, paint);
			canvas.rotate(35, 12, 15);
			canvas.drawCircle(12, 20, 11, paint);
			canvas.drawRect(11, 33, 13, 48, paint);
		}

	}
}
