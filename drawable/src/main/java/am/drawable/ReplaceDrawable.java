package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 * 交替图片
 * Created by Alex on 2016/7/13.
 */
@SuppressWarnings("unused")
public class ReplaceDrawable extends Drawable {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private Drawable mFirst;
    private Drawable mSecond;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private int mForegroundAlpha;
    private int mBackgroundAlpha;
    private int mBackground;

    public ReplaceDrawable(Drawable first, Drawable second) {
        if (first == null || second == null)
            throw new IllegalArgumentException("Drawable is null");
        mFirst = first;
        mSecond = second;
        mIntrinsicWidth = Math.max(mFirst.getIntrinsicWidth(), mSecond.getIntrinsicWidth());
        mIntrinsicHeight = Math.max(mFirst.getIntrinsicHeight(), mSecond.getIntrinsicHeight());
        setReplace(0, 255);
        setBackground(SECOND);
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        mFirst.setBounds(left, top, right, bottom);
        mSecond.setBounds(left, top, right, bottom);
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBackground == FIRST) {
            mSecond.setAlpha(mForegroundAlpha);
            mFirst.setAlpha(mBackgroundAlpha);
            mFirst.draw(canvas);
            mSecond.draw(canvas);
        } else {
            mFirst.setAlpha(mForegroundAlpha);
            mSecond.setAlpha(mBackgroundAlpha);
            mSecond.draw(canvas);
            mFirst.draw(canvas);
        }
        mSecond.setAlpha(255);
        mFirst.setAlpha(255);
    }

    @Override
    public void setAlpha(int alpha) {
        mFirst.setAlpha(alpha);
        mSecond.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mFirst.setColorFilter(colorFilter);
        mSecond.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    /**
     * 设置交替
     *
     * @param foregroundAlpha 前景透明度
     * @param backgroundAlpha 背景透明度
     */
    public void setReplace(int foregroundAlpha, int backgroundAlpha) {
        mForegroundAlpha = foregroundAlpha;
        mBackgroundAlpha = backgroundAlpha;
        invalidateSelf();
    }

    /**
     * 设置背景
     *
     * @param background FIRST 或者 SECOND
     */
    public void setBackground(int background) {
        if ((background == FIRST || background == SECOND) && mBackground != background) {
            mBackground = background;
            invalidateSelf();
        }
    }
}
