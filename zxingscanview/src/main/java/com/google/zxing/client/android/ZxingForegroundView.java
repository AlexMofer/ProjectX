package com.google.zxing.client.android;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.util.Compat;

/**
 * 前景视图
 * Created by Alex on 2016/11/30.
 */

public class ZxingForegroundView extends View {

    public static final int MODE_OPEN = 0;
    public static final int MODE_ERROR = 1;
    private Drawable mOpenDrawable;
    private Drawable mErrorDrawable;
    private ZxingScanView mScanView;
    private final OnScanListener scanListener = new OnScanListener();
    private final OnStateListener stateListener = new OnStateListener();
    private int mScanViewId;
    private int mode;
    private int mCoverColor;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mCoverRect = new Rect();
    private Drawable mScanRectDrawable;
    private Drawable mScanFlagDrawable;
    private final ValueAnimator mLoadingAnimator = ValueAnimator.ofFloat(0f, 1f);// 载入动画
    private float mOffset = 0;

    public ZxingForegroundView(Context context) {
        super(context);
        initView(null);
    }

    public ZxingForegroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ZxingForegroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    public ZxingForegroundView(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        Drawable open;
        Drawable error;
        int scanId = 0;
        int mode = MODE_OPEN;
        int coverColor = 0x80000000;
        Drawable scanRect;
        Drawable scanFlag;
        int duration = 2000;
        int repeatMode = 1;
        TypedArray custom = getContext().obtainStyledAttributes(attrs,
                R.styleable.ZxingForegroundView);
        open = custom.getDrawable(R.styleable.ZxingForegroundView_zfvOpenDrawable);
        error = custom.getDrawable(R.styleable.ZxingForegroundView_zfvErrorDrawable);
        scanId = custom.getResourceId(R.styleable.ZxingForegroundView_zfvZxingScanView, scanId);
        mode = custom.getInt(R.styleable.ZxingForegroundView_zfvMode, mode);
        coverColor = custom.getColor(R.styleable.ZxingForegroundView_zfvCoverColor, coverColor);
        scanRect = custom.getDrawable(R.styleable.ZxingForegroundView_zfvScanRectDrawable);
        scanFlag = custom.getDrawable(R.styleable.ZxingForegroundView_zfvScanFlagDrawable);
        duration = custom.getInteger(R.styleable.ZxingForegroundView_zfvFlagAnimatorDuration,
                duration);
        repeatMode = custom.getInt(R.styleable.ZxingForegroundView_zfvFlagAnimatorRepeatMode,
                repeatMode);
        custom.recycle();
        mScanViewId = scanId;
        setOpenDrawable(open);
        setErrorDrawable(error);
        setMode(mode);
        setCoverColor(coverColor);
        setScanRectDrawable(scanRect);
        setScanFlagDrawable(scanFlag);
        setFlagAnimatorDuration(duration);
        if (repeatMode == 1) {
            setFlagAnimatorRepeatMode(ValueAnimator.RESTART);
        } else {
            setFlagAnimatorRepeatMode(ValueAnimator.REVERSE);
        }
        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mOffset = (float) animator.getAnimatedValue();
                if (mScanFlagDrawable != null)
                    invalidate();
            }
        });
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bindScanView();
        start();
    }

    private void bindScanView() {
        if (mScanViewId == 0)
            return;
        ViewParent parent = getParent();
        if (parent != null && parent instanceof View) {
            View vParent = (View) parent;
            View child = vParent.findViewById(mScanViewId);
            if (child != null && child instanceof ZxingScanView) {
                bindScanView((ZxingScanView) child);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        end();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawState(canvas);
    }

    private void drawState(Canvas canvas) {
        if (mScanView == null)
            return;
        if (isInEditMode() || mScanView.isOpen()) {
            drawScan(canvas);
            return;
        }
        if (mScanView.getErrorCode() == ZxingScanView.ERROR_CODE_NULL) {
            drawOpen(canvas);
        } else {
            switch (mode) {
                default:
                case MODE_OPEN:
                    drawOpen(canvas);
                    break;
                case MODE_ERROR:
                    drawError(canvas);
                    break;
            }
        }
    }

    private void drawOpen(Canvas canvas) {
        if (mOpenDrawable == null)
            return;
        final int width = mOpenDrawable.getIntrinsicWidth();
        final int height = mOpenDrawable.getIntrinsicHeight();
        mOpenDrawable.setBounds(0, 0, width, height);
        final float xMove = (getWidth() - width) * 0.5f;
        final float yMove = (getHeight() - height) * 0.5f;
        canvas.save();
        canvas.translate(xMove, yMove);
        mOpenDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawError(Canvas canvas) {
        if (mErrorDrawable == null)
            return;
        final int width = mErrorDrawable.getIntrinsicWidth();
        final int height = mErrorDrawable.getIntrinsicHeight();
        mErrorDrawable.setBounds(0, 0, width, height);
        final float xMove = (getWidth() - width) * 0.5f;
        final float yMove = (getHeight() - height) * 0.5f;
        canvas.save();
        canvas.translate(xMove, yMove);
        mErrorDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawScan(Canvas canvas) {
        final int scanWidth = mScanView.getScanWidth() > 0 ?
                (mScanView.getScanWidth() > getWidth() ? getWidth() : mScanView.getScanWidth())
                : getWidth();
        final int scanHeight = mScanView.getScanHeight() > 0 ?
                (mScanView.getScanHeight() > getHeight() ? getHeight() : mScanView.getScanHeight())
                : getHeight();
        final int coverX = (getWidth() - scanWidth) / 2;
        final int coverY = (getHeight() - scanHeight) / 2;
        mPaint.setColor(mCoverColor);
        if (coverX > 0 && coverY > 0) {
            mCoverRect.set(0, 0, getWidth(), coverY);
            canvas.drawRect(mCoverRect, mPaint);
            mCoverRect.set(0, getHeight() - coverY, getWidth(), getHeight());
            canvas.drawRect(mCoverRect, mPaint);
            mCoverRect.set(0, coverY, coverX, getHeight() - coverY);
            canvas.drawRect(mCoverRect, mPaint);
            mCoverRect.set(getWidth() - coverX, coverY, getWidth(), getHeight() - coverY);
            canvas.drawRect(mCoverRect, mPaint);
        } else if (coverX > 0) {
            mCoverRect.set(0, 0, coverX, getHeight());
            canvas.drawRect(mCoverRect, mPaint);
            mCoverRect.set(getWidth() - coverX, 0, getWidth(), getHeight());
            canvas.drawRect(mCoverRect, mPaint);
        } else if (coverY > 0) {
            mCoverRect.set(0, 0, getWidth(), coverY);
            canvas.drawRect(mCoverRect, mPaint);
            mCoverRect.set(0, getHeight() - coverY, getWidth(), getHeight());
            canvas.drawRect(mCoverRect, mPaint);
        }
        if (mScanRectDrawable != null) {
            mScanRectDrawable.setBounds(0, 0, scanWidth, scanHeight);
            canvas.save();
            canvas.translate(coverX, coverY);
            mScanRectDrawable.draw(canvas);
            canvas.restore();
        }
        drawScanPoint(canvas);
        drawScanFlag(canvas, mScanFlagDrawable, scanWidth, scanHeight, mOffset);
    }

    private void drawScanPoint(Canvas canvas) {

    }

    /**
     * 绘制扫描标志
     *
     * @param canvas     画布
     * @param drawable   扫描图
     * @param scanWidth  扫描区域宽
     * @param scanHeight 扫描区域高
     * @param offset     动画偏移值
     */
    protected void drawScanFlag(Canvas canvas, Drawable drawable, int scanWidth, int scanHeight, float offset) {
        final int coverX = (getWidth() - scanWidth) / 2;
        final int coverY = (getHeight() - scanHeight) / 2;
        if (drawable != null) {
            drawable.setBounds(0, 0, scanWidth, drawable.getIntrinsicHeight());
            canvas.save();
            canvas.translate(coverX, coverY);
            canvas.translate(0, (scanHeight - drawable.getIntrinsicHeight()) * offset);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final float x = ev.getX();
                final float y = ev.getY();
                if (mOpenDrawable != null) {
                    final int width = mOpenDrawable.getIntrinsicWidth();
                    final int height = mOpenDrawable.getIntrinsicHeight();
                    final float offsetX = (getWidth() - width) * 0.5f;
                    final float offsetY = (getHeight() - height) * 0.5f;
                    final float dX = x - offsetX;
                    final float dY = y - offsetY;
                    if (dX >= 0 && dX <= width && dY >= 0 && dY <= height)
                        Compat.setHotspot(mOpenDrawable, ev.getX(), ev.getY());
                }
                if (mErrorDrawable != null) {
                    final int width = mErrorDrawable.getIntrinsicWidth();
                    final int height = mErrorDrawable.getIntrinsicHeight();
                    final float offsetX = (getWidth() - width) * 0.5f;
                    final float offsetY = (getHeight() - height) * 0.5f;
                    final float dX = x - offsetX;
                    final float dY = y - offsetY;
                    if (dX >= 0 && dX <= width && dY >= 0 && dY <= height)
                        Compat.setHotspot(mErrorDrawable, ev.getX(), ev.getY());
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    @Override
    protected void drawableStateChanged() {
        if (mOpenDrawable != null && mOpenDrawable.isStateful()) {
            mOpenDrawable.setState(getDrawableState());
        }
        if (mErrorDrawable != null && mErrorDrawable.isStateful()) {
            mErrorDrawable.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    @Override
    @SuppressWarnings("all")
    protected boolean verifyDrawable(Drawable who) {
        if (mOpenDrawable == null && mErrorDrawable == null)
            return super.verifyDrawable(who);
        return who == mOpenDrawable || who == mErrorDrawable || super.verifyDrawable(who);
    }

    /**
     * 设置开启图片
     *
     * @param drawable 开启图片
     */
    public void setOpenDrawable(Drawable drawable) {
        if (mOpenDrawable == drawable)
            return;
        if (mOpenDrawable != null) {
            if (mOpenDrawable instanceof Animatable)
                ((Animatable) mOpenDrawable).stop();
            mOpenDrawable.setCallback(null);
        }
        mOpenDrawable = drawable;
        if (mOpenDrawable != null) {
            mOpenDrawable.setCallback(this);
            if (mOpenDrawable instanceof Animatable) {
                Animatable animatable = (Animatable) mOpenDrawable;
                if (!animatable.isRunning())
                    animatable.start();
            }
        }
        invalidate();
    }

    /**
     * 设置错误图片
     *
     * @param drawable 错误图片
     */
    public void setErrorDrawable(Drawable drawable) {
        if (mErrorDrawable == drawable)
            return;
        if (mErrorDrawable != null) {
            if (mErrorDrawable instanceof Animatable)
                ((Animatable) mErrorDrawable).stop();
            mErrorDrawable.setCallback(null);
        }
        mErrorDrawable = drawable;
        if (mErrorDrawable != null) {
            mErrorDrawable.setCallback(this);
            if (mErrorDrawable instanceof Animatable) {
                Animatable animatable = (Animatable) mErrorDrawable;
                if (!animatable.isRunning())
                    animatable.start();
            }
        }
        invalidate();
    }

    /**
     * 绑定扫描视图
     *
     * @param view 扫描视图
     */
    public void bindScanView(ZxingScanView view) {
        if (mScanView == view)
            return;
        if (mScanView != null) {
            mScanView.removeOnScanListener(scanListener);
            mScanView.removeOnStateListener(stateListener);
        }
        mScanView = view;
        if (mScanView != null) {
            mScanView.addOnScanListener(scanListener);
            mScanView.addOnStateListener(stateListener);
        }
        invalidate();
    }

    /**
     * 设置模式
     *
     * @param mode 模式，可用参数：{@link ZxingForegroundView#MODE_OPEN}、
     *             {@link ZxingForegroundView#MODE_ERROR}
     */
    public void setMode(int mode) {
        if (mode != MODE_OPEN && mode != MODE_ERROR)
            return;
        this.mode = mode;
        invalidate();
    }

    /**
     * 设置覆盖颜色
     *
     * @param color 颜色
     */
    public void setCoverColor(int color) {
        if (mCoverColor == color)
            return;
        mCoverColor = color;
        invalidate();
    }

    /**
     * 设置扫描区域背景图
     *
     * @param drawable 背景图
     */
    public void setScanRectDrawable(Drawable drawable) {
        if (mScanRectDrawable == drawable)
            return;
        mScanRectDrawable = drawable;
        invalidate();
    }

    /**
     * 设置扫描标志图
     *
     * @param drawable 图
     */
    public void setScanFlagDrawable(Drawable drawable) {
        if (mScanFlagDrawable == drawable)
            return;
        mScanFlagDrawable = drawable;
        invalidate();
    }

    /**
     * 开始动画
     */
    public void start() {
        mLoadingAnimator.start();
    }

    /**
     * 结束动画
     */
    public void end() {
        mLoadingAnimator.end();
    }

    /**
     * 设置标志动画时长
     *
     * @param duration 时长
     */
    public void setFlagAnimatorDuration(long duration) {
        mLoadingAnimator.setDuration(duration);
    }

    /**
     * 设置标志动画循环模式
     *
     * @param mode 循环模式，可用参数：{@link ValueAnimator#RESTART}、
     *             {@link ValueAnimator#REVERSE}
     */
    public void setFlagAnimatorRepeatMode(int mode) {
        mLoadingAnimator.setRepeatMode(mode);
    }

    private class OnScanListener implements ZxingScanView.OnScanListener {
        @Override
        public void onError(ZxingScanView scanView) {
            invalidate();
        }

        @Override
        public void onResult(ZxingScanView scanView, Result result, Bitmap barcode,
                             float scaleFactor) {
            // do nothing
        }
    }

    private class OnStateListener implements ZxingScanView.OnStateListener {
        @Override
        public void onPrepareOpen(ZxingScanView scanView) {
            invalidate();
        }

        @Override
        public void onOpened(ZxingScanView scanView) {
            invalidate();
        }

        @Override
        public void foundPossibleResultPoint(ZxingScanView scanView, ResultPoint point) {
            // TODO
        }

        @Override
        public void onPrepareClose(ZxingScanView scanView) {
            invalidate();
        }

        @Override
        public void onClosed(ZxingScanView scanView) {
            invalidate();
        }
    }
}
