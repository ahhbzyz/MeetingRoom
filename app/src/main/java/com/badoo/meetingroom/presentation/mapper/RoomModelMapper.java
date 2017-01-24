package com.badoo.meetingroom.presentation.mapper;

import android.util.SparseArray;

import com.badoo.meetingroom.domain.entity.intf.Room;
import com.badoo.meetingroom.presentation.model.intf.RoomModel;
import com.badoo.meetingroom.presentation.model.impl.RoomModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public SparseArray<List<RoomModel>> map(List<Room> roomList) {

        final SparseArray<List<RoomModel>> roomModelListMap = new SparseArray<>();

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
}
