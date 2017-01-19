package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.BadooPersonModel;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingView extends LoadDataView {

    void setUpAutoCompleteTextView(List<BadooPersonModel> badooPersonModelList);
    void renderTimeSlotsInView(List<EventModel> availableEventList);
    void updateTimePeriodTextView(long selectedEndTime, long selectedEndTime1);
    void showRecoverableAuth(UserRecoverableAuthIOException e);
    void showBookingSuccessful(long value);
}
