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
package io.github.alexmofer.projectx.business.others.printer;

import android.bluetooth.BluetoothDevice;

import com.am.mvp.core.MVPPresenter;

/**
 * Presenter
 */
class PrinterPresenter extends MVPPresenter<PrinterView, PrinterModel> implements PrinterView,
        PrinterViewModel {

    PrinterPresenter() {
        setModel(new PrinterModel());
    }

    // View
    @Override
    public void onPrinterStateChanged(String state) {
        final PrinterView view = getView();
        if (view != null)
            view.onPrinterStateChanged(state);
    }

    @Override
    public void onPrinterResult(String result) {
        final PrinterView view = getView();
        if (view != null)
            view.onPrinterResult(result);
    }

    // ViewModel
    @Override
    public void setType(int type) {
        getModel().setType(type);
    }

    @Override
    public void setImageEnable(boolean enable) {
        getModel().setImageEnable(enable);
    }

    @Override
    public void setImageWidth(int width) {
        getModel().setImageWidth(width);
    }

    @Override
    public void setImageHeightParting(int height) {
        getModel().setImageHeightParting(height);
    }

    @Override
    public void setQRCode(String data) {
        getModel().setQRCode(data);
    }

    @Override
    public void print(String ip, int port) {
        getModel().print(ip, port);
    }

    @Override
    public void print(BluetoothDevice device) {
        getModel().print(device);
    }

    @Override
    public void print() {
        getModel().print();
    }

    @Override
    public void stop() {
        getModel().stop();
    }
}
