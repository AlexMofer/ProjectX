package am.widget.cameraview.old.lollipop;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import am.widget.cameraview.old.CameraManager;
import am.widget.cameraview.old.CameraStateCallback;
import am.widget.cameraview.old.CameraView;
import am.widget.cameraview.old.tool.CameraException;

/**
 * 摄像头开启
 * Created by Alex on 2017/2/27.
 */
@TargetApi(21)
class CameraOpenLollipop {

    private final CameraManager.OnOpenListener listener;
    private final CameraDevice.StateCallback mStateCallback = new StateCallback();
    CameraCharacteristics characteristicsSelected;
    private long mTimeout = CameraView.DEFAULT_OPEN_TIMEOUT;
    private android.hardware.camera2.CameraManager manager;
    private String mCameraId;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private CameraDevice mCameraDevice;

    CameraOpenLollipop(Context context, CameraManager.OnOpenListener listener) {
        this.listener = listener;
        manager = (android.hardware.camera2.CameraManager) context
                .getSystemService(Context.CAMERA_SERVICE);
    }

    void setTimeout(long timeout) {
        mTimeout = timeout;
    }

    @SuppressWarnings("all")
    void openCamera(int id, boolean isForceFacing) throws CameraException {
        closeCamera();
        final String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_1);
        }
        if (cameraIds.length <= 0)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_1);
        switch (id) {
            case CameraView.CAMERA_FACING_BACK:
                // 选择后置摄像头
                for (String cameraId : cameraIds) {
                    final CameraCharacteristics characteristics;
                    try {
                        characteristics = manager.getCameraCharacteristics(cameraId);
                    } catch (CameraAccessException e) {
                        continue;
                    }
                    final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraId = cameraId;
                        characteristicsSelected = characteristics;
                        break;
                    }
                }
                break;

            case CameraView.CAMERA_FACING_FRONT:
                // 选择前置摄像头
                for (String cameraId : cameraIds) {
                    final CameraCharacteristics characteristics;
                    try {
                        characteristics = manager.getCameraCharacteristics(cameraId);
                    } catch (CameraAccessException e) {
                        continue;
                    }
                    final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        mCameraId = cameraId;
                        characteristicsSelected = characteristics;
                        break;
                    }
                }
                break;
            case CameraView.CAMERA_FACING_EXTERNAL:
                // 选择外置摄像头
                for (String cameraId : cameraIds) {
                    final CameraCharacteristics characteristics;
                    try {
                        characteristics = manager.getCameraCharacteristics(cameraId);
                    } catch (CameraAccessException e) {
                        continue;
                    }
                    final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
                        mCameraId = cameraId;
                        characteristicsSelected = characteristics;
                        break;
                    }
                }
                break;
        }
        if (mCameraId == null) {
            if (isForceFacing) {
                throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_2);
            } else {
                for (String cameraId : cameraIds) {
                    final CameraCharacteristics characteristics;
                    try {
                        characteristics = manager.getCameraCharacteristics(cameraId);
                    } catch (CameraAccessException e) {
                        continue;
                    }
                    mCameraId = cameraId;
                    characteristicsSelected = characteristics;
                }
                if (mCameraId == null)
                    throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_1);
            }
        }
        if (null != listener)
            listener.onSelected();
        try {
            if (!mCameraOpenCloseLock.tryAcquire(mTimeout, TimeUnit.MILLISECONDS)) {
                throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_3);
            }
            manager.openCamera(mCameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_3);
        } catch (InterruptedException e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_OPEN_3);
        }
    }

    void closeCamera() throws CameraException {
        try {
            mCameraOpenCloseLock.acquire();
//            if (null != mCaptureSession) {
//                mCaptureSession.close();
//                mCaptureSession = null;
//            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (InterruptedException e) {
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CLOSE_1);
        } finally {
            mCameraOpenCloseLock.release();
        }
        characteristicsSelected = null;
        mCameraId = null;
    }

    @SuppressWarnings("all")
    private class StateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            if (null != listener)
                listener.onOpened();
//            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    }
}
