package com.badoo.meetingroom.presentation.view.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.view.adapter.RoomListFragmentPagerAdapter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRoomActivity extends BaseActivity {


    @BindView(R.id.tv_current_date) TextView mCurrentDateTv;
    @BindView(R.id.tv_all_rooms) TextView mAllRoomsTv;
    @BindView(R.id.tab_layout_floors) TabLayout mFloorsTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    private RoomListFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_room);
        ButterKnife.bind(this);
        getComponent().inject(this);

        setUpToolbar();
        setUpViewPager();
    }

    public void setUpToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Typeface stolzlMedium = Typeface.createFromAsset(getAssets(),"fonts/stolzl_medium.otf");
        mAllRoomsTv.setTypeface(stolzlMedium);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));
    }

    public void setUpViewPager() {
        mAdapter = new RoomListFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mFloorsTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mAllRoomsTv.setText("Bahamas");
            finishAfterTransition();
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSystemTimeRefresh() {
        mCurrentDateTv.setText(TimeHelper.getCurrentDateAndWeek(this));
        if (mViewPager.getChildCount() > 0  && mAdapter.getRegisteredFragment(0) != null) {
            mAdapter.getRegisteredFragment(mViewPager.getCurrentItem()).updateRoomList();
        }
    }
}
