package am.project.x.activities.old;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import am.project.x.R;

/**
 * 打印机
 * Created by Alex on 2015/9/21.
 */
public class PrinterActivity extends Activity implements View.OnClickListener {

    private static final String IP = "192.168.1.102";
    private static final String PORT = "9100";
    private static final String TIME = "10";
    private EditText edtIP, edtPort, edtFor;
    private Button btnPrint, btnPrintPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_printer);
        edtIP = (EditText) findViewById(R.id.printer_edt_ip);
        edtPort = (EditText) findViewById(R.id.printer_edt_port);
        edtFor = (EditText) findViewById(R.id.printer_edt_times);
        btnPrint = (Button) findViewById(R.id.printer_btn_print);
        btnPrintPic = (Button) findViewById(R.id.printer_btn_printpic);
        edtIP.setText(IP);
        edtPort.setText(PORT);
        edtFor.setText(TIME);
        btnPrint.setOnClickListener(this);
        btnPrintPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printer_btn_print:
                new PrintTask().execute();
                break;
            case R.id.printer_btn_printpic:
                new PrintPicTask().execute();
                break;
        }
    }

    private class PrintTask extends AsyncTask<Void, Void, Void> {

        private String ip;
        private int port;
        private Socket socket;
        private OutputStream out;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ip = edtIP.getText().toString();
            port = Integer.parseInt(edtPort.getText().toString());

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(ip, port);
                out = socket.getOutputStream();
                print();
                destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void destroy() {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void print() {
            try {
                if (out == null) {
                    return;
                }
                writePaper(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class PrintPicTask extends AsyncTask<Void, Void, Void> {

        private String ip;
        private int port;
        private Socket socket;
        private OutputStream out;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ip = edtIP.getText().toString();
            port = Integer.parseInt(edtPort.getText().toString());

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(ip, port);
                out = socket.getOutputStream();
                print();
                destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void destroy() {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void print() {
            try {
                if (out == null) {
                    return;
                }
                writePic(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void writePaper(OutputStream outputStream) throws IOException {
        outputStream.write(PrinterUtils.initPrinter());
        outputStream.write(PrinterUtils.fontSizeSetBig(2));
        outputStream.write(PrinterUtils.alignCenter());
        outputStream.write("好好食".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printAndFeedLines((byte) 1));

        outputStream.write(PrinterUtils.fontSizeSetBig(0));
        outputStream.write("好好食烧鸡金香爽滑 只此一家".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("客服电话：400-8888888".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printAndFeedLines((byte) 1));

        outputStream.write(PrinterUtils.alignLeft());
        outputStream.write("订单号：15879869708986".getBytes("gb2312"));

        outputStream.write(PrinterUtils.alignRight());
        outputStream.write("下单时间：2015/08/08".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignCenter());
        outputStream.write(PrinterUtils.emphasizedOn());
        outputStream.write(PrinterUtils.underLine(false, 2));
        outputStream.write("                                          ".getBytes("gb2312"));
        outputStream.write(PrinterUtils.underLine(false, 0));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.alignLeft());
        outputStream.write("#11 08/08（已付款）".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("天河区海乐路南国奥园 1006".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("18977766748（李生）".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("口味选择/微辣".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("留言：无".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.alignCenter());
        outputStream.write(PrinterUtils.underLine(false, 2));
        outputStream.write("                                          ".getBytes("gb2312"));
        outputStream.write(PrinterUtils.underLine(false, 0));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignLeft());
        outputStream.write(PrinterUtils.printLineHeight((byte) 80));

        outputStream.write("麻辣小龙虾(400克/份) x1           ￥ 138.9".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());


        outputStream.write("桥底辣大膏蟹(400克/份) x1         ￥ 638.0".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write("餐具 x1                             ￥ 0.0".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write("配送费                                免收".getBytes("gb2312"));

        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.printLineNormalHeight());
        outputStream.write(PrinterUtils.alignRight());
        outputStream.write("合计：￥776.9".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.emphasizedOff());
        outputStream.write(PrinterUtils.alignLeft());
        printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_logo.png", outputStream);
        outputStream.write(PrinterUtils.underLine(true, 2));
        outputStream.write(PrinterUtils.underLine(false, 2));
        outputStream.write("互联网餐饮平台                  mazing.com".getBytes("gb2312"));
        outputStream.write(PrinterUtils.underLine(true, 0));
        outputStream.write(PrinterUtils.underLine(false, 0));
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.emphasizedOff());
        outputStream.write(PrinterUtils.alignCenter());
        printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_QR.png", outputStream);
        outputStream.write(PrinterUtils.printLineFeed());
        outputStream.write("扫一扫 查看订单".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());



        outputStream.write(PrinterUtils.feedPaperCutPartial());

        outputStream.flush();

    }

    public void writePic(OutputStream outputStream) throws IOException {
        for (int i = 0; i < Integer.parseInt(edtFor.getText().toString()); i++) {
            printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_1.jpg", outputStream);
            printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_2.jpg", outputStream);
            printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_3.jpg", outputStream);
            printPhotoInAssets(getApplicationContext(), "old_dnet_bitmap_4.jpg", outputStream);
            outputStream.write(PrinterUtils.feedPaperCutPartial());
        }
        outputStream.flush();
    }


    /**
     * print photo in assets 打印assets里的图片
     * <p/>
     * 图片在assets目录，如:pic.bmp
     */
    public void printPhotoInAssets(Context context, String fileName, OutputStream outputStream) {

        AssetManager asm = context.getResources().getAssets();
        InputStream is;
        try {
            is = asm.open(fileName);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                byte[] command = PrinterUtils.decodeBitmap(bmp);
                if (command != null) {
                    outputStream.write(command);
                }
            } else {
                Log.e("PrintTools", "the file isn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }


    public static void writePaperTest(OutputStream outputStream) throws IOException {

        outputStream.write(PrinterUtils.initPrinter());
        outputStream.write("测试文字".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.underLine(true, 1));
        outputStream.write("下划线一点".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.underLine(true, 2));
        outputStream.write("下划线二点".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.underLine(true, 0));
        outputStream.write("下划线关闭".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.emphasizedOn());
        outputStream.write("开启强调模式".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.emphasizedOff());
        outputStream.write("关闭强调模式".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());


        outputStream.write(PrinterUtils.doubleStrikeOn());
        outputStream.write("开启DoubleStrike".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.doubleStrikeOff());
        outputStream.write("关闭DoubleStrike".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.selectFontA());
        outputStream.write("选择字体A selectFontA".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.selectFontB());
        outputStream.write("选择字体B selectFontB".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.selectFontA());
        outputStream.write(PrinterUtils.doubleHeightWidthOn());
        outputStream.write("开启双倍字宽 doubleHeightWidthOn".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.doubleHeightOn());
        outputStream.write("关闭双倍字宽 doubleHeightWidthOn".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.doubleHeightWidthOff());
        outputStream.write("关闭双倍字宽 doubleHeightWidthOn".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignLeft());
        outputStream.write("左对齐 alignLeft".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignCenter());
        outputStream.write("居中对齐 alignCenter".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignRight());
        outputStream.write("右对齐 alignRight".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.alignLeft());

        outputStream.write(PrinterUtils.printAndFeedLines((byte) 1));
        outputStream.write("打印空行".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printAndReverseFeedLines((byte) 1));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.selectColor1());
        outputStream.write("选择颜色1 selectColor1".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.selectColor2());
        outputStream.write("选择颜色2 selectColor2".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.whitePrintingOn());
        outputStream.write("开启白色打印？ whitePrintingOn".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.whitePrintingOff());
        outputStream.write("关闭白色打印？ whitePrintingOff".getBytes("gb2312"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.printAndFeedLines((byte) 3));
        outputStream.write(PrinterUtils.select_position_hri((byte) 2));
        outputStream.write(PrinterUtils.print_bar_code(PrinterUtils.BarCode.CODE39, "123456789"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.printAndFeedLines((byte) 2));
        outputStream.write(PrinterUtils.barcode_height((byte) 80));
        outputStream.write(PrinterUtils.alignCenter());
        outputStream.write(PrinterUtils.select_position_hri((byte) 1));
        outputStream.write(PrinterUtils.print_bar_code(PrinterUtils.BarCode.EAN13, "9783125171541"));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.fontSizeSetBig(0));
        outputStream.write("字体0 fontSizeSetBig".getBytes("gb2312"));
        outputStream.write(PrinterUtils.fontSizeSetBig(0));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.fontSizeSetBig(1));
        outputStream.write("字体1 fontSizeSetBig".getBytes("gb2312"));
        outputStream.write(PrinterUtils.fontSizeSetBig(0));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.fontSizeSetBig(2));
        outputStream.write("字体2 fontSizeSetBig".getBytes("gb2312"));
        outputStream.write(PrinterUtils.fontSizeSetBig(0));
        outputStream.write(PrinterUtils.printLineFeed());

        outputStream.write(PrinterUtils.feedPaperCutPartial());
        outputStream.flush();


//

    }

}
