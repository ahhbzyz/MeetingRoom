package com.badoo.meetingroom.presentation.mapper;

import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.model.impl.RoomModelImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by zhangyaozhong on 16/01/2017.
 */

public class RoomModelMapper {

    @Inject
    RoomModelMapper() {}

    private RoomModel map(Room room) {

        RoomModel roomModel;

        if (room == null) {
            return null;
        }
        roomModel = new RoomModelImpl();
        roomModel.setName(room.getName());
        roomModel.setId(room.getId());

        return roomModel;
    }


    public List<RoomModel> map(List<Room> roomList) {

        final List<RoomModel> roomModelList = new ArrayList<>();

        for (Room room : roomList) {
            final RoomModel roomModel = map(room);
            if (roomModel != null) {
                roomModelList.add(roomModel);
            }
        }
        return roomModelList;
    }
}
