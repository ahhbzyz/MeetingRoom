package com.badoo.meetingroom.presentation.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.badoo.meetingroom.presentation.view.fragment.RoomListFragment;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListFragmentPagerAdapter extends FragmentPagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private String[] mTabTitles = new String[]{"1 floor", "4th floor"};

    @Inject
    public RoomListFragmentPagerAdapter(FragmentManager fm) {
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

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public RoomListFragment getRegisteredFragment(int position) {
        return (RoomListFragment) registeredFragments.get(position);
    }
}
