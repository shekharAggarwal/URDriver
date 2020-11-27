package com.urdriver.urdriver.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.urdriver.urdriver.FragmentToday;
import com.urdriver.urdriver.FragmentUpcoming;

public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new FragmentToday();
                break;
            case 1:
                fragment = new FragmentUpcoming();
                break;
            default:
                break;
        }
        return fragment == null ? new FragmentToday() : fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
