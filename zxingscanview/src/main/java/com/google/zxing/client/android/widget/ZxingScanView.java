package com.google.zxing.client.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.client.android.camera.CameraManager;
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
        // TODO 灯光模式

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        mAmbientLightManager = new AmbientLightManager(getContext(), new AmbientLightCallBack());
        mScanFeedbackManager = new ScanFeedbackManager(getContext());
        getHolder().addCallback(new CameraCallBack());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAmbientLightManager.release();
        mScanFeedbackManager.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAmbientLightManager.setMode(AmbientLightManager.MODE_CLOSE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAmbientLightManager.setMode(AmbientLightManager.MODE_OPEN);
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
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            closeDriver();
        }
    }

    private void openDriver(SurfaceHolder surfaceHolder) {
        mCameraManager = new CameraManager(getContext());
        if (surfaceHolder == null)
            return;// 已经销毁
        if (mCameraManager.isOpen())
            return;// 摄像头已经打开
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

    public void setOnScanListener(OnScanListener listener) {
        mListener = listener;
    }

    public interface OnScanListener {
        void onError(int errorCode);
    }

    /**
     * 检查权限
     *
     * @param permission 权限
     * @return 是否拥有权限
     */
    private boolean lacksPermission(String permission) {
        return Compat.checkSelfPermission(getContext(), permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
