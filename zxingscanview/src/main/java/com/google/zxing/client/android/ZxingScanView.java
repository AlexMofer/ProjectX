package com.google.zxing.client.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import com.google.zxing.client.android.manager.AmbientLightManager;
import com.google.zxing.client.android.manager.ScanFeedbackManager;
import com.google.zxing.client.android.util.Compat;

/**
 * ZxingScanView
 * Created by Alex on 2016/11/24.
 */

public class ZxingScanView extends SurfaceView {

    public static final int ERROR_CODE_0 = 0;//开启摄像头失败
    private CameraManager mCameraManager;
    private OnScanListener mListener;
    private AmbientLightManager mAmbientLightManager;
    private ScanFeedbackManager mScanFeedbackManager;
    private int mScanWidth;
    private int mScanHeight;
    private int mCameraId;
    private Drawable mOpenDrawable;

    public ZxingScanView(Context context) {
        super(context);
        initView(null);
    }

    public ZxingScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ZxingScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    public ZxingScanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        int mode = AmbientLightManager.MODE_AUTO;
        int feedback = ScanFeedbackManager.MODE_AUTO;
        String fileName;
        int rawId = NO_ID;
        int scanWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int scanHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        int cameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;
        int milliseconds = ScanFeedbackManager.DEFAUT_MILLISECONDS;
        Drawable open;
        TypedArray custom = getContext().obtainStyledAttributes(attrs, R.styleable.ZxingScanView);
        mode = custom.getInt(R.styleable.ZxingScanView_zsvAmbientLight, mode);
        feedback = custom.getInt(R.styleable.ZxingScanView_zsvFeedback, feedback);
        fileName = custom.getString(R.styleable.ZxingScanView_zsvAudioAssetsFileName);
        rawId = custom.getResourceId(R.styleable.ZxingScanView_zsvAudioRaw, rawId);
        milliseconds = custom.getInteger(R.styleable.ZxingScanView_zsvVibrateMilliseconds,
                milliseconds);
        scanWidth = custom.getLayoutDimension(R.styleable.ZxingScanView_zsvScanWidth, scanWidth);
        scanHeight = custom.getLayoutDimension(R.styleable.ZxingScanView_zsvScanHeight, scanHeight);
        cameraId = custom.getInteger(R.styleable.ZxingScanView_zsvCameraId, cameraId);
        open = custom.getDrawable(R.styleable.ZxingScanView_zsvOpenDrawable);
        custom.recycle();
        setScanWidth(scanWidth);
        setScanHeight(scanHeight);
        setCameraId(cameraId);
        setOpenDrawable(open);
        // TODO

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        if (isInEditMode())
            return;
        mAmbientLightManager = new AmbientLightManager(getContext(),
                new AmbientLightCallBack(), mode);
        mScanFeedbackManager = new ScanFeedbackManager(getContext());
        setFeedbackMode(feedback);
        setFeedbackAudioAssetsFileName(fileName);
        setFeedbackAudioRawId(rawId);
        setFeedbackVibrateMilliseconds(milliseconds);
        getHolder().addCallback(new CameraCallBack());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOpen(canvas);
    }

    private void drawOpen(Canvas canvas) {
        if (isOpen())
            return;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAmbientLightManager.release();
        mScanFeedbackManager.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOpenDrawable != null)
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
        super.drawableStateChanged();
    }

    @Override
    @SuppressWarnings("all")
    protected boolean verifyDrawable(Drawable who) {
        if (mOpenDrawable == null)
            return super.verifyDrawable(who);
        return who == mOpenDrawable || super.verifyDrawable(who);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                setAmbientLightMode(AmbientLightManager.MODE_CLOSE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                setAmbientLightMode(AmbientLightManager.MODE_OPEN);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class CameraCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            openDriver(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mCameraManager != null) {
                final int scanWidth = mScanWidth == ViewGroup.LayoutParams.MATCH_PARENT ? width :
                        (mScanWidth > width ? width : mScanWidth);
                final int scanHeight = mScanHeight == ViewGroup.LayoutParams.MATCH_PARENT ? height :
                        (mScanHeight > height ? height : mScanHeight);
                mCameraManager.setManualFramingRect(scanWidth, scanHeight);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            closeDriver();
        }
    }

    private void openDriver(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null)
            return;// 已经销毁
        if (mCameraManager != null && mCameraManager.isOpen())
            return;// 摄像头已经打开
        mCameraManager = new CameraManager(getContext());
        if (mCameraId != OpenCameraInterface.NO_REQUESTED_CAMERA)
            mCameraManager.setManualCameraId(mCameraId);
        final int width = mScanWidth == ViewGroup.LayoutParams.MATCH_PARENT ? getWidth() :
                (mScanWidth > getWidth() ? getWidth() : mScanWidth);
        final int height = mScanHeight == ViewGroup.LayoutParams.MATCH_PARENT ? getHeight() :
                (mScanHeight > getHeight() ? getHeight() : mScanHeight);
        mCameraManager.setManualFramingRect(width, height);
        try {
            mCameraManager.openDriver(surfaceHolder);
            mCameraManager.startPreview();
        } catch (Exception e) {
            if (mListener != null)
                mListener.onError(ERROR_CODE_0);
            return;
        }
        mAmbientLightManager.resume();
    }

    public boolean isOpen() {
        return getHolder().isCreating() && mCameraManager != null && mCameraManager.isOpen();
    }

    private void closeDriver() {
        mCameraManager.stopPreview();
        mAmbientLightManager.pause();
        mCameraManager.closeDriver();
        mCameraManager = null;
    }

    private class AmbientLightCallBack implements AmbientLightManager.AmbientLightCallBack {
        @Override
        public void onChange(boolean on) {
            if (mCameraManager != null)
                mCameraManager.setTorch(on);
        }
    }

    /**
     * 设置扫描监听
     *
     * @param listener 监听器
     */
    @SuppressWarnings("unused")
    public void setOnScanListener(OnScanListener listener) {
        mListener = listener;
    }

    /**
     * 设置背光模式
     *
     * @param mode 背光模式，可用参数：{@link AmbientLightManager#MODE_AUTO}、
     *             {@link AmbientLightManager#MODE_OPEN}、{@link AmbientLightManager#MODE_CLOSE}
     */
    public void setAmbientLightMode(int mode) {
        mAmbientLightManager.setMode(mode);
    }

    /**
     * 设置扫描反馈模式
     *
     * @param mode 扫描反馈模式，可用参数：{@link ScanFeedbackManager#MODE_AUTO}、
     *             {@link ScanFeedbackManager#MODE_AUDIO_ONLY}、
     *             {@link ScanFeedbackManager#MODE_VIBRATOR_ONLY}、
     *             {@link ScanFeedbackManager#MODE_AUDIO_VIBRATOR}
     */
    public void setFeedbackMode(int mode) {
        mScanFeedbackManager.setMode(mode);
    }

    /**
     * 设置音频Assets文件名
     *
     * @param fileName 文件名
     */
    public void setFeedbackAudioAssetsFileName(String fileName) {
        mScanFeedbackManager.setAudioAssetsFileName(fileName);
    }

    /**
     * 设置音频资源ID
     *
     * @param id 资源ID
     */
    public void setFeedbackAudioRawId(int id) {
        mScanFeedbackManager.setAudioRawId(id);
    }

    /**
     * 设置震动时长
     *
     * @param milliseconds 时长
     */
    public void setFeedbackVibrateMilliseconds(long milliseconds) {
        mScanFeedbackManager.setVibrateMilliseconds(milliseconds);
    }

    /**
     * 设置扫描宽度
     * 下次创建CameraManager时生效
     *
     * @param width 扫描宽度
     */
    public void setScanWidth(int width) {
        mScanWidth = width;
    }

    /**
     * 设置扫描高度
     * 下次创建CameraManager时生效
     *
     * @param height 扫描高度
     */
    public void setScanHeight(int height) {
        mScanHeight = height;
    }

    /**
     * 设置摄像头ID
     * 下次创建CameraManager时生效
     *
     * @param id 摄像头ID
     */
    public void setCameraId(int id) {
        mCameraId = id;
    }

    /**
     * 设置开启动画
     *
     * @param drawable Drawable
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
            if (mOpenDrawable instanceof Animatable)
                ((Animatable) mOpenDrawable).start();
        }
    }

    /**
     * 扫描监听
     */
    public interface OnScanListener {
        /**
         * 出现错误
         *
         * @param errorCode 错误代码
         */
        void onError(int errorCode);
    }
}
