package android.support.v4.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

/**
 * 停止带状态的材料进度Drawable
 * <p> 下箭头下一步、错误叉、叹号警告
 * @author Mofer
 * 
 */
public class SuperMaterialProgressDrawable extends MaterialProgressDrawable {

	public static final int MAX_ALPHA = 255;
	public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;
	public static final int LARGE = MaterialProgressDrawable.LARGE;
	public static final int[] COLORS = new int[] { 0xff33b5e5, 0xffff4444,
			0xff99cc00, 0xffffbb33, 0xffaa66cc };
	public static final int COLOR_RED = 0xffff4444;
	public static final int COLOR_ORANGE = 0xffffbb33;
	public static final int COLOR_GREEN = 0xff99cc00;
	private final float screenDensity;
	private static final float DEFAULT_RADIUS = 11.25f;
	private static final float DEFAULT_WIDTH = 16f;
	private static final float DEFAULT_HEIGHT = 2.5f;
	private static final float LARGE_RADIUS = 15.5f;
	private static final float LARGE_WIDTH = 23;
	private static final float LARGE_HEIGHT = 3f;
	private boolean running = false;
	private boolean drawWarning = false;
	private boolean drawError = false;
	private boolean drawNext = false;
	private boolean simultaneously = false;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Path mPath = new Path();
	private int colorNext = COLOR_GREEN;
	private int colorWarning = COLOR_ORANGE;
	private int colorError = COLOR_RED;
	private float mRadius;
	private float mWidth;
	private float mHeight;

