package am.project.x.activities.other.printer.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import am.project.x.R;
import am.project.x.activities.other.printer.adapters.DeviceAdapter;
import am.project.x.activities.other.printer.data.TestPrintDataMaker;
import am.project.x.activities.other.printer.viewholders.DeviceViewHolder;
import am.project.x.widgets.divider.DividerItemDecoration;
import am.util.printer.PrintExecutor;
import am.util.printer.PrintSocketHolder;
import am.util.printer.PrinterWriter;
import am.util.printer.PrinterWriter80mm;

/**
 * 地址选择对话框Fragment
 * Created by Alex on 2015/11/14.
 */
public class BluetoothTestDialogFragment extends DialogFragment {

    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_WIDTH = "width";
    private static final String EXTRA_HEIGHT = "height";
    private static final String EXTRA_QR = "qr";
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private IPTestDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int type = getArguments().getInt(EXTRA_TYPE, PrinterWriter80mm.TYPE_80);
        int width = getArguments().getInt(EXTRA_WIDTH, 500);
        int height = getArguments().getInt(EXTRA_HEIGHT, PrinterWriter.HEIGHT_PARTING_DEFAULT);
        String qr = getArguments().getString(EXTRA_QR);
        dialog = new IPTestDialog(getActivity(), type, width, height, qr);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkBluetooth();
    }

    private void checkBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
            dialog.cancel();
    }

    public void updateAdapter() {
        dialog.updateAdapter();
    }

    class IPTestDialog extends AppCompatDialog implements View.OnClickListener,
            PrintSocketHolder.OnStateChangedListener, PrintExecutor.OnPrintResultListener,
            DeviceViewHolder.OnHolderListener {

        private int type;
        private TextView tvState;
        private Button btnPrint;
        private DeviceAdapter bondedAdapter = new DeviceAdapter(this);
        private BluetoothDevice mDevice;
        private PrintExecutor executor;
        private TestPrintDataMaker maker;

        @SuppressWarnings("all")
        IPTestDialog(Context context, int type, int width, int height, String qr) {
            super(context);
            this.type = type;
            setContentView(R.layout.dlg_printer_bluetooth);
            RecyclerView rvBonded = (RecyclerView) findViewById(R.id.printer_rv_bonded);
            rvBonded.setLayoutManager(new LinearLayoutManager(getContext()));
            rvBonded.addItemDecoration(new DividerItemDecoration(
                    ContextCompat.getDrawable(getContext(), R.drawable.divider_printer),
                    DividerItemDecoration.VERTICAL_LIST));
            rvBonded.setAdapter(bondedAdapter);
            updateAdapter();
            tvState = (TextView) findViewById(R.id.printer_tv_state);
            btnPrint = (Button) findViewById(R.id.printer_btn_test_print);
            btnPrint.setOnClickListener(this);
            setEditable(true);
            maker = new TestPrintDataMaker(context, qr, width, height);
        }

        void updateAdapter() {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
                bondedAdapter.setDevices(bluetoothAdapter.getBondedDevices());
        }

        private void setEditable(boolean editable) {
            btnPrint.setEnabled(editable);
        }

        private void setState(int resId) {
            tvState.setText(resId);
        }

        @Override
        public void onItemClicked(BluetoothDevice device) {
            mDevice = device;
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
            if (mDevice == null)
                return;
            if (executor == null) {
                executor = new PrintExecutor(mDevice, type);
                executor.setOnStateChangedListener(this);
                executor.setOnPrintResultListener(this);
                executor.setOnPrintResultListener(new PrintExecutor.OnPrintResultListener() {
                    @Override
                    public void onResult(int errorCode) {

                    }
                });
            }
            executor.setDevice(mDevice);
            executor.doPrinterRequestAsync(maker);
        }

        @Override
        public void onStateChanged(int state) {
            switch (state) {
                case PrintSocketHolder.STATE_0:
                    dialog.setState(R.string.printer_test_message_1);
                    break;
                case PrintSocketHolder.STATE_1:
                    dialog.setState(R.string.printer_test_message_2);
                    break;
                case PrintSocketHolder.STATE_2:
                    dialog.setState(R.string.printer_test_message_3);
                    break;
                case PrintSocketHolder.STATE_3:
                    dialog.setState(R.string.printer_test_message_4);
                    break;
                case PrintSocketHolder.STATE_4:
                    dialog.setState(R.string.printer_test_message_5);
                    break;
            }
        }

        @Override
        public void onResult(int errorCode) {
            switch (errorCode) {
                case PrintSocketHolder.ERROR_0:
                    dialog.setState(R.string.printer_result_message_1);
                    break;
                case PrintSocketHolder.ERROR_1:
                    dialog.setState(R.string.printer_result_message_2);
                    break;
                case PrintSocketHolder.ERROR_2:
                    dialog.setState(R.string.printer_result_message_3);
                    break;
                case PrintSocketHolder.ERROR_3:
                    dialog.setState(R.string.printer_result_message_4);
                    break;
                case PrintSocketHolder.ERROR_4:
                    dialog.setState(R.string.printer_result_message_5);
                    break;
                case PrintSocketHolder.ERROR_5:
                    dialog.setState(R.string.printer_result_message_6);
                    break;
                case PrintSocketHolder.ERROR_6:
                    dialog.setState(R.string.printer_result_message_7);
                    break;
                case PrintSocketHolder.ERROR_100:
                    dialog.setState(R.string.printer_result_message_8);
                    break;
            }
            dialog.setEditable(true);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }

        @Override
        public void cancel() {
            super.cancel();
            if (executor != null)
                executor.closeSocket();
        }
    }

    public static BluetoothTestDialogFragment getFragment(int type, int width, int height, String qr) {
        BluetoothTestDialogFragment fragment = new BluetoothTestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        bundle.putInt(EXTRA_WIDTH, width);
        bundle.putInt(EXTRA_HEIGHT, height);
        bundle.putString(EXTRA_QR, qr);
        fragment.setArguments(bundle);
        return fragment;
    }
}
