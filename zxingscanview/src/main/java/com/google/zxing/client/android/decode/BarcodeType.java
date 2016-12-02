package com.google.zxing.client.android.decode;

/**
 * 条码类型
 * Created by Alex on 2016/12/2.
 */
@SuppressWarnings("all")
public class BarcodeType {
    public static final int PRODUCT_1D = 1;// 一维码：商品
    public static final int INDUSTRIAL_1D = 2;// 一维码：工业
    public static final int QR = 4;// 二维码
    public static final int DATA_MATRIX = 8;// Data Matrix
    public static final int AZTEC = 16;// Aztec
    public static final int PDF417 = 32;// PDF417
    public static final int DEFAULT = PRODUCT_1D | INDUSTRIAL_1D | QR | DATA_MATRIX;
}
