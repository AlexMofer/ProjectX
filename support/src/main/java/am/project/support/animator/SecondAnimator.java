package am.project.support.animator;

import android.view.View;

/**
 * 每秒动画
 * 使用ValueAnimator及其setFrameDelay(long frameDelay)方法替代
 * 将在下一个版本去除
 * Created by Alex on 2016/6/17.
 */
@Deprecated
public class SecondAnimator implements Runnable {
    protected static final long DEFAULT_FRAME_DELAY = 1000;
    private View mView;
    protected long mDuration = 0;
    protected long mTime = 0;
    protected boolean mRunning = false;
    private SecondAnimatorListener mListener;

    public SecondAnimator(View view, int duration, SecondAnimatorListener listener) {
        mView = view;
        mDuration = duration;
        mListener = listener;
    }

    public void start() {
        mRunning = true;
        mTime = 0;
        if (mListener != null)
            mListener.onStart(mView);
        mView.post(this);
    }

    public void stop() {
        mRunning = false;
        mView.removeCallbacks(this);
        if (mListener != null)
            mListener.onStop(mView);
    }

    @Override
    public void run() {
        if (mTime < mDuration) {
            if (mListener != null)
                mListener.onAnimator(mView, mTime, mDuration);
            mTime += DEFAULT_FRAME_DELAY;
            mView.postDelayed(this, DEFAULT_FRAME_DELAY);
        } else {
            stop();
        }
    }

    /**
     * 判断是否正在运行
     *
     * @return 是否正在运行
     */
    @SuppressWarnings("unused")
    public boolean isRunning() {
        return mRunning;
    }


    /**
     * 设置时长
     *
     * @param duration 时长
     */
    @SuppressWarnings("unused")
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * 设置监听
     *
     * @param listener 监听
     */
    @SuppressWarnings("unused")
    public void setOnSecondAnimatorListener(SecondAnimatorListener listener) {
        mListener = listener;
    }

    /**
     * 秒动画监听
     */
    public interface SecondAnimatorListener {
        /**
         * 动画开始
         *
         * @param view View
         */
        void onStart(View view);

        /**
         * 动画进行中
         *
         * @param view     View
         * @param duration 第多少秒
         * @param total    总多少秒
         */
        void onAnimator(View view, long duration, long total);

        /**
         * 动画结束
         *
         * @param view View
         */
        void onStop(View view);
    }
}
