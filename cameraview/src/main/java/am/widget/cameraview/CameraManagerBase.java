package am.widget.cameraview;

import android.hardware.Camera;

/**
 * 低版本摄像头管理器
 * Created by Alex on 2017/2/11.
 */
@SuppressWarnings("deprecation")
class CameraManagerBase implements CameraManagerImpl {

    private int cameraId;
    private Camera.CameraInfo selectedCameraInfo;
    private Camera camera;

    @Override
    public int openCamera(int id, boolean isForce) {
        closeCamera();
        final int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return CameraView.ERROR_CODE_OPEN_1;
        }
        switch (id) {
            case CameraView.CAMERA_FACING_BACK:
                // 选择后置摄像头
                for (int i = 0; i < numCameras; i++) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        selectedCameraInfo = cameraInfo;
                        cameraId = i;
                        break;
                    }
                }
                break;

            case CameraView.CAMERA_FACING_FRONT:
                // 选择前置摄像头
                for (int i = 0; i < numCameras; i++) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        selectedCameraInfo = cameraInfo;
                        cameraId = i;
                        break;
                    }
                }
                break;
        }
        if (selectedCameraInfo == null) {
            if (isForce) {
                return CameraView.ERROR_CODE_OPEN_2;
            } else {
                selectedCameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(0, selectedCameraInfo);
                cameraId = 0;
            }
        }
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            return CameraView.ERROR_CODE_OPEN_3;
        }

        return CameraView.ERROR_CODE_OK;
    }

    @Override
    public void closeCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        cameraId = -1;
        selectedCameraInfo = null;
    }
}
