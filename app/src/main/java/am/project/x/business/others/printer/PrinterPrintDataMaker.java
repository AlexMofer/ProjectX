/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.business.others.printer;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import am.project.x.R;
import am.project.x.utils.QRCodeUtil;
import am.util.printer.PrintDataMaker;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * 数据生成器
 * Created by Alex on 2016/11/10.
 */

class PrinterPrintDataMaker implements PrintDataMaker {

    private static final String PATTERN = "yyyy/MM/dd HH:mm";
    private Context context;
    private String qr;
    private boolean enable;
    private int width;
    private int height;

    PrinterPrintDataMaker(Context context, String qr, boolean enable, int width, int height) {
        this.context = context;
        this.qr = qr;
        this.enable = enable;
        this.width = width;
        this.height = height;
        if (this.width <= 0 || this.height <= 0)
            this.enable = false;
    }

    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();
        try {
            PrinterWriter printer;
            printer = type == PrinterWriter58mm.TYPE_58 ?
                    new PrinterWriter58mm(height, width) : new PrinterWriter80mm(height, width);
            printer.setAlignCenter();
            data.add(printer.getDataAndReset());

            if (enable) {
                data.addAll(printer.getImageByte(context.getResources(),
                        R.drawable.ic_printer_logo));
            }

            printer.setAlignLeft();
            printer.printLine();
            printer.printLineFeed();

            printer.printLineFeed();
            printer.setAlignCenter();
            printer.setEmphasizedOn();
            printer.setFontSize(1);
            printer.print(context.getString(R.string.printer_data_1));
            printer.printLineFeed();
            printer.setFontSize(0);
            printer.setEmphasizedOff();
            printer.printLineFeed();

            printer.print(context.getString(R.string.printer_data_2));
            printer.printLineFeed();
            printer.print(context.getString(R.string.printer_data_3));
            printer.printLineFeed();

            printer.setAlignLeft();
            printer.printLineFeed();

            printer.print(context.getString(R.string.printer_data_4));
            printer.printLineFeed();

            printer.print(context.getString(R.string.printer_data_5) +
                    new SimpleDateFormat(PATTERN, Locale.getDefault())
                            .format(GregorianCalendar.getInstance().getTime()));
            printer.printLineFeed();

            printer.setEmphasizedOn();
            printer.print(context.getString(R.string.printer_data_6));
            printer.printLineFeed();
            printer.print(context.getString(R.string.printer_data_7));
            printer.printLineFeed();
            printer.setEmphasizedOff();
            printer.print(context.getString(R.string.printer_data_8));
            printer.print(context.getString(R.string.printer_data_9));
            printer.printLineFeed();
            printer.print(context.getString(R.string.printer_data_10));
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.printInOneLine(context.getString(R.string.printer_data_11),
                    context.getString(R.string.printer_data_12), 0);
            printer.printLineFeed();
            printer.printInOneLine(context.getString(R.string.printer_data_13),
                    context.getString(R.string.printer_data_14), 0);
            printer.printLineFeed();
            printer.printInOneLine(context.getString(R.string.printer_data_15),
                    context.getString(R.string.printer_data_16), 0);
            printer.printLineFeed();
            printer.printInOneLine(context.getString(R.string.printer_data_17),
                    context.getString(R.string.printer_data_18), 0);
            printer.printLineFeed();

            printer.printLine();
            printer.printLineFeed();

            printer.setAlignRight();
            printer.print(context.getString(R.string.printer_data_19));
            printer.printLineFeed();
            printer.printLineFeed();

            printer.setAlignCenter();

            data.add(printer.getDataAndReset());

            if (enable) {
                printQRCode(printer, data);
            }

            printer.printLineFeed();
            printer.print(context.getString(R.string.printer_data_20));
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();

            printer.feedPaperCutPartial();

            data.add(printer.getDataAndClose());
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void printQRCode(PrinterWriter printer, ArrayList<byte[]> data) {
        final int size = 380;
        final Bitmap image;
        try {
            image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            data.addAll(printer.getImageByte(context.getResources(), R.drawable.ic_printer_qr));
            return;
        }
        if (QRCodeUtil.createQRCode(qr, image)) {
            data.addAll(printer.getImageByte(image));
        } else {
            data.addAll(printer.getImageByte(context.getResources(), R.drawable.ic_printer_qr));
        }
        image.recycle();
    }
}
