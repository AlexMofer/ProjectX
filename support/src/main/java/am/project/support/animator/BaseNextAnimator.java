package am.project.support.animator;

import android.view.View;
import android.view.animation.Interpolator;

/**
 * 基础动画
 *
 * @author Mofer
 */
@SuppressWarnings("unused")
public abstract class BaseNextAnimator extends BaseAnimator {

    private boolean toNext = false;

    public BaseNextAnimator(View view, long duration, Interpolator interpolator) {
        super(view, duration, interpolator);
    }

    @Override
    public void run() {
        if (mTime < mDuration) {
            final float p = mInterpolator.getInterpolation((float) mTime
                    / (float) mDuration);
            animator(p);
            if (toNext) {
                toNext = false;
                mTime += DEFAULT_FRAME_DELAY;
            }
            mView.postDelayed(this, DEFAULT_FRAME_DELAY);
        } else {
            end();
        }
    }

    /**
     * 下一帧
     */
    public void next() {
        toNext = true;
    }

}
