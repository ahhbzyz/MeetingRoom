package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

public class DailyEventsFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private final int numOfDays = 10;
    private List<String>tabTitles = new ArrayList<>(numOfDays);

    @Inject
    public DailyEventsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
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
}
