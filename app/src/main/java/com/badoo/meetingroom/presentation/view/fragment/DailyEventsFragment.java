package com.badoo.meetingroom.presentation.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.DailyEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.component.layoutmanager.LinearLayoutManagerWithSmoothScroller;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsAdapter;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class DailyEventsFragment extends BaseFragment implements DailyEventsView{

    private static final int REQUEST_AUTHORIZATION = 1001;
    public static final int MIN_BOOKING_TIME = 15;

    @Inject DailyEventsPresenterImpl mPresenter;

    @BindView(R.id.rv_daily_events) RecyclerView mRecyclerView;
    @BindView(R.id.layout_current_time_mark) LinearLayout mCurrentTimeMarkLayout;
    @BindView(R.id.tv_current_time) TextView mCurrentTimeTv;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataPb;

    private DailyEventsAdapter mAdapter;
    private int mScrollOffset;
    private static final String ARG_PAGE = "page";
    private int mPage;
    private OnFragmentInteractionListener mListener;


    public DailyEventsFragment() {
        // Required empty public constructor
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

        setUpRecyclerView();
        mPresenter.setView(this);
        mPresenter.init();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setUpRecyclerView() {
        // Todo di for adapter
        mAdapter = new DailyEventsAdapter(this.getContext());
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this.getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
        mScrollOffset = 0;
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollOffset += dy;
                mPresenter.updateCurrentTimeMarkWhenScrolled(dy);
            }
        });


    }

    @Override
    public void renderDailyEvents(List<RoomEventModel> roomEventModelList) {
        mAdapter.setDailyEventList(roomEventModelList);

        for (int i = 0 ; i < roomEventModelList.size(); i++) {
            if (roomEventModelList.get(i).isProcessing()) {
                mRecyclerView.smoothScrollToPosition(i);
                break;
            }
        }
        mPresenter.showCurrentTimeMark();
    }

    @Override
    public int getCurrentPage() {
        return this.mPage;
    }

    @Override
    public void showCurrentTimeMark(boolean visibility, long currentTime, String time) {
        if (visibility) {
            mCurrentTimeMarkLayout.setVisibility(View.VISIBLE);
        } else {
            mCurrentTimeMarkLayout.setVisibility(View.GONE);
        }

        float currTimeHeight = currentTime * mAdapter.getWidthPerMillis();
        mCurrentTimeMarkLayout.measure(0, 0);
        mCurrentTimeMarkLayout.setY((int)(currTimeHeight - mCurrentTimeMarkLayout.getMeasuredHeight() / 2f));
        mCurrentTimeTv.setText(time);
    }

    @Override
    public void bookRoom(long startTime, long endTime) {
        Intent intent = new Intent(this.getActivity(), RoomBookingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("startTime", startTime);
        bundle.putLong("endTime", endTime);
        intent.putExtra("timePeriod", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void showUserRecoverableAuth(UserRecoverableAuthIOException e) {
        this.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public void updateCurrentTimeMarkPosition(int dy) {
        float startY = mCurrentTimeMarkLayout.getY();
        mCurrentTimeMarkLayout.setY(startY - dy);
    }

    @Override
    public void updateCurrentTimeMarkPosition(long currentTimeInMillis) {
        float currTimeHeight = currentTimeInMillis * mAdapter.getWidthPerMillis();
        mCurrentTimeMarkLayout.measure(0, 0);
        mCurrentTimeMarkLayout.setY((int)(currTimeHeight - mCurrentTimeMarkLayout.getMeasuredHeight() / 2f - mScrollOffset));
    }

    @Override
    public void updateCurrentTimeText(String time) {
        mCurrentTimeTv.setText(time);
    }


    @Override
    public void updateDailyEventList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingData(boolean visibility) {
        if (visibility) {
            mLoadingDataPb.setVisibility(View.VISIBLE);
        } else {
            mLoadingDataPb.setVisibility(View.GONE);
        }
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
