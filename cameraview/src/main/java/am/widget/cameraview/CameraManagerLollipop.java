package am.widget.cameraview;

import android.content.Context;

/**
 * L版CameraManager
 * Created by Alex on 2017/2/11.
 */
class CameraManagerLollipop implements CameraManagerImpl {

    private final CameraOpenLollipop mOpen;
    private final CameraConfigLollipop mConfig = new CameraConfigLollipop();

    CameraManagerLollipop(Context context) {
        mOpen = new CameraOpenLollipop(context);
    }

    @Override
    public void setTimeout(long timeout) {
        mOpen.setTimeout(timeout);
    }

    @Override
    public void openCamera(int id, boolean isForceFacing, CameraManager.OnOpenListener listener)
            throws CameraException {
        mOpen.openCamera(id, isForceFacing, listener);
    }

    @Override
    public void closeCamera() throws CameraException {
        mOpen.closeCamera();
    }


    @Override
    public CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return mConfig.getSize(mOpen.characteristicsSelected, maxWidth, maxHeight, mode);
    }
}
