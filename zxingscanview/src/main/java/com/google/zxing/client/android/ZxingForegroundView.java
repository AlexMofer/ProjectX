package com.google.zxing.client.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.google.zxing.client.android.manager.ScanFeedbackManager;
import com.google.zxing.client.android.util.Compat;

/**
 * 前景视图
 * Created by Alex on 2016/11/30.
 */

public class ZxingForegroundView extends View {

    private static final int MODE_OPEN = 0;
    private static final int MODE_ERROR = 1;
    private Drawable mOpenDrawable;
    private Drawable mErrorDrawable;
    private ZxingScanView mScanView;
    private final OnScanListener scanListener = new OnScanListener();
    private final OnStateListener stateListener = new OnStateListener();
    private int mScanViewId;
    private int mode;

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
        TypedArray custom = getContext().obtainStyledAttributes(attrs,
                R.styleable.ZxingForegroundView);
        open = custom.getDrawable(R.styleable.ZxingForegroundView_zfvOpenDrawable);
        error = custom.getDrawable(R.styleable.ZxingForegroundView_zfvErrorDrawable);
        scanId = custom.getResourceId(R.styleable.ZxingForegroundView_zfvZxingScanView, scanId);
        mode = custom.getInt(R.styleable.ZxingForegroundView_zfvMode, mode);
        custom.recycle();
        mScanViewId = scanId;
        setOpenDrawable(open);
        setErrorDrawable(error);
        setMode(mode);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bindScanView();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawState(canvas);
    }

    private void drawState(Canvas canvas) {
        if (mScanView == null)
            return;
        if (mScanView.isOpen()) {
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

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO 位置
                if (mOpenDrawable != null)
                    Compat.setHotspot(mOpenDrawable, ev.getX(), ev.getY());
                if (mErrorDrawable != null)
                    Compat.setHotspot(mOpenDrawable, ev.getX(), ev.getY());
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

    private class OnScanListener implements ZxingScanView.OnScanListener {
        @Override
        public void onError(ZxingScanView scanView) {
            invalidate();
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
        public void onPrepareClose(ZxingScanView scanView) {
            invalidate();
        }

        @Override
        public void onClosed(ZxingScanView scanView) {
            invalidate();
        }
    }
}
