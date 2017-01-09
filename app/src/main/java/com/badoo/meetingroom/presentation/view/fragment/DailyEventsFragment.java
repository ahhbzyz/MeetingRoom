package com.badoo.meetingroom.presentation.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.DailyEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsAdapter;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class DailyEventsFragment extends BaseFragment implements DailyEventsView {

    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_BOOK_ROOM= 1000;

    public static final int MIN_SLOT_TIME = 5;
    public static final int MIN_BOOKING_TIME = 15;

    @Inject DailyEventsPresenterImpl mPresenter;

    @BindView(R.id.rv_daily_events) RecyclerView mDailyEventsRv;
    @BindView(R.id.layout_current_time_mark) LinearLayout mCurrentTimeMarkLayout;
    @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataPb;

    private DailyEventsAdapter mAdapter;
    private int mScrollOffset;
    private static final String ARG_PAGE = "page";
    private int mPage;


    public DailyEventsFragment() {
        setRetainInstance(true);
    }


    public static DailyEventsFragment newInstance(int page) {
        DailyEventsFragment fragment = new DailyEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
        this.getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        ButterKnife.bind(this, view);
        mPresenter.setView(this);

        setUpEventsRecyclerView();
        mPresenter.init();

        return view;
    }
    
    private void setUpEventsRecyclerView() {
        // Todo di for adapter
        mAdapter = new DailyEventsAdapter(this.getContext(), mPage);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        this.mDailyEventsRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mDailyEventsRv.setAdapter(mAdapter);
        this.mScrollOffset = 0;
        this.mDailyEventsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollOffset += dy;
                updateCurrentTimeMarkPosition(dy);
            }
        });
    }

    @Override
    public void renderDailyEvents(List<RoomEventModel> roomEventModelList) {
        mAdapter.setDailyEventList(roomEventModelList);
        showCurrentTimeMark();
        scrollToCurrentTimePosition();
    }

    public void scrollToCurrentTimePosition() {
        if (mPage == 0 && getTimelineMarkOffset() > 0) {
            mCurrentTimeMarkLayout.measure(0, 0);
            mDailyEventsRv.smoothScrollBy(0, Math.round (getTimelineMarkOffset()) - mScrollOffset);
        }
    }


    @Override
    public int getCurrentPage() {
        return this.mPage;
    }


    public void showCurrentTimeMark() {
        if (mPage == 0) {
            mCurrentTimeMarkLayout.setVisibility(View.VISIBLE);
            updateCurrentTimeView();
        } else {
            mCurrentTimeMarkLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void bookRoom(long startTime, long endTime) {
        Intent intent = new Intent(this.getActivity(), RoomBookingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("startTime", startTime);
        bundle.putLong("endTime", endTime);
        intent.putExtra("timePeriod", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQUEST_BOOK_ROOM);
    }

    @Override
    public void showUserRecoverableAuth(UserRecoverableAuthIOException e) {
        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    public void updateCurrentTimeMarkPosition(int dy) {
        float startY = mCurrentTimeMarkLayout.getY();
        mCurrentTimeMarkLayout.setY(startY - dy);
    }

    private float getTimelineMarkOffset() {
       return (TimeHelper.getCurrentTimeInMillis() - Badoo.getStartTimeOfDay(0)) * mAdapter.getHeightPerMillis()
              - mCurrentTimeMarkLayout.getMeasuredHeight() / 2f
              + mPresenter.getNumOfExpiredEvents() * getActivity().getApplicationContext().getResources().getDimension(R.dimen.daily_events_divider_height);
    }

    public void updateCurrentTimeView() {
        mCurrentTimeMarkLayout.measure(0, 0);
        mCurrentTimeMarkLayout.setY(Math.round(getTimelineMarkOffset()) - mScrollOffset);
        mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLoadingData(String message) {
        mLoadingDataPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingData() {
        mLoadingDataPb.setVisibility(View.GONE);
    }

    @Override
    public void showRetryLoading(boolean visibility) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.loadRoomEventList();
                }
                break;
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this.getContext(), "Room is booked successfully", Toast.LENGTH_SHORT).show();
                    mPresenter.init();
                    Intent returnIntent = new Intent();
                    this.getActivity().setResult(Activity.RESULT_OK,returnIntent);
                }
                break;
        }
    }

    public DailyEventsPresenterImpl getPresenter() {
        return mPresenter;
    }


    private DailyEventsAdapter.OnItemClickListener mOnItemClickListener = position -> {
        if (mPresenter != null) {
            mPresenter.onEventClicked(position);
        }
    };
}
