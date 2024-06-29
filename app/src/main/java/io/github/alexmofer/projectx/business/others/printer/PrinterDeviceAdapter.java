package io.github.alexmofer.projectx.business.others.printer;

import android.bluetooth.BluetoothDevice;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * PrinterDeviceAdapter
 * Created by Alex on 2016/6/22.
 */
class PrinterDeviceAdapter extends RecyclerView.Adapter<PrinterDeviceViewHolder> {

    private final PrinterDeviceViewHolder.OnHolderListener mListener;
    private final ArrayList<BluetoothDevice> mData = new ArrayList<>();

    PrinterDeviceAdapter(PrinterDeviceViewHolder.OnHolderListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public PrinterDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrinterDeviceViewHolder(parent, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrinterDeviceViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void setDevices(Collection<? extends BluetoothDevice> devices) {
        mData.clear();
        if (devices != null)
            mData.addAll(devices);
        notifyDataSetChanged();
    }
}
