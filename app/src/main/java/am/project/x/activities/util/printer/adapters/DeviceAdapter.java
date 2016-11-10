package am.project.x.activities.util.printer.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Set;

import am.project.x.activities.util.printer.viewholders.DeviceViewHolder;

/**
 * DeviceAdapter
 * Created by Alex on 2016/6/22.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> implements
        DeviceViewHolder.OnHolderListener{

    private DeviceViewHolder.OnHolderListener listener;
    private ArrayList<BluetoothDevice> mData = new ArrayList<>();

    public DeviceAdapter(DeviceViewHolder.OnHolderListener listener) {
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(parent, this);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onItemClicked(BluetoothDevice device) {
        notifyItemRangeChanged(0, getItemCount());
        if (listener != null)
            listener.onItemClicked(device);
    }

    public void setDevices(Set<BluetoothDevice> devices) {
        clear();
        mData.addAll(devices);
        if (mData.size() > 0)
            notifyItemRangeInserted(0, mData.size());
    }

    public void clear() {
        if (mData.size() > 0)
            notifyItemRangeRemoved(0, mData.size());
        mData.clear();
    }
}
