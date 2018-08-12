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
