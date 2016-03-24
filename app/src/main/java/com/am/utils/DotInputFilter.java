package com.am.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 小数点后几位限制
 * Created by Mofer on 2016/3/24.
 */
public class DotInputFilter implements InputFilter {

    private static final int DECIMAL_DIGITS = 2;
    private int decimalDigits = 0;

    public DotInputFilter() {
        this(DECIMAL_DIGITS);
    }

    public DotInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        // 删除等特殊字符，直接返回
        if ("".equals(source.toString())) {
            return null;
        }
        String dValue = dest.toString();
        String[] splitArray = dValue.split("\\.");
        if (splitArray.length > 1) {
            String dotValue = splitArray[1];
            int diff = dotValue.length() + 1 - decimalDigits;
            if (diff > 0) {
                return source.subSequence(start, end - diff);
            }
        }
        return null;
    }
}
