/*
 * Copyright (C) 2010 ZXing authors
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

package com.google.zxing.client.android;

import com.google.zxing.BarcodeFormat;

import java.util.EnumSet;
import java.util.Set;

final class DecodeFormatManager {

  static final Set<BarcodeFormat> PRODUCT_FORMATS;
  static final Set<BarcodeFormat> INDUSTRIAL_FORMATS;
  private static final Set<BarcodeFormat> ONE_D_FORMATS;
  static final Set<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
  static final Set<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);
  static final Set<BarcodeFormat> AZTEC_FORMATS = EnumSet.of(BarcodeFormat.AZTEC);
  static final Set<BarcodeFormat> PDF417_FORMATS = EnumSet.of(BarcodeFormat.PDF_417);
  static {
    PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                                 BarcodeFormat.UPC_E,
                                 BarcodeFormat.EAN_13,
                                 BarcodeFormat.EAN_8,
                                 BarcodeFormat.RSS_14,
                                 BarcodeFormat.RSS_EXPANDED);
    INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                                    BarcodeFormat.CODE_93,
                                    BarcodeFormat.CODE_128,
                                    BarcodeFormat.ITF,
                                    BarcodeFormat.CODABAR);
    ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
    ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);
  }

  private DecodeFormatManager() {}

}
