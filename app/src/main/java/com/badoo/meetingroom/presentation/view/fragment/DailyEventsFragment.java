package com.badoo.meetingroom.presentation.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.badoo.meetingroom.presentation.presenter.impl.DailyEventsPresenterImpl;
import com.badoo.meetingroom.presentation.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.adapter.DailyEventsAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DailyEventsFragment extends BaseFragment implements DailyEventsView{

    @Inject DailyEventsPresenterImpl mPresenter;
    @Inject DailyEventsAdapter mAdapter;

    @BindView(R.id.rv_daily_events) RecyclerView mRecyclerView;

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
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(context()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void renderDailyEvents(List<RoomEventModel> roomEventModelList) {
        mAdapter.setDailyEventList(roomEventModelList);
    }

    @Override
    public int getCurrentPage() {
        return this.mPage;
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
        return null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
