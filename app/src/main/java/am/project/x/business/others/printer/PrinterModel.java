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

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.am.mvp.core.MVPModel;

import am.project.x.ProjectXApplication;
import am.project.x.R;
import am.util.printer.PrintExecutor;
import am.util.printer.PrintSocketHolder;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * Model
 */
class PrinterModel extends MVPModel<PrinterPresenter> implements PrinterViewModel,
        PrintSocketHolder.OnStateChangedListener, PrintExecutor.OnPrintResultListener {

    private int mType = PrinterWriter80mm.TYPE_80;
    private boolean mImageEnable = true;
    private int mWidth = 300;
    private int mHeight = PrinterWriter.HEIGHT_PARTING_DEFAULT;
    private String mQRCodeData;
    private PrintExecutor mExecutor;
    private PrinterPrintDataMaker mMaker;

    PrinterModel() {
        mMaker = new PrinterPrintDataMaker(ProjectXApplication.getInstance(), mQRCodeData,
                mImageEnable, mWidth, mHeight);
    }

    @Override
    public void setType(int type) {
        if (type == TYPE_80) {
            mType = PrinterWriter80mm.TYPE_80;
        } else {
            mType = PrinterWriter58mm.TYPE_58;
        }
    }

    @Override
    public void setImageEnable(boolean enable) {
        if (mImageEnable == enable)
            return;
        mImageEnable = enable;
        mMaker = new PrinterPrintDataMaker(ProjectXApplication.getInstance(), mQRCodeData,
                mImageEnable, mWidth, mHeight);
    }

    @Override
    public void setImageWidth(int width) {
        if (mWidth == width)
            return;
        mWidth = width;
        mMaker = new PrinterPrintDataMaker(ProjectXApplication.getInstance(), mQRCodeData,
                mImageEnable, mWidth, mHeight);
    }

    @Override
    public void setImageHeightParting(int height) {
        if (mHeight == height)
            return;
        mHeight = height;
        mMaker = new PrinterPrintDataMaker(ProjectXApplication.getInstance(), mQRCodeData,
                mImageEnable, mWidth, mHeight);
    }

    @Override
    public void setQRCode(String data) {
        if (TextUtils.equals(mQRCodeData, data))
            return;
        mQRCodeData = data;
        mMaker = new PrinterPrintDataMaker(ProjectXApplication.getInstance(), mQRCodeData,
                mImageEnable, mWidth, mHeight);
    }

    @Override
    public void print(String ip, int port) {
        if (TextUtils.isEmpty(ip) || mMaker == null) {
            notifyPrinterResult(R.string.printer_result_message_6);
            return;
        }
        if (mExecutor != null) {
            mExecutor.closeSocket();
            mExecutor = null;
        }
        mExecutor = new PrintExecutor(ip, port, mType);
        mExecutor.setOnStateChangedListener(this);
        mExecutor.setOnPrintResultListener(this);
        mExecutor.doPrinterRequestAsync(mMaker);
        notifyPrinterStateChanged(R.string.printer_test_message_0);
    }

    @Override
    public void print(BluetoothDevice device) {
        if (device == null || mMaker == null) {
            notifyPrinterResult(R.string.printer_result_message_6);
            return;
        }
        if (mExecutor != null) {
            mExecutor.closeSocket();
            mExecutor = null;
        }
        mExecutor = new PrintExecutor(device, mType);
        mExecutor.setOnStateChangedListener(this);
        mExecutor.setOnPrintResultListener(this);
        mExecutor.doPrinterRequestAsync(mMaker);
        notifyPrinterStateChanged(R.string.printer_test_message_0);
    }

    @Override
    public void print() {
        if (mExecutor == null || mMaker == null) {
            notifyPrinterResult(R.string.printer_result_message_6);
            return;
        }
        mExecutor.doPrinterRequestAsync(mMaker);
        notifyPrinterStateChanged(R.string.printer_test_message_0);
    }

    @Override
    public void stop() {
        if (mExecutor != null) {
            mExecutor.closeSocket();
            mExecutor = null;
        }
    }

    private void notifyPrinterStateChanged(int strId) {
        final PrinterPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onPrinterStateChanged(ProjectXApplication.getInstance().getString(strId));
    }

    private void notifyPrinterResult(int strId) {
        final PrinterPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onPrinterResult(ProjectXApplication.getInstance().getString(strId));
    }

    // Listener
    @Override
    public void onResult(int errorCode) {
        switch (errorCode) {
            case PrintSocketHolder.ERROR_0:
                notifyPrinterResult(R.string.printer_result_message_1);
                break;
            case PrintSocketHolder.ERROR_1:
                notifyPrinterResult(R.string.printer_result_message_2);
                break;
            case PrintSocketHolder.ERROR_2:
                notifyPrinterResult(R.string.printer_result_message_3);
                break;
            case PrintSocketHolder.ERROR_3:
                notifyPrinterResult(R.string.printer_result_message_4);
                break;
            case PrintSocketHolder.ERROR_4:
                notifyPrinterResult(R.string.printer_result_message_5);
                break;
            case PrintSocketHolder.ERROR_5:
                notifyPrinterResult(R.string.printer_result_message_6);
                break;
            case PrintSocketHolder.ERROR_6:
                notifyPrinterResult(R.string.printer_result_message_7);
                break;
            case PrintSocketHolder.ERROR_100:
                notifyPrinterResult(R.string.printer_result_message_8);
                break;
        }
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case PrintSocketHolder.STATE_0:
                notifyPrinterStateChanged(R.string.printer_test_message_1);
                break;
            case PrintSocketHolder.STATE_1:
                notifyPrinterStateChanged(R.string.printer_test_message_2);
                break;
            case PrintSocketHolder.STATE_2:
                notifyPrinterStateChanged(R.string.printer_test_message_3);
                break;
            case PrintSocketHolder.STATE_3:
                notifyPrinterStateChanged(R.string.printer_test_message_4);
                break;
            case PrintSocketHolder.STATE_4:
                notifyPrinterStateChanged(R.string.printer_test_message_5);
                break;
        }
    }
}
