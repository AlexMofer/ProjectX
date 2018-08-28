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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import am.project.x.R;

/**
 * DeviceViewHolder
 * Created by Alex on 2016/6/22.
 */
class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnHolderListener mListener;
    private WeakReference<BluetoothDevice> mDevice;

    DeviceViewHolder(ViewGroup parent, OnHolderListener listener) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_printer_device, parent, false));
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mListener.onItemClicked(mDevice == null ? null : mDevice.get());
    }

    void bind(BluetoothDevice device) {
        mDevice = new WeakReference<>(device);
        if (itemView instanceof TextView && device != null) {
            ((TextView) itemView).setText(device.getName());
        }
    }

    public interface OnHolderListener {
        void onItemClicked(BluetoothDevice device);
    }
}
