package com.latticeapplication.fragments;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.latticeapplication.R;
import com.latticeapplication.adapters.AllDevicesAdapter;

import java.util.HashSet;
import java.util.Set;


public class AvailableDevicesFragment extends Fragment {

    private static final String TAG = "AvailableDevices";
    private RecyclerView recyclerView;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> allDevices;
    private AllDevicesAdapter allDevicesAdapter;
    private Dialog dialog;
    private Button pairDeviceBtn;
    private LinearLayout noDeviceFound;
    public static AvailableDevicesFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_devices, container, false);
        allDevices = new HashSet<>();
        recyclerView = view.findViewById(R.id.availabledevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        noDeviceFound = view.findViewById(R.id.noDeviceFound);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        ImageView imageView = dialog.findViewById(R.id.image);
        Glide.with(getActivity()).load(R.drawable._logo).into(imageView);
        pairDeviceBtn = dialog.findViewById(R.id.pairDevice);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        startSearching();
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
                if (allDevicesAdapter == null) return true;
                allDevicesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }


    // method for searching available devices


    private void startSearching() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.i("Log", "in the start searching method");
            registerAllDevicesReciever();
            bluetoothAdapter.startDiscovery();
        }
    }

    // broadcast revceiver for getting available bluetooth devices

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    allDevices.add(device);
                } catch (Exception e) {
                    Log.i(TAG, "Inside the exception: ");
                    e.printStackTrace();
                }
            }
            if (allDevices.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                noDeviceFound.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noDeviceFound.setVisibility(View.GONE);
                allDevicesAdapter = new AllDevicesAdapter(allDevices, AvailableDevicesFragment.this);
                recyclerView.setAdapter(allDevicesAdapter);
                allDevicesAdapter.notifyDataSetChanged();
            }
        }
    };

    // pairs a device
    public void pairDevice(BluetoothDevice bdDevice) {
        dialog.show();
        pairDeviceBtn.setText("Pair with " + bdDevice.getName());
        pairDeviceBtn.setOnClickListener(v -> {
            bdDevice.createBond();
            pairDeviceBtn.setText("Pairing with " + bdDevice.getName());
            registerBondReciever();
            dialog.setCancelable(false);
        });
    }

    private void registerBondReciever() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(bondReciever, filter);
    }

    // Receiver for detecting bond state

    private final BroadcastReceiver bondReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Snackbar snackbar = Snackbar.make(getView(), "Paired Successfully", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    if (dialog.isShowing()) dialog.dismiss();
                    startSearching();
                    Log.d(TAG, "BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Snackbar snackbar = Snackbar.make(getView(), "Unable To Paired", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                    if (dialog.isShowing()) dialog.dismiss();
                    startSearching();
                    Log.d(TAG, "BOND_NONE.");
                }
            }
        }
    };

    // Function diaplays whether devices are connected or not
    public void showConnectionStatus(BluetoothDevice bluetoothDevice) {
        dialog.show();
        dialog.setCancelable(true);
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            pairDeviceBtn.setText("Connected with " + bluetoothDevice.getName());
            pairDeviceBtn.setEnabled(false);
        }
    }

    private void registerAllDevicesReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(myReceiver, intentFilter);
    }

}