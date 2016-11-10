package am.util.printer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 打印机写入器
 * Created by Alex on 2016/4/18.
 */
public abstract class PrinterWriter {

    public static final int HEIGHT_PARTING_DEFAULT = 255;
    private static final String CHARSET = "gb2312";
    private ByteArrayOutputStream bos;
    private int heightParting;

    public PrinterWriter() throws IOException {
        this(HEIGHT_PARTING_DEFAULT);
    }

    public PrinterWriter(int parting) throws IOException {
        if (parting <= 0 || parting > HEIGHT_PARTING_DEFAULT)
            heightParting = HEIGHT_PARTING_DEFAULT;
        else
            heightParting = parting;
        init();
    }

    /**
     * 重置
     * 使用 init 替代
     *
     * @throws IOException 异常
     */
    @Deprecated
    public void reset() throws IOException {
        init();
    }

    /**
     * 初始化
     *
     * @throws IOException 异常
     */
    public void init() throws IOException {
        bos = new ByteArrayOutputStream();
        write(PrinterUtils.initPrinter());
    }

    /**
     * 获取预打印数据并关闭流
     *
     * @return 预打印数据
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    @Deprecated
    public byte[] getData() throws IOException {
        return getDataAndClose();
    }

    /**
     * 获取预打印数据并重置流
     *
     * @return 预打印数据
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public byte[] getDataAndReset() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.reset();
        return data;
    }

    /**
     * 获取预打印数据并关闭流
     *
     * @return 预打印数据
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public byte[] getDataAndClose() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.close();
        bos = null;
        return data;
    }

    /**
     * 写入数据
     *
     * @param data 数据
     * @throws IOException 异常
     */
    public void write(byte[] data) throws IOException {
        if (bos == null)
            reset();
        bos.write(data);
    }

    /**
     * 设置居中
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignCenter() throws IOException {
        write(PrinterUtils.alignCenter());
    }

    /**
     * 设置左对齐
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignLeft() throws IOException {
        write(PrinterUtils.alignLeft());
    }

    /**
     * 设置右对齐
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setAlignRight() throws IOException {
        write(PrinterUtils.alignRight());
    }

    /**
     * 开启着重
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setEmphasizedOn() throws IOException {
        write(PrinterUtils.emphasizedOn());
    }

    /**
     * 关闭着重
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setEmphasizedOff() throws IOException {
        write(PrinterUtils.emphasizedOff());
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小 （0～7）（默认0）
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setFontSize(int size) throws IOException {
        write(PrinterUtils.fontSizeSetBig(size));
    }

    /**
     * 设置行高度
     *
     * @param height 行高度
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void setLineHeight(int height) throws IOException {
        if (height >= 0 && height <= 255)
            write(PrinterUtils.printLineHeight((byte) height));
    }

    /**
     * 写入字符串
     *
     * @param string 字符串
     * @throws IOException 异常
     */
    public void print(String string) throws IOException {
        print(string, CHARSET);
    }

    /**
     * 写入字符串
     *
     * @param string      字符串
     * @param charsetName 编码方式
     * @throws IOException 异常
     */
    public void print(String string, String charsetName) throws IOException {
        if (string == null)
            return;
        write(string.getBytes(charsetName));
    }

    /**
     * 写入一条横线
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printLine() throws IOException {
        int length = getLineWidth();
        String line = "";
        while (length > 0) {
            line += "─";
            length--;
        }
        print(line);
    }

    /**
     * 获取横线线宽
     *
     * @return 横线线宽
     */
    protected abstract int getLineWidth();

    /**
     * 一行输出
     *
     * @param str1     字符串
     * @param str2     字符串
     * @param textSize 文字大小
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printInOneLine(String str1, String str2, int textSize) throws IOException {
        printInOneLine(str1, str2, textSize, CHARSET);
    }

    /**
     * 一行输出
     *
     * @param str1        字符串
     * @param str2        字符串
     * @param textSize    文字大小
     * @param charsetName 编码方式
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printInOneLine(String str1, String str2, int textSize, String charsetName) throws IOException {
        int lineLength = getLineStringWidth(textSize);
        int needEmpty = lineLength - (getStringWidth(str1) + getStringWidth(str2)) % lineLength;
        String empty = "";
        while (needEmpty > 0) {
            empty += " ";
            needEmpty--;
        }
        print(str1 + empty + str2, charsetName);
    }

    /**
     * 获取一行字符串长度
     *
     * @param textSize 文字大小
     * @return 一行字符串长度
     */
    protected abstract int getLineStringWidth(int textSize);

