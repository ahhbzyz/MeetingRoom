package com.badoo.meetingroom.presentation.view;


import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {
    void setUpCircleTimeView();
    void setCircleTimeViewTime(long millis);
    void renderNextRoomEvent(RoomEventModel nextEvent);
    void setUpHorizontalTimelineView();
    void renderRoomEvents(LinkedList<RoomEventModel> mEventModelList);
    void setCurrentTime(String currentTime);

    void clearAllButtonsInLayout();
    void showButtonsInAvailableStatus();
    void showButtonsInOnHoldStatus();
    void showButtonsInBusyStatus();
    void showButtonsInDoNotDisturbStatus();
}
