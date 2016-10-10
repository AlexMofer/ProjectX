package am.project.x.activities.util.printer.managers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.util.printer.adapters.DeviceAdapter;
import am.project.x.activities.util.printer.data.PrinterTester;
import am.project.x.activities.util.printer.viewholders.DeviceViewHolder;
import am.project.x.widgets.divider.DividerItemDecoration;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

/**
 * 蓝牙打印管理器
 * Created by Alex on 2016/6/21.
 */
public class BluetoothPrinterManager implements View.OnClickListener,
        DeviceViewHolder.OnHolderListener {

    private static final int REQUEST_CODE_BLUETOOTH = 1088;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Activity activity;
    private TextView tvBluetooth;
    private View vBluetoothContent;
    private RadioGroup rgType;
    private AlertDialog dlgBluetoothOpen;
    private AlertDialog dlgBluetoothClose;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        boolean enabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (enabled != bluetoothAdapter.isEnabled()) {
                    enabled = bluetoothAdapter.isEnabled();
                    if (resume) {
                        if (bluetoothAdapter.isEnabled()) {
                            checkBluetooth();
                        } else {
                            if (dlgBluetoothClose == null) {
                                checkBluetooth();
                            } else {
                                dlgBluetoothClose.cancel();
                            }
                        }
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                bondedAdapter.setDevices(bluetoothAdapter.getBondedDevices());
            }
        }

    };
    private boolean resume = false;
    private boolean isDeny = false;
    private boolean shouldClose = false;
    private DeviceAdapter bondedAdapter = new DeviceAdapter(this);
    private PrinterTester tester;

    public BluetoothPrinterManager(Activity activity) {
        this.activity = activity;
        rgType = (RadioGroup) activity.findViewById(R.id.printer_rg_type);
        tvBluetooth = (TextView) activity.findViewById(R.id.printer_tv_bluetooth);
        vBluetoothContent = activity.findViewById(R.id.printer_lyt_bluetooth);
        RecyclerView rvBonded = (RecyclerView) activity.findViewById(R.id.printer_rv_bonded);
        tvBluetooth.setOnClickListener(this);
        rvBonded.setLayoutManager(new LinearLayoutManager(activity));
        rvBonded.addItemDecoration(new DividerItemDecoration(
                ContextCompat.getDrawable(activity, R.drawable.divider_printer),
                DividerItemDecoration.VERTICAL_LIST));
        rvBonded.setAdapter(bondedAdapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
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
                            shouldClose = true;
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

    @Override
    public void onItemClicked(BluetoothDevice device) {
        int id = rgType.getCheckedRadioButtonId();
        int type = id == R.id.printer_rb_80 ? PrinterWriter80mm.TYPE_80 : PrinterWriter58mm.TYPE_58;
        if (tester == null) {
            tester = new PrinterTester(activity);
        }
        tester.startTest(device, type,
                ((EditText) activity.findViewById(R.id.printer_edt_code)).getText().toString());
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    shouldClose = true;
                    break;
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
        if (dlgBluetoothClose != null && dlgBluetoothClose.isShowing()) {
            dlgBluetoothClose.cancel();
        }
    }

    public void onPause() {
        resume = false;
    }

    public boolean onBackPressed() {
        boolean showDialog = bluetoothAdapter != null && bluetoothAdapter.isEnabled() && shouldClose;
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
        bondedAdapter.setDevices(bluetoothAdapter.getBondedDevices());
    }

    private void hideDevicesList() {
        bondedAdapter.clear();
        tvBluetooth.setVisibility(View.VISIBLE);
        vBluetoothContent.setVisibility(View.INVISIBLE);
    }
}
