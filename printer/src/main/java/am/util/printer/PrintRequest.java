package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * 打印请求
 * Created by Alex on 2016/6/24.
 */
@SuppressWarnings("unused")
public abstract class PrintRequest {

    public static final int STATE_0 = 0;// 生成测试页面数据
    public static final int STATE_1 = 1;// 创建Socket连接
    public static final int STATE_2 = 2;// 发送测试数据
    public static final int STATE_3 = 3;// 写入测试页面数据
    public static final int STATE_4 = 4;// 完成测试
    public static final int ERROR_0 = 0;// 成功
    public static final int ERROR_1 = -1;// 生成测试页面数据失败
    public static final int ERROR_2 = -2;// 创建Socket失败
    public static final int ERROR_3 = -3;// 获取输出流失败
    public static final int ERROR_4 = -4;// 写入测试页面数据失败
    public static final int ERROR_5 = -5;// 必要参数不能为空
    private String ip;
    private int port = 9100;
    private BluetoothDevice mDevice;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙打印UUID
    private int type;
    private Socket socket;
    private BluetoothSocket bluetoothSocket;
    private OutputStream out;

    public PrintRequest(BluetoothDevice device, int type) {
        setDevice(device);
        setType(type);
    }

    public PrintRequest(String ip, int port, int type) {
        setIp(ip, port);
        setType(type);
    }

    /**
     * 设置IP及端口
     *
     * @param ip   IP
     * @param port 端口
     */
    public void setIp(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 设置蓝牙
     *
     * @param device 设备
     */
    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
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
    @SuppressWarnings("all")
    public int doPrinterRequest() {
        if (mDevice == null && ip == null)
            return ERROR_5;
        onPrinterStateChanged(STATE_0);
        byte[] data;
        try {
            data = getPrintData(type);
        } catch (Exception e) {
            return ERROR_1;
        }
        onPrinterStateChanged(STATE_1);
        try {
            if (mDevice != null) {
                bluetoothSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
            } else {
                socket = new Socket(ip, port);
            }
        } catch (Exception e) {
            destroy();
            return ERROR_2;
        }
        onPrinterStateChanged(STATE_2);
        try {
            if (mDevice != null) {
                out = bluetoothSocket.getOutputStream();
            } else {
                out = socket.getOutputStream();
            }
        } catch (IOException e) {
            destroy();
            return ERROR_3;
        }
        onPrinterStateChanged(STATE_3);
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            destroy();
            return ERROR_4;
        }
        onPrinterStateChanged(STATE_4);
        destroy();
        return ERROR_0;
    }

    /**
     * 销毁
     */
    public void destroy() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDevice = null;
        ip = null;
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
}
