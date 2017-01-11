package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {

    void renderRoomEvent(RoomEventModel nextEvent);

    void renderRoomEvents(List<RoomEventModel> roomEvents);

    void updateCircleTimeViewTimeText(String text);

    void updateCircleTimeViewStatus(RoomEventModel mCurrentEvent);

    void updateHorizontalTimelineView(int numOfExpiredEvents);

    void updateHorizontalTimelineCurrentTime();

    void bookRoom(long startTime, long endTime);

    void showEventOrganizerDialog(RoomEventModel mCurrentEvent);

    void showButtonsForAvailableStatus();

    void showButtonsForOnHoldStatus();

    void showButtonsForBusyStatus();

    void showButtonsForDoNotDisturbStatus(String endTimeInText);

    void handleRecoverableAuthException(UserRecoverableAuthIOException e);

    void hideTopBottomContent();

    void showTopBottomContent();
}
