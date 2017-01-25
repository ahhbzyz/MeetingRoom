package com.badoo.meetingroom.presentation.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.activity.EventsCalendarActivity;
import com.badoo.meetingroom.presentation.view.adapter.RoomListAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomListFragmentView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class RoomListFragment extends BaseFragment implements RoomListFragmentView {

    @Inject RoomListAdapter mAdapter;
    @Inject RoomListPresenter mPresenter;

    @BindView(R.id.rv_room_list) RecyclerView mRecyclerView;
    @BindView(R.id.layout_refresh_room_list) SwipeRefreshLayout mLayoutRefreshRoomList;

    private static final String ARG_PAGE = "page";
    private static final String ARG_ROOM_LIST = "roomList";
    private static final int REQUEST_BOOK_ROOM = 1000;


    private int mPage;
    private List<RoomModel> mRoomModelList;


    public RoomListFragment() {
        setRetainInstance(true);
    }

    public static RoomListFragment newInstance(int page, ArrayList<RoomModel> roomModelList) {
        RoomListFragment fragment = new RoomListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putParcelableArrayList(ARG_ROOM_LIST, roomModelList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
            mRoomModelList = getArguments().getParcelableArrayList(ARG_ROOM_LIST);
        }
        this.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_list, container, false);
        ButterKnife.bind(this, view);

        setUpRecyclerView();
        setUpSwipeRefreshLayout();
        mPresenter.setView(this);
        mPresenter.setPage(mPage);
        getRoomEvents();

        return view;
    }



    public void getRoomEvents() {
        if (mRoomModelList != null) {
            mPresenter.getRoomEvents(mRoomModelList);
        }
    }
    private void setUpSwipeRefreshLayout() {
        mLayoutRefreshRoomList.setOnRefreshListener(() -> getRoomEvents());
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RoomListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPresenter.onRoomItemClick(position);
            }
        });
    }

    @Override
    public int getPage() {
        return mPage;
    }

    @Override
    public void showEventsCalendarView(String id) {
        Intent intent = new Intent(getActivity(), EventsCalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EventsCalendarActivity.ARG_ROOM_ID, id);
        bundle.putBoolean(EventsCalendarActivity.ARG_SHOW_ROOM_LIST_ICON, false);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_BOOK_ROOM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BOOK_ROOM:
                if (resultCode == RESULT_OK) {
                    getRoomEvents();
                    Intent returnIntent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void renderRoomListInView(List<RoomModel> roomModelList) {
        mAdapter.setRoomList(roomModelList);
    }

    @Override
    public void showLoadingData(String message) {
        mLayoutRefreshRoomList.post(() -> mLayoutRefreshRoomList.setRefreshing(true));
    }

    @Override
    public void dismissLoadingData() {
        mLayoutRefreshRoomList.post(() -> mLayoutRefreshRoomList.setRefreshing(false));
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return null;
    }

    @Override
    public void notifyListChange() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


}
