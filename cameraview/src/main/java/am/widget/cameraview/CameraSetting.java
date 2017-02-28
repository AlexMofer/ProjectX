package am.widget.cameraview;

/**
 * 相机设置
 * Created by Alex on 2017/2/28.
 */

class CameraSetting {
    private boolean mAutoFocus = true;// 自动对焦
    private boolean mContinuousFocus = false;// 持续对焦
    private boolean mInvertScan = false;// 翻转扫描
    private boolean mBarcodeSceneMode = false;// 条码扫描模式
    private boolean mVideoStabilization = false;// 视频稳定
    private boolean mControlFocusAreas = false;// 控制对焦区域
    private int mFocusAreasRadius = -1;// 对焦区域半径
    private boolean mControlMeteringAreas = false;// 控制计量区域
    private int mMeteringAreasRadius = -1;// 计量区域半径
    private int mAmbientLightMode = CameraView.AMBIENT_LIGHT_MODE_AUTO;// 背光模式
    private boolean mControlExposure = false;// 控制曝光
    private float mLightOnExposureCompensation = 0.0f;// 背光开启时，曝光补偿
    private float mLightOffExposureCompensation = 1.5f;// 背光关闭时，曝光补偿


    boolean isAutoFocus() {
        return mAutoFocus;
    }

    boolean setAutoFocus(boolean autoFocus) {
        if (mAutoFocus == autoFocus)
            return false;
        mAutoFocus = autoFocus;
        return true;
    }

    boolean isContinuousFocus() {
        return mContinuousFocus;
    }

    boolean setContinuousFocus(boolean continuousFocus) {
        if (mContinuousFocus == continuousFocus)
            return false;
        mContinuousFocus = continuousFocus;
        return true;
    }

    boolean isInvertScan() {
        return mInvertScan;
    }

    boolean setInvertScan(boolean invertScan) {
        if (mInvertScan == invertScan)
            return false;
        mInvertScan = invertScan;
        return true;
    }

    boolean isBarcodeSceneMode() {
        return mBarcodeSceneMode;
    }

    boolean setBarcodeSceneMode(boolean barcodeSceneMode) {
        if (mBarcodeSceneMode == barcodeSceneMode)
            return false;
        mBarcodeSceneMode = barcodeSceneMode;
        return true;
    }

    boolean isVideoStabilization() {
        return mVideoStabilization;
    }

    boolean setVideoStabilization(boolean videoStabilization) {
        if (mVideoStabilization == videoStabilization)
            return false;
        mVideoStabilization = videoStabilization;
        return true;
    }

    boolean isControlFocusAreas() {
        return mControlFocusAreas;
    }

    boolean setControlFocusAreas(boolean controlFocusAreas) {
        if (mControlFocusAreas == controlFocusAreas)
            return false;
        mControlFocusAreas = controlFocusAreas;
        return true;
    }

    int getFocusAreasRadius() {
        return mFocusAreasRadius;
    }

    boolean setFocusAreasRadius(int focusAreasRadius) {
        if (mFocusAreasRadius == focusAreasRadius)
            return false;
        mFocusAreasRadius = focusAreasRadius;
        return true;
    }

    boolean isControlMeteringAreas() {
        return mControlMeteringAreas;
    }

    boolean setControlMeteringAreas(boolean controlMeteringAreas) {
        if (mControlMeteringAreas == controlMeteringAreas)
            return false;
        mControlMeteringAreas = controlMeteringAreas;
        return true;
    }

    int getMeteringAreasRadius() {
        return mMeteringAreasRadius;
    }

    boolean setMeteringAreasRadius(int meteringAreasRadius) {
        if (mMeteringAreasRadius == meteringAreasRadius)
            return false;
        mMeteringAreasRadius = meteringAreasRadius;
        return true;
    }

    int getAmbientLightMode() {
        return mAmbientLightMode;
    }

    boolean setAmbientLightMode(int ambientLightMode) {
        if (mAmbientLightMode == ambientLightMode)
            return false;
        mAmbientLightMode = ambientLightMode;
        return true;
    }

    boolean isControlExposure() {
        return mControlExposure;
    }

    boolean setControlExposure(boolean controlExposure) {
        if (mControlExposure == controlExposure)
            return false;
        mControlExposure = controlExposure;
        return true;
    }

    float getLightOnExposureCompensation() {
        return mLightOnExposureCompensation;
    }

    float getLightOffExposureCompensation() {
        return mLightOffExposureCompensation;
    }

    boolean setExposureCompensation(float lightOn, float lightOff) {
        if (mLightOnExposureCompensation == lightOn && mLightOffExposureCompensation == lightOff)
            return false;
        mLightOnExposureCompensation = lightOn;
        mLightOffExposureCompensation = lightOff;
        return true;
    }
}
