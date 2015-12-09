package android.support.v4.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * 中部绘制Drawable下拉刷新
 * 
 * @author Mofer
 * 
 */
public class SuperSwipeRefreshLayout extends SwipeRefreshLayout {

	private static final float DRAG_RATE = .5f;
	private static final int DEFAULT_CIRCLE_TARGET = 64;
	private static final int SCALE_DOWN_DURATION = 150;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
	private boolean showCenter = false;
	private Drawable mCenterDrawable;
	private float mTotalDragDistance = -1;
	private float mInitialDownY;
	private int mAlpha = 255;
	private AlphaEndAnimation mAlphaEndAnimation;
	private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(
			DECELERATE_INTERPOLATION_FACTOR);
	private final AnimationListener mAnimationListener;

	public SuperSwipeRefreshLayout(Context context) {
		this(context, null);
	}

	public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTotalDragDistance = DEFAULT_CIRCLE_TARGET
				* getResources().getDisplayMetrics().density;
		mAnimationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				hideCenterDrawable();
			}
		};
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (showCenter && mCenterDrawable != null) {
			canvas.save();
			canvas.translate(
					(getWidth() - mCenterDrawable.getIntrinsicWidth()) * 0.5f,
					(getHeight() - mCenterDrawable.getIntrinsicHeight()) * 0.5f);
			mCenterDrawable.setAlpha(mAlpha);
			mCenterDrawable.setBounds(0, 0,
					mCenterDrawable.getIntrinsicWidth(),
					mCenterDrawable.getIntrinsicHeight());
			mCenterDrawable.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mInitialDownY = ev.getY();
			if (mAlphaEndAnimation != null) {
				mAlphaEndAnimation.stop();
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final boolean result = super.onTouchEvent(ev);
		if (showCenter) {
			final int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mInitialDownY = ev.getY();
				break;

			case MotionEvent.ACTION_MOVE: {
				final float overscrollTop = (ev.getY() - mInitialDownY)
						* DRAG_RATE;
				if (overscrollTop < 0) {
					mAlpha = 255;
					invalidateCenterDrawable();
				} else if (overscrollTop < mTotalDragDistance) {
					mAlpha = (int) (255 * ((mTotalDragDistance - overscrollTop) / mTotalDragDistance));
					mAlpha = mAlpha < 0 ? 0 : mAlpha;
					invalidateCenterDrawable();
				} else {
					mAlpha = 0;
					invalidateCenterDrawable();
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				final float overscrollTop = (ev.getY() - mInitialDownY)
						* DRAG_RATE;
				if (overscrollTop > mTotalDragDistance) {
					if (isRefreshing()) {
						hideCenterDrawable();
					} else {
						mAlphaEndAnimation = new AlphaEndAnimation(mAlpha, 255);
						startAnimation(mAlphaEndAnimation);
					}
				} else {
					mAlphaEndAnimation = new AlphaEndAnimation(mAlpha, 255);
					startAnimation(mAlphaEndAnimation);
				}
				break;
			}
			}
		}
		return result;
	}

	private void invalidateCenterDrawable() {
		if (showCenter && mCenterDrawable != null) {
			invalidate((getWidth() - mCenterDrawable.getIntrinsicWidth()) / 2,
					(getHeight() - mCenterDrawable.getIntrinsicHeight()) / 2,
					(getWidth() + mCenterDrawable.getIntrinsicWidth()) / 2,
					(getHeight() + mCenterDrawable.getIntrinsicHeight()) / 2);
		}
	}

	public void setCenterDrawable(Drawable drawable) {
		mCenterDrawable = drawable;
		invalidate();
	}

	public void setCenterDrawable(int resId) {
		setCenterDrawable(ContextCompat.getDrawable(getContext(), resId));
	}

	private void hideCenterDrawable() {
		showCenter = false;
		if (mCenterDrawable != null) {
			invalidate((getWidth() - mCenterDrawable.getIntrinsicWidth()) / 2,
					(getHeight() - mCenterDrawable.getIntrinsicHeight()) / 2,
					(getWidth() + mCenterDrawable.getIntrinsicWidth()) / 2,
					(getHeight() + mCenterDrawable.getIntrinsicHeight()) / 2);
		}
	}

	public void showCenterDrawable(boolean show) {
		mAlpha = 255;
		if (mCenterDrawable != null) {
			if (show) {
				showCenter = show;
				mAlphaEndAnimation = new AlphaEndAnimation(0, 255);
				startAnimation(mAlphaEndAnimation);
			} else {
				mAlphaEndAnimation = new AlphaEndAnimation(255, 0);
				mAlphaEndAnimation.setAnimationListener(mAnimationListener);
				startAnimation(mAlphaEndAnimation);
			}
		} else {
			showCenter = show;
		}
	}

	@Override
	public void setRefreshing(boolean refreshing) {
		if (refreshing && showCenter) {
			showCenterDrawable(false);
		}
		super.setRefreshing(refreshing);
	}
	
	@Override
	public void setDistanceToTriggerSync(int distance) {
		mTotalDragDistance = distance;
		super.setDistanceToTriggerSync(distance);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		SavedState ss = new SavedState(superState);
		ss.showCenter = showCenter ? 1 : 0;
		ss.alpha = mAlpha;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		showCenter = ss.showCenter == 1;
		mAlpha = ss.alpha;
		invalidate();
		super.onRestoreInstanceState(ss.getSuperState());
		
	}

	static class SavedState extends BaseSavedState {
		private int showCenter = 0;
		private int alpha = 0;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			showCenter = in.readInt();
			alpha = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(showCenter);
			out.writeInt(alpha);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	class AlphaEndAnimation extends Animation {
		private int mStartAlpha;
		private int mEndAlpha;

		AlphaEndAnimation(int startAlpha, int endAlpha) {
			mStartAlpha = startAlpha;
			mEndAlpha = endAlpha;
			setDuration(SCALE_DOWN_DURATION);
			setInterpolator(mDecelerateInterpolator);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			mAlpha = (int) (mStartAlpha + (mEndAlpha - mStartAlpha)
					* interpolatedTime);
			invalidateCenterDrawable();
			super.applyTransformation(interpolatedTime, t);
		}

		/**
		 * 结束动画
		 */
		public void stop() {
			if (hasStarted() && !hasEnded()) {
				cancel();
				mAlpha = mEndAlpha;
				invalidateCenterDrawable();
			}
		}
	}
}
