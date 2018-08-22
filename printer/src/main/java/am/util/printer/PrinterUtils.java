/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.util.printer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * ESC-POS指令集
 * 未经过测试的指令集已全部放在了 {@link PrintCommands}
 * Created by Alex on 2015/9/22.
 */
@SuppressWarnings("all")
public class PrinterUtils {

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    /**
     * 初始化打印机
     *
     * @return command
     */
    public static byte[] initPrinter() {
        return PrintCommands.initializePrinter();
    }

    /**
     * 打印并换行
     *
     * @return command
     */
    public static byte[] printLineFeed() {
        return PrintCommands.printLineFeed();
    }

    /**
     * 开启着重强调(加粗)
     * ESC E n
     *
     * @return bytes for this command
     */
    public static byte[] emphasizedOn() {
        return PrintCommands.turnOnEmphasizedMode();
    }

    /**
     * 关闭着重强调(加粗)
     * ESC E n
     *
     * @return bytes for this command
     */
    public static byte[] emphasizedOff() {
        return PrintCommands.turnOffEmphasizedMode();
    }

    /**
     * 左对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignLeft() {
        return PrintCommands.selectJustification(0);
    }

    /**
     * 居中对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignCenter() {
        return PrintCommands.selectJustification(1);
    }

    /**
     * 右对齐
     * ESC a n
     *
     * @return bytes for this command
     */
    public static byte[] alignRight() {
        return PrintCommands.selectJustification(2);
    }

    /**
     * 设置行间距
     *
     * @param height 0≤height≤255
     * @return command
     */
    public static byte[] printLineHeight(int height) {
        return PrintCommands.setLineSpacing(height);
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
        return PrintCommands.selectCharacterSize(realSize);
    }

    /**
     * 进纸切割
     * Feeds paper to ( cutting position + n x vertical motion unit )
     * and executes a full cut ( cuts the paper completely )
     *
     * @return bytes for this command
     */
    public static byte[] feedPaperCut() {
        return PrintCommands.selectCutModeAndCutPaper(1, 0);
    }

    /**
     * 进纸切割（留部分）
     * Feeds paper to ( cutting position + n x vertical motion unit )
     * and executes a partial cut ( one point left uncut )
     *
     * @return bytes for this command
     */
    public static byte[] feedPaperCutPartial() {
        return PrintCommands.selectCutModeAndCutPaper(66, 0);
    }

    /**
     * 解码图片
     *
     * @param image   图片
     * @param parting 高度分割值
     * @return 数据流
     */
    public static ArrayList<byte[]> decodeBitmapToDataList(Bitmap image, int parting) {
        if (parting <= 0 || parting > 255)
            parting = 255;
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
            ArrayList<byte[]> data = decodeBitmapToDataList(resizeImage, parting);
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
        // 高度每parting像素进行一次分割
        int time = height % parting == 0 ? height / parting : (height / parting + 1);// 循环打印次数
        for (int t = 0; t < time; t++) {
            int partHeight = t == time - 1 ? height % parting : parting;// 分段高度

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
                    int startHeight = t * parting + i;
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
        ArrayList<byte[]> data = new ArrayList<>();
        for (String hexStr : commandList) {
            data.add(hexStringToBytes(hexStr));
        }
        return data;
    }

    /**
     * 解码图片
     *
     * @param image   图片
     * @param parting 高度分割值
     * @return 数据流
     */
    public static byte[] decodeBitmap(Bitmap image, int parting) {
        ArrayList<byte[]> data = decodeBitmapToDataList(image, parting);
        int len = 0;
        for (byte[] srcArray : data) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : data) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    /**
     * 解码图片
     *
     * @param image 图片
     * @return 数据流
     */
    public static byte[] decodeBitmap(Bitmap image) {
        return decodeBitmap(image, PrinterWriter.HEIGHT_PARTING_DEFAULT);
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
     *
     * @param list 指令集
     * @return byte[]指令
     */
    public static byte[] hexListToByte(List<String> list) {
        ArrayList<byte[]> commandList = new ArrayList<>();
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
