package com.latticeapplication.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.latticeapplication.R;
import com.latticeapplication.adapters.PairedDevicesAdapter;

import java.util.HashSet;
import java.util.Set;


public class PairedDevicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private Set<BluetoothDevice> allDevices;
    private PairedDevicesAdapter pairedDevicesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_paired_devices, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        recyclerView = view.findViewById(R.id.recentpaireddevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        allDevices = new HashSet<>();
        allPairedDevices();
        SearchView searchView = view.findViewById(R.id.search);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setQueryHint("Search device name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (pairedDevicesAdapter == null) return true;
                pairedDevicesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }

    // Get All Paired Devices
    public void allPairedDevices() {
        allDevices.clear();
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                allDevices.add(device);
            }
        }
        pairedDevicesAdapter = new PairedDevicesAdapter(allDevices, PairedDevicesFragment.this);
        pairedDevicesAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(pairedDevicesAdapter);
        pairedDevicesAdapter.notifyDataSetChanged();
    }


}