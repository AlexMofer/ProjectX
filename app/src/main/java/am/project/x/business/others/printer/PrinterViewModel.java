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

/**
 * ViewModel
 */
interface PrinterViewModel {
    int TYPE_80 = 0;
    int TYPE_58 = 1;
    int PARTING_MAX = 255;

    /**
     * 设置打印机类型
     *
     * @param type 类型
     */
    void setType(int type);

    /**
     * 设置打印图片是否开启
     *
     * @param enable 是否开启
     */
    void setImageEnable(boolean enable);

    /**
     * 设置图片宽度
     *
     * @param width 宽度
     */
    void setImageWidth(int width);

    /**
     * 设置图片分段高度
     *
     * @param height 分段高度
     */
    void setImageHeightParting(int height);

    /**
     * 设置二维码数据
     *
     * @param data 数据
     */
    void setQRCode(String data);

    /**
     * 打印
     *
     * @param ip   IP
     * @param port 端口
     */
    void print(String ip, int port);

    /**
     * 打印
     *
     * @param device 蓝牙设备
     */
    void print(BluetoothDevice device);

    /**
     * 打印
     */
    void print();

    /**
     * 停止
     */
    void stop();
}
