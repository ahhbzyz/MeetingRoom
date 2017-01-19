package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.view.adapter.TimeSlotsAdapter;
import com.badoo.meetingroom.presentation.view.view.RoomBookingView;

import java.util.List;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingPresenter extends Presenter {
    void getContactList();

    void setView(RoomBookingView roomBookingView);
    void bookRoom(String organizer);
    void setTimeSlotList(List<EventModel> eventModelList);
}
