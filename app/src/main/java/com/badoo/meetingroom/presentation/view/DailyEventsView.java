package com.badoo.meetingroom.presentation.view;

import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public interface DailyEventsView extends LoadDataView{
    void renderDailyEvents(List<RoomEventModel> roomEventModelList);
    int getCurrentPage();
}
