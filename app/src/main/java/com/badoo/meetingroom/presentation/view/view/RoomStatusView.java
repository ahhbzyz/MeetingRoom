package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.EventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomStatusView extends LoadDataView {

    void renderRoomEvent(EventModel nextEvent);

    void renderRoomEventList(List<EventModel> roomEvents);

    void updateCircleTimeViewStatus(EventModel currentEvent);

    void updateHorizontalTimeline(int numOfExpiredEvents);

    void updateRecyclerView();

    void startCircleViewAnimator(EventModel currentEvent);

    void bookRoom(long startTime, long endTime);

    void showEventOrganizerDialog(EventModel mCurrentEvent);

    void showButtonGroupForAvailableStatus();

    void showButtonGroupForOnHoldStatus();

    void showButtonGroupForBusyStatus();

    void showButtonGroupForDoNotDisturbStatus(String endTimeInText);

    void handleRecoverableAuthException(UserRecoverableAuthIOException e);

    void hideTopBottomContent();

    void showTopBottomContent();

    void stopCountDown();
}
