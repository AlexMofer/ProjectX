package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * 打印任务
 * Created by Alex on 2016/6/23.
 */
@SuppressWarnings("unused")
public abstract class PrintTask extends AsyncTask<Void, Integer, Integer> {

    public static final int TYPE_80 = 0;// 纸宽80mm
    public static final int TYPE_58 = 1;// 纸宽58mm
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

    public PrintTask(BluetoothDevice device, int type) {
        this.mDevice = device;
        this.type = type;
    }

    public PrintTask(String ip, int port, int type) {
        this.ip = ip;
        this.port = port;
        this.type = type;
    }

    @Override
    @SuppressWarnings("all")
    protected Integer doInBackground(Void... voids) {
        if (mDevice == null && ip == null)
            return ERROR_5;
        publishProgress(STATE_0);
        byte[] data;
        try {
            data = getPrintData(type);
        } catch (Exception e) {
            return ERROR_1;
        }
        publishProgress(STATE_1);
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
        publishProgress(STATE_2);
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
        publishProgress(STATE_3);
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            destroy();
            return ERROR_4;
        }
        publishProgress(STATE_4);
        destroy();
        return ERROR_0;
    }

    /**
     * 获取打印数据
     *
     * @param type 打印类型
     * @return 打印数据
     * @throws Exception 异常
     */
    protected abstract byte[] getPrintData(int type) throws Exception;

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values == null || values.length < 1)
            return;
        onPrinterStateChanged(values[0]);
    }

    /**
     * 打印状态变化
     *
     * @param state 打印状态
     */
    protected void onPrinterStateChanged(int state) {
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        destroy();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (integer != null) {
            onResult(integer);
        }
    }

    /**
     * 打印结果
     *
     * @param errorCode 错误代码
     */
    protected void onResult(int errorCode) {

    }

    /**
     * 销毁
     */
    protected void destroy() {
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
}
