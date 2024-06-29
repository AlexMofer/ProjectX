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

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.utils.AlertDialogUtils;
import io.github.alexmofer.projectx.utils.StringUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.am.tool.support.utils.InputMethodManagerUtils;

/**
 * 固定IP填写对话框
 */
class PrinterIPDialog extends AppCompatDialog implements View.OnClickListener {

    private final OnDialogListener mListener;
    private final EditText mVIp;
    private final EditText mVPort;

    PrinterIPDialog(@NonNull Context context, @NonNull OnDialogListener listener) {
        super(context, AlertDialogUtils.getAlertDialogTheme(context));
        mListener = listener;
        setContentView(R.layout.dlg_printer_ip);
        mVIp = findViewById(R.id.dpi_edt_ip);
        mVPort = findViewById(R.id.dpi_edt_port);
        final View positive = findViewById(R.id.dpi_btn_positive);
        if (positive != null)
            positive.setOnClickListener(this);
    }

    // Listener
    @Override
    public void onClick(View v) {
        final String ip = mVIp.getText().toString().trim();
        if (ip.length() <= 0) {
            Toast.makeText(getContext(), R.string.printer_edit_toast_1, Toast.LENGTH_SHORT).show();
            InputMethodManagerUtils.showSoftInput(mVIp);
            return;
        } else if (!StringUtils.isIp(ip)) {
            Toast.makeText(getContext(), R.string.printer_edit_toast_2, Toast.LENGTH_SHORT).show();
            mVIp.setText(null);
            InputMethodManagerUtils.showSoftInput(mVIp);
            return;
        }
        int port;
        String portStr = mVPort.getText().toString().trim();
        if (portStr.length() <= 0) {
            Toast.makeText(getContext(), R.string.printer_edit_toast_3, Toast.LENGTH_SHORT).show();
            InputMethodManagerUtils.showSoftInput(mVPort);
            return;
        } else {
            try {
                port = Integer.parseInt(portStr);
            } catch (Exception e) {
                port = -1;
            }
            if (port < 0 || port > 65535) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_4, Toast.LENGTH_SHORT).show();
                mVPort.setText(null);
                InputMethodManagerUtils.showSoftInput(mVPort);
                return;
            }
        }
        if (mVIp.isFocused())
            InputMethodManagerUtils.hideSoftInput(mVIp, false);
        if (mVPort.isFocused())
            InputMethodManagerUtils.hideSoftInput(mVPort, false);
        dismiss();
        mListener.onIPCommit(ip, port);
    }

    /**
     * 对话框监听
     */
    public interface OnDialogListener {
        /**
         * IP信息已确认
         *
         * @param ip   IP
         * @param port 端口
         */
        void onIPCommit(String ip, int port);
    }
}
