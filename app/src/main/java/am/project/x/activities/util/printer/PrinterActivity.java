package am.project.x.activities.util.printer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;


import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.activities.util.printer.data.PrinterTester;
import am.project.x.activities.util.printer.dialogs.IPTestDialogFragment;
import am.project.x.activities.util.printer.managers.BluetoothPrinterManager;
import am.project.x.activities.util.printer.managers.IPPrinterManager;
import am.project.x.utils.ImmUtils;
import am.project.x.utils.StringUtils;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter58mm;
import am.util.printer.PrinterWriter80mm;

public class PrinterActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener,SeekBar.OnSeekBarChangeListener, View.OnClickListener {


    private static final String FRAGMENT_IP = "ip";
    private PrinterActivity me = this;
    private EditText edtWidth;
    private EditText edtQRCode;
    private int type = PrinterWriter80mm.TYPE_80;
    private int height = PrinterWriter.HEIGHT_PARTING_DEFAULT;


    private BluetoothPrinterManager bluetoothPrinterManager;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_printer;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.printer_toolbar);
        findViewById(R.id.printer_btn_test_ip).setOnClickListener(me);
        edtWidth = (EditText) findViewById(R.id.printer_edt_width);
        edtQRCode = (EditText) findViewById(R.id.printer_edt_code);
        ((RadioGroup) findViewById(R.id.printer_rg_type)).setOnCheckedChangeListener(me);
        ((SeekBar) findViewById(R.id.printer_sb_height)).setOnSeekBarChangeListener(me);



        bluetoothPrinterManager = new BluetoothPrinterManager(this);


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
