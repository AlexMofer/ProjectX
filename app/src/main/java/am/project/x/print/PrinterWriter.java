package am.project.x.print;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import am.project.x.R;
import am.project.x.utils.StringUtils;

/**
 * 打印机写入器
 * Created by Alex on 2016/4/18.
 */
public class PrinterWriter {

    public static final int TYPE_58mm = 0;// 58mm宽
    public static final int TYPE_80mm = 1;// 80mm宽
    private static final String CHARSET = "gb2312";
    private ByteArrayOutputStream bos;
    private int type;

    public PrinterWriter() throws IOException {
        this(TYPE_80mm);
    }

    public PrinterWriter(int type) throws IOException {
        this.type = type;
        reset();
    }

    /**
     * 重置
     *
     * @throws IOException
     */
    public void reset() throws IOException {
        bos = new ByteArrayOutputStream();
        write(PrinterUtils.initPrinter());
    }

    private void write(byte[] data) throws IOException {
        if (bos == null)
            reset();
        bos.write(data);
    }

    public byte[] getData() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.close();
        bos = null;
        return data;
    }

    public void testDelivery(Context context) throws IOException {
        createDeliveryStiff(PrinterData.getTest(), context);
    }

    public void testKitchen() throws IOException {
        createKitchenStiff(PrinterData.PrinterDishData.getTest());
    }

    public void createDeliveryStiff(PrinterData data, Context context) throws IOException {
        if (data == null)
            return;
        write(PrinterUtils.alignCenter());// 居中对齐
        printDrawable(context.getResources(), R.drawable.old_ic_print_logo);// 打印 Mazing Logo

        write(PrinterUtils.alignLeft());// 左对齐
        writeLine();// 绘制横线
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.alignCenter());// 居中对齐
        write(PrinterUtils.emphasizedOn());// 开启着重
        write(PrinterUtils.fontSizeSetBig(1));//字体1倍大小
        writeString(data.getStoreName());
        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.emphasizedOff());// 关闭着重
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.printLineHeight((byte) 80));
        write(PrinterUtils.fontSizeSetBig(0));//字体0倍大小
        writeString(data.getStoreInfo());
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("客服电话：" + data.getStorePhone());
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.alignLeft());// 左对齐
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("订单号：" + data.getOrderNoStr());
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("预计送达：" + data.getDeliveryTime());
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.emphasizedOn());// 开启着重
        writeString(data.getSerialNumber());
        writeString(data.getPayType() == PrinterData.PAY_TYPE_ONLINE ?
                "（已付款）" : "（未付款）");
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString(data.getAddress());
        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.emphasizedOff());// 关闭着重

        writeString(data.getPhone());
        writeString("（" + data.getContacts() + "）");
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("备注：" + data.getRemark());
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeLine();// 绘制横线
        write(PrinterUtils.printLineFeed());// 输出并换行

        for (PrinterData.PrinterDishData dish : data.getDishes()) {
            String str1 = "" + dish.getDishName();
            if (dish.hasDishSpec()) {
                str1 += "（" + dish.getDishSpec() + "）";
            }
            str1 += dish.getDishCount();
            writeStringOneLine(str1, dish.getDishFee(), 0);
            write(PrinterUtils.printLineFeed());// 输出并换行
        }
        writeStringOneLine("配送费", data.getDeliveryFee(), 0);
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeLine();// 绘制横线
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.alignRight());// 右对齐
        writeString("合计：" + data.getTotalFee());
        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.alignCenter());// 居中对齐
        printDrawable(context.getResources(), R.drawable.old_ic_print_qr);
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("多谢惠顾！扫一扫 查看订单");
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.feedPaperCutPartial());
    }

    public void createKitchenStiff(PrinterData.PrinterDishData dish) throws IOException {
        if (dish == null)
            return;
        write(PrinterUtils.alignLeft());// 左对齐

        write(PrinterUtils.emphasizedOn());// 开启着重
        write(PrinterUtils.fontSizeSetBig(1));//字体1倍大小
        writeString(dish.getSerialNumber());// 打印流水号
        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.emphasizedOff());// 关闭着重

        write(PrinterUtils.fontSizeSetBig(0));//字体0倍大小
        writeLine();// 绘制横线
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.emphasizedOn());// 开启着重
        write(PrinterUtils.fontSizeSetBig(1));//字体1倍大小
        String name = dish.hasDishSpec() ? dish.getDishName() + "（" + dish.getDishSpec() + "）" :
                dish.getDishName();// 名称与规格组合
        writeStringOneLine(name, dish.getDishCount(), 1);// 一行输出
        write(PrinterUtils.printLineFeed());// 输出并换行
        write(PrinterUtils.emphasizedOff());// 关闭着重
        write(PrinterUtils.fontSizeSetBig(0));//字体0倍大小
        write(PrinterUtils.printLineFeed());// 输出并换行

        writeString("备注：");
        writeString(dish.getDishRemark());
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.printLineFeed());// 输出并换行
        writeString("预计送达：");
        writeString(dish.getDeliveryTime());
        write(PrinterUtils.printLineFeed());// 输出并换行

        write(PrinterUtils.feedPaperCutPartial());
    }

    private void writeLine() throws IOException {
        // TODO TYPE_58mm未验证
        int length = type == TYPE_58mm ? 12 : 24;
        String line = "";
        while (length > 0) {
            line += "─";
            length--;
        }
        writeString(line);
    }

    /**
     * 写入字符串
     *
     * @param string 字符串
     * @throws IOException
     */
    private void writeString(String string) throws IOException {
        if (string == null)
            return;
        write(string.getBytes(CHARSET));
    }

    /**
     * 一行输出
     *
     * @param str1 字符串
     * @param str2 字符串
     */
    private void writeStringOneLine(String str1, String str2, int textSize) throws IOException {
        int lineLength;
        if (type == TYPE_58mm) {
            // TODO TYPE_58mm未验证
            lineLength = 23;
        } else {
            switch (textSize) {
                default:
                case 0:
                    lineLength = 47;
                    break;
                case 1:
                    lineLength = 23;
                    break;
            }
        }
        int needEmpty = lineLength - (getStringWidth(str1) + getStringWidth(str2)) % lineLength;
        String empty = "";
        while (needEmpty > 0) {
            empty += " ";
            needEmpty--;
        }
        writeString(str1 + empty + str2);
    }

    private int getStringWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            width += StringUtils.isChinese(c) ? 2 : 1;
        }
        return width;
    }

    /**
     * 打印 Drawable 图片
     *
     * @param res Resources
     * @param id  资源ID
     * @throws IOException
     */
    private void printDrawable(Resources res, int id) throws IOException {
        // TODO TYPE_58mm未验证
        int maxWidth = type == TYPE_58mm ? 300 : 500;
        Bitmap image = scalingBitmap(res, id, maxWidth);
        if (image == null)
            return;
        byte[] command = PrinterUtils.decodeBitmap(image);
        image.recycle();
        try {
            if (command != null) {
                write(command);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 缩放图片
     *
     * @param res      资源
     * @param id       ID
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingBitmap(Resources res, int id, int maxWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置只量取宽高
        BitmapFactory.decodeResource(res, id, options);// 量取宽高
        options.inJustDecodeBounds = false;
        if (maxWidth > 0 && options.outWidth > maxWidth) {
            // 超过限定宽
            double ratio = options.outWidth / (double) maxWidth;// 计算缩放比
            int sampleSize = (int) Math.floor(ratio);// 向下取整，保证缩放后不会低于最大宽高
            if (sampleSize > 1) {
                options.inSampleSize = sampleSize;// 设置缩放比，原图的几分之一
            }
        }
        try {
            Bitmap image = BitmapFactory.decodeResource(res, id, options);
            final int width = image.getWidth();
            final int height = image.getHeight();
            if (maxWidth <= 0 || width <= maxWidth) {
                return image;
            }
            final float scale = maxWidth / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
            image.recycle();
            return resizeImage;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
