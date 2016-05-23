package am.project.x.activities.old;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtil {

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static String myBinaryStrToHexString(String binaryStr) {
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
     *
     * @param hexString 字符串
     * @return 将十六进制转换为字节数组
     */
    public static byte[] HexStringToBinary(String hexString) {
        // hexString的长度对2取整，作为bytes的长度
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        byte high;// 字节高四位
        byte low;// 字节低四位

        for (int i = 0; i < len; i++) {
            // 右移四位得到高位
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            bytes[i] = (byte) (high & 0xF0 | low & 0x0F);// 高地位做或运算
        }
        return bytes;
    }

    /**
     *
     * @param bytes 数据流
     * @return 将二进制转换为十六进制字符输出
     */
    public static String binaryToHexString(byte[] bytes) {

        String result = "";
        String hex;
        for (byte b : bytes) {
            // 字节高4位
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            // 字节低4位
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode(new String(new byte[] { src0 })).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode(new String(new byte[] { src1 })).byteValue();
        byte ret = (byte) (_b0 | _b1);
        byte aret = Byte.decode("0x" + ret).byteValue();

        return aret;
    }

    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] bytes = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; ++i) {
            bytes[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return bytes;
    }

    /**
     * String的字符串转换成unicode的String
     */
    public static String stringToUnicode(String strText) throws Exception {
        char c;
        String strRet = "";
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                strRet += "\\u" + strHex;
            } else {
                // 低位在前面补00
                strRet += "\\u00" + strHex;
            }
        }
        return strRet;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /** Convert hexString to bytes(可用) */
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

    /** 二进制List<String>转为HexString */
    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                // 转成16进制
                String hexString = ConvertUtil.myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    /**
     * 指令list转换为byte[]指令
     */
    public static byte[] hexList2Byte(List<String> list) {

        List<byte[]> commandList = new ArrayList<>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        return sysCopy(commandList);
    }

    /**
     * 系统提供的数组拷贝方法arraycopy
     * */
    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    /**
     * 将字符串转成unicode
     *
     * @param str
     *            待转字符串
     * @return unicode字符串
     */
    public static String convert(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); // 取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); // 取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }
}
