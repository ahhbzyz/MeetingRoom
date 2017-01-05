package com.badoo.meetingroom.presentation.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.badoo.meetingroom.presentation.view.fragment.RoomListFragment;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class AllRoomsFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles = new String[]{"1 floor", "4th floor"};

    @Inject
    public AllRoomsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return RoomListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
