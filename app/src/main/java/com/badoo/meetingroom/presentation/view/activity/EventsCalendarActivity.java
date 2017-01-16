package com.badoo.meetingroom.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.intf.EventsCalendarPresenter;
import com.badoo.meetingroom.presentation.view.component.nonswipeviewpager.NonSwipeViewPager;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.EventsCalendarView;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsFragmentPagerAdapter;
import com.badoo.meetingroom.presentation.view.component.tablayoutwithoval.TabLayout;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsCalendarActivity extends BaseActivity implements EventsCalendarView {


    @Inject EventsCalendarPresenter mPresenter;
    // Typeface
    @Inject @Named("stolzl_medium") Typeface mStolzlMediumTypeface;
    DailyEventsFragmentPagerAdapter mAdapter;

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.img_room) ImageView mRoomImg;
    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) NonSwipeViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendar);
        ButterKnife.bind(this);
        getComponent().inject(this);
        mPresenter.setView(this);

        initViews();
        setUpViewPager();
    }

    public void initViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mRoomNameTv.setTypeface(mStolzlMediumTypeface);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mRoomImg.setOnClickListener(v -> {
            Intent intent = new Intent(EventsCalendarActivity.this, AllRoomsActivity.class);
            startActivity(intent);
        });

        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));
    }


    private void setUpViewPager() {
        mAdapter = new DailyEventsFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        // Center tab layout
        float width = Resources.getSystem().getDisplayMetrics().widthPixels;
        mTabLayout.setPadding((int)(width / 2f - getResources().getDimension(R.dimen.events_calendar_tab_min_width) / 2f), 0 , 0, 8);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                if (mViewPager.getChildCount() > 0  && mAdapter.getRegisteredFragment(0) != null) {
                    mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(EventsCalendarActivity.this));
                    mAdapter.getRegisteredFragment(0).getPresenter().onSystemTimeUpdate();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTimeRefreshReceiver);
    }
}
