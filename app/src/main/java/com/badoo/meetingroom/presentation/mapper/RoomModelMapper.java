package com.badoo.meetingroom.presentation.mapper;


import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.model.impl.RoomModelImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
        roomModel.setFloor(room.getFloor());
        roomModel.setCapacity(room.getCapacity());
        roomModel.setTvSupported(room.isTvSupported());
        roomModel.setVideoConferenceSupported(room.isVideoConferenceSupported());
        roomModel.setBeverageAllowed(room.isBeverageAllowed());
        roomModel.setStationerySupported(room.isStationerySupported());
        return roomModel;
    }


    public TreeMap<Integer, List<RoomModel>> mapToRoomMap(List<Room> roomList) {

        final TreeMap<Integer, List<RoomModel>> roomModelListMap = new TreeMap<>();

        for (Room room : roomList) {

            final RoomModel roomModel = map(room);

            if (roomModel != null) {

                List<RoomModel> roomModelList = roomModelListMap.get(roomModel.getFloor()) == null ?
                        new ArrayList<>() : roomModelListMap.get(roomModel.getFloor());

                roomModelList.add(roomModel);

                roomModelListMap.put(roomModel.getFloor(), roomModelList);
            }
        }


        return roomModelListMap;
    }

    public List<RoomModel> mapToRoomList(List<Room> roomList) {

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
