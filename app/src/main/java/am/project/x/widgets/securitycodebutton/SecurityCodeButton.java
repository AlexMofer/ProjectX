package am.project.x.widgets.securitycodebutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 验证码获取按钮
 * Created by Alex on 2015/10/16.
 */
public class SecurityCodeButton extends Button {

    private final TimeAnimator mTimeAnimator = new TimeAnimator();
    private TimeListener listener;

    public SecurityCodeButton(Context context) {
        super(context);
    }

    public SecurityCodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecurityCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SecurityCodeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDuration(long duration) {
        mTimeAnimator.setDuration(duration);
    }

    public void start() {
        mTimeAnimator.start();
    }

    public void stop() {
        mTimeAnimator.stop();
    }

    public void setOnTimeListener(TimeListener listener) {
        this.listener = listener;
    }

    public class TimeAnimator implements Runnable {

        protected static final long DEFAULT_FRAME_DELAY = 1000;
        protected long mDuration = 0;
        protected long mTime = 0;
        protected boolean mRunning = false;

        public void start() {
            mRunning = true;
            mTime = 0;
            post(this);
        }

        public void stop() {
            mRunning = false;
            removeCallbacks(this);
            if (listener != null)
                listener.onNormal(SecurityCodeButton.this);
        }

        @Override
        public void run() {
            if (mTime < mDuration) {
                if (listener != null)
                    listener.onCount(SecurityCodeButton.this, mTime);
                mTime += DEFAULT_FRAME_DELAY;
                postDelayed(this, DEFAULT_FRAME_DELAY);
            } else {
                stop();
            }
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void setDuration(long duration) {
            mDuration = duration;
        }
    }

    public interface TimeListener {
        void onNormal(SecurityCodeButton view);
        void onCount(SecurityCodeButton view, long duration);
    }
}
