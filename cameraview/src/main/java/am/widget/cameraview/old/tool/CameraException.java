package am.widget.cameraview.old.tool;

/**
 * 摄像头错误
 * Created by Alex on 2017/2/27.
 */

public class CameraException extends Exception {

    private int code;
    private int reason;

    private CameraException(int code) {
        this.code = code;
    }

    private CameraException(int code, int reason) {
        this(code);
        this.reason = reason;
    }

    public static CameraException newInstance(int code) {
        return new CameraException(code);
    }

    public int getCode() {
        return code;
    }

    public int getReason() {
        return reason;
    }
}
