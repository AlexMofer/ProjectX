package am.project.x.activities.util.printer.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.util.printer.data.PrinterTester;
import am.project.x.utils.ImmUtils;
import am.project.x.utils.StringUtils;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter80mm;

/**
 * 地址选择对话框Fragment
 * Created by Alex on 2015/11/14.
 */
public class IPTestDialogFragment extends DialogFragment {

    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_WIDTH = "width";
    private static final String EXTRA_HEIGHT = "height";
    private static final String EXTRA_QR = "qr";
    private IPTestDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int type = getArguments().getInt(EXTRA_TYPE, PrinterWriter80mm.TYPE_80);
        int width = getArguments().getInt(EXTRA_WIDTH, 500);
        int height = getArguments().getInt(EXTRA_HEIGHT, PrinterWriter.HEIGHT_PARTING_DEFAULT);
        String qr = getArguments().getString(EXTRA_QR);
        return new IPTestDialog(getActivity(), type, width, height, qr);
    }

    public static class IPTestDialog extends AppCompatDialog implements View.OnClickListener{

        private int type;
        private int width;
        private int height;
        private String qr;
        private EditText edtIp;
        private EditText edtPort;
        private PrinterTester tester;

        public IPTestDialog(Context context, int type, int width, int height, String qr) {
            super(context);
            this.type = type;
            this.width = width;
            this.height = height;
            this.qr = qr;
            setContentView(R.layout.dlg_printer_ip);
            edtIp = (EditText) findViewById(R.id.printer_edt_ip);
            edtPort = (EditText) findViewById(R.id.printer_edt_port);
            findViewById(R.id.printer_btn_test_print).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.printer_btn_test_print:
                    print();
                    break;
            }
        }

        private void print() {
            String ip = edtIp.getText().toString().trim();
            if (ip.length() <= 0) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_1, Toast.LENGTH_SHORT).show();
                edtIp.requestFocus();
                edtIp.requestFocusFromTouch();
                ImmUtils.showImm(getContext(), edtIp);
                return;
            } else if (!StringUtils.isIp(ip)) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_2, Toast.LENGTH_SHORT).show();
                edtIp.setText(null);
                edtIp.requestFocus();
                edtIp.requestFocusFromTouch();
                ImmUtils.showImm(getContext(), edtIp);
                return;
            }
            int port;
            String portStr = edtPort.getText().toString().trim();
            if (portStr.length() <= 0) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_3, Toast.LENGTH_SHORT).show();
                edtPort.requestFocus();
                edtPort.requestFocusFromTouch();
                ImmUtils.showImm(getContext(), edtPort);
                return;
            } else {
                try {
                    port = Integer.valueOf(portStr);
                } catch (Exception e) {
                    port = -1;
                }
                if (port < 0 || port > 65535) {
                    Toast.makeText(getContext(), R.string.printer_edit_toast_4, Toast.LENGTH_SHORT).show();
                    edtPort.setText(null);
                    edtPort.requestFocus();
                    edtPort.requestFocusFromTouch();
                    ImmUtils.showImm(getContext(), edtPort);
                    return;
                }
            }
            edtIp.clearFocus();
            edtPort.clearFocus();
            ImmUtils.closeImm(getOwnerActivity());
            if (tester == null) {
                tester = new PrinterTester(getOwnerActivity());
            }
            tester.startTest(ip, port, type, qr, width);
        }
    }

    public static IPTestDialogFragment getFragment(int type, int width, int height, String qr) {
        IPTestDialogFragment fragment = new IPTestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        bundle.putInt(EXTRA_WIDTH, width);
        bundle.putInt(EXTRA_HEIGHT, height);
        bundle.putString(EXTRA_QR, qr);
        fragment.setArguments(bundle);
        return fragment;
    }
}
