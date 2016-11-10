package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 打印执行者
 * Created by Alex on 2016/11/10.
 */

public class PrintExecutor {
    private int type;
    private PrintSocketHolder holder;
    private int mReconnectTimes = 3;
    private int time = 0;
    private PrintSocketHolder.OnStateChangedListener listener;
    private WeakReference<OnPrintResultListener> mListener;

    public PrintExecutor(BluetoothDevice device, int type) {
        holder = new PrintSocketHolder(device);
        setType(type);
    }

    public PrintExecutor(String ip, int port, int type) {
        holder = new PrintSocketHolder(ip, port);
        setType(type);
    }

    /**
     * 执行打印请求
     *
     * @return 错误代码
     */
    private int doRequest(PrintDataMaker maker) {
        if (mReconnectTimes == 1) {
            holder.onPrinterStateChanged(PrintSocketHolder.STATE_0);
            List<byte[]> data = maker.getPrintData(type);
            if (!holder.isSocketPrepared()) {
                int prepare = holder.prepareSocket();
                if (prepare != PrintSocketHolder.ERROR_0)
                    return prepare;
            }
            return holder.sendData(data);
        } else {
            holder.onPrinterStateChanged(PrintSocketHolder.STATE_0);
            List<byte[]> data = maker.getPrintData(type);
            if (holder.isSocketPrepared()) {
                if (sendData(data))
                    return PrintSocketHolder.ERROR_0;
                else
                    return PrintSocketHolder.ERROR_100;
            } else {
                if (prepareSocket() && sendData(data)) {
                    return PrintSocketHolder.ERROR_0;
                } else {
                    return PrintSocketHolder.ERROR_100;
                }
            }
        }
    }

    /**
     * 执行打印请求
     *
     * @return 错误代码
     */
    public int doPrinterRequest(PrintDataMaker maker) {
        holder.setOnStateChangedListener(listener);
        return doRequest(maker);
    }

    private boolean prepareSocket() {
        time++;
        return time < mReconnectTimes &&
                (holder.prepareSocket() == PrintSocketHolder.ERROR_0 || prepareSocket());
    }

    private boolean sendData(List<byte[]> data) {
        if (holder.sendData(data) == PrintSocketHolder.ERROR_0) {
            time = 0;
            return true;
        } else {
            return prepareSocket() && sendData(data);
        }
    }

    public void doPrinterRequestAsync(PrintDataMaker maker) {
        new PrintTask().execute(maker);
    }

    private class PrintTask extends AsyncTask<PrintDataMaker, Integer, Integer> implements
            PrintSocketHolder.OnStateChangedListener {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.setOnStateChangedListener(this);
        }

        @Override
        protected Integer doInBackground(PrintDataMaker... makers) {
            if (makers == null || makers.length < 1)
                return PrintSocketHolder.ERROR_0;
            return doRequest(makers[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values == null || values.length < 1)
                return;
            if (listener != null)
                listener.onStateChanged(values[0]);
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
        private void onResult(int errorCode) {
            try {
                if (mListener != null)
                    mListener.get().onResult(errorCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStateChanged(int state) {
            publishProgress(state);
        }
    }

    /**
     * 销毁
     */
    public int closeSocket() {
        return holder.closeSocket();
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
     * 设置状态监听
     *
     * @param listener 监听
     */
    public void setOnStateChangedListener(PrintSocketHolder.OnStateChangedListener listener) {
        this.listener = listener;
    }

    public void setReconnectTimes(int times) {
        mReconnectTimes = times;
    }

    public void setOnPrintResultListener(OnPrintResultListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public interface OnPrintResultListener {
        void onResult(int errorCode);
    }

}
