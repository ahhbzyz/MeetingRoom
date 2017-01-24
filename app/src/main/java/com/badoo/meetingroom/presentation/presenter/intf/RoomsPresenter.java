package com.badoo.meetingroom.presentation.presenter.intf;

import com.badoo.meetingroom.presentation.view.view.RoomListView;

/**
 * Created by zhangyaozhong on 22/01/2017.
 */

public interface RoomsPresenter extends Presenter {
    void getRoomList();
    void setView(RoomListView roomListView);
}
