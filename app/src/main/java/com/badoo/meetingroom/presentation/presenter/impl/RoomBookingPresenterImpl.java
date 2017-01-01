package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.presentation.presenter.intf.RoomBookingPresenter;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public class RoomBookingPresenterImpl implements RoomBookingPresenter {

    private RoomBookingView mRoomBookingView;
    private List<TimeSlotsAdapter.TimeSlot> mTimeSlotList;
    private long selectedStartTime = -1;
    private long selectedEndTime = -1;
    @Inject
    RoomBookingPresenterImpl() {}

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


    public void setTimeSlotList(List<TimeSlotsAdapter.TimeSlot> timeSlotList) {
        this.mTimeSlotList = timeSlotList;
        boolean startTimeHasVal = false;
        boolean isFirstSelectedSlot = false;
        int numOfSelectedSlots = 0;
        for (int i = 0; i < timeSlotList.size(); i++) {

            TimeSlotsAdapter.TimeSlot slot = timeSlotList.get(i);

            if (!slot.isSelected() && !isFirstSelectedSlot) {
                continue;
            }
            isFirstSelectedSlot = true;
            if (!startTimeHasVal) {
                selectedStartTime = slot.getStartTime();
                startTimeHasVal = true;
            }
            numOfSelectedSlots++;
            if (!slot.isSelected() || i == timeSlotList.size() - 1) {
                selectedEndTime = slot.getStartTime();
                break;
            }
        }

        if (numOfSelectedSlots == 0) {
            selectedStartTime = -1;
            selectedEndTime = -1;
        }
        mRoomBookingView.updateTimePeriodTextView(selectedStartTime, selectedEndTime);
    }
}
