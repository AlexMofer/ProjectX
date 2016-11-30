package com.google.zxing.client.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import com.google.zxing.client.android.manager.AmbientLightManager;
import com.google.zxing.client.android.manager.ScanFeedbackManager;
import com.google.zxing.client.android.util.Utils;

import java.util.ArrayList;

/**
 * ZxingScanView
 * Created by Alex on 2016/11/24.
 */

public class ZxingScanView extends SurfaceView {

    public static final int ERROR_CODE_NULL = -1;//无错误
    public static final int ERROR_CODE_0 = 0;//开启摄像头失败
    public static final int ERROR_CODE_1 = 1;//无开启摄像头权限
    private CameraManager mCameraManager;
    private AmbientLightManager mAmbientLightManager;
    private ScanFeedbackManager mScanFeedbackManager;
    private int mScanWidth;
    private int mScanHeight;
    private int mCameraId;
    private int mErrorCode = ERROR_CODE_NULL;
    private ArrayList<OnScanListener> mListeners = new ArrayList<>();
    private ArrayList<OnStateListener> mStateListeners = new ArrayList<>();


    public ZxingScanView(Context context) {
        super(context);
        initView(null);
    }

    public ZxingScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ZxingScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    public ZxingScanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        int mode = AmbientLightManager.MODE_AUTO;
        int feedback = ScanFeedbackManager.MODE_AUTO;
        String fileName;
        int rawId = NO_ID;
        int scanWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int scanHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        int cameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;
        int milliseconds = ScanFeedbackManager.DEFAUT_MILLISECONDS;

        TypedArray custom = getContext().obtainStyledAttributes(attrs, R.styleable.ZxingScanView);
        mode = custom.getInt(R.styleable.ZxingScanView_zsvAmbientLight, mode);
        feedback = custom.getInt(R.styleable.ZxingScanView_zsvFeedback, feedback);
        fileName = custom.getString(R.styleable.ZxingScanView_zsvAudioAssetsFileName);
        rawId = custom.getResourceId(R.styleable.ZxingScanView_zsvAudioRaw, rawId);
        milliseconds = custom.getInteger(R.styleable.ZxingScanView_zsvVibrateMilliseconds,
                milliseconds);
        scanWidth = custom.getLayoutDimension(R.styleable.ZxingScanView_zsvScanWidth, scanWidth);
        scanHeight = custom.getLayoutDimension(R.styleable.ZxingScanView_zsvScanHeight, scanHeight);
        cameraId = custom.getInteger(R.styleable.ZxingScanView_zsvCameraId, cameraId);

        custom.recycle();
        setScanWidth(scanWidth);
        setScanHeight(scanHeight);
        setCameraId(cameraId);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        if (isInEditMode())
            return;
        mAmbientLightManager = new AmbientLightManager(getContext(),
                new AmbientLightCallBack(), mode);
        mScanFeedbackManager = new ScanFeedbackManager(getContext());
        setFeedbackMode(feedback);
        setFeedbackAudioAssetsFileName(fileName);
        setFeedbackAudioRawId(rawId);
        setFeedbackVibrateMilliseconds(milliseconds);
        getHolder().addCallback(new CameraCallBack());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAmbientLightManager.release();
        mScanFeedbackManager.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                setAmbientLightMode(AmbientLightManager.MODE_CLOSE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                setAmbientLightMode(AmbientLightManager.MODE_OPEN);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class CameraCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            openDriver(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mCameraManager != null) {
                final int scanWidth = mScanWidth == ViewGroup.LayoutParams.MATCH_PARENT ? width :
                        (mScanWidth > width ? width : mScanWidth);
                final int scanHeight = mScanHeight == ViewGroup.LayoutParams.MATCH_PARENT ? height :
                        (mScanHeight > height ? height : mScanHeight);
                mCameraManager.setManualFramingRect(scanWidth, scanHeight);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            closeDriver();
        }
    }

    private void openDriver(SurfaceHolder surfaceHolder) {
        notifyListenerPrepareOpen();
        if (surfaceHolder == null)
            return;// 已经销毁
        if (isOpen())
            return;// 摄像头已经打开
        if (Utils.lacksPermission(getContext(), Utils.PERMISSION_CAMERA)) {
            mErrorCode = ERROR_CODE_1;
            notifyListenerError();
            return;
        }
        mCameraManager = new CameraManager(getContext());
        if (mCameraId != OpenCameraInterface.NO_REQUESTED_CAMERA)
            mCameraManager.setManualCameraId(mCameraId);
        final int width = mScanWidth == ViewGroup.LayoutParams.MATCH_PARENT ? getWidth() :
                (mScanWidth > getWidth() ? getWidth() : mScanWidth);
        final int height = mScanHeight == ViewGroup.LayoutParams.MATCH_PARENT ? getHeight() :
                (mScanHeight > getHeight() ? getHeight() : mScanHeight);
        mCameraManager.setManualFramingRect(width, height);
        try {
            mCameraManager.openDriver(surfaceHolder);
            mCameraManager.startPreview();
        } catch (Exception e) {
            mErrorCode = ERROR_CODE_0;
            notifyListenerError();
            return;
        }
        mAmbientLightManager.resume();
        notifyListenerOpened();
    }

