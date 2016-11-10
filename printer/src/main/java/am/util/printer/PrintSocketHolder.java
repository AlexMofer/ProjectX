package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

/**
 * 打印Socket
 * Created by Alex on 2016/11/10.
 */

public class PrintSocketHolder {
    public static final int STATE_0 = 0;// 生成测试页面数据
    public static final int STATE_1 = 1;// 创建Socket连接
    public static final int STATE_2 = 2;// 获取输出流
    public static final int STATE_3 = 3;// 写入测试页面数据
    public static final int STATE_4 = 4;// 关闭输出流
    public static final int ERROR_0 = 0;// 成功
    public static final int ERROR_1 = -1;// 生成测试页面数据失败
    public static final int ERROR_2 = -2;// 创建Socket失败
    public static final int ERROR_3 = -3;// 获取输出流失败
    public static final int ERROR_4 = -4;// 写入测试页面数据失败
    public static final int ERROR_5 = -5;// 必要参数不能为空
    public static final int ERROR_6 = -6;// 关闭Socket出错
    public static final int ERROR_100 = -100;// 失败
    private String ip;
    private int port = 9100;
    private BluetoothDevice mDevice;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙打印UUID
    private Socket socket;
    private BluetoothSocket bluetoothSocket;
    private OutputStream out;
    private WeakReference<OnStateChangedListener> mListener;

    public PrintSocketHolder(BluetoothDevice device) {
        setDevice(device);
    }

    public PrintSocketHolder(String ip, int port) {
        setIp(ip, port);
    }

    @SuppressWarnings("all")
    public int createSocket() {
        onPrinterStateChanged(STATE_1);
        if (mDevice == null && ip == null)
            return ERROR_5;
        try {
            if (mDevice != null) {
                bluetoothSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
            } else {
                socket = new Socket(ip, port);
            }
        } catch (Exception e) {
            closeSocket();
            return ERROR_2;
        }
        return ERROR_0;
    }

    public int getOutputStream() {
        onPrinterStateChanged(STATE_2);
        try {
            if (mDevice != null) {
                out = bluetoothSocket.getOutputStream();
            } else {
                out = socket.getOutputStream();
            }
        } catch (IOException e) {
            closeSocket();
            return ERROR_3;
        }
        return ERROR_0;
    }

    public boolean isSocketPrepared() {
        return (bluetoothSocket != null || socket != null) && out != null;
    }

    public int sendData(List<byte[]> data) {
        onPrinterStateChanged(STATE_3);
        if (data == null || data.size() <= 0)
            return ERROR_0;
        for (byte[] item : data) {
            try {
                out.write(item);
                out.flush();
            } catch (IOException e) {
                closeSocket();
                return ERROR_4;
            }
        }
        return ERROR_0;
    }

    public int sendData(byte[] data) {
        onPrinterStateChanged(STATE_3);
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            closeSocket();
            return ERROR_4;
        }
        return ERROR_0;
    }

    public int sendData(byte[]... data) {
        onPrinterStateChanged(STATE_3);
        for (byte[] item : data) {
            try {
                out.write(item);
                out.flush();
            } catch (IOException e) {
                closeSocket();
                return ERROR_4;
            }
        }
        return ERROR_0;
    }

    public int prepareSocket() {
        int create = createSocket();
        if (create != PrintSocketHolder.ERROR_0)
            return create;
        return getOutputStream();
    }

    /**
     * 销毁
     */
    public int closeSocket() {
        onPrinterStateChanged(STATE_4);
        boolean error = false;
        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            out = null;
            error = true;
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            socket = null;
            error = true;
        }
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            bluetoothSocket = null;
            error = true;
        }
        return error ? ERROR_6 : ERROR_0;
    }

    /**
     * 打印状态变化
     *
     * @param state 打印状态
     */
    public void onPrinterStateChanged(int state) {
        try {
            if (mListener != null)
                mListener.get().onStateChanged(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 设置状态监听
     *
     * @param listener 监听
     */
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int state);
    }
}
