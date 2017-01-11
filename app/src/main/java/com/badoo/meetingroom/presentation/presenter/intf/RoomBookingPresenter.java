package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import java.util.List;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingPresenter extends Presenter {
    void setView(RoomBookingView roomBookingView);
    void init();
    void bookRoom(String organizer);
    void setTimeSlotList(List<TimeSlotsAdapter.TimeSlot> timeSlotList);
}
