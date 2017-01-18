package com.badoo.meetingroom.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.adapter.RoomListAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RoomListFragment extends BaseFragment implements RoomListView{

    @Inject RoomListAdapter mAdapter;
    @Inject RoomListPresenter mPresenter;

    @BindView(R.id.rv_room_list) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_room_list) ProgressBar mLoadingRoomListPb;

    private static final String ARG_PAGE = "page";
    private int mPage;


    public RoomListFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    public static RoomListFragment newInstance(int page) {
        RoomListFragment fragment = new RoomListFragment();
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
        this.getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_list, container, false);
        ButterKnife.bind(this, view);

        setUpRecyclerView();
        mPresenter.setView(this);
        mPresenter.getRoomList();

        return view;
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public int getPage() {
        return mPage;
    }

    @Override
    public void renderRoomListInView(List<RoomModel> roomModelList) {
        mAdapter.setRoomList(roomModelList);
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
        return null;
    }

    @Override
    public void updateRoomList() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyItemChange(int position) {
        mAdapter.notifyItemChanged(position);
    }
}
