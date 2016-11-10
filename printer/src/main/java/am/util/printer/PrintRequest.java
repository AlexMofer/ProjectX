package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * 打印请求
 * 已过时，使用PrintSocketHolder替代
 *
 * @see PrintSocketHolder
 * Created by Alex on 2016/6/24.
 */
@SuppressWarnings("unused")
@Deprecated
public abstract class PrintRequest implements PrintSocketHolder.OnStateChangedListener {

    public static final int STATE_0 = 0;// 生成测试页面数据
    public static final int STATE_1 = 1;// 创建Socket连接
    public static final int STATE_2 = 2;// 发送测试数据
    public static final int STATE_3 = 3;// 写入测试页面数据
    public static final int STATE_4 = 4;// 关闭输出流
    public static final int ERROR_0 = 0;// 成功
    public static final int ERROR_1 = -1;// 生成测试页面数据失败
    public static final int ERROR_2 = -2;// 创建Socket失败
    public static final int ERROR_3 = -3;// 获取输出流失败
    public static final int ERROR_4 = -4;// 写入测试页面数据失败
    public static final int ERROR_5 = -5;// 必要参数不能为空
    private int type;
    private PrintSocketHolder holder;

    public PrintRequest(BluetoothDevice device, int type) {
        holder = new PrintSocketHolder(device);
        holder.setOnStateChangedListener(this);
        setType(type);
    }

    public PrintRequest(String ip, int port, int type) {
        holder = new PrintSocketHolder(ip, port);
        holder.setOnStateChangedListener(this);
        setType(type);
    }

    /**
     * 设置IP及端口
     *
     * @param ip   IP
     * @param port 端口
     */
    public void setIp(String ip, int port) {
        holder.setIp(ip, port);
    }

    /**
     * 设置蓝牙
     *
     * @param device 设备
     */
    public void setDevice(BluetoothDevice device) {
        holder.setDevice(device);
    }

    /**
     * 设置打印类型
     *
     * @param type 打印类型
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 执行打印请求
     *
     * @return 错误代码
     */
    public int doPrinterRequest() {
        onPrinterStateChanged(PrintSocketHolder.STATE_0);
        byte[] data;
        try {
            data = getPrintData(type);
        } catch (Exception e) {
            return PrintSocketHolder.ERROR_1;
        }
        int create = holder.createSocket();
        if (create != PrintSocketHolder.ERROR_0)
            return create;
        int output = holder.getOutputStream();
        if (output != PrintSocketHolder.ERROR_0)
            return output;
        int send = holder.sendData(data);
        if (send != PrintSocketHolder.ERROR_0)
            return send;
        holder.closeSocket();
        return PrintSocketHolder.ERROR_0;
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (holder != null)
            holder.closeSocket();
    }

    /**
     * 获取打印数据
     *
     * @param type 打印类型
     * @return 打印数据
     * @throws Exception 异常
     */
    protected abstract byte[] getPrintData(int type) throws Exception;

    /**
     * 打印状态变化
     *
     * @param state 打印状态
     */
    protected void onPrinterStateChanged(int state) {
    }

    @Override
    public void onStateChanged(int state) {
        onPrinterStateChanged(state);
    }
}
