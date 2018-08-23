package am.project.x.business.others.printer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import am.project.support.utils.InputMethodUtils;
import am.project.x.R;
import am.project.x.utils.StringUtils;

/**
 * 地址选择对话框Fragment
 * Created by Alex on 2015/11/14.
 */

public class PrinterIPTestDialogFragment extends DialogFragment {

    private static final String TAG = "PrinterIPTestDialogFragment";

    public static PrinterIPTestDialogFragment newInstance() {
        return new PrinterIPTestDialogFragment();
    }

    public static void showDialog(FragmentManager manager) {
        dismissDialog(manager);
        newInstance().show(manager, TAG);
    }

    public static void dismissDialog(FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof DialogFragment)
            ((DialogFragment) fragment).dismissAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new IPTestDialog(getActivity());
    }

    class IPTestDialog extends AppCompatDialog implements View.OnClickListener {

        private EditText edtIp;
        private EditText edtPort;
        private TextView tvState;
        private Button btnPrint;

        IPTestDialog(Context context) {
            super(context);
            setContentView(R.layout.dlg_printer_ip);
            edtIp = findViewById(R.id.printer_edt_ip);
            edtPort = findViewById(R.id.printer_edt_port);
            tvState = findViewById(R.id.printer_tv_state);
            btnPrint = findViewById(R.id.printer_btn_test_print);
            btnPrint.setOnClickListener(this);
            setEditable(true);
        }

        private void setEditable(boolean editable) {
            edtIp.setEnabled(editable);
            edtPort.setEnabled(editable);
            btnPrint.setEnabled(editable);
        }

        private void setState(int resId) {
            tvState.setText(resId);
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
                InputMethodUtils.openInputMethod(edtIp);
                return;
            } else if (!StringUtils.isIp(ip)) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_2, Toast.LENGTH_SHORT).show();
                edtIp.setText(null);
                InputMethodUtils.openInputMethod(edtIp);
                return;
            }
            int port;
            String portStr = edtPort.getText().toString().trim();
            if (portStr.length() <= 0) {
                Toast.makeText(getContext(), R.string.printer_edit_toast_3, Toast.LENGTH_SHORT).show();
                InputMethodUtils.openInputMethod(edtPort);
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
                    InputMethodUtils.openInputMethod(edtPort);
                    return;
                }
            }
            if (edtIp.isFocused()) {
                InputMethodUtils.closeInputMethod(edtIp);
            }
            if (edtPort.isFocused()) {
                InputMethodUtils.closeInputMethod(edtPort);
            }
            // TODO
        }
    }
}
