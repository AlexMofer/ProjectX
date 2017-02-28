package am.widget.cameraview;

import android.content.Context;
import android.view.SurfaceHolder;

/**
 * 摄像头管理器接口
 * Created by Alex on 2017/2/11.
 */

interface CameraManagerImpl {

    void setTimeout(long timeout);

    void setMinPixelsPercentage(int min);

    void setMaxAspectDistortion(double max);

    void openCamera(int id, boolean isForceFacing) throws CameraException;

    void closeCamera() throws CameraException;

    CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException;

    void configCamera(Context context, SurfaceHolder holder, CameraSetting setting)
            throws CameraException;
}
