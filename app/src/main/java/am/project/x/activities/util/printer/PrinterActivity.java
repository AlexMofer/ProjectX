package am.project.x.activities.util.printer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.activities.util.printer.dialogs.PrinterTestDialog;
import am.project.x.activities.util.printer.managers.BluetoothPrinterManager;
import am.project.x.activities.util.printer.managers.IPPrinterManager;
import am.project.x.utils.ImmUtils;
import am.project.x.utils.StringUtils;

public class PrinterActivity extends BaseActivity  {


    private BluetoothPrinterManager bluetoothPrinterManager;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_printer;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.printer_toolbar);
        IPPrinterManager ipPrinterManager = new IPPrinterManager(this);
        bluetoothPrinterManager = new BluetoothPrinterManager(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bluetoothPrinterManager.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothPrinterManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothPrinterManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothPrinterManager.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (bluetoothPrinterManager.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, PrinterActivity.class));
    }
}
