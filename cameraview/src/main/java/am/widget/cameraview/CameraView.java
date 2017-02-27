package am.widget.cameraview;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import java.util.ArrayList;

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

        // TODO
        mCamera.setCameraFacing(CAMERA_FACING_BACK);
        mCamera.setForceFacing(false);
        mCamera.setPreviewSizeMode(PREVIEW_SIZE_AT_MATCHED_INSIDE);
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
     * 设置打开超时时长
     *
     * @param timeout 时长
     */
    public void setOpenTimeout(long timeout) {
        mCamera.setOpenTimeout(timeout);
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
