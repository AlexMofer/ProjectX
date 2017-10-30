package am.widget.cameraview.old;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import java.util.ArrayList;

import am.widget.cameraview.R;

/**
 * 摄像头视图
 * Created by Alex on 2017/2/7.
 */

public class CameraView extends ViewGroup {
    public static final int CAMERA_FACING_BACK = 0;// 使用后置摄像头
    public static final int CAMERA_FACING_FRONT = 1;// 使用前置摄像头
    public static final int CAMERA_FACING_EXTERNAL = 2;// 使用外置摄像头
    public static final int PREVIEW_SIZE_AT_MAX = 0;// 所有预览尺寸取最大
    public static final int PREVIEW_SIZE_AT_MIN = 1;// 所有预览尺寸取最小
    public static final int PREVIEW_SIZE_AT_MATCHED_MAX = 2;// 所有匹配的预览尺寸取最大
    public static final int PREVIEW_SIZE_AT_MATCHED_MIN = 3;// 所有匹配的预览尺寸取最小
    public static final int PREVIEW_SIZE_AT_MATCHED_CROP = 4;// 所有匹配的预览尺寸中，比视图尺寸大，但最接近
    public static final int PREVIEW_SIZE_AT_MATCHED_INSIDE = 5;// 所有匹配的预览尺寸中，比视图尺寸小，但最接近
    public static final int MIN_PIXELS_PERCENTAGE = 6;// 最小像素不得低于View尺寸的几分之一
    public static final float MAX_ASPECT_DISTORTION = 0.15f;// 最大允许的高宽比差值
    public static final int DEFAULT_OPEN_TIMEOUT = 2500;// 默认打开超时时长（仅高版本有效）
    public static final int AMBIENT_LIGHT_MODE_AUTO = 0;// 背光自动
    public static final int AMBIENT_LIGHT_MODE_OPEN = 1;// 背光开启
    public static final int AMBIENT_LIGHT_MODE_CLOSE = 2;// 背光关闭

    protected final CameraSurface mCamera;
    private final ArrayList<CameraStateCallback> callbacks = new ArrayList<>();
    private int mGravity = Gravity.CENTER;

