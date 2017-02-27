package am.widget.cameraview;

/**
 * 低版本摄像头管理器
 * Created by Alex on 2017/2/11.
 */
@SuppressWarnings("deprecation")
class CameraManagerBase implements CameraManagerImpl {

    private final CameraOpenBase mOpen = new CameraOpenBase();
    private final CameraConfigBase mConfig = new CameraConfigBase();

    @Override
    public void setTimeout(long timeout) {
        // no need
    }

    @Override
    public void openCamera(int id, boolean isForceFacing, CameraManager.OnOpenListener listener)
            throws CameraException {
        mOpen.openCamera(id, isForceFacing);
        listener.onOpened();
    }

    @Override
    public void closeCamera() {
        mOpen.closeCamera();
    }

    @Override
    public CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return mConfig.getSize(mOpen.camera.getParameters(), maxWidth, maxHeight, mode);
    }
}
