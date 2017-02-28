package am.widget.cameraview;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 配置
 * Created by Alex on 2017/2/28.
 */
@TargetApi(21)
class CameraConfigLollipop implements Comparator<Size> {

    private int mMinPixelsPercentage = CameraView.MIN_PIXELS_PERCENTAGE;
    private double mMaxAspectDistortion = CameraView.MAX_ASPECT_DISTORTION;
    private CameraSize mBestPreviewSize;

    void setMinPixelsPercentage(int min) {
        mMinPixelsPercentage = min;
    }

    void setMaxAspectDistortion(double max) {
        mMaxAspectDistortion = max;
    }

    CameraSize getSize(CameraCharacteristics characteristics, int maxWidth, int maxHeight, int mode)
            throws CameraException {
        mBestPreviewSize = null;
        if (null == characteristics)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_1);
        final int width = maxWidth > maxHeight ? maxWidth : maxHeight;
        final int height = maxWidth > maxHeight ? maxHeight : maxWidth;
        StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (null == map)
            throw CameraException.newInstance(CameraStateCallback.ERROR_CODE_CONFIG_1);
        List<Size> supportedPreviewSizes = new ArrayList<>(Arrays.asList(
                map.getOutputSizes(SurfaceTexture.class)));
        Size largestPreview = Collections.max(supportedPreviewSizes, this);
        if (mode == CameraView.PREVIEW_SIZE_AT_MAX) {
            // 取最大预览尺寸模式
            mBestPreviewSize = new CameraSize(largestPreview.getWidth(), largestPreview.getHeight());
            return mBestPreviewSize;
        } else if (mode == CameraView.PREVIEW_SIZE_AT_MIN) {
            // 取最小预览尺寸模式
            Size smallest = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
            mBestPreviewSize = new CameraSize(smallest.getWidth(), smallest.getHeight());
            return mBestPreviewSize;
        }
        double screenAspectRatio = width / (double) height;
        Iterator<Size> it = supportedPreviewSizes.iterator();
        final int minPixels = width * height / mMinPixelsPercentage;
        while (it.hasNext()) {
            // 去除像素过低的
            Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.getWidth();
            int realHeight = supportedPreviewSize.getHeight();
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
                mBestPreviewSize = new CameraSize(supportedPreviewSize.getWidth(),
                        supportedPreviewSize.getHeight());
                return mBestPreviewSize;
            }
        }
        if (supportedPreviewSizes.isEmpty()) {
            // 无匹配的集合，取最大的预览尺寸
            mBestPreviewSize = new CameraSize(largestPreview.getWidth(),
                    largestPreview.getHeight());
            return mBestPreviewSize;
        } else {
            switch (mode) {
                default:
                case CameraView.PREVIEW_SIZE_AT_MATCHED_MAX:
                    // 所有匹配的预览尺寸取最大
                    Size largestMatchedPreview = supportedPreviewSizes.get(0);
                    mBestPreviewSize = new CameraSize(largestMatchedPreview.getWidth(),
                            largestMatchedPreview.getHeight());
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_MIN:
                    // 所有匹配的预览尺寸取最小
                    Size smallestMatchedPreview = supportedPreviewSizes.get(
                            supportedPreviewSizes.size() - 1);
                    mBestPreviewSize = new CameraSize(smallestMatchedPreview.getWidth(),
                            smallestMatchedPreview.getHeight());
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_CROP:
                    // 所有匹配的预览尺寸中，比视图尺寸大，但最接近
                    Size bestMaxSize = supportedPreviewSizes.get(0);
                    for (Size size : supportedPreviewSizes) {
                        if (size.getWidth() >= width && size.getHeight() >= height) {
                            bestMaxSize = size;
                        } else {
                            break;
                        }
                    }
                    mBestPreviewSize = new CameraSize(bestMaxSize.getWidth(),
                            bestMaxSize.getHeight());
                    return mBestPreviewSize;
                case CameraView.PREVIEW_SIZE_AT_MATCHED_INSIDE:
                    // 所有匹配的预览尺寸中，比视图尺寸小，但最接近
                    Size bestMinSize = supportedPreviewSizes.get(
                            supportedPreviewSizes.size() - 1);
                    for (Size size : supportedPreviewSizes) {
                        if (size.getWidth() <= width && size.getHeight() <= height) {
                            bestMinSize = size;
                            break;
                        }
                    }
                    mBestPreviewSize = new CameraSize(bestMinSize.getWidth(),
                            bestMinSize.getHeight());
                    return mBestPreviewSize;
            }
        }
    }

    @Override
    public int compare(Size lhs, Size rhs) {
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }
}
