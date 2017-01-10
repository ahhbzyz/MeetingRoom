package com.badoo.meetingroom.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.impl.EventsCalendarPresenterImpl;
import com.badoo.meetingroom.presentation.presenter.intf.EventsCalendarPresenter;
import com.badoo.meetingroom.presentation.view.component.tablayoutwithoval.FadePageTransformer;
import com.badoo.meetingroom.presentation.view.view.EventsCalendarView;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsFragmentPagerAdapter;
import com.badoo.meetingroom.presentation.view.component.tablayoutwithoval.TabLayout;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsCalendarActivity extends BaseActivity implements EventsCalendarView {


    @Inject
    EventsCalendarPresenter mPresenter;
    DailyEventsFragmentPagerAdapter mAdapter;

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendar);
        ButterKnife.bind(this);

        this.getComponent().inject(this);

        mPresenter.setView(this);
        mPresenter.init();

       registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void setUpToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_medium.otf");
        mRoomNameTv.setTypeface(stolzlMedium);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void setUpViewPager() {
        mAdapter = new DailyEventsFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(false , new FadePageTransformer());
        mTabLayout.setupWithViewPager(mViewPager);

        // Center tab layout
        float width = Resources.getSystem().getDisplayMetrics().widthPixels;
        mTabLayout.setPadding((int)(width / 2f - getResources().getDimension(R.dimen.event_calendar_tab_min_width) / 2f), 0 , 0, 8);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                if (mViewPager.getChildCount() > 0  && mAdapter.getRegisteredFragment(0) != null) {
                    mAdapter.getRegisteredFragment(0).getPresenter().updateNumOfExpiredEvents();
                    mAdapter.getRegisteredFragment(0).updateCurrentTimeView();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeRefreshReceiver);
    }
}
