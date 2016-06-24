package am.project.x.activities.util.printer.data;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import am.project.x.R;
import am.util.printer.PrintTask;
import am.util.printer.PrinterRequest;
import am.util.printer.PrinterWriter80mm;

/**
 * 测试器
 * Created by Alex on 2016/6/23.
 */
public class PrinterTester {

    private Activity activity;
    private AlertDialog dlgInfo;

    public PrinterTester(Activity activity) {
        this.activity = activity;
    }

    public void startTest(String ip, int port, int type) {
        if (dlgInfo == null) {
            dlgInfo = new AlertDialog.Builder(activity).create();
        }
        new TestPrintTask(ip, port, type).execute();
    }

    public void startTest(BluetoothDevice device, int type) {
        if (dlgInfo == null) {
            dlgInfo = new AlertDialog.Builder(activity).create();
        }
        new TestPrintTask(device, type).execute();
    }

    private class TestPrintTask extends PrintTask implements
            DialogInterface.OnCancelListener, DialogInterface.OnClickListener {

        public TestPrintTask(BluetoothDevice device, int type) {
            super(device, type);
        }

        public TestPrintTask(String ip, int port, int type) {
            super(ip, port, type);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlgInfo.setOnCancelListener(this);
            dlgInfo.setButton(DialogInterface.BUTTON_NEGATIVE,
                    activity.getString(R.string.printer_cancel), this);
            dlgInfo.setMessage(activity.getString(R.string.printer_test_message_0));
            dlgInfo.show();
            dlgInfo.getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.printer_cancel);
        }

        @Override
        protected byte[] getPrintData(int type) throws Exception {
            return type == PrinterWriter80mm.TYPE_80 ? PrinterData.getPrintData80(activity.getResources())
                    : PrinterData.getPrintData58(activity.getResources());
        }

        @Override
        protected void onPrinterStateChanged(int state) {
            super.onPrinterStateChanged(state);
            switch (state) {
                case PrinterRequest.STATE_0:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_1));
                    break;
                case PrinterRequest.STATE_1:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_2));
                    break;
                case PrinterRequest.STATE_2:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_3));
                    break;
                case PrinterRequest.STATE_3:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_4));
                    break;
                case PrinterRequest.STATE_4:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_5));
                    break;
            }
        }

        @Override
        protected void onResult(int errorCode) {
            super.onResult(errorCode);
            switch (errorCode) {
                case PrinterRequest.ERROR_0:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_1));
                    break;
                case PrinterRequest.ERROR_1:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_2));
                    break;
                case PrinterRequest.ERROR_2:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_3));
                    break;
                case PrinterRequest.ERROR_3:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_4));
                    break;
                case PrinterRequest.ERROR_4:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_5));
                    break;
                case PrinterRequest.ERROR_5:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_6));
                    break;
            }
            dlgInfo.getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.printer_determine);
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            cancel(true);
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (getStatus() == Status.RUNNING) {
                cancel(true);
            }
        }
    }
}
