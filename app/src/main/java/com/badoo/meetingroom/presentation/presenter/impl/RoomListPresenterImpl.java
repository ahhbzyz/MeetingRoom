package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.presentation.model.Room;
import com.badoo.meetingroom.presentation.presenter.intf.RoomListPresenter;
import com.badoo.meetingroom.presentation.view.view.RoomListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 05/01/2017.
 */

public class RoomListPresenterImpl implements RoomListPresenter {

    private RoomListView mRoomListView;
    private int mPage = 0;
    private List<Room> mRoomList;

    @Inject
    RoomListPresenterImpl() {
        mRoomList = new ArrayList<>();
    }

    @Override
    public void setView(RoomListView view) {
        this.mRoomListView = view;
    }


    public void init() {
        loadRoomList();
        mPage = mRoomListView.getPage();
    }

    private void loadRoomList() {
        for (int i = 0; i < 8; i++) {
            Room room = new Room();
            mRoomList.add(room);
        }
        mRoomListView.renderRoomListInView(mRoomList);
    }

    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {

    }
}
