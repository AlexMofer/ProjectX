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
import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Collection;

import am.project.x.R;
import am.project.x.utils.AlertDialogUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 蓝牙设备选择对话框
 */
class PrinterBluetoothDialog extends AppCompatDialog implements PrinterDeviceViewHolder.OnHolderListener {

    private final OnDialogListener mListener;
    private final PrinterDeviceAdapter mAdapter = new PrinterDeviceAdapter(this);

    PrinterBluetoothDialog(@NonNull Context context, @NonNull OnDialogListener listener) {
        super(context, AlertDialogUtils.getAlertDialogTheme(context));
        mListener = listener;
        setContentView(R.layout.dlg_printer_bluetooth);
        final RecyclerView bonded = findViewById(R.id.dpb_rv_bonded);
        if (bonded == null)
            return;
        final Drawable divider = ContextCompat.getDrawable(context,
                R.drawable.divider_common);
        if (divider != null) {
            final DividerItemDecoration decoration = new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL);
            decoration.setDrawable(divider);
            bonded.addItemDecoration(decoration);
        }
        bonded.setLayoutManager(new LinearLayoutManager(context));
        bonded.setAdapter(mAdapter);
    }

    void invalidateBondedBluetoothDevices(Collection<? extends BluetoothDevice> devices) {
        mAdapter.setDevices(devices);
    }

    @Override
    public void dismiss() {
        mAdapter.setDevices(null);
        super.dismiss();
    }

    // Listener
    @Override
    public void onItemClicked(BluetoothDevice device) {
        mListener.onBluetoothDeviceSelect(device);
        dismiss();
    }

    /**
     * 对话框监听
     */
    public interface OnDialogListener {
        /**
         * 蓝牙设备已选择
         *
         * @param device 蓝牙设备
         */
        void onBluetoothDeviceSelect(BluetoothDevice device);
    }
}
