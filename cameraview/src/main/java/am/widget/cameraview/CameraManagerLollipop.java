package am.widget.cameraview;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * L版CameraManager
 * Created by Alex on 2017/2/11.
 */
@TargetApi(21)
class CameraManagerLollipop implements CameraManagerImpl {

    private android.hardware.camera2.CameraManager manager;
    private String mCameraId;
    private CameraCharacteristics characteristicsSelected;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private CameraDevice mCameraDevice;
    private final CameraDevice.StateCallback mStateCallback = new CameraStateCallback();

    CameraManagerLollipop(Context context) {
        manager = (android.hardware.camera2.CameraManager) context
                .getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    @SuppressWarnings("all")
    public int openCamera(int id, boolean isForce) {
        closeCamera();
        final String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            return CameraView.ERROR_CODE_OPEN_3;
        }
        if (cameraIds.length <= 0)
            return CameraView.ERROR_CODE_OPEN_1;
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
        }
        if (mCameraId == null) {
            if (isForce) {
                return CameraView.ERROR_CODE_OPEN_2;
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
                    return CameraView.ERROR_CODE_OPEN_3;
            }
        }
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                return CameraView.ERROR_CODE_OPEN_3;
            }
            manager.openCamera(mCameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            return CameraView.ERROR_CODE_OPEN_3;
        } catch (InterruptedException e) {
            return CameraView.ERROR_CODE_OPEN_3;
        }
        return CameraView.ERROR_CODE_OK;
    }

    @Override
    public void closeCamera() {
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
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock.release();
        }
        characteristicsSelected = null;
        mCameraId = null;
    }

    @SuppressWarnings("all")
    private class CameraStateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
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
