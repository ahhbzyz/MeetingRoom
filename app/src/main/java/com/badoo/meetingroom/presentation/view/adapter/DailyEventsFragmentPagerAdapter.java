package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class DailyEventsFragmentPagerAdapter extends FragmentStatePagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    private Context mContext;
    private final int numOfDays = 10;
    private List<String>tabTitles = new ArrayList<>(numOfDays);
    private FragmentManager mFragmentManager;

    @Inject
    public DailyEventsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mFragmentManager = fm;
        this.mContext = context;
        tabTitles.add("Today");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        for (int i = 1; i < numOfDays; i ++) {
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            date = c.getTime();
            tabTitles.add(sdf.format(date));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return DailyEventsFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
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

    public DailyEventsFragment getRegisteredFragment(int position) {
        return (DailyEventsFragment) registeredFragments.get(position);
    }
}
