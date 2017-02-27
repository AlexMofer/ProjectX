package am.widget.cameraview;

import android.hardware.Camera;

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

    private static final int MIN_PIXELS = 6;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private CameraSize mBestPreviewSize;

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
        final int minPixels = width * height / MIN_PIXELS;
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
            if (distortion > MAX_ASPECT_DISTORTION) {
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
}
