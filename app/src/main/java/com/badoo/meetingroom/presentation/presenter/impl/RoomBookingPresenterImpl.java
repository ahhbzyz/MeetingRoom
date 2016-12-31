package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class RoomBookingPresenterImpl implements RoomBookingPresenter {

    private RoomBookingView mRoomBookingView;

    @Inject
    public RoomBookingPresenterImpl() {}

    @Override
    public void init() {
        setTimeSlotsInView();
    }

    private void setTimeSlotsInView() {
        mRoomBookingView.setTimeSlotsInView();
    }

    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void setView(RoomBookingView roomBookingView) {
        this.mRoomBookingView = roomBookingView;
    }


}
