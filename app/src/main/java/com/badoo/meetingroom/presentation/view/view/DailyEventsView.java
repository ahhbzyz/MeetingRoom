package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsView extends LoadDataView{
    void renderDailyEvents(List<RoomEventModel> roomEventModelList);
    int getCurrentPage();
    void updateCurrentTimeText(String time);
    void updateCurrentTimeMarkPosition(long currentTimeInMillis);
    void updateCurrentTimeMarkPosition(int dy);
    void updateDailyEventList();
    void showCurrentTimeMark(boolean visibility, long currentTime, String time);
    void bookRoom(long startTime, long endTime);
}
