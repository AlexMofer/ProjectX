package am.project.x.activities.util.printer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.activities.util.printer.dialogs.PrinterTestDialog;
import am.project.x.utils.ImmUtils;
import am.project.x.utils.StringUtils;

public class PrinterActivity extends BaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener{

    private EditText edtIp;
    private EditText edtPort;
    private int type = PrinterTestDialog.TYPE_80;
    private PrinterTestDialog printerTestDialog;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_printer;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.printer_toolbar);
        edtIp = (EditText) findViewById(R.id.printer_edt_ip);
        edtPort = (EditText) findViewById(R.id.printer_edt_port);
        ((RadioGroup) findViewById(R.id.printer_rg_type)).setOnCheckedChangeListener(this);
        findViewById(R.id.printer_btn_test_ip).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.printer_rb_80:
                type = PrinterTestDialog.TYPE_80;
                break;
            case R.id.printer_rb_58:
                type = PrinterTestDialog.TYPE_58;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printer_btn_test_ip:
                testIp();
                break;
        }
    }

    private void testIp() {
        String ip = edtIp.getText().toString().trim();
        if (ip.length() <= 0) {
            Toast.makeText(getApplicationContext(), R.string.printer_edit_toast_1, Toast.LENGTH_SHORT).show();
            ImmUtils.showImm(getApplicationContext(), edtIp);
            return;
        } else if (!StringUtils.isIp(ip)) {
            Toast.makeText(getApplicationContext(), R.string.printer_edit_toast_2, Toast.LENGTH_SHORT).show();
            edtIp.setText(null);
            ImmUtils.showImm(getApplicationContext(), edtIp);
            return;
        }
        int port;
        String portStr = edtPort.getText().toString().trim();
        if (portStr.length() <= 0) {
            Toast.makeText(getApplicationContext(), R.string.printer_edit_toast_3, Toast.LENGTH_SHORT).show();
            ImmUtils.showImm(getApplicationContext(), edtPort);
            return;
        } else {
            try {
                port = Integer.valueOf(portStr);
            } catch (Exception e) {
                port = -1;
            }
            if (port < 0 || port > 65535) {
                Toast.makeText(getApplicationContext(), R.string.printer_edit_toast_4, Toast.LENGTH_SHORT).show();
                edtPort.setText(null);
                ImmUtils.showImm(getApplicationContext(), edtPort);
                return;
            }
        }
        edtIp.clearFocus();
        edtPort.clearFocus();
        ImmUtils.closeImm(this);
        if (printerTestDialog == null) {
            printerTestDialog = new PrinterTestDialog(this);
        }
        printerTestDialog.startTest(ip, port, type);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, PrinterActivity.class));
    }
}
