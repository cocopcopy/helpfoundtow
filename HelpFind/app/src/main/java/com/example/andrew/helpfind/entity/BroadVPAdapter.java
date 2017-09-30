package com.example.andrew.helpfind.entity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrew.helpfind.fragments.SubBroadcastFragment;

/**
 * Created by Andrew on 2017/8/29.
 */

public class BroadVPAdapter extends FragmentPagerAdapter {
    private final int PAGE_SIZE = 2;
    private Context mContext;

    public BroadVPAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        int type = 0;
        switch (position) {
            case 0:
                type = Constant.TYPE_VIEW_PAGER_FOUD;
                break;
            case 1:
                type = Constant.TYPE_VIEW_PAGER_LOST;
                break;
        }
        return SubBroadcastFragment.newInstance(type);
    }

    @Override
    public int getCount() {
        return PAGE_SIZE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "Default";
        switch (position) {
            case 0:
                title = "寻找失主";
                break;
            case 1:
                title = "寻找失物";
                break;
        }
        return title;
    }
}
