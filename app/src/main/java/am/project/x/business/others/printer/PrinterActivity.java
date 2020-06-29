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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import am.project.x.R;
import am.project.x.common.CommonActivity;

/**
 * 小票打印
 */
public class PrinterActivity extends CommonActivity implements PrinterView,
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, PrinterIPDialog.OnDialogListener,
        PrinterBluetoothDialog.OnDialogListener, PrinterStateDialog.OnDialogListener {

    private final PrinterPresenter mPresenter =
            new PrinterPresenter().setViewHolder(getViewHolder());
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private EditText mVWidth;
    private EditText mVHeight;
    private EditText mVQRCode;
    private AlertDialog mBluetoothOpen;
    private AlertDialog mBluetoothClose;
    private boolean mShouldOpen = false;
    private boolean mShouldClose = false;
    private PrinterIPDialog mIP;
    private PrinterBluetoothDialog mBluetooth;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mAdapter.isEnabled()) {
                    if (mShouldOpen) {
                        mShouldOpen = false;
                        if (mBluetooth == null)
                            mBluetooth = new PrinterBluetoothDialog(PrinterActivity.this,
                                    PrinterActivity.this);
                        mBluetooth.invalidateBondedBluetoothDevices(mAdapter.getBondedDevices());
                        showDialog(mBluetooth);
                        mShouldClose = true;
                    }
                } else {
                    if (mBluetooth != null)
                        dismissDialog(mBluetooth);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (mBluetooth != null)
                    mBluetooth.invalidateBondedBluetoothDevices(mAdapter.getBondedDevices());
            }
        }

    };
    private PrinterStateDialog mState;

    public PrinterActivity() {
        super(R.layout.activity_printer);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, PrinterActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.printer_toolbar);
        mVWidth = findViewById(R.id.printer_edt_width);
        mVHeight = findViewById(R.id.printer_edt_height);
        mVQRCode = findViewById(R.id.printer_edt_code);

        this.<RadioGroup>findViewById(R.id.printer_rg_type).setOnCheckedChangeListener(this);
        this.<Switch>findViewById(R.id.printer_sh_image).setOnCheckedChangeListener(this);
        mVWidth.addTextChangedListener(new WidthTextWatcher());
        mVHeight.addTextChangedListener(new HeightPartingTextWatcher());
        mVQRCode.addTextChangedListener(new QRCodeTextWatcher());
        findViewById(R.id.printer_btn_test_ip).setOnClickListener(this);
        findViewById(R.id.printer_btn_test_bluetooth).setOnClickListener(this);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);

        mPresenter.setType(PrinterPresenter.TYPE_80);
        mPresenter.setImageEnable(true);
        mPresenter.setImageHeightParting(PrinterPresenter.PARTING_MAX);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mShouldClose && mAdapter != null && mAdapter.isEnabled()) {
            if (mBluetoothClose == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.printer_bluetooth_dlg_close);
                builder.setNegativeButton(R.string.printer_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mShouldClose = false;
                                onBackPressed();
                            }
                        });
                builder.setPositiveButton(R.string.printer_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mAdapter.disable()) {
                                    mShouldClose = false;
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.printer_bluetooth_dlg_close_error,
                                            Toast.LENGTH_SHORT).show();
                                    mShouldClose = false;
                                    onBackPressed();
                                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                }
                            }
                        });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mShouldClose = false;
                        onBackPressed();
                    }
                });
                mBluetoothClose = builder.create();
            }
            mBluetoothClose.show();
        } else {
            super.onBackPressed();
        }
    }

    // View
    @Override
    public void onPrinterStateChanged(String state) {
        if (mState == null)
            return;
        mState.addState(state);
    }

    @Override
    public void onPrinterResult(String result) {
        if (mState == null)
            return;
        mState.addState(result);
        mState.end();
    }

    // Listener
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.printer_rb_80:
                mPresenter.setType(PrinterPresenter.TYPE_80);
                break;
            case R.id.printer_rb_58:
                mPresenter.setType(PrinterPresenter.TYPE_58);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.setImageEnable(isChecked);
        mVWidth.setEnabled(isChecked);
        mVHeight.setEnabled(isChecked);
        mVQRCode.setEnabled(isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printer_btn_test_ip:
                if (mIP == null)
                    mIP = new PrinterIPDialog(this, this);
                showDialog(mIP);
                break;
            case R.id.printer_btn_test_bluetooth:
                checkBluetooth();
                break;
        }
    }

    private void checkBluetooth() {
        if (mAdapter == null) {
            Toast.makeText(this, R.string.printer_bluetooth_not_support,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.isEnabled()) {
            if (mBluetoothOpen != null && mBluetoothOpen.isShowing())
                mBluetoothOpen.dismiss();
            if (mBluetooth == null)
                mBluetooth = new PrinterBluetoothDialog(this, this);
            mBluetooth.invalidateBondedBluetoothDevices(mAdapter.getBondedDevices());
            showDialog(mBluetooth);
        } else {
            showForceTurnOnBluetoothDialog();
        }
    }

    private void showForceTurnOnBluetoothDialog() {
        if (mBluetoothOpen == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.printer_bluetooth_dlg_open);
            builder.setNegativeButton(R.string.printer_deny, null);
            builder.setPositiveButton(R.string.printer_allow,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            forceTurnOnBluetooth();
                        }
                    });
            mBluetoothOpen = builder.create();
        }
        mBluetoothOpen.show();
    }

    private void forceTurnOnBluetooth() {
        final boolean open = mAdapter.isEnabled() || mAdapter.enable();
        mShouldOpen = true;
        if (!open) {
            showSystemTurnOnBluetoothDialog();
        }
    }

    private void showSystemTurnOnBluetoothDialog() {
        final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivity(intent);
    }

    @Override
    public void onIPCommit(String ip, int port) {
        if (mState == null)
            mState = new PrinterStateDialog(this, this);
        mState.start();
        showDialog(mState);
        mPresenter.print(ip, port);
    }

    @Override
    public void onBluetoothDeviceSelect(BluetoothDevice device) {
        if (mState == null)
            mState = new PrinterStateDialog(this, this);
        mState.start();
        showDialog(mState);
        mPresenter.print(device);
    }

    @Override
    public void onReprint() {
        mState.start();
        mPresenter.print();
    }

    private class WidthTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                mPresenter.setImageWidth(0);
                return;
            }
            try {
                mPresenter.setImageWidth(Integer.valueOf(s.toString()));
            } catch (Exception e) {
                mPresenter.setImageWidth(0);
            }
        }
    }

    private class HeightPartingTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                mPresenter.setImageHeightParting(0);
                return;
            }
            int height;
            try {
                height = Integer.valueOf(s.toString());
            } catch (Exception e) {
                height = 0;
            }
            if (height > PrinterPresenter.PARTING_MAX) {
                height = 255;
                Toast.makeText(PrinterActivity.this,
                        R.string.printer_image_height_parting_error, Toast.LENGTH_SHORT).show();
            }
            mPresenter.setImageHeightParting(height);
        }
    }

    private class QRCodeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s))
                mPresenter.setQRCode(null);
            else
                mPresenter.setQRCode(s.toString());
        }
    }
}
