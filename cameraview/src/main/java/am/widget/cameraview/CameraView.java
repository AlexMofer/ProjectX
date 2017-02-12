package am.widget.cameraview;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/**
 * 摄像头视图
 * Created by Alex on 2017/2/7.
 */

public class CameraView extends SurfaceView {

    public static final int CAMERA_FACING_BACK = 0;// 使用后置摄像头
    public static final int CAMERA_FACING_FRONT = 1;// 使用前置摄像头
    public static final int ERROR_CODE_OK = 0;// 无错误出现
    public static final int ERROR_CODE_OPEN_1 = 1;// 未找到摄像头设备
    public static final int ERROR_CODE_OPEN_2 = 2;// 未找到指定FACING的摄像头
    public static final int ERROR_CODE_OPEN_3 = 3;// 开启摄像头失败

    private OnCameraListener cameraListener;
    private boolean isOpen = false;
    private CameraManager cameraManager;
    private int mCameraFacing = CAMERA_FACING_BACK;// 前置/后置
    private boolean isForceFacing = false;// 是否强制使用前置/后置
    public CameraView(Context context) {
        super(context);
        initView(null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        cameraManager = new CameraManager(getContext());

        getHolder().addCallback(new CameraCallBack());
    }

    private void openCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null)
            return;// 已经销毁
        if (isOpen())
            return;// 摄像头已经打开
        if (Compat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            if (cameraListener != null)
                cameraListener.onCameraPermissionDenied(this);
            return;
        }
        int code = cameraManager.openCamera(mCameraFacing, isForceFacing);
        if (code != ERROR_CODE_OK) {
            if (cameraListener != null)
                cameraListener.onOpenCameraError(code);
            return;
        }
        isOpen = true;
    }

    private void closeCamera() {
        isOpen = false;
        cameraManager.closeCamera();
    }

    private class CameraCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            openCamera(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            closeCamera();
        }
    }

    /**
     * 设置摄像头监听
     * @param listener 监听器
     */
    public void setOnCameraListener(OnCameraListener listener) {
        cameraListener = listener;
    }

    /**
     * 打开摄像头
     */
    public void open() {
        openCamera(getHolder());
    }

    /**
     * 摄像头是否已打开
     * @return 是否已打开
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 扫描监听
     */
    public interface OnCameraListener {

        /**
         * 摄像头权限拒绝
         *
         * @param cameraView CameraView
         */
        void onCameraPermissionDenied(CameraView cameraView);

        /**
         * 打开摄像头出错
         * @param code 错误码
         */
        void onOpenCameraError(int code);
    }
}
