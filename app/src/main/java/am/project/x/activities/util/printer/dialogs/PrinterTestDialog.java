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
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;
import am.widget.MaterialProgressImageView;

/**
 * 打印机测试对话框
 * Created by Alex on 2016/4/15.
 */
public class PrinterTestDialog extends AppCompatDialog implements View.OnClickListener {

    public static final int TYPE_80 = 0;
    public static final int TYPE_58 = 1;
    private MaterialProgressImageView mivLoading;
    private TextView tvInfo;
    private Button btnOk;
    private String ip;
    private int port;
    private int type;
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

    public void startTest(String ip, int port, int type) {
        this.ip = ip;
        this.port = port;
        this.type = type;
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
                if (type == TYPE_80)
                    data = getPrintData80();
                else
                    data = getPrintData58();
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

        private byte[] getPrintData80() throws IOException {
            PrinterWriter80mm printer = new PrinterWriter80mm();
            printer.setAlignCenter();
            printer.printDrawable(getContext().getResources(), R.drawable.ic_printer_logo);
            printer.setAlignLeft();
            printer.printLine();
            printer.printLineFeed();

            printer.printLineFeed();
            printer.setAlignCenter();
            printer.setEmphasizedOn();
            printer.setFontSize(1);
            printer.print("我的餐厅");
            printer.printLineFeed();
            printer.setFontSize(0);
            printer.setEmphasizedOff();
            printer.printLineFeed();

            printer.setLineHeight(80);
            printer.print("最时尚的明星餐厅");
            printer.printLineFeed();
            printer.print("客服电话：400-8008800");
            printer.printLineFeed();

            printer.setAlignLeft();
            printer.printLineFeed();

            printer.print("订单号：88888888888888888");
            printer.printLineFeed();

            printer.print("预计送达：" +
                    new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                            .format(new Date(System.currentTimeMillis())));
            printer.printLineFeed();

            printer.setEmphasizedOn();
            printer.print("#8（已付款）");
            printer.printLineFeed();
            printer.print("××区××路×××大厦××楼×××室");
            printer.printLineFeed();
            printer.setEmphasizedOff();
            printer.print("13843211234");
            printer.print("（张某某）");
            printer.printLineFeed();
            printer.print("备注：多加点辣椒，多加点香菜，多加点酸萝卜，多送点一次性手套");
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.printInOneLine("星级美食（豪华套餐）×1", "￥88.88", 0);
            printer.printLineFeed();
            printer.printInOneLine("星级美食（限量套餐）×1", "￥888.88", 0);
            printer.printLineFeed();
            printer.printInOneLine("餐具×1", "￥0.00", 0);
            printer.printLineFeed();
            printer.printInOneLine("配送费", "免费", 0);
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.setAlignRight();
            printer.print("合计：977.76");
            printer.printLineFeed();
            printer.printLineFeed();

            printer.setLineHeight(0);
            printer.setAlignCenter();
            printer.printDrawable(getContext().getResources(), R.drawable.ic_printer_qr);
            printer.printLineFeed();
            printer.print("扫一扫，查看详情");

            printer.feedPaperCutPartial();
            return printer.getData();
        }

        private byte[] getPrintData58() throws IOException {
            PrinterWriter58mm printer = new PrinterWriter58mm();
            printer.setAlignCenter();
            printer.printDrawable(getContext().getResources(), R.drawable.ic_printer_logo);
            printer.setAlignLeft();
            printer.printLine();
            printer.printLineFeed();

            printer.printLineFeed();
            printer.setAlignCenter();
            printer.setEmphasizedOn();
            printer.setFontSize(1);
            printer.print("我的餐厅");
            printer.printLineFeed();
            printer.setFontSize(0);
            printer.setEmphasizedOff();
            printer.printLineFeed();

            printer.setLineHeight(80);
            printer.print("最时尚的明星餐厅");
            printer.printLineFeed();
            printer.print("客服电话：400-8008800");
            printer.printLineFeed();

            printer.setAlignLeft();
            printer.printLineFeed();

            printer.print("订单号：88888888888888888");
            printer.printLineFeed();

            printer.print("预计送达：" +
                    new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                            .format(new Date(System.currentTimeMillis())));
            printer.printLineFeed();

            printer.setEmphasizedOn();
            printer.print("#8（已付款）");
            printer.printLineFeed();
            printer.print("××区××路×××大厦××楼×××室");
            printer.printLineFeed();
            printer.setEmphasizedOff();
            printer.print("13843211234");
            printer.print("（张某某）");
            printer.printLineFeed();
            printer.print("备注：多加点辣椒，多加点香菜，多加点酸萝卜，多送点一次性手套");
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.printInOneLine("星级美食（豪华套餐）×1", "￥88.88", 0);
            printer.printLineFeed();
            printer.printInOneLine("星级美食（限量套餐）×1", "￥888.88", 0);
            printer.printLineFeed();
            printer.printInOneLine("餐具×1", "￥0.00", 0);
            printer.printLineFeed();
            printer.printInOneLine("配送费", "免费", 0);
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.setAlignRight();
            printer.print("合计：977.76");
            printer.printLineFeed();
            printer.printLineFeed();

            printer.setLineHeight(0);
            printer.setAlignCenter();
            printer.printDrawable(getContext().getResources(), R.drawable.ic_printer_qr);
            printer.printLineFeed();
            printer.print("扫一扫，查看详情");

            printer.feedPaperCutPartial();
            return printer.getData();
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
