package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.model.intf.EventModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingView extends LoadDataView {

    void getBundleData();
    void setUpAutoCompleteTextView(List<PersonModel> personModelList);
    void renderTimeSlotsInView(int position, List<EventModel> availableEventList, String date);
    void updateTimePeriodTextView(long selectedStarTime, long selectedEndTime);
    void showBookingSuccessful(long value);
}
