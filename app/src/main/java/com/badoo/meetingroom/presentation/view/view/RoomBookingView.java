package com.badoo.meetingroom.presentation.view.view;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

/**
 * Created by zhangyaozhong on 30/12/2016.
 */

public interface RoomBookingView extends LoadDataView {
    void setTimeSlotsInView();
    void updateTimePeriodTextView(long selectedEndTime, long selectedEndTime1);
    void showRecoverableAuth(UserRecoverableAuthIOException e);
    void showBookingSuccessful();
}
