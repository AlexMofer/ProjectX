package am.project.x.activities.util.printer.managers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import am.project.x.R;

/**
 * 蓝牙打印管理器
 * Created by Alex on 2016/6/21.
 */
public class BluetoothPrinterManager implements View.OnClickListener {

    private static final int REQUEST_CODE_BLUETOOTH = 1088;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Activity activity;
    private TextView tvBluetooth;
    private View vBluetoothContent;
    private AlertDialog dlgBluetoothOpen;
    private AlertDialog dlgBluetoothClose;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        boolean enabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    addBandDevices(device);
                } else {
//                    addUnbondDevices(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                progressDialog = ProgressDialog.show(context, "请稍等...",
//                        "搜索蓝牙设备中...", true);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                System.out.println("设备搜索完毕");
//                progressDialog.dismiss();

//                addUnbondDevicesToListView();
//                addBondDevicesToListView();
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (enabled != bluetoothAdapter.isEnabled()) {
                    enabled = bluetoothAdapter.isEnabled();
                    if (resume) {
                        if (bluetoothAdapter.isEnabled()) {
                            checkBluetooth();
                        } else {
                            if (dlgBluetoothClose == null) {
                                checkBluetooth();
                            } else {
                                dlgBluetoothClose.dismiss();
                            }
                        }
                    }
                }
            }
        }

    };
    private boolean resume = false;
    private boolean isDeny = false;

    public BluetoothPrinterManager(Activity activity) {
        this.activity = activity;
        tvBluetooth = (TextView) activity.findViewById(R.id.printer_tv_bluetooth);
        vBluetoothContent = activity.findViewById(R.id.printer_lyt_bluetooth);
        tvBluetooth.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.printer_tv_bluetooth:
                tvBluetooth.setClickable(false);
                isDeny = false;
                checkBluetooth();
                break;
        }
    }

    private void checkBluetooth() {
        if (bluetoothAdapter == null) {
            hideDevicesList();
            tvBluetooth.setText(R.string.printer_bluetooth_not_support);
            tvBluetooth.setEnabled(false);
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            if (dlgBluetoothOpen != null && dlgBluetoothOpen.isShowing())
                dlgBluetoothOpen.dismiss();
            // 载入设备
            showDevicesList();
        } else {
            hideDevicesList();
            if (isDeny) {
                tvBluetooth.setText(R.string.printer_bluetooth_open);
                tvBluetooth.setClickable(true);
            } else {
                // 开启蓝牙
                tvBluetooth.setText(R.string.printer_bluetooth_init);
                showForceTurnOnBluetoothDialog();
            }
        }
    }

    private void showForceTurnOnBluetoothDialog() {
        if (!resume || activity.isFinishing())
            return;
        if (dlgBluetoothOpen == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.printer_bluetooth_dlg_open);
            builder.setNegativeButton(R.string.printer_deny,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isDeny = true;
                            checkBluetooth();
                        }
                    });
            builder.setPositiveButton(R.string.printer_allow,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            forceTurnOnBluetooth();
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    isDeny = true;
                    checkBluetooth();
                }
            });
            dlgBluetoothOpen = builder.create();
        }
        dlgBluetoothOpen.show();

    }

    private void forceTurnOnBluetooth() {
        final boolean open = bluetoothAdapter.isEnabled() || bluetoothAdapter.enable();
        if (!open) {
            showSystemTurnOnBluetoothDialog();
        }
    }

    private void showSystemTurnOnBluetoothDialog() {
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        activity.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    isDeny = true;
                    checkBluetooth();
                    break;
            }
        }
    }

    public void onResume() {
        resume = true;
        checkBluetooth();
        if (dlgBluetoothClose != null && !bluetoothAdapter.isEnabled()) {
            dlgBluetoothClose.dismiss();
        }
    }

    public void onPause() {
        resume = false;
    }

    public boolean onBackPressed() {
        boolean showDialog = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        if (showDialog) {
            if (dlgBluetoothClose == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.printer_bluetooth_dlg_close);
                builder.setNegativeButton(R.string.printer_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        });
                builder.setPositiveButton(R.string.printer_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (bluetoothAdapter.disable())
                                    activity.finish();
                                else {
                                    Toast.makeText(activity,
                                            R.string.printer_bluetooth_dlg_close_error,
                                            Toast.LENGTH_SHORT).show();
                                    activity.finish();
                                    activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                }
                            }
                        });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
                dlgBluetoothClose = builder.create();
            }
            dlgBluetoothClose.show();
        }
        return !showDialog;
    }

    public void onDestroy() {
        activity.unregisterReceiver(receiver);
    }

    private void showDevicesList() {
        tvBluetooth.setVisibility(View.INVISIBLE);
        tvBluetooth.setClickable(true);
        vBluetoothContent.setVisibility(View.VISIBLE);

        // TODO
    }

    private void hideDevicesList() {
        tvBluetooth.setVisibility(View.VISIBLE);
        vBluetoothContent.setVisibility(View.INVISIBLE);

        // TODO
    }
}
