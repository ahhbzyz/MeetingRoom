package com.badoo.meetingroom.presentation.view.view;


import android.content.Intent;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.LinkedList;


/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {
    void setCircleTimeViewTime(String millis);
    void renderNextRoomEvent(RoomEventModel nextEvent);
    void renderRoomEvents(LinkedList<RoomEventModel> mEventModelList);
    void clearAllButtonsInLayout();
    void showButtonsInAvailableStatus();
    void showButtonsInOnHoldStatus();
    void showButtonsInBusyStatus();
    void showButtonsInDoNotDisturbStatus(String endTimeInText);
    void updateEventStatus();
    void showRecoverableAuth(UserRecoverableAuthIOException e);
    void bookRoom(long startTime, long endTime);
    void showEventInsertSuccessful();
    void showEventDeleteSuccessful();
    void showEventExtendSuccessful();
    void showEventOrganizerDialog();
}
