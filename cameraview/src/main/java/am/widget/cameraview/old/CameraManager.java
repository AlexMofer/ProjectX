package am.widget.cameraview.old;


import android.content.Context;
import android.view.SurfaceHolder;

import am.widget.cameraview.old.base.CameraManagerBase;
import am.widget.cameraview.old.tool.CameraException;
import am.widget.cameraview.old.tool.CameraSetting;
import am.widget.cameraview.old.tool.CameraSize;

/**
 * 摄像头管理器
 * Created by Alex on 2017/2/11.
 */

public class CameraManager {

    private CameraManagerImpl cameraManager;

    CameraManager(Context context, OnOpenListener listener) {
        cameraManager = new CameraManagerBase(listener);
//        cameraManager = new CameraManagerLollipop(context, listener);
    }

    void setTimeout(long timeout) {
        cameraManager.setTimeout(timeout);
    }

    void setMinPixelsPercentage(int min) {
        cameraManager.setMinPixelsPercentage(min);
    }

    void setMaxAspectDistortion(double max) {
        cameraManager.setMaxAspectDistortion(max);
    }

    synchronized void openCamera(int id, boolean isForceFacing) throws CameraException {
        cameraManager.openCamera(id, isForceFacing);
    }

    synchronized void closeCamera() throws CameraException {
        cameraManager.closeCamera();
    }

    synchronized CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return cameraManager.getSize(maxWidth, maxHeight, mode);
    }

    synchronized void configCamera(Context context, SurfaceHolder holder, CameraSetting setting)
            throws CameraException {
        cameraManager.configCamera(context, holder, setting);
    }

    public interface OnOpenListener {
        void onSelected();

        void onOpened();
    }
}
