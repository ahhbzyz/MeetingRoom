package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomStatusView extends LoadDataView {

    void renderRoomEvent(RoomEventModel nextEvent);

    void renderRoomEventList(List<RoomEventModel> roomEvents);

    void updateCircleTimeViewStatus(RoomEventModel currentEvent);

    void updateHorizontalTimeline(int numOfExpiredEvents);

    void updateRecyclerView();

    void startCircleViewAnimator(RoomEventModel currentEvent);

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
