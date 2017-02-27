package am.widget.cameraview;

import android.hardware.Camera;

/**
 * 摄像头开启
 * Created by Alex on 2017/2/27.
 */
@SuppressWarnings("deprecation")
class CameraOpenBase {

    private int cameraId;
    private Camera.CameraInfo selectedCameraInfo;
    Camera camera;

    void openCamera(int id, boolean isForceFacing) throws CameraException{
        closeCamera();
        final int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_1);
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
            case CameraView.CAMERA_FACING_EXTERNAL:
                // 选择外置摄像头
                // api 不支持
                break;
        }
        if (selectedCameraInfo == null) {
            if (isForceFacing) {
                throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_2);
            } else {
                selectedCameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(0, selectedCameraInfo);
                cameraId = 0;
            }
        }
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_3);
        }
    }

    void closeCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        cameraId = -1;
        selectedCameraInfo = null;
    }
}