    private int getStringWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            width += isChinese(c) ? 2 : 1;
        }
        return width;
    }

    /**
     * 打印 Drawable 图片
     *
     * @param res Resources
     * @param id  资源ID
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    @Deprecated
    public void printDrawable(Resources res, int id) throws IOException {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingBitmap(res, id, maxWidth);
        if (image == null)
            return;
        byte[] command = PrinterUtils.decodeBitmap(image, heightParting);
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
     * 获取图片数据流
     *
     * @param res Resources
     * @param id  资源ID
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Resources res, int id) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingBitmap(res, id, maxWidth);
        if (image == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 获取图片最大宽度
     *
     * @return 图片最大宽度
     */
    protected abstract int getDrawableMaxWidth();

    /**
     * 缩放图片
     *
     * @param res      资源
     * @param id       ID
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingBitmap(Resources res, int id, int maxWidth) {
        if (res == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置只量取宽高
        BitmapFactory.decodeResource(res, id, options);// 量取宽高
        options.inJustDecodeBounds = false;
        // 粗略缩放
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
            // 精确缩放
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

    /**
     * 打印 Drawable 图片
     *
     * @param drawable 图片
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    @Deprecated
    public void printDrawable(Drawable drawable) throws IOException {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingDrawable(drawable, maxWidth);
        if (image == null)
            return;
        byte[] command = PrinterUtils.decodeBitmap(image, heightParting);
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
     * 获取图片数据流
     *
     * @param drawable 图片
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Drawable drawable) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap image = scalingDrawable(drawable, maxWidth);
        if (image == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 缩放图片
     *
     * @param drawable 图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingDrawable(Drawable drawable, int maxWidth) {
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0)
            return null;
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();
        try {
            Bitmap image = Bitmap.createBitmap(width, height,
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(image);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            // 精确缩放
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

    /**
     * 打印 Bitmap 图片
     *
     * @param image 图片
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    @Deprecated
    public void printBitmap(Bitmap image) throws IOException {
        int maxWidth = getDrawableMaxWidth();
        Bitmap scalingImage = scalingBitmap(image, maxWidth);
        if (scalingImage == null)
            return;
        byte[] command = PrinterUtils.decodeBitmap(scalingImage, heightParting);
        scalingImage.recycle();
        try {
            if (command != null) {
                write(command);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 获取图片数据流
     *
     * @param image 图片
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(Bitmap image) {
        int maxWidth = getDrawableMaxWidth();
        Bitmap scalingImage = scalingBitmap(image, maxWidth);
        if (scalingImage == null)
            return null;
        ArrayList<byte[]> data = PrinterUtils.decodeBitmapToDataList(image, heightParting);
        image.recycle();
        return data;
    }

    /**
     * 缩放图片
     *
     * @param image    图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    private Bitmap scalingBitmap(Bitmap image, int maxWidth) {
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0)
            return null;
        try {
            final int width = image.getWidth();
            final int height = image.getHeight();
            // 精确缩放
            float scale = 1;
            if (maxWidth <= 0 || width <= maxWidth) {
                scale = maxWidth / (float) width;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 打印图片文件
     *
     * @param filePath 图片
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    @Deprecated
    public void printImageFile(String filePath) throws IOException {
        Bitmap image;
        try {
            int width;
            int height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            width = options.outWidth;
            height = options.outHeight;
            if (width <= 0 || height <= 0)
                return;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            image = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError | Exception e) {
            return;
        }
        printBitmap(image);
    }

    /**
     * 获取图片数据流
     *
     * @param filePath 图片路径
     * @return 数据流
     */
    public ArrayList<byte[]> getImageByte(String filePath) {
        Bitmap image;
        try {
            int width;
            int height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            width = options.outWidth;
            height = options.outHeight;
            if (width <= 0 || height <= 0)
                return null;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            image = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError | Exception e) {
            return null;
        }
        return getImageByte(image);
    }

    /**
     * 输出并换行
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void printLineFeed() throws IOException {
        write(PrinterUtils.printLineFeed());
    }

    /**
     * 进纸切割
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void feedPaperCut() throws IOException {
        write(PrinterUtils.feedPaperCut());
    }

    /**
     * 进纸切割（留部分）
     *
     * @throws IOException 异常
     */
    @SuppressWarnings("unused")
    public void feedPaperCutPartial() throws IOException {
        write(PrinterUtils.feedPaperCutPartial());
    }

    /**
     * 设置图片打印高度分割值
     * 最大允许255像素
     *
     * @param parting 高度分割值
     */
    @SuppressWarnings("unused")
    public void setHeightParting(int parting) {
        if (parting <= 0 || parting > HEIGHT_PARTING_DEFAULT)
            return;
        heightParting = parting;
    }

    /**
     * 获取图片打印高度分割值
     *
     * @return 高度分割值
     */
    @SuppressWarnings("unused")
    public int getHeightParting() {
        return heightParting;
    }

    /**
     * 判断是否中文
     * GENERAL_PUNCTUATION 判断中文的“号
     * CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
     * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
     *
     * @param c 字符
     * @return 是否中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
}
