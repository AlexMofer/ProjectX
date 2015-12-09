package com.am.activity;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ESC-POS指令集
 * Created by Alex on 2015/9/22.
 */
public class PrinterUtils {

    public static final byte ESC = 27;//换码
    public static final byte FS = 28;//文本分隔符
    public static final byte GS = 29;//组分隔符
    public static final byte DLE = 16;//数据连接换码
    public static final byte EOT = 4;//传输结束
    public static final byte ENQ = 5;//询问字符
    public static final byte SP = 32;//空格
    public static final byte HT = 9;//横向列表
    public static final byte LF = 10;//打印并换行（水平定位）
    public static final byte CR = 13;//归位键
    public static final byte FF = 12;//走纸控制（打印并回到标准模式（在页模式下） ）
    public static final byte CAN = 24;//作废（页模式下取消打印数据 ）

    /**
     * CodePage table
     */
    public static class CodePage {
        public static final byte PC437 = 0;
        public static final byte KATAKANA = 1;
        public static final byte PC850 = 2;
        public static final byte PC860 = 3;
        public static final byte PC863 = 4;
        public static final byte PC865 = 5;
        public static final byte WPC1252 = 16;
        public static final byte PC866 = 17;
        public static final byte PC852 = 18;
        public static final byte PC858 = 19;
    }


    /**
     * BarCode table
     */
    public static class BarCode {
        public static final byte UPC_A = 0;
        public static final byte UPC_E = 1;
        public static final byte EAN13 = 2;
        public static final byte EAN8 = 3;
        public static final byte CODE39 = 4;
        public static final byte ITF = 5;
        public static final byte NW7 = 6;
        //public static final byte CODE93      = 72;
        // public static final byte CODE128     = 73;
    }

