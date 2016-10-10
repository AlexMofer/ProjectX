package am.project.x.activities.util.printer.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import am.project.x.R;
import am.project.x.utils.FileUtils;
import am.project.x.utils.QRCodeUtil;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * 打印数据
 * Created by Alex on 2016/6/22.
 */
class PrinterData {

    static byte[] getPrintData80(Context context, String qrContent) throws IOException {
        PrinterWriter80mm printer = new PrinterWriter80mm();
        printer.setAlignCenter();
        printer.printDrawable(context.getResources(), R.drawable.ic_printer_logo);
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

        String bitmapPath = FileUtils.getExternalFilesDir(context, "Temp") + "tmp_qr.jpg";
        if (QRCodeUtil.createQRImage(qrContent, 200, 200, null, bitmapPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmapQR = BitmapFactory.decodeFile(bitmapPath, options);
            printer.printBitmap(bitmapQR);
        } else {
            printer.printDrawable(context.getResources(), R.drawable.ic_printer_qr);
        }
        printer.printLineFeed();
        printer.print("扫一扫，查看详情");
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();

        printer.feedPaperCutPartial();
        return printer.getData();
    }

    static byte[] getPrintData58(Context context, String qrContent) throws IOException {
        PrinterWriter58mm printer = new PrinterWriter58mm();
        printer.setAlignCenter();
        printer.printDrawable(context.getResources(), R.drawable.ic_printer_logo);
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

        printer.setAlignCenter();


        String bitmapPath = FileUtils.getExternalFilesDir(context, "Temp") + "tmp_qr.jpg";
        if (QRCodeUtil.createQRImage(qrContent, 200, 200, null, bitmapPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmapQR = BitmapFactory.decodeFile(bitmapPath, options);
            printer.printBitmap(bitmapQR);
        } else {
            printer.printDrawable(context.getResources(), R.drawable.ic_printer_qr);
        }
        printer.printLineFeed();
        printer.print("扫一扫，查看详情");
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();
        printer.printLineFeed();

        printer.feedPaperCutPartial();
        return printer.getData();
    }
}
