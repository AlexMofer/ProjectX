package am.project.x.widgets.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 双圈动图
 * @author Mofer
 *
 */
public class DoubleCircleDrawable extends Drawable implements Animatable {

	private static final long FRAME_DURATION = 1000 / 60;
	
	private final int mWidth;
	private final int mHeight;
	private final float mGap;
	private final float mRadio;
	private final float mMaxRadio;
	private final float mMinRadio;
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean mIsRunning;
	private int mProgress = 0;
	private boolean isUp = true;
	private float mAnimatRadioLeft;
	private float mAnimatRadioRight;
	private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	
	public DoubleCircleDrawable(float density) {
		mGap = 2 * density;
		mMaxRadio = 13 * density;
		mMinRadio = 8 * density;
		mRadio = (mMaxRadio + mMinRadio) * 0.5f;
		mWidth = (int) Math.ceil(mMaxRadio * 4 + mGap);
		mHeight = (int) Math.ceil(mMaxRadio * 2);
	}
	
	@Override
	public int getIntrinsicHeight() {
		return mHeight;
	}
	
	@Override
	public int getIntrinsicWidth() {
		return mWidth;
	}
	
	@Override
	public void start() {
		if (!isRunning()) {
			mIsRunning = true;
			mProgress = 0;
			isUp = true;
			scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
			invalidateSelf();
		}
	}

	@Override
	public void stop() {
		if (isRunning()) {
			mIsRunning = false;
		}
	}

	@Override
	public boolean isRunning() {
		return mIsRunning;
	}

	@Override
	public void draw(Canvas canvas) {
		final float centerX = getIntrinsicWidth() * 0.5f;
		final float centerY = getIntrinsicHeight() * 0.5f;
		final float gap = mGap * 0.5f;
		canvas.save();
		canvas.translate(centerX, centerY);
		
		mPaint.setColor(0xfff75656);
		canvas.drawCircle(-gap - mAnimatRadioLeft, 0, mAnimatRadioLeft, mPaint);
		mPaint.setColor(0xff569af7);
		canvas.drawCircle(gap + mAnimatRadioRight, 0, mAnimatRadioRight, mPaint);
		canvas.restore();

	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	private final Runnable mUpdater = new Runnable() {
		@Override
		public void run() {
			if (mIsRunning) {
				if (isUp) {
					if (mProgress == 24) {
						mProgress = 25;
						isUp = false;
					} else {
						mProgress++;
					}
				} else {
					if (mProgress == -24) {
						mProgress = -25;
						isUp = true;
					} else {
						mProgress--;
					}
				}
				final float input = Math.abs(mProgress * 0.04f);
				mAnimatRadioLeft = mMaxRadio - (mMaxRadio - mMinRadio) * mInterpolator.getInterpolation(input);
				mAnimatRadioRight = mRadio * 2 - mAnimatRadioLeft;
				scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
				invalidateSelf();
			}
		}
	};

}
