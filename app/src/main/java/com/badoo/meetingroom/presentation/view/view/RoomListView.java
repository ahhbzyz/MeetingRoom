package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.Room;

import java.util.List;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public interface RoomListView extends LoadDataView {
    int getPage();
    void renderRoomListInView(List<Room> mRoomList);
}