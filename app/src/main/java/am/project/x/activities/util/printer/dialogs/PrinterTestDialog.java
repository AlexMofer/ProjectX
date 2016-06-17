package am.project.x.activities.util.printer.dialogs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import am.project.x.R;
import am.util.printer.PrinterWriter80mm;
import am.widget.MaterialProgressImageView;

/**
 * 打印机测试对话框
 * Created by Alex on 2016/4/15.
 */
public class PrinterTestDialog extends AppCompatDialog implements View.OnClickListener {

    private MaterialProgressImageView mivLoading;
    private TextView tvInfo;
    private Button btnOk;
    private String ip;
    private int port;
    private PrintTask task;

    @SuppressWarnings("all")
    public PrinterTestDialog(Context context) {
        super(context);
        setContentView(R.layout.dlg_printer_test);
        mivLoading = (MaterialProgressImageView) findViewById(R.id.dpt_miv_loading);
        tvInfo = (TextView) findViewById(R.id.dpt_tv_info);
        btnOk = (Button) findViewById(R.id.dpt_btn_cancel);
        btnOk.setOnClickListener(this);
    }

    public void startTest(String ip, int port) {
        this.ip = ip;
        this.port = port;
        task = new PrintTask();
        show();
        task.execute();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mivLoading.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dpt_btn_cancel:
                cancel();
                break;
        }
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel(true);
        }
        super.cancel();
    }

    private class PrintTask extends AsyncTask<Void, Integer, Integer> {


        private Socket socket;
        private OutputStream out;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mivLoading.setVisibility(View.VISIBLE);
            mivLoading.start();
            tvInfo.setText(R.string.printer_test_toast_0);
            btnOk.setText(R.string.printer_cancel);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            publishProgress(0);
            byte[] data;
            try {
                data = getPrintData();
            } catch (Exception e) {
                return -1;
            }
            publishProgress(1);
            try {
                socket = new Socket(ip, port);
            } catch (Exception e) {
                destroy();
                return -2;
            }
            publishProgress(2);
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                destroy();
                return -3;
            }
            publishProgress(3);
            try {
                out.write(data);
                out.flush();
            } catch (IOException e) {
                destroy();
                return -4;
            }
            publishProgress(4);
            destroy();
            return 0;
        }

        private byte[] getPrintData() throws IOException{
            PrinterWriter80mm printer80 = new PrinterWriter80mm();
            printer80.setAlignCenter();
            printer80.printDrawable(getContext().getResources(), R.drawable.ic_printer_logo);
            printer80.setAlignLeft();
            printer80.printLine();
            printer80.printLineFeed();

            printer80.printLineFeed();
            printer80.setAlignCenter();
            printer80.setEmphasizedOn();
            printer80.setFontSize(1);
            printer80.print("我的餐厅");
            printer80.printLineFeed();
            printer80.setFontSize(0);
            printer80.setEmphasizedOff();
            printer80.printLineFeed();

            printer80.setLineHeight(80);
            printer80.print("最时尚的明星餐厅");
            printer80.printLineFeed();
            printer80.print("客服电话：400-8008800");
            printer80.printLineFeed();

            printer80.setAlignLeft();
            printer80.printLineFeed();

            printer80.print("订单号：88888888888888888");
            printer80.printLineFeed();

            printer80.print("预计送达：" +
                    new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                            .format(new Date(System.currentTimeMillis())));
            printer80.printLineFeed();

            printer80.setEmphasizedOn();
            printer80.print("#8（已付款）");
            printer80.printLineFeed();
            printer80.print("××区××路×××大厦××楼×××室");
            printer80.printLineFeed();
            printer80.setEmphasizedOff();
            printer80.print("13843211234");
            printer80.print("（张某某）");
            printer80.printLineFeed();
            printer80.print("备注：多加点辣椒，多加点香菜，多加点酸萝卜，多送点一次性手套");
            printer80.printLineFeed();

            printer80.printLine();
            printer80.printLineFeed();

            printer80.printInOneLine("星级美食（豪华套餐）×1", "￥88.88", 0);
            printer80.printLineFeed();
            printer80.printInOneLine("星级美食（限量套餐）×1", "￥888.88", 0);
            printer80.printLineFeed();
            printer80.printInOneLine("餐具×1", "￥0.00", 0);
            printer80.printLineFeed();
            printer80.printInOneLine("配送费", "免费", 0);
            printer80.printLineFeed();

            printer80.printLine();
            printer80.printLineFeed();

            printer80.setAlignRight();
            printer80.print("合计：977.76");
            printer80.printLineFeed();
            printer80.printLineFeed();

            printer80.setLineHeight(0);
            printer80.setAlignCenter();
            printer80.printDrawable(getContext().getResources(), R.drawable.ic_printer_qr);
            printer80.printLineFeed();
            printer80.print("扫一扫，查看详情");

            printer80.feedPaperCutPartial();
            return printer80.getData();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values == null || values.length < 1)
                return;
            switch (values[0]) {
                case 0:
                    tvInfo.setText(R.string.printer_test_toast_1);
                    break;
                case 1:
                    tvInfo.setText(R.string.printer_test_toast_2);
                    break;
                case 2:
                    tvInfo.setText(R.string.printer_test_toast_3);
                    break;
                case 3:
                    tvInfo.setText(R.string.printer_test_toast_4);
                    break;
                case 4:
                    tvInfo.setText(R.string.printer_test_toast_5);
                    break;
            }
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
                mivLoading.stop();
                mivLoading.setVisibility(View.GONE);
                switch (integer) {
                    case 0:
                        tvInfo.setText(R.string.printer_result_toast_1);
                        break;
                    case -1:
                        tvInfo.setText(R.string.printer_result_toast_2);
                        break;
                    case -2:
                        tvInfo.setText(R.string.printer_result_toast_3);
                        break;
                    case -3:
                        tvInfo.setText(R.string.printer_result_toast_4);
                        break;
                    case -4:
                        tvInfo.setText(R.string.printer_result_toast_5);
                        break;
                }
                btnOk.setText(R.string.printer_determine);
            }
        }

        public void destroy() {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
