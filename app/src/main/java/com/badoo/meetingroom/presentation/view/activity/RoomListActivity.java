package com.badoo.meetingroom.presentation.view.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomsPresenter;
import com.badoo.meetingroom.presentation.view.adapter.RoomListFragmentPagerAdapter;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.RoomListView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomListActivity extends BaseActivity implements RoomListView {

    @Inject RoomsPresenter mPresenter;

    @Inject @Named("stolzl_medium") Typeface mStolzlMediumTypeface;

    @BindView(R.id.tv_current_date_time) TextView mCurrentDateTimeTv;
    @BindView(R.id.tv_all_rooms) TextView mAllRoomsTv;
    @BindView(R.id.tab_layout_floors) TabLayout mFloorsTabLayout;
    @BindView(R.id.pb_loading_room_list_events) ProgressBar mLoadingRoomListPb;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    private RoomListFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        ButterKnife.bind(this);
        getComponent().inject(this);

        setUpToolbar();
        mPresenter.setView(this);
        mPresenter.getRoomList();
    }

    public void setUpToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAllRoomsTv.setTypeface(mStolzlMediumTypeface);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
    }

    @Override
    public void setUpViewPager(SparseArray<List<RoomModel>> roomModelListMap) {
        mAdapter = new RoomListFragmentPagerAdapter(context(), getSupportFragmentManager(), roomModelListMap);
        mViewPager.setAdapter(mAdapter);
        mFloorsTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSystemTimeRefresh() {
        mCurrentDateTimeTv.setText(TimeHelper.getCurrentDateTime(this));
        if (mViewPager.getChildCount() > 0  && mAdapter.getRegisteredFragment(0) != null) {
            mAdapter.getRegisteredFragment(mViewPager.getCurrentItem()).notifyListChange();
        }
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingRoomListPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingData() {
        mLoadingRoomListPb.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return getApplicationContext();
    }
}
