package am.project.x.business.others.printer;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DeviceAdapter
 * Created by Alex on 2016/6/22.
 */
class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private final DeviceViewHolder.OnHolderListener mListener;
    private final ArrayList<BluetoothDevice> mData = new ArrayList<>();

    DeviceAdapter(DeviceViewHolder.OnHolderListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(parent, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
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
