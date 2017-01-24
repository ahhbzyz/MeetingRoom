package com.badoo.meetingroom.presentation.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.view.adapter.VerticalEventItemDecoration;
import com.badoo.meetingroom.presentation.view.component.layoutmanager.LinearLayoutManagerWithSmoothScroller;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.activity.RoomBookingActivity;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsAdapter;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class DailyEventsFragment extends BaseFragment implements DailyEventsView, DailyEventsAdapter.OnEventClickListener {

    private static final int REQUEST_BOOK_ROOM = 1000;

    @Inject DailyEventsPresenter mPresenter;
    @Inject DailyEventsAdapter mAdapter;

    @BindView(R.id.rv_daily_events) RecyclerView mDailyEventsRv;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingDataPb;

    private static final String ARG_PAGE = "page";
    private static final String ARG_ROOM_ID = "roomId";
    private int mPage;
    private String roomId;

    private EventCreatorDialogFragment mEventOrganizerDialog;

    public static DailyEventsFragment newInstance(int page, String roomId) {
        DailyEventsFragment fragment = new DailyEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_ROOM_ID, roomId);
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
            roomId = getArguments().getString(ARG_ROOM_ID);
        }
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);

        mEventOrganizerDialog = EventCreatorDialogFragment.newInstance();
        ButterKnife.bind(this, view);
        setUpEventsRecyclerView();
        mPresenter.setView(this);

        mPresenter.getEvents(roomId);
        return view;
    }

    private void setUpEventsRecyclerView() {
        mAdapter.setOnItemClickListener(this);
        mDailyEventsRv.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this.getActivity().getApplicationContext()));
        mDailyEventsRv.setAdapter(mAdapter);
    }

    @Override
    public void renderDailyEvents(List<EventModel> roomEventModelList) {

        mDailyEventsRv.addItemDecoration(new VerticalEventItemDecoration(getActivity().getApplicationContext(), R.drawable.divider_vertical_event, roomEventModelList));

        mAdapter.setDailyEventList(roomEventModelList);

        mAdapter.setOnEventRenderFinishListener(() -> {

            if (mPage == 0) {
                int currentEventPosition = 0;

                for (EventModel roomEventModel : roomEventModelList) {
                    if (roomEventModel.isProcessing()) {
                        break;
                    }
                    currentEventPosition ++;
                }
                if (currentEventPosition >= 2) {
                    mDailyEventsRv.scrollToPosition(currentEventPosition - 2);
                }
                mDailyEventsRv.smoothScrollToPosition(currentEventPosition);
            }
        });


    }

    @Override
    public void onAvailableEventClicked(int position, ArrayList<EventModel> eventModelList) {
        Intent intent = new Intent(getActivity(), RoomBookingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(RoomBookingActivity.ARG_POSITION, position);
        bundle.putParcelableArrayList(RoomBookingActivity.ARG_ROOM_LIST, eventModelList);
        bundle.putString(RoomBookingActivity.ARG_ROOM_ID, roomId);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQUEST_BOOK_ROOM);
    }

    @Override
    public void onBusyEventClicked(EventModel eventModel) {
        if (mEventOrganizerDialog != null) {
            mEventOrganizerDialog.setEvent(eventModel);
            mEventOrganizerDialog.show(getActivity());
        }
    }

    @Override
    public void updateRecyclerView() {
        mAdapter.notifyDataSetChanged();
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
        return getActivity().getApplicationContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    mPresenter.getEvents(roomId);
                    Intent returnIntent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                }
                break;
        }
    }

    public DailyEventsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public int getCurrentPage() {
        return mPage;
    }
}
