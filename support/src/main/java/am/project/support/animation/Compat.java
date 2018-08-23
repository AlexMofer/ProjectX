package am.project.support.animation;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/**
 * 版本兼容器
 * Created by Alex on 2017/11/10.
 */

class Compat {

    private static final long FAKE_FRAME_TIME = 10;
    private static final CompatBaseImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new CompatApi16Impl();
        } else if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new CompatApi11Impl();
        } else {
            IMPL = new CompatBaseImpl();
        }
    }

    /**
     * <p>Causes the Runnable to execute on the next animation time step.
     * The runnable will be run on the user interface thread.</p>
     * <p>
     * <p>This method can be invoked from outside of the UI thread
     * only when this View is attached to a window.</p>
     *
     * @param view   View to post this Runnable to
     * @param action The Runnable that will be executed.
     */
    static void postOnAnimation(View view, Runnable action) {
        IMPL.postOnAnimation(view, action);
    }

    /**
     * <p>Causes the Runnable to execute on the next animation time step,
     * after the specified amount of time elapses.
     * The runnable will be run on the user interface thread.</p>
     * <p>
     * <p>This method can be invoked from outside of the UI thread
     * only when this View is attached to a window.</p>
     *
     * @param view        The view to post this Runnable to
     * @param action      The Runnable that will be executed.
     * @param delayMillis The delay (in milliseconds) until the Runnable
     *                    will be executed.
     */
    static void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
        IMPL.postOnAnimationDelayed(view, action, delayMillis);
    }

    static class CompatBaseImpl {
        public void postOnAnimation(View view, Runnable action) {
            view.postDelayed(action, getFrameTime());
        }

        public void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postDelayed(action, getFrameTime() + delayMillis);
        }

        long getFrameTime() {
            return FAKE_FRAME_TIME;
        }
    }

    @TargetApi(11)
    static class CompatApi11Impl extends CompatBaseImpl {
        @Override
        long getFrameTime() {
            return ValueAnimator.getFrameDelay();
        }
    }

    @TargetApi(16)
    static class CompatApi16Impl extends CompatBaseImpl {
        @Override
        public void postOnAnimation(View view, Runnable action) {
            view.postOnAnimation(action);
        }

        @Override
        public void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postOnAnimationDelayed(action, delayMillis);
        }
    }
}
