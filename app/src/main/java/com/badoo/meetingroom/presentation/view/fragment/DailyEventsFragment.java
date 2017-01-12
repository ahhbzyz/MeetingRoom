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
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
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


public class DailyEventsFragment extends BaseFragment implements DailyEventsView, DailyEventsAdapter.OnItemClickListener {

    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_BOOK_ROOM = 1000;

    @Inject DailyEventsPresenter mPresenter;
    @Inject DailyEventsAdapter mAdapter;

    @BindView(R.id.rv_daily_events) RecyclerView mDailyEventsRv;
    @BindView(R.id.layout_current_time) LinearLayout mCurrentTimeLayout;
    @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataPb;

    private static final String ARG_PAGE = "page";
    private int mPage;
    private int mScrollOffset = 0;
    private float mTimelineMarkOffset = 0;

    public static DailyEventsFragment newInstance(int page) {
        DailyEventsFragment fragment = new DailyEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    public DailyEventsFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        ButterKnife.bind(this, view);
        setUpEventsRecyclerView();
        mPresenter.setView(this);
        mPresenter.getEvents();
        return view;
    }

    private void setUpEventsRecyclerView() {
        mAdapter.setPage(mPage);
        mAdapter.setOnItemClickListener(this);
        mDailyEventsRv.setLayoutManager(new LinearLayoutManager(this.getActivity().getApplicationContext()));
        mDailyEventsRv.setAdapter(mAdapter);
        mDailyEventsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float startY = mCurrentTimeLayout.getY();
                mCurrentTimeLayout.setY(startY - dy);
                mScrollOffset += dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    mPresenter.updateCurrentTimeLayout();
                }
            }
        });
    }

    @Override
    public void renderDailyEvents(List<RoomEventModel> roomEventModelList) {
        mAdapter.setDailyEventList(roomEventModelList);
        if (mPage == 0) {
            mCurrentTimeLayout.setVisibility(View.VISIBLE);
            mDailyEventsRv.smoothScrollBy(0, (int) (mTimelineMarkOffset - mScrollOffset));
        }

    }

    @Override
    public void onEventItemClicked(int position) {
        if (mPresenter != null) {
            mPresenter.onEventClicked(position);
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
    public void updateCurrentTimeLayout(int numOfExpiredEvents) {
        if (mPage == 0) {
            mTimelineMarkOffset = (TimeHelper.getCurrentTimeInMillis() - Badoo.getStartTimeOfDay(mPage)) * mAdapter.getHeightPerMillis()
                - mCurrentTimeLayout.getMeasuredHeight() / 2f
                + numOfExpiredEvents * getActivity().getApplicationContext().getResources().getDimension(R.dimen.daily_events_divider_height);

            mCurrentTimeLayout.setY(mTimelineMarkOffset - mScrollOffset);
            mCurrentTimeTv.setText(TimeHelper.getCurrentTimeInMillisInText());
            // Update recycler view
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
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
    public void showError(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mPresenter.getEvents();
                }
                break;
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    mPresenter.getEvents();
                    Intent returnIntent = new Intent();
                    this.getActivity().setResult(Activity.RESULT_OK, returnIntent);
                }
                break;
        }
    }

    public DailyEventsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void handlerUserRecoverableAuth(UserRecoverableAuthIOException e) {
        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public int getCurrentPage() {
        return this.mPage;
    }
}
