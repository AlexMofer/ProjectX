package am.widget.cameraview.old.base;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import am.widget.cameraview.old.CameraManager;
import am.widget.cameraview.old.CameraManagerImpl;
import am.widget.cameraview.old.CameraStateCallback;
import am.widget.cameraview.old.tool.CameraException;
import am.widget.cameraview.old.tool.CameraSetting;
import am.widget.cameraview.old.tool.CameraSize;

/**
 * 低版本摄像头管理器
 * Created by Alex on 2017/2/11.
 */
@SuppressWarnings("deprecation")
public class CameraManagerBase implements CameraManagerImpl {

    private final CameraManager.OnOpenListener listener;
    private final CameraOpenBase mOpen = new CameraOpenBase();
    private final CameraConfigBase mConfig = new CameraConfigBase();

    public CameraManagerBase(CameraManager.OnOpenListener listener) {
        this.listener = listener;
    }

    @Override
    public void setTimeout(long timeout) {
        // no need
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
        if (null != listener) {
            listener.onSelected();
            listener.onOpened();
        }
    }

    @Override
    public void closeCamera() {
        mOpen.closeCamera();
    }

    @Override
    public CameraSize getSize(int maxWidth, int maxHeight, int mode) throws CameraException {
        return mConfig.getSize(mOpen.camera.getParameters(), maxWidth, maxHeight, mode);
    }

    @Override
    public void configCamera(Context context, SurfaceHolder holder, CameraSetting setting)
            throws CameraException {
        if (null == mOpen.camera)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_2);
        Camera.Parameters parameters = mOpen.camera.getParameters();
        if (null == parameters)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_2);
        // 备份参数
        final String parametersFlattened = parameters.flatten();
        try {
            mConfig.configCamera(context, mOpen.camera, setting, mOpen.selectedCameraInfo, false);
        } catch (RuntimeException e) {
            if (null != parametersFlattened) {
                parameters = mOpen.camera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    mConfig.configCamera(context, mOpen.camera, setting,
                            mOpen.selectedCameraInfo, true);
                } catch (RuntimeException e1) {
                    // No configuration
                }
            }
        }
        try {
            mOpen.camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_3);
        }
    }
}
