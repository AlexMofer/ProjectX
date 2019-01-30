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
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import am.project.x.R;
import am.project.x.utils.AlertDialogUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.widget.NestedScrollView;

/**
 * 打印信息对话框
 */
class PrinterStateDialog extends AppCompatDialog implements View.OnLayoutChangeListener,
        View.OnClickListener {

    private final OnDialogListener mListener;
    private NestedScrollView mVContent;
    private TextView mVState;
    private View mVNegative;
    private View mVPositive;

    PrinterStateDialog(@NonNull Context context, @NonNull OnDialogListener listener) {
        super(context, AlertDialogUtils.getAlertDialogTheme(context));
        mListener = listener;
        setContentView(R.layout.dlg_printer_state);
        mVContent = findViewById(R.id.dps_sv_content);
        mVState = findViewById(R.id.dps_tv_state);
        mVNegative = findViewById(R.id.dps_btn_negative);
        mVPositive = findViewById(R.id.dps_btn_positive);
        mVState.addOnLayoutChangeListener(this);
        mVNegative.setOnClickListener(this);
        mVPositive.setOnClickListener(this);
    }

    void start() {
        mVState.setText(R.string.printer_test_start);
        mVNegative.setEnabled(false);
        mVPositive.setEnabled(false);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    void addState(String state) {
        if (TextUtils.isEmpty(state))
            return;
        final String text = mVState.getText().toString();
        mVState.setText(String.format(Locale.getDefault(), "%s\n%s", text, state));
    }

    void end() {
        mVNegative.setEnabled(true);
        mVPositive.setEnabled(true);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    // Listener


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mVContent.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dps_btn_negative:
                mListener.onReprint();
                break;
            case R.id.dps_btn_positive:
                dismiss();
                break;
        }
    }

    /**
     * 对话框监听
     */
    public interface OnDialogListener {
        /**
         * 重新打印
         */
        void onReprint();
    }
}