	public SuperMaterialProgressDrawable(Context context, View parent) {
		super(context, parent);
		screenDensity = context.getResources().getDisplayMetrics().density;
		setSizeParameters(DEFAULT_RADIUS, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public void updateSizes(@ProgressDrawableSize int size) {
		super.updateSizes(size);
		if (size == LARGE) {
			setSizeParameters(DEFAULT_RADIUS, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		} else {
			setSizeParameters(LARGE_RADIUS, LARGE_WIDTH, LARGE_HEIGHT);
		}
	}

	private void setSizeParameters(float defaultRadius, float defaultWidth,
			float defaultHeight) {
		mRadius = DEFAULT_RADIUS * screenDensity;
		mWidth = DEFAULT_WIDTH * screenDensity;
		mHeight = DEFAULT_HEIGHT * screenDensity;
	}

	@Override
	public void draw(Canvas c) {
		super.draw(c);
		if (!running) {
			if (drawNext) {
				drawNext(c);
			}
			if (drawWarning) {
				drawWarning(c);
			}
			if (drawError) {
				drawError(c);
			}
		}
	}

	private float lerp(float paramFloat1, float paramFloat2, float paramFloat3) {
		return paramFloat1 + paramFloat3 * (paramFloat2 - paramFloat1);
	}
	
	private void drawNext(Canvas c) {
		Rect localRect = getBounds();
        final float arrowArm = mWidth * 0.5f;
        final float arrowCenter = mWidth * 0.75f;
        final float arrowWight = mHeight * 0.75f;
        final float f1 = lerp(localRect.width(), arrowArm, 1);
        final float f2 = lerp(localRect.width(), arrowCenter, 1);
        final float f3 = lerp(0.0F, arrowWight / 2.0F, 1);
        final float f4 = lerp(0.0F, (float) Math.toRadians(45.0D), 1);
        final float f5 = lerp(arrowWight + arrowWight, 0.0F, 1);
        this.mPath.rewind();
        final float f6 = -f2 / 2.0F;
        this.mPath.moveTo(f6 + f3, 0.0F);
        this.mPath.rLineTo(f2 - f3, 0.0F);
        final float f7 = (float) Math.round(f1 * Math.cos(f4));
        final float f8 = (float) Math.round(f1 * Math.sin(f4));
        this.mPath.moveTo(f6, f5);
        this.mPath.rLineTo(f7, f8);
        this.mPath.moveTo(f6, -f5);
        this.mPath.rLineTo(f7, -f8);
        this.mPath.moveTo(0.0F, 0.0F);
        this.mPath.close();
        c.save();
        c.rotate(-90, localRect.centerX(), localRect.centerY());
        c.translate(localRect.centerX(), localRect.centerY());
        mPaint.setColor(colorNext);
        c.drawCircle(0, 0, mRadius, mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mPaint.setStrokeWidth(arrowWight);
        mPaint.setColor(Color.WHITE);
        c.drawPath(this.mPath, this.mPaint);
        c.restore();
        this.mPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void start() {
		if (!running) {
			running = true;
			super.start();
		}
	}

	@Override
	public void stop() {
		if (running) {
			running = false;
			super.stop();
		}
	}

	private void drawWarning(Canvas c) {
		final int width = getIntrinsicWidth();
		final int height = getIntrinsicHeight();
		final float padding = mHeight * 2;
		final double sin60 = Math.sin(60 * 2 * Math.PI / 360);
		final double cos60 = Math.cos(60 * 2 * Math.PI / 360);
		final float triangleWidth = width - padding * 2;
		final float triangleHeight = (float) (triangleWidth * sin60);
		c.save();
		c.translate(width * 0.5f, height * 0.5f);

		// 绘制圆角三角形
		c.save();
		mPaint.setColor(colorWarning);
		c.translate(0, triangleWidth * 0.5f - (triangleWidth - triangleHeight));
		mPath.reset();
		mPath.moveTo(0, 0);
		float tempXl;
		float tempYl;
		float tempX;
		float tempY;

		tempXl = triangleWidth * 0.5f - padding;
		tempYl = 0;
		mPath.lineTo(tempXl, tempYl);
		tempX = triangleWidth * 0.5f - (float) (padding * cos60);
		tempY = -(float) (padding * sin60);
		mPath.cubicTo(tempXl, tempYl, triangleWidth * 0.5f, 0, tempX, tempY);

		tempXl = triangleWidth * 0.5f
				- (float) ((triangleWidth - padding) * cos60);
		tempYl = -(float) ((triangleWidth - padding) * sin60);
		mPath.lineTo(tempXl, tempYl);
		tempX = -tempXl;
		tempY = tempYl;
		mPath.cubicTo(tempXl, tempYl, 0, -triangleHeight, tempX, tempY);

		tempXl = -triangleWidth * 0.5f + (float) (padding * cos60);
		tempYl = -(float) (padding * sin60);
		mPath.lineTo(tempXl, tempYl);
		tempX = -triangleWidth * 0.5f + padding;
		tempY = 0;
		mPath.cubicTo(tempXl, tempYl, -triangleWidth * 0.5f, 0, tempX, tempY);

		mPath.close();
		c.drawPath(mPath, mPaint);
		c.restore();

		mPaint.setColor(Color.BLACK);
		mPath.reset();

		final float tWidth = mHeight * 0.75f;
		final float bottom = mWidth * 0.5f - mHeight * 2;
		mPath.moveTo(0, bottom);
		mPath.lineTo(tWidth * 0.5f, bottom);
		mPath.lineTo(mHeight * 0.5f, -mWidth * 0.5f);
		mPath.lineTo(-mHeight * 0.5f, -mWidth * 0.5f);
		mPath.lineTo(-tWidth * 0.5f, bottom);
		mPath.close();
		c.drawPath(mPath, mPaint);
		c.drawCircle(0, (mWidth - mHeight) * 0.5f, mHeight * 0.5f, mPaint);
		c.restore();
	}

	private void drawError(Canvas c) {
		final int width = getIntrinsicWidth();
		final int height = getIntrinsicHeight();
		c.save();
		c.translate(width * 0.5f, height * 0.5f);
		mPaint.setColor(colorError);
		c.drawCircle(0, 0, mRadius, mPaint);
		mPaint.setColor(Color.WHITE);
		c.rotate(45);
		c.drawRect(-mWidth * 0.5f, -mHeight * 0.5f, mWidth * 0.5f,
				mHeight * 0.5f, mPaint);
		c.rotate(90);
		c.drawRect(-mWidth * 0.5f, -mHeight * 0.5f, mWidth * 0.5f,
				mHeight * 0.5f, mPaint);
		c.restore();
	}

	public void showNext(boolean show) {
		drawNext = show;
		if (show && !simultaneously) {
			drawWarning = false;
			drawError = false;
		}
		invalidateSelf();
	}

	public void showWarning(boolean show) {
		drawWarning = show;
		if (show && !simultaneously) {
			drawNext = false;
			drawError = false;
		}
		invalidateSelf();
	}

	public void showError(boolean show) {
		drawError = show;
		if (show && !simultaneously) {
			drawNext = false;
			drawWarning = false;
		}
		invalidateSelf();
	}

	public void showNothing() {
		drawWarning = false;
		drawError = false;
		invalidateSelf();
	}

	@SuppressLint("NewApi")
	@Override
	public int getAlpha() {
		return super.getAlpha();
	}

	public boolean isSimultaneously() {
		return simultaneously;
	}

	public void setSimultaneously(boolean simultaneously) {
		this.simultaneously = simultaneously;
	}

	public int getColorWarning() {
		return colorWarning;
	}

	public void setColorWarning(int color) {
		colorWarning = color;
	}

	public int getColorError() {
		return colorError;
	}

	public void setColorError(int color) {
		colorError = color;
	}

	public int getColorNext() {
		return colorNext;
	}

	public void setColorNext(int color) {
		colorNext = color;
	}
	
	public boolean isProgressing() {
		return running;
	}
}
