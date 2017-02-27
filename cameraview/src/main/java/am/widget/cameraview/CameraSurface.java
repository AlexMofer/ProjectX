package am.widget.cameraview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 摄像头视图
 * Created by Alex on 2017/2/7.
 */

class CameraSurface extends SurfaceView {

    private CameraManager cameraManager;
    private OnCameraListener cameraListener;
    private boolean isOpen = false;
    private boolean isSizeFixed = false;
    private int mCameraFacing;// 前置/后置/外置
    private boolean isForceFacing = false;// 是否强制摄像头类型
    private final CameraManager.OnOpenListener openListener = new OnOpenListener();// 开启监听
    private int mMaxWidth;// View可拥有的最大宽度
    private int mMaxHeight;// View可拥有的最大高度
    private CameraSize mSize;// 摄像头需要的尺寸
    private int mPreviewSizeMode;// 预览模式

    CameraSurface(Context context) {
        super(context);
        cameraManager = new CameraManager(getContext());
        getHolder().addCallback(new CameraCallBack());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
        mMaxHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mSize == null) {
            isSizeFixed = false;
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST));
        } else {
            isSizeFixed = true;
            final int width = mMaxWidth > mMaxHeight ? mSize.getWidth() : mSize.getHeight();
            final int height = mMaxWidth > mMaxHeight ? mSize.getHeight() : mSize.getWidth();
            setMeasuredDimension(width, height);
        }

    }

    private void openCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null)
            return;// 已经销毁
        if (isOpen())
            return;// 摄像头已经打开
        if (Compat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            if (cameraListener != null)
                cameraListener.onPermissionDenied(this);
            return;
        }
        try {
            cameraManager.openCamera(mCameraFacing, isForceFacing, openListener);
        } catch (CameraException e) {
            if (cameraListener != null)
                cameraListener.onError(this, e.getCode(), e.getReason());
            return;
        }
        CameraSize newSize;
        try {
            newSize = cameraManager.getSize(mMaxWidth, mMaxHeight, mPreviewSizeMode);
        } catch (CameraException e) {
            if (cameraListener != null)
                cameraListener.onError(CameraSurface.this, e.getCode(), e.getReason());
            closeCamera();
            return;
        }
        if (!newSize.equals(mSize)) {
            // 修改View尺寸，调整高宽比
            mSize = newSize;
            final int width = mMaxWidth > mMaxHeight ? mSize.getWidth() : mSize.getHeight();
            final int height = mMaxWidth > mMaxHeight ? mSize.getHeight() : mSize.getWidth();
            getHolder().setFixedSize(width, height);
        }
    }

    private void previewCamera() {
        if (!isOpen)
            return;
        if (!isSizeFixed)
            return;
        System.out.println("lalala---------------------------------previewCamera" + getHeight());
    }

    private void closeCamera() {
        if (!isOpen)
            return;
        isOpen = false;
        try {
            cameraManager.closeCamera();
        } catch (CameraException e) {
            if (cameraListener != null)
                cameraListener.onError(this, e.getCode(), e.getReason());
        }
    }

    private class CameraCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            openCamera(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            previewCamera();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            closeCamera();
        }
    }

    private class OnOpenListener implements CameraManager.OnOpenListener {

        @Override
        public void onOpened() {
            isOpen = true;
            previewCamera();
        }
    }

    void setOnCameraListener(OnCameraListener listener) {
        cameraListener = listener;
    }

    void open() {
        openCamera(getHolder());
    }

    void close() {
        closeCamera();
    }

    boolean isOpen() {
        return isOpen;
    }

    void setOpenTimeout(long timeout) {
        cameraManager.setTimeout(timeout);
    }

    void setCameraFacing(int facing) {
        if (mCameraFacing == facing)
            return;
        mCameraFacing = facing;
        if (isOpen) {
            close();
            open();
        }
    }

    void setForceFacing(boolean isForce) {
        if (isForceFacing == isForce)
            return;
        isForceFacing = isForce;
        if (isOpen) {
            close();
            open();
        }
    }

    void setPreviewSizeMode(int mode) {
        if (mPreviewSizeMode == mode)
            return;
        mPreviewSizeMode = mode;
        if (isOpen) {
            close();
            open();
        }
    }

    interface OnCameraListener {

        void onPermissionDenied(CameraSurface cameraView);

        void onOpened(CameraSurface cameraView);

        void onDisconnected(CameraSurface cameraView);

        void onError(CameraSurface cameraView, int error, int reason);
    }
}
