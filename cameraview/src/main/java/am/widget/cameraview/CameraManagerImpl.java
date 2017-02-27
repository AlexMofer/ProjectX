package am.widget.cameraview;

/**
 * 摄像头管理器接口
 * Created by Alex on 2017/2/11.
 */

interface CameraManagerImpl {

    void setTimeout(long timeout);

    void openCamera(int id, boolean isForceFacing, CameraManager.OnOpenListener listener)
            throws CameraException;

    void closeCamera() throws CameraException;


    CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException;
}
