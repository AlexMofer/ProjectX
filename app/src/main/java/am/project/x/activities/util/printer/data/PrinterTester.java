package am.project.x.activities.util.printer.data;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import am.project.x.R;
import am.util.printer.PrintRequest;
import am.util.printer.PrintTask;
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

    public void startTest(String ip, int port, int type, String qrContent, int width) {
        if (dlgInfo == null) {
            dlgInfo = new AlertDialog.Builder(activity).create();
        }
        new TestPrintTask(ip, port, type, qrContent,width).execute();
    }

    public void startTest(BluetoothDevice device, int type, String qrContent) {
        if (dlgInfo == null) {
            dlgInfo = new AlertDialog.Builder(activity).create();
        }
        new TestPrintTask(device, type, qrContent).execute();
    }

    private class TestPrintTask extends PrintTask implements
            DialogInterface.OnCancelListener, DialogInterface.OnClickListener {

        private String qrContent;
        private int width = 300;

        public TestPrintTask(BluetoothDevice device, int type, String qrContent) {
            super(device, type);
            this.qrContent = qrContent;
        }

        public TestPrintTask(String ip, int port, int type, String qrContent, int width) {
            super(ip, port, type);
            this.qrContent = qrContent;
            this.width = width;
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
            return type == PrinterWriter80mm.TYPE_80 ? PrinterData.getPrintData80(activity.getApplicationContext(), qrContent, width)
                    : PrinterData.getPrintData58(activity.getApplicationContext(), qrContent, width);
        }

        @Override
        protected void onPrinterStateChanged(int state) {
            super.onPrinterStateChanged(state);
            switch (state) {
                case PrintRequest.STATE_0:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_1));
                    break;
                case PrintRequest.STATE_1:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_2));
                    break;
                case PrintRequest.STATE_2:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_3));
                    break;
                case PrintRequest.STATE_3:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_4));
                    break;
                case PrintRequest.STATE_4:
                    dlgInfo.setMessage(activity.getString(R.string.printer_test_message_5));
                    break;
            }
        }

        @Override
        protected void onResult(int errorCode) {
            super.onResult(errorCode);
            switch (errorCode) {
                case PrintRequest.ERROR_0:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_1));
                    break;
                case PrintRequest.ERROR_1:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_2));
                    break;
                case PrintRequest.ERROR_2:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_3));
                    break;
                case PrintRequest.ERROR_3:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_4));
                    break;
                case PrintRequest.ERROR_4:
                    dlgInfo.setMessage(activity.getString(R.string.printer_result_message_5));
                    break;
                case PrintRequest.ERROR_5:
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
