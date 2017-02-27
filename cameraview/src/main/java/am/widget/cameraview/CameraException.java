package am.widget.cameraview;

import android.os.Bundle;

/**
 * 摄像头错误
 * Created by Alex on 2017/2/27.
 */

class CameraException extends Exception {

    private int code;
    private int reason;

    private CameraException(int code) {
        this.code = code;
    }

    private CameraException(int code, int reason) {
        this(code);
        this.reason = reason;
    }

    int getCode() {
        return code;
    }

    int getReason() {
        return reason;
    }

    static CameraException newInstance(int code) {
        return new CameraException(code);
    }

    static CameraException newInstance(int code, int reason) {
        return new CameraException(code, reason);
    }
}
