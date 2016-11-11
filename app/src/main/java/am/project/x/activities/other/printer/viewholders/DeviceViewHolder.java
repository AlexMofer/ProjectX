package am.project.x.activities.other.printer.viewholders;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import am.project.x.R;

/**
 * DeviceViewHolder
 * Created by Alex on 2016/6/22.
 */
public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private OnHolderListener mListener;
    private BluetoothDevice mDevice;
    public DeviceViewHolder(ViewGroup parent, OnHolderListener listener) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_printer_device, parent, false));
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null)
            mListener.onItemClicked(mDevice);
        view.setSelected(true);
    }

    public void setData(BluetoothDevice device, boolean isSelected) {
        itemView.setSelected(isSelected);
        mDevice = device;
        if (itemView instanceof TextView && mDevice != null) {
            TextView tvName = (TextView) itemView;
            tvName.setText(mDevice.getName());
        }
    }

    public interface OnHolderListener {
        void onItemClicked(BluetoothDevice device);
    }
}
