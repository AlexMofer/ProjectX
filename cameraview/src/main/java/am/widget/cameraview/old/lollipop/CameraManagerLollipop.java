package am.widget.cameraview.old.lollipop;

import android.content.Context;
import android.view.SurfaceHolder;

import am.widget.cameraview.old.CameraManager;
import am.widget.cameraview.old.CameraManagerImpl;
import am.widget.cameraview.old.tool.CameraException;
import am.widget.cameraview.old.tool.CameraSetting;
import am.widget.cameraview.old.tool.CameraSize;

/**
 * L版CameraManager
 * Created by Alex on 2017/2/11.
 */
public class CameraManagerLollipop implements CameraManagerImpl {

    private final CameraOpenLollipop mOpen;
    private final CameraConfigLollipop mConfig = new CameraConfigLollipop();

    public CameraManagerLollipop(Context context, CameraManager.OnOpenListener listener) {
        mOpen = new CameraOpenLollipop(context, listener);
    }

    @Override
    public void setTimeout(long timeout) {
        mOpen.setTimeout(timeout);
    }

    @Override
    public void setMinPixelsPercentage(int min) {
        mConfig.setMinPixelsPercentage(min);
    }

    @Override
    public void setMaxAspectDistortion(double max) {
        mConfig.setMaxAspectDistortion(max);
    }

    @Override
    public void openCamera(int id, boolean isForceFacing)
            throws CameraException {
        mOpen.openCamera(id, isForceFacing);
    }

    @Override
    public void closeCamera() throws CameraException {
        mOpen.closeCamera();
    }


    @Override
    public CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return mConfig.getSize(mOpen.characteristicsSelected, maxWidth, maxHeight, mode);
    }

    @Override
    public void configCamera(Context context, SurfaceHolder holder, CameraSetting setting)
            throws CameraException {
        // TODO
    }
}
