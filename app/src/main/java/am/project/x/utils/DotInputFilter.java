package am.project.x.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 小数点后几位限制
 * Created by Alex on 2016/3/24.
 */
public class DotInputFilter implements InputFilter {

    private static final int DECIMAL_DIGITS = 2;
    private static final String DOT = "\\.";
    private static final String DOT_STR = ".";
    private int decimalDigits = 0;

    public DotInputFilter() {
        this(DECIMAL_DIGITS);
    }

    public DotInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        if (source.length() == 0) {
            // 删除剪切操作
            return null;
        } else if (source.length() == 1) {
            // 单个输入
            String destStr = dest.toString();
            if (destStr.contains(DOT_STR)) {
                int location = destStr.indexOf(DOT_STR);
                if (dstart > location && (dest.length() - location - 1) >= decimalDigits) {
                    // 输入发生在小数点后
                    source = "";
                }
            } else {
                if (source.toString().equals(DOT_STR) && dest.length() == 0)
                    source = "0" + source;
            }
        } else {
            // 粘贴多值
        }
        //匹配非负浮点数
//        Pattern pat = Pattern.compile("^\\d*(\\.\\d*)?$");
//        Matcher mat = pat.matcher(source);
//        if (!mat.find()) {
//            return "";
//        }
//        // 插入有效字符
//        if (!dest.toString().contains(DOT_STR) && !source.toString().contains(DOT_STR)) {
//            // 都不包括小数点
//            return source;
//        } else if (dest.toString().contains(DOT_STR) && !source.toString().contains(DOT_STR)) {
//            // 已输入包括小数点，待输入不包含
//            String destStr = dest.toString();
//            int location = destStr.indexOf(DOT_STR);
//            if (dstart > location) {
//                // 插入发生在小数点后
//                // 需要检查是否能够插入
//                if ((dest.length() - location - 1) >= decimalDigits)
//                    // 不能插入
//                    source = "";
//                else {
//                    // 能够插入，检查最多能够插入的位数
//                    int max = decimalDigits - (dest.length() - location - 1);
//                    if (source.length() > max)
//                        source = source.subSequence(start, start + max);
//                }
//            }
//
//
//        } else if (!dest.toString().contains(DOT_STR) && source.toString().contains(DOT_STR)) {
//            // 待输入包括小数点，已输入不包含
//            String sourceStr = source.toString();
//            if (!sourceStr.equals(DOT_STR)) {
//                // 非仅输入一个小数点
//                int location = sourceStr.indexOf(DOT_STR);
//                System.out.println(location);
//            } else {
//                // 仅输入一个小数点
//                if (dest.length() == 0)
//                    source = "0" + source;
//            }
//
//        } else {
//            // 都含有小数点
//            // 不允许插入
//            source = "";
//        }

        return source;
    }
}
