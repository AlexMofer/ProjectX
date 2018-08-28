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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import am.project.support.utils.InputMethodUtils;
import am.project.x.R;
import am.project.x.utils.AlertDialogUtils;
import am.project.x.utils.StringUtils;

/**
 * 固定IP填写对话框
 */
class PrinterIPTestDialog extends AppCompatDialog implements View.OnClickListener {

    private final OnDialogListener mListener;
    private EditText mVIp;
    private EditText mVPort;

    PrinterIPTestDialog(@NonNull Context context, @NonNull OnDialogListener listener) {
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
            InputMethodUtils.openInputMethod(mVIp);
            return;
        } else if (!StringUtils.isIp(ip)) {
            Toast.makeText(getContext(), R.string.printer_edit_toast_2, Toast.LENGTH_SHORT).show();
            mVIp.setText(null);
            InputMethodUtils.openInputMethod(mVIp);
            return;
        }
        int port;
        String portStr = mVPort.getText().toString().trim();
        if (portStr.length() <= 0) {
            Toast.makeText(getContext(), R.string.printer_edit_toast_3, Toast.LENGTH_SHORT).show();
            InputMethodUtils.openInputMethod(mVPort);
            return;
        } else {
            try {
                port = Integer.valueOf(portStr);
            } catch (Exception e) {
                port = -1;
            }
            if (port < 0 || port > 65535) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_4, Toast.LENGTH_SHORT).show();
                mVPort.setText(null);
                InputMethodUtils.openInputMethod(mVPort);
                return;
            }
        }
        if (mVIp.isFocused())
            InputMethodUtils.closeInputMethod(mVIp);
        if (mVPort.isFocused())
            InputMethodUtils.closeInputMethod(mVPort);
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
