package com.latticeapplication.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.latticeapplication.fragments.AvailableDevicesFragment;
import com.latticeapplication.fragments.PairedDevicesFragment;

public class TabLayoutAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public TabLayoutAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AvailableDevicesFragment availableDevicesFragment = new AvailableDevicesFragment();
                return availableDevicesFragment;
            case 1:
                PairedDevicesFragment pairedDevicesFragment = new PairedDevicesFragment();
                return pairedDevicesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
