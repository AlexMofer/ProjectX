package am.widget.cameraview.old;

/**
 * 状态回调
 * Created by Alex on 2017/2/27.
 */

public interface CameraStateCallback {
    int ERROR_CODE_OPEN_1 = 1;// 未找到摄像头设备
    int ERROR_CODE_OPEN_2 = 2;// 未找到指定FACING的摄像头
    int ERROR_CODE_OPEN_3 = 3;// 开启摄像头失败
    int ERROR_CODE_CLOSE_1 = 11;// 关闭摄像头失败
    int ERROR_CODE_CONFIG_1 = 21;// 无可用的配置信息
    int ERROR_CODE_CONFIG_2 = 22;// 配置摄像头出错
    int ERROR_CODE_CONFIG_3 = 23;// 设置surface出错
    int ERROR_CODE_CONFIG_4 = 24;// 无效的旋转角度
    /**
     * 权限拒绝
     */
    void onPermissionDenied(CameraView cameraView);

    /**
     * 打开
     */
    void onOpened(CameraView cameraView);

    /**
     * 断开连接
     */
    void onDisconnected(CameraView cameraView);

    /**
     * 出错
     * @param error 错误码
     * @param reason 缘由，仅API21以上有效，参考CameraDevice.StateCallback
     */
    void onError(CameraView cameraView, int error, int reason);
}
