package am.widget.cameraview.old;

import android.content.Context;
import android.view.SurfaceHolder;

import am.widget.cameraview.old.tool.CameraException;
import am.widget.cameraview.old.tool.CameraSetting;
import am.widget.cameraview.old.tool.CameraSize;

/**
 * 摄像头管理器接口
 * Created by Alex on 2017/2/11.
 */

public interface CameraManagerImpl {

    void setTimeout(long timeout);

    void setMinPixelsPercentage(int min);

    void setMaxAspectDistortion(double max);

    void openCamera(int id, boolean isForceFacing) throws CameraException;

    void closeCamera() throws CameraException;

    CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException;

    void configCamera(Context context, SurfaceHolder holder, CameraSetting setting)
            throws CameraException;
}
