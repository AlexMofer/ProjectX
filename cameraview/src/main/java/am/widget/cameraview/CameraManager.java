package am.widget.cameraview;


import android.content.Context;

/**
 * 摄像头管理器
 * Created by Alex on 2017/2/11.
 */

class CameraManager {

    private CameraManagerImpl cameraManager;

    CameraManager(Context context) {
        cameraManager = new CameraManagerBase();
//        cameraManager = new CameraManagerLollipop(context);
    }

    void setTimeout(long timeout) {
        cameraManager.setTimeout(timeout);
    }

    synchronized void openCamera(int id, boolean isForceFacing, OnOpenListener listener) throws CameraException {
        cameraManager.openCamera(id, isForceFacing, listener);
    }

    synchronized void closeCamera() throws CameraException {
        cameraManager.closeCamera();
    }

    synchronized CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return cameraManager.getSize(maxWidth, maxHeight, mode);
    }

    interface OnOpenListener {
        /**
         * 打开
         */
        void onOpened();
    }
}
