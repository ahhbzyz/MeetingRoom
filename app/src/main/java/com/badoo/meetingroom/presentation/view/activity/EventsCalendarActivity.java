package com.badoo.meetingroom.presentation.view.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.presenter.impl.EventsCalendarPresenterImpl;
import com.badoo.meetingroom.presentation.view.EventsCalendarView;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsFragmentPagerAdapter;
import com.badoo.meetingroom.presentation.view.fragment.DailyEventsFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsCalendarActivity extends BaseActivity implements EventsCalendarView, DailyEventsFragment.OnFragmentInteractionListener {


    @Inject
    EventsCalendarPresenterImpl mEventsCalendarPresenter;

    @BindView(R.id.tv_room_name) TextView mRoomNameTv;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendar);
        ButterKnife.bind(this);

        this.getApplicationComponent().inject(this);

        mEventsCalendarPresenter.setView(this);
        mEventsCalendarPresenter.init();
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
        mViewPager.setAdapter(new DailyEventsFragmentPagerAdapter(getSupportFragmentManager(), this));

        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showLoadingData(boolean visibility) {

    }

    @Override
    public void showRetryLoading(boolean visibility) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return this.getApplicationContext();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
