package com.am.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2015/9/22.
 */


@SuppressWarnings("all")
public class PrintTools {

    public static final byte HT = 0x9; // 水平制表
    public static final byte LF = 0x0A; // 打印并换行
    public static final byte CR = 0x0D; // 打印回车
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;
    public static final byte[] LOCATION = {0x1B, 0x44, 0x04, 0x00};
    /* 默认颜色字体指令 */
    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[]{ESC, 'r', 0x00};
    /* 标准大小 */
    public static final byte[] FS_FONT_ALIGN = new byte[]{FS, 0x21, 1, ESC,
            0x21, 1};
    /* 靠左打印命令 */
    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};

    /* 靠右打印命令 */
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};

    /* 居中打印命令 */
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};

    /* 取消字体加粗 */
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{ESC, 0x45, 0};


    /*********************************************/
    /* 水平定位 */
    public static final byte[] ESC_HORIZONTAL_CENTERS = new byte[]{ESC, 0x44, 20, 28, 00};

    /* 取消水平定位 */
    public static final byte[] ESC_CANCLE_HORIZONTAL_CENTERS = new byte[]{ESC, 0x44, 00};
    /*********************************************/


    // 进纸
    public static final byte[] ESC_ENTER = new byte[]{0x1B, 0x4A, 0x40};

    // 自检
    public static final byte[] PRINTE_TEST = new byte[]{0x1D, 0x28, 0x41};

    // 测试输出Unicode Pirit Message
    public static final byte[] UNICODE_TEXT = new byte[]{0x00, 0x50, 0x00,
            0x72, 0x00, 0x69, 0x00, 0x6E, 0x00, 0x74, 0x00, 0x20, 0x00, 0x20,
            0x00, 0x20, 0x00, 0x4D, 0x00, 0x65, 0x00, 0x73, 0x00, 0x73, 0x00,
            0x61, 0x00, 0x67, 0x00, 0x65};

    /**
     * print test 打印机自检
     */
    public static void printTest() {
        writeEnterLine(1);
        print(PRINTE_TEST);
        writeEnterLine(3);
    }

    /**
     * print text 打印文字
     */
    public static void printText_Unicode(String text) {
        print(ESC_ALIGN_CENTER);
        writeEnterLine(1);
        print(text);
        try {
            Log.e("unicode", ConvertUtil.binaryToHexString(text.getBytes("unicode")));
            String uMsg = UnicodeUtil.getUNICODEBytes(text);
            Log.e("uMsg", uMsg);
            print(UNICODE_TEXT);
            writeEnterLine(1);
            //		resetPrint();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void enterLine() {
        byte[] b = new byte[]{0x1B, 0x21, 0x00};
        print(b);
    }

    public static void setLineLegth() {
        byte[] b = new byte[]{0x1B, 0x33, 0x33};
        print(b);
    }

    /**
     * print text 打印文字
     */
    public static void printTextLine(String text, String str) {

        print(ESC_ALIGN_LEFT);
        print(ESC_HORIZONTAL_CENTERS);
        print_Unicode(text, str);
        print(LF);
    }


    /**
     * print text 打印文字
     */
    public static void printTextCenterCmd() {
        print(ESC_ALIGN_LEFT);
        print(ESC_HORIZONTAL_CENTERS);
    }

    /**
     * print text 打印文字
     */
    public static void printText(String text, String str) {

        print(ESC_ALIGN_LEFT);
        print_Unicode(text, str);
        print(LF);
    }

    /**
     * print text 打印文字
     */
    public static void printTitle(String text, String str) {
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};//字体加粗
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20};//字体背宽
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10};//字体背高
        byte[] cc = new byte[]{0x1B, 0x21, 0x00};
        print(bb);
        print(bb2);
        print(bb3);
        print(ESC_ALIGN_CENTER);
        print_Unicode(text, str);
        print(LF);
        print(LF);
        print(cc);
    }

    public static void printTextCenter(String text, String str) {
        print_Unicode(text, str);
        print(HT);
    }

    public static void printTextRight(String text, String str) {
        print(ESC_ALIGN_RIGHT);
        print_Unicode(text, str);
        print(LF);
    }

    public static void HT() {

    }

    public static void setLocation(int i) {
        byte[] bytes = new byte[]{0x1B, 0x44, (byte) i, 0x00};
        print(bytes);
    }

    public static void setLocation() {
        byte[] bytes = new byte[]{0x1B, 0x44, (byte) 0x0C, 0x18, 0x24, 0x00};
        print(bytes);
    }

    public static void printEnter() {

//		print(LF);
        printCode(LF);
        writeEnterLine(1);
    }


    /**
     * print photo with path 根据图片路径打印图片
     *
     * @param 图片在SD卡路径，如:photo/pic.bmp
     */
    public static void printPhotoWithPath(String filePath) {

        String SDPath = Environment.getExternalStorageDirectory() + "/";
        String path = SDPath + filePath;

        // 根据路径获取图片
        File mfile = new File(path);
        if (mfile.exists()) {// 若该文件存在
            Bitmap bmp = BitmapFactory.decodeFile(path);
            byte[] command = decodeBitmap(bmp);
            printPhoto(command);
        } else {
            Log.e("PrintTools_58mm", "the file isn't exists");
        }
    }

    /**
     * print photo in assets 打印assets里的图片
     *
     * @param 图片在assets目录，如:pic.bmp
     */
    public static void printPhotoInAssets(Context context, String fileName) {

        AssetManager asm = context.getResources().getAssets();
        InputStream is;
        try {
            is = asm.open(fileName);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                byte[] command = decodeBitmap(bmp);
                printPhoto(command);
            } else {
                Log.e("PrintTools", "the file isn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    /**
     * decode bitmap to bytes 解码Bitmap为位图字节流
     */
    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>(); //binaryString list
        StringBuffer sb;

        // 每行字节数(除以8，不足补0)
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        // 每行需要补充的0
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        // 逐个读取像素颜色，将非白色改为黑色
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i); // 获得Bitmap 图片中每一个点的color颜色值
                //颜色值的R G B
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            // 每一行结束时，补充剩余的0
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        // binaryStr每8位调用一次转换方法，再拼合
        List<String> bmpHexList = ConvertUtil.binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        // 宽度指令
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", "宽度超出 width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        // 高度指令
        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", "高度超出 height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return ConvertUtil.hexList2Byte(commandList);
    }

    /**
     * print photo with bytes 根据指令打印图片
     */
    public static void printPhoto(byte[] bytes) {
        print(ESC_ALIGN_CENTER);
        writeEnterLine(1);
        print(bytes);
        writeEnterLine(3);
    }

    /**
     * reset 重置格式
     */
    public static void resetPrint() {

        print(ESC_FONT_COLOR_DEFAULT);
        print(FS_FONT_ALIGN);
        print(ESC_ALIGN_LEFT);
        print(ESC_CANCEL_BOLD);
        print(LF);
    }

    /**
     * 串口是否就绪
     */
    public static boolean allowTowrite() {
        //return C.printSerialPortTools != null;
        return false;
    }

    /**
     * 输出
     *
     * @param String内容
     */
    public static void print(String msg) {
//        if (allowTowrite())
//            C.printSerialPortTools.write(msg);
    }

    public static void print_unicode(String msg) {
//        if (allowTowrite())
//            C.printSerialPortTools.write_unicode(msg);
    }

    public static void print_Unicode(String msg, String str) {
//        if (allowTowrite())
//            C.printSerialPortTools.write_Unicode(msg, str);
    }

    public static void print_Chinese(String msg, String str) {
//        if (allowTowrite()) {
//            C.printSerialPortTools.write_Chinese(msg, str);
//        }
    }


    /**
     * 输出
     *
     * @param byte[]指令
     */
    public static void print(byte[] b) {
//        if (allowTowrite())
//            C.printSerialPortTools.write(b);
    }

    /**
     * 输出
     *
     * @param int指令
     */
    public static void print(int oneByte) {
//        if (allowTowrite())
//            C.printSerialPortTools.write(oneByte);
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

    public static void printCode(int oneByte) {
        if (allowTowrite()) {
//            C.printSerialPortTools.write(oneByte);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * EnterLine 进纸
     *
     * @param 进纸行数
     */
    public static void writeEnterLine(int count) {
        for (int i = 0; i < count; i++) {
            print(ESC_ENTER);

        }

    }

    public static String getEnterLine(int count) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ESC_ENTER);
        return sBuilder.toString();
    }


}

