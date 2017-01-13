package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {

    void renderRoomEvent(RoomEventModel nextEvent);

    void renderRoomEventList(List<RoomEventModel> roomEvents);

    void updateCircleTimeViewTime(String text);

    void updateCircleTimeViewStatus(RoomEventModel currentEvent);

    void updateHorizontalTimelinePosition(int numOfExpiredEvents);

    void updateRecyclerView();

    void updateHorizontalTimelineCurrentTime();

    void restartCountDownTimer(RoomEventModel currentEvent);

    void bookRoom(long startTime, long endTime);

    void showEventOrganizerDialog(RoomEventModel mCurrentEvent);

    void showButtonGroupForAvailableStatus();

    void showButtonGroupForOnHoldStatus();

    void showButtonGroupForBusyStatus();

    void showButtonGroupForDoNotDisturbStatus(String endTimeInText);

    void handleRecoverableAuthException(UserRecoverableAuthIOException e);

    void hideTopBottomContent();

    void showTopBottomContent();

}
