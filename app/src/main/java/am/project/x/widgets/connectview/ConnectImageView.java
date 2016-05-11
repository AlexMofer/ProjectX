package am.project.x.widgets.connectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.animators.AnimatorCallback;
import am.project.x.widgets.animators.BaseNextAnimator;
import am.project.x.widgets.drawables.CirclingDrawable;


public class ConnectImageView extends ImageView {

	private CirclingDrawable mDrawableConnectting;
	private Drawable mDrawableConnectSuccess;
	private Drawable mDrawableConnectFailure;
	private boolean mSuccess = false;
	private boolean mFailure = false;
	private boolean mConnectting = false;
	private Interpolator interpolator = new AccelerateDecelerateInterpolator();
	private ConnecttingAnimator mStartAnimation;
	private EndConnectAnimator mEndConnectAnimator;

	public ConnectImageView(Context context) {
		super(context);
		initImageView();
	}
	
	public ConnectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initImageView();
	}

	public ConnectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initImageView();
	}

	private void initImageView() {
		mStartAnimation = new ConnecttingAnimator(this, 400, interpolator);
		mStartAnimation.addAnimatorCallback(new AnimatorCallback() {
			
			@Override
			public void isStop() {
			}
			
			@Override
			public void isStart() {
				mConnectting = true;
			}
			
			@Override
			public void isEnd() {
				mSuccess = false;
				mFailure = false;
				if (mDrawableConnectting != null) {
					mDrawableConnectting.setAlpha(255);
					mDrawableConnectting.start();
				}
			}
		});
		mEndConnectAnimator = new EndConnectAnimator(this, 400, interpolator);
		mEndConnectAnimator.addAnimatorCallback(new AnimatorCallback() {
			
			@Override
			public void isStop() {
			}
			
			@Override
			public void isStart() {
				if (mDrawableConnectting != null) {
					mDrawableConnectting.setAlpha(255);
					mDrawableConnectting.stop();
				}
			}
			
			@Override
			public void isEnd() {
				mConnectting = false;
				if (mDrawableConnectting != null) {
					mDrawableConnectting.setAlpha(0);
				}
			}
		});
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawSuccess(canvas);
		drawFailure(canvas);
		if (mConnectting) {
			mStartAnimation.next();
			mEndConnectAnimator.next();
		}
	}

	private void drawSuccess(Canvas canvas) {
		if (mSuccess && mDrawableConnectSuccess != null) {
			canvas.save();
			canvas.translate(
					(getWidth() - mDrawableConnectSuccess.getIntrinsicWidth()) * 0.5f,
					(getHeight() - mDrawableConnectSuccess.getIntrinsicHeight()) * 0.5f);
			mDrawableConnectSuccess.setBounds(0, 0,
					mDrawableConnectSuccess.getIntrinsicWidth(),
					mDrawableConnectSuccess.getIntrinsicHeight());
			mDrawableConnectSuccess.draw(canvas);
			canvas.restore();
		}
	}
	
	private void drawFailure(Canvas canvas) {
		if (mFailure && mDrawableConnectFailure != null) {
			canvas.save();
			canvas.translate(
					(getWidth() - mDrawableConnectFailure.getIntrinsicWidth()) * 0.5f,
					(getHeight() - mDrawableConnectFailure.getIntrinsicHeight()) * 0.5f);
			mDrawableConnectFailure.setBounds(0, 0,
					mDrawableConnectFailure.getIntrinsicWidth(),
					mDrawableConnectFailure.getIntrinsicHeight());
			mDrawableConnectFailure.draw(canvas);
			canvas.restore();
		}
	}

	public void setDrawables(int stroke, int color, int connectting,
			int success, int failure) {
		setDrawables(stroke, color,
				ContextCompat.getDrawable(getContext(), connectting),
				ContextCompat.getDrawable(getContext(), success),
				ContextCompat.getDrawable(getContext(), failure));
	}

	public void setDrawables(int stroke, int color, Drawable connectting,
			Drawable success, Drawable failure) {
		mDrawableConnectting = new CirclingDrawable(stroke, color, connectting);
		mDrawableConnectSuccess = success;
		mDrawableConnectFailure = failure;
		mDrawableConnectting.setAlpha(0);
		setImageDrawable(mDrawableConnectting);
	}

	public void setDrawables(int connectting, int success, int failure) {
		setDrawables(ContextCompat.getDrawable(getContext(), connectting),
				ContextCompat.getDrawable(getContext(), success),
				ContextCompat.getDrawable(getContext(), failure));
	}

	public void setDrawables(Drawable connectting, Drawable success,
			Drawable failure) {
		mDrawableConnectting = new CirclingDrawable(getResources()
				.getDisplayMetrics().density, connectting);
		mDrawableConnectSuccess = success;
		mDrawableConnectFailure = failure;
		mDrawableConnectting.setAlpha(0);
		setImageDrawable(mDrawableConnectting);
	}
	
	public void startConnectting() {
		if (!mConnectting) {
			mStartAnimation.start();
		}
	}
	
	public void stopToSuccess() {
		if (mConnectting) {
			if (!mSuccess) {
				mSuccess = true;
				mFailure = false;
				mEndConnectAnimator.start();
			}
		} else {
			mSuccess = true;
			mFailure = false;
			invalidate();
		}
		
	}
	
	public void stopToFailure() {
		if (mConnectting) {
			if (!mFailure) {
				mSuccess = false;
				mFailure = true;
				mEndConnectAnimator.start();
			}
		} else {
			mSuccess = false;
			mFailure = true;
			invalidate();
		}
		
	}
	
	private void applyConnecttingAnimation(float input) {
		int alpha = (int) (255 * input);
		if (mSuccess && mDrawableConnectSuccess != null) {
			mDrawableConnectSuccess.setAlpha(255 - alpha);
		}
		if (mFailure && mDrawableConnectFailure != null) {
			mDrawableConnectFailure.setAlpha(255 - alpha);
		}
		if (mDrawableConnectting != null) {
			mDrawableConnectting.setAlpha(alpha);
		}
	}
	
	private void applyEndConnectAnimation(float input) {
		int alpha = (int) (255 * input);
		if (mDrawableConnectting != null) {
			mDrawableConnectting.setAlpha(255 - alpha);
		}
		if (mSuccess && mDrawableConnectSuccess != null) {
			mDrawableConnectSuccess.setAlpha(alpha);
		}
		if (mFailure && mDrawableConnectFailure != null) {
			mDrawableConnectFailure.setAlpha(alpha);
		}
		invalidate();
	}
	
	class ConnecttingAnimator extends BaseNextAnimator {

		public ConnecttingAnimator(View view, long duration,
				Interpolator interpolator) {
			super(view, duration, interpolator);
		}

		@Override
		protected void animator(float p) {
			applyConnecttingAnimation(p);
		}

	}
	
	class EndConnectAnimator extends BaseNextAnimator {

		public EndConnectAnimator(View view, long duration,
				Interpolator interpolator) {
			super(view, duration, interpolator);
		}

		@Override
		protected void animator(float p) {
			applyEndConnectAnimation(p);
		}

	}
	
}