    public CameraView(Context context) {
        super(context);
        mCamera = new CameraSurface(context);
        initView(null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamera = new CameraSurface(context);
        initView(attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCamera = new CameraSurface(context);
        initView(attrs);
    }

    @TargetApi(21)
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mCamera = new CameraSurface(context);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        mCamera.setOnCameraListener(new OnCameraListener());
        TypedArray custom = getContext().obtainStyledAttributes(attrs, R.styleable.CameraView);
        int facing = custom.getInt(R.styleable.CameraView_cvCameraFacing, CAMERA_FACING_BACK);
        boolean isForceFacing = custom.getBoolean(R.styleable.CameraView_cvForceFacing, false);
        int previewSizeMode = custom.getInt(R.styleable.CameraView_cvPreviewSizeMode,
                PREVIEW_SIZE_AT_MATCHED_INSIDE);
        int minPixelsPercentage = custom.getInt(R.styleable.CameraView_cvMinPixelsPercentage,
                MIN_PIXELS_PERCENTAGE);
        float maxAspectDistortion = custom.getFloat(R.styleable.CameraView_cvMaxAspectDistortion,
                MAX_ASPECT_DISTORTION);
        int openTimeout = custom.getInteger(R.styleable.CameraView_cvOpenTimeout,
                DEFAULT_OPEN_TIMEOUT);

        custom.recycle();
        setCameraFacing(facing);
        setForceFacing(isForceFacing);
        setPreviewSizeMode(previewSizeMode);
        setMinPixelsPercentage(minPixelsPercentage);
        setMaxAspectDistortion(maxAspectDistortion);
        setOpenTimeout(openTimeout);
        addView(mCamera);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mCamera.measure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),
                        MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
                        MeasureSpec.AT_MOST));
        final int cameraWidth = mCamera.getMeasuredWidth();
        final int cameraHeight = mCamera.getMeasuredHeight();
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int width = Math.max(cameraWidth + paddingStart + paddingEnd,
                suggestedMinimumWidth);
        final int height = Math.max(cameraHeight + paddingTop + paddingBottom,
                suggestedMinimumHeight);
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int cameraWidth = mCamera.getMeasuredWidth();
        final int cameraHeight = mCamera.getMeasuredHeight();
        int left = 0;
        int top = 0;
        switch (mGravity) {
            case Gravity.CENTER:
                left = paddingStart + (width - paddingStart - paddingEnd - cameraWidth) / 2;
                top = paddingTop + (height - paddingTop - paddingBottom - cameraHeight) / 2;
                break;
        }
        mCamera.layout(left, top, left + cameraWidth, top + cameraHeight);
        System.out.println("size--------width:" + cameraWidth + "height:" + cameraHeight);
        System.out.println("layout------left:" + left + "top:" + top);
    }

    private void notifyPermissionDenied() {
        for (CameraStateCallback callback : callbacks)
            callback.onPermissionDenied(this);
    }

    private void notifyOpened() {
        for (CameraStateCallback callback : callbacks)
            callback.onOpened(this);
    }

    private void notifyDisconnected() {
        for (CameraStateCallback callback : callbacks)
            callback.onDisconnected(this);
    }

    private void notifyError(int error, int reason) {
        for (CameraStateCallback callback : callbacks)
            callback.onError(this, error, reason);
    }

    /**
     * 打开摄像头
     */
    public void open() {
        mCamera.open();
    }

    /**
     * 关闭摄像头
     */
    @SuppressWarnings("unused")
    public void close() {
        mCamera.close();
    }

    /**
     * 摄像头是否已打开
     *
     * @return 是否已打开
     */
    public boolean isOpen() {
        return mCamera.isOpen();
    }

    /**
     * 设置摄像头类型
     *
     * @param facing 前置/后置/外置 {@link #CAMERA_FACING_BACK },
     *               {@link #CAMERA_FACING_FRONT}, {@link #CAMERA_FACING_EXTERNAL}
     */
    public void setCameraFacing(int facing) {
        mCamera.setCameraFacing(facing);
    }

    /**
     * 是否强制使用选定的摄像头类型
     *
     * @param isForce 是否强制
     */
    public void setForceFacing(boolean isForce) {
        mCamera.setForceFacing(isForce);
    }

    /**
     * 设置摄像头预览尺寸的选取模式
     *
     * @param mode 选取模式
     */
    public void setPreviewSizeMode(int mode) {
        mCamera.setPreviewSizeMode(mode);
    }

    /**
     * 设置打开超时时长
     *
     * @param timeout 时长
     */
    public void setOpenTimeout(long timeout) {
        mCamera.setOpenTimeout(timeout);
    }

    /**
     * 最小像素不得低于View尺寸的几分之一
     *
     * @param min 几分之一
     */
    public void setMinPixelsPercentage(int min) {
        mCamera.setMinPixelsPercentage(min);
    }

    /**
     * 最大允许的高宽比差值
     *
     * @param max 差值
     */
    public void setMaxAspectDistortion(double max) {
        mCamera.setMaxAspectDistortion(max);
    }

    /**
     * 添加回调
     *
     * @param callback 回调
     */
    public void addCallback(CameraStateCallback callback) {
        callbacks.add(callback);
    }

    /**
     * 移出回调
     *
     * @param callback 回调
     */
    @SuppressWarnings("unused")
    public void removeCallback(CameraStateCallback callback) {
        callbacks.remove(callback);
    }

    private class OnCameraListener implements CameraSurface.OnCameraListener {

        @Override
        public void onPermissionDenied(CameraSurface cameraView) {
            notifyPermissionDenied();
        }

        @Override
        public void onOpened(CameraSurface cameraView) {
            notifyOpened();
        }

        @Override
        public void onDisconnected(CameraSurface cameraView) {
            notifyDisconnected();
        }

        @Override
        public void onError(CameraSurface cameraView, int error, int reason) {
            notifyError(error, reason);
        }
    }
}
