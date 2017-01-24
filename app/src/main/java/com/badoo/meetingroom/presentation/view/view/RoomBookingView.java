package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.intf.BadooPersonModel;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingView extends LoadDataView {

    void getBundleData();
    void showBookingInformation(String date);
    void setUpAutoCompleteTextView(List<BadooPersonModel> badooPersonModelList);
    void renderTimeSlotsInView(int position, List<EventModel> availableEventList);
    void updateTimePeriodTextView(long selectedStarTime, long selectedEndTime);
    void showBookingSuccessful(long value);
}