    /**
     * 初始化打印机
     * Clears the data in the print buffer and resets the printer modes to the modes that were
     * in effect when the power was turned on.
     * ESC @
     *
     * @return bytes for this command
     */
    public static byte[] initPrinter() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 64;
        return result;
    }

    /**
     * 打印并换行
     * LF
     *
     * @return bytes for this command
     */
    public static byte[] printLineFeed() {
        byte[] result = new byte[1];
        result[0] = LF;
        return result;
    }

    /**
     * 下划线
     * ESC - n/FS - n
     *
     * @param cn  是否为中文
     * @param dot 线宽 （0表示关闭）
     * @return bytes for this command
     */
    public static byte[] underLine(boolean cn, int dot) {
        byte[] result = new byte[3];
        result[0] = cn ? FS : ESC;
        result[1] = 45;
        switch (dot) {
            default:
            case 0:
                result[2] = 0;
                break;
            case 1:
                result[2] = 1;
                break;
            case 2:
                result[2] = 2;
                break;
        }
        return result;
    }

    /**
     * 开启着重强调(加粗)
     * ESC E n
     *
     * @return bytes for this command
     */
    public static byte[] emphasizedOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    /**
     * 关闭着重强调(加粗)
     * ESC E n
     *
     * @return bytes for this command
     */
    public static byte[] emphasizedOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

    public static byte[] overlappingOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 47;
        result[2] = 0xF;
        return result;
    }

    public static byte[] overlappingOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 47;
        result[2] = 0;
        return result;
    }

    /**
     * 开启 double-strike 模式
     * ESC G n
     *
     * @return bytes for this command
     */
    public static byte[] doubleStrikeOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0xF;
        return result;
    }

    /**
     * 关闭 double-strike 模式
     * ESC G n
     *
     * @return bytes for this command
     */
    public static byte[] doubleStrikeOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0;
        return result;
    }

    /**
     * Select Font A
     * ESC M n
     *
     * @return bytes for this command
     */
    public static byte[] selectFontA() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 0;
        return result;
    }

    /**
     * Select Font B
     * ESC M n
     *
     * @return bytes for this command
     */
    public static byte[] selectFontB() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 1;
        return result;
    }

    /**
     * Select Font C ( some printers don't have font C )
     * ESC M n
     *
     * @return bytes for this command
     */
    public static byte[] selectFontC() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 2;
        return result;
    }

    /**
     * Select Font A
     * FS ! n
     *
     * @return bytes for this command
     */
    public static byte[] selectCNFontA() {
        byte[] result = new byte[3];
        result[0] = FS;
        result[1] = 33;
        result[2] = 0;
        return result;
    }

    /**
     * Select Font B
     * FS ! n
     *
     * @return bytes for this command
     */
    public static byte[] selectCNFontB() {
        byte[] result = new byte[3];
        result[0] = FS;
        result[1] = 33;
        result[2] = 1;
        return result;
    }

    /**
     * 关闭双倍字高
     * ESC ! n
     *
     * @return bytes for this command
     */
    public static byte[] doubleHeightWidthOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 0;
        return result;
    }

    /**
     * 双倍字高（仅英文字体有效）
     * ESC ! n
     *
     * @return bytes for this command
     */
    public static byte[] doubleHeightOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 16;
        return result;
    }

    /**
     * 双倍字体高宽（仅英文字体有效）
     * ESC ! n
     *
     * @return bytes for this command
     */
    public static byte[] doubleHeightWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 56;
        return result;
    }


    /**
     * 左对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignLeft() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }

    /**
     * 居中对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignCenter() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }

    /**
     * 右对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignRight() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }


    /**
     * 打印并走纸n行
     * Prints the data in the print buffer and feeds n lines
     * ESC d n
     *
     * @param n lines
     * @return bytes for this command
     */
    public static byte[] printAndFeedLines(byte n) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 100;
        result[2] = n;
        return result;
    }

    /**
     * 打印并反向走纸n行（不一定有效）
     * Prints the data in the print buffer and feeds n lines in the reserve direction
     * ESC e n
     *
     * @param n lines
     * @return bytes for this command
     */
    public static byte[] printAndReverseFeedLines(byte n) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 101;
        result[2] = n;
        return result;
    }

    public static byte[] printHorizontalTab() {
        byte[] result = new byte[5];
        result[0] = ESC;
        result[1] = 44;
        result[2] = 20;
        result[3] = 28;
        result[4] = 0;
        return result;
    }

    public static byte[] printHTNext() {
        byte[] result = new byte[1];
        result[0] = HT;
        return result;
    }

    public static byte[] printLineNormalHeight() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 50;
        return result;
    }

    public static byte[] printLineHeight(byte height) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 51;
        result[2] = height;
        return result;
    }

    /**
     * Select character code table
     * ESC t n
     *
     * @param cp example:CodePage.WPC1252
     * @return bytes for this command
     */
    public static byte[] selectCodeTab(byte cp) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 116;
        result[2] = cp;
        return result;
    }

    /**
     * 弹开纸箱
     * Drawer kick-out connector pin 2
     * ESC p m t1 t2
     *
     * @return bytes for this command
     */
    public static byte[] drawerKick() {
        byte[] result = new byte[5];
        result[0] = ESC;
        result[1] = 112;
        result[2] = 0;
        result[3] = 60;
        result[4] = 120;
        return result;
    }


    /**
     * 选择打印颜色1（不一定有效）
     * ESC r n
     *
     * @return bytes for this command
     */
    public static byte[] selectColor1() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 0;
        return result;
    }

    /**
     * 选择打印颜色2（不一定有效）
     * ESC r n
     *
     * @return bytes for this command
     */
    public static byte[] selectColor2() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 1;
        return result;
    }


    /**
     * white printing mode on (不一定有效)
     * Turn white/black reverse printing mode on
     * GS B n
     *
     * @return bytes for this command
     */
    public static byte[] whitePrintingOn() {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = (byte) 128;
        return result;
    }

    /**
     * white printing mode off (不一定有效)
     * Turn white/black reverse printing mode off
     * GS B n
     *
     * @return bytes for this command
     */
    public static byte[] whitePrintingOff() {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = 0;
        return result;
    }


    /**
     * select bar code height
     * Select the height of the bar code as n dots
     * default dots = 162
     *
     * @param dots ( heigth of the bar code )
     * @return bytes for this command
     */
    public static byte[] barcode_height(byte dots) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 104;
        result[2] = dots;
        return result;
    }

    /**
     * select font hri
     * Selects a font for the Human Readable Interpretation (HRI) characters when printing a barcode, using n as follows:
     *
     * @param n Font
     *          0, 48 Font A
     *          1, 49 Font B
     * @return bytes for this command
     */
    public static byte[] select_font_hri(byte n) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 102;
        result[2] = n;
        return result;
    }

    /**
     * select position_hri
     * Selects the print position of Human Readable Interpretation (HRI) characters when printing a barcode, using n as follows:
     *
     * @param n Print position
     *          0, 48 Not printed
     *          1, 49 Above the barcode
     *          2, 50 Below the barcode
     *          3, 51 Both above and below the barcode
     * @return bytes for this command
     */
    public static byte[] select_position_hri(byte n) {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 72;
        result[2] = n;
        return result;
    }

    /**
     * print bar code
     *
     * @param barcode_typ   ( Barcode.CODE39, Barcode.EAN8 ,...)
     * @param barcode2print
     * @return bytes for this command
     */
    public static byte[] print_bar_code(byte barcode_typ, String barcode2print) {
        byte[] barcodebytes = barcode2print.getBytes();
        byte[] result = new byte[3 + barcodebytes.length + 1];
        result[0] = GS;
        result[1] = 107;
        result[2] = barcode_typ;
        int idx = 3;

        for (int i = 0; i < barcodebytes.length; i++) {
            result[idx] = barcodebytes[i];
            idx++;
        }
        result[idx] = 0;

        return result;
    }

    /**
     * Set horizontal tab positions
     *
     * @param col ( coulumn )
     * @return bytes for this command
     */
    public static byte[] set_HT_position(byte col) {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 68;
        result[2] = col;
        result[3] = 0;
        return result;
    }

    /**
     * 字体变大为标准的n倍
     *
     * @param num 倍数
     * @return bytes for this command
     */
    public static byte[] fontSizeSetBig(int num) {
        byte realSize = 0;
        switch (num) {
            case 0:
                realSize = 0;
                break;
            case 1:
                realSize = 17;
                break;
            case 2:
                realSize = 34;
                break;
            case 3:
                realSize = 51;
                break;
            case 4:
                realSize = 68;
                break;
            case 5:
                realSize = 85;
                break;
            case 6:
                realSize = 102;
                break;
            case 7:
                realSize = 119;
                break;
        }
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 33;
        result[2] = realSize;
        return result;
    }

    /**
     * 进纸切割
     * Feeds paper to ( cutting position + n x vertical motion unit )
     * and executes a full cut ( cuts the paper completely )
     *
     * @return bytes for this command
     */
    public static byte[] feedPaperCut() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 65;
        result[3] = 0;
        return result;
    }

    /**
     * 进纸切割（留部分）
     * Feeds paper to ( cutting position + n x vertical motion unit )
     * and executes a partial cut ( one point left uncut )
     *
     * @return bytes for this command
     */
    public static byte[] feedPaperCutPartial() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 66;
        result[3] = 0;
        return result;
    }

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

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return ConvertUtil.hexList2Byte(commandList);
    }

    /**
     * 合并byte数组
     *
     * @param byteArray byte数组
     * @return 一个byte数组
     */
    public static byte[] mergerByteArray(byte[]... byteArray) {

        int length = 0;
        for (byte[] item : byteArray) {
            length += item.length;
        }
        byte[] result = new byte[length];
        int index = 0;
        for (byte[] item : byteArray) {
            for (byte b : item) {
                result[index] = b;
                index++;
            }
        }
        return result;
    }

}
