package com.urdriver.urdriver.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.urdriver.urdriver.FragmentAdminDriver;
import com.urdriver.urdriver.FragmentAdminUser;
import com.urdriver.urdriver.FragmentToday;

public class PagerViewAdminAdapter extends FragmentPagerAdapter {
    public PagerViewAdminAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new FragmentAdminUser();
                break;
            case 1:
                fragment = new FragmentAdminDriver();
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
