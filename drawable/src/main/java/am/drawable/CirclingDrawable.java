package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 外围小点转圈动图
 *
 * @author Mofer
 */
public class CirclingDrawable extends Drawable implements Animatable {

    private static final long FRAME_DURATION = 1000 / 60;
    private static final float SWEEPANGLE = 0.5f;
    private static final int COLOR = 0xff308530;
    private static final int STROKE = 4;// dp

    private final int mWidth;
    private final int mHeight;
    private final int mStroke;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mRectF = new RectF();
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final Drawable mDrawableCenter;
    private boolean mIsRunning;
    private int mColor;
    private int mProgress = 0;
    private float mAnimatAngle;

    public CirclingDrawable(float density, Drawable center) {
        this((int) Math.ceil(STROKE * density), COLOR, center);
    }

    public CirclingDrawable(int stroke, int color, Drawable center) {
        mStroke = stroke;
        mDrawableCenter = center;
        mWidth = mDrawableCenter.getIntrinsicWidth() + mStroke * 2;
        mHeight = mDrawableCenter.getIntrinsicHeight() + mStroke * 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStroke);
        mColor = color;
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
        canvas.save();
        mDrawableCenter.setBounds(mStroke, mStroke, mWidth
                - mStroke, mHeight - mStroke);
        mDrawableCenter.draw(canvas);
        if (mIsRunning) {
            canvas.rotate(-90, mWidth * 0.5f, mHeight * 0.5f);
            mPaint.setColor(mColor);
            mRectF.set(mStroke * 0.5f, mStroke * 0.5f, mWidth
                    - mStroke * 0.5f, mHeight - mStroke * 0.5f);
            canvas.drawArc(mRectF, mAnimatAngle, SWEEPANGLE, false, mPaint);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        mDrawableCenter.setAlpha(alpha);
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
                if (mProgress == 50) {
                    mProgress = 1;
                } else {
                    mProgress++;
                }
                mAnimatAngle = 360 * mInterpolator
                        .getInterpolation(mProgress * 0.02f);
                scheduleSelf(mUpdater, SystemClock.uptimeMillis()
                        + FRAME_DURATION);
                invalidateSelf();
            }
        }
    };

}