    private void closeDriver() {
        notifyListenerPrepareClose();
        mErrorCode = ERROR_CODE_NULL;
        if (mCameraManager != null)
            mCameraManager.stopPreview();
        mAmbientLightManager.pause();
        if (mCameraManager != null)
            mCameraManager.closeDriver();
        mCameraManager = null;
        notifyListenerClosed();
    }

    private class AmbientLightCallBack implements AmbientLightManager.AmbientLightCallBack {
        @Override
        public void onChange(boolean on) {
            if (mCameraManager != null)
                mCameraManager.setTorch(on);
        }
    }

    private void notifyListenerError() {
        for (OnScanListener listener : mListeners) {
            listener.onError(this);
        }
    }

    private void notifyListenerPrepareOpen() {
        for (OnStateListener listener : mStateListeners) {
            listener.onPrepareOpen(this);
        }
    }

    private void notifyListenerOpened() {
        for (OnStateListener listener : mStateListeners) {
            listener.onOpened(this);
        }
    }

    private void notifyListenerPrepareClose() {
        for (OnStateListener listener : mStateListeners) {
            listener.onPrepareClose(this);
        }
    }

    private void notifyListenerClosed() {
        for (OnStateListener listener : mStateListeners) {
            listener.onClosed(this);
        }
    }

    public void open() {
        openDriver(getHolder());
    }

    /**
     * 扫描是否已打开
     *
     * @return 是否打开
     */
    public boolean isOpen() {
        return mCameraManager != null && mCameraManager.isOpen();
    }

    /**
     * 添加扫描监听
     *
     * @param listener 监听器
     */
    public void addOnScanListener(OnScanListener listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    /**
     * 移除扫描监听器
     *
     * @param listener 监听器
     * @return 是否移除成功
     */
    public boolean removeOnScanListener(OnScanListener listener) {
        return listener != null && mListeners.remove(listener);
    }

    /**
     * 添加状态监听
     *
     * @param listener 状态监听
     */
    public void addOnStateListener(OnStateListener listener) {
        if (listener != null)
            mStateListeners.add(listener);
    }

    /**
     * 移除状态监听
     *
     * @param listener 状态监听
     * @return 是否成功移除
     */
    public boolean removeOnStateListener(OnStateListener listener) {
        return listener != null && mStateListeners.remove(listener);
    }

    /**
     * 设置背光模式
     *
     * @param mode 背光模式，可用参数：{@link AmbientLightManager#MODE_AUTO}、
     *             {@link AmbientLightManager#MODE_OPEN}、{@link AmbientLightManager#MODE_CLOSE}
     */
    public void setAmbientLightMode(int mode) {
        mAmbientLightManager.setMode(mode);
    }

    /**
     * 设置扫描反馈模式
     *
     * @param mode 扫描反馈模式，可用参数：{@link ScanFeedbackManager#MODE_AUTO}、
     *             {@link ScanFeedbackManager#MODE_AUDIO_ONLY}、
     *             {@link ScanFeedbackManager#MODE_VIBRATOR_ONLY}、
     *             {@link ScanFeedbackManager#MODE_AUDIO_VIBRATOR}
     */
    public void setFeedbackMode(int mode) {
        mScanFeedbackManager.setMode(mode);
    }

    /**
     * 设置音频Assets文件名
     *
     * @param fileName 文件名
     */
    public void setFeedbackAudioAssetsFileName(String fileName) {
        mScanFeedbackManager.setAudioAssetsFileName(fileName);
    }

    /**
     * 设置音频资源ID
     *
     * @param id 资源ID
     */
    public void setFeedbackAudioRawId(int id) {
        mScanFeedbackManager.setAudioRawId(id);
    }

    /**
     * 设置震动时长
     *
     * @param milliseconds 时长
     */
    public void setFeedbackVibrateMilliseconds(long milliseconds) {
        mScanFeedbackManager.setVibrateMilliseconds(milliseconds);
    }

    /**
     * 设置扫描宽度
     * 下次创建CameraManager时生效
     *
     * @param width 扫描宽度
     */
    public void setScanWidth(int width) {
        mScanWidth = width;
    }

    /**
     * 设置扫描高度
     * 下次创建CameraManager时生效
     *
     * @param height 扫描高度
     */
    public void setScanHeight(int height) {
        mScanHeight = height;
    }

    /**
     * 设置摄像头ID
     * 下次创建CameraManager时生效
     *
     * @param id 摄像头ID
     */
    public void setCameraId(int id) {
        mCameraId = id;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * 获取扫描宽度
     *
     * @return 扫描宽度
     */
    public int getScanWidth() {
        return mScanWidth;
    }

    /**
     * 获取扫描高度
     *
     * @return 扫描高度
     */
    public int getScanHeight() {
        return mScanHeight;
    }

    /**
     * 扫描监听
     */
    public interface OnScanListener {
        /**
         * 出现错误
         *
         * @param scanView ZxingScanView
         */
        void onError(ZxingScanView scanView);
    }

    public interface OnStateListener {
        void onPrepareOpen(ZxingScanView scanView);

        void onOpened(ZxingScanView scanView);

        void onPrepareClose(ZxingScanView scanView);

        void onClosed(ZxingScanView scanView);
    }
}
