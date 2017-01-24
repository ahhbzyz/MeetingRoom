package com.badoo.meetingroom.presentation.view.view;

import com.badoo.meetingroom.presentation.model.intf.RoomModel;

import java.util.List;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public interface RoomListFragmentView extends LoadDataView {

    int getPage();

    void renderRoomListInView(List<RoomModel> mRoomList);

    void notifyListChange();

    void showEventsCalendarView(String id);
}
