package com.miracleworld.lingxingdao.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by HeavenDong on 2016/2/25.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> fragments;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments) {
        super(fragmentManager);
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
