package am.project.x.business.others.printer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import am.project.x.R;

/**
 * 地址选择对话框Fragment
 * Created by Alex on 2015/11/14.
 */
public class PrinterBluetoothTestDialogFragment extends DialogFragment {

    private static final String TAG = "PrinterBluetoothTestDialogFragment";


    public static PrinterBluetoothTestDialogFragment newInstance() {
        return new PrinterBluetoothTestDialogFragment();
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

    public static void notifyDataSetChanged(FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof PrinterBluetoothTestDialogFragment)
            ((PrinterBluetoothTestDialogFragment) fragment).notifyDataSetChanged();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new IPTestDialog(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        final IPTestDialog dialog = (IPTestDialog) getDialog();
        if (dialog.isEmpty())
            dismissAllowingStateLoss();
    }

    public void notifyDataSetChanged() {
        ((IPTestDialog) getDialog()).notifyDataSetChanged();
    }

    class IPTestDialog extends AppCompatDialog implements View.OnClickListener,
            DeviceViewHolder.OnHolderListener {

        private TextView tvState;
        private Button btnPrint;
        private DeviceAdapter bondedAdapter = new DeviceAdapter(this);
        private BluetoothDevice mDevice;

        IPTestDialog(Context context) {
            super(context);
            setContentView(R.layout.dlg_printer_bluetooth);
            RecyclerView rvBonded = findViewById(R.id.printer_rv_bonded);
            rvBonded.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration decoration = new DividerItemDecoration(rvBonded.getContext(),
                    DividerItemDecoration.VERTICAL);
            decoration.setDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.divider_printer_device));
            rvBonded.addItemDecoration(decoration);
            rvBonded.setAdapter(bondedAdapter);
            tvState = findViewById(R.id.printer_tv_state);
            btnPrint = findViewById(R.id.printer_btn_test_print);
            btnPrint.setOnClickListener(this);
            setEditable(true);
        }

        void notifyDataSetChanged() {
            bondedAdapter.notifyDataSetChanged();
        }

        boolean isEmpty() {
            return bondedAdapter.getItemCount() == 0;
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
            // TODO
        }
    }
}
