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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.latticeapplication.R;
import com.latticeapplication.fragments.AvailableDevicesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AllDevicesAdapter extends RecyclerView.Adapter<AllDevicesAdapter.ViewHolder> implements Filterable {

    private List<BluetoothDevice> allDevices;
    private AvailableDevicesFragment fragment;
    private List<BluetoothDevice> allBLuetoothDevices;

    public AllDevicesAdapter(Set<BluetoothDevice> allDevices, AvailableDevicesFragment fragment) {
        this.allDevices = new ArrayList<>(allDevices);
        this.fragment = fragment;
        this.allBLuetoothDevices = new ArrayList<>(allDevices);

    }

    @NonNull
    @Override
    public AllDevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_device_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllDevicesAdapter.ViewHolder holder, int position) {
        BluetoothDevice devices = allDevices.get(position);
        holder.deviceName.setText(devices.getName());
        if (devices.getBondState() == BluetoothDevice.BOND_BONDED) {
            Glide.with(fragment).load(R.drawable.bluetooth_paired).into(holder.icon);
        } else {
            Glide.with(fragment).load(R.drawable.bluetooth_unpaired).into(holder.icon);
        }
        holder.singleDevice.setOnClickListener(v -> {
            if (devices.getBondState() != BluetoothDevice.BOND_BONDED)
                fragment.pairDevice(devices);
            else {
                fragment.showConnectionStatus(devices);
            }
        });
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
                filteredDeviceList.addAll(allBLuetoothDevices);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (BluetoothDevice bluetoothDevice : allBLuetoothDevices) {
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
