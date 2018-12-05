package am.project.support.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;


/**
 * 基础View动画
 * Created by Alex on 2017/11/9.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ViewAnimation implements Runnable {
    private static final int DEFAULT_DURATION = 250;
    private final Animation mAnimation = new Animation();
    private View mView;
    private long mDuration = DEFAULT_DURATION;
    private Interpolator mInterpolator = new AccelerateInterpolator();
    private boolean mEatRunOnAnimationRequest = false;
    private boolean mReSchedulePostAnimationCallback = false;

    @Override
    public void run() {
        if (mView == null) {
            stop();
            return;
        }
        disableRunOnAnimationRequests();
        final Animation animation = mAnimation;
        boolean first = animation.isFirst();
        if (animation.animate()) {
            final float interpolation = animation.getInterpolation();
            if (first) {
                onStart(interpolation);
            }
            if (animation.isFinished()) {
                onStop(1);
            } else {
                onAnimate(interpolation);
                postOnAnimation();
            }
        }
        enableRunOnAnimationRequests();
    }

    protected void onStart(float interpolation) {

    }

    protected abstract void onAnimate(float interpolation);

    protected void onStop(@SuppressWarnings("SameParameterValue") float interpolation) {

    }

    private void disableRunOnAnimationRequests() {
        mReSchedulePostAnimationCallback = false;
        mEatRunOnAnimationRequest = true;
    }

    private void enableRunOnAnimationRequests() {
        mEatRunOnAnimationRequest = false;
        if (mReSchedulePostAnimationCallback) {
            postOnAnimation();
        }
    }

    private void postOnAnimation() {
        if (mEatRunOnAnimationRequest) {
            mReSchedulePostAnimationCallback = true;
        } else {
            mView.removeCallbacks(this);
            Compat.postOnAnimation(mView, this);
        }
    }

    private void postOnAnimationDelayed(long delayMillis) {
        if (mEatRunOnAnimationRequest) {
            mReSchedulePostAnimationCallback = true;
        } else {
            mView.removeCallbacks(this);
            Compat.postOnAnimationDelayed(mView, this, delayMillis);
        }
    }

    public void attach(View view) {
        stop();
        mView = view;
    }

    public void detach() {
        stop();
        mView = null;
    }

    public boolean isRunning() {
        return !mAnimation.isFinished();
    }

    public void stop() {
        if (mView == null)
            return;
        mView.removeCallbacks(this);
        mAnimation.abortAnimation();
    }

    public void start() {
        start(mDuration, mInterpolator);
    }

    public void start(long duration, Interpolator interpolator) {
        if (mView == null)
            return;
        mAnimation.reset(duration, interpolator);
        postOnAnimation();
    }

    public void startDelayed(long delayMillis) {
        startDelayed(mDuration, mInterpolator, delayMillis);
    }

    public void startDelayed(long duration, Interpolator interpolator, long delayMillis) {
        if (mView == null)
            return;
        mAnimation.reset(duration, interpolator);
        postOnAnimationDelayed(delayMillis);
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    private class Animation {
        private boolean mFinished;
        private long mStartTime;
        private long mDuration;
        private Interpolator mInterpolator;
        private boolean mFirst;
        private float mInterpolation;

        void reset(long duration, Interpolator interpolator) {
            mFinished = false;
            mDuration = duration;
            mInterpolator = interpolator;
        }

        boolean isFinished() {
            return mFinished;
        }

        boolean isFirst() {
            return mFirst;
        }

        void abortAnimation() {
            mFinished = true;
            mFirst = true;
            mInterpolation = 0;
        }

        boolean animate() {
            if (isFinished()) {
                return false;
            }
            if (mFirst) {
                mStartTime = AnimationUtils.currentAnimationTimeMillis();
                mFirst = false;
                return true;
            }
            long time = AnimationUtils.currentAnimationTimeMillis();
            final long elapsedTime = time - mStartTime;

            final long duration = mDuration;
            if (elapsedTime < duration) {
                mInterpolation = mInterpolator.getInterpolation(elapsedTime / (float) duration);
            } else {
                abortAnimation();
            }
            return true;
        }

        float getInterpolation() {
            return mInterpolation;
        }
    }
}