package am.widget.cameraview;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 配置
 * Created by Alex on 2017/2/27.
 */
@SuppressWarnings("deprecation")
class CameraConfigBase implements Comparator<Camera.Size> {

    private int mMinPixelsPercentage = CameraView.MIN_PIXELS_PERCENTAGE;
    private double mMaxAspectDistortion = CameraView.MAX_ASPECT_DISTORTION;
    private CameraSize mBestPreviewSize;
    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;

    void setMinPixelsPercentage(int min) {
        mMinPixelsPercentage = min;
    }

    void setMaxAspectDistortion(double max) {
        mMaxAspectDistortion = max;
    }

    CameraSize getSize(Camera.Parameters parameters, int maxWidth, int maxHeight, int mode)
            throws CameraException {
        mBestPreviewSize = null;
        if (null == parameters)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_1);
        final int width = maxWidth > maxHeight ? maxWidth : maxHeight;
        final int height = maxWidth > maxHeight ? maxHeight : maxWidth;
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (null == rawSupportedSizes || rawSupportedSizes.size() <= 0) {
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (null == defaultSize) {
                mBestPreviewSize = new CameraSize(width, height);
                return mBestPreviewSize;
            }
            mBestPreviewSize = new CameraSize(defaultSize.width, defaultSize.height);
            return mBestPreviewSize;
        }
        ArrayList<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Camera.Size largestPreview = Collections.max(supportedPreviewSizes, this);
        if (mode == CameraView.PREVIEW_SIZE_AT_MAX) {
            // 取最大预览尺寸模式
            mBestPreviewSize = new CameraSize(largestPreview.width, largestPreview.height);
            return mBestPreviewSize;
        } else if (mode == CameraView.PREVIEW_SIZE_AT_MIN) {
            // 取最小预览尺寸模式
            Camera.Size smallest = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
            mBestPreviewSize = new CameraSize(smallest.width, smallest.height);
            return mBestPreviewSize;
        }
        double screenAspectRatio = width / (double) height;
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        final int minPixels = width * height / mMinPixelsPercentage;
        while (it.hasNext()) {
            // 去除像素过低的
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < minPixels) {
                it.remove();
                continue;
            }
            // 去除高宽比差距过大的
            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > mMaxAspectDistortion) {
                it.remove();
                continue;
            }
            // 找到高宽正好符合的
            if (maybeFlippedWidth == width && maybeFlippedHeight == height) {
                mBestPreviewSize = new CameraSize(supportedPreviewSize.width,
                        supportedPreviewSize.height);
                return mBestPreviewSize;
            }
        }
        if (supportedPreviewSizes.isEmpty()) {
            // 无匹配的集合，取最大的预览尺寸
            mBestPreviewSize = new CameraSize(largestPreview.width, largestPreview.height);
            return mBestPreviewSize;
        } else {
            switch (mode) {
                default:
                case CameraView.PREVIEW_SIZE_AT_MATCHED_MAX:
                    // 所有匹配的预览尺寸取最大
                    Camera.Size largestMatchedPreview = supportedPreviewSizes.get(0);
                    mBestPreviewSize = new CameraSize(largestMatchedPreview.width,
                            largestMatchedPreview.height);
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_MIN:
                    // 所有匹配的预览尺寸取最小
                    Camera.Size smallestMatchedPreview = supportedPreviewSizes.get(
                            supportedPreviewSizes.size() - 1);
                    mBestPreviewSize = new CameraSize(smallestMatchedPreview.width,
                            smallestMatchedPreview.height);
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_CROP:
                    // 所有匹配的预览尺寸中，比视图尺寸大，但最接近
                    Camera.Size bestMaxSize = supportedPreviewSizes.get(0);
                    for (Camera.Size size : supportedPreviewSizes) {
                        if (size.width >= width && size.height >= height) {
                            bestMaxSize = size;
                        } else {
                            break;
                        }
                    }
                    mBestPreviewSize = new CameraSize(bestMaxSize.width, bestMaxSize.height);
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_INSIDE:
                    // 所有匹配的预览尺寸中，比视图尺寸小，但最接近
                    Camera.Size bestMinSize = supportedPreviewSizes.get(
                            supportedPreviewSizes.size() - 1);
                    for (Camera.Size size : supportedPreviewSizes) {
                        if (size.width <= width && size.height <= height) {
                            bestMinSize = size;
                            break;
                        }
                    }
                    mBestPreviewSize = new CameraSize(bestMinSize.width, bestMinSize.height);
                    return mBestPreviewSize;
            }
        }
    }

    @Override
    public int compare(Camera.Size a, Camera.Size b) {
        return Long.signum((long) a.width * a.height - (long) b.width * b.height);
    }

    void configCamera(Context context, Camera camera, CameraSetting setting,
                      Camera.CameraInfo selectedCameraInfo, boolean safeMode)
            throws CameraException {
        getRotation(context, selectedCameraInfo);

    }

    private void getRotation(Context context, Camera.CameraInfo selectedCameraInfo)
            throws CameraException {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int displayRotation = display.getRotation();
        int cwRotationFromNaturalToDisplay;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                cwRotationFromNaturalToDisplay = 0;
                break;
            case Surface.ROTATION_90:
                cwRotationFromNaturalToDisplay = 90;
                break;
            case Surface.ROTATION_180:
                cwRotationFromNaturalToDisplay = 180;
                break;
            case Surface.ROTATION_270:
                cwRotationFromNaturalToDisplay = 270;
                break;
            default:
                // Have seen this return incorrect values like -90
                if (displayRotation % 90 == 0) {
                    cwRotationFromNaturalToDisplay = (360 + displayRotation) % 360;
                } else {
                    throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_4);
                }
        }

        int cwRotationFromNaturalToCamera = selectedCameraInfo.orientation;

        // Still not 100% sure about this. But acts like we need to flip this:
        if (selectedCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360;
        }
        cwRotationFromDisplayToCamera =
                (360 + cwRotationFromNaturalToCamera - cwRotationFromNaturalToDisplay) % 360;
        if (selectedCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cwNeededRotation = (360 - cwRotationFromDisplayToCamera) % 360;
        } else {
            cwNeededRotation = cwRotationFromDisplayToCamera;
        }
    }
}
