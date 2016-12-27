package am.project.x.widgets.supergridview.animator;

import android.view.View;
import android.view.animation.Interpolator;

public abstract class BaseAnimator implements Runnable {

	protected static final long DEFAULT_FRAME_DELAY = 10;
	private View mView;
	protected long mDuration;
	protected Interpolator mInterpolator;
	protected long mTime = 0;
	protected boolean mRunning = false;
	protected AnimatorCallback animatorCallback;
	
	public BaseAnimator(View view,long duration, Interpolator interpolator) {
		mDuration = duration;
		mView = view;
		mInterpolator = interpolator;
	}

	/**
	 * 开始
	 */
	public void start() {
		mRunning = true;
		if (animatorCallback != null)
			animatorCallback.isStart();
		animator(0);
		mTime = DEFAULT_FRAME_DELAY;
		mView.postDelayed(this, DEFAULT_FRAME_DELAY);
	}

	/**
	 * 停止
	 */
	public void stop() {
		mRunning = false;
		mView.removeCallbacks(this);
		if (animatorCallback != null)
			animatorCallback.isStop();
	}

	/**
	 * 结束动画
	 */
	public void end() {
		stop();
		mView.removeCallbacks(this);
		animator(1);
		if (animatorCallback != null)
			animatorCallback.isEnd();
	}

	@Override
	public void run() {
		if (mTime < mDuration) {
			final float p = mInterpolator.getInterpolation((float) mTime
					/ (float) mDuration);
			animator(p);
			mTime += DEFAULT_FRAME_DELAY;
			mView.postDelayed(this, DEFAULT_FRAME_DELAY);
		} else {
			end();
		}
	}

	/**
	 * 动画
	 * 
	 * @param p
	 */
	protected abstract void animator(float p);

	/**
	 * 正在动画
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * 修改动画持续时间
	 * 
	 * @param duration
	 */
	public void setDuration(long duration) {
		mDuration = duration;
	}

	/**
	 * 添加动画状态回调
	 * @param callback
	 */
	public void addAnimatorCallback(AnimatorCallback callback) {
		animatorCallback = callback;
	}
	
	/**
	 * 移除动画状态回调
	 * @param callback
	 */
	public void removeAnimatorCallback() {
		animatorCallback = null;
	}
	
	/**
	 * 修改补帧器
	 * 
	 * @param interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

}
