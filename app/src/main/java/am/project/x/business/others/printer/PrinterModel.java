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

import am.project.x.ProjectXApplication;
import am.util.mvp.AMModel;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * Model
 */
class PrinterModel extends AMModel<PrinterPresenter> implements PrinterViewModel {

    private int mType = PrinterWriter80mm.TYPE_80;
    private boolean mImageEnable = true;
    private int mWidth = 300;
    private int mHeight = PrinterWriter.HEIGHT_PARTING_DEFAULT;
    private String mQRCodeData;
    private String mIP;
    private int mPort;
    private BluetoothDevice mDevice;

    PrinterModel(PrinterPresenter presenter) {
        super(presenter);
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
        mImageEnable = enable;
    }

    @Override
    public void setImageWidth(int width) {
        mWidth = width;
    }

    @Override
    public void setImageHeightParting(int height) {
        mHeight = height;
    }

    @Override
    public void setQRCode(String data) {
        mQRCodeData = data;
    }

    @Override
    public void print(String ip, int port) {
        mIP = ip;
        mPort = port;
        mDevice = null;
    }

    @Override
    public void print(BluetoothDevice device) {
        mIP = null;
        mPort = 0;
        mDevice = device;
    }

    @Override
    public void print() {

    }

    private void notifyPrinterStateChanged(int strId) {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onPrinterStateChanged(ProjectXApplication.getInstance().getString(strId));
    }

    private void notifyPrinterResult(int strId) {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onPrinterResult(ProjectXApplication.getInstance().getString(strId));
    }
}
