package am.widget.cameraview;


import android.content.Context;
import android.os.Build;

/**
 * 摄像头管理器
 * Created by Alex on 2017/2/11.
 */

class CameraManager {

    private CameraManagerImpl cameraManager;

    CameraManager(Context context) {
//        cameraManager = new CameraManagerBase();
        cameraManager = new CameraManagerLollipop(context);
    }

    synchronized int openCamera(int id, boolean isForce) {
        return cameraManager.openCamera(id, isForce);
    }

    synchronized void closeCamera() {
        cameraManager.closeCamera();
    }
}
