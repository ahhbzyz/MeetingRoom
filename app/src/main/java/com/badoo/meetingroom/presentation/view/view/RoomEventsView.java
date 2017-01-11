package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

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
    void updateCircleTimeViewStatus(RoomEventModel mCurrentEvent);
    void setCircleTimeViewTimeText(String text);
    void updateHorizontalTimelineCurrentTime();
}
