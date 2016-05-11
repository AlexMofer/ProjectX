package am.project.x.print;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * ESC-POS指令集
 * Created by Alex on 2015/9/22.
 */
public class PrinterUtils {

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

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

    /**
     * 解码图片
     *
     * @param image 图片
     * @return 数据流
     */
    public static byte[] decodeBitmap(Bitmap image) {
        if (image == null)
            return null;
        final int width = image.getWidth();
        final int height = image.getHeight();
        if (width <= 0 || height <= 0)
            return null;

        if (width > 2040) {
            // 8位9针，宽度限制2040像素（但一般纸张都没法打印那么宽，但并不影响打印）
            final float scale = 2040 / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeImage;
            try {
                resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
            } catch (OutOfMemoryError e) {
                return null;
            }
            byte[] data = decodeBitmap(resizeImage);
            resizeImage.recycle();
            return data;
        }

        // 宽命令
        String widthHexString = Integer.toHexString(width % 8 == 0 ? width / 8 : (width / 8 + 1));
        if (widthHexString.length() > 2) {
            // 超过2040像素才会到达这里
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString += "00";

        // 每行字节数(除以8，不足补0)
        String zeroStr = "";
        int zeroCount = width % 8;
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr += "0";
            }
        }

        ArrayList<String> commandList = new ArrayList<>();

        // 高度每255像素进行一次分割
        int time = height % 255 == 0 ? height / 255 : (height / 255 + 1);// 循环打印次数
        for (int t = 0; t < time; t++) {
            int partHeight = t == time - 1 ? height % 255 : 255;// 分段高度

            // 高命令
            String heightHexString = Integer.toHexString(partHeight);
            if (heightHexString.length() > 2) {
                // 超过255像素才会到达这里
                return null;
            } else if (heightHexString.length() == 1) {
                heightHexString = "0" + heightHexString;
            }
            heightHexString += "00";

            // 宽高指令
            String commandHexString = "1D763000";
            commandList.add(commandHexString + widthHexString + heightHexString);

            ArrayList<String> list = new ArrayList<>(); //binaryString list
            StringBuilder sb = new StringBuilder();
            // 像素二值化，非黑即白
            for (int i = 0; i < partHeight; i++) {
                sb.delete(0, sb.length());
                for (int j = 0; j < width; j++) {
                    // 实际在图片中的高度
                    int startHeight = t * 255 + i;
                    //得到当前像素的值
                    int color = image.getPixel(j, startHeight);
                    int red, green, blue;
                    if (image.hasAlpha()) {
                        //得到alpha通道的值
                        int alpha = Color.alpha(color);
                        //得到图像的像素RGB的值
                        red = Color.red(color);
                        green = Color.green(color);
                        blue = Color.blue(color);
                        final float offset = alpha / 255.0f;
                        // 根据透明度将白色与原色叠加
                        red = 0xFF + (int) Math.ceil((red - 0xFF) * offset);
                        green = 0xFF + (int) Math.ceil((green - 0xFF) * offset);
                        blue = 0xFF + (int) Math.ceil((blue - 0xFF) * offset);
                    } else {
                        //得到图像的像素RGB的值
                        red = Color.red(color);
                        green = Color.green(color);
                        blue = Color.blue(color);
                    }
                    // 接近白色改为白色。其余黑色
                    if (red > 160 && green > 160 && blue > 160)
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
            ArrayList<String> bmpHexList = new ArrayList<>();
            for (String binaryStr : list) {
                sb.delete(0, sb.length());
                for (int i = 0; i < binaryStr.length(); i += 8) {
                    String str = binaryStr.substring(i, i + 8);
                    // 2进制转成16进制
                    String hexString = binaryStrToHexString(str);
                    sb.append(hexString);
                }
                bmpHexList.add(sb.toString());
            }

            // 数据指令
            commandList.addAll(bmpHexList);
        }
        return hexListToByte(commandList);
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


    /**
     * 2进制转成16进制
     *
     * @param binaryStr 2进制串
     * @return 16进制串
     */
    public static String binaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        return hex;
    }

    /**
     * 16进制指令list转换为byte[]指令
     */
    public static byte[] hexListToByte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        int len = 0;
        for (byte[] srcArray : commandList) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : commandList) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    /**
     * 16进制串转byte数组
     *
     * @param hexString 16进制串
     * @return byte数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 16进制char 转 byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) hexStr.indexOf(c);
    }
}
