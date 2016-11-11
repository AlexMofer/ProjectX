package am.project.x.activities.other.printer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;


import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.activities.other.printer.dialogs.BluetoothTestDialogFragment;
import am.project.x.activities.other.printer.dialogs.IPTestDialogFragment;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

public class PrinterActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String FRAGMENT_IP = "ip";
    private static final String FRAGMENT_BLUETOOTH = "bluetooth";
    private PrinterActivity me = this;
    private EditText edtWidth;
    private EditText edtQRCode;
    private int type = PrinterWriter80mm.TYPE_80;
    private int height = PrinterWriter.HEIGHT_PARTING_DEFAULT;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private AlertDialog dlgBluetoothOpen;
    private boolean shouldOpen = false;
    private boolean shouldClose = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (bluetoothAdapter.isEnabled()) {
                    if (shouldOpen) {
                        shouldOpen = false;
                        showBluetoothTest();
                        shouldClose = true;
                    }
                } else {
                    Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_BLUETOOTH);
                    if (fragment != null && fragment instanceof BluetoothTestDialogFragment) {
                        BluetoothTestDialogFragment bluetooth =
                                (BluetoothTestDialogFragment) fragment;
                        bluetooth.dismissAllowingStateLoss();
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_BLUETOOTH);
                if (fragment != null && fragment instanceof BluetoothTestDialogFragment) {
                    BluetoothTestDialogFragment bluetooth = (BluetoothTestDialogFragment) fragment;
                    bluetooth.updateAdapter();
                }
            }
        }

    };

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_printer;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.printer_toolbar);
        findViewById(R.id.printer_btn_test_ip).setOnClickListener(me);
        findViewById(R.id.printer_btn_test_bluetooth).setOnClickListener(me);
        edtWidth = (EditText) findViewById(R.id.printer_edt_width);
        edtQRCode = (EditText) findViewById(R.id.printer_edt_code);
        ((RadioGroup) findViewById(R.id.printer_rg_type)).setOnCheckedChangeListener(me);
        ((SeekBar) findViewById(R.id.printer_sb_height)).setOnSeekBarChangeListener(me);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.printer_rb_80:
                type = PrinterWriter80mm.TYPE_80;
                break;
            case R.id.printer_rb_58:
                type = PrinterWriter58mm.TYPE_58;
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        height = progress + 1;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.printer_btn_test_ip:
                showIpTest();
                break;
            case R.id.printer_btn_test_bluetooth:
                checkBluetooth();
                break;
        }
    }

    private void showIpTest() {
        int width;
        try {
            width = Integer.valueOf(edtWidth.getText().toString());
        } catch (Exception e) {
            width = 500;
        }
        String strQRCode = edtQRCode.getText().toString();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAGMENT_IP);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        IPTestDialogFragment fragment = IPTestDialogFragment
                .getFragment(type, width, height, strQRCode);
        fragment.show(ft, FRAGMENT_IP);
    }

    private void checkBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(me, R.string.printer_bluetooth_not_support, Toast.LENGTH_SHORT).show();
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            if (dlgBluetoothOpen != null && dlgBluetoothOpen.isShowing())
                dlgBluetoothOpen.dismiss();
            // 载入设备
            showBluetoothTest();
        } else {
            showForceTurnOnBluetoothDialog();
        }
    }


    private void showBluetoothTest() {
        int width;
        try {
            width = Integer.valueOf(edtWidth.getText().toString());
        } catch (Exception e) {
            width = 500;
        }
        String strQRCode = edtQRCode.getText().toString();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAGMENT_BLUETOOTH);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        BluetoothTestDialogFragment fragment = BluetoothTestDialogFragment
                .getFragment(type, width, height, strQRCode);
        fragment.show(ft, FRAGMENT_BLUETOOTH);
    }

    private void showForceTurnOnBluetoothDialog() {
        if (dlgBluetoothOpen == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(me);
            builder.setMessage(R.string.printer_bluetooth_dlg_open);
            builder.setNegativeButton(R.string.printer_deny, null);
            builder.setPositiveButton(R.string.printer_allow,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            forceTurnOnBluetooth();
                        }
                    });
            dlgBluetoothOpen = builder.create();
        }
        dlgBluetoothOpen.show();
    }

    private void forceTurnOnBluetooth() {
        final boolean open = bluetoothAdapter.isEnabled() || bluetoothAdapter.enable();
        shouldOpen = true;
        if (!open) {
            showSystemTurnOnBluetoothDialog();
        }
    }

    private void showSystemTurnOnBluetoothDialog() {
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivity(requestBluetoothOn);
    }



    @Override
    public void onBackPressed() {
        if (shouldClose && bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(me);
            builder.setMessage(R.string.printer_bluetooth_dlg_close);
            builder.setNegativeButton(R.string.printer_no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            shouldClose = false;
                            onBackPressed();
                        }
                    });
            builder.setPositiveButton(R.string.printer_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (bluetoothAdapter.disable()) {
                                shouldClose = false;
                                onBackPressed();
                            } else {
                                Toast.makeText(me,
                                        R.string.printer_bluetooth_dlg_close_error,
                                        Toast.LENGTH_SHORT).show();
                                shouldClose = false;
                                onBackPressed();
                                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                            }
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    shouldClose = false;
                    onBackPressed();
                }
            });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, PrinterActivity.class));
    }
}
