package com.google.zxing.client.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.util.Compat;

/**
 * ZxingScanView
 * Created by alexm on 2016/11/24.
 */

public class ZxingScanView extends SurfaceView {

    public static final int ERROR_CODE_0 = 0;//开启摄像头失败
    private SurfaceHolder mHolder;
    private CameraManager mCameraManager;
    private OnScanListener mListener;

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
        mCameraManager = new CameraManager(getContext());
        getHolder().addCallback(new CameraCallBack());
    }

    private void openCamera() {
        if (mHolder == null)
            return;// 已经销毁
        if (mCameraManager.isOpen())
            return;// 摄像头已经打开
        try {
            mCameraManager.openDriver(mHolder);
            mCameraManager.startPreview();
        } catch (Exception e) {
            if (mListener != null)
                mListener.onError(ERROR_CODE_0);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    private class CameraCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mHolder = surfaceHolder;
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mHolder = null;
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
