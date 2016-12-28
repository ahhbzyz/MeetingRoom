package com.badoo.meetingroom.presentation.view;


import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {
    void setUpCircleTimeView();
    void setCircleTimeViewTime(String millis);
    void renderNextRoomEvent(RoomEventModel nextEvent);
    void setUpHorizontalTimelineView();
    void renderRoomEvents(LinkedList<RoomEventModel> mEventModelList);
    void setCurrentTimeText(String currentTime);

    void clearAllButtonsInLayout();
    void showButtonsInAvailableStatus();
    void showButtonsInOnHoldStatus();
    void showButtonsInBusyStatus();
    void showButtonsInDoNotDisturbStatus(String endTimeInText);

    void updateEventStatus();
    void setUpToolbar();
}
