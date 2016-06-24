package am.util.printer;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

/**
 * 打印任务
 * Created by Alex on 2016/6/23.
 */
@SuppressWarnings("unused")
public abstract class PrintTask extends AsyncTask<Void, Integer, Integer> {

    private SimplePrinterRequest request;

    public PrintTask(BluetoothDevice device, int type) {
        request = new SimplePrinterRequest(device, type);
    }

    public PrintTask(String ip, int port, int type) {
        request = new SimplePrinterRequest(ip, port, type);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        return request.doPrinterRequest();
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
        request.destroy();
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

    private class SimplePrinterRequest extends PrintRequest {
        public SimplePrinterRequest(BluetoothDevice device, int type) {
            super(device, type);
        }

        public SimplePrinterRequest(String ip, int port, int type) {
            super(ip, port, type);
        }

        @Override
        protected byte[] getPrintData(int type) throws Exception {
            return PrintTask.this.getPrintData(type);
        }

        @Override
        protected void onPrinterStateChanged(int state) {
            PrintTask.this.publishProgress(state);
        }
    }
}
