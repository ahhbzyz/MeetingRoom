package com.badoo.meetingroom.presentation.view.view;


import android.content.Intent;

import com.badoo.meetingroom.domain.entity.intf.RoomEvent;
import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {
    void renderNextRoomEvent(RoomEventModel nextEvent);
    void renderRoomEvents(List<RoomEventModel> roomEvents);
    void showButtonsForAvailableStatus();
    void showButtonsForOnHoldStatus();
    void showButtonsForBusyStatus();
    void showButtonsForDoNotDisturbStatus(String endTimeInText);
    void updateHorizontalTimelineView(int numOfExpiredEvents);
    void handleRecoverableAuthException(UserRecoverableAuthIOException e);
    void bookRoom(long startTime, long endTime);
    void showEventOrganizerDialog();
    void updateCircleTimeView(RoomEventModel mCurrentEvent);
}
