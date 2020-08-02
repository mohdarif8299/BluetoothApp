package com.latticeapplication.adapters;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.latticeapplication.R;
import com.latticeapplication.fragments.PairedDevicesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.ViewHolder> implements Filterable {

    private List<BluetoothDevice> allDevices;
    private List<BluetoothDevice> allPairedDevices;
    private PairedDevicesFragment fragment;

    public PairedDevicesAdapter(Set<BluetoothDevice> allDevices, PairedDevicesFragment fragment) {
        this.allDevices = new ArrayList<>(allDevices);
        this.fragment = fragment;
        this.allPairedDevices = new ArrayList<>(allDevices);
    }

    @NonNull
    @Override
    public PairedDevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_device_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PairedDevicesAdapter.ViewHolder holder, int position) {
        BluetoothDevice devices = allDevices.get(position);
        holder.deviceName.setText(devices.getName());
        if (devices.getBondState() == BluetoothDevice.BOND_BONDED) {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(fragment.getContext(), R.drawable.bluetooth_paired));
        } else {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(fragment.getContext(), R.drawable.bluetooth_unpaired));
        }

    }

    @Override
    public int getItemCount() {
        return allDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        LinearLayout singleDevice;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            singleDevice = itemView.findViewById(R.id.device);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    @Override
    public Filter getFilter() {
        return deviceFilter;
    }

    private Filter deviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<BluetoothDevice> filteredDeviceList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredDeviceList.addAll(allPairedDevices);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (BluetoothDevice bluetoothDevice : allPairedDevices) {
                    if (bluetoothDevice.getName().toLowerCase().contains(filterPattern)) {
                        filteredDeviceList.add(bluetoothDevice);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredDeviceList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            allDevices.clear();
            allDevices.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
