package com.badoo.meetingroom.presentation.view;


import com.badoo.meetingroom.presentation.model.RoomEventModel;

import java.util.LinkedList;



/**
 * Created by zhangyaozhong on 22/12/2016.
 */

public interface RoomEventsView extends LoadDataView {
    void renderRoomEvents(LinkedList<RoomEventModel> roomEventQueue);
}
