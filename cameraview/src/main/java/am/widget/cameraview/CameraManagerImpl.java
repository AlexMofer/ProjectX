package am.widget.cameraview;

/**
 * 摄像头管理器接口
 * Created by Alex on 2017/2/11.
 */

interface CameraManagerImpl {

    int openCamera(int id, boolean isForce);

    void closeCamera();

}
